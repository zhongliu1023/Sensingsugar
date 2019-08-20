package me.sensingself.sensingsugar.Activites.LoginRegisterProfile;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by liujie on 1/6/18.
 */

public class RegisterActivity  extends AppCompatActivity {
    private EditText mobileNumRegister;
    private ImageView iagreeImageView;
    private TextView viewTermsTextView;
    private Button nextBtn;
    private TextView agreeTxtView;
    private TextView mobileNumTitle;

    private CountryCodePicker registerPreCCP;

    Boolean isSetIAgree = false;
    private AccountClient accountClient;
    private KProgressHUD hud;
    private SharedPreferencesManager sharedPreferencesManager;

    String enteredFullPhoneNumber = "";
    private long currentTime;
    public String genaratedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        SoftKeyboardHandle.setupUI(findViewById(R.id.parentRegister), this);

        currentTime = (System.currentTimeMillis() / 1000);
        String combinations = URLConstant.CLIENT_ID + URLConstant.CLIENT_SECRET + currentTime;
        genaratedCode = StringUtil.generateMD5code(combinations);

        initActionBar();
        init();
        initFontAndText();
        setListener();
    }
    private void initActionBar(){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_default);
        getSupportActionBar().setElevation(0);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        titleText.setText(R.string.nav_register);
        ImageView backImage = (ImageView) findViewById(R.id.backImageView);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void init(){
        accountClient = new AccountClient();

        mobileNumRegister = (EditText) findViewById(R.id.mobileNumberTextForRegister);
        mobileNumRegister.requestFocus();
        mobileNumRegister.setCursorVisible(true);
        iagreeImageView = (ImageView) findViewById(R.id.agreeImageBtn);
        viewTermsTextView = (TextView) findViewById(R.id.linkViewTerms);
        nextBtn = (Button) findViewById(R.id.registerNextBtn);
        mobileNumTitle = (TextView) findViewById(R.id.mobileNumTitle);
        agreeTxtView = (TextView) findViewById(R.id.agreeTxtView);

        registerPreCCP = (CountryCodePicker) findViewById(R.id.registerPreCCP);
    }
    private void initFontAndText(){
        mobileNumTitle.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        mobileNumRegister.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        agreeTxtView.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        viewTermsTextView.setTypeface(FontUtility.getSeravekItalic(getApplicationContext()));
        nextBtn.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        registerPreCCP.setTypeFace(FontUtility.getOfficinaSansCBook(getApplicationContext()));
    }
    private void setListener(){
        mobileNumRegister.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (mobileNumRegister.getText().length() == 0){
                        Toast.makeText(RegisterActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                    }else if(!isSetIAgree) {
                        Toast.makeText(RegisterActivity.this, "Please agree the Terms", Toast.LENGTH_SHORT).show();
                    }else{
                        getAccesstoken();
                    }
                }
                return false;
            }
        });
        iagreeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSetIAgree = !isSetIAgree;
                if (isSetIAgree){
                    iagreeImageView.setImageResource(R.drawable.check_agree);
                }else{
                    iagreeImageView.setImageResource(R.drawable.uncheck_agree);
                }
            }
        });
        viewTermsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "You tap 'View Terms'!", Toast.LENGTH_SHORT).show();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mobileNumRegister.getText().length() == 0){
                    Toast.makeText(RegisterActivity.this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
                }else if(!isSetIAgree) {
                    Toast.makeText(RegisterActivity.this, "Please agree the Terms", Toast.LENGTH_SHORT).show();
                }else{

                    getAccesstoken();
                }
            }
        });
        registerPreCCP.setDialogEventsListener(new CountryCodePicker.DialogEventsListener() {
            @Override
            public void onCcpDialogOpen(Dialog dialog) {
                int maxLength = 20;
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(maxLength);
                mobileNumRegister.setFilters(fArray);
            }

            @Override
            public void onCcpDialogDismiss(DialogInterface dialogInterface) {
            }

            @Override
            public void onCcpDialogCancel(DialogInterface dialogInterface) {
            }
        });
        registerPreCCP.registerCarrierNumberEditText(mobileNumRegister);
        registerPreCCP.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    mobileNumRegister.setTextColor(getResources().getColor(R.color.editTextColor));

                    int maxLength = mobileNumRegister.getText().length();
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(maxLength);
                    mobileNumRegister.setFilters(fArray);
                }else{
                    mobileNumRegister.setTextColor(getResources().getColor(R.color.colorMobileNumberInvalid));
                }
            }
        });
        registerPreCCP.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, registerPreCCP.getSelectedCountryCode());
            }
        });
        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, registerPreCCP.getSelectedCountryCode());
    }
    private void getAccesstoken(){

        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY, registerPreCCP.getSelectedCountryCode());
        enteredFullPhoneNumber = registerPreCCP.getSelectedCountryCodeWithPlus() + " " + mobileNumRegister.getText().toString();
        hud = KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Register…");
        hud.show();

        accountClient.accessToken(RegisterActivity.this, sharedPreferencesManager, genaratedCode, currentTime, new APICallbacks() {
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
                    editText.requestFocus();
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_FORCED);
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
                apiCallForRegister();
            }
        }.execute(sharedPreferencesManager);
    }

    public void getAuthTokenFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(RegisterActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(RegisterActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(RegisterActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(RegisterActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                PrintUtil.showToast(RegisterActivity.this, getResources().getString(R.string
                        .error_login));
        }
    }




    public void apiCallForRegister() {
        accountClient.startPhoneNumber(RegisterActivity.this, sharedPreferencesManager, enteredFullPhoneNumber, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onRegisterCompleted(statusCode, errorMessage);
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {

            }
            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onRegisterFailed(errorCode, errorMessage);
            }
        });
    }
    public void onRegisterCompleted(int statusCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        SharedPreferencesManager.getInstance(RegisterActivity.this).setPrefernceValueString(SharedPreferencesKeys.CURRENTPHONENUMBER,
                enteredFullPhoneNumber);

        UserVo currentUserVo = new UserVo();
        currentUserVo.setPhoneNumber(enteredFullPhoneNumber);
        UsersManagement.saveCurrentUser(currentUserVo, sharedPreferencesManager);
        sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.MOVEACTIVITYWITHMOBILENUMBER, SharedPreferencesKeys.FROM_REGISTER);
        Intent mainIntent = new Intent(RegisterActivity.this, VerifyMobileNumActivity.class);
        RegisterActivity.this.startActivity(mainIntent);
    }

    public void onRegisterFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(RegisterActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(RegisterActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(RegisterActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(RegisterActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                if (errorMessage.equals(""))
                    PrintUtil.showToast(RegisterActivity.this, getResources().getString(R.string.something_went_wrong));
                else{
                    PrintUtil.showToast(RegisterActivity.this, errorMessage);
                }
        }
    }
}