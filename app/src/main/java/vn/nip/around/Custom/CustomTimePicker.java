package vn.nip.around.Custom;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import vn.nip.around.Interface.ICallback;
import vn.nip.around.Interface.IDateCallBack;

public class CustomTimePicker {
    Context context;
    private DateTime dateTime;

    public CustomTimePicker(Context context) {
        this.context = context;
        dateTime = new DateTime();
    }

    public CustomTimePicker(Context context, DateTime dateTime) {
        this.context = context;
        this.dateTime = dateTime;
    }

    public TimePickerDialog show(final IDateCallBack callBack) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                DateTime retValue = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), hourOfDay, minute);
                callBack.excute(retValue);
            }
        }, dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), false);

        timePickerDialog.show();
        return timePickerDialog;
    }

//    public TimePickerDialog show(final IDateCallBack callBack, final ICallback callback){
//        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int i, int i1) {
//                DateTime retValue = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), t);
//                callBack.excute(retValue);
//            }
//        },dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), dateTime.getSecondOfMinute());
//    }
}
