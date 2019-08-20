package me.sensingself.sensingsugar.Activites.Fragments.SearchTab;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Lib.SoftKeyboardHandle;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by liujie on 2/7/18.
 */

public class PatientProfileViewFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView profileTitle, editText, contactTxt, mobileNumTxt, mobileNumValueTxt;
    private TextView bioDataTxt, birthdayTxt, birthdayValTxt, heightTxt, weightTxt , bmiTxt, bmiValTxt;
    private TextView aadhaartxt, aadhaarNumTxt , districtTxt , stateTxt;
    private ImageView profileBackImage;

    private EditText patientNameTxt, patientSurName, heightValTxt, weightValTxt, editPhoneNumberEditView;
    private EditText aadhaarNumValTxt, districtValTxt, stateValTxt;
    private CountryCodePicker editProfileCCP;
    private LinearLayout mobileNumberEditLayout;
    private TextView heightMesureLbl, weightMesureLbl;

    private SharedPreferencesManager sharedPreferencesManager;

    static Patient currentPatient = new Patient();

    private boolean isProfileViewMode = true;  // true: profile view mode   false: Edit mode

    private KProgressHUD hud;
    private AccountClient accountClient;
    private boolean isValidPhoneNumber = false;
    SimpleDateFormat simpleDateFormat;

    private String updatedBrithdDay;

    FragmentManager fragmentManager;

    public static PatientProfileViewFragment newInstance(String param1, String param2) {
        PatientProfileViewFragment fragment = new PatientProfileViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_for_patient, container, false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();
        SoftKeyboardHandle.setupUI(rootView.findViewById(R.id.profileViewLayout), getActivity());
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        fragmentManager = FragmentmanagerUtils.getFragmentManagerSearch();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }
    private void initNewPatient(){

        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY));
        String currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPATIENT);
        if (!currentPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Patient>() {
            }.getType();
            currentPatient = gson.fromJson(currentPatientStr, type);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        initNewPatient();
        initValue();
    }

    private void initController(View rootView) {
        profileTitle = (TextView) rootView.findViewById(R.id.profileTitle);
        editText = (TextView) rootView.findViewById(R.id.editText);
        patientNameTxt = (EditText) rootView.findViewById(R.id.patientNameTxt);
        patientSurName = (EditText) rootView.findViewById(R.id.patientSurName);
        patientNameTxt.setInputType(InputType.TYPE_NULL);
        patientSurName.setInputType(InputType.TYPE_NULL);
        contactTxt = (TextView) rootView.findViewById(R.id.contactTxt);
        mobileNumTxt = (TextView) rootView.findViewById(R.id.mobileNumTxt);
        mobileNumValueTxt = (TextView) rootView.findViewById(R.id.mobileNumValueTxt);
        bioDataTxt = (TextView) rootView.findViewById(R.id.bioDataTxt);
        birthdayTxt = (TextView) rootView.findViewById(R.id.birthdayTxt);
        birthdayValTxt = (TextView) rootView.findViewById(R.id.birthdayValTxt);
        heightTxt = (TextView) rootView.findViewById(R.id.heightTxt);
        heightValTxt = (EditText) rootView.findViewById(R.id.heightValTxt);
        heightValTxt.setInputType(InputType.TYPE_NULL);
        weightTxt = (TextView) rootView.findViewById(R.id.weightTxt);
        weightValTxt = (EditText) rootView.findViewById(R.id.weightValTxt);
        weightValTxt.setInputType(InputType.TYPE_NULL);
        bmiTxt = (TextView) rootView.findViewById(R.id.bmiTxt);
        bmiValTxt = (TextView) rootView.findViewById(R.id.bmiValTxt);
        aadhaartxt = (TextView) rootView.findViewById(R.id.aadhaartxt);
        aadhaarNumTxt = (TextView) rootView.findViewById(R.id.aadhaarNumTxt);
        aadhaarNumValTxt = (EditText) rootView.findViewById(R.id.aadhaarNumValTxt);
        aadhaarNumValTxt.setInputType(InputType.TYPE_NULL);
        districtTxt = (TextView) rootView.findViewById(R.id.districtTxt);
        districtValTxt = (EditText) rootView.findViewById(R.id.districtValTxt);
        districtValTxt.setInputType(InputType.TYPE_NULL);
        stateTxt = (TextView) rootView.findViewById(R.id.stateTxt);
        stateValTxt = (EditText) rootView.findViewById(R.id.stateValTxt);
        stateValTxt.setInputType(InputType.TYPE_NULL);

        heightMesureLbl = (TextView) rootView.findViewById(R.id.heightMesureLbl);
        weightMesureLbl = (TextView) rootView.findViewById(R.id.weightMesureLbl);

        mobileNumberEditLayout = (LinearLayout) rootView.findViewById(R.id.mobileNumberEditLayout);
        mobileNumberEditLayout.setVisibility(View.GONE);


        editPhoneNumberEditView = (EditText) rootView.findViewById(R.id.editPhoneNumberEditView);
        editProfileCCP = (CountryCodePicker) rootView.findViewById(R.id.editProfileCCP);

        profileBackImage = (ImageView) rootView.findViewById(R.id.profileBackImage);
        profileBackImage.setImageResource(R.drawable.back_img);

    }

    private void initFontAndText(View rootView) {
        profileTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        editText.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientNameTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        patientSurName.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        contactTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        mobileNumTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        mobileNumValueTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        bioDataTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        birthdayTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        birthdayValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        heightTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        heightValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weightTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weightValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        bmiTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        bmiValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        aadhaartxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        aadhaarNumTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        aadhaarNumValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        districtTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        districtValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        stateTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        stateValTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        editProfileCCP.setTypeFace(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        heightMesureLbl.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weightMesureLbl.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }
    private void initValue(){
        profileTitle.setText(getResources().getString(R.string.profile));
        patientNameTxt.setText(currentPatient.getFirstName());
        patientSurName.setText(currentPatient.getLastName());
        mobileNumValueTxt.setText(currentPatient.getPhoneNumber());

        float height = currentPatient.getHeight();
        float weight = currentPatient.getWeight();
        heightValTxt.setText(String.format("%d", Math.round(currentPatient.getHeight())));
        weightValTxt.setText(String.format("%d", Math.round(currentPatient.getWeight())));
        String[] phoneArray = currentPatient.getPhoneNumber().split(" ");
        String phoneCodeStr = phoneArray[0];
        int phoneCode = Integer.parseInt(phoneCodeStr.substring(1));
        if (phoneCode == 1){
            long heightIn = Math.round(height * 0.39);
            double weightPounds = Math.round(weight * 2.2046);
            heightValTxt.setText(String.format("%d", (int)heightIn));
            weightValTxt.setText(String.format("%d", (int)weightPounds));
            heightMesureLbl.setText("in");
            weightMesureLbl.setText("lbs");
        }else{
            heightMesureLbl.setText("cms");
            weightMesureLbl.setText("kgs");
        }

        if (!currentPatient.getBirthday().equals("")){
            String [] dateParts = currentPatient.getBirthday().split("-");
            String year = "";
            String month = "";
            String day = "";
            if (dateParts.length > 2){
                year = dateParts[0];
                month = dateParts[1];
                day = dateParts[2];
            }else{dateParts = currentPatient.getBirthday().split("/");
                if (dateParts.length > 2){
                    year = dateParts[0];
                    month = dateParts[1];
                    day = dateParts[2];
                }
            }
            birthdayValTxt.setText(DateUtils.string2StringDate(currentPatient.getBirthday(), "yyyy-MM-dd" ,"dd MMM yyy") + " (" + DateUtils.getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)) + " yrs)");
        }else{
            birthdayValTxt.setText(" yrs");
        }

        float bmi = Float.parseFloat(String.format("%.1f", (float)currentPatient.getWeight()/Math.pow((float)currentPatient.getHeight()/100, 2)));
        String categoryBmi = "";
        if (Float.compare(bmi, 15f) <= 0) {
            categoryBmi = "Severe";
        } else if (Float.compare(bmi, 15f) > 0  &&  Float.compare(bmi, 16f) <= 0) {
            categoryBmi = "Moderate";
        } else if (Float.compare(bmi, 16f) > 0  &&  Float.compare(bmi, 18.5f) <= 0) {
            categoryBmi = "Mild";
        } else if (Float.compare(bmi, 18.5f) > 0  &&  Float.compare(bmi, 25f) <= 0) {
            categoryBmi = "Normal";
        } else if (Float.compare(bmi, 25f) > 0  &&  Float.compare(bmi, 30f) <= 0) {
            categoryBmi = "Overweight";
        } else if (Float.compare(bmi, 30f) > 0  &&  Float.compare(bmi, 35f) <= 0) {
            categoryBmi = "Obese Class I";
        } else if (Float.compare(bmi, 35f) > 0  &&  Float.compare(bmi, 40f) <= 0) {
            categoryBmi = "Obese Class II";
        } else {
            categoryBmi = "Obese Class III";
        }
        bmiValTxt.setText(Float.toString(bmi) + " (" + categoryBmi +")");

        if (currentPatient.getAadhaar().length() != 0){
            InputFilter[] fArray1 = new InputFilter[1];
            fArray1[0] = new InputFilter.LengthFilter(14);
            aadhaarNumValTxt.setFilters(fArray1);
            String aadhaarNum = currentPatient.getAadhaar();
            aadhaarNumValTxt.setText(aadhaarNum.replaceAll("(\\d{4})(?=\\d)", "$1 "));
        }

        updatedBrithdDay = currentPatient.getBirthday();
        districtValTxt.setText(currentPatient.getDistrict());
        stateValTxt.setText(currentPatient.getState());
    }

    private void setListener(final View rootView) {
        profileBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Fragment fragment = this;
        birthdayValTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProfileViewMode) return;

                new SpinnerDatePickerDialogBuilder()
                        .context(getActivity())
                        .callback((DatePickerDialog.OnDateSetListener)fragment)
                        .spinnerTheme(R.style.NumberPickerStyle)
                        .defaultDate(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        .maxDate(3000, 0, 1)
                        .minDate(1900, 0, 1)
                        .build()
                        .show();

            }
        });
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isProfileViewMode){
                    isProfileViewMode = false;
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

                    mobileNumberEditLayout.setVisibility(View.VISIBLE);
                    mobileNumValueTxt.setVisibility(View.GONE);

                    profileTitle.setText(getResources().getString(R.string.editProfile));
                    editText.setText(getResources().getString(R.string.done_btn));

                    patientNameTxt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    patientSurName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    heightValTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    weightValTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    aadhaarNumValTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    districtValTxt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    stateValTxt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                    InputFilter[] fArray1 = new InputFilter[1];
                    fArray1[0] = new InputFilter.LengthFilter(12);
                    aadhaarNumValTxt.setFilters(fArray1);
                    aadhaarNumValTxt.setInputType(InputType.TYPE_CLASS_NUMBER);

                    String[] phoneArray = currentPatient.getPhoneNumber().split(" ");
                    String phoneCodeStr = phoneArray[0];
                    int phoneCode = Integer.parseInt(phoneCodeStr.substring(1));
                    editProfileCCP.setCountryForPhoneCode(phoneCode);
                    String phoneNum = "";
                    for (int i = 1; i < phoneArray.length ; i ++){
                        phoneNum = phoneNum + phoneArray[i];
                    }
                    editPhoneNumberEditView.setText(phoneNum);
                    aadhaarNumValTxt.setText(currentPatient.getAadhaar());
                    patientNameTxt.requestFocus();
                }else{
                    if (heightValTxt.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Height", Toast.LENGTH_SHORT).show();
                    }else if (weightValTxt.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Weight", Toast.LENGTH_SHORT).show();
                    }else if (birthdayValTxt.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Date of Birth", Toast.LENGTH_SHORT).show();
                    }else if (aadhaarNumValTxt.getText().length() > 0 && aadhaarNumValTxt.getText().length() < 12) {
                        Toast.makeText(getActivity(), "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                    }else if (editPhoneNumberEditView.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Mobile Number.", Toast.LENGTH_SHORT).show();
                    }else if (patientNameTxt.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Patient Name.", Toast.LENGTH_SHORT).show();
                    }else if (patientSurName.getText().length() == 0){
                        Toast.makeText(getActivity(), "Please enter Surname.", Toast.LENGTH_SHORT).show();
                    }else{
                        hud = KProgressHUD.create(getActivity())
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel("Saving information…");
                        hud.show();
                        updateProfile();
                    }
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

                float height = Float.parseFloat(heightValTxt.getText().toString());
                float weight = Float.parseFloat(weightValTxt.getText().toString());
                if (!sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT).equals( editProfileCCP.getSelectedCountryCode()))
                {
                    if (editProfileCCP.getSelectedCountryCode().equals("1")){
                        heightMesureLbl.setText("in");
                        weightMesureLbl.setText("lbs");
                        long heightIn = Math.round(height * 0.39);
                        double weightPounds = Math.round(weight * 2.2046);
                        heightValTxt.setText(String.format("%d", (int)heightIn));
                        weightValTxt.setText(String.format("%d", (int)weightPounds));
                    }else{
                        heightMesureLbl.setText("cms");
                        weightMesureLbl.setText("kgs");
                        heightValTxt.setText(String.format("%d", Math.round(currentPatient.getHeight())));
                        weightValTxt.setText(String.format("%d", Math.round(currentPatient.getWeight())));
                    }
                }

                sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, editProfileCCP.getSelectedCountryCode());
            }
        });
        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, editProfileCCP.getSelectedCountryCode());
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
                                fragmentManager.popBackStack(FragmentPopKeys.SEARCHREPORTFORPATIENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
                fragmentManager.popBackStack(FragmentPopKeys.SEARCHREPORTFORPATIENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    protected void onBackPressed(){
        if (isProfileViewMode){
            closeKeyboard();
            fragmentManager.popBackStack(FragmentPopKeys.SEARCHREPORTFORPATIENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else{
            onCheckingProfileEditStatus();
        }
    }

    private boolean isChangedProfile(){
        if (!currentPatient.getAadhaar().equals(aadhaarNumValTxt.getText().toString())){
            return true;
        }else if (!currentPatient.getFirstName().equals(patientNameTxt.getText().toString())){
            return true;
        }else if (!currentPatient.getLastName().equals(patientSurName.getText().toString())){
            return true;
        }else if (!currentPatient.getDistrict().equals(districtValTxt.getText().toString())){
            return true;
        }else if (!currentPatient.getState().equals(stateValTxt.getText().toString())){
            return true;
        }else if (!currentPatient.getBirthday().equals(updatedBrithdDay)){
            return true;
        }else{
            String prePhoneNum = (currentPatient.getPhoneNumber()).replaceAll("\\D+", "");
            String afterPhoneNum = (editProfileCCP.getSelectedCountryCodeWithPlus() + editPhoneNumberEditView.getText().toString()).replaceAll("\\D+", "");
            if (!prePhoneNum.equals(afterPhoneNum)){
                return true;
            }
            String currentCountryCode = sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT);
            float height = Float.parseFloat(heightValTxt.getText().toString());
            float weight = Float.parseFloat(weightValTxt.getText().toString());
            if (currentCountryCode.equals("1")){
                float heightIn = Float.parseFloat(heightValTxt.getText().toString());
                float weightPounds = Float.parseFloat(weightValTxt.getText().toString());
                height = Math.round(heightIn / 0.39);
                weight = Math.round(weightPounds / 2.2046);
            }
            if (currentPatient.getHeight() != height){
                return true;
            }else if (currentPatient.getWeight() != weight){
                return true;
            }
        }
        return false;
    }
    private void updateProfile(){
        currentPatient.setFirstName(patientNameTxt.getText().toString());
        currentPatient.setLastName(patientSurName.getText().toString());
        currentPatient.setAadhaar(aadhaarNumValTxt.getText().toString());
        currentPatient.setPhoneNumber(editProfileCCP.getSelectedCountryCodeWithPlus() + " " + editPhoneNumberEditView.getText().toString());
        currentPatient.setBirthday(updatedBrithdDay);
        currentPatient.setDistrict(districtValTxt.getText().toString());
        currentPatient.setState(stateValTxt.getText().toString());
        String currentCountryCode = sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT);
        if (currentCountryCode.equals("1")){
            float heightIn = Float.parseFloat(heightValTxt.getText().toString());
            float weightPounds = Float.parseFloat(weightValTxt.getText().toString());
            float height = (float) (heightIn / 0.39);
            float weight = (float) (weightPounds / 2.2046);
            currentPatient.setHeight(height);
            currentPatient.setWeight(weight);
        }else{
            currentPatient.setHeight(Float.parseFloat(heightValTxt.getText().toString()));
            currentPatient.setWeight(Float.parseFloat(weightValTxt.getText().toString()));
        }

        updateUserInfo();
    }
    private void updateUserInfo(){
        accountClient.updatePatient(getActivity(), sharedPreferencesManager, currentPatient, new APICallbacks() {
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

        UsersManagement.saveCurrentPatent(currentPatient, sharedPreferencesManager);

        isProfileViewMode = true;
        mobileNumberEditLayout.setVisibility(View.GONE);
        mobileNumValueTxt.setVisibility(View.VISIBLE);

        profileTitle.setText(getResources().getString(R.string.profile));
        editText.setText(getResources().getString(R.string.edit));

        patientNameTxt.setInputType(InputType.TYPE_NULL);
        patientSurName.setInputType(InputType.TYPE_NULL);
        heightValTxt.setInputType(InputType.TYPE_NULL);
        weightValTxt.setInputType(InputType.TYPE_NULL);
        aadhaarNumValTxt.setInputType(InputType.TYPE_NULL);
        districtValTxt.setInputType(InputType.TYPE_NULL);
        stateValTxt.setInputType(InputType.TYPE_NULL);
        aadhaarNumValTxt.setInputType(InputType.TYPE_NULL);

        initValue();

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

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        updatedBrithdDay = simpleDateFormat.format(calendar.getTime());
        birthdayValTxt.setText(DateUtils.string2StringDate(updatedBrithdDay, "yyyy-MM-dd" ,"dd MMM yyy") + " (" + DateUtils.getAge(year, monthOfYear, dayOfMonth) + " yrs)");
    }
}