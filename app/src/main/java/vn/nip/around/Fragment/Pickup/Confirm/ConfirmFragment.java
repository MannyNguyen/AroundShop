package vn.nip.around.Fragment.Pickup.Confirm;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import vn.nip.around.Adapter.CODPagerAdapter;
import vn.nip.around.Adapter.PaymentMethodAdapter;
import vn.nip.around.Adapter.TransportPurchaseConfirmAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Custom.CustomDatePicker;
import vn.nip.around.Custom.CustomTimePicker;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.ListPaymentFragment;
import vn.nip.around.Fragment.Common.MatchFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.Interface.IDateCallBack;
import vn.nip.around.R;
import vn.nip.around.Util.NetworkUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends BaseFragment implements View.OnClickListener {
    final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public int orderID;
    public ListPaymentFragment listPaymentFragment;
    public DateTime dateTime;
    public TextView dropDate;
    public int TYPE;
    List<BeanPoint> points;
    public int totalFee;

    public ConfirmFragment() {
        // Required empty public constructor
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_confirm2, container, false);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        AppActivity.FLOW = AppActivity.PICKUP;
        orderID = 0;
        if (!isLoaded) {
        TextView title = (TextView) view.findViewById(R.id.title);
        points = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("locations"), BeanPoint.class);
        TYPE = points.get(1).getPickup_type();
        if (TYPE == BeanPoint.COD) {
            title.setText(getString(R.string.confirm_cod_order));
            FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CODConfirmFragment.newInstance(getArguments().getString("locations")));
        } else {
            title.setText(getString(R.string.delivery));
            FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CommonConfirmFragment.newInstance(getArguments().getString("locations")));
        }
        listPaymentFragment = (ListPaymentFragment) getChildFragmentManager().findFragmentById(R.id.list_payment_fragment);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                dropDate = (TextView) view.findViewById(R.id.drop_date);

                view.findViewById(R.id.calendar_area).setOnClickListener(ConfirmFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long mili60 = System.currentTimeMillis() + 3600000;
                        DateTime dt = new DateTime(mili60);
                        int minute = dt.getMinuteOfHour();
                        if (minute % 15 != 0) {
                            mili60 = ((mili60 / 900000) + 1) * 900000;
                            dt = new DateTime(mili60);
                        }
                        dropDate.setText(dt.toString("hh:mm a EEEE, dd/MM/yyyy"));
                        view.findViewById(R.id.confirm).setOnClickListener(ConfirmFragment.this);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
        }
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
                    dateTime = DateTime.parse(time, formatter);
                    dropDate.setText(dateTime.toString("hh:mm a EEEE, dd/MM/yyyy"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calendar_area:
                if (dateTime == null) {
                    dateTime = new DateTime(System.currentTimeMillis() + 5400000);
                }
                CustomDatePicker customDatePicker = new CustomDatePicker(getContext(), dateTime );
                DatePickerDialog datePickerDialog = customDatePicker.show(new IDateCallBack() {
                    @Override
                    public void excute(DateTime dt) {
                        dateTime = dt;
                        new CustomTimePicker(getContext(), dt).show(new IDateCallBack() {
                            @Override
                            public void excute(DateTime dt) {
                                try {
                                    long mili = dt.getMillis();
                                    dateTime = new DateTime(mili);
                                    int minute = dt.getMinuteOfHour();
                                    if (minute % 15 != 0) {
                                        mili = ((mili / 900000) + 1) * 900000;
                                        dateTime = new DateTime(mili);
                                    }

                                    dropDate.setText(dateTime.toString("hh:mm a EEEE, dd/MM/yyyy"));
                                    for (Fragment fragment : getChildFragmentManager().getFragments()) {
                                        if (fragment instanceof CODConfirmFragment) {
                                            CODConfirmFragment codConfirmFragment = (CODConfirmFragment) fragment;
                                            codConfirmFragment.getEstimate(dateTime, (Boolean) codConfirmFragment.returnToPickup.getTag());
                                            continue;
                                        }
                                        if (fragment instanceof CommonConfirmFragment) {
                                            CommonConfirmFragment commonConfirmFragment = (CommonConfirmFragment) fragment;
                                            commonConfirmFragment.getEstimate(dateTime, false);
                                            continue;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }, new ICallback() {
                    @Override
                    public void excute() {
                        try {
                            dateTime = null;
                            long mili60 = System.currentTimeMillis() + 3600000;
                            DateTime dt = new DateTime(mili60);
                            int minute = dt.getMinuteOfHour();
                            if (minute % 15 != 0) {
                                mili60 = ((mili60 / 900000) + 1) * 900000;
                                dt = new DateTime(mili60);
                            }

                            dropDate.setText(dt.toString("hh:mm a EEEE, dd/MM/yyyy"));
                            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                                if (fragment instanceof CODConfirmFragment) {
                                    CODConfirmFragment codConfirmFragment = (CODConfirmFragment) fragment;
                                    codConfirmFragment.getEstimate(dateTime, (Boolean) codConfirmFragment.returnToPickup.getTag());
                                    continue;
                                }
                                if (fragment instanceof CommonConfirmFragment) {
                                    CommonConfirmFragment commonConfirmFragment = (CommonConfirmFragment) fragment;
                                    commonConfirmFragment.getEstimate(dateTime, false);
                                    continue;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 60000);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + (CmmVariable.maxDay - 1) * 24 * 60 * 60 * 1000);

                break;
            case R.id.confirm:
                confirm(true);
                break;
        }
    }

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

            if (totalFee > CmmVariable.minGiftPaymentCost && listPaymentFragment.paymentType.equals("CASH")) {
                CustomDialog.showMessage(getActivity(), "", getString(R.string.error_max_fee_online, CmmFunc.formatMoney(CmmVariable.minGiftPaymentCost)));
                hideProgress();
                PaymentMethodAdapter adapter = (PaymentMethodAdapter) listPaymentFragment.recyclerView.getAdapter();
                adapter.updatePayment("ONLINE");
                return;
            }

            if (isCheckTime && dateTime != null) {
                if (dateTime.getMillis() - new DateTime().getMillis() < 5400000 - 60000) {
                    CustomDialog.showMessage(getActivity(), "", getString(R.string.time_minimum));
                    hideProgress();
                    return;
                }
            }

            boolean isReturnToPickup = false;
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment instanceof CODConfirmFragment) {
                    CODConfirmFragment codConfirmFragment = (CODConfirmFragment) fragment;
                    isReturnToPickup = (boolean) codConfirmFragment.returnToPickup.getTag();
                    break;
                }

            }
            Fragment fragment;
            if (dateTime == null) {
                fragment = MatchFragment.newInstance(getArguments().getInt("orderID"), false, "PICKUP", getArguments().getString("locations"), 0, 0, 0, 0, 0, isReturnToPickup);
            } else {
                fragment = MatchFragment.newInstance(getArguments().getInt("orderID"), false, "PICKUP", getArguments().getString("locations"), dateTime.getYear(), dateTime.getMonthOfYear(),
                        dateTime.getDayOfMonth(), dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), isReturnToPickup);
            }
            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion
}
