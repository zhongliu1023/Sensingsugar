package me.sensingself.sensingsugar.Activites.Fragments.ReportsTab;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import info.hoang8f.android.segmented.SegmentedGroup;
import me.sensingself.sensingsugar.Activites.Adapter.SearchReportsAdapter;
import me.sensingself.sensingsugar.Activites.Fragments.TestTab.SeedTestReportFragment;
import me.sensingself.sensingsugar.Common.Interfaces.PDFListener;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesKeys;
import me.sensingself.sensingsugar.Common.sharedpreferences.SharedPreferencesManager;
import me.sensingself.sensingsugar.Common.util.CalendarUtil;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.FragmentPopKeys;
import me.sensingself.sensingsugar.Common.util.FragmentmanagerUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Common.util.PDFUtils;
import me.sensingself.sensingsugar.Common.util.PrintUtil;
import me.sensingself.sensingsugar.Common.util.UsersManagement;
import me.sensingself.sensingsugar.Common.webutil.APICallbacks;
import me.sensingself.sensingsugar.Constants.PermissionRequest;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.Model.UserVo;
import me.sensingself.sensingsugar.R;
import me.sensingself.sensingsugar.webutil.AccountClient;

/**
 * Created by liujie on 1/12/18.
 */

public class ReportsFragment extends Fragment {
    public static final String ARG_OBJECT = "object";//testing
    private SharedPreferencesManager sharedPreferencesManager;

    private SegmentedGroup dateSegmentGroup;
    private RadioButton daySegment, weekSegment, monthSegment;
    private TextView reportsTitle, shareTxtView;
    private ImageView resultCheckBox;
    private ImageButton cyclePreMonth, cycleNextMonth;
    private TextView monthTxtView, reportsTxtView, levelTxtView, dateTxtView, weekTxtView;
    private TwoWayView monthCycleView, weekCycleView;
    private ConstraintLayout dayCycleLayout, dayConstraint, weekCycleLayout, monthCycleLayout;
    private TextView morningTxt, afternoonTxt, eveningTxt;
    private ConstraintLayout reportsConstraint;
    private RelativeLayout noReportsLayout;
    private TextView noReportsTxtView;

    private ListView searchedResultListView;
    SearchReportsAdapter adapter;

    ArrayList<Patient> patients = new ArrayList<Patient>();

    Map<Integer, ArrayList<Patient>> testingResultWithKey = new ArrayMap<>();
    ArrayList<Patient> patientsGroupedByMonth;
    ArrayList<Patient> selectedTestingResults = new ArrayList<Patient>();
    int currentSelectedMonthIndex = 0;
    boolean isSelectedAll = false;

    static Patient selectedPatient = new Patient();

    private AccountClient accountClient;

    int showType = 1;
    MyMonthCycleAdapter weekCycleAdapter;
    MyMonthCycleAdapter monthCycleAdapter;

    FragmentManager fragmentManager;
    UserVo currentUserVo = new UserVo();
    private KProgressHUD hud;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_tab, container, false);


        accountClient = new AccountClient();
        sharedPreferencesManager = SharedPreferencesManager.getInstance(getActivity());
        fragmentManager = FragmentmanagerUtils.getFragmentManagerReports();

        initNewUserVo();
        initController(rootView);
        initFontAndText(rootView);
        setListener(rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        apiCallSeachPatients();
    }

    public void onReloadReports(){
        apiCallSeachPatients();
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
    private void initController(View rootView) {
        dateSegmentGroup = (SegmentedGroup) rootView.findViewById(R.id.dateSegmentGroup);
        daySegment = (RadioButton)rootView.findViewById(R.id.daySegment);
        weekSegment = (RadioButton)rootView.findViewById(R.id.weekSegment);
        monthSegment = (RadioButton)rootView.findViewById(R.id.monthSegment);
        dateSegmentGroup.check(0);
        daySegment.setChecked(true);
        reportsTitle = (TextView) rootView.findViewById(R.id.reports_title_text);
        shareTxtView = (TextView) rootView.findViewById(R.id.shareTxtView);

        resultCheckBox = (ImageView) rootView.findViewById(R.id.resultCheckBox);
        resultCheckBox.setImageResource(R.drawable.uncheck_agree);
        cyclePreMonth = (ImageButton) rootView.findViewById(R.id.cyclePreMonth);
        cyclePreMonth.setImageResource(R.drawable.cycle_left_arrow);
        cycleNextMonth = (ImageButton) rootView.findViewById(R.id.cycleNextMonth);
        cycleNextMonth.setImageResource(R.drawable.cycle_right_arrow);
        monthTxtView = (TextView) rootView.findViewById(R.id.monthTxtView);
        reportsTxtView = (TextView) rootView.findViewById(R.id.reportsTxtView);
        levelTxtView = (TextView) rootView.findViewById(R.id.levelTxtView);
        dateTxtView = (TextView) rootView.findViewById(R.id.dateTxtView);
        weekTxtView = (TextView) rootView.findViewById(R.id.weekTxtView);
        morningTxt = (TextView) rootView.findViewById(R.id.morningTxt);
        afternoonTxt = (TextView) rootView.findViewById(R.id.afternoonTxt);
        eveningTxt = (TextView) rootView.findViewById(R.id.eveningTxt);

        reportsConstraint = (ConstraintLayout) rootView.findViewById(R.id.reportsConstraint);
        noReportsLayout = (RelativeLayout) rootView.findViewById(R.id.noReportsLayout);
        noReportsTxtView = (TextView) rootView.findViewById(R.id.noReportsTxtView);

        dayCycleLayout = (ConstraintLayout) rootView.findViewById(R.id.dayCycleLayout);
        dayConstraint = (ConstraintLayout) rootView.findViewById(R.id.dayConstraint);

        weekCycleLayout = (ConstraintLayout) rootView.findViewById(R.id.weekCycleLayout);
        weekCycleLayout.setVisibility(View.GONE);
        monthCycleLayout = (ConstraintLayout) rootView.findViewById(R.id.monthCycleLayout);
        monthCycleLayout.setVisibility(View.GONE);

        searchedResultListView = (ListView) rootView.findViewById(R.id.resultsList);
        searchedResultListView.setDivider(null);

        adapter = new SearchReportsAdapter(getActivity(), R.layout.adapter_date_month, selectedTestingResults);
        adapter.setParientFragment(this);
        searchedResultListView.setAdapter(adapter);
        searchedResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Patient patient = selectedTestingResults.get(position);
                TestingResult testingResult = patient.getTestingResults().get(0);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.note_root_frame, SeedTestReportFragment.newInstance(3, "", patient, testingResult), "Post")
                        .addToBackStack(FragmentPopKeys.REPORTSFRAGMENT)
                        .commit();
                FragmentmanagerUtils.setFragmentManagerReports(fragmentManager);
            }
        });

        monthCycleView = (TwoWayView) rootView.findViewById(R.id.monthCycleView);
        String[] institutionDays = {"1", "2", "3"};
        monthCycleAdapter = new MyMonthCycleAdapter(
                getActivity(),
                R.layout.day_list_item,
                institutionDays
        );
        monthCycleView.setAdapter(monthCycleAdapter);
        monthCycleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        weekCycleView = (TwoWayView) rootView.findViewById(R.id.weekCycleView);
        weekCycleAdapter = new MyMonthCycleAdapter(
                getActivity(),
                R.layout.day_list_item,
                institutionDays
        );
        weekCycleView.setAdapter(weekCycleAdapter);
        weekCycleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });


        cyclePreMonth.setVisibility(View.INVISIBLE);
        cycleNextMonth.setVisibility(View.INVISIBLE);
    }
    private void initFontAndText(View rootView) {
        reportsTitle.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        shareTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        daySegment.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weekSegment.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        monthSegment.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        monthTxtView.setTypeface(FontUtility.getOfficinaSansCBold(rootView.getContext()));
        reportsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        levelTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        dateTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        weekTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        morningTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        afternoonTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        eveningTxt.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
        noReportsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(rootView.getContext()));
    }
    private void setListener(final View rootView) {
        reportsTitle.setText(getResources().getString(R.string.reports));

        shareTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSharingReports();
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
        resultCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedAll = !isSelectedAll;
                for (int i = 0 ; i < selectedTestingResults.size() ; i++){
                    Patient testingResult = selectedTestingResults.get(i);
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
        dateSegmentGroup.setOnCheckedChangeListener(new SegmentedGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.daySegment:
                        showType = 1;
                        dayConstraint.setVisibility(View.VISIBLE);
                        weekCycleLayout.setVisibility(View.GONE);
                        monthCycleLayout.setVisibility(View.GONE);
                        break;
                    case R.id.weekSegment:
                        showType = 2;
                        dayConstraint.setVisibility(View.GONE);
                        weekCycleLayout.setVisibility(View.VISIBLE);
                        monthCycleLayout.setVisibility(View.GONE);
                        break;
                    case R.id.monthSegment:
                        showType = 3;
                        dayConstraint.setVisibility(View.GONE);
                        weekCycleLayout.setVisibility(View.GONE);
                        monthCycleLayout.setVisibility(View.VISIBLE);
                        break;
                }
                initPatientsAndTestingResults();
                updateTestingResultList();
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
        Collections.sort(selectedTestingResults, new Comparator<Patient>() {
            @Override
            public int compare(Patient entry1, Patient entry2) {
                TestingResult testingResult1 =  entry1.getTestingResults().get(0);
                TestingResult testingResult2 =  entry2.getTestingResults().get(0);

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
    private void updateWeekCycleAdapter(int weekNumber){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
        List<String> daysOfWeek =  CalendarUtil.getStartOFWeekInYear(weekNumber%100, (int)(weekNumber/100), formatter);
        String[] institutionDays = new String[daysOfWeek.size()];
        for (int i = 0 ; i < daysOfWeek.size() ; i ++){
            institutionDays[i] = daysOfWeek.get(i);
        }
        weekCycleAdapter.updateMonthCycle(institutionDays);
    }
    private void updateMonthCycleAdapter(int monthNumber){
        int dayOfMonth = CalendarUtil.getDaysInMonth(monthNumber%100, (int)(monthNumber/100));
        String[] institutionDays = new String[dayOfMonth];
        for (int i = 1 ; i <= dayOfMonth ; i ++){
            institutionDays[i-1] = DateUtils.getDayOfMonthSuffix(i);
        }
        monthCycleAdapter.updateMonthCycle(institutionDays);
    }
    private Map<Integer, ArrayList<Patient>> getDataParseReports(int type){
        Map<Integer, ArrayList<Patient>> results = new ArrayMap<>();
        for (Patient patient: patients){
            for (TestingResult testingResult : patient.getTestingResults()){
                int numberKey = 0;
                switch (type){
                    case 1:
                        numberKey = getDay(testingResult.getCurrentDate());
                        break;
                    case 2:
                        numberKey = getWeek(testingResult.getCurrentDate());
                        break;
                    case 3:
                        numberKey = getMonth(testingResult.getCurrentDate());
                        break;
                }
                if (results.containsKey(numberKey)) {
                    patientsGroupedByMonth.add(patient);
                    results.put(numberKey, patientsGroupedByMonth);
                } else {
                    patientsGroupedByMonth = new ArrayList<Patient>();
                    patientsGroupedByMonth.add(patient);
                    results.put(numberKey, patientsGroupedByMonth);
                }
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
    private int getDay(long timeStamp) {
        String time = DateUtils.string2StringDate(GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(timeStamp)), "yyyy-MM-dd HH:mm", "yyyy-MM-dd");
        String[] dayDate = time.split("-");
        int day = Integer.parseInt(dayDate[0] + dayDate[1] + dayDate[2]);
        return day;
    }
    private int getWeek(long timeStamp) {
        String time = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(timeStamp));
        String[] splitDate = time.split("-");
        int weekN = CalendarUtil.findWeekNumber(time);
        int currentYear = Integer.parseInt(splitDate[0]);
        if (weekN == 1){
            int currentMonth = CalendarUtil.getMonth(time);
            if (currentMonth != 0){
                currentYear ++;
            }
        }
        int weekNumber = 0;
        if (weekN < 10) {
            weekNumber = Integer.parseInt(currentYear + "0" + weekN);
        } else {
            weekNumber = Integer.parseInt(currentYear +""+ weekN);
        }
        return weekNumber;
    }
    private void updateHeaderView(){

        Set<Integer> keys = testingResultWithKey.keySet();
        Integer[] keySet = keys.toArray(new Integer[keys.size()]);
        Integer currentMonth = keySet[currentSelectedMonthIndex];
        switch (showType){
            case 1:
                monthTxtView.setText(intDay2String(currentMonth));
                break;
            case 2:
                monthTxtView.setText(intWeek2String(currentMonth));
                updateWeekCycleAdapter(currentMonth);
                break;
            case 3:
                monthTxtView.setText(intMonth2String(currentMonth));
                updateMonthCycleAdapter(currentMonth);
                break;
        }
        reportsTxtView.setText(selectedTestingResults.size() + " REPORTS");

        isSelectedAll = true;
        for (int i = 0 ; i < selectedTestingResults.size() ; i++){
            Patient testingResult = selectedTestingResults.get(i);
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
    private String intDay2String(Integer day){
        return DateUtils.string2StringDate(String.valueOf(day), "yyyyMMdd", "dd MMM yyyy");
    }
    private String intWeek2String(Integer week){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyy");
        List<String> daysOfWeek =  CalendarUtil.getStartEndOFWeek(week%100, (int)(week/100), formatter);
        String[] splitStartDayOfWeek = daysOfWeek.get(0).split(" ");
        String[] splitEndDayOfWeek= daysOfWeek.get(1).split(" ");
        String returnWeek = "";
        if (!splitStartDayOfWeek[1].equals(splitEndDayOfWeek[1])){ // same month?
            if (!splitStartDayOfWeek[2].equals(splitEndDayOfWeek[2])) // same year?
                returnWeek = splitStartDayOfWeek[0] + " " + splitStartDayOfWeek[1] + " " + splitStartDayOfWeek[2] + "-" + splitEndDayOfWeek[0] + " " + splitEndDayOfWeek[1] + " " + splitEndDayOfWeek[2];
            else
                returnWeek = splitStartDayOfWeek[0] + " " + splitStartDayOfWeek[1] + "-" + splitEndDayOfWeek[0] + " " + splitEndDayOfWeek[1] + " " + splitEndDayOfWeek[2];
        }else{
            returnWeek = splitStartDayOfWeek[0] + " " + "-" + splitEndDayOfWeek[0] + " " + splitEndDayOfWeek[1] + splitEndDayOfWeek[2];
        }
        return returnWeek;
    }
    private String intMonth2String(Integer month){
        return DateUtils.string2StringDate(String.valueOf(month), "yyyyMM", "MMM yyyy");
    }

    private String getDateFromTimeStamp(long timeStamp){
        String time = GmtUtil.localToGmt0AfterCheckingTimeStamp(timeStamp);
        return DateUtils.string2StringDate(time, "yyyy-MM-dd HH:mm", "dd.MM.yyyy");
    }




    private void initPatientsAndTestingResults(){
        String currentPatientStr = sharedPreferencesManager.getPreferenceValueString(SharedPreferencesKeys.SEARCHEDREPORTS);
        if (!currentPatientStr.equals("")) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Patient>>() {
            }.getType();
            patients = gson.fromJson(currentPatientStr, type);
        }

        testingResultWithKey =  getDataParseReports(showType);
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
    private void updateTestingResultList(){
        testingResultWithKey =  getDataParseReports(showType);
        Set<Integer> keys = testingResultWithKey.keySet();
        Integer[] keySet = keys.toArray(new Integer[keys.size()]);
        selectedTestingResults = testingResultWithKey.get(keySet[currentSelectedMonthIndex]);
        sortingTestingResults(sharedPreferencesManager.getPreferenceValueInt(SharedPreferencesKeys.SORTINGMODEREPORTS));

        showingHidingArrowsForMonthCycle();
    }
    public void updateTestingResultsSelected(int position, boolean isSelected){
        Patient patient = selectedTestingResults.get(position);
        patient.setSelected(isSelected);
        selectedTestingResults.set(position, patient);
        updateHeaderView();
    }
    private  void apiCallSeachPatients(){
        accountClient.getReports(getActivity(), sharedPreferencesManager, new APICallbacks() {
            @Override
            public <E> void onSuccess(E responseObject, int statusCode, String errorMessage) {
                ArrayList<Patient> searchedReports = new ArrayList<Patient>();
                searchedReports = (ArrayList<Patient>) responseObject;
                if (searchedReports.size() == 0){
                    onSearchCompleted(statusCode, errorMessage, false);
                }else{
                    UsersManagement.saveReports(searchedReports, sharedPreferencesManager);
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
        if (isReturn) {
            reportsConstraint.setVisibility(View.VISIBLE);
            shareTxtView.setVisibility(View.VISIBLE);
            noReportsLayout.setVisibility(View.GONE);

            initPatientsAndTestingResults();
            updateTestingResultList();
        }else{
            reportsConstraint.setVisibility(View.GONE);
            shareTxtView.setVisibility(View.GONE);
        }
    }

    public void onSearchFailed(int errorCode, String errorMessage) {
        reportsConstraint.setVisibility(View.GONE);
        shareTxtView.setVisibility(View.GONE);
    }

    private static class MyMonthCycleAdapter extends ArrayAdapter<String> {
        String[] daysOfMonth;
        private MyMonthCycleAdapter(Context context, int resource, String[] items) {
            super(context, resource, items);
            this.daysOfMonth = new String[items.length];
            this.daysOfMonth = items;
        }
        @Override
        public int getCount() {
            return daysOfMonth.length;
        }
        @Override
        public String getItem(int position) {
            return daysOfMonth[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setText(daysOfMonth[position]);
            view.setTypeface(FontUtility.getOfficinaSansCBook(getContext()));
            return view;
        }
        public void updateMonthCycle(String[] items){
            this.daysOfMonth = new String[items.length];
            daysOfMonth = items;
            notifyDataSetChanged();
        }
    }

    private void onSharingReports(){
        if (!checkPermissions())
            return;

        ArrayList<Patient> sharingPatients = new ArrayList<Patient>();
        boolean isPatientsShared = false;

        Set<Integer> keySet= testingResultWithKey.keySet();
        Integer[] keys  = keySet.toArray(new Integer[keySet.size()]);
        for (Integer key : keys ){
            ArrayList<Patient> patientsToParse = new ArrayList<Patient>();
            patientsToParse = testingResultWithKey.get(key);
            for (Patient patient: patientsToParse){
                if (patient.getSelected()){
                    isPatientsShared = true;
                    sharingPatients.add(patient);
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
            PDFUtils.createPDFFromPatients(getActivity(), sharingPatients, Environment.getExternalStorageDirectory().getAbsolutePath()+"/SensingSugar/" + pdfName, currentUserVo, true, new PDFListener() {
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
