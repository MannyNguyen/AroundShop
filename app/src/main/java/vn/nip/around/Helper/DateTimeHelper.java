package vn.nip.around.Helper;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HOME on 8/17/2017.
 */

public class DateTimeHelper {
    public static String parseDateTime(LocalDateTime dateTime) {
        String retValue = "";
        try {
            int h = dateTime.getHourOfDay();
            String ampm = "AM";
            if (h == 0) {
                h = 12;
                ampm = "AM";
            } else if (h == 12) {
                h = 12;
                ampm = "PM";
            } else if (h > 12) {
                h = h - 12;
                ampm = "PM";
            } else {
                ampm = "AM";
            }
            String[] dayNames = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            retValue = StringUtils.leftPad(h + "", 2, "0") + ":" + StringUtils.leftPad(dateTime.getMinuteOfHour() + "", 2, "0") + " " + ampm + " | " +
                    dayNames[dateTime.getDayOfWeek() - 1] + ", " + monthNames[dateTime.getMonthOfYear() - 1] + " " + StringUtils.leftPad(dateTime.getDayOfMonth() + "", 2, "0") + ", " + dateTime.getYear();
            if (StorageHelper.getLanguage().equals("vi")) {
                dayNames = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
                monthNames = new String[]{"tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6", "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"};
                retValue = StringUtils.leftPad(h + "", 2, "0") + ":" + StringUtils.leftPad(dateTime.getMinuteOfHour() + "", 2, "0") + " " + ampm + " | " +
                        dayNames[dateTime.getDayOfWeek() - 1] + ", " + StringUtils.leftPad(dateTime.getDayOfMonth() + "", 2, "0") + " " + monthNames[dateTime.getMonthOfYear() - 1] + ", " + dateTime.getYear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }

    public static String parseDate(LocalDateTime dateTime) {
        String retValue = "";
        try {
            String[] dayNames = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
            String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            retValue = dayNames[dateTime.getDayOfWeek() - 1] + ", " + monthNames[dateTime.getMonthOfYear() - 1] + " " + StringUtils.leftPad(dateTime.getDayOfMonth() + "", 2, "0") + ", " + dateTime.getYear();
            if (StorageHelper.getLanguage().equals("vi")) {
                dayNames = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
                monthNames = new String[]{"tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6", "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"};
                retValue = dayNames[dateTime.getDayOfWeek() - 1] + ", " + StringUtils.leftPad(dateTime.getDayOfMonth() + "", 2, "0") + " " + monthNames[dateTime.getMonthOfYear() - 1] + ", " + dateTime.getYear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retValue;
    }

}
