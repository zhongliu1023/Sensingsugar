package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Activites.HomeActivity;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultBus;
import me.sensingself.sensingsugar.Common.ActivityResults.ActivityResultEvent;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.ActivityCodes;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Common.util.UrlUtils;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Engine.CameraCropLib.CameraConfig;
import me.sensingself.sensingsugar.Engine.CameraCropLib.CropActivity;
import me.sensingself.sensingsugar.Engine.libs.kt3_decode;
import me.sensingself.sensingsugar.Engine.libs.kt3_decode_camera;
import me.sensingself.sensingsugar.Engine.models.Image;
import me.sensingself.sensingsugar.HandleToAws.AwsUploadUtil;
import me.sensingself.sensingsugar.HandleToAws.Constants;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.R;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by liujie on 1/24/18.
 */

public class SeedBeginTestFragment extends BaseFragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Jella_log";
    private TextView beginTestTitle, noteBeginTest, testGuidLink;
    private TextView fastingBloodSugarEdit, postprandialBloodSugarEdit, randomBloodSugarEdit;
    private ImageView fastingCheckBox, postprandialCheckBox,randomBloodCheckBox, backBeginTest;

    private View rootView;
    private String filePathToUpload = "";
    private String currentTestingResutlID = "";

    private SharedPreferencesManager sharedPreferencesManager;
    private Patient currentPatient = new Patient();

    int currentBloodType = 1;

    static String typeBackToFragment = "";
    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    private List<TransferObserver> observers;
    static boolean isBackToSearchTab;

    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
       rootView = inflater.inflate(R.layout.fragment_seed_tab_begin_test, container, false);

        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        transferUtility = AwsUploadUtil.getTransferUtility(getActivity());
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);

        fragmentManager = FragmentmanagerUtils.getFragmentManagerIdentify();

        initNewPatient();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Clear transfer listeners to prevent memory leak, or
        // else this activity won't be garbage collected.
        if (observers != null && !observers.isEmpty()) {
            for (TransferObserver observer : observers) {
                observer.cleanTransferListener();
            }
        }
    }

    public static SeedBeginTestFragment newInstance(String param1, String param2, boolean isBack) {
        isBackToSearchTab = isBack;
        typeBackToFragment = param1;
        SeedBeginTestFragment fragment = new SeedBeginTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private void initNewPatient(){
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
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        TransferListener listener = new UploadListener();
        for (TransferObserver observer : observers) {
            File imgFile = new File(observer.getAbsoluteFilePath());
            if(imgFile.exists()) {
                if (TransferState.WAITING.equals(observer.getState())
                        || TransferState.WAITING_FOR_NETWORK.equals(observer.getState())
                        || TransferState.IN_PROGRESS.equals(observer.getState())) {
                    observer.setTransferListener(listener);
                }else if (TransferState.FAILED.equals(observer.getState())){
                    transferUtility.resume(observer.getId());
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

    private void initController(View rootView){
        beginTestTitle = (TextView) rootView.findViewById(R.id.begin_test_title_text);
        noteBeginTest = (TextView) rootView.findViewById(R.id.noteBeginTest);
        fastingCheckBox = (ImageView) rootView.findViewById(R.id.fastingCheckBox);
        postprandialCheckBox = (ImageView) rootView.findViewById(R.id.postprandialCheckBox);
        fastingBloodSugarEdit = (TextView) rootView.findViewById(R.id.fastingBloodSugarEdit);
        postprandialBloodSugarEdit = (TextView) rootView.findViewById(R.id.postprandialBloodSugarEdit);
        testGuidLink = (TextView) rootView.findViewById(R.id.testGuidLink);
        backBeginTest = (ImageView) rootView.findViewById(R.id.beginTestbackImageView);
        randomBloodSugarEdit = (TextView) rootView.findViewById(R.id.randomBloodSugarEdit);
        randomBloodCheckBox = (ImageView) rootView.findViewById(R.id.randomBloodSugarCheckBox);
        backBeginTest.setImageResource(R.drawable.back_img);
        fastingCheckBox.setImageResource(R.drawable.uncheck_agree);
        postprandialCheckBox.setImageResource(R.drawable.uncheck_agree);
        randomBloodCheckBox.setImageResource(R.drawable.uncheck_agree);


    }

    private void initFontAndText(View rootView){
        beginTestTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        noteBeginTest.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        fastingBloodSugarEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        postprandialBloodSugarEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        randomBloodSugarEdit.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        String guidLink = getResources().getString(R.string.test_guid);
        testGuidLink.setText(Html.fromHtml(guidLink));
        testGuidLink.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }

    private void setListener(final View rootView){
        beginTestTitle.setText(getResources().getString(R.string.begin_test));
        fastingCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedBloodSugarWithType(1);
            }
        });
        postprandialCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedBloodSugarWithType(2);
            }
        });
        fastingBloodSugarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedBloodSugarWithType(1);
            }
        });
        postprandialBloodSugarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedBloodSugarWithType(2);
            }
        });

        randomBloodSugarEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedBloodSugarWithType(3);
            }
        });
        randomBloodCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedBloodSugarWithType(3);
            }
        });
        testGuidLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.seed_root_frame, SeedNeedHelpGuid.newInstance("", ""), "Post")
                        .addToBackStack(FragmentPopKeys.SEEDBEGINTEST)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
            }
        });

        backBeginTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void isSelectedBloodSugarWithType(int type){
        if (type == 1){
            fastingCheckBox.setImageResource(R.drawable.check_agree);
            postprandialCheckBox.setImageResource(R.drawable.uncheck_agree);
            randomBloodCheckBox.setImageResource(R.drawable.uncheck_agree);
        }else if (type == 2){
            fastingCheckBox.setImageResource(R.drawable.uncheck_agree);
            postprandialCheckBox.setImageResource(R.drawable.check_agree);
            randomBloodCheckBox.setImageResource(R.drawable.uncheck_agree);
        }else {
            fastingCheckBox.setImageResource(R.drawable.uncheck_agree);
            postprandialCheckBox.setImageResource(R.drawable.uncheck_agree);
            randomBloodCheckBox.setImageResource(R.drawable.check_agree);
        }
        currentBloodType = type;
//        cameraIntent();
        String userName = "";
        if (currentPatient.getFirstName().length() > 0){
            userName = currentPatient.getFirstName().substring(0,1);
        }
        if (currentPatient.getLastName().length() > 0){
            userName = userName  +  currentPatient.getLastName().substring(0,1);
        }
        currentTestingResutlID = currentPatient.getPatientId() +"_"+ userName +"_"+ (int)(System.currentTimeMillis()/1000) +"_"+ nDigitRandomNo(8);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.seed_root_frame, SeedTestStepsFragment.newInstance(currentTestingResutlID, "", rootView), "Post")
                .addToBackStack(FragmentPopKeys.SEEDBEGINTEST)
                .commit();
        FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
    }

    private void createNewBloodSugar(int type, int mgdl , float mmol){
        TestingResult testingResult = new TestingResult();
        testingResult.setPatientId(currentPatient.getPatientId());
        if (type == 1){
            testingResult.setBloodSugarType("BB");
        }else if (type == 2){
            testingResult.setBloodSugarType("PM");
        }else {
            testingResult.setBloodSugarType("RM");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        testingResult.setCurrentDate(timestamp.getTime()/1000);

        ArrayList<TestingResult> testingResults = new ArrayList<TestingResult>();
        testingResults = currentPatient.getTestingResults();

        testingResult.settestResultId(currentTestingResutlID);
        testingResult.setMgdl(mgdl);
        testingResult.setMmol(mmol);
        testingResult.setSample("saliva");


        testingResults.add(testingResult);
        currentPatient.setTestingResults(testingResults);

        UsersManagement.saveCurrentTestingResult(testingResult, sharedPreferencesManager);
        UsersManagement.saveCurrentPatent(currentPatient, sharedPreferencesManager);
        UsersManagement.addOrUpdateNewPatient(currentPatient, sharedPreferencesManager, SharedPreferencesKeys.REGISTEREDPATIENTSFROMSEED);
    }

    private int nDigitRandomNo(int digits){
        int max = (int) Math.pow(10,(digits)) - 1; //for digits =7, max will be 9999999
        int min = (int) Math.pow(10, digits-1); //for digits = 7, min will be 1000000
        int range = max-min; //This is 8999999
        Random r = new Random();
        int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
        int nDigitRandomNo = x+min; //Our random rumber will be any random number x + min
        return nDigitRandomNo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Don't forget to check requestCode before continuing your job
        if (requestCode == ActivityCodes.CROP_IMAGE) {
            String path = data.getStringExtra(CameraConfig.IMAGE_PATH);
            beginParsing(path, 2);
        }else if (requestCode == ActivityCodes.CROP_GALLERY) {
            Uri uri = data.getData();
            try {
                String path = UrlUtils.getPath(uri, getActivity());
                beginParsing(path, 1);
            } catch (URISyntaxException e) {
                Log.e(TAG, "Unable to get the file from the given URI.  See error log for details", e);
            }
        }
    }

    protected void onBackPressed(){
        if (isBackToSearchTab){
            ((HomeActivity)getActivity()).gotoSearchTab();
            FragmentmanagerUtils.getFragmentManagerSearch().popBackStack();
        }else{
            fragmentManager.popBackStack(typeBackToFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
    /*
     * Begins to upload the file specified by the file path.
     */
    private void beginParsing(String filePath, int type) {
        if (filePath == null) {
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File imgFile = new File(filePath);
        if(imgFile.exists()) {
            //parsing part
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            Map<String, Object> results = new HashMap<String, Object>();

            kt3_decode kt3Decode = new kt3_decode();
            results = kt3Decode.kt3_decode(filePath, myBitmap);
            if (myBitmap != null){
                myBitmap.recycle();
                myBitmap = null;
            }

            Image imageGroup = (Image)results.get("image");
            filePathToUpload = imageGroup.getStoreFilePath();

            boolean successStatus = (Boolean) results.get("success");
            String errMsg = (String) results.get("err");
            int mgdl = 0;
            float mmol = 0;
            if (successStatus == false) {
                Toast.makeText(getActivity(), errMsg, Toast.LENGTH_LONG).show();
            }else{
                beginUpload(filePath);
                Map<String, Object> if_result =  (Map<String, Object>)results.get("lf_result");
                mgdl = (int)if_result.get("lf_glucose_value");
                mmol = Float.parseFloat(String.format("%.1f", (float)mgdl/18));
            }

            createNewBloodSugar(currentBloodType, mgdl, mmol);

            String currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTTESTINGRESULT);
            TestingResult sendTestingResult = new TestingResult();
            Patient sendPatient = new Patient();
            if (!currentPatientStr.equals("")) {
                Gson gson = new Gson();
                Type type1 = new TypeToken<TestingResult>() {
                }.getType();
                sendTestingResult = gson.fromJson(currentPatientStr, type1);
            }
            currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPATIENT);
            if (!currentPatientStr.equals("")) {
                Gson gson = new Gson();
                Type type2 = new TypeToken<Patient>() {
                }.getType();
                sendPatient = gson.fromJson(currentPatientStr, type2);
            }

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .replace(R.id.seed_root_frame, SeedTestReportFragment.newInstance(1, "", sendPatient, sendTestingResult), "Post")
                    .addToBackStack(FragmentPopKeys.SEEDBEGINTEST)
                    .commit();
            FragmentmanagerUtils.setFragmentManagerIdentify(fragmentManager);
        }else{
            Toast.makeText(getActivity(), "No Image.", Toast.LENGTH_LONG).show();
        }
    }

    private void beginUpload(String filePath) {
        if (filePath == null) {
            Toast.makeText(getActivity(), "Could not find the filepath of the selected file",
                    Toast.LENGTH_LONG).show();
            return;
        }
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, file.getName(),
                file);
        observer.setTransferListener(new UploadListener());
    }

    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e("", "Error during upload: " + id, e);
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d("", String.format("onProgressChanged: %d, total: %d, current: %d",
                    id, bytesTotal, bytesCurrent));
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d("", "onStateChanged: " + id + ", " + newState);
        }
    }
}