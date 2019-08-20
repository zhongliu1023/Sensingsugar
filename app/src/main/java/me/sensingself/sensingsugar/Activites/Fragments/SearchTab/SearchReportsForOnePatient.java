package me.sensingself.sensingsugar.Activites.Fragments.SearchTab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.otto.Subscribe;

import junit.framework.Test;

import me.sensingself.sensingsugar.Activites.Adapter.SearchResultAdapter;
import me.sensingself.sensingsugar.Activites.Fragments.BaseFragment;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedTestReportFragment;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsBus;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEvents;
import me.sensingself.sensingsugar.Common.FragmentsBus.FragmentsEventsKeys;
import me.sensingself.sensingsugar.Common.Interfaces.PDFListener;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Common.util.PDFUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Constants.DiabetesCheckingValues;
import me.sensingself.sensingsugar.Constants.PermissionRequest;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;

import java.io.File;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Created by liujie on 2/6/18.
 */

public class SearchReportsForOnePatient extends BaseFragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView reportsTitle, shareTxtView, patientNameTxt, patientAddress, testPeriodTxt, testPeriodValueTxt;
    private TextView lastReadingTxt, fastingTxt, currentMgdlValue, currentMmolValue, mgdlLabel, mmolLLabl;
    private TextView postMealTxt, postCurrentMgdlVal, postCurrentMmolVal, mgdlLabel1, mmolLLabel1;

    //header
    private ImageView resultCheckBox;
    private ImageButton cyclePreMonth, cycleNextMonth;
    private TextView monthTxtView, reportsTxtView, levelTxtView;

    private ListView searchedResultListView;
    private ImageView searchReportBackImageView, patientAvatarIamgeView;
    SearchResultAdapter adapter;

    private SharedPreferencesManager sharedPreferencesManager;
    ArrayList<Patient> patients = new ArrayList<Patient>();

    Map<Integer, ArrayList<TestingResult>> testingResultWithKey = new ArrayMap<>();
    ArrayList<TestingResult> testingResults;
    ArrayList<TestingResult> selectedTestingResults = new ArrayList<TestingResult>();
    int currentSelectedMonthIndex = 0;
    boolean isSelectedAll = false;

    static Patient selectedPatient = new Patient();

    FragmentManager fragmentManager;

    UserVo currentUserVo = new UserVo();
    private KProgressHUD hud;
    public static SearchReportsForOnePatient newInstance(String param1, String param2) {
        SearchReportsForOnePatient fragment = new SearchReportsForOnePatient();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_report_for_patient, container, false);
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        fragmentManager = FragmentmanagerUtils.getFragmentManagerSearch();
        initNewUserVo();
        initNewPatientAndTestingResults();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateData(selectedTestingResults);
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
    private void initNewPatientAndTestingResults(){

        sharedPreferencesManager.setCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRYPATIENT, sharedPreferencesManager.getCurrentCountry(SharedPreferencesKeys.CURRENTCOUNTRY));
        String currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.CURRENTPATIENT);
        if (!currentPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<Patient>() {
            }.getType();
            selectedPatient = gson.fromJson(currentPatientStr, type);
        }

        testingResultWithKey =  getDataParseTestingResults();
        Set<Integer> keys = testingResultWithKey.keySet();
        Integer[] keySet = keys.toArray(new Integer[keys.size()]);
        currentSelectedMonthIndex = keySet.length-1;
        selectedTestingResults = testingResultWithKey.get(keySet[currentSelectedMonthIndex]);

    }
    private void showingHidingArrowsForMonthCycle(){
        Set<Integer> keys = testingResultWithKey.keySet();
        if (keys.size() == 1){
            cyclePreMonth.setVisibility(View.INVISIBLE);
            cycleNextMonth.setVisibility(View.INVISIBLE);
        }else{
            if (currentSelectedMonthIndex == keys.size()-1){
                cyclePreMonth.setVisibility(View.VISIBLE);
                cycleNextMonth.setVisibility(View.INVISIBLE);
            }else if (currentSelectedMonthIndex == 0){
                cyclePreMonth.setVisibility(View.INVISIBLE);
                cycleNextMonth.setVisibility(View.VISIBLE);
            }else{
                cyclePreMonth.setVisibility(View.VISIBLE);
                cycleNextMonth.setVisibility(View.VISIBLE);
            }
        }
        updateHeaderView();

    }
    private void initController(View rootView) {
        reportsTitle = (TextView) rootView.findViewById(R.id.reportsTitle);
        shareTxtView = (TextView) rootView.findViewById(R.id.shareTxtView);
        patientNameTxt  = (TextView) rootView.findViewById(R.id.patientNameTxt);
        patientAddress = (TextView) rootView.findViewById(R.id.patientAddress);
        testPeriodTxt = (TextView) rootView.findViewById(R.id.testPeriodTxt);
        testPeriodValueTxt = (TextView) rootView.findViewById(R.id.testPeriodValueTxt);

        lastReadingTxt = (TextView) rootView.findViewById(R.id.lastReadingTxt);
        fastingTxt = (TextView) rootView.findViewById(R.id.fastingTxt);
        currentMgdlValue = (TextView) rootView.findViewById(R.id.currentMgdlValue);
        currentMmolValue = (TextView) rootView.findViewById(R.id.currentMmolValue);
        mgdlLabel = (TextView) rootView.findViewById(R.id.mgdlLabel);
        mmolLLabl = (TextView) rootView.findViewById(R.id.mmolLLabl);
        postMealTxt = (TextView) rootView.findViewById(R.id.postMealTxt);
        postCurrentMgdlVal = (TextView) rootView.findViewById(R.id.postCurrentMgdlVal);
        postCurrentMmolVal = (TextView) rootView.findViewById(R.id.postCurrentMmolVal);
        mgdlLabel1 = (TextView) rootView.findViewById(R.id.mgdlLabel1);
        mmolLLabel1 = (TextView) rootView.findViewById(R.id.mmolLLabel1);

        searchReportBackImageView = (ImageView) rootView.findViewById(R.id.searchBackImageView);
        searchReportBackImageView.setImageResource(R.drawable.back_img);

        patientAvatarIamgeView = (ImageView) rootView.findViewById(R.id.patientAvatarIamgeView);
        patientAvatarIamgeView.setImageResource(R.drawable.profile_man);

        resultCheckBox = (ImageView) rootView.findViewById(R.id.resultCheckBox);
        resultCheckBox.setImageResource(R.drawable.uncheck_agree);
        cyclePreMonth = (ImageButton) rootView.findViewById(R.id.cyclePreMonth);
        cyclePreMonth.setImageResource(R.drawable.cycle_left_arrow);
        cycleNextMonth = (ImageButton) rootView.findViewById(R.id.cycleNextMonth);
        cycleNextMonth.setImageResource(R.drawable.cycle_right_arrow);
        monthTxtView = (TextView) rootView.findViewById(R.id.monthTxtView);
        reportsTxtView = (TextView) rootView.findViewById(R.id.reportsTxtView);
        levelTxtView = (TextView) rootView.findViewById(R.id.levelTxtView);

        searchedResultListView = (ListView) rootView.findViewById(R.id.resultsList);
        searchedResultListView.setDivider(null);

        adapter = new SearchResultAdapter(getActivity(), R.layout.adapter_date_month, selectedTestingResults);
        adapter.setParientFragment(this);
        searchedResultListView.setAdapter(adapter);
        searchedResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TestingResult testingResult = selectedTestingResults.get(position);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.search_root_frame, SeedTestReportFragment.newInstance(2, "", selectedPatient, testingResult), "Post")
                        .addToBackStack(FragmentPopKeys.SEARCHREPORTFORPATIENT)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);
            }
        });


        showingHidingArrowsForMonthCycle();
    }
    private Map<Integer, ArrayList<TestingResult>> getDataParseTestingResults(){
        Map<Integer, ArrayList<TestingResult>> results = new ArrayMap<>();
        for (TestingResult testingResult : selectedPatient.getTestingResults()){
            int monthNumber = getMonth(testingResult.getCurrentDate());
            if (results.containsKey(monthNumber)) {
                testingResults.add(testingResult);
                results.put(monthNumber, testingResults);
            } else {
                testingResults = new ArrayList<TestingResult>();
                testingResults.add(testingResult);
                results.put(monthNumber, testingResults);
            }
        }
        return results;
    }
    private int getMonth(long timeStamp) {
        String time = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(timeStamp));
        String[] monthDate = time.split("-");
        int month = Integer.parseInt(monthDate[0] + monthDate[1]);
        return month;
    }
    private void initFontAndText(View rootView) {
        reportsTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        shareTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        patientNameTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        patientAddress.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        testPeriodTxt.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        testPeriodValueTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));

        monthTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        reportsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        levelTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));

        lastReadingTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        fastingTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        currentMgdlValue.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        currentMmolValue.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        mgdlLabel.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        mmolLLabl.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        postMealTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        postCurrentMgdlVal.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        postCurrentMmolVal.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        mgdlLabel1.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        mmolLLabel1.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));

        int searchMode = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SEARCHMODE);
        String fromDateStr = "";
        String toDateStr = "";
        if (searchMode == 1){
            fromDateStr = DateUtils.string2StringDate(sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHFROMDATE), "yyyy-MM-dd", "dd.MM.yyyy");
            toDateStr = DateUtils.string2StringDate(sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHTODATE), "yyyy-MM-dd", "dd.MM.yyyy");
        }else{
            ArrayList<TestingResult> tmpTestingResult = new ArrayList<TestingResult>();

            for (TestingResult testingResult : selectedPatient.getTestingResults()){
                tmpTestingResult.add(testingResult);
            }

            Collections.sort(tmpTestingResult, new Comparator<TestingResult>() {
                @Override
                public int compare(TestingResult entry1, TestingResult entry2) {
                    long time1 = (long) entry1.getCurrentDate();
                    long time2 = (long)entry2.getCurrentDate();
                    if (time1 > time2) {
                        return 1;
                    }
                    else if (time1 <  time2) {
                        return -1;
                    }
                    else {
                        return 0;
                    }
                }
            });

            TestingResult pat1 = tmpTestingResult.get(0);
            TestingResult pat2 = tmpTestingResult.get(tmpTestingResult.size()-1);
            fromDateStr = getDateFromTimeStamp(pat1.getCurrentDate());
            toDateStr = getDateFromTimeStamp(pat2.getCurrentDate());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date fromDate = null;
        Date toDate = null;
        long days = 0;
        try {
            fromDate = sdf.parse(fromDateStr);
            toDate = sdf.parse(toDateStr);
            long diff = fromDate.getTime() - toDate.getTime();
            days =  TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        testPeriodValueTxt.setText( Html.fromHtml("<b>" +selectedPatient.getTestingResults().size()+ "</b> in <b>" + days + "</b> days (" + fromDateStr + " to " + toDateStr + ")"));
        updateHeaderView();

        sortingTestingResults(sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SORTINGMODEREPORTS));
    }
    private void updateHeaderView(){

        if (selectedTestingResults.size() == 1){
            TestingResult testingResult = selectedTestingResults.get(0);
            currentMgdlValue.setText(Long.toString(testingResult.getMgdl()));
            currentMmolValue.setText(Float.toString(testingResult.getMmol()));
            postCurrentMgdlVal.setText("0");
            postCurrentMmolVal.setText("0.0");
            updateColorOfTestingResultsForLastReading(testingResult, null);
        }else if (selectedTestingResults.size() > 1){

            TestingResult firsttestingResult = selectedTestingResults.get(0);
            currentMgdlValue.setText(Long.toString(firsttestingResult.getMgdl()));
            currentMmolValue.setText(Float.toString(firsttestingResult.getMmol()));


            TestingResult secondtestingResult = selectedTestingResults.get(1);
            postCurrentMgdlVal.setText(Long.toString(secondtestingResult.getMgdl()));
            postCurrentMmolVal.setText(Float.toString(secondtestingResult.getMmol()));


            updateColorOfTestingResultsForLastReading(firsttestingResult, secondtestingResult);
        }

        Set<Integer> keys = testingResultWithKey.keySet();
        Integer[] keySet = keys.toArray(new Integer[keys.size()]);
        Integer currentMonth = keySet[currentSelectedMonthIndex];
        monthTxtView.setText(intMonth2String(currentMonth));
        reportsTxtView.setText(selectedTestingResults.size() + " REPORTS");

        isSelectedAll = true;
        for (int i = 0 ; i < selectedTestingResults.size() ; i++){
            TestingResult testingResult = selectedTestingResults.get(i);
            if (!testingResult.getSelected()){
                isSelectedAll = false;
                break;
            }
        }
        if (isSelectedAll){
            resultCheckBox.setImageResource(R.drawable.check_agree);
        }else{
            resultCheckBox.setImageResource(R.drawable.uncheck_agree);
        }

    }
    private String intMonth2String(Integer month){
        return DateUtils.string2StringDate(String.valueOf(month), "yyyyMM", "MMM yyyy");
    }
    private void updateColorOfTestingResultsForLastReading(TestingResult firstTest, TestingResult secondTest){
        if (firstTest.getBloodSugarType().equals("BB")){
            fastingTxt.setText("Fasting");
            if (firstTest.getMgdl() >= DiabetesCheckingValues.FSDIABETESMIN){
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResultDark));
            }else if (firstTest.getMgdl() < DiabetesCheckingValues.FSDIABETESMIN && firstTest.getMgdl() >= DiabetesCheckingValues.FSPREDIABETESMIN){
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResult));
            }else {
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResultLight));
            }
        }else if (firstTest.getBloodSugarType().equals("PM")){
            fastingTxt.setText("Post Meal");
            if (firstTest.getMgdl() >= DiabetesCheckingValues.PMSDIABETESMIN){
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResultDark));
            }else if (firstTest.getMgdl() < DiabetesCheckingValues.PMSDIABETESMIN && firstTest.getMgdl() >= DiabetesCheckingValues.PMSPREDIABETESMIN){
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResult));
            }else {
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResultLight));
            }
        }else {
            fastingTxt.setText("Random");
            if (firstTest.getMgdl() >= 200.0){
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResultDark));
            }else {
                changeColorOfFirstTestingResult(getResources().getColor(R.color.colorResultLight));
            }
        }
        if (secondTest != null){
            if (secondTest.getBloodSugarType().equals("BB")){
                postMealTxt.setText("Fasting");
                if (secondTest.getMgdl() >= DiabetesCheckingValues.FSDIABETESMIN){
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultDark));
                }else if (secondTest.getMgdl() < DiabetesCheckingValues.FSDIABETESMIN && secondTest.getMgdl() >= DiabetesCheckingValues.FSPREDIABETESMIN){
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResult));
                }else {
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultLight));
                }
            }else if (secondTest.getBloodSugarType().equals("PM")){
                postMealTxt.setText("Post Meal");
                if (secondTest.getMgdl() >= DiabetesCheckingValues.PMSDIABETESMIN){
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultDark));
                }else if (secondTest.getMgdl() < DiabetesCheckingValues.PMSDIABETESMIN && secondTest.getMgdl() >= DiabetesCheckingValues.PMSPREDIABETESMIN){
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResult));
                }else {
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultLight));
                }
            }else {
                postMealTxt.setText("Random");
                if (secondTest.getMgdl() >= DiabetesCheckingValues.RSDIABETESMIN){
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultDark));
                }else {
                    changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultLight));
                }
            }
        }else{
            changeColorOfSecondTestingResult(getResources().getColor(R.color.colorResultLight));
        }
    }
    private void changeColorOfFirstTestingResult(int colorID){
        currentMgdlValue.setTextColor(colorID);
        currentMmolValue.setTextColor(colorID);
    }
    private void changeColorOfSecondTestingResult(int colorID){
        postCurrentMgdlVal.setTextColor(colorID);
        postCurrentMmolVal.setTextColor(colorID);
    }
    private String getDateFromTimeStamp(long timeStamp){
        String time = GmtUtil.localToGmt0AfterCheckingTimeStamp(timeStamp);
        return DateUtils.string2StringDate(time, "yyyy-MM-dd HH:mm", "dd.MM.yyyy");
    }

    protected void onBackPressed(){
        fragmentManager.popBackStack(FragmentPopKeys.SEARCHRESULT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    private void setListener(final View rootView) {
        reportsTitle.setText(getResources().getString(R.string.reports));
        patientNameTxt.setText(selectedPatient.getFirstName() + " " + selectedPatient.getLastName());

        searchReportBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        shareTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSharingReports();
            }
        });
        patientAvatarIamgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.search_root_frame, PatientProfileViewFragment.newInstance("", ""), "Post")
                        .addToBackStack(FragmentPopKeys.SEARCHREPORTFORPATIENT)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerSearch(fragmentManager);
            }
        });

        resultCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedAll = !isSelectedAll;
                for (int i = 0 ; i < selectedTestingResults.size() ; i++){
                    TestingResult testingResult = selectedTestingResults.get(i);
                    testingResult.setSelected(isSelectedAll);
                    selectedTestingResults.set(i, testingResult);
                }
                if (isSelectedAll){
                    resultCheckBox.setImageResource(R.drawable.check_agree);
                }else{
                    resultCheckBox.setImageResource(R.drawable.uncheck_agree);
                }
                adapter.updateData(selectedTestingResults);
            }
        });
        cyclePreMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedMonthIndex == 0) return;
                currentSelectedMonthIndex --;
                updateTestingResultList();
            }
        });
        cycleNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedMonthIndex == testingResultWithKey.size()-1) return;
                currentSelectedMonthIndex ++;
                updateTestingResultList();
            }
        });
        levelTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sortingMode = sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SORTINGMODEREPORTS);
                if (sortingMode == 2)
                    sortingMode = 0;
                else
                    sortingMode++;
                sharedPreferencesManager.setPreferenceValueInt(SharedPreferencesKeys.SORTINGMODEREPORTS, sortingMode);
                sortingTestingResults(sortingMode);
            }
        });
    }
    private void sortingTestingResults(final int sortMode){
        switch (sortMode){
            case 0:
                levelTxtView.setText("HIGH/LOW");
                break;
            case 1:
                levelTxtView.setText(Html.fromHtml("<u>HIGH</u>/LOW"));
                break;
            case 2:
                levelTxtView.setText(Html.fromHtml("HIGH/<u>LOW</u>"));
                break;
        }
        Collections.sort(selectedTestingResults, new Comparator<TestingResult>() {
            @Override
            public int compare(TestingResult testingResult1, TestingResult testingResult2) {
                if (sortMode == 0){ //Latest to Oldest
                    long time1 = testingResult1.getCurrentDate();
                    long time2 = testingResult2.getCurrentDate();
                    if (time1 > time2) {
                        return -1;
                    }
                    else if (time1 <  time2) {
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }else{
                    long mgdl1 = testingResult1.getMgdl();
                    long mgdl2 = testingResult2.getMgdl();
                    if (mgdl1 > mgdl2) {
                        return (sortMode == 1) ? -1 : 1;
                    }
                    else if (mgdl1 <  mgdl2) {
                        return (sortMode == 1) ? 1 : -1;
                    }
                    else {
                        return 0;
                    }
                }
            }
        });

        adapter.updateData(selectedTestingResults);
    }
    private void updateTestingResultList(){
        testingResultWithKey =  getDataParseTestingResults();
        Set<Integer> keys = testingResultWithKey.keySet();
        Integer[] keySet = keys.toArray(new Integer[keys.size()]);
        selectedTestingResults = testingResultWithKey.get(keySet[currentSelectedMonthIndex]);
        sortingTestingResults(sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SORTINGMODEREPORTS));

        showingHidingArrowsForMonthCycle();
    }
    public void updateTestingResultsSelected(int position, boolean isSelected){
        TestingResult testingResult = selectedTestingResults.get(position);
        testingResult.setSelected(isSelected);
        selectedTestingResults.set(position, testingResult);
        updateHeaderView();
    }
    private void onSharingReports(){
        if (!checkPermissions())
            return;

        ArrayList<TestingResult> sharingTestingResults = new ArrayList<TestingResult>();
        boolean isPatientsShared = false;

        Set<Integer> keySet= testingResultWithKey.keySet();
        Integer[] keys  = keySet.toArray(new Integer[keySet.size()]);
        for (Integer key : keys ){
            ArrayList<TestingResult> testingREsultsToParse = new ArrayList<TestingResult>();
            testingREsultsToParse = testingResultWithKey.get(key);
            for (TestingResult testingResults: testingREsultsToParse){
                if (testingResults.getSelected()){
                    isPatientsShared = true;
                    sharingTestingResults.add(testingResults);
                }
            }
        }
        if (!isPatientsShared){
            PrintUtil.showToast(getActivity(), getResources().getString(R.string.no_sharing));
        }else{
            String userName = "";
            if (currentUserVo.getFirstName().length() > 0){
                userName = currentUserVo.getFirstName().substring(0,1);
            }
            if (currentUserVo.getLastName().length() > 0){
                userName = userName  +  currentUserVo.getLastName().substring(0,1);
            }
            String pdfName = currentUserVo.getUserId() +"_"+ userName +"_"+ (int)(System.currentTimeMillis()/1000) + ".pdf";

            hud = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Sharing reports…");
            hud.show();
            PDFUtils.createPDFFromTestingResults(getActivity(), sharingTestingResults, Environment.getExternalStorageDirectory().getAbsolutePath()+"/SensingSugar/" + pdfName, currentUserVo,selectedPatient,  new PDFListener() {
                @Override
                public void createPDF(String path) {
                    hud.dismiss();
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, setUriForPDF(path));
                    shareIntent.setType("*/*");
                    startActivity(Intent.createChooser(shareIntent, ""));
                }
            });
        }
    }
    public Uri setUriForPDF(String pdfPath) {
        File pdfFile = new File(pdfPath);
        Uri pdfPathUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pdfPathUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", pdfFile);
        } else {
            pdfPathUri = Uri.fromFile(pdfFile);
        }
        return pdfPathUri;
    }
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT < 23)
            return true;

        String[] PERMISSIONS = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            final String perm = PERMISSIONS[i];
            if (ActivityCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(perm);
            }
        }

        if (permissionsNeeded.size() == 0)
            return true;

        requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), PermissionRequest.READ_WRITE_STORAGE);
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PermissionRequest.READ_WRITE_STORAGE:
                if (grantResults.length > 0) {
                    // All requested permissions must be granted.
                    boolean allPermissionsGranted = true;
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            allPermissionsGranted = false;
                            break;
                        }
                    }
                    if (allPermissionsGranted) {
                        onSharingReports();
                        return;
                    }
                }
                Toast.makeText(getActivity(), "External storage permission is necessary.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}