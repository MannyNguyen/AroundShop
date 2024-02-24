package vn.nip.around.Fragment.Pickup;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import vn.nip.around.Adapter.PaymentMethodAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.ListPaymentFragment;
import vn.nip.around.Fragment.Common.MatchFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;
import vn.nip.around.Util.NetworkUtil;

import static vn.nip.around.Bean.BeanPoint.PURCHASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    public int orderID;
    public int day, month, year, hour, minute;
    private List<BeanPoint> points;
    private int total;
    public ListPaymentFragment listPaymentFragment;

    //Order gồm những service nào
    List<Integer> types = new ArrayList<Integer>();
    //endregion

    //region Constructor
    public ConfirmFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static ConfirmFragment newInstance(int orderID, String locations, String deliveryTime) {

        Bundle args = new Bundle();
        args.putBoolean("cannot_find_shipper", false);
        args.putInt("orderID", orderID);
        args.putString("locations", locations);
        args.putString("delivery_time", deliveryTime);
        ConfirmFragment fragment = new ConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_confirm, container, false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        hideProgress();
        AppActivity.FLOW = AppActivity.PICKUP;
        SmartFoxHelper.getInstance().disconnect();
        orderID = 0;
        try {
            hideProgress();
            if (listPaymentFragment != null) {
                listPaymentFragment.onResume();
            }
            if (getArguments().getBoolean("cannot_find_shipper")) {
                //show pop khong tim thay shipper

            } else {
                //Qua thoi gian lam viec
                if (!getArguments().getString("delivery_time").equals("")) {
                    String time = getArguments().getString("delivery_time");
                    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(time, df);
                    JSONObject jsonObject = CmmFunc.formatDate(dateTime);
                    day = jsonObject.getInt("day");
                    month = jsonObject.getInt("month");
                    year = jsonObject.getInt("year");
                    hour = jsonObject.getInt("hour");
                    minute = jsonObject.getInt("minute");
                    TextView dropDate = (TextView) getView().findViewById(R.id.drop_date);
                    dropDate.setText(jsonObject.getString("value"));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppActivity.FLOW = AppActivity.PICKUP;
        listPaymentFragment = (ListPaymentFragment) getChildFragmentManager().findFragmentById(R.id.list_payment_fragment);
        if (!isLoaded) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        points = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("locations"), BeanPoint.class);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TextView title = (TextView) view.findViewById(R.id.title);
                                    title.setText(getString(R.string.full_order));
                                    LinearLayout containerItem = (LinearLayout) view.findViewById(R.id.container_location);
                                    containerItem.removeAllViews();

                                    for (int i = 0; i < points.size() - 1; i++) {
                                        BeanPoint bean = points.get(i);

                                        View row = getActivity().getLayoutInflater().inflate(R.layout.row_detail_pickup_location, null);
                                        TextView dropLocation = (TextView) row.findViewById(R.id.drop_location);
                                        dropLocation.append(" " + (i + 1));
                                        TextView pickupType = (TextView) row.findViewById(R.id.type_pickup);
                                        switch (bean.getPickup_type()) {
                                            case 1:
                                                pickupType.setText(getString(R.string.transport));
                                                break;
                                            case 2:
                                                pickupType.setText(getString(R.string.purchase));
                                                break;
                                            case 3:
                                                pickupType.setText(getString(R.string.collect));
                                                break;
                                        }
                                        types.add(bean.getPickup_type());
                                        ImageView icLocation = (ImageView) row.findViewById(R.id.ic_location);
                                        switch (i) {
                                            case 0:
                                                icLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_1));
                                                break;
                                            case 1:
                                                icLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_2));
                                                break;
                                            case 2:
                                                icLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_3));
                                                break;
                                        }

                                        TextView fee = (TextView) row.findViewById(R.id.fee);
                                        if (bean.getItem_cost() == 0) {
                                            fee.setVisibility(View.GONE);
                                            //fee.setText("0" + getString(R.string.vnd));
                                        } else {
                                            fee.setText(CmmFunc.formatMoney(bean.getItem_cost(), true));
                                        }


                                        TextView pickupAddress = (TextView) row.findViewById(R.id.pickup_addresss);
                                        pickupAddress.setText(bean.getAddress());
                                        TextView itemName = (TextView) row.findViewById(R.id.item_name);
                                        itemName.setText(bean.getItem_name());

                                        View paynow15 = row.findViewById(R.id.pay_now_15);
                                        paynow15.setVisibility(View.GONE);
                                        if (bean.isX15()) {
                                            paynow15.setVisibility(View.VISIBLE);
                                        }
                                        TextView noteView = (TextView) row.findViewById(R.id.note);
                                        if (!bean.getNote().trim().equals("")) {
                                            row.findViewById(R.id.note_area).setVisibility(View.VISIBLE);
                                            noteView.setText(bean.getNote());
                                        } else {
                                            row.findViewById(R.id.note_area).setVisibility(View.GONE);
                                        }


                                        containerItem.addView(row);
                                    }
                                    BeanPoint last = points.get(points.size() - 1);
                                    TextView dropAddress = (TextView) view.findViewById(R.id.drop_address);
                                    dropAddress.setText(last.getAddress() + "");

                                    TextView dropRecipient = (TextView) view.findViewById(R.id.drop_recipient);
                                    dropRecipient.setText(last.getRecipent_name() + "");
                                    if (points.get(points.size() - 1).getNote().equals("")) {
                                        view.findViewById(R.id.drop_note_area).setVisibility(View.GONE);
                                    } else {
                                        TextView dropNote = (TextView) view.findViewById(R.id.drop_note);
                                        dropNote.setText(last.getNote() + "");
                                    }

                                    new ActionGetEstimate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                                    getView().findViewById(R.id.calendar_area).setOnClickListener(ConfirmFragment.this);

                                    LocalDateTime localDateTime = new LocalDateTime(System.currentTimeMillis() + 3600000);
                                    JSONObject jsonObject = CmmFunc.formatDate(localDateTime);
                                    TextView dropDate = (TextView) getView().findViewById(R.id.drop_date);
                                    dropDate.setText(jsonObject.getString("value"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            isLoaded = true;
        }


    }
    //endregion

    //region Event
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fee_shipping:
                CustomDialog.popupShipFee(getActivity());
                break;

            case R.id.fee_service:
                CustomDialog.popupServiceFee(getActivity(), 0);
                break;

            case R.id.confirm:
                confirm(true);
                break;

            case R.id.calendar_area:
                LocalDateTime date = new LocalDateTime(System.currentTimeMillis() + 5400000);
                if (day != 0) {
                    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
                    date = LocalDateTime.parse(day + "-" + (month) + "-" + year + " " + hour + ":" + minute, dtf);
                }
                final LocalDateTime dateClone = date;
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, final int y, final int m, final int d) {
                                if (datePicker.isShown()) {
                                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                            try {
                                                day = d;
                                                month = m;
                                                year = y;
                                                hour = i;
                                                minute = i1;
                                                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
                                                LocalDateTime dateTime = LocalDateTime.parse(day + "-" + (month + 1) + "-" + year + " " + hour + ":" + minute, formatter);
                                                JSONObject jsonObject = CmmFunc.formatDate(dateTime);
                                                day = jsonObject.getInt("day");
                                                month = jsonObject.getInt("month");
                                                year = jsonObject.getInt("year");
                                                hour = jsonObject.getInt("hour");
                                                minute = jsonObject.getInt("minute");
                                                TextView dropDate = (TextView) getView().findViewById(R.id.drop_date);
                                                dropDate.setText(jsonObject.getString("value"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, dateClone.getHourOfDay(), dateClone.getMinuteOfHour(), false);
                                    timePickerDialog.show();
                                }

                            }
                        }, date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel_schedule), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            try {
                                day = 0;
                                month = 0;
                                year = 0;
                                hour = 0;
                                minute = 0;
                                JSONObject jsonObject = CmmFunc.formatDate(new LocalDateTime(System.currentTimeMillis() + 3600000));
                                TextView dropDate = (TextView) getView().findViewById(R.id.drop_date);
                                dropDate.setText(jsonObject.getString("value"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 60000);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (CmmVariable.maxDay - 1) * 24 * 60 * 60 * 1000);
                datePickerDialog.show();
                break;


        }
    }
    //endregion

    //region Methods

    public void confirm(boolean isCheckTime) {
        try {
            if (NetworkUtil.getConnectivityStatus(GlobalClass.getContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
                CustomDialog.showMessage(GlobalClass.getActivity(), "", GlobalClass.getActivity().getString(R.string.disconnect_match));
                return;
            }
            showProgress();
            final View confirm = view.findViewById(R.id.confirm);
            confirm.setOnClickListener(null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        confirm.setOnClickListener(ConfirmFragment.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            if (listPaymentFragment.paymentType == null) {
                hideProgress();
                return;
            }

            if (total > CmmVariable.minGiftPaymentCost && listPaymentFragment.paymentType.equals("CASH")) {
                CustomDialog.showMessage(getActivity(), "", getString(R.string.error_max_fee_online, CmmFunc.formatMoney(CmmVariable.minGiftPaymentCost)));
                hideProgress();
                PaymentMethodAdapter adapter = (PaymentMethodAdapter) listPaymentFragment.recyclerView.getAdapter();
                adapter.updatePayment("ONLINE");
                return;
            }

            if (isCheckTime && day != 0) {
                SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date selectedDate = dtf.parse(day + "-" + (month) + "-" + year + " " + hour + ":" + minute);
                Date now = new Date();
                if (selectedDate.getTime() - now.getTime() < 5400000 - 60000) {
                    CustomDialog.showMessage(getActivity(), "", getString(R.string.time_minimum));
                    hideProgress();
                    return;
                }
            }
            Tracking.excute("C7.1N");


            Fragment fragment = MatchFragment.newInstance(getArguments().getInt("orderID"), false, "PICKUP", getArguments().getString("locations"), year, month, day, hour, minute);
            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Actions
    class ActionGetEstimate extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("minute", minute + ""));
                params.add(new AbstractMap.SimpleEntry("day", day + ""));
                params.add(new AbstractMap.SimpleEntry("month", month + ""));
                params.add(new AbstractMap.SimpleEntry("hour", hour + ""));
                params.add(new AbstractMap.SimpleEntry("year", year + ""));
                params.add(new AbstractMap.SimpleEntry("return_to_pickup", "false"));
                params.add(new AbstractMap.SimpleEntry("locations", getArguments().getString("locations")));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/estimate_cost", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetEstimate", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {

                    if (types.contains(PURCHASE)) {
                        TextView itemCost = (TextView) view.findViewById(R.id.item_cost);
                        itemCost.setText(CmmFunc.formatMoney(jsonObject.getInt("item_cost"), true) + "");
                    } else {
                        view.findViewById(R.id.container_purchase_fee).setVisibility(View.GONE);
                    }


                    TextView shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                    shippingFee.setText(CmmFunc.formatMoney(jsonObject.getInt("shipping_fee"), true) + "");
                    TextView serviceFee = (TextView) view.findViewById(R.id.service_fee);
                    serviceFee.setText(CmmFunc.formatMoney(jsonObject.getInt("service_fee"), true) + "");

                    total = jsonObject.getInt("total");
                    TextView totalView = (TextView) view.findViewById(R.id.total);
                    totalView.setText(CmmFunc.formatMoney(total) + "");

                    view.findViewById(R.id.confirm).setOnClickListener(ConfirmFragment.this);
                    view.findViewById(R.id.fee_shipping).setOnClickListener(ConfirmFragment.this);
                    view.findViewById(R.id.fee_service).setOnClickListener(ConfirmFragment.this);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }


    //endregion
}
