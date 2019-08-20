package me.sensingself.sensingsugar.Activites.LoginRegisterProfile;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import me.sensingself.sensingsugar.Activites.HomeActivity;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Lib.SoftKeyboardHandle;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.lang.reflect.Type;

/**
 * Created by liujie on 1/6/18.
 */

public class VerifyMobileNumActivity extends AppCompatActivity{

    private EditText firstDigitTextEdit;
    private EditText secondDigitTextEdit;
    private EditText thirdDigitTextEdit;
    private EditText fourthDigitTextEdit;
    private EditText fifthDigitTextEdit;
    private EditText sixthDigitTextEdit;

    private Button nextBtn;
    private TextView verifyActivityNote;
    private TextView mobileNumTitle;


    private AccountClient accountClient;
    private KProgressHUD hud;
    private SharedPreferencesManager sharedPreferencesManager;

    private String verificationCode = "";
    private UserVo currentUserVo = new UserVo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile_num);

        SoftKeyboardHandle.setupUI(findViewById(R.id.parentVerifyView), this);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);


        initActionBar();
        initNewUserVo();
        init();
        initFontAndText();
        setListener();
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
    private void initActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_default);
        getSupportActionBar().setElevation(0);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        titleText.setText(R.string.nav_verify_mobile_num);
        ImageView backImage = (ImageView) findViewById(R.id.backImageView);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void init() {
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        accountClient = new AccountClient();

        firstDigitTextEdit = (EditText) findViewById(R.id.firstDigitCode);
        secondDigitTextEdit = (EditText) findViewById(R.id.secondDigitCode);
        thirdDigitTextEdit = (EditText) findViewById(R.id.thirdDigitCode);
        fourthDigitTextEdit = (EditText) findViewById(R.id.fourthDigitCode);
        fifthDigitTextEdit = (EditText) findViewById(R.id.fifthDigitCode);
        sixthDigitTextEdit = (EditText) findViewById(R.id.sixthDigitCode);
        nextBtn = (Button) findViewById(R.id.verifyNextDoneBtn);
        mobileNumTitle = (TextView) findViewById(R.id.mobileNumTitle);
        verifyActivityNote = (TextView) findViewById(R.id.verifyViewNote);
    }

    private void initFontAndText() {
        String verifyResendNote = getResources().getString(R.string.if_incase_you_do_not_recive_code);
        verifyActivityNote.setText(Html.fromHtml(verifyResendNote));

        mobileNumTitle.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        firstDigitTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        secondDigitTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        thirdDigitTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        fourthDigitTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        fifthDigitTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        sixthDigitTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        nextBtn.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        verifyActivityNote.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
    }

    private void setListener() {

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode();
            }
        });

        verifyActivityNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int typeToMove = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER);
                switch (typeToMove){
                    case SharedPreferencesKeys.FROM_LOGIN:
                        apiCallToRecieveCodeAgainFromLogin();
                        break;
                    case SharedPreferencesKeys.FROM_REGISTER:
                        apiCallToRecieveCodeAgain();
                        break;
                    default:
                        apiCallToRecieveCodeAgain();
                        break;
                }
            }
        });

        firstDigitTextEdit.addTextChangedListener(new addListenerOnTextChange(this, firstDigitTextEdit, secondDigitTextEdit));
        firstDigitTextEdit.setOnKeyListener(new onkeyListner(this, firstDigitTextEdit, null));
        firstDigitTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR))) {

                } else {
                    secondDigitTextEdit.requestFocus();
                }
                return true;
            }
        });


        secondDigitTextEdit.addTextChangedListener(new addListenerOnTextChange(this, secondDigitTextEdit, thirdDigitTextEdit));
        secondDigitTextEdit.setOnKeyListener(new onkeyListner(this, secondDigitTextEdit, firstDigitTextEdit));
        secondDigitTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR))) {
                } else {
                    thirdDigitTextEdit.requestFocus();
                }
                return true;
            }
        });

        thirdDigitTextEdit.addTextChangedListener(new addListenerOnTextChange(this, thirdDigitTextEdit, fourthDigitTextEdit));
        thirdDigitTextEdit.setOnKeyListener(new onkeyListner(this, thirdDigitTextEdit, secondDigitTextEdit));
        thirdDigitTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR))) {

                } else {
                    fourthDigitTextEdit.requestFocus();
                }
                return true;
            }
        });

        fourthDigitTextEdit.addTextChangedListener(new addListenerOnTextChange(this, fourthDigitTextEdit, fifthDigitTextEdit));
        fourthDigitTextEdit.setOnKeyListener(new onkeyListner(this, fourthDigitTextEdit, thirdDigitTextEdit));
        fourthDigitTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR))) {

                } else {
                    fifthDigitTextEdit.requestFocus();
                }
                return true;
            }
        });
        fifthDigitTextEdit.addTextChangedListener(new addListenerOnTextChange(this, fifthDigitTextEdit, sixthDigitTextEdit));
        fifthDigitTextEdit.setOnKeyListener(new onkeyListner(this, fifthDigitTextEdit, fourthDigitTextEdit));
        fifthDigitTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR))) {

                } else {
                    sixthDigitTextEdit.requestFocus();
                }
                return true;
            }
        });

        sixthDigitTextEdit.addTextChangedListener(new addListenerOnTextChange(this, sixthDigitTextEdit, null));
        sixthDigitTextEdit.setOnKeyListener(new onkeyListner(this, sixthDigitTextEdit, fifthDigitTextEdit));
        sixthDigitTextEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_CLEAR))) {

                } else if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    verifyCode();
                }
                return true;
            }
        });
    }

    private void verifyCode() {
        verificationCode = firstDigitTextEdit.getText().toString() + "" + secondDigitTextEdit.getText().toString() + "" + thirdDigitTextEdit.getText().toString() + "" +
                fourthDigitTextEdit.getText().toString() + "" + fifthDigitTextEdit.getText().toString() + "" + sixthDigitTextEdit.getText().toString();
        hud = KProgressHUD.create(VerifyMobileNumActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Verifying…");
        hud.show();

        String currentEnetedPhoneNumer = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPHONENUMBER);
        accountClient.verifyCode(VerifyMobileNumActivity.this, sharedPreferencesManager, currentEnetedPhoneNumer, verificationCode, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
//                PrintUtil.showToast(VerifyMobileNumActivity.this, "The StatusCode is " + statusCode);

                int typeToMove = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER);
                Intent mainIntent;
                Bundle bundle = new Bundle();
                switch (typeToMove){
                    case SharedPreferencesKeys.FROM_LOGIN:
                        getVerificationCompleted(sharedPreferencesManager);
                        break;
                    case SharedPreferencesKeys.FROM_REGISTER:
                        if (hud!= null)hud.dismiss();
                        mainIntent = new Intent(VerifyMobileNumActivity.this, ProfileEditActivity.class);
                        bundle.putBoolean("redrectProfileView", false);
                        mainIntent.putExtras(bundle);
                        VerifyMobileNumActivity.this.startActivity(mainIntent);
                        finish();
                        break;
                    case SharedPreferencesKeys.FROM_PATIENT_PROFILE:
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        break;
                    default:
                        if (hud!= null)hud.dismiss();
                        mainIntent = new Intent(VerifyMobileNumActivity.this, ProfileEditActivity.class);
                        bundle.putBoolean("redrectProfileView", false);
                        mainIntent.putExtras(bundle);
                        VerifyMobileNumActivity.this.startActivity(mainIntent);
                        finish();
                        break;
                }

            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                getVerificationFailed(errorCode, errorMessage);
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    public void getVerificationCompleted(final SharedPreferencesManager sharedPreferencesManager) {


        new AsyncTask<SharedPreferencesManager, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Boolean doInBackground(SharedPreferencesManager... params) {
                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                apiCallForFetchUser();
            }
        }.execute(sharedPreferencesManager);
    }

    public void getVerificationFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();

        firstDigitTextEdit.requestFocus();
        firstDigitTextEdit.setText("");
        secondDigitTextEdit.setText("");
        thirdDigitTextEdit.setText("");
        fourthDigitTextEdit.setText("");
        fifthDigitTextEdit.setText("");
        sixthDigitTextEdit.setText("");

        switch (errorCode) {
            case 400:
                PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string
                        .error_verification));
        }
    }

    public void apiCallForFetchUser() {
        accountClient.fetchProfile(VerifyMobileNumActivity.this, sharedPreferencesManager, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                currentUserVo.setUserId(((UserVo) responseObject).getUserId());
                currentUserVo.setFirstName(((UserVo) responseObject).getFirstName());
                currentUserVo.setLastName(((UserVo) responseObject).getLastName());
                currentUserVo.setAadhaar(((UserVo) responseObject).getAadhaar());
                currentUserVo.setInstitutatinType(((UserVo) responseObject).getInstitutionType());
                currentUserVo.setInstitutationName(((UserVo) responseObject).getInstitutationName());
                currentUserVo.setState(((UserVo) responseObject).getState());
                currentUserVo.setCity(((UserVo) responseObject).getCity());
                currentUserVo.setTotalReading(((UserVo) responseObject).getTotalReading());
                currentUserVo.setTotalPatients(((UserVo) responseObject).getTotalPatients());
                currentUserVo.setTestingPeriod(((UserVo) responseObject).getTestingPeriod());
                currentUserVo.setAvatarUrl(((UserVo) responseObject).getAvatarUrl());

                UsersManagement.saveCurrentUser(currentUserVo, sharedPreferencesManager);

                onFetchUserCompleted();
            }


            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onFetchUserFailed(errorCode, errorMessage);
            }
        });
    }
    public void onFetchUserCompleted() {
        if (hud!= null)hud.dismiss();
        int typeToMove = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER);
        Intent mainIntent;
        switch (typeToMove){
            case 1:
                mainIntent = new Intent(VerifyMobileNumActivity.this, HomeActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                VerifyMobileNumActivity.this.startActivity(mainIntent);
                finishAffinity();
                break;
        }
    }

    public void onFetchUserFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string
                        .something_went_wrong));
        }
    }
    public void showDialogBox(String message, final EditText editText, String Title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
                    }
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public void openLoginKeyboard() {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);

    }
    public void apiCallToRecieveCodeAgainFromLogin(){
        hud = KProgressHUD.create(VerifyMobileNumActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Requesting code…");
        hud.show();
        accountClient.loginWihtPhone(VerifyMobileNumActivity.this, sharedPreferencesManager, sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPHONENUMBER), new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onResendingCompleted(statusCode, errorMessage);
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onResendingFailed(errorCode, errorMessage);
            }
        });
    }
    public void apiCallToRecieveCodeAgain() {
        hud = KProgressHUD.create(VerifyMobileNumActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Requesting code…");
        hud.show();
        accountClient.startPhoneNumber(VerifyMobileNumActivity.this, sharedPreferencesManager, sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPHONENUMBER), new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onResendingCompleted(statusCode, errorMessage);
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onResendingFailed(errorCode, errorMessage);
            }
        });
    }
    public void onResendingCompleted(int statusCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        PrintUtil.showToast(VerifyMobileNumActivity.this, "We sent the verification code!");
    }

    public void onResendingFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                if (errorMessage.equals(""))
                    PrintUtil.showToast(VerifyMobileNumActivity.this, getResources().getString(R.string.something_went_wrong));
                else{
                    PrintUtil.showToast(VerifyMobileNumActivity.this, errorMessage);
                }
        }
    }
    public class addListenerOnTextChange implements TextWatcher {
        private Context mContext;
        EditText currentEditText;
        EditText nextEditText;

        public addListenerOnTextChange(Context context, EditText currentEditText, EditText nextEditText) {
            super();
            this.mContext = context;
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && this.nextEditText != null) {
                if (this.nextEditText.getText().length() == 0){
                    nextEditText.requestFocus();
                }
            }
            if (s.length() == 1 && this.currentEditText == sixthDigitTextEdit && firstDigitTextEdit.getText().length() == 1 && secondDigitTextEdit.getText().length() == 1 &&
                    thirdDigitTextEdit.getText().length() == 1 && fourthDigitTextEdit.getText().length() == 1 && fifthDigitTextEdit.getText().length() == 1){
                verifyCode();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //What you want to do
        }
    }
    public class onkeyListner implements View.OnKeyListener {
        private Context mContext;
        EditText currentEditText;
        EditText preEditText;

        public onkeyListner(Context context, EditText currentEditText, EditText preEditText) {
            super();
            this.mContext = context;
            this.currentEditText = currentEditText;
            this.preEditText = preEditText;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                //check if the right key was pressed
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (currentEditText.getText().length() == 0){
                        if (preEditText != null) {
                            preEditText.setText("");
                            preEditText.requestFocus();
                        }
                    }
                }
            }else if(event.getAction() == KeyEvent.FLAG_LONG_PRESS){
                Log.w("", "");
            }
            return false;
        }
    }
}

