package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import vn.nip.around.Adapter.FullOrderPagerAdapter;
import vn.nip.around.Adapter.FullOrderPointAdapter;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import org.joda.time.LocalDate;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFullOrderFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    int currentPayment;

    //region Constructor
    public ViewFullOrderFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_view_full_order, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FullOrderPagerAdapter adapter = new FullOrderPagerAdapter(getView());
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
        tab.setupWithViewPager(pager);


        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getResources().getString(R.string.full_order));
        CmmFunc.setDelay(400, new ICallback() {
            @Override
            public void excute() {
                new ActionGetData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id_order"));
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }

    //endregion

    //region Actions
    class ActionGetData extends ActionAsync {
        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int idOrder = (int) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_order", idOrder + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_full_order", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetData", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONObject data = jsonObject.getJSONObject("data");
                List<BeanPoint> points = (List<BeanPoint>) CmmFunc.tryParseList(data.getString("locations"), BeanPoint.class);
                RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler_points);
                FullOrderPointAdapter adapter = new FullOrderPointAdapter(getActivity(), recycler, points, data.getBoolean("show_gift"));
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recycler.setLayoutManager(layoutManager);
                recycler.setAdapter(adapter);


                RadioButton radioReturn = (RadioButton) view.findViewById(R.id.radio_return);
                radioReturn.setChecked(data.getBoolean("return_to_pickup"));
                if (radioReturn.isChecked()) {
                    radioReturn.setBackground(getResources().getDrawable(R.drawable.border_main_high));
                }


                int day = data.getInt("delivery_day");
                if (day == 0) {
                    RelativeLayout dateTimeArea = (RelativeLayout) view.findViewById(R.id.date_time_area);
                    //dateTimeArea.setVisibility(View.GONE);
                    TextView dateTime = (TextView) view.findViewById(R.id.date_time);
                    dateTime.setBackground(getResources().getDrawable(R.drawable.border_gray_small));
                    dateTime.setText("");

                } else {
                    RelativeLayout dateTimeArea = (RelativeLayout) view.findViewById(R.id.date_time_area);
                    //dateTimeArea.setVisibility(View.VISIBLE);

                    String[] monthNames = new String[]{"JAN", "FEB", "MAR", "APRIL", "MAY", "JUNE", "JULY", "AUG", "SEP", "OCT", "NOV", "DEC"};
                    String[] dayNames = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
                    if (StorageHelper.getLanguage().equals("vi")) {
                        monthNames = new String[]{"tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6", "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"};
                        dayNames = new String[]{"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
                    }
                    int month = data.getInt("delivery_month");
                    String monthName = monthNames[month - 1];
                    int year = data.getInt("delivery_year");
                    int hour = data.getInt("delivery_hour");
                    int minute = data.getInt("delivery_minute");
                    String ampm = "AM";
                    if (hour == 0) {
                        hour = 12;
                        ampm = "AM";
                    } else if (hour == 12) {
                        ampm = "PM";
                    } else if (hour > 12) {
                        hour = hour - 12;
                        ampm = "PM";
                    } else {
                        ampm = "AM";
                    }
                    LocalDate today = new LocalDate(year, month, day);
                    String nameDay = dayNames[today.getDayOfWeek() - 1];
                    String formatDateTime = nameDay + ", " + monthName + " " + day + ", " + year + "  |  " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + " " + ampm;
                    if (StorageHelper.getLanguage().equals("vi")) {
                        formatDateTime = nameDay + ", " + day + " " + monthName + ", " + year + "  -  " + String.format("%02d", hour) + ":" + String.format("%02d", minute) + " " + ampm;
                    }
                    TextView dateTime = (TextView) view.findViewById(R.id.date_time);
                    dateTime.setBackground(getResources().getDrawable(R.drawable.border_main_small));
                    dateTime.setText(formatDateTime);


                }
                if (data.getBoolean("show_gift") == false) {
                    ImageView isGift = (ImageView) view.findViewById(R.id.parent_is_gift);
                    isGift.setVisibility(View.GONE);
                    FrameLayout line = (FrameLayout) view.findViewById(R.id.line);
                    line.setVisibility(View.GONE);
                } else {
                    TextView itemCost = (TextView) view.findViewById(R.id.item_cost_text);
                    itemCost.setText(getString(R.string.item_cost));
                    ImageView isGift = (ImageView) view.findViewById(R.id.parent_is_gift);
                    if (data.getBoolean("is_gift")) {
                        isGift.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus_gift));
                    } else {
                        isGift.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus_gift_disable));
                    }
                }
                String[] arrPay = {"CASH", "ONLINE"};
                Spinner payment = (Spinner) view.findViewById(R.id.payment);
                ImageView paymentType = (ImageView) view.findViewById(R.id.payment_type);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row_spinner_payment, arrPay);
                arrayAdapter.setDropDownViewResource
                        (android.R.layout.simple_list_item_single_choice);

                payment.setAdapter(arrayAdapter);
                if (data.getString("payment_type").equals("ONLINE")) {
                    currentPayment = 1;
                    payment.setSelection(1);
                    paymentType.setImageDrawable(GlobalClass.getActivity().getResources().getDrawable(R.drawable.ic_online_selected));
                } else {
                    currentPayment = 0;
                    payment.setSelection(0);
                    paymentType.setImageDrawable(GlobalClass.getActivity().getResources().getDrawable(R.drawable.ic_cast_selected));

                }
                payment.setEnabled(false);
//                if (data.getBoolean("allow_change_payment") == false) {
//                    payment.setEnabled(false);
//                } else {
//                    payment.setEnabled(false);
//                    payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        boolean isFirst = true;
//
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            if (!isFirst) {
//                                new ActionChangePayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id_order"), i);
//                            }
//                            isFirst = false;
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });
//                }

                TextView distance = (TextView) view.findViewById(R.id.distance);
                TextView time = (TextView) view.findViewById(R.id.time);
                TextView total = (TextView) view.findViewById(R.id.total);
                TextView shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                TextView serviceFee = (TextView) view.findViewById(R.id.service_fee);
                TextView itemCost = (TextView) view.findViewById(R.id.item_cost);
                TextView returnFee = (TextView) view.findViewById(R.id.return_fee);
                distance.setText(data.getDouble("distance") + " km");
                Double duration = data.getDouble("duration");
                Double mduration = duration / 60;
                if (mduration < 60) {
                    time.setText(String.valueOf(mduration).split("\\.")[0] + " " + getString(R.string.minute));
                } else {
                    Double hduration = mduration / 60;
                    int hour = hduration.intValue();
                    int minute = (int) (mduration - (hour * 60));
                    if (minute > 0) {
                        time.setText(hour + " " + getString(R.string.hour) + " " + minute + " " + getString(R.string.minute));
                    } else {
                        time.setText(hour + " " + getString(R.string.hour));
                    }
                }

                shippingFee.setText(CmmFunc.formatMoney(data.getInt("shipping_fee")) + "");
                serviceFee.setText(CmmFunc.formatMoney(data.getInt("service_fee")) + "");
                itemCost.setText(CmmFunc.formatMoney(data.getInt("item_cost")) + "");
                returnFee.setText(CmmFunc.formatMoney(data.getInt("return_to_pickup_fee")) + "");
                total.setText(CmmFunc.formatMoney(data.getInt("total")) + "");
                view.findViewById(R.id.pager).setVisibility(View.VISIBLE);
            } catch (Exception e) {

            }
        }
    }

    class ActionChangePayment extends ActionAsync {
        int paymentCode;

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int idOrder = (int) objects[0];
                paymentCode = (int) objects[1];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_order", idOrder + ""));
                if (paymentCode == 0) {
                    params.add(new AbstractMap.SimpleEntry("payment_type", "CASH"));
                } else if (paymentCode == 1) {
                    params.add(new AbstractMap.SimpleEntry("payment_type", "ONLINE"));
                }
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/change_order_payment", params, false);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetData", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                Spinner payment = (Spinner) view.findViewById(R.id.payment);
                ImageView paymentType = (ImageView) view.findViewById(R.id.payment_type);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    currentPayment = paymentCode;

                } else {
                }
                //payment.setSelection(currentPayment);
                if (currentPayment == 1) {

                    paymentType.setImageDrawable(GlobalClass.getActivity().getResources().getDrawable(R.drawable.ic_online_selected));
                    Fragment fragment = CmmFunc.getFragmentByTag(getActivity(), FollowJourneyFragment.class.getName());
                    JSONObject infoPackage = new JSONObject(fragment.getArguments().getString("info_package"));
                    infoPackage.put("is_payment", false);
                    fragment.getArguments().putString("info_package", infoPackage.toString());
                } else {
                    paymentType.setImageDrawable(GlobalClass.getActivity().getResources().getDrawable(R.drawable.ic_cast_selected));
                    Fragment fragment = CmmFunc.getFragmentByTag(getActivity(), FollowJourneyFragment.class.getName());
                    JSONObject infoPackage = new JSONObject(fragment.getArguments().getString("info_package"));
                    infoPackage.put("is_payment", true);
                    fragment.getArguments().putString("info_package", infoPackage.toString());
                }
            } catch (Exception e) {

            }

        }
    }
    //endregion
}
