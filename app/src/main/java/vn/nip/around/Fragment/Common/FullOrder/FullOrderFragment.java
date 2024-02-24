package vn.nip.around.Fragment.Common.FullOrder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.FullOrderCommonFragment;
import vn.nip.around.Fragment.Pickup.Confirm.CODConfirmFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullOrderFragment extends BaseFragment implements View.OnClickListener {

    ActionGetFullOrder actionGetFullOrder;
    TextView title, orderCode, deliveryTime, itemCost, serviceFee, shippingFee, totalFee, textPayment, itemCostName, verifyCode, totalCashDelivery;
    ImageView icPayment;
    View containerPurchaseFee;
    List<BeanPoint> points;
    LinearLayout llCashDelivery;

    public FullOrderFragment() {
        // Required empty public constructor
    }

    public static FullOrderFragment newInstance(int orderID) {
        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        FullOrderFragment fragment = new FullOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_full_order, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                title = (TextView) view.findViewById(R.id.title);
                orderCode = (TextView) view.findViewById(R.id.order_code);
                deliveryTime = (TextView) view.findViewById(R.id.delivery_time);
                itemCost = (TextView) view.findViewById(R.id.item_cost);
                itemCostName = (TextView) view.findViewById(R.id.item_fee_name);
                serviceFee = (TextView) view.findViewById(R.id.service_fee);
                shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                totalFee = (TextView) view.findViewById(R.id.total);
                verifyCode = (TextView) view.findViewById(R.id.code);
                textPayment = (TextView) view.findViewById(R.id.text_payment);
                icPayment = (ImageView) view.findViewById(R.id.ic_payment);
                containerPurchaseFee = view.findViewById(R.id.container_purchase_fee);
                totalCashDelivery = (TextView) view.findViewById(R.id.total_cash_delivery);
                llCashDelivery = (LinearLayout) view.findViewById(R.id.ll_cash_delivery);
                view.findViewById(R.id.fee_shipping).setOnClickListener(FullOrderFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        actionGetFullOrder = new ActionGetFullOrder();
                        actionGetFullOrder.execute();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGetFullOrder != null) {
            actionGetFullOrder.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fee_shipping:
                CustomDialog.popupShipFee(getActivity());
                break;
        }
    }

    class ActionGetFullOrder extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                String response = APIHelper.getFullOrder(getArguments().getInt("order_id"));
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt("code") == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    points = (List<BeanPoint>) CmmFunc.tryParseList(data.getString("locations"), BeanPoint.class);
                }
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetFullOrder", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    final JSONObject data = jsonObject.getJSONObject("data");
                    final boolean isGift = data.getBoolean("show_gift");
                    final String order_code = data.getString("order_code");
                    final String paymentType = data.getString("payment_type");

                    int day = data.getInt("delivery_day");
                    int month = data.getInt("delivery_month");
                    int year = data.getInt("delivery_year");
                    int hour = data.getInt("delivery_hour");
                    int minute = data.getInt("delivery_minute");
                    DateTime delivery = new DateTime(year, month, day, hour, minute);

                    final String ship = CmmFunc.formatMoney(data.getInt("shipping_fee"), true);
                    final String service = CmmFunc.formatMoney(data.getInt("service_fee"), true);
                    final String itemFee = CmmFunc.formatMoney(data.getInt("item_cost"), true);
                    final String total = CmmFunc.formatMoney(data.getInt("total"), true);
                    final String totalCOD = CmmFunc.formatMoney(data.getInt("total_cod"), true);

                    TextView txtNotice = (TextView) view.findViewById(R.id.txt_notice);
                    if (data.getBoolean("return_to_pickup") == true) {
                        txtNotice.setVisibility(View.VISIBLE);
                    } else {
                        txtNotice.setVisibility(View.GONE);
                    }

                    orderCode.setText(order_code + StringUtils.EMPTY);
                    deliveryTime.setText(delivery.toString("hh:mm a EEEE, dd/MM/yyyy"));
                    if (isGift) {
                        title.setText(getString(R.string.gifting));
                        view.findViewById(R.id.fee_service).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CustomDialog.popupServiceFee(getActivity(), 1);
                            }
                        });
                        containerPurchaseFee.setVisibility(View.VISIBLE);
                        itemCostName.setText(getString(R.string.item_cost));
                        itemCost.setText(itemFee);
                        FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CommonFullOrderFragment.newInstance(CommonFullOrderFragment.GIFTING, data.getString("locations")));

                    } else {
                        title.setText(getString(R.string.cod_order_info));
                        view.findViewById(R.id.fee_service).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CustomDialog.popupServiceFee(getActivity(), 0);
                            }
                        });
                        int type = points.get(1).getPickup_type();
                        if (type == BeanPoint.COD) {
                            llCashDelivery.setVisibility(View.VISIBLE);
                            //add Fragment
                            FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CODFullOrderFragment.newInstance(data.getString("locations")));

                        } else {
                            title.setText(getString(R.string.pickup_delivery));
                            int t = points.get(0).getPickup_type();
                            if (t == BeanPoint.PURCHASE) {
                                containerPurchaseFee.setVisibility(View.VISIBLE);
                                itemCostName.setText(getString(R.string.pay_on_behaft));
                                itemCost.setText(itemFee);
                            }
                            FragmentHelper.addFragment(getChildFragmentManager(), R.id.child_fragment_container, CommonFullOrderFragment.newInstance(CommonFullOrderFragment.PICKUP, data.getString("locations")));
                            //add Fragment
                        }
                    }

                    shippingFee.setText(ship + StringUtils.EMPTY);
                    serviceFee.setText(service + StringUtils.EMPTY);
                    totalFee.setText(total + StringUtils.EMPTY);
                    totalCashDelivery.setText(totalCOD + StringUtils.EMPTY);
                    verifyCode.setText(data.getString("verify_code") + "");
                    switch (paymentType) {
                        case "CASH":
                            icPayment.setImageResource(R.drawable.ic_cash_selected);
                            textPayment.setText(getString(R.string.cast));
                            break;
                        case "ONLINE":
                            icPayment.setImageResource(R.drawable.ic_online_selected);
                            textPayment.setText(getString(R.string.online_payment));
                            break;
                        case "AROUND_PAY":
                            icPayment.setImageResource(R.drawable.ic_around_box_selected);
                            textPayment.setText(getString(R.string.around_payment));
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
}
