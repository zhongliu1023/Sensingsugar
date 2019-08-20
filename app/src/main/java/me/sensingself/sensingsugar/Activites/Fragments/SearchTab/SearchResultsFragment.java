package me.sensingself.sensingsugar.Activites.Fragments.SearchTab;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.otto.Subscribe;

import me.sensingself.sensingsugar.Activites.Adapter.SearchPatientsAdapter;
import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.Interfaces.PDFListener;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.PDFUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Constants.PermissionRequest;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by liujie on 2/5/18.
 */

public class SearchResultsFragment extends BaseFragment implements ExpandableStickyListHeadersListView.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView searchTitle, shareTxtView;
    private StickyListHeadersListView searchedResultListView;
    private ImageView searchResultBackImageView;
    SearchPatientsAdapter adapter;

    WeakHashMap<View,Integer> mOriginalViewHeightPool = new WeakHashMap<View, Integer>();
    private SharedPreferencesManager sharedPreferencesManager;
    ArrayList<Patient> patients = new ArrayList<Patient>();

    private KProgressHUD hud;
    private AccountClient accountClient;

    private TextView fromDateLabel, fromDateTxt, toDateLabel, toDateTxt, reportsLabel, reportsTxt;

    private RelativeLayout linearLayout4;

    FragmentManager fragmentManager;
    UserVo currentUserVo = new UserVo();

    public static SearchResultsFragment newInstance(String param1, String param2) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_result, container, false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();
        fragmentManager = FragmentmanagerUtils.getFragmentManagerSearch();

        initNewUserVo();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        searchedResultListView.setAdapter(adapter);
        sortRegisteredPatients();
        adapter.updateData(patients);
    }
    private void initNewUserVo(){
        String currentUserVoStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTUSER);
        if (!currentUserVoStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<UserVo>() {
            }.getType();
            currentUserVo = gson.fromJson(currentUserVoStr, type);
        }
    }
    private void initController(View rootView) {
        searchTitle = (TextView) rootView.findViewById(R.id.searchTitle);
        shareTxtView = (TextView) rootView.findViewById(R.id.shareTxtView);
        searchResultBackImageView = (ImageView) rootView.findViewById(R.id.searchBackImageView);

        fromDateLabel = (TextView) rootView.findViewById(R.id.fromDateLabel);
        fromDateTxt = (TextView) rootView.findViewById(R.id.fromDateTxt);
        toDateLabel = (TextView) rootView.findViewById(R.id.toDateLabel);
        toDateTxt = (TextView) rootView.findViewById(R.id.toDateTxt);
        reportsLabel = (TextView) rootView.findViewById(R.id.reportsLabel);
        reportsTxt = (TextView) rootView.findViewById(R.id.reportsTxt);

        linearLayout4 = (RelativeLayout) rootView.findViewById(R.id.linearLayout4);

        int searchMode = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SEARCHMODE);
        if (searchMode == 1){
            fromDateTxt.setText(DateUtils.string2StringDate(sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHFROMDATE), "yyyy-MM-dd", "dd.MM.yyyy"));
            toDateTxt.setText(DateUtils.string2StringDate(sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHTODATE), "yyyy-MM-dd", "dd.MM.yyyy"));
        }else{
            linearLayout4.setVisibility(View.GONE);
        }


        searchedResultListView = (StickyListHeadersListView) rootView.findViewById(R.id.resultsList);
        searchedResultListView.setDivider(null);
        searchedResultListView.setDrawingListUnderStickyHeader(true);
        searchedResultListView.setAreHeadersSticky(true);
        searchResultBackImageView.setImageResource(R.drawable.back_img);
        sortRegisteredPatients();
        adapter = new SearchPatientsAdapter(getActivity(), R.layout.adapter_date_month, patients, new Patient());
        adapter.setParientFragment(this);
        searchedResultListView.setAdapter(adapter);
        searchedResultListView.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {

            }
        });
        searchedResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Patient patient = patients.get(position);
                UsersManagement.saveCurrentPatent(patient, sharedPreferencesManager);
                apiCallSeachTestingResult(patient);
            }
        });

        reportsTxt.setText(String.valueOf(patients.size()));
    }

    public void updateMultiPatients(int startPosition, int count, boolean isSelected){
        for (int i = 0; i < count; i ++){
            Patient patient = patients.get(i + startPosition);
            patient.setSelected(isSelected);
            patients.set(i + startPosition, patient);
        }
    }
    private void sortRegisteredPatients(){
        String registeredPatientStr = sharedPreferencesManager.getRegisteredPatients( SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEARCH);
        boolean isFound = false;
        if (!registeredPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Patient>>() {
            }.getType();
            patients = gson.fromJson(registeredPatientStr, type);
        }
        Collections.sort(patients, new Comparator<Patient>() {
            @Override
            public int compare(Patient entry1, Patient entry2) {
                if (entry1.getTestdate() > entry2.getTestdate()) {
                    return 1;
                }
                else if (entry1.getTestdate() <  entry2.getTestdate()) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
    }
    private void initFontAndText(View rootView) {
        searchTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        shareTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));

        fromDateLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        fromDateTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        toDateLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        toDateTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        reportsLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        reportsTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
    }

    private void setListener(final View rootView) {
        searchTitle.setText(getResources().getString(R.string.search_btn));
        searchResultBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        shareTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSharingReports();
            }
        });

    }

    protected void onBackPressed(){
        fragmentManager.popBackStack(FragmentPopKeys.SEARCH, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    @Override
    public void onClick(View v) {

    }

    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {

        @Override
        public void executeAnim(final View target, final int animType) {
            if(ExpandableStickyListHeadersListView.ANIMATION_EXPAND==animType&&target.getVisibility()==View.VISIBLE){
                return;
            }
            if(ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE==animType&&target.getVisibility()!=View.VISIBLE){
                return;
            }
            if(mOriginalViewHeightPool.get(target)==null){
                mOriginalViewHeightPool.put(target,target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                    target.setLayoutParams(lp);
                    target.requestLayout();
                }
            });
            animator.start();

        }
    }

    private void apiCallSeachTestingResult(final Patient patient) {
        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Searching…");
        hud.show();
        int searchMode = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SEARCHMODE);
        String fromDateStr = "";
        String toDateStr = "";
        if (searchMode == 1){
            fromDateStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHFROMDATE);
            toDateStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHTODATE);
        }

        accountClient.searchTestingResult(getActivity(), sharedPreferencesManager, patient.getPatientId(), sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SEARCHTYPE)
                ,sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHKEY) , fromDateStr,  toDateStr, new APICallbacks() {
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
        PrintUtil.showToast(getActivity(), "No Testing results");
    }

    private void onSharingReports(){
        if (!checkPermissions())
            return;

        ArrayList<Patient> sharingPatients = new ArrayList<Patient>();
        boolean isPatientsShared = false;
        for (Patient patient : patients ){
            if (patient.getSelected()){
                isPatientsShared = true;
                sharingPatients.add(patient);
            }
        }
        if (!isPatientsShared){
            PrintUtil.showToast(getActivity(), getResources().getString(R.string.no_sharing));
        }else{
            String userName = "";
            if (currentUserVo.getFirstName().length() > 0){
                userName = currentUserVo.getFirstName().substring(0,1);
            }
            if (currentUserVo.getLastName().length() > 0){
                userName = userName  +  currentUserVo.getLastName().substring(0,1);
            }
            String pdfName = currentUserVo.getUserId() +"_"+ userName +"_"+ (int)(System.currentTimeMillis()/1000) + ".pdf";

            hud = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Sharing reports…");
            hud.show();
            PDFUtils.createPDFFromPatients(getActivity(), sharingPatients, Environment.getExternalStorageDirectory().getAbsolutePath()+"/SensingSugar/" + pdfName, currentUserVo,false, new PDFListener() {
                @Override
                public void createPDF(String path) {
                    hud.dismiss();
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, setUriForPDF(path));
                    shareIntent.setType("*/*");
                    startActivity(Intent.createChooser(shareIntent, ""));
                }
            });
        }
    }
    public Uri setUriForPDF(String pdfPath) {
        File pdfFile = new File(pdfPath);
        Uri pdfPathUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pdfPathUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", pdfFile);
        } else {
            pdfPathUri = Uri.fromFile(pdfFile);
        }
        return pdfPathUri;
    }
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        String[] PERMISSIONS = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            final String perm = PERMISSIONS[i];
            if (ActivityCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(perm);
            }
        }

        if (permissionsNeeded.size() == 0)
            return true;

        requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PermissionRequest.READ_WRITE_STORAGE);
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionRequest.READ_WRITE_STORAGE:
                if (grantResults.length > 0) {
                    // All requested permissions must be granted.
                    boolean allPermissionsGranted = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        onSharingReports();
                        return;
                    }
                }
                Toast.makeText(getActivity(), "External storage permission is necessary.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}