package vn.nip.around.Custom;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONObject;

import java.util.Date;

import javax.security.auth.callback.Callback;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.Interface.IDateCallBack;
import vn.nip.around.R;

public class CustomDatePicker {
    Context context;
    private DateTime dateTime;

    public CustomDatePicker(Context context) {
        this.context = context;
        dateTime = new DateTime();
    }

    public CustomDatePicker(Context context, DateTime dateTime) {
        this.context = context;
        this.dateTime = dateTime;
    }

    public DatePickerDialog show(final IDateCallBack callBack, final ICallback callback) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                DateTime retValue = new DateTime(i, i1 + 1, i2, dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
                callBack.excute(retValue);
            }
        }, dateTime.getYear(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth());

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel_schedule), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    try {
                        callback.excute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.scheduled),datePickerDialog );
        datePickerDialog.show();
        return datePickerDialog;
    }
}
