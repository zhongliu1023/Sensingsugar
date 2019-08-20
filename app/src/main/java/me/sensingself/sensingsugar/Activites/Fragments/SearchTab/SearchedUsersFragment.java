package me.sensingself.sensingsugar.Activites.Fragments.SearchTab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.otto.Subscribe;

import me.sensingself.sensingsugar.Activites.Adapter.UsersAdapter;
import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedBeginTestFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by liujie on 1/31/18.
 */

public class SearchedUsersFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView searchUserTitle, userNameTxt;
    private ImageView suBack;

    private ListView listUsers;
    ArrayList<Patient> users = new ArrayList<Patient>();
    UsersAdapter adapter;
    ArrayList<Patient> patients = new ArrayList<Patient>();
    private UserVo currentUserVo = new UserVo();
    private Patient selectedPatient = new Patient();

    private SharedPreferencesManager sharedPreferencesManager;

    private KProgressHUD hud;
    private AccountClient accountClient;

    static String fullName = "";

    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_tab_userlist, container, false);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();
        fragmentManager = FragmentmanagerUtils.getFragmentManagerSearch();
        initNewUserVoAndPatient();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }
    public static SearchedUsersFragment newInstance(String param1, String param2) {
        if (param1.length() > 0 && param2.length() > 0){
            fullName = param1 + " " + param2;
        }
        SearchedUsersFragment fragment = new SearchedUsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initController(View rootView){
        searchUserTitle = (TextView) rootView.findViewById(R.id.su_title_text);
        userNameTxt = (TextView) rootView.findViewById(R.id.userNameTxt);
        suBack = (ImageView)rootView.findViewById(R.id.suBackImageView);
        suBack.setImageResource(R.drawable.back_img);

        listUsers = (ListView) rootView.findViewById(R.id.listUsers);
        int resID = R.layout.adapter_users;
        adapter = new UsersAdapter(getActivity(), resID, users);
        listUsers.setAdapter(adapter);
        adapter.updateData(users);

        userNameTxt.setText(fullName);
    }
    private void initNewUserVoAndPatient(){
        String currentUserVoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTUSER);
        if (!currentUserVoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<UserVo>() {
            }.getType();
            currentUserVo = gson.fromJson(currentUserVoStr, type);
        }

        String registeredPatientStr = sharedPreferencesManager.getRegisteredPatients( SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEARCH);
        users = new ArrayList<Patient>();
        if (!registeredPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Patient>>() {
            }.getType();
            patients = gson.fromJson(registeredPatientStr, type);
            for (int i = 0; i < patients.size(); i++) {
                Patient onePatient = patients.get(i);
                if (i == 0){ //set the First name and last name of first patient if full name is empty, which could happen when searchtype = 2(Aadhaar number)
                    fullName = onePatient.getFirstName() + " " + onePatient.getLastName();
                }
                users.add(onePatient);
            }
        }
    }
    private void initFontAndText(View rootView){
        searchUserTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
    }

    private void setListener(final View rootView){
        searchUserTitle.setText(getResources().getString(R.string.su_title));
        suBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPatient = patients.get(i);
                UsersManagement.saveCurrentPatent(selectedPatient, sharedPreferencesManager);
                apiCallSeachTestingResult(selectedPatient);
            }
        });
    }

    protected void onBackPressed(){
        fragmentManager.popBackStack(FragmentPopKeys.SEARCH, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void apiCallSeachTestingResult(final Patient patient) {
        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Searching…");
        hud.show();
        accountClient.searchTestingResult(getActivity(), sharedPreferencesManager, patient.getPatientId(), sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SEARCHTYPE)
                ,sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHKEY) , sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHFROMDATE)
                ,  sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHTODATE), new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                ArrayList<TestingResult> searchedTestingResults = new ArrayList<TestingResult>();
                searchedTestingResults = (ArrayList<TestingResult>) responseObject;
                if (searchedTestingResults.size() == 0) {
                    onSearchCompleted(statusCode, errorMessage, false);
                } else {
                    UsersManagement.saveSearchedTestingResult(searchedTestingResults, sharedPreferencesManager, patient.getPatientId(),  SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEARCH);
                    patient.setTestingResults(searchedTestingResults);
                    UsersManagement.saveCurrentPatent(patient, sharedPreferencesManager);
                    onSearchCompleted(statusCode, errorMessage, true);
                }
            }

            @Override
            public <E> void onSuccessJsonArray(E responseObject, int statusCode, String errorMessage) {
            }

            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onSearchFailed(errorCode, errorMessage);
            }
        });
    }

    public void onSearchCompleted(int statusCode, String errorMessage, boolean isReturn) {
        if (hud!= null)hud.dismiss();
        if (isReturn) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.search_root_frame, SearchReportsForOnePatient.newInstance("", ""), "Post")
                    .addToBackStack(FragmentPopKeys.SEARCHRESULT)
                    .commit();
            FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);
        } else {
            onCheckingTestingResults();
        }
    }

    public void onSearchFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(getActivity(), errorMessage);
                break;
            case 404:
                PrintUtil.showToast(getActivity(), errorMessage);
                break;
            case 0:
                PrintUtil.showToast(getActivity(), getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(getActivity(), getResources().getString(R.string.internet_connection));
                break;
            default:
                if (errorMessage.equals(""))
                    PrintUtil.showToast(getActivity(), getResources().getString(R.string.something_went_wrong));
                else{
                    PrintUtil.showToast(getActivity(), errorMessage);
                }
        }
    }

    public void showDialogBox(String message, final EditText editText, String Title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(Title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editText != null) {
                    // editText.setText("");
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            //TODO show keyboard
                            openLoginKeyboard();
                        }
                    });
                    if (editText != null) {
                        editText.requestFocus();
                        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
                    }
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void openLoginKeyboard() {
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);

    }

    private void onCheckingTestingResults(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(getResources().getString(R.string.noTestingResults))
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        ((HomeActivity)getActivity()).gotoTestBySelectedPatientView();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();

        PrintUtil.showToast(getActivity(), "No Testing results");
    }
}