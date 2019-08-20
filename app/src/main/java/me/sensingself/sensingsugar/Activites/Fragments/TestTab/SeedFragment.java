package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.w3c.dom.Text;

import me.sensingself.sensingsugar.Activites.Fragments.SearchTab.SearchedUsersFragment;
import me.sensingself.sensingsugar.Activites.LoginRegisterProfile.ProfileEditActivity;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
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
import java.util.ArrayList;

/**
 * Created by liujie on 1/12/18.
 */

public class SeedFragment extends Fragment {
    private TextView notePatient, identifyTitle, orTxtView;
    private Button searchBtn, addBtn;
    private EditText patientAadhaarEdit, phoneNumberEdit;
    private CountryCodePicker identyPreCCP;

    private SharedPreferencesManager sharedPreferencesManager;
    ArrayList<Patient> patients = new ArrayList<Patient>();
    private Patient currentPatient = new Patient();

    private KProgressHUD hud;
    private AccountClient accountClient;

    public static final String ARG_OBJECT = "object";//testing
    static boolean isSelectedPatient = false;
    FragmentManager fragmentManager;
    private int identifyMode = 0; // 1:aadhaar 2:phone
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab, container, false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();

        fragmentManager = FragmentmanagerUtils.getFragmentManagerIdentify();

        SoftKeyboardHandle.setupUI(rootView.findViewById(R.id.seedTabLayout), getActivity());
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
        }else{
            currentPatient = null;
        }
    }
    private void initController(View rootView){
        identifyTitle = (TextView) rootView.findViewById(R.id.identify_title_text);
        notePatient = (TextView) rootView.findViewById(R.id.notePatient);
        orTxtView = (TextView) rootView.findViewById(R.id.orTxtView);
        searchBtn = (Button) rootView.findViewById(R.id.searchBtn);
        addBtn = (Button) rootView.findViewById(R.id.addBtn);
        patientAadhaarEdit = (EditText) rootView.findViewById(R.id.patientAadhaarEdit);
        phoneNumberEdit = (EditText) rootView.findViewById(R.id.phoneNumberEdit);
        identyPreCCP = (CountryCodePicker) rootView.findViewById(R.id.identyPreCCP);

    }

    private void initFontAndText(View rootView){
        identifyTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        notePatient.setTypeface(FontUtility.getTimesNewRoman(rootView.getContext()));
        orTxtView.setTypeface(FontUtility.getTimesNewRomanItalic(rootView.getContext()));
        patientAadhaarEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        phoneNumberEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        searchBtn.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        addBtn.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        identyPreCCP.setTypeFace(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }

    private void setListener(final View rootView){
        identifyTitle.setText(getResources().getString(R.string.identify));

        patientAadhaarEdit.addTextChangedListener(new addListenerOnTextChange(getActivity(), patientAadhaarEdit));
        phoneNumberEdit.addTextChangedListener(new addListenerOnTextChange(getActivity(), phoneNumberEdit));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchkey = "";
                if (identifyMode == 1){
                    if (patientAadhaarEdit.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter Aadhaar number", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (patientAadhaarEdit.getText().length() < 12){
                        Toast.makeText(getActivity(), "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        searchkey = patientAadhaarEdit.getText().toString();
                    }
                }else{
                    if (phoneNumberEdit.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        searchkey = identyPreCCP.getSelectedCountryCodeWithPlus() + " " + phoneNumberEdit.getText().toString().replaceAll("\\D+", "");
                    }
                }
                if (!searchkey.equals("")){
                    hud = KProgressHUD.create(getActivity())
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Searching…");
                    hud.show();
                    apiCallSeachPatients(searchkey);
                }
            }
        });
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Patient patient = new Patient();
                if (identifyMode == 1){
                    if (patientAadhaarEdit.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter Aadhaar number", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (patientAadhaarEdit.getText().length() < 12){
                        Toast.makeText(getActivity(), "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        patient.setAadhaar(patientAadhaarEdit.getText().toString());
                    }
                }else{
                    if (phoneNumberEdit.getText().length() == 0) {
                        Toast.makeText(getActivity(), "Please enter Mobile Number", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        patient.setPhoneNumber(identyPreCCP.getSelectedCountryCodeWithPlus() + " " + phoneNumberEdit.getText().toString());
                    }
                }
                UsersManagement.saveCurrentPatent(patient, sharedPreferencesManager);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.seed_root_frame, SeedAddPatientInfoFragment.newInstance("", "", identifyMode), "Post")
                        .addToBackStack(FragmentPopKeys.SEED)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
                identifyMode = 0;
            }
        });
        identyPreCCP.registerCarrierNumberEditText(phoneNumberEdit);
        identyPreCCP.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    phoneNumberEdit.setTextColor(getResources().getColor(R.color.editTextColor));
                    int maxLength = phoneNumberEdit.getText().length();
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    phoneNumberEdit.setFilters(fArray);
                }else{
                    phoneNumberEdit.setTextColor(getResources().getColor(R.color.colorMobileNumberInvalid));
                }

            }
        });
        identyPreCCP.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                int maxLength = 20;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                phoneNumberEdit.setFilters(fArray);
                phoneNumberEdit.setText("");
                sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, identyPreCCP.getSelectedCountryCode());
            }
        });
        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, identyPreCCP.getSelectedCountryCode());
    }
    private  void apiCallSeachPatients(String searchKey){
        accountClient.searchPatients(getActivity(), sharedPreferencesManager, searchKey, identifyMode, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                ArrayList<Patient> searchedpatients = new ArrayList<Patient>();
                searchedpatients = (ArrayList<Patient>) responseObject;
                if (searchedpatients.size() == 0){
                    onSearchCompleted(statusCode, errorMessage, false);
                }else{
                    UsersManagement.savePatients(searchedpatients, sharedPreferencesManager, SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEED);
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
                    .replace(R.id.seed_root_frame, SeedSearchedUsersFragment.newInstance("", ""), "Post")
                    .addToBackStack(FragmentPopKeys.SEED)
                    .commit();
            identifyMode = 0;
            FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
        }else{
            if (identifyMode == 1){
                PrintUtil.showToast(getActivity(), "Can't find Aadhaar Number:" + patientAadhaarEdit.getText().toString() + ". Please try with other.");
            }else{
                PrintUtil.showToast(getActivity(), "Can't find Mobile:" + identyPreCCP.getSelectedCountryCodeWithPlus() + " " + phoneNumberEdit.getText().toString() + ". Please try with other.");
            }
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
    public class addListenerOnTextChange implements TextWatcher {
        private Context mContext;
        EditText currentEditText;

        public addListenerOnTextChange(Context context, EditText currentEditText) {
            super();
            this.mContext = context;
            this.currentEditText = currentEditText;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //What you want to do
            if (s.length() == 0 && start == 0 && before == 0 && count == 0) return;
            if (patientAadhaarEdit.getText().length() == 0 && phoneNumberEdit.getText().length() == 0){
                changeIdentifyMode(3);
                return;
            }else if (currentEditText.equals(patientAadhaarEdit) && patientAadhaarEdit.getText().length() > 0 && identifyMode!= 1){
                    changeIdentifyMode(1);
            }else if (currentEditText.equals(phoneNumberEdit) &&phoneNumberEdit.getText().length() > 0 && identifyMode!= 2){
                    changeIdentifyMode(2);
            }
        }
    }
    private void changeIdentifyMode(int type){
        if (type == 1){ // by Aadhaar
            identifyMode = 1;
            patientAadhaarEdit.setEnabled(true);
            phoneNumberEdit.setEnabled(false);
            identyPreCCP.setContentColor(Color.GRAY);
            identyPreCCP.setCcpClickable(false);
        }else if(type == 2){ // by mobile
            identifyMode = 2;
            patientAadhaarEdit.setEnabled(false);
            phoneNumberEdit.setEnabled(true);
            identyPreCCP.setContentColor(getResources().getColor(R.color.editTextColor));
            identyPreCCP.setCcpClickable(true);
        }else if(type == 3){ // None
            identifyMode = 3;
            patientAadhaarEdit.setEnabled(true);
            phoneNumberEdit.setEnabled(true);
            identyPreCCP.setContentColor(getResources().getColor(R.color.editTextColor));
            identyPreCCP.setCcpClickable(true);
        }
    }
}
