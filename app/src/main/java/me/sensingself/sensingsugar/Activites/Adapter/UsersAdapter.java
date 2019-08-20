package me.sensingself.sensingsugar.Activites.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import me.sensingself.sensingsugar.Common.util.DateUtils;
import me.sensingself.sensingsugar.Common.util.GmtUtil;
import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.Patient;
import me.sensingself.sensingsugar.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by liujie on 1/31/18.
 */

public class UsersAdapter extends ArrayAdapter<Patient> {

    int resource;
    private Context context;
    private ArrayList<Patient> datas;

    boolean isSetIAgree = false;


    public UsersAdapter(Context _context, int _resource, ArrayList<Patient>  _items) {
        super(_context, _resource, _items);
        resource = _resource;
        context = _context;
        datas = _items;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Patient data = datas.get(position);
        if (data == null)
            return null;

        final UsersAdapter.ViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_patient, null);

            holder = new UsersAdapter.ViewHolder();
            holder.patientNameTxt = (TextView) view.findViewById(R.id.patientNameTxt);
            holder.patientNameTxt.setTypeface(FontUtility.getOfficinaSansCBold(view.getContext()));
            holder.patientBirthTxt = (TextView) view.findViewById(R.id.patientBirthTxt);
            holder.patientBirthTxt.setTypeface(FontUtility.getOfficinaSansCBook(view.getContext()));
            holder.testingDate = (TextView) view.findViewById(R.id.testingDateTxt);
            holder.testingDate.setTypeface(FontUtility.getOfficinaSansCBook(view.getContext()));
            holder.testingTime = (TextView) view.findViewById(R.id.testingTimeTxt);
            holder.testingTime.setTypeface(FontUtility.getOfficinaSansCBook(view.getContext()));
            view.setTag(holder);
        }else{
            holder = (UsersAdapter.ViewHolder) view.getTag();
        }


        holder.initHolder(data);

        return view;
    }
    public void updateData(ArrayList<Patient> patients) {
        ArrayList<Patient> users = new ArrayList<>();
        users.addAll(patients);
        this.datas.clear();
        this.datas.addAll(users);
        notifyDataSetChanged();
    }
    private class ViewHolder {
        TextView patientNameTxt;
        TextView patientBirthTxt;
        TextView testingDate;
        TextView testingTime;

        public void initHolder(final Patient data) {
            if (!data.getBirthday().equals("")){
                String [] dateParts = data.getBirthday().split("-");
                String year = "";
                String month = "";
                String day = "";
                if (dateParts.length > 2){
                    year = dateParts[0];
                    month = dateParts[1];
                    day = dateParts[2];
                    patientNameTxt.setText(DateUtils.string2StringDate(data.getBirthday(), "yyyy-MM-dd" ,"MM.dd.yyyy") + " (" + DateUtils.getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)) + " yrs)");
                }else{dateParts = data.getBirthday().split("/");
                    if (dateParts.length > 2){
                        year = dateParts[0];
                        month = dateParts[1];
                        day = dateParts[2];
                        patientNameTxt.setText(DateUtils.string2StringDate(data.getBirthday(), "yyyy/MM/dd" ,"MM.dd.yyyy") + " (" + DateUtils.getAge(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)) + " yrs)");
                    }
                }
            }else{
                patientNameTxt.setText(" yrs");
            }
            patientBirthTxt.setText("Nedumangad, Kerala");

            if (data.getTestdate() > 0){
                String testDate = GmtUtil.gmt0ToLocal(DateUtils.long2MinuteString(data.getTestdate()));
                String convertDate = DateUtils.string2StringDate(testDate, "yyyy-MM-dd HH:mm", "dd MMM");
                String dateArray[] = convertDate.split(" ");
                testingDate.setText(DateUtils.getDayOfMonthSuffix(Integer.parseInt(dateArray[0])) + ", " + dateArray[1]);

                testingTime.setText(DateUtils.string2StringDate(testDate, "yyyy-MM-dd HH:mm", "HH:mmaaa"));
            }else{
                testingDate.setText("");
                testingTime.setText("");
            }
        }

    }

}