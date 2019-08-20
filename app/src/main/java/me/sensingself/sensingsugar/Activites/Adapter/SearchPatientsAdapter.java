package me.sensingself.sensingsugar.Activites.Adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import me.sensingself.sensingsugar.Activites.Fragments.SearchTab.SearchResultsFragment;
import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by liujie on 2/15/18.
 */

public class SearchPatientsAdapter  extends ArrayAdapter<Patient> implements StickyListHeadersAdapter, SectionIndexer {

    private ArrayList<Patient> datesWithPatients;
    private String[] datesHeader;
    private int[] mSectionIndices;
    private int[] mSectionIDs;
    private LayoutInflater inflater;
    private Context context;
    int resource;
    private Patient currentPatient;
    private Fragment parentFragment;

    public SearchPatientsAdapter(Context _context, int _resource, ArrayList<Patient>  _items, Patient _patient) {
        super(_context, _resource, _items);
        resource = _resource;
        context = _context;
        datesWithPatients = _items;
        currentPatient = _patient;
        mSectionIndices = getDisctinctSectionIds();
        datesHeader = getSectionLetters();
        mSectionIDs = getSectionIds();
        inflater = LayoutInflater.from(context);
    }
    public void setParientFragment(Fragment fragment){
        this.parentFragment = fragment;
    }
    private int[] getDisctinctSectionIds() {
        if (datesWithPatients.size() == 0) return new int[0];
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        Patient onePatient = datesWithPatients.get(0);
        String lastFirstString = GmtUtil.gmt0ToLocalOnlyMonthYear(DateUtils.long2MinuteString(onePatient.getTestdate()));

        sectionIndices.add(0);
        for (int i = 1; i < datesWithPatients.size(); i++) {
            Patient otherObject = datesWithPatients.get(i);
            String compareString =  GmtUtil.gmt0ToLocalOnlyMonthYear(DateUtils.long2MinuteString(otherObject.getTestdate()));
            if (!compareString.equals(lastFirstString)) {
                lastFirstString = compareString;
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }
    private int[] getSectionIds() {
        if (datesWithPatients.size() == 0) return new int[0];
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        Patient oneObject = datesWithPatients.get(0);
        String  lastFirstString = GmtUtil.gmt0ToLocalOnlyMonthYear(DateUtils.long2MinuteString(oneObject.getTestdate()));
        int index = 1;
        sectionIndices.add(index);
        for (int i = 1; i < datesWithPatients.size(); i++) {
            Patient otherObject = datesWithPatients.get(i);
            String compareString = GmtUtil.gmt0ToLocalOnlyMonthYear(DateUtils.long2MinuteString(otherObject.getTestdate()));
            if (!compareString.equals(lastFirstString)) {
                index ++;
                lastFirstString = compareString;
            }
            sectionIndices.add(index);
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }
    private String[] getSectionLetters() {
        String[] letters = new String[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            Patient otherObject = datesWithPatients.get(mSectionIndices[i]);
            long compareString =  otherObject.getTestdate();
            String timer = GmtUtil.gmt0ToLocalOnlyMonthYear(DateUtils.long2MinuteString(compareString));
            letters[i] =timer;
        }
        return letters;
    }
    @Override
    public int getCount() {
        return datesWithPatients.size();
    }

    @Override
    public Patient getItem(int position) {
        return datesWithPatients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Patient patient = datesWithPatients.get(position);

        SearchPatientsAdapter.ViewHolder holder;
        if (convertView == null){

            convertView = inflater.inflate(R.layout.adapter_users, null);

            holder = new SearchPatientsAdapter.ViewHolder();
            holder.userFullName = (TextView) convertView.findViewById(R.id.userFullNameTxt);
            holder.userFullName.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.userBirthName = (TextView) convertView.findViewById(R.id.resultTestingTime);
            holder.userBirthName.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.userAddressTxt = (TextView) convertView.findViewById(R.id.brithdayTxtView);
            holder.userAddressTxt.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.patientAddress = (TextView) convertView.findViewById(R.id.patientAddress);
            holder.patientAddress.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            holder.resultCheckBox = (ImageView)convertView.findViewById(R.id.resutlCheckBox);
            holder.checkboxLayout = (ConstraintLayout)convertView.findViewById(R.id.checkboxLayout);
            convertView.setTag(holder);
        }else{
            holder = (SearchPatientsAdapter.ViewHolder) convertView.getTag();
        }
        final SearchPatientsAdapter.ViewHolder finalHolder = holder;
        holder.checkboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = patient.getSelected();
                patient.setSelected(!isSelected);
                notifyDataSetChanged();
                if (parentFragment instanceof SearchResultsFragment){
                    ((SearchResultsFragment)parentFragment).updateMultiPatients(position, 1, !isSelected);
                }
            }
        });
        holder.initHolder(patient);

        return convertView;
    }

    @Override
    public View getHeaderView(final int position, View convertView, ViewGroup parent) {
        Patient patient = datesWithPatients.get(position);
        final long date =patient.getTestdate();

        SearchPatientsAdapter.HeaderViewHolder headerViewHolder;

        if (convertView == null){
            convertView = inflater.inflate(R.layout.adapter_date_month, null);

            headerViewHolder = new SearchPatientsAdapter.HeaderViewHolder();
            headerViewHolder.monthTxtView = (TextView) convertView.findViewById(R.id.monthTxtView);
            headerViewHolder.monthTxtView.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            headerViewHolder.reportsTxtView = (TextView) convertView.findViewById(R.id.reportsTxtView);
            headerViewHolder.reportsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            headerViewHolder.levelOrReportsTxtView = (TextView) convertView.findViewById(R.id.levelOrReportsTxtView);
            headerViewHolder.levelOrReportsTxtView.setTypeface(FontUtility.getOfficinaSansCBook(convertView.getContext()));
            headerViewHolder.resultCheckBox = (ImageView)convertView.findViewById(R.id.resultCheckBox);
            convertView.setTag(headerViewHolder);
        }else{
            headerViewHolder = (SearchPatientsAdapter.HeaderViewHolder) convertView.getTag();
        }
        final SearchPatientsAdapter.HeaderViewHolder finalHolder = headerViewHolder;
        headerViewHolder.resultCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countInCurrentHeader = getCountReports(position);
                boolean isAllSelected = finalHolder.isAllSelected;

                for (int i = 0; i < countInCurrentHeader; i ++){
                    Patient patient = datesWithPatients.get(i + position);
                    patient.setSelected(!isAllSelected);
                    datesWithPatients.set(i + position, patient);
                }
                updateData(datesWithPatients);

                if (parentFragment instanceof SearchResultsFragment){
                    ((SearchResultsFragment)parentFragment).updateMultiPatients(position, countInCurrentHeader, !isAllSelected);
                }
            }
        });

        String timer = GmtUtil.gmt0ToLocalOnlyMonthYear(DateUtils.long2MinuteString(date));
        headerViewHolder.initHolder(timer, "" + getCountReports(position), 1, position);

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return mSectionIDs[position];
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return datesHeader;
    }

    class HeaderViewHolder {
        TextView levelOrReportsTxtView,reportsTxtView, monthTxtView;
        ImageView resultCheckBox;
        boolean isAllSelected = true;
        public void initHolder(final String date, final String reports, final int type, int startPosition) {
            monthTxtView.setText(Html.fromHtml(" <b>" + getMonthName(date) + "</b>" + " " + getYearNumber(date)));
            if (type == 1){
                reportsTxtView.setVisibility(View.INVISIBLE);
                levelOrReportsTxtView.setText(reports + " REPORTS");
            }else{
                reportsTxtView.setVisibility(View.VISIBLE);
                reportsTxtView.setText(reports + " REPORTS");
                levelOrReportsTxtView.setText("HIGH / LOW");
            }

            boolean isExistSection = false;
            for (int i = 0; i < mSectionIndices.length; i++) {
                if (startPosition == mSectionIndices[i]) {
                    isExistSection = true;
                    break;
                }
            }
            if (!isExistSection) return;

            isAllSelected = true;
            for (int i = 0; i <getCountReports(startPosition); i ++){
                Patient patient = datesWithPatients.get(i + startPosition);
                if (!patient.getSelected()){
                    isAllSelected = false;
                    break;
                }
            }

            if (isAllSelected){
                resultCheckBox.setImageResource(R.drawable.check_agree);
            }else{
                resultCheckBox.setImageResource(R.drawable.uncheck_agree);
            }

        }
    }

    class ViewHolder {
        TextView userFullName;
        TextView userAddressTxt;
        TextView userBirthName;
        TextView patientAddress;
        ImageView resultCheckBox;
        ConstraintLayout checkboxLayout;

        public void initHolder(final Patient patient) {
            userFullName.setText(Html.fromHtml(patient.getFirstName() + " <b>" + patient.getLastName() + "</b>"));
            userAddressTxt.setText(patient.getDistrict() + " " + patient.getState());
            if (!patient.getBirthday().equals("")){
                userBirthName.setText(DateUtils.string2StringDate(patient.getBirthday(), "yyyy-MM-dd", "dd.MM.yyyy")  );
            }else{
                userBirthName.setText("");
            }
            if (patient.getSelected()){
                resultCheckBox.setImageResource(R.drawable.check_agree);
            }else{
                resultCheckBox.setImageResource(R.drawable.uncheck_agree);
            }
        }
    }
    private String getMonthName(String date) {
        Date d = null;
        String monthName = "";
        try {
            d = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return monthName;
    }
    private int getYearNumber(String date) {
        Date year = null;
        int yearNum = 0;
        try {
            year = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(year);
            yearNum = cal.get(Calendar.YEAR);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return yearNum;
    }

    public void updateData(ArrayList<Patient> datas) {
        ArrayList<Patient> patients = new ArrayList<>();
        patients.addAll(datas);
        this.datesWithPatients.clear();
        this.datesWithPatients.addAll(patients);
        mSectionIndices = getDisctinctSectionIds();
        datesHeader = getSectionLetters();
        mSectionIDs = getSectionIds();
        notifyDataSetChanged();
    }

    private int getCountReports(int position){
        int countInCurrentHeader = 0;
        if (mSectionIDs[position] == mSectionIndices.length){
            countInCurrentHeader = datesWithPatients.size() - mSectionIndices[mSectionIDs[position] - 1];
        }else{
            countInCurrentHeader = mSectionIndices[mSectionIDs[position]] - mSectionIndices[mSectionIDs[position] - 1];
        }
        return countInCurrentHeader;
    }
}