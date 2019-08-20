package me.sensingself.sensingsugar.Activites.Adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.sensingself.sensingsugar.Activites.Fragments.ReportsTab.ReportsFragment;
import me.sensingself.sensingsugar.Activites.Fragments.SearchTab.SearchReportsForOnePatient;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Constants.DiabetesCheckingValues;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.Model.TestingResult;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 2/28/18.
 */

public class SearchReportsAdapter   extends ArrayAdapter<Patient> {

    private ArrayList<Patient> datesWithResults;
    private LayoutInflater inflater;
    private Context context;
    int resource;
    private Fragment parentFragment;

    public SearchReportsAdapter(Context _context, int _resource, ArrayList<Patient>  _items) {
        super(_context, _resource, _items);
        resource = _resource;
        context = _context;
        datesWithResults = _items;
        inflater = LayoutInflater.from(context);
    }
    public void setParientFragment(Fragment fragment){
        this.parentFragment = fragment;
    }

    @Override
    public int getCount() {
        return datesWithResults.size();
    }

    @Override
    public Patient getItem(int position) {
        return datesWithResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Patient patient = datesWithResults.get(position);

        SearchReportsAdapter.ViewHolder holder;
        if (convertView == null){

            convertView = inflater.inflate(R.layout.adapter_testingresult, null);

            holder = new SearchReportsAdapter.ViewHolder();
            holder.testingTimeTxt = (TextView) convertView.findViewById(R.id.testingTimeTxt);
            holder.testingTimeTxt.setTypeface(FontUtility.getOfficinaSansCBold(convertView.getContext()));
            holder.testingTypeTxt = (TextView) convertView.findViewById(R.id.testingTypeTxt);
            holder.testingTypeTxt.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.mgdlTxt = (TextView) convertView.findViewById(R.id.mgdlTxt);
            holder.mgdlTxt.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.textView.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.mmolTxt = (TextView) convertView.findViewById(R.id.mmolTxt);
            holder.mmolTxt.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.textView2 = (TextView) convertView.findViewById(R.id.textView2);
            holder.textView2.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.resultCheckBox = (ImageView)convertView.findViewById(R.id.resutlCheckBox);
            holder.checkBoxConstraint = (ConstraintLayout)convertView.findViewById(R.id.checkBoxConstraint);
            convertView.setTag(holder);
        }else{
            holder = (SearchReportsAdapter.ViewHolder) convertView.getTag();
        }
        final SearchReportsAdapter.ViewHolder finalHolder = holder;
        final Patient finalPatient = patient;
        holder.checkBoxConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = finalPatient.getSelected();
                isSelected = !isSelected;
                finalPatient.setSelected(isSelected);
                datesWithResults.set(position, finalPatient);
                updateData(datesWithResults);

                if (parentFragment instanceof ReportsFragment){
                    ((ReportsFragment)parentFragment).updateTestingResultsSelected(position, isSelected);
                }
            }
        });
        holder.initHolder(patient);

        return convertView;
    }

    class ViewHolder {
        TextView testingTimeTxt;
        TextView testingTypeTxt;
        TextView mgdlTxt;
        TextView mmolTxt;
        TextView textView;
        TextView textView2;
        ImageView resultCheckBox;
        ConstraintLayout checkBoxConstraint;
        private void changeColorOfTestingResult(int colorID){
            mgdlTxt.setTextColor(colorID);
            textView.setTextColor(colorID);
        }
        public void initHolder(final Patient data) {
            TestingResult testingResult = data.getTestingResults().get(0);
            if (testingResult.getBloodSugarType().equals("BB")){
                testingTypeTxt.setText(context.getString(R.string.fasting_blood_sugar));
                if (testingResult.getMgdl() >= DiabetesCheckingValues.FSDIABETESMIN){
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResultDark));
                }else if (testingResult.getMgdl() < DiabetesCheckingValues.FSDIABETESMIN && testingResult.getMgdl() >=  DiabetesCheckingValues.FSPREDIABETESMIN){
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResult));
                }else {
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResultLight));
                }
            }else if (testingResult.getBloodSugarType().equals("PM")){
                testingTypeTxt.setText(context.getString(R.string.postprandial_blood_sugar));

                if (testingResult.getMgdl() >= DiabetesCheckingValues.PMSDIABETESMIN){
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResultDark));
                }else if (testingResult.getMgdl() < DiabetesCheckingValues.PMSDIABETESMIN && testingResult.getMgdl() >= DiabetesCheckingValues.PMSPREDIABETESMIN){
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResult));
                }else {
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResultLight));
                }
            }else {
                testingTypeTxt.setText(context.getString(R.string.random_blood_sugar));
                if (testingResult.getMgdl() >= DiabetesCheckingValues.RSDIABETESMIN){
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResultDark));
                }else {
                    changeColorOfTestingResult(context.getResources().getColor(R.color.colorResultLight));
                }
            }
            mgdlTxt.setText(Long.toString(testingResult.getMgdl()));
            mmolTxt.setText(Float.toString(testingResult.getMmol()));

            long timestamp = testingResult.getCurrentDate();
            String timer = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(timestamp));

            String convertDate = DateUtils.string2StringDate(timer, "yyyy-MM-dd hh:mm", "dd, hh:mm");
            String dateArray[] = convertDate.split(", ");
            testingTimeTxt.setText(DateUtils.getDayOfMonthSuffix(Integer.parseInt(dateArray[0])) + ", " + dateArray[1]);


            if (data.getSelected()){
                resultCheckBox.setImageResource(R.drawable.check_agree);
            }else{
                resultCheckBox.setImageResource(R.drawable.uncheck_agree);
            }
        }
    }
    
    public void updateData(ArrayList<Patient> datas) {
        ArrayList<Patient> testingResults = new ArrayList<>();
        testingResults.addAll(datas);
        this.datesWithResults.clear();
        this.datesWithResults.addAll(testingResults);
        notifyDataSetChanged();
    }
}