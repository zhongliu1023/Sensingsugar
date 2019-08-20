package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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

import java.lang.reflect.Type;
import java.util.ArrayList;

import me.sensingself.sensingsugar.Activites.Adapter.UsersAdapter;
import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 3/1/18.
 */

public class SeedSearchedUsersFragment  extends BaseFragment {
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

    static String fullName = "";
    FragmentManager fragmentManager;

    public static SeedSearchedUsersFragment newInstance(String param1, String param2) {
        if (param1.length() > 0 && param2.length() > 0){
            fullName = param1 + " " + param2;
        }
        SeedSearchedUsersFragment fragment = new SeedSearchedUsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab_search_userlist, container, false);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        fragmentManager = FragmentmanagerUtils.getFragmentManagerIdentify();

        initNewUserVoAndPatient();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
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

        String registeredPatientStr = sharedPreferencesManager.getRegisteredPatients(SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEED);
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
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.seed_root_frame, SeedBeginTestFragment.newInstance(FragmentPopKeys.SEEEDSEARCH, "", false), "Post")
                        .addToBackStack(FragmentPopKeys.SEEEDSEARCH)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
            }
        });
    }

    protected void onBackPressed(){
        fragmentManager.popBackStack(FragmentPopKeys.SEED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}