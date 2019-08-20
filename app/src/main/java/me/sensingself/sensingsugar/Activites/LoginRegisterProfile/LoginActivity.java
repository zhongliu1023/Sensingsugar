package me.sensingself.sensingsugar.Activites.LoginRegisterProfile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import me.sensingself.sensingsugar.BuildConfig;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.StringUtil;
import me.sensingself.sensingsugar.Common.util.URLConstant;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Lib.SoftKeyboardHandle;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

public class LoginActivity extends AppCompatActivity {
    private AccountClient accountClient;

    private Button loginBtn;
    private EditText mobileNumEditText;
    private ImageView sensingsuarLogo;
    private TextView helloTxt;
    private TextView mobileNumTitle;
    private TextView registerLinkTextView;
    private TextView versionTxtView;

    private CountryCodePicker loginPreCCP;

    private boolean isChangedCountry = false;
    private KProgressHUD hud;
    private SharedPreferencesManager sharedPreferencesManager;

    private long currentTime;
    public String genaratedCode;

    private String enteredFullPhoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);

        SoftKeyboardHandle.setupUI(findViewById(R.id.parentLogin), this);

        currentTime = (System.currentTimeMillis() / 1000);
        String combinations = URLConstant.CLIENT_ID + URLConstant.CLIENT_SECRET + currentTime;
        genaratedCode = StringUtil.generateMD5code(combinations);

        String versionName = BuildConfig.VERSION_NAME;

        init();
        initFontAndText();
        setListener();
    }

    private void init(){
        accountClient = new AccountClient();

        loginBtn = (Button) findViewById(R.id.loginBtn);
        mobileNumEditText = (EditText) findViewById(R.id.mobileNumberTextForLogin);
        mobileNumEditText.requestFocus();
        mobileNumEditText.setCursorVisible(true);
        sensingsuarLogo = (ImageView) findViewById(R.id.sensingsuarLogo);
        helloTxt = (TextView) findViewById(R.id.helloTxtView);
        mobileNumTitle = (TextView) findViewById(R.id.mobileNumTitle);
        registerLinkTextView = (TextView) findViewById(R.id.registerLinkFromLogin);
        loginPreCCP = (CountryCodePicker)findViewById(R.id.loginPreCCP);
        versionTxtView = (TextView) findViewById(R.id.versionTxtView);

        versionTxtView.setText("Version." + BuildConfig.VERSION_NAME);

    }

    private void initFontAndText(){
        String titleString = getResources().getString(R.string.register_link_label);
        registerLinkTextView.setText(Html.fromHtml(titleString));

        helloTxt.setTypeface(FontUtility.getSeravekExtraLightItalic(getApplicationContext()));
        mobileNumTitle.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        mobileNumEditText.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        loginBtn.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        registerLinkTextView.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        loginPreCCP.setTypeFace(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        versionTxtView.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        sensingsuarLogo.setImageResource(R.drawable.sensingsugar);
    }

    private void setListener(){

        mobileNumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (mobileNumEditText.getText().length() == 0){
                        Toast.makeText(LoginActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    }else{
                        getAccesstoken();
                    }
                }
                return true;
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNumEditText.getText().length() == 0){
                    Toast.makeText(LoginActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                }else{
                    getAccesstoken();
                }
            }
        });

        registerLinkTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(mainIntent);
            }
        });


        loginPreCCP.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                int maxLength = 20;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                mobileNumEditText.setFilters(fArray);
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
            }
        });
        loginPreCCP.registerCarrierNumberEditText(mobileNumEditText);
        loginPreCCP.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    mobileNumEditText.setTextColor(getResources().getColor(R.color.editTextColor));
                    int maxLength = mobileNumEditText.getText().length();
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    mobileNumEditText.setFilters(fArray);
                }else{
                    mobileNumEditText.setTextColor(getResources().getColor(R.color.colorMobileNumberInvalid));
                }
            }
        });
        loginPreCCP.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, loginPreCCP.getSelectedCountryCode());
            }
        });
        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, loginPreCCP.getSelectedCountryCode());
    }
    private void getAccesstoken(){

        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, loginPreCCP.getSelectedCountryCode());
        enteredFullPhoneNumber = loginPreCCP.getSelectedCountryCodeWithPlus() + " " + mobileNumEditText.getText().toString();
        hud = KProgressHUD.create(LoginActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Login…");
        hud.show();
        accountClient.accessToken(LoginActivity.this, sharedPreferencesManager, genaratedCode, currentTime, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                getAuthTokenCompleted(sharedPreferencesManager);
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                getAuthTokenFailed(errorCode, errorMessage);
            }
        });
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
    @SuppressLint("StaticFieldLeak")
    public void getAuthTokenCompleted(final SharedPreferencesManager sharedPreferencesManager) {

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
                apiCallForLogin();
            }
        }.execute(sharedPreferencesManager);
    }

    public void getAuthTokenFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(LoginActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(LoginActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(LoginActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(LoginActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                PrintUtil.showToast(LoginActivity.this, getResources().getString(R.string
                        .error_login));
        }
    }
    public void apiCallForLogin() {
        accountClient.loginWihtPhone(LoginActivity.this, sharedPreferencesManager, enteredFullPhoneNumber, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onLoginCompleted(statusCode, errorMessage);
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }

            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onLoginFailed(errorCode, errorMessage);
            }
        });
    }
    public void onLoginCompleted(int statusCode, String errorMessage) {
        if (hud!= null)hud.dismiss();

//        PrintUtil.showToast(LoginActivity.this, "The StatusCode is " + statusCode);

        SharedPreferencesManager.getInstance(LoginActivity.this).setPrefernceValueString(SharedPreferencesKeys.CURRENTPHONENUMBER,
                enteredFullPhoneNumber);
        sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_LOGIN);

        UserVo currentUserVo = new UserVo();
        currentUserVo.setPhoneNumber(enteredFullPhoneNumber);
        UsersManagement.saveCurrentUser(currentUserVo, sharedPreferencesManager);


        Intent mainIntent = new Intent(LoginActivity.this, VerifyMobileNumActivity.class);
        LoginActivity.this.startActivity(mainIntent);
    }

    public void onLoginFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(LoginActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(LoginActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(LoginActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(LoginActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                //PrintUtil.showToast(LoginActivity.this, "The statusCode is " + errorCode);
                if (errorMessage.equals("")){
                    PrintUtil.showToast(LoginActivity.this, getResources().getString(R.string.something_went_wrong));
                }else{
                    PrintUtil.showToast(LoginActivity.this, errorMessage);
                }
        }
    }
}
