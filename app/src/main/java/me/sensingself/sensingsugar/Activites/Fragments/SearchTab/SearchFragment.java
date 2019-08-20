package me.sensingself.sensingsugar.Activites.Fragments.SearchTab;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Lib.DatePickerDialogPlus;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Lib.SoftKeyboardHandle;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by liujie on 1/12/18.
 */

public class SearchFragment extends Fragment{
    private TextView searchTitle, selectDate, searchByTxt,fromDateTxt, toDateTxt, fullNameBtn, aadhaarNumBtn;
    private Button searchBtn;
    private EditText patientNameEdit, surNameEdit, aadhaarEdit;
    private ConstraintLayout fullNameSearchByConstraint, aadhaarSearchByConstraint;
    private LinearLayout searchBySelectionLayout;

    private SharedPreferencesManager sharedPreferencesManager;
    ArrayList<Patient> patients = new ArrayList<Patient>();

    private ImageView fromCalendarImage, toCalendarImage;

    private KProgressHUD hud;
    private AccountClient accountClient;

    public static final String ARG_OBJECT = "object";//testing

    private int searchType = 1; // 1 : full name   2 : Aadhaar number
    private int calendarType = 1; // 1 :from   2:to
    private int mYear, mMonth, mDay;

    int searchMode = 3;
    FragmentManager fragmentManager;
    SimpleDateFormat simpleDateFormat;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_tab, container, false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();

        fragmentManager = FragmentmanagerUtils.getFragmentManagerSearch();
        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        SoftKeyboardHandle.setupUI(rootView.findViewById(R.id.searchTabLayout), getActivity());
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchType == 1){
            fullNameSearchByConstraint.setVisibility(View.VISIBLE);
            aadhaarSearchByConstraint.setVisibility(View.GONE);
        }else{
            fullNameSearchByConstraint.setVisibility(View.GONE);
            aadhaarSearchByConstraint.setVisibility(View.VISIBLE);
        }
    }

    private void initController(View rootView) {
        searchTitle = (TextView) rootView.findViewById(R.id.search_title_text);
        selectDate = (TextView) rootView.findViewById(R.id.selectDateTxtView);
        fromDateTxt = (TextView) rootView.findViewById(R.id.fromDataTxtView);
        fromCalendarImage = (ImageView)rootView.findViewById(R.id.fromCalendarImage);
        toDateTxt = (TextView) rootView.findViewById(R.id.toDataTxtView);
        toCalendarImage = (ImageView)rootView.findViewById(R.id.toCalendarImage);
        searchByTxt = (TextView) rootView.findViewById(R.id.searchByTxtView);
        fullNameBtn = (TextView) rootView.findViewById(R.id.fullnameSearchByBtn);
        aadhaarNumBtn = (TextView) rootView.findViewById(R.id.aadhaarSearchByBtn);
        patientNameEdit = (EditText) rootView.findViewById(R.id.patientNameSearchKeyEditTxt);
        surNameEdit = (EditText) rootView.findViewById(R.id.surnameSearchKeyEditTxt);
        aadhaarEdit = (EditText) rootView.findViewById(R.id.aadhaarSearchKeyEditTxt);
        searchBtn = (Button) rootView.findViewById(R.id.searchBtn);
        fullNameSearchByConstraint = (ConstraintLayout) rootView.findViewById(R.id.fullNameSearchByConstraint);
        aadhaarSearchByConstraint = (ConstraintLayout) rootView.findViewById(R.id.aadhaarSearchByConstraint);
        aadhaarSearchByConstraint.setVisibility(View.INVISIBLE);
        searchBySelectionLayout = (LinearLayout) rootView.findViewById(R.id.searchBySelectionLayout);

        fromCalendarImage.setImageResource(R.drawable.calendar);
        toCalendarImage.setImageResource(R.drawable.calendar);
    }

    private void initFontAndText(View rootView) {
        searchTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        selectDate.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        fromDateTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        toDateTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        searchByTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        fullNameBtn.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        aadhaarNumBtn.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientNameEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        surNameEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        aadhaarEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        searchBtn.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
    }

    private void setListener(final View rootView) {
        searchTitle.setText(getResources().getString(R.string.search_btn));
        fullNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 1)return;
                searchType = 1;
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light_right);
                fullNameSearchByConstraint.setVisibility(View.VISIBLE);
                aadhaarSearchByConstraint.setVisibility(View.GONE);
            }
        });
        aadhaarNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 1)return;
                searchType = 2;
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark_right);
                fullNameSearchByConstraint.setVisibility(View.GONE);
                aadhaarSearchByConstraint.setVisibility(View.VISIBLE);
            }
        });
        surNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    onCheckingConditionsToSearch();
                }
                return true;
            }
        });
        aadhaarEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    onCheckingConditionsToSearch();
                }
                return true;
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckingConditionsToSearch();

            }
        });
        final Fragment fragment = this;
        fromCalendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 2)return;
                calendarType = 1;
                onShownCalendar();
            }
        });

        toCalendarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 2)return;
                calendarType = 2;
                onShownCalendar();
            }
        });
        fromDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 2)return;
                calendarType = 1;
                onShownCalendar();
            }
        });

        toDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchMode == 2)return;
                calendarType = 2;
                onShownCalendar();
            }
        });

        patientNameEdit.addTextChangedListener(new addListenerOnTextChange(getActivity(), patientNameEdit));
        surNameEdit.addTextChangedListener(new addListenerOnTextChange(getActivity(), surNameEdit));
        aadhaarEdit.addTextChangedListener(new addListenerOnTextChange(getActivity(), aadhaarEdit));
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
            if (searchType == 1){
                if (patientNameEdit.getText().length() == 0 && surNameEdit.getText().length() == 0 && before == 1){
                    if (searchMode == 3) return;
                    changeSearchMode(3);
                }else{
                    if (searchMode == 2) return;
                    changeSearchMode(2);
                }
            }else{
                if (aadhaarEdit.getText().length() == 0 && before == 1){
                    if (searchMode == 3) return;
                    changeSearchMode(3);
                }else{
                    if (searchMode == 2) return;
                    changeSearchMode(2);
                }
            }
        }
    }
    private void onCheckingConditionsToSearch(){
        String searchKey = "";
        if (searchMode == 1){
            if (fromDateTxt.getText().length() > 0 && toDateTxt.getText().length() > 0){
                sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHFROMDATE, DateUtils.string2StringDate(fromDateTxt.getText().toString(), "dd MMM yyyy", "yyyy-MM-dd"));
                sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHTODATE, DateUtils.string2StringDate(toDateTxt.getText().toString(), "dd MMM yyyy", "yyyy-MM-dd"));
                sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHKEY, "");
            }else{
                Toast.makeText(getActivity(), "Please enter Date range.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            if (searchType == 1){
                if (patientNameEdit.getText().length() == 0 || surNameEdit.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter Patient name or surname", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    searchKey = patientNameEdit.getText().toString() + " " + surNameEdit.getText().toString();
                }
            }else if (searchType == 2){
                if (aadhaarEdit.getText().length() == 0) {
                    Toast.makeText(getActivity(), "Please enter Aadhaar", Toast.LENGTH_SHORT).show();
                    return;
                }else if (aadhaarEdit.getText().length() != 12){
                    Toast.makeText(getActivity(), "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    searchKey = aadhaarEdit.getText().toString();
                }
            }
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHFROMDATE, "");
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHTODATE, "");
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHKEY, searchKey);
        }
        sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.SEARCHTYPE, searchType);
        sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.SEARCHMODE, searchMode);

        

        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Searching…");
        hud.show();
        apiCallPatients( searchKey);
    }
    private void onShownCalendar(){
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialogPlus datePickerDialog = new DatePickerDialogPlus(getActivity(), R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        if (year == 0 && monthOfYear == 0 && dayOfMonth == 0){
                            if (calendarType == 1){
                                fromDateTxt.setText("");
                            }else{
                                toDateTxt.setText("");
                            }
                        }else{
                            Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                            String periodDate = simpleDateFormat.format(calendar.getTime());
                            if (calendarType == 1){
                                fromDateTxt.setText(periodDate);
                            }else{
                                toDateTxt.setText(periodDate);
                            }
                        }

                        if (fromDateTxt.getText().length() > 0 || toDateTxt.getText().length() > 0){
                            changeSearchMode(1);
                        }else if (fromDateTxt.getText().length() == 0 && toDateTxt.getText().length() == 0){
                            changeSearchMode(3);
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void apiCallPatients(String searchKey) {
        String fromDateStr = "";
        String toDateStr = "";
        if (searchMode == 1){
            fromDateStr = DateUtils.string2StringDate(fromDateTxt.getText().toString(), "dd MMM yyyy", "yyyy-MM-dd");
            toDateStr = DateUtils.string2StringDate(toDateTxt.getText().toString(), "dd MMM yyyy", "yyyy-MM-dd");
        }

        accountClient.searchPatientsForTestingResult(getActivity(), sharedPreferencesManager, searchKey, searchType,
                fromDateStr, toDateStr, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                ArrayList<Patient> searchedpatients = new ArrayList<Patient>();
                searchedpatients = (ArrayList<Patient>) responseObject;
                if (searchedpatients.size() == 0) {
                    onSearchCompleted(statusCode, errorMessage, searchedpatients, false);
                } else {
                    UsersManagement.savePatients(searchedpatients, sharedPreferencesManager, SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEARCH);
                    onSearchCompleted(statusCode, errorMessage, searchedpatients, true);
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

    @SuppressLint("StaticFieldLeak")
    public void onSearchCompleted(int statusCode, String errorMessage, ArrayList<Patient> searchedpatients, boolean isReturn) {
        if (isReturn) {
            if (searchedpatients.size() > 0){
                if (hud!= null)hud.dismiss();
                if (searchMode == 1){
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.search_root_frame, SearchResultsFragment.newInstance("", ""), "Post")
                            .addToBackStack(FragmentPopKeys.SEARCH)
                            .commit();
                }else{
                    if (searchType == 1){
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.search_root_frame, SearchedUsersFragment.newInstance(patientNameEdit.getText().toString(), surNameEdit.getText().toString()), "Post")
                                .addToBackStack(FragmentPopKeys.SEARCH)
                                .commit();
                    }else{
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.search_root_frame, SearchedUsersFragment.newInstance("", ""), "Post")
                                .addToBackStack(FragmentPopKeys.SEARCH)
                                .commit();
                    }
                }
                FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);
                searchMode = 3;
            }
        } else {
            if (hud!= null)hud.dismiss();
            if (searchMode == 1){
                PrintUtil.showToast(getActivity(), "Can't Find. Please try with other dates.");
            }else{
                if (searchType == 1){
                    PrintUtil.showToast(getActivity(), "Can't find " + patientNameEdit.getText().toString() + " " + surNameEdit.getText().toString() + ". Please try with other name.");
                }else{
                    PrintUtil.showToast(getActivity(), "Can't find Aadhaar : " + aadhaarEdit.getText().toString() + ". Please try with other Number.");
                }
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
                if (searchMode == 1){
                    PrintUtil.showToast(getActivity(), "Can't Find. Please try with other dates.");
                }else{
                    if (searchType == 1){
                        PrintUtil.showToast(getActivity(), "Can't find " + patientNameEdit.getText().toString() + " " + surNameEdit.getText().toString() + ". Please try with other name.");
                    }else{
                        PrintUtil.showToast(getActivity(), "Can't find Aadhaar : " + aadhaarEdit.getText().toString() + ". Please try with other Number.");
                    }
                }
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

    private void changeSearchMode(int type){
        if (type == 1){ // Search by Date
            searchMode = 1;
            selectDate.setTextColor(getResources().getColor(R.color.colorDarkBrown));

            patientNameEdit.setInputType(InputType.TYPE_NULL);
            surNameEdit.setInputType(InputType.TYPE_NULL);
            aadhaarEdit.setInputType(InputType.TYPE_NULL);
            patientNameEdit.setCursorVisible(false);
            surNameEdit.setCursorVisible(false);
            aadhaarEdit.setCursorVisible(false);

            searchByTxt.setTextColor(Color.GRAY);
            if (searchType == 1){
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark_gray);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light_gray_right);
            }else{
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light_gray);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark_gray_right);
            }

            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHKEY, "");

        }else if(type == 2){ // Search by Full name or Aadhaar
            searchMode = 2;
            selectDate.setTextColor(Color.GRAY);

            patientNameEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            surNameEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            aadhaarEdit.setInputType(InputType.TYPE_CLASS_NUMBER);

            patientNameEdit.setCursorVisible(true);
            surNameEdit.setCursorVisible(true);
            aadhaarEdit.setCursorVisible(true);
            searchByTxt.setTextColor(getResources().getColor(R.color.colorDarkBrown));
            if (searchType == 1){
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light_right);
            }else{
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark_right);
            }
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHFROMDATE, "");
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHTODATE, "");
        }else if(type == 3){ // None
            searchMode = 3;
            selectDate.setTextColor(getResources().getColor(R.color.colorDarkBrown));
            searchByTxt.setTextColor(getResources().getColor(R.color.colorDarkBrown));
            patientNameEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            surNameEdit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            aadhaarEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
            patientNameEdit.setCursorVisible(true);
            surNameEdit.setCursorVisible(true);
            aadhaarEdit.setCursorVisible(true);
            if (searchType == 1){
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light_right);
            }else{
                fullNameBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_light);
                aadhaarNumBtn.setBackgroundResource(R.drawable.radius_parallelogram_gradiant_dark_right);
            }
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHKEY, "");
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHFROMDATE, "");
            sharedPreferencesManager.setPrefernceValueString(SharedPreferencesKeys.SEARCHTODATE, "");
        }
    }

    private void apiCallSeachTestingResult(final Patient patient) {
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
                            UsersManagement.saveSearchedTestingResult(searchedTestingResults, sharedPreferencesManager, patient.getPatientId(),  SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEARCH);
                            onSearchTestingResultCompleted(statusCode, errorMessage, true);
                    }

                    @Override
                    public <E> void onSuccessJsonArray(E responseObject, int statusCode, String errorMessage) {
                    }

                    @Override
                    public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                        onSearchTestingResultFailed(errorCode, errorMessage);
                    }
                });
    }
    public void onSearchTestingResultCompleted(int statusCode, String errorMessage, boolean isReturn) {
        if (hud!= null)hud.dismiss();
        if (isReturn) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.search_root_frame, SearchReportsForOnePatient.newInstance("", ""), "Post")
                    .addToBackStack(FragmentPopKeys.SEARCHRESULT)
                    .commit();
            FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);
        } else {
            PrintUtil.showToast(getActivity(), "No Testing results");
        }
    }
    public void onSearchTestingResultFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(getActivity(), errorMessage);
                break;
            case 404:
                if (searchType == 1){
                    PrintUtil.showToast(getActivity(), "Can't find " + patientNameEdit.getText().toString() + " " + surNameEdit.getText().toString() + ". Please try with other name.");
                }else{
                    PrintUtil.showToast(getActivity(), "Can't find Aadhaar : " + aadhaarEdit.getText().toString() + ". Please try with other Number.");
                }
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
}