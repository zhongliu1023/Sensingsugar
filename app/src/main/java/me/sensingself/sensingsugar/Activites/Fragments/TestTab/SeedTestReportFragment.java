package me.sensingself.sensingsugar.Activites.Fragments.TestTab;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Constants.DiabetesCheckingValues;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by liujie on 1/25/18.
 */

public class SeedTestReportFragment  extends BaseFragment {
    private static final String ARG_PARAM1 = "nav_type";
    private static final String ARG_PARAM2 = "param2";

    private TextView retakeTxt, test_report_title, newtakeTxt, usernameOnResultScreenTxt, testingResultDate;
    private TextView currentResultMdglTxt, mdglLabl, currentResultMmolTxt, monthAverageLbl, monthAverageValueTxt, mmolLLabl;
    private TextView testTypeLabel, testTypeValue, bmiLabel, bmiValue, heightLabel, heightValue, weightLabel, weightValue, ageLabel, ageValue, testGuidLabel;
    private ImageView backImageView, informationImageView;
    private TextView veryHightTxt, hightTxt, normalTxt;
    private SharedPreferencesManager sharedPreferencesManager;

    private View borderVer1;

    private int thisMonth = 0;

    static Patient currentPatient = new Patient();
    static TestingResult currentTestingResult = new TestingResult();
    static int nav_type = 1;

    private AccountClient accountClient;

    private ConstraintLayout informationConstraint;
    private TextView infoTitleTxt;
    private ImageView infoLayoutCloseImageView, infoContentImageView;

    FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_seed_tab_test_report, container, false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        accountClient = new AccountClient();
        fragmentManager = FragmentmanagerUtils.getFragmentManagerIdentify();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        thisMonth = getMonth(df.format(c.getTime()));

//        initNewPatient();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);
        updateTestingResult(rootView);

        if (currentTestingResult != null && nav_type == 1 && (int)currentTestingResult.getMgdl() != 0){
            saveTestingResultToServer();
        }

        return rootView;
    }
    public static SeedTestReportFragment newInstance(int navi_type, String param2, Patient patient, TestingResult testingResult) {
        nav_type = navi_type;
        currentPatient = patient;
        currentTestingResult = testingResult;
        SeedTestReportFragment fragment = new SeedTestReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, navi_type);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
//    private void initNewPatient(){
//        String currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTTESTINGRESULT);
//        if (!currentPatientStr.equals("")) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<TestingResult>() {
//            }.getType();
//            currentTestingResult = gson.fromJson(currentPatientStr, type);
//        }
//        currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPATIENT);
//        if (!currentPatientStr.equals("")) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<Patient>() {
//            }.getType();
//            currentPatient = gson.fromJson(currentPatientStr, type);
//        }
//    }
    private void initController(View rootView){
        retakeTxt = (TextView) rootView.findViewById(R.id.retakeTxt);
        retakeTxt.setVisibility(View.GONE);
        test_report_title = (TextView) rootView.findViewById(R.id.test_report_title);
        newtakeTxt = (TextView) rootView.findViewById(R.id.newtakeTxt);
        usernameOnResultScreenTxt = (TextView) rootView.findViewById(R.id.usernameOnResultScreenTxt);
        testingResultDate = (TextView) rootView.findViewById(R.id.todayOnResultTxt);
        currentResultMdglTxt = (TextView) rootView.findViewById(R.id.currentResultMdglTxt);
        mdglLabl = (TextView) rootView.findViewById(R.id.mdglLabl);
        mmolLLabl = (TextView) rootView.findViewById(R.id.mmolLLabl);
        currentResultMmolTxt = (TextView) rootView.findViewById(R.id.currentResultMmolTxt);
        monthAverageLbl = (TextView) rootView.findViewById(R.id.monthAverageLbl);
        monthAverageValueTxt = (TextView) rootView.findViewById(R.id.monthAverageValueTxt);
        testTypeLabel = (TextView) rootView.findViewById(R.id.testTypeLabel);
        testTypeValue = (TextView) rootView.findViewById(R.id.testTypeValue);
        bmiLabel = (TextView) rootView.findViewById(R.id.bmiLabel);
        bmiValue = (TextView) rootView.findViewById(R.id.bmiValue);
        heightLabel = (TextView) rootView.findViewById(R.id.heightLabel);
        heightValue = (TextView) rootView.findViewById(R.id.heightValue);
        weightLabel = (TextView) rootView.findViewById(R.id.weightLabel);
        weightValue = (TextView) rootView.findViewById(R.id.weightValue);
        ageLabel = (TextView) rootView.findViewById(R.id.ageLabel);
        ageValue = (TextView) rootView.findViewById(R.id.ageValue);
        testGuidLabel = (TextView) rootView.findViewById(R.id.testGuidLabel);
        backImageView = (ImageView)rootView.findViewById(R.id.backImageView);
        informationImageView = (ImageView)rootView.findViewById(R.id.informationImageView);
        veryHightTxt = (TextView)rootView.findViewById(R.id.colorView2);
        hightTxt = (TextView)rootView.findViewById(R.id.colorView3);
        normalTxt = (TextView)rootView.findViewById(R.id.colorView4);

        borderVer1 = (View)rootView.findViewById(R.id.borderVer1);

        informationConstraint = (ConstraintLayout)rootView.findViewById(R.id.informationConstraint);
        informationConstraint.setVisibility(View.GONE);
        infoTitleTxt = (TextView) rootView.findViewById(R.id.infoTitleTxt);
        infoLayoutCloseImageView = (ImageView) rootView.findViewById(R.id.infoLayoutCloseImageView);
        infoContentImageView = (ImageView)rootView.findViewById(R.id.infoContentImageView);



        monthAverageValueTxt.setText(Html.fromHtml("<b>246 </b>" +  "<small>mg/dl</small>" + "<b> | </b>" +
                "<b>13.7 </b>"+  "<small>mmol</small>"));


        backImageView.setImageResource(R.drawable.back_img);
        informationImageView.setImageResource(R.drawable.information);
        infoLayoutCloseImageView.setImageResource(R.drawable.close_x);
        infoContentImageView.setImageResource(R.drawable.information_content);


        borderVer1.getLayoutParams().height = currentResultMdglTxt.getLineHeight() + mdglLabl.getLineHeight() + 20;
        borderVer1.requestLayout();
    }

    private void initFontAndText(View rootView){
        retakeTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        test_report_title.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        newtakeTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        usernameOnResultScreenTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        testingResultDate.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        currentResultMdglTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        mdglLabl.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        mmolLLabl.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        currentResultMmolTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        monthAverageLbl.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        monthAverageValueTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        testTypeLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        testTypeValue.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        bmiLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        bmiValue.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        heightLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        heightValue.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weightLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weightValue.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        ageLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        ageValue.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        testGuidLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        veryHightTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        hightTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        normalTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        infoTitleTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
    }

    private void setListener(final View rootView){
        test_report_title.setText(getResources().getString(R.string.testreport));
        if (nav_type == 1){
            newtakeTxt.setText(getResources().getString(R.string.takeNewtest_navi_item));
            backImageView.setVisibility(View.INVISIBLE);
        }else{
            newtakeTxt.setText(getResources().getString(R.string.share));
            backImageView.setVisibility(View.VISIBLE);
        }
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        retakeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nav_type == 1){
                    fragmentManager.popBackStack(FragmentPopKeys.SEEDBEGINTEST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }else{

                }
            }
        });
        newtakeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nav_type == 1){
                    fragmentManager.popBackStack(FragmentPopKeys.SEEDBEGINTEST, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }else{

                }
            }
        });
        informationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationConstraint.setVisibility(View.VISIBLE);
            }
        });
        infoLayoutCloseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                informationConstraint.setVisibility(View.GONE);
            }
        });
    }

    protected void onBackPressed(){
        if (nav_type == 2){
            FragmentmanagerUtils.getFragmentManagerSearch().popBackStack(FragmentPopKeys.SEARCHREPORTFORPATIENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }else{
            FragmentmanagerUtils.getFragmentManagerReports().popBackStack(FragmentPopKeys.REPORTSFRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
    public void onFirstFragmentBac(){
        accountClient = null;
        sharedPreferencesManager.setCurrentPatient(SharedPreferencesKeys.CURRENTPATIENT, "");
        fragmentManager.popBackStack(FragmentPopKeys.SEED, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    private void updateTestingResult(View rootView){
        usernameOnResultScreenTxt.setText(currentPatient.getFirstName() + " " + currentPatient.getLastName());
        currentResultMdglTxt.setText(Long.toString(currentTestingResult.getMgdl()));
        currentResultMmolTxt.setText(Float.toString(currentTestingResult.getMmol()));

        if (currentTestingResult.getBloodSugarType().equals("BB")){
            testTypeValue.setText(getActivity().getString(R.string.fasting_blood_sugar));
            if (currentTestingResult.getMgdl() >= DiabetesCheckingValues.FSDIABETESMIN){
                changeColorOfTestingResult(getResources().getColor(R.color.colorResultDark));
            }else if (currentTestingResult.getMgdl() < DiabetesCheckingValues.FSDIABETESMIN && currentTestingResult.getMgdl() >= DiabetesCheckingValues.FSPREDIABETESMIN){
                changeColorOfTestingResult(getResources().getColor(R.color.colorResult));
            }else {
                changeColorOfTestingResult(getResources().getColor(R.color.colorResultLight));
            }
        }else if (currentTestingResult.getBloodSugarType().equals("PM")){
            testTypeValue.setText(getActivity().getString(R.string.postprandial_blood_sugar));
            if (currentTestingResult.getMgdl() >= DiabetesCheckingValues.PMSDIABETESMIN){
                changeColorOfTestingResult(getResources().getColor(R.color.colorResultDark));
            }else if (currentTestingResult.getMgdl() < DiabetesCheckingValues.PMSDIABETESMIN && currentTestingResult.getMgdl() >= DiabetesCheckingValues.PMSPREDIABETESMIN){
                changeColorOfTestingResult(getResources().getColor(R.color.colorResult));
            }else {
                changeColorOfTestingResult(getResources().getColor(R.color.colorResultLight));
            }
        }else {
            testTypeValue.setText(getActivity().getString(R.string.random_blood_sugar));
            if (currentTestingResult.getMgdl() >= DiabetesCheckingValues.RSDIABETESMIN){
                changeColorOfTestingResult(getResources().getColor(R.color.colorResultDark));
            }else {
                changeColorOfTestingResult(getResources().getColor(R.color.colorResultLight));
            }
        }
        float height = currentPatient.getHeight();
        float weight = currentPatient.getWeight();
        heightValue.setText(String.format("%d", Math.round(currentPatient.getHeight())) + " cms");
        weightValue.setText(String.format("%d", Math.round(currentPatient.getWeight())) + " kgs");
        String currentCountryCode = sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT);
        if (currentCountryCode.equals("1")){
            long heightIn = Math.round(height * 0.39);
            double weightPounds = Math.round(weight * 2.2046);
            heightValue.setText(String.format("%d", (int)heightIn/12) + " ft " + String.format("%d", (int)heightIn%12) + " in");
            weightValue.setText(String.format("%d", (int)weightPounds) + " lbs");
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

            ageValue.setText(DateUtils.getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)) + " yrs");
        }else{
            ageValue.setText(" yrs");
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
        bmiValue.setText(Float.toString(bmi) + " (" + categoryBmi +")");
        monthAverageValueTxt.setText(Html.fromHtml("<b>" + getAverageMgdl() + " </b>" +  "<small>mg/dl</small>" + "<b> | </b>" +
                "<b>" + getAverageMmol()+ " </b>"+  "<small>mmol</small>"));

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMMM");
        monthAverageLbl.setText(getResources().getString(R.string.this_month_avg) + " " + df.format(c.getTime()));

        long resultDate = currentTestingResult.getCurrentDate();
        String timer = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(resultDate));
        String convertDate = DateUtils.string2StringDate(timer, "yyyy-MM-dd hh:mm", "dd, MMMM yyyy hh:mmaaa");
        testingResultDate.setText(convertDate);

    }
    private void changeColorOfTestingResult(int colorID){
        currentResultMdglTxt.setTextColor(colorID);
        currentResultMmolTxt.setTextColor(colorID);
        mdglLabl.setTextColor(colorID);
        mmolLLabl.setTextColor(colorID);
    }
    private String getAverageMgdl(){
        long totalMgdl = 0;
        int index = 0;

        ArrayList<TestingResult> testingResults = new ArrayList<TestingResult>();
        testingResults = currentPatient.getTestingResults();
        for (int i = 0; i < testingResults.size(); i++) {
            TestingResult oneTestingResult = testingResults.get(i);
            long timeStamp = oneTestingResult.getCurrentDate();
            String time = GmtUtil.localToGmt0AfterCheckingTimeStamp(timeStamp);
            int monthIdxFromSugar = getMonth(time);
            if (thisMonth == monthIdxFromSugar){
                totalMgdl = totalMgdl + oneTestingResult.getMgdl();
                index ++;
            }
        }


        int averageMgdl = 0;
        if (index > 0){
            averageMgdl = (int)totalMgdl/index;
        }
        return Integer.toString(averageMgdl);
    }
    private String getAverageMmol(){
        float totalMmol = 0;
        int index = 0;

        ArrayList<TestingResult> testingResults = new ArrayList<TestingResult>();
        testingResults = currentPatient.getTestingResults();
        for (int i = 0; i < testingResults.size(); i++) {
            TestingResult oneTestingResult = testingResults.get(i);
            long timeStamp = oneTestingResult.getCurrentDate();
            String time = GmtUtil.localToGmt0AfterCheckingTimeStamp(timeStamp);
            int monthIdxFromSugar = getMonth(time);
            if (thisMonth == monthIdxFromSugar){
                totalMmol = totalMmol + oneTestingResult.getMmol();
                index ++;
            }
        }

        float averageMmol =  0;
        if (index > 0){
            averageMmol = Float.parseFloat(String.format("%.1f", totalMmol/index));
        }
        return Float.toString(averageMmol);
    }
    private int getMonth(String date) {
        String[] monthDate = date.split("-");
        int month = Integer.parseInt(monthDate[0] + monthDate[1]);
        return month;
    }

    private void saveTestingResultToServer(){
        accountClient.saveTestingResult(getActivity(), sharedPreferencesManager, currentTestingResult, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                onSaveTestingResultCompleted();
            }

            @Override
            public <E> void onSuccessJsonArray(E responseArray, int statusCode, String errorMessage) {
            }

            @Override
            public <E> void onFailure(int errorCode, String errorMessage, E failureDetails) {
                onSaveTestingResultFailed(errorCode, errorMessage);
            }
        });
    }
    public void onSaveTestingResultCompleted() {

    }
    public void onSaveTestingResultFailed(int errorCode, String errorMessage) {
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
}