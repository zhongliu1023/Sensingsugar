package me.sensingself.sensingsugar.Activites.Fragments.HomeTab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.chootdev.imagecache.CImageLoader;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Activites.HomeActivity;
import me.sensingself.sensingsugar.Activites.LoginRegisterProfile.LoginActivity;

import me.sensingself.sensingsugar.BuildConfig;
import me.sensingself.sensingsugar.Common.Interfaces.ImageListener;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.ActivityCodes;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.ImageCropUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.URLConstant;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Constants.PermissionRequest;
import me.sensingself.sensingsugar.Engine.CameraCropLib.AvatarCropActivity;
import me.sensingself.sensingsugar.HandleToAws.AwsUploadUtil;
import me.sensingself.sensingsugar.HandleToAws.Constants;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Lib.SoftKeyboardHandle;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by liujie on 1/30/18.
 */

public class HomeProfileViewFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnKeyListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Jella_log";
    private TextView pvTitle, pvBackTxtView, pvSignOutTxtView;
    private ImageView backPvImageView;
    private TextView pvThumbNameTxtView, pvAadhaarTxtView, pvFullNameTxtView, pvFullPhoneNumberTxtView ;
    private TextView pvServicePerformanceTxtView, pvTotalReadingsTxtView, pvTotalReadingsValueTxtView;
    private TextView pvTotalPatientsTxtView, pvTotalPatitentsValueTxtView, pvTestingPeriodTxtView, pvTestingPeriodValueTxtView;
    private TextView pvServedForTxtView, pvInstitutionTypeTxtView, pvInstitutionTypeValueTxtView, pvNameInstitutionTxtView;
    private TextView pvStateTxtView, pvCityTxtView, pvSupporttxtView, pvTermsConditionsTxtView;
    private TextView pvSoftwareVersionTxtView;
    private CircleImageView avatarImageView;
    private ImageView sensingsuarLogo;

    private LinearLayout profileMainInfoLinearLayout;
    private TextView pvPatientNamelabel, pvSurNamelabel, pvMobileLabel;
    private EditText pvAadhaarValueEditView, userFirstNameTxt, userLastNameTxt, editPhoneNumberEditView;
    private CountryCodePicker editProfileCCP;
    private EditText pvNameInstitutionValueTxtView, pvStateValueTxtView, pvCityValueTxtView;

    private static final String[] institutionTypies = {"Clinic/Hospital", "School/College", "NGOs", "Business", "Family/Community", "Institution Type"};
    private Spinner spinnerInstitutionType;
    private TextView addphotoTxt;


    CImageLoader loader;

    private LinearLayout subLayout1, subLayout2, subLayout3, subLayout4, subLayout5, subLayout6, subLayout7, subLayout8, subLayout9, subLayout10, subLayout11,subLayout12, profileViewLayout;
    private View underLineView1, underLineView2, underLineView3, underLineView4, underLineView5, underLineView6, underLineView7, underLineView8, underLineView9, underLineView10, underLineView11;
    private boolean isProfileViewMode = true;  // true: profile view mode   false: Edit mode


    private Button btnEditProfile;

    private SharedPreferencesManager sharedPreferencesManager;
    private UserVo currentUserVo = new UserVo();

    boolean isInitializedController = false;

    String selectedInstitutionType = "";


    private KProgressHUD hud;
    private AccountClient accountClient;
    private boolean isValidPhoneNumber = false;

    private View rootView;



    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    private String currentAvatatLocalPath = "";
    private String currentAvatatFileName = "";

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 100;
    private Uri imgPathUri;

    FragmentManager fragmentManager;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_view, container, false);
        fragmentManager = FragmentmanagerUtils.getFragmentManagerHome();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        SoftKeyboardHandle.setupUI(rootView.findViewById(R.id.profileViewLayout), getActivity());
        accountClient = new AccountClient();
        transferUtility = AwsUploadUtil.getTransferUtility(getActivity());
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);

        initNewUserVo();
        initController();
        initFontAndText();
        fillDataFromCurrentUserVo();
        setListener();
        return rootView;
    }
    public static HomeProfileViewFragment newInstance(String param1, String param2) {
        HomeProfileViewFragment fragment = new HomeProfileViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

    @Override
    public void onResume() {
        super.onResume();
        if (isInitializedController && isProfileViewMode){
            initNewUserVo();
            fillDataFromCurrentUserVo();
        }
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        TransferListener listener = new HomeProfileViewFragment.UploadListener();
        for (TransferObserver observer : observers) {
            File imgFile = new File(observer.getAbsoluteFilePath());
            if(imgFile.exists()) {
                if (TransferState.WAITING.equals(observer.getState())
                        || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                        || TransferState.IN_PROGRESS.equals(observer.getState())) {
                    observer.setTransferListener(listener);
                }else if (TransferState.COMPLETED.equals(observer.getState())){
                    transferUtility.deleteTransferRecord(observer.getId());
                }
            }else{
                transferUtility.cancel(observer.getId());
                transferUtility.deleteTransferRecord(observer.getId());
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            AwsUploadUtil.fillMap(map, observer, false);
            Log.d("", "");
        }
    }
    protected void onBackPressed(){
        if (isProfileViewMode){
            closeKeyboard();
            if (((HomeActivity)getActivity()) != null) {
                ((HomeActivity)getActivity()).changeDarkTheme();
            }
            fragmentManager.popBackStack(FragmentPopKeys.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else{
            onCheckingProfileEditStatus();
        }
    }
    private void initController() {
        pvTitle = (TextView) rootView.findViewById(R.id.pvTitle);
        pvTitle.setText(getResources().getString(R.string.profile));
        sensingsuarLogo = (ImageView)rootView.findViewById(R.id.sensingsuarLogo);
        pvBackTxtView = (TextView) rootView.findViewById(R.id.pvBackTxtView);
        pvSignOutTxtView = (TextView) rootView.findViewById(R.id.pvSignOutTxtView);
        profileViewLayout = (LinearLayout)rootView.findViewById(R.id.profileViewLayout);
        backPvImageView = (ImageView) rootView.findViewById(R.id.backPvImageView);
        backPvImageView.setImageResource(R.drawable.back_img);
        pvThumbNameTxtView = (TextView) rootView.findViewById(R.id.pvThumbNameTxtView);
        pvAadhaarTxtView = (TextView) rootView.findViewById(R.id.pvAadhaarTxtView);
        pvFullNameTxtView = (TextView) rootView.findViewById(R.id.pvFullNameTxtView);
        pvFullPhoneNumberTxtView = (TextView) rootView.findViewById(R.id.pvFullPhoneNumberTxtView);
        pvServicePerformanceTxtView = (TextView) rootView.findViewById(R.id.pvServicePerformanceTxtView);
        pvTotalReadingsTxtView = (TextView) rootView.findViewById(R.id.pvTotalReadingsTxtView);
        pvTotalReadingsValueTxtView = (TextView) rootView.findViewById(R.id.pvTotalReadingsValueTxtView);
        pvTotalPatientsTxtView = (TextView) rootView.findViewById(R.id.pvTotalPatientsTxtView);
        pvTotalPatitentsValueTxtView = (TextView) rootView.findViewById(R.id.pvTotalPatitentsValueTxtView);
        pvTestingPeriodTxtView = (TextView) rootView.findViewById(R.id.pvTestingPeriodTxtView);
        pvTestingPeriodValueTxtView = (TextView) rootView.findViewById(R.id.pvTestingPeriodValueTxtView);
        pvServedForTxtView = (TextView) rootView.findViewById(R.id.pvServedForTxtView);
        pvInstitutionTypeTxtView = (TextView) rootView.findViewById(R.id.pvInstitutionTypeTxtView);
        pvInstitutionTypeValueTxtView = (TextView) rootView.findViewById(R.id.pvInstitutionTypeValueTxtView);
        pvNameInstitutionTxtView = (TextView) rootView.findViewById(R.id.pvNameInstitutionTxtView);
        pvNameInstitutionValueTxtView = (EditText) rootView.findViewById(R.id.pvNameInstitutionValueTxtView);
        pvStateTxtView = (TextView) rootView.findViewById(R.id.pvStateTxtView);
        pvStateValueTxtView = (EditText) rootView.findViewById(R.id.pvStateValueTxtView);
        pvCityTxtView = (TextView) rootView.findViewById(R.id.pvCityTxtView);
        pvCityValueTxtView = (EditText) rootView.findViewById(R.id.pvCityValueTxtView);
        pvSupporttxtView = (TextView) rootView.findViewById(R.id.pvSupporttxtView);
        pvTermsConditionsTxtView = (TextView) rootView.findViewById(R.id.pvTermsConditionsTxtView);
        pvSoftwareVersionTxtView = (TextView) rootView.findViewById(R.id.pvSoftwareVersionTxtView);
        avatarImageView = (CircleImageView) rootView.findViewById(R.id.avatarImageView);


        profileMainInfoLinearLayout = (LinearLayout) rootView.findViewById(R.id.profileMainInfoLinearLayout);
        profileMainInfoLinearLayout.setVisibility(View.GONE);
        pvPatientNamelabel = (TextView)rootView.findViewById(R.id.pvPatientNamelabel);
        pvSurNamelabel = (TextView)rootView.findViewById(R.id.pvSurNamelabel);
        pvMobileLabel = (TextView)rootView.findViewById(R.id.pvMobileLabel);
        pvAadhaarValueEditView = (EditText) rootView.findViewById(R.id.pvAadhaarValueEditView);

        userFirstNameTxt = (EditText) rootView.findViewById(R.id.userFirstNameTxt);
        userLastNameTxt = (EditText) rootView.findViewById(R.id.userLastNameTxt);
        editPhoneNumberEditView = (EditText) rootView.findViewById(R.id.editPhoneNumberEditView);
        editProfileCCP = (CountryCodePicker) rootView.findViewById(R.id.editProfileCCP);

        btnEditProfile = (Button) rootView.findViewById(R.id.btnEditProfile);
        isInitializedController = true;

        addphotoTxt = (TextView)rootView.findViewById(R.id.addPhotoTxt);
        addphotoTxt.setVisibility(View.GONE);
        subLayout1 = (LinearLayout)rootView.findViewById(R.id.subLayout1);
        subLayout2 = (LinearLayout)rootView.findViewById(R.id.subLayout2);
        subLayout3 = (LinearLayout)rootView.findViewById(R.id.subLayout3);
        subLayout4 = (LinearLayout)rootView.findViewById(R.id.subLayout4);
        subLayout5 = (LinearLayout)rootView.findViewById(R.id.subLayout5);
        subLayout6 = (LinearLayout)rootView.findViewById(R.id.subLayout6);
        subLayout7 = (LinearLayout)rootView.findViewById(R.id.subLayout7);
        subLayout8 = (LinearLayout)rootView.findViewById(R.id.subLayout8);
        subLayout9 = (LinearLayout)rootView.findViewById(R.id.subLayout9);
        subLayout10 = (LinearLayout)rootView.findViewById(R.id.subLayout10);
        subLayout11 = (LinearLayout)rootView.findViewById(R.id.subLayout11);
        subLayout12 = (LinearLayout)rootView.findViewById(R.id.subLayout12);
        underLineView1 = (View)rootView.findViewById(R.id.underLineView1);
        underLineView2 = (View)rootView.findViewById(R.id.underLineView2);
        underLineView3 = (View)rootView.findViewById(R.id.underLineView3);
        underLineView4 = (View)rootView.findViewById(R.id.underLineView4);
        underLineView5 = (View)rootView.findViewById(R.id.underLineView5);
        underLineView6 = (View)rootView.findViewById(R.id.underLineView6);
        underLineView7 = (View)rootView.findViewById(R.id.underLineView7);
        underLineView8 = (View)rootView.findViewById(R.id.underLineView8);
        underLineView9 = (View)rootView.findViewById(R.id.underLineView9);
        underLineView10 = (View)rootView.findViewById(R.id.underLineView10);
        underLineView11 = (View)rootView.findViewById(R.id.underLineView11);


        spinnerInstitutionType = (Spinner)rootView.findViewById(R.id.spinnerInstitutionType);
        MySpinnerAdapter adapter = new MySpinnerAdapter(
                getActivity(),
                R.layout.spinner_default_item_right,
                institutionTypies
        );

        adapter.setDropDownViewResource(R.layout.spinner_default_item_right);
        spinnerInstitutionType.setAdapter(adapter);
        spinnerInstitutionType.setOnItemSelectedListener(this);
        spinnerInstitutionType.setSelection(adapter.getCount());

        spinnerInstitutionType.setVisibility(View.GONE);
        pvInstitutionTypeValueTxtView.setVisibility(View.VISIBLE);
        pvNameInstitutionValueTxtView.setInputType(InputType.TYPE_NULL);
        pvStateValueTxtView.setInputType(InputType.TYPE_NULL);
        pvCityValueTxtView.setInputType(InputType.TYPE_NULL);
        pvAadhaarValueEditView.setInputType(InputType.TYPE_NULL);

        loader = new CImageLoader(getActivity(), getResources().getColor(R.color.translucentWhite));
    }
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if (position != institutionTypies.length-1){
            selectedInstitutionType = institutionTypies[position];
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void fillDataFromCurrentUserVo(){
        //fill info of user
        if (currentUserVo != null){
            String firstName  = currentUserVo.getFirstName();
            String lastName = currentUserVo.getLastName();
            if (firstName.length() > 0 && lastName.length() > 0){
                pvThumbNameTxtView.setText(firstName.substring(0,1) + lastName.substring(0,1) );
            }
            if (currentUserVo.getAadhaar().length() != 0){
                InputFilter[] fArray1 = new InputFilter[1];
                fArray1[0] = new InputFilter.LengthFilter(14);
                pvAadhaarValueEditView.setFilters(fArray1);
                String aadhaarNum = currentUserVo.getAadhaar();
                pvAadhaarValueEditView.setText(aadhaarNum.replaceAll("(\\d{4})(?=\\d)", "$1 "));
            }
            pvFullNameTxtView.setText(firstName +" "+ lastName);
            pvFullPhoneNumberTxtView.setText(currentUserVo.getPhoneNumber());
            pvInstitutionTypeValueTxtView.setText(currentUserVo.getInstitutionType());
            pvNameInstitutionValueTxtView.setText(currentUserVo.getInstitutationName());
            pvStateValueTxtView.setText(currentUserVo.getState());
            pvCityValueTxtView.setText(currentUserVo.getCity());
            selectedInstitutionType = currentUserVo.getInstitutionType();
            String avatarUrl = currentUserVo.getAvatarUrl();
            if (avatarUrl.equals("")){
                avatarImageView.setVisibility(View.GONE);
            }else{
                    loader.DisplayImage(URLConstant.AVATARBASEURL + avatarUrl,avatarImageView);
                    avatarImageView.setVisibility(View.VISIBLE);
            }
            pvTotalReadingsValueTxtView.setText(String.valueOf(currentUserVo.getTotalReading()));
            pvTotalPatitentsValueTxtView.setText(String.valueOf(currentUserVo.getTotalPatients()));
            pvTestingPeriodValueTxtView.setText(String.valueOf(currentUserVo.getTestingPeriod()) + " days");
        }
    }
    private void initFontAndText(){
        pvTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvBackTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvSignOutTxtView .setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvThumbNameTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvAadhaarTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvFullNameTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvFullPhoneNumberTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvServicePerformanceTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvTotalReadingsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvTotalReadingsValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvTotalPatientsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvTotalPatitentsValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvTestingPeriodTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvTestingPeriodValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvServedForTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvInstitutionTypeTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvInstitutionTypeValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvNameInstitutionTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvNameInstitutionValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvStateTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvStateValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvCityTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvCityValueTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvSupporttxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvTermsConditionsTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        pvSoftwareVersionTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        btnEditProfile.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        userFirstNameTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        userLastNameTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        editPhoneNumberEditView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        editProfileCCP.setTypeFace(FontUtility.getOfficinaSansCBold(rootView.getContext()));


        pvPatientNamelabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvSurNamelabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvMobileLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        pvAadhaarValueEditView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));

        pvSoftwareVersionTxtView.setText("Software Version " + BuildConfig.VERSION_NAME);
        sensingsuarLogo.setImageResource(R.drawable.sensingsugar);

    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackButtonPressed();
                return true;
            }
        }

        return false;
    }

    public void onBackButtonPressed() {
        Log.w("","");
    }

    private static class MySpinnerAdapter extends ArrayAdapter<String> {
        // (In reality I used a manager which caches the Typeface objects)
        // Typeface font = FontManager.getInstance().getFont(getContext(), BLAMBOT);

        private MySpinnerAdapter(Context context, int resource, String[] items) {
            super(context, resource, items);
        }

        // Affects default (closed) state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(FontUtility.getOfficinaSansCBold(getContext()));
            view.setTextColor(getContext().getResources().getColor(R.color.colorDarkBrown));
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(FontUtility.getOfficinaSansCBold(getContext()));
            return view;
        }

        @Override
        public int getCount() {

            // TODO Auto-generated method stub
            int count = super.getCount();

            return count>0 ? count-1 : count ;


        }
    }
    private void setListener(){
        backPvImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pvBackTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isProfileViewMode){

                    isValidPhoneNumber = false;
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isValidPhoneNumber = true;
                        }
                    }, 700);
                    int maxLength = 20;
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    editPhoneNumberEditView.setFilters(fArray);

                    profileMainInfoLinearLayout.setVisibility(View.VISIBLE);
                    isProfileViewMode = false;
                    pvServicePerformanceTxtView.setVisibility(View.GONE);
                    subLayout2.setVisibility(View.GONE);
                    subLayout3.setVisibility(View.GONE);
                    subLayout4.setVisibility(View.GONE);
                    subLayout10.setVisibility(View.GONE);
                    subLayout11.setVisibility(View.GONE);
                    subLayout12.setVisibility(View.GONE);
                    underLineView2.setVisibility(View.GONE);
                    underLineView3.setVisibility(View.GONE);
                    underLineView4.setVisibility(View.GONE);
                    underLineView10.setVisibility(View.GONE);
                    underLineView11.setVisibility(View.GONE);
                    addphotoTxt.setVisibility(View.VISIBLE);
                    btnEditProfile.setText(getResources().getString(R.string.done_btn));
                    btnEditProfile.setTextColor(getResources().getColor(R.color.colorPrimaryButton));
                    btnEditProfile.setBackground(getResources().getDrawable(R.drawable.radius_controller_btn_white));

                    pvNameInstitutionValueTxtView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    pvStateValueTxtView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    pvCityValueTxtView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                    pvSignOutTxtView.setVisibility(View.GONE);
                    pvTitle.setText(getResources().getString(R.string.editProfile));

                    if (!(currentUserVo.getInstitutionType().equals(""))){
                        for (int i = 0; i < institutionTypies.length; i ++){
                            String currentType = institutionTypies[i];
                            if (currentType.equals(currentUserVo.getInstitutionType())){
                                spinnerInstitutionType.setSelection(i);
                            }
                        }
                    }
                    spinnerInstitutionType.setVisibility(View.VISIBLE);
                    pvInstitutionTypeValueTxtView.setVisibility(View.GONE);

                    pvAadhaarValueEditView.setText(currentUserVo.getAadhaar());
                    InputFilter[] fArray1 = new InputFilter[1];
                    fArray1[0] = new InputFilter.LengthFilter(12);
                    pvAadhaarValueEditView.setFilters(fArray1);
                    pvAadhaarValueEditView.setInputType(InputType.TYPE_CLASS_NUMBER);

                    userFirstNameTxt.setText(currentUserVo.getFirstName());
                    userLastNameTxt.setText(currentUserVo.getLastName());

                    String[] phoneArray = currentUserVo.getPhoneNumber().split(" ");
                    String phoneCodeStr = phoneArray[0];
                    int phoneCode = Integer.parseInt(phoneCodeStr.substring(1));
                    editProfileCCP.setCountryForPhoneCode(phoneCode);
                    String phoneNum = "";
                    for (int i = 1; i < phoneArray.length ; i ++){
                        phoneNum = phoneNum + phoneArray[i];
                    }
                    editPhoneNumberEditView.setText(phoneNum);

                    userFirstNameTxt.requestFocus();
                    currentAvatatFileName = "";
                    currentAvatatLocalPath = "";
                }else{
                    if (userFirstNameTxt.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter First Name", Toast.LENGTH_SHORT).show();
                    }else if (userLastNameTxt.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Last Name", Toast.LENGTH_SHORT).show();
                    }else if (pvNameInstitutionValueTxtView.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Name of Institution", Toast.LENGTH_SHORT).show();
                    }else if (pvAadhaarValueEditView.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Aadhaar", Toast.LENGTH_SHORT).show();
                    }else if (pvAadhaarValueEditView.getText().length() < 12){
                        Toast.makeText(getActivity(), "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                    }else if (editPhoneNumberEditView.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Mobile Number.", Toast.LENGTH_SHORT).show();
                    }else if (pvStateValueTxtView.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter State.", Toast.LENGTH_SHORT).show();
                    }else if (pvCityValueTxtView.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter city.", Toast.LENGTH_SHORT).show();
                    }else{
                        hud = KProgressHUD.create(getActivity())
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Saving information…");
                        hud.show();
                        beginUploadAvatar();
                    }
                }




            }
        });
        pvSignOutTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut(rootView);
            }
        });
        addphotoTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isProfileViewMode){
                    selectImage();
                }
            }
        });
        editProfileCCP.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
            }
        });
        editProfileCCP.registerCarrierNumberEditText(editPhoneNumberEditView);
        editProfileCCP.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidPhoneNumber){
                    if (isValidNumber) {
                        editPhoneNumberEditView.setTextColor(getResources().getColor(R.color.colorDarkBrown));
                        int maxLength = editPhoneNumberEditView.getText().length();
                        InputFilter[] fArray = new InputFilter[1];
                        fArray[0] = new InputFilter.LengthFilter(maxLength);
                        editPhoneNumberEditView.setFilters(fArray);
                    }else{
                        editPhoneNumberEditView.setTextColor(getResources().getColor(R.color.colorMobileNumberInvalid));
                    }
                }

            }
        });
        editProfileCCP.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                if (!isValidPhoneNumber)return;
                int maxLength = 20;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                editPhoneNumberEditView.setFilters(fArray);
                editPhoneNumberEditView.setText("");
                sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, editProfileCCP.getSelectedCountryCode());
            }
        });
        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, editProfileCCP.getSelectedCountryCode());
    }
    private void onCheckingProfileEditStatus(){
        if (isChangedProfile()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.changeSave))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateProfile();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            closeKeyboard();
                            if (isProfileViewMode == false){
                                onSaveUserCompleted();
                            }else{
                                fragmentManager.popBackStack(FragmentPopKeys.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            closeKeyboard();
            if (isProfileViewMode == false){
                onSaveUserCompleted();
            }else{
                fragmentManager.popBackStack(FragmentPopKeys.HOME, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }
    private boolean isChangedProfile(){
        if (!currentUserVo.getAadhaar().equals(pvAadhaarValueEditView.getText().toString())){
            return true;
        }else if (!currentUserVo.getFirstName().equals(userFirstNameTxt.getText().toString())){
            return true;
        }else if (!currentUserVo.getLastName().equals(userLastNameTxt.getText().toString())){
            return true;
        }else if (!currentUserVo.getInstitutionType().equals(selectedInstitutionType)){
            return true;
        }else if (!currentUserVo.getInstitutationName().equals(pvNameInstitutionValueTxtView.getText().toString())){
            return true;
        }else if (!currentUserVo.getState().equals(pvStateValueTxtView.getText().toString())){
            return true;
        }else if (!currentUserVo.getCity().equals(pvCityValueTxtView.getText().toString())){
            return true;
        }else if (currentAvatatFileName.length() > 0){
            return true;
        }else{
            String prePhoneNum = (currentUserVo.getPhoneNumber()).replaceAll("\\D+", "");
            String afterPhoneNum = (editProfileCCP.getSelectedCountryCodeWithPlus() + editPhoneNumberEditView.getText().toString()).replaceAll("\\D+", "");
            if (!prePhoneNum.equals(afterPhoneNum)){
                return true;
            }
        }
        return false;
    }
    private void signOut(final View rootView){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.logout))
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        initAllSharedData();
                        Intent mainIntent = new Intent(rootView.getContext(), LoginActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        rootView.getContext().startActivity(mainIntent);
                        getActivity().finishAffinity();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateProfile(){
        currentUserVo.setFirstName(userFirstNameTxt.getText().toString());
        currentUserVo.setLastName(userLastNameTxt.getText().toString());
        currentUserVo.setAadhaar(pvAadhaarValueEditView.getText().toString());
        currentUserVo.setPhoneNumber(editProfileCCP.getSelectedCountryCodeWithPlus() + " " + editPhoneNumberEditView.getText().toString());
        currentUserVo.setInstitutatinType(selectedInstitutionType);
        currentUserVo.setInstitutationName(pvNameInstitutionValueTxtView.getText().toString());
        currentUserVo.setState(pvStateValueTxtView.getText().toString());
        currentUserVo.setCity(pvCityValueTxtView.getText().toString());
        if (currentAvatatLocalPath == null || currentAvatatLocalPath.length() == 0) {

        }else{
            currentUserVo.setAvatarUrl(currentAvatatFileName);
        }


        updateUserInfo();
    }
    private void updateUserInfo(){
        accountClient.saveProfile(getActivity(), sharedPreferencesManager, currentUserVo, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onSaveUserCompleted();
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onSaveUserFailed(errorCode, errorMessage);
            }
        });
    }
    public void onSaveUserCompleted() {
        if (hud!= null)hud.dismiss();

        UsersManagement.saveCurrentUser(currentUserVo, sharedPreferencesManager);

        isProfileViewMode = true;
        profileMainInfoLinearLayout.setVisibility(View.GONE);
        pvServicePerformanceTxtView.setVisibility(View.VISIBLE);
        subLayout2.setVisibility(View.VISIBLE);
        subLayout3.setVisibility(View.VISIBLE);
        subLayout4.setVisibility(View.VISIBLE);
        subLayout10.setVisibility(View.VISIBLE);
        subLayout11.setVisibility(View.VISIBLE);
        subLayout12.setVisibility(View.VISIBLE);
        underLineView2.setVisibility(View.VISIBLE);
        underLineView3.setVisibility(View.VISIBLE);
        underLineView4.setVisibility(View.VISIBLE);
        underLineView10.setVisibility(View.VISIBLE);
        underLineView11.setVisibility(View.VISIBLE);
        btnEditProfile.setText(getResources().getString(R.string.editProfile));
        btnEditProfile.setTextColor(Color.WHITE);
        btnEditProfile.setBackground(getResources().getDrawable(R.drawable.radius_controller_btn));

        pvNameInstitutionValueTxtView.setInputType(InputType.TYPE_NULL);
        pvStateValueTxtView.setInputType(InputType.TYPE_NULL);
        pvCityValueTxtView.setInputType(InputType.TYPE_NULL);
        pvAadhaarValueEditView.setInputType(InputType.TYPE_NULL);

        addphotoTxt.setVisibility(View.GONE);
        pvSignOutTxtView.setVisibility(View.VISIBLE);
        pvTitle.setText(getResources().getString(R.string.profile));

        spinnerInstitutionType.setVisibility(View.GONE);
        pvInstitutionTypeValueTxtView.setVisibility(View.VISIBLE);

        userFirstNameTxt.setText("");
        userLastNameTxt.setText("");

        currentAvatatFileName = "";
        currentAvatatLocalPath = "";
        fillDataFromCurrentUserVo();

    }
    public void onSaveUserFailed(int errorCode, String errorMessage) {
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
                PrintUtil.showToast(getActivity(), getResources().getString(R.string
                        .something_went_wrong));
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
    public void closeKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null){
            if (getActivity().getCurrentFocus().getWindowToken() != null){
                inputMethodManager.hideSoftInputFromWindow(
                        getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
    private void initAllSharedData(){
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.ACCESS_TOKEN,"");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.ID_TOKEN,"");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.CURRENTPHONENUMBER, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.CURRENTPATIENT, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.CURRENTUSER, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.CURRENTCOUNTRY, "");
        sharedPreferencesManager.setPrefernceValueString( SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEARCH, "");
        sharedPreferencesManager.setPrefernceValueString( SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEED, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHKEY, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHFROMDATE, "");
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHTODATE, "");
        sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.SEARCHTYPE, 0);
        sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.SEARCHMODE, 0);
        sharedPreferencesManager.clearPrefernces();
    }

    public void selectImage() {
        final CharSequence[] items = {
                "Take Photo",
                "Choose from Library",
                "Cancel"
        };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        if (!checkPermissions(PermissionRequest.STORAGE_GALLERY))
            return;

        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_picture)), ActivityCodes.REQUEST_IMAGE_GALLERY);
    }

    private void cameraIntent() {
        if (!checkPermissions(PermissionRequest.PERMISSION_ALL))
            return;

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setUriForPhoto());
            startActivityForResult(cameraIntent, ActivityCodes.REQUEST_IMAGE_CAPTURE);
        }
    }

    private boolean checkPermissions(int requestCode) {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        String[] PERMISSIONS;
        if (requestCode == PermissionRequest.STORAGE_GALLERY) {
            PERMISSIONS = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        } else { // PermissionRequest.PERMISSION_ALL
            PERMISSIONS = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            final String perm = PERMISSIONS[i];
            if (ActivityCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(perm);
            }
        }

        if (permissionsNeeded.size() == 0)
            return true;

        requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), requestCode);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionRequest.PERMISSION_ALL:
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
                        cameraIntent();
                        return;
                    }
                }
                Toast.makeText(getActivity(), "No Camera Permission.", Toast.LENGTH_SHORT).show();
                break;
            case PermissionRequest.STORAGE_GALLERY:
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
                        galleryIntent();
                        return;
                    }
                }
                Toast.makeText(getActivity(), "External storage permission is necessary.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Don't forget to check requestCode before continuing your job
        if (resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case ActivityCodes.REQUEST_IMAGE_CAPTURE:
                    hud = KProgressHUD.create(getActivity())
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Resizing photo…");
                    hud.show();
                    ImageCropUtils.resizeImage(getActivity(), getImagePathUri(), true, new ImageListener() {
                        @Override
                        public void onImageResize(String path, String uri) {
                            if (hud!= null)hud.dismiss();
                            if (path != null && path.trim().length() > 0 && uri != null && uri.trim().length() > 0) {
                                goCrop(uri, path);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.unable_to_get_image), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, false);
                    break;
                case ActivityCodes.REQUEST_IMAGE_GALLERY:
                    hud = KProgressHUD.create(getActivity())
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Resizing photo…");
                    hud.show();
                    ImageCropUtils.resizeImage(getActivity(), data.getData(), true, new ImageListener() {
                        @Override
                        public void onImageResize(String path, String uri) {
                            if (hud!= null)hud.dismiss();
                            if (path != null && path.trim().length() > 0 && uri != null && uri.trim().length() > 0) {
                                goCrop(uri, path);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.unable_to_get_image), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, true);
                    break;
                case ActivityCodes.REQUEST_IMAGE_CROP:
                    if (data.getStringExtra("IMAGE_PATH") != null && data.getStringExtra("IMAGE_PATH").trim().length() > 0) {
                        onCaptureImageResult(data);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.unable_to_get_image), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }

    public Uri setUriForPhoto() {
        File pictureFile = new File(ImageCropUtils.getAppStoragePath(getActivity(), "") + "/" + Calendar.getInstance().getTimeInMillis() + ".jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imgPathUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", pictureFile);
        } else {
            imgPathUri = Uri.fromFile(pictureFile);
        }
        return imgPathUri;
    }
    public Uri getImagePathUri() {
        return imgPathUri;
    }

    private void goCrop(String sourUri, String path) {
        sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.AVATARIMAGENAME, "profile_image_" + currentUserVo.getUserId() + ".png");
        Intent cropIntent = new Intent(getActivity(), AvatarCropActivity.class);
        cropIntent.putExtra("IMAGE_PATH", path);
        startActivityForResult(cropIntent, ActivityCodes.REQUEST_IMAGE_CROP);
    }

    private void onCaptureImageResult(Intent data) {
        String imagePath = data.getStringExtra("IMAGE_PATH");
        File imgFile = new File(imagePath);
        currentAvatatFileName = imgFile.getName(); // temp
        currentAvatatLocalPath = imagePath;
        avatarImageView.setVisibility(View.VISIBLE);
        avatarImageView.setImageURI(Uri.fromFile(imgFile));
    }

    @SuppressLint("StaticFieldLeak")
    private void beginUploadAvatar() {
        if (currentAvatatLocalPath == null || currentAvatatLocalPath.length() == 0) {
            updateProfile();
        }else{

            new AsyncTask<SharedPreferencesManager, Void, Boolean>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                }

                @Override
                protected Boolean doInBackground(SharedPreferencesManager... params) {
                    AmazonS3Client s3Client = AwsUploadUtil.getS3Client(getActivity());
                    try {
                        if (s3Client.doesObjectExist(Constants.BUCKET_NAME, "profile/" +currentUserVo.getAvatarUrl())){
                            s3Client.deleteObject(new DeleteObjectRequest(Constants.BUCKET_NAME, "profile/" +currentUserVo.getAvatarUrl()));
                        }else{
                            return true;
                        }
                    } catch (AmazonServiceException ase) {
                        System.out.println("Caught an AmazonServiceException.");
                        System.out.println("Error Message:    " + ase.getMessage());
                        System.out.println("HTTP Status Code: " + ase.getStatusCode());
                        System.out.println("AWS Error Code:   " + ase.getErrorCode());
                        System.out.println("Error Type:       " + ase.getErrorType());
                        System.out.println("Request ID:       " + ase.getRequestId());
                    } catch (AmazonClientException ace) {
                        System.out.println("Caught an AmazonClientException.");
                        System.out.println("Error Message: " + ace.getMessage());
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    startToUploadAvatarS3();
                }
            }.execute(sharedPreferencesManager);


        }
    }
    private void startToUploadAvatarS3(){
        File file = new File(currentAvatatLocalPath);
        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, "profile/" +file.getName(),
                file, CannedAccessControlList.PublicReadWrite);

        observer.setTransferListener(new HomeProfileViewFragment.UploadListener());
    }
    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("", "Error during upload: " + id, e);
            if (hud!= null)hud.dismiss();
            PrintUtil.showToast(getActivity(), "Something wrong!");
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d("", "onStateChanged: " + id + ", " + newState);
            if(TransferState.COMPLETED.equals(newState)){
                updateProfile();
            }else if (TransferState.FAILED.equals(newState)){
                if (hud!= null)hud.dismiss();
                PrintUtil.showToast(getActivity(), "Faild to upload Avatar image, Please try.");
            }else if (TransferState.PENDING_NETWORK_DISCONNECT.equals(newState)){
                if (hud!= null)hud.dismiss();
                showDialogBox(getResources().getString(R.string.no_internet_connection_msg), null,
                        getResources().getString(R.string.no_intenet_connection_title));
            }else if (TransferState.UNKNOWN.equals(newState)){
                if (hud!= null)hud.dismiss();
                PrintUtil.showToast(getActivity(), "Something wrong!");
            }
        }
    }

}