package vn.nip.around.Bean;

import vn.nip.around.Class.CmmVariable;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viminh on 1/18/2017.
 */

public class BeanDate {
    private DateTime localDate;
    private boolean isActive;
    private String nameDay;
    private int day;
    private boolean isSelected;
    private int hour; // 1 - 12
    private int minute; // 0 - 59
    private int ampm; // 0 - 1

    public static JSONObject genaratesCurrent(long input) {
        DateTime dateTime = new DateTime(input);
        if (dateTime.getSecondOfMinute() != 0 && dateTime.getMinuteOfHour() != 0) {
            dateTime = new DateTime(input + 900000);
        }
        DateTime toDay = new DateTime();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            List<BeanDate> dates = new ArrayList<>();
            int currentMonth = toDay.getMonthOfYear();
            int currentDay = toDay.getDayOfMonth();
            int currentYear = toDay.getYear();
            int currentHour = dateTime.getHourOfDay();
            int currentMinute = dateTime.getMinuteOfHour();
            String[] dayNames = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

            DateTime start = toDay.withDayOfMonth(1);
            DateTime end = start.dayOfMonth().withMaximumValue();
            //LocalDate end = start.plusDays(7);

            while (!start.isAfter(end)) {
                BeanDate beanDate = new BeanDate();
                beanDate.setLocalDate(start);
                beanDate.setActive(false);
                if (start.getDayOfMonth() >= currentDay && start.getDayOfMonth() < currentDay + CmmVariable.maxDay) {
                    beanDate.setActive(true);
                }

                beanDate.setNameDay(dayNames[start.getDayOfWeek() - 1]);
                beanDate.setDay(start.getDayOfMonth());
                beanDate.setSelected(false);
                if (beanDate.getDay() == dateTime.getDayOfMonth()) {
                    beanDate.setActive(true);
                    beanDate.setSelected(true);
                }
                dates.add(beanDate);
                start = start.plusDays(1);
            }

            jsonObject.put("dates", dates);
            if (currentHour == 0) {
                jsonObject.put("hour", 12);
                jsonObject.put("ampm", 0);
            } else if (currentHour == 12) {
                jsonObject.put("hour", 12);
                jsonObject.put("ampm", 1);
            } else if (currentHour > 12) {
                jsonObject.put("hour", currentHour - 12);
                jsonObject.put("ampm", 1);
            } else {
                jsonObject.put("hour", currentHour);
                jsonObject.put("ampm", 0);
            }
            jsonObject.put("minute", currentMinute);
            jsonObject.put("month", currentMonth);
            jsonObject.put("month_name", monthNames[currentMonth - 1]);
            jsonObject.put("year", currentYear);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject genaratesNext(long input) {
        JSONObject jsonObject = new JSONObject();
        List<BeanDate> dates = new ArrayList<>();

        DateTime today = new DateTime();
        int currentHour = today.getHourOfDay();
        int currentMinute = today.getMinuteOfHour();

        DateTime selectDate = today.plusMonths(1).withDayOfMonth(1);
        if (input != 0) {
            selectDate = new DateTime(input);
            currentHour = selectDate.getHourOfDay();
            currentMinute = selectDate.getMinuteOfHour();
        }

        int index = today.dayOfMonth().getMaximumValue() - today.getDayOfMonth();
        if (index >= CmmVariable.maxDay) {
            index = 0;
        } else {
            index = CmmVariable.maxDay - index;
        }
        String[] dayNames = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        try {
            DateTime start = today.plusMonths(1).withDayOfMonth(1);
            DateTime end = start.dayOfMonth().withMaximumValue();
            boolean defaultSelected = false;
            while (!start.isAfter(end)) {
                BeanDate beanDate = new BeanDate();
                beanDate.setActive(false);
                beanDate.setSelected(false);
                if (start.getDayOfMonth() < index) {
                    beanDate.setActive(true);
                    if (!defaultSelected) {
                        if (input == 0) {
                            beanDate.setSelected(true);
                            defaultSelected = true;
                        } else {
                            if (start.getDayOfMonth() == selectDate.getDayOfMonth() && start.getMonthOfYear() == selectDate.getMonthOfYear() &&
                                    start.getYear() == selectDate.getYear()) {
                                beanDate.setSelected(true);
                                defaultSelected = true;
                            }
                        }

                    }
                    //Truong hợp ngày cần set lại lớn hơn khoảng 8 ngày
                }
                else if (start.getDayOfMonth() == index && !defaultSelected) {
                    beanDate.setActive(true);
                    beanDate.setSelected(true);
                    defaultSelected = true;
                }
                beanDate.setNameDay(dayNames[start.getDayOfWeek() - 1]);
                beanDate.setDay(start.getDayOfMonth());
                dates.add(beanDate);
                start = start.plusDays(1);
            }
            jsonObject.put("dates", dates);
            jsonObject.put("month", end.getMonthOfYear());
            jsonObject.put("month_name", monthNames[end.getMonthOfYear() - 1]);
            jsonObject.put("year", end.getYear());
            if (currentHour == 0) {
                jsonObject.put("hour", 12);
                jsonObject.put("ampm", 0);
            } else if (currentHour > 12) {
                jsonObject.put("hour", currentHour - 12);
                jsonObject.put("ampm", 1);
            } else {
                jsonObject.put("hour", currentHour);
                jsonObject.put("ampm", 0);
            }
            jsonObject.put("minute", currentMinute);
        } catch (Exception e) {

        }

        return jsonObject;
    }

    public static JSONObject generatesCurrentMonth() {
        JSONObject jsonObject = new JSONObject();
        List<BeanDate> dates = new ArrayList<>();
        LocalDate today = new LocalDate();
        int currentMonth = today.getMonthOfYear();
        int currentDay = today.getDayOfMonth();
        int currentYear = today.getYear();
        String[] dayNames = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        try {

            LocalDate start = today.withDayOfMonth(1);
            LocalDate end = start.dayOfMonth().withMaximumValue();
            //LocalDate end = start.plusDays(7);

            while (!start.isAfter(end)) {
                BeanDate beanDate = new BeanDate();
                //beanDate.setLocalDate(start);
                beanDate.setActive(false);
                if (start.getDayOfMonth() >= currentDay && start.getDayOfMonth() < currentDay + CmmVariable.maxDay) {
                    beanDate.setActive(true);
                }

                beanDate.setNameDay(dayNames[start.getDayOfWeek() - 1]);
                beanDate.setDay(start.getDayOfMonth());
                beanDate.setSelected(false);
                if (beanDate.getDay() == today.getDayOfMonth()) {
                    beanDate.setSelected(true);
                }
                dates.add(beanDate);
                start = start.plusDays(1);
            }
            jsonObject.put("dates", dates);
            jsonObject.put("month", currentMonth);
            jsonObject.put("month_name", monthNames[currentMonth - 1]);
            jsonObject.put("year", currentYear);
        } catch (Exception e) {

        }

        return jsonObject;
    }

    public static JSONObject generatesNextMonth() {
        JSONObject jsonObject = new JSONObject();
        List<BeanDate> dates = new ArrayList<>();
        LocalDate today = new LocalDate();
        int index = today.dayOfMonth().getMaximumValue() - today.getDayOfMonth();
        if (index >= CmmVariable.maxDay) {
            index = 0;
        } else {
            index = CmmVariable.maxDay - index;
        }
        String[] dayNames = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        try {

            LocalDate start = today.plusMonths(1).withDayOfMonth(1);
            LocalDate end = start.dayOfMonth().withMaximumValue();

            while (!start.isAfter(end)) {
                BeanDate beanDate = new BeanDate();
                //beanDate.setLocalDate(start);
                beanDate.setActive(false);
                if (start.getDayOfMonth() < index) {
                    beanDate.setActive(true);
                }
                beanDate.setNameDay(dayNames[start.getDayOfWeek() - 1]);
                beanDate.setDay(start.getDayOfMonth());
                beanDate.setSelected(false);
                dates.add(beanDate);
                start = start.plusDays(1);
            }
            jsonObject.put("dates", dates);
            jsonObject.put("month", end.getMonthOfYear());
            jsonObject.put("month_name", monthNames[end.getMonthOfYear() - 1]);
            jsonObject.put("year", end.getYear());
        } catch (Exception e) {

        }

        return jsonObject;
    }

    public static int getPositionSelected(List<BeanDate> list) {
        for (BeanDate bean : list) {
            if (bean.isSelected()) {
                return list.indexOf(bean);
            }
        }
        return -1;
    }

    public static void resetSelected(List<BeanDate> list) {
        for (BeanDate bean : list) {
            bean.setSelected(false);
        }
    }

    public DateTime getLocalDate() {
        return localDate;
    }

    public void setLocalDate(DateTime localDate) {
        this.localDate = localDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNameDay() {
        return nameDay;
    }

    public void setNameDay(String nameDay) {
        this.nameDay = nameDay;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
