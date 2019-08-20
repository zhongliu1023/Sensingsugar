package me.sensingself.sensingsugar.Lib;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import me.sensingself.sensingsugar.R;

/**
 * Created by liujie on 2/15/18.
 */

public class DatePickerDialogPlus extends DatePickerDialog {
    private final DatePicker mDatePicker;
    private final OnDateSetListener mCallBack;

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DatePickerDialogPlus(Context context, int themeID, DatePickerDialog.OnDateSetListener callBack,
                                int year, int monthOfYear, int dayOfMonth) {
        super(context, themeID, callBack, year, monthOfYear, dayOfMonth);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE,
                themeContext.getText(R.string.datePicker_setButton), this);
        setButton(BUTTON_NEUTRAL,
                themeContext.getText(R.string.datePicker_clearButton), this);
        setButton(BUTTON_NEGATIVE,
                themeContext.getText(R.string.datePicker_cancelButton), this);
        setIcon(0);
        setTitle("");

        LayoutInflater inflater = (LayoutInflater)
                themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.datepickerdialog, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            if (which == BUTTON_POSITIVE) {
                mDatePicker.clearFocus();
                mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
                        mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            } else if (which == BUTTON_NEUTRAL) {
                mDatePicker.clearFocus();
                mCallBack.onDateSet(mDatePicker, 0, 0, 0);
            }
        }
    }
}
