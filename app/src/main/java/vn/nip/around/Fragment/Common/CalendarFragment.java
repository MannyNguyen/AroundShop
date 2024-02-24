package vn.nip.around.Fragment.Common;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import vn.nip.around.Adapter.CalendarAdapter;
import vn.nip.around.Bean.BeanDate;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends BaseFragment implements View.OnClickListener {
    public ImageButton backMonth, nextMonth;
    public TextView monthCalendar;
    public TextView yearCalendar;
    public NumberPicker hourClock, minuteClock, ampmClock;
    public JSONObject dates;
    ImageButton isSchedule;
    public boolean isEnable;
    FrameLayout enableDelivery;
    RecyclerView recyclerCalendar;

    public CalendarFragment() {
        // Required empty public constructor
        Bundle bundle = new Bundle();
        bundle.putLong("delivery_time", 0);
        setArguments(bundle);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_calendar, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (getArguments().getLong("delivery_time") != 0) {
                long deliveryTime = getArguments().getLong("delivery_time");
                DateTime setDateTime = new DateTime(deliveryTime);
                DateTime today = new DateTime();
                if (setDateTime.getMonthOfYear() > today.getMonthOfYear()) {
                    dates = BeanDate.genaratesNext(deliveryTime);
                    nextMonth.setVisibility(View.GONE);
                    backMonth.setVisibility(View.VISIBLE);
                } else {
                    dates = BeanDate.genaratesCurrent(deliveryTime);
                    nextMonth.setVisibility(View.VISIBLE);
                    backMonth.setVisibility(View.GONE);
                }

                createCalendar(dates);
                enableDelivery.setVisibility(View.GONE);
                if (StorageHelper.getLanguage().equals("vi")) {
                    isSchedule.setImageDrawable(getResources().getDrawable(R.drawable.v_delivery_on));
                } else {
                    isSchedule.setImageDrawable(getResources().getDrawable(R.drawable.e_delivery_on));
                }
            }

        } catch (Exception e) {

        }
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                recyclerCalendar = (RecyclerView) getView().findViewById(R.id.recycler_calendar);
                enableDelivery = (FrameLayout) getView().findViewById(R.id.enable_delivery);
                backMonth = (ImageButton) getView().findViewById(R.id.back_month);
                backMonth.setOnClickListener(CalendarFragment.this);
                nextMonth = (ImageButton) getView().findViewById(R.id.next_month);
                nextMonth.setOnClickListener(CalendarFragment.this);
                monthCalendar = (TextView) getView().findViewById(R.id.month_calendar);
                yearCalendar = (TextView) getView().findViewById(R.id.year_calendar);
                isSchedule = (ImageButton) getView().findViewById(R.id.is_schedule);
                ampmClock = (NumberPicker) getView().findViewById(R.id.ampm);
                ampmClock.setMinValue(0);
                ampmClock.setMaxValue(1);
                ampmClock.setDisplayedValues(new String[]{"AM", "PM"});
                hourClock = (NumberPicker) getView().findViewById(R.id.hour_clock);
                hourClock.setMinValue(1);
                hourClock.setMaxValue(12);
                hourClock.setWrapSelectorWheel(true);
                minuteClock = (NumberPicker) getView().findViewById(R.id.minute_clock);
                minuteClock.setMinValue(0);
                minuteClock.setMaxValue(3);
                minuteClock.setDisplayedValues(new String[]{"00", "15", "30", "45"});
                minuteClock.setWrapSelectorWheel(true);


                setSourceSchedule(isSchedule, isEnable);
                isSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //nhấn để active
                        if (enableDelivery.getVisibility() == View.VISIBLE) {
                            Tracking.excute("C6.1N");
                            enableDelivery.setVisibility(View.GONE);
                            backMonth.setVisibility(View.GONE);
                            nextMonth.setVisibility(View.VISIBLE);
                            isEnable = true;
                            setSourceSchedule(isSchedule, isEnable);
                            DateTime dateTime = new DateTime();
                            long mili = dateTime.getMillis();
                            mili = mili + (CmmVariable.deliveryTime) * 60 * 1000; //Cộng thêm 15' để lấy thời gian sau 45p
                            dates = BeanDate.genaratesCurrent(mili);
                            createCalendar(dates);

                        } else {
                            Tracking.excute("C6.2N");
                            enableDelivery.setVisibility(View.VISIBLE);
                            isEnable = false;
                            setSourceSchedule(isSchedule, isEnable);
                            DateTime dateTime = new DateTime();
                            long mili = dateTime.getMillis();
                            mili = mili + (CmmVariable.unDeliveryTime) * 60 * 1000; //Cộng thêm 15' để lấy thời gian sau 45p
                            dates = BeanDate.genaratesCurrent(mili);
                            createCalendar(dates);

                        }
                    }
                });
                DateTime dateTime = new DateTime();
                long mili = dateTime.getMillis();
                mili = mili + (CmmVariable.unDeliveryTime) * 60 * 1000; //Cộng thêm 15' để lấy thời gian sau 45p
                dates = BeanDate.genaratesCurrent(mili);
                createCalendar(dates);
            }
        }).start();
    }

    //region Methods
    private void createCalendar(final JSONObject calendar) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<BeanDate> dates = (List<BeanDate>) calendar.get("dates");
                    monthCalendar.setText(calendar.getString("month_name") + ", ");
                    yearCalendar.setText(calendar.getString("year"));
                    CalendarAdapter calendarAdapter = new CalendarAdapter(getActivity(), recyclerCalendar, dates);
                    LinearLayoutManager calendarLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerCalendar.setLayoutManager(calendarLayout);
                    recyclerCalendar.setAdapter(calendarAdapter);
                    int pos = BeanDate.getPositionSelected(dates);
                    recyclerCalendar.scrollToPosition(pos);

                    calendarAdapter.notifyDataSetChanged();
                    hourClock.setValue(calendar.getInt("hour"));
                    ampmClock.setValue(calendar.getInt("ampm"));
                    minuteClock.setValue(Math.round(calendar.getInt("minute") / 15));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_month:
                try {
                    dates = BeanDate.genaratesNext(0);
                    createCalendar(dates);
                    nextMonth.setVisibility(View.INVISIBLE);
                    backMonth.setVisibility(View.VISIBLE);
                } catch (Exception e) {

                }
                break;
            case R.id.back_month:
                try {
                    DateTime dateTime = new DateTime();
                    long mili = dateTime.getMillis();
                    mili = mili + (CmmVariable.deliveryTime + 15) * 60 * 1000; //Cộng thêm 15' để lấy thời gian sau 45p
                    dates = BeanDate.genaratesCurrent(mili);
                    createCalendar(dates);
                    nextMonth.setVisibility(View.VISIBLE);
                    backMonth.setVisibility(View.INVISIBLE);
                } catch (Exception e) {

                }
                break;
        }
    }

    private void setSourceSchedule(ImageButton imageButton, boolean isEnable) {
        if (StorageHelper.getLanguage().equals("vi")) {
            if (isEnable) {
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.v_delivery_on));
            } else {
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.v_delivery_off));
            }
        } else {
            if (isEnable) {
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.e_delivery_on));
            } else {
                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.e_delivery_off));
            }
        }

    }


    //endregion
}
