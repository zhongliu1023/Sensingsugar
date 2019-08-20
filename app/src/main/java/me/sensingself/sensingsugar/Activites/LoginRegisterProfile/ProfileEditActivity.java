package me.sensingself.sensingsugar.Activites.LoginRegisterProfile;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import me.sensingself.sensingsugar.Lib.Utility;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.lang.reflect.Type;

/**
 * Created by liujie on 1/6/18.
 */

public class ProfileEditActivity  extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText firstNameTextEdit;
    private EditText lastnameTextEdit;
    private EditText aadhaarTextEdit;
    private EditText nameInstitutionTextEdit;
    private EditText stateTextEdit;
    private EditText cityTextEdit;

    private TextView profileTitle;
    private TextView profileEditNote;

    private ImageView cameraImage;

    private Button doneBtn;

    private Spinner spinner;

    private SharedPreferencesManager sharedPreferencesManager;
    private UserVo currentUserVo = new UserVo();
    private AccountClient accountClient;
    private KProgressHUD hud;
    private static final String[] institutionTypies = {"Clinic/Hospital", "School/College", "NGOs", "Business", "Family/Community", "Institution Type"};
    String selectedInstitutionType = "";
    boolean redirectToProfileView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Bundle bundle = getIntent().getExtras();
        redirectToProfileView = bundle.getBoolean("redrectProfileView", false);

        SoftKeyboardHandle.setupUI(findViewById(R.id.profileEditView), this);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
        accountClient = new AccountClient();


        initActionBar();
        initNewUserVo();
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
        titleText.setText(R.string.nav_your_profile);
        ImageView backImage = (ImageView) findViewById(R.id.backImageView);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    private void init(){
        profileTitle = (TextView) findViewById(R.id.profileTitle);
        firstNameTextEdit = (EditText) findViewById(R.id.firstNameText);
        lastnameTextEdit = (EditText)findViewById(R.id.lastNameText);
        aadhaarTextEdit = (EditText)findViewById(R.id.aadhaarText);
        nameInstitutionTextEdit = (EditText)findViewById(R.id.nameInstitutionText);
        stateTextEdit = (EditText)findViewById(R.id.stateText);
        cityTextEdit = (EditText)findViewById(R.id.cityText);

        cameraImage = (ImageView) findViewById(R.id.cameraImage);

        doneBtn = (Button) findViewById(R.id.doneBtn);

        spinner = (Spinner) findViewById(R.id.spinnerInstitutionType);

        MySpinnerAdapter adapter = new MySpinnerAdapter(
                ProfileEditActivity.this,
                R.layout.spinner_default_item,
                institutionTypies
        );

        adapter.setDropDownViewResource(R.layout.spinner_default_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(adapter.getCount());
        profileEditNote = (TextView) findViewById(R.id.profileEditNote);
        cameraImage.setVisibility(View.GONE);

        if (currentUserVo != null){
            firstNameTextEdit.setText(currentUserVo.getFirstName());
            lastnameTextEdit.setText(currentUserVo.getLastName());
            aadhaarTextEdit.setText(currentUserVo.getAadhaar());
            if (!(currentUserVo.getInstitutionType().equals(""))){
                for (int i = 0; i < institutionTypies.length; i ++){
                    String currentType = institutionTypies[i];
                    if (currentType.equals(currentUserVo.getInstitutionType())){
                        spinner.setSelection(i);
                    }
                }
            }
            spinner.setPrompt(currentUserVo.getInstitutionType());
            nameInstitutionTextEdit.setText(currentUserVo.getInstitutationName());
            stateTextEdit.setText(currentUserVo.getState());
            cityTextEdit.setText(currentUserVo.getCity());
        }
    }
    private void initFontAndText(){
        profileTitle.setTypeface(FontUtility.getTimesNewRomanItalic(getApplicationContext()));
        firstNameTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        lastnameTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        aadhaarTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        nameInstitutionTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        stateTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        cityTextEdit.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
        doneBtn.setTypeface(FontUtility.getOfficinaSansCBold(getApplicationContext()));
        profileEditNote.setTypeface(FontUtility.getOfficinaSansCBook(getApplicationContext()));
    }
    private void setListener(){
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNameTextEdit.getText().length() == 0){
                    Toast.makeText(ProfileEditActivity.this, "Please enter First Name.", Toast.LENGTH_SHORT).show();
                }else if (lastnameTextEdit.getText().length() == 0){
                    Toast.makeText(ProfileEditActivity.this, "Please enter Last Name.", Toast.LENGTH_SHORT).show();
                }else if (nameInstitutionTextEdit.getText().length() == 0){
                    Toast.makeText(ProfileEditActivity.this, "Please enter Name of Institution.", Toast.LENGTH_SHORT).show();
                }else if (stateTextEdit.getText().length() == 0){
                    Toast.makeText(ProfileEditActivity.this, "Please enter State.", Toast.LENGTH_SHORT).show();
                }else if (cityTextEdit.getText().length() ==0 ){
                    Toast.makeText(ProfileEditActivity.this, "Please enter City.", Toast.LENGTH_SHORT).show();
                }else if (aadhaarTextEdit.getText().length() == 0){
                    Toast.makeText(ProfileEditActivity.this, "Please enter Aadhaar", Toast.LENGTH_SHORT).show();
                }else if (aadhaarTextEdit.getText().length() < 12){
                    Toast.makeText(ProfileEditActivity.this, "Aadhaar should be 12 characters at least.", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    currentUserVo.setFirstName(firstNameTextEdit.getText().toString());
                    currentUserVo.setLastName(lastnameTextEdit.getText().toString());
                    currentUserVo.setAadhaar(aadhaarTextEdit.getText().toString());
                    currentUserVo.setInstitutatinType(selectedInstitutionType);
                    currentUserVo.setInstitutationName(nameInstitutionTextEdit.getText().toString());
                    currentUserVo.setState(stateTextEdit.getText().toString());
                    currentUserVo.setCity(cityTextEdit.getText().toString());

                    hud = KProgressHUD.create(ProfileEditActivity.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setLabel("Saving information…");
                    hud.show();

                    updateUserInfo();
                }
            }
        });
        cameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateUserInfo(){
        accountClient.saveProfile(ProfileEditActivity.this, sharedPreferencesManager, currentUserVo, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onSaveUserCompleted(sharedPreferencesManager);
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
        if (!redirectToProfileView){
            Intent mainIntent = new Intent(ProfileEditActivity.this, HomeActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            ProfileEditActivity.this.startActivity(mainIntent);
            finishAffinity();
        }else{
            finish();
        }
    }
    public void onSaveUserFailed(int errorCode, String errorMessage) {
        if (hud!= null)hud.dismiss();
        switch (errorCode) {
            case 400:
                PrintUtil.showToast(ProfileEditActivity.this, errorMessage);
                break;
            case 404:
                PrintUtil.showToast(ProfileEditActivity.this, errorMessage);
                break;
            case 0:
                PrintUtil.showToast(ProfileEditActivity.this, getResources().getString(R.string.internet_connection));
                break;
            case 599:
                PrintUtil.showToast(ProfileEditActivity.this, getResources().getString(R.string.internet_connection));
                break;
            default:
                PrintUtil.showToast(ProfileEditActivity.this, getResources().getString(R.string
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
    @SuppressLint("StaticFieldLeak")
    public void onSaveUserCompleted(final SharedPreferencesManager sharedPreferencesManager) {

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
                getUserInfo();
            }
        }.execute(sharedPreferencesManager);
    }
    private void getUserInfo(){
        accountClient.fetchProfile(ProfileEditActivity.this, sharedPreferencesManager, new APICallbacks() {
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
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if (position != institutionTypies.length-1){
            selectedInstitutionType = institutionTypies[position];
        }
    }
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
            view.setTypeface(FontUtility.getOfficinaSansCBook(getContext()));
            if (position == super.getCount()-1){
                view.setTextColor(Color.GRAY);
            }else{
                view.setTextColor(getContext().getResources().getColor(R.color.editTextColor));
            }
            return view;
        }

        // Affects opened state of the spinner
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(FontUtility.getOfficinaSansCBook(getContext()));
            return view;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            int count = super.getCount();
            return count>0 ? count-1 : count ;
        }
    }
}
