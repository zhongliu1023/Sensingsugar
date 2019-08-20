package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.jiangyy.easydialog.SingleChoiceDialog;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.otto.Subscribe;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultBus;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultEvent;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.ActivityCodes;
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
 * Created by liujie on 1/24/18.
 */

public class SeedAddPatientInfoFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private TextView noteAddPatient, identifyTitle, patientGenderTxt, patientBirthTxt, unitHeight, unitWeight;
    private Button nextPatientAddBtn;
    private EditText patientHeightTxt, patientWeightTxt , patientAadhaarTxt, patientMobileTxt;
    private ImageView patientAddbackImageView;
    private CountryCodePicker patientMobileCCP;
    private TextView patientNameTxt, patientSurNameTxt;

    SimpleDateFormat simpleDateFormat;

    private SharedPreferencesManager sharedPreferencesManager;
    private Patient currentPatient = new Patient();

    private AccountClient accountClient;
    private KProgressHUD hud;

    static int typeToSave = 1; // 1 : create   2 : update

    FragmentManager fragmentManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab_add_patient_info, container, false);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();

        SoftKeyboardHandle.setupUI(rootView.findViewById(R.id.seedParentAddPatientInfo), getActivity());
        fragmentManager = FragmentmanagerUtils.getFragmentManagerIdentify();

        initNewPatient();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);
        return rootView;
    }
    public static SeedAddPatientInfoFragment newInstance(String param1, String param2, int type) {
        typeToSave = type;
        SeedAddPatientInfoFragment fragment = new SeedAddPatientInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentPatient != null){
            patientGenderTxt.setText(currentPatient.getGender());
            patientBirthTxt.setText(currentPatient.getBirthday());
        }

        patientNameTxt.requestFocus();
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
    private void initController(View rootView){
        patientMobileCCP = (CountryCodePicker)rootView.findViewById(R.id.patientMobileCCP);
        identifyTitle = (TextView) rootView.findViewById(R.id.patient_add_title_text);
        noteAddPatient = (TextView) rootView.findViewById(R.id.noteAddPatient);
        nextPatientAddBtn = (Button) rootView.findViewById(R.id.nextPatientAddBtn);
        unitHeight = (TextView) rootView.findViewById(R.id.unitHeight);
        patientHeightTxt = (EditText) rootView.findViewById(R.id.patientHeightTxt);
        patientHeightTxt.requestFocus();
        unitWeight = (TextView) rootView.findViewById(R.id.unitWeight);
        patientWeightTxt = (EditText) rootView.findViewById(R.id.patientWeightTxt);
        patientGenderTxt = (TextView) rootView.findViewById(R.id.patientGenderTxt);
        patientBirthTxt = (TextView) rootView.findViewById(R.id.patientBirthTxt);
        patientAadhaarTxt = (EditText) rootView.findViewById(R.id.patientAadhaarTxt);
        patientMobileTxt = (EditText) rootView.findViewById(R.id.patientMobileTxt);
        patientAddbackImageView = (ImageView)rootView.findViewById(R.id.patientAddbackImageView);
        patientAddbackImageView.setImageResource(R.drawable.back_img);
        patientNameTxt  = (TextView) rootView.findViewById(R.id.patientNameTxt);
        patientSurNameTxt = (TextView) rootView.findViewById(R.id.patientSurNameTxt);


        String currentCountryCode = sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT);
        if (currentCountryCode.equals("1")){
            unitHeight.setHint("in");
            unitWeight.setHint("lbs");
        }else{
            unitHeight.setHint("cms");
            unitWeight.setHint("kgs");
        }

        if (typeToSave == 1){//aadhaar
            patientAadhaarTxt.setVisibility(View.GONE);
            patientMobileCCP.setVisibility(View.VISIBLE);
            patientMobileTxt.setVisibility(View.VISIBLE);
        }else{//phone
            patientAadhaarTxt.setVisibility(View.VISIBLE);
            patientMobileCCP.setVisibility(View.GONE);
            patientMobileTxt.setVisibility(View.GONE);
        }

        patientAadhaarTxt.setHint(Html.fromHtml("Aadhaar <i><small>(optional)</small></i>"));
    }

    private void initFontAndText(View rootView){
        unitHeight.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        unitWeight.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientMobileCCP.setTypeFace(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        identifyTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        noteAddPatient.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientHeightTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientWeightTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientGenderTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientBirthTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientAadhaarTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientMobileTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        nextPatientAddBtn.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        patientNameTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientSurNameTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }
    private void setListener(final View rootView){
        identifyTitle.setText(getResources().getString(R.string.add_patient_info));
        patientMobileCCP.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                int maxLength = 20;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                patientMobileTxt.setFilters(fArray);
                patientMobileTxt.setText("");
                sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, patientMobileCCP.getSelectedCountryCode());
                if (patientMobileCCP.getSelectedCountryCode().equals("1")){
                    unitHeight.setHint("in");
                    unitWeight.setHint("lbs");
                }else{
                    unitHeight.setHint("cms");
                    unitWeight.setHint("kgs");
                }
            }
        });
        patientMobileCCP.registerCarrierNumberEditText(patientMobileTxt);
        patientMobileCCP.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    patientMobileTxt.setTextColor(getResources().getColor(R.color.editTextColor));
                    int maxLength = patientMobileTxt.getText().length();
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    patientMobileTxt.setFilters(fArray);
                }else{
                    patientMobileTxt.setTextColor(getResources().getColor(R.color.colorMobileNumberInvalid));
                }
            }
        });
        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, patientMobileCCP.getSelectedCountryCode());
        String currentCountryCode = sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT);
        if (currentCountryCode.equals("1")){
            unitHeight.setHint("in");
            unitWeight.setHint("lbs");
        }else{
            unitHeight.setHint("cms");
            unitWeight.setHint("kgs");
        }
        patientMobileTxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    createPatientAfterChecking();
                }
                return true;
            }
        });
        nextPatientAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPatientAfterChecking();
            }
        });
        patientAddbackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        patientGenderTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleChoiceDialog.Builder(getActivity()).setTitle("Change your gender.")
                        .addList(new String[]{"Male", "Female"})
                        .setOnItemClickListener(new SingleChoiceDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(String title, int position) {
                                if (position == 0){
                                    patientGenderTxt.setText("Male");
                                }else{
                                    patientGenderTxt.setText("Female");
                                }
                            }
                        }).setCancelTextColor(getResources().getColor(R.color.colorPrimaryButton)).show();
            }
        });
        final Fragment fragment = this;
        patientBirthTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }

    private void createPatientAfterChecking(){
        if (patientNameTxt.getText().length() == 0){
            Toast.makeText(getActivity(), "Please enter Patient Name", Toast.LENGTH_SHORT).show();
        }else if (patientSurNameTxt.getText().length() == 0){
            Toast.makeText(getActivity(), "Please enter Surename", Toast.LENGTH_SHORT).show();
        }else if (patientHeightTxt.getText().length() == 0){
            Toast.makeText(getActivity(), "Please enter Height", Toast.LENGTH_SHORT).show();
        }else if (patientWeightTxt.getText().length() == 0){
            Toast.makeText(getActivity(), "Please enter Weight", Toast.LENGTH_SHORT).show();
        }else if (patientGenderTxt.getText().length() == 0){
            Toast.makeText(getActivity(), "Please enter Gender", Toast.LENGTH_SHORT).show();
        }else if (patientBirthTxt.getText().length() == 0){
            Toast.makeText(getActivity(), "Please enter Date of Birth", Toast.LENGTH_SHORT).show();
        }else{
            if (typeToSave == 1){
                if (patientMobileTxt.getText().length() == 0){
                    Toast.makeText(getActivity(), "Please enter Mobile Number.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else{
                if (patientAadhaarTxt.getText().length() > 0 && patientAadhaarTxt.getText().length() < 12){
                    Toast.makeText(getActivity(), "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            float height = Float.parseFloat(patientHeightTxt.getText().toString());
            float weight = Float.parseFloat(patientWeightTxt.getText().toString());
            String currentCountryCode = sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT);
            if (currentCountryCode.equals("1")){
                height = Float.parseFloat(String.format("%.2f", height / 0.39));
                weight = Float.parseFloat(String.format("%.2f", weight / 2.2046));
            }
            currentPatient.setFirstName(patientNameTxt.getText().toString());
            currentPatient.setLastName(patientSurNameTxt.getText().toString());
            currentPatient.setHeight(height);
            currentPatient.setWeight(weight);
            currentPatient.setGender(patientGenderTxt.getText().toString());
            currentPatient.setBirthday(patientBirthTxt.getText().toString());
            if (typeToSave == 1){
                currentPatient.setPhoneNumber(patientMobileCCP.getSelectedCountryCodeWithPlus() + " " + patientMobileTxt.getText().toString());

            }else{
                currentPatient.setAadhaar(patientAadhaarTxt.getText().toString());
            }

            hud = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Adding Patient…");
            hud.show();
            createPatient();
        }
    }
    private void createPatient(){
        accountClient.createPatient(getActivity(), sharedPreferencesManager, currentPatient, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                currentPatient.setFirstName(((Patient) responseObject).getFirstName());
                currentPatient.setLastName(((Patient) responseObject).getLastName());
                currentPatient.setAadhaar(((Patient) responseObject).getAadhaar());
                currentPatient.setHeight(((Patient) responseObject).getHeight());
                currentPatient.setWeight(((Patient) responseObject).getWeight());
                currentPatient.setPhoneNumber(((Patient) responseObject).getPhoneNumber());
                currentPatient.setPatientId(((Patient) responseObject).getPatientId());

                UsersManagement.saveCurrentPatent(currentPatient, sharedPreferencesManager);
                UsersManagement.addOrUpdateNewPatient(currentPatient, sharedPreferencesManager, SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEED );
                onSavePaitentCompleted();
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }

            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onSavePatientFailed(errorCode, errorMessage);
            }
        });
    }
    public void onSavePaitentCompleted() {
        if (hud!= null)hud.dismiss();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.seed_root_frame, SeedBeginTestFragment.newInstance(FragmentPopKeys.SEEDADDPATIENTINFO, "", false), "Post")
                .addToBackStack(FragmentPopKeys.SEEDADDPATIENTINFO)
                .commit();
        FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
    }
    public void onSavePatientFailed(int errorCode, String errorMessage) {
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
                    PrintUtil.showToast(getActivity(), getResources().getString(R.string
                        .something_went_wrong));
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

    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        String birthday = simpleDateFormat.format(calendar.getTime());
        patientBirthTxt.setText(birthday);
    }

    protected void onBackPressed(){
        fragmentManager.popBackStack(FragmentPopKeys.SEED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}