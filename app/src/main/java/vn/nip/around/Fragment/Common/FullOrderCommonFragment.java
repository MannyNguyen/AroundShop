package vn.nip.around.Fragment.Common;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullOrderCommonFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    List<BeanPoint> locations;
    //endregion

    //region Contructors
    public FullOrderCommonFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static FullOrderCommonFragment newInstance(int orderID) {

        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        FullOrderCommonFragment fragment = new FullOrderCommonFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_full_order_common, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CmmFunc.setDelay(400, new ICallback() {
            @Override
            public void excute() {
                if (!isLoaded) {
                    new ActionGetFullOrder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    isLoaded = true;
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fee_shipping:
                CustomDialog.popupShipFee(getActivity());
                break;


        }
    }
    //endregion

    //region Actions
    class ActionGetFullOrder extends ActionAsync {
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
                params.add(new AbstractMap.SimpleEntry("id_order", getArguments().getInt("order_id") + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_full_order", params);
                JSONObject jsonObject = new JSONObject(response);
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final JSONObject data = jsonObject.getJSONObject("data");
                                final boolean isGift = data.getBoolean("show_gift");
                                final String orderCode = data.getString("order_code");

                                final int day = data.getInt("delivery_day");
                                int month = data.getInt("delivery_month");
                                int year = data.getInt("delivery_year");
                                int hour = data.getInt("delivery_hour");
                                int minute = data.getInt("delivery_minute");
                                DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm");
                                LocalDateTime dateTime = LocalDateTime.parse(day + "-" + month + "-" + year + " " + hour + ":" + minute, formatter);
                                final JSONObject formatDate = CmmFunc.formatDate(dateTime, false);


                                locations = (List<BeanPoint>) CmmFunc.tryParseList(data.getString("locations"), BeanPoint.class);


                                final String paymentType = data.getString("payment_type");

                                final String ship = CmmFunc.formatMoney(data.getInt("shipping_fee"), true);
                                final String service = CmmFunc.formatMoney(data.getInt("service_fee"), true);
                                final String itemFee = CmmFunc.formatMoney(data.getInt("item_cost"), true);
                                final String totalFee = CmmFunc.formatMoney(data.getInt("total"), true);
                                view.findViewById(R.id.fee_shipping).setOnClickListener(FullOrderCommonFragment.this);
                                view.findViewById(R.id.fee_service).setOnClickListener(FullOrderCommonFragment.this);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            TextView title = (TextView) view.findViewById(R.id.title);
                                            if (isGift) {
                                                title.setText(getString(R.string.gifting));
                                                view.findViewById(R.id.fee_service).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CustomDialog.popupServiceFee(getActivity(), 1);
                                                    }
                                                });
                                            } else {
                                                title.setText(getString(R.string.pickup_delivery));
                                                view.findViewById(R.id.fee_service).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CustomDialog.popupServiceFee(getActivity(), 0);
                                                    }
                                                });
                                            }
                                            TextView orderCodeView = (TextView) view.findViewById(R.id.order_code);
                                            orderCodeView.setText(orderCode);

                                            TextView deliveryTime = (TextView) view.findViewById(R.id.delivery_time);
                                            deliveryTime.setText(formatDate.getString("value"));

                                            final BeanPoint last = locations.get(locations.size() - 1);
                                            TextView dropAddress = (TextView) view.findViewById(R.id.drop_address);
                                            dropAddress.setText(last.getAddress() + "");

                                            TextView dropRecipient = (TextView) view.findViewById(R.id.drop_recipent_name);
                                            dropRecipient.setText(last.getRecipent_name() + "");
                                            final ImageButton dropCall = (ImageButton) view.findViewById(R.id.drop_call);
                                            if (!last.getPhone().equals("")) {
                                                dropCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_now));
                                                dropCall.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", last.getPhone(), null));
                                                        startActivity(intent);
                                                    }
                                                });
                                            } else {
                                                dropCall.setImageDrawable(getResources().getDrawable(R.drawable.ic_uncall));
                                            }


                                            if (last.getNote().equals("")) {
                                                view.findViewById(R.id.drop_note_area).setVisibility(View.GONE);
                                            } else {
                                                TextView dropNote = (TextView) view.findViewById(R.id.drop_note);
                                                dropNote.setText(last.getNote() + "");
                                            }

                                            if (locations.get(0).getShipper_fullname().equals(StringUtils.EMPTY)) {
                                                view.findViewById(R.id.shipper_area).setVisibility(View.GONE);
                                            } else {
                                                view.findViewById(R.id.shipper_area).setVisibility(View.VISIBLE);
                                                TextView shipperName = (TextView) view.findViewById(R.id.shipper_name);
                                                shipperName.setText(locations.get(0).getShipper_fullname());
                                                view.findViewById(R.id.call_shipper).setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", locations.get(0).getShipper_phone(), null));
                                                        startActivity(intent);
                                                    }
                                                });
                                            }

                                            ImageView icPayment = (ImageView) view.findViewById(R.id.ic_payment);
                                            TextView textPayment = (TextView) view.findViewById(R.id.text_payment);
                                            switch (paymentType) {
                                                case "CASH":
                                                    icPayment.setImageDrawable(getResources().getDrawable(R.drawable.ic_cash_selected));
                                                    textPayment.setText(getString(R.string.cast));
                                                    break;
                                                case "ONLINE":
                                                    icPayment.setImageDrawable(getResources().getDrawable(R.drawable.ic_online_selected));
                                                    textPayment.setText(getString(R.string.online_payment));
                                                    break;
                                                case "AROUND_PAY":
                                                    icPayment.setImageDrawable(getResources().getDrawable(R.drawable.ic_around_box_selected));
                                                    textPayment.setText(getString(R.string.around_payment));
                                                    break;
                                            }

                                            DetailLocationFragment detailLocationFragment = (DetailLocationFragment) getChildFragmentManager().findFragmentById(R.id.detail_location_fragment);
                                            detailLocationFragment.getArguments().putString("data", CmmFunc.tryParseObject(locations));

                                            List<Integer> types = new ArrayList<Integer>();
                                            TextView itemCostName = (TextView) view.findViewById(R.id.item_fee_name);
                                            if (isGift) {
                                                detailLocationFragment.getArguments().putString("type", DetailLocationFragment.GIFTING);
                                                itemCostName.setText(getString(R.string.item_cost));
                                                view.findViewById(R.id.container_purchase_fee).setVisibility(View.VISIBLE);
                                            } else {
                                                detailLocationFragment.getArguments().putString("type", DetailLocationFragment.PICKUP);
                                                for (BeanPoint bean : locations) {
                                                    types.add(bean.getPickup_type());
                                                }
                                                itemCostName.setText(getString(R.string.purchase_cost));
                                                if (types.contains(BeanPoint.PURCHASE)) {
                                                    view.findViewById(R.id.container_purchase_fee).setVisibility(View.VISIBLE);
                                                } else {
                                                    view.findViewById(R.id.container_purchase_fee).setVisibility(View.GONE);
                                                }
                                            }
                                            detailLocationFragment.onResume();

                                            TextView shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                                            shippingFee.setText(ship);
                                            TextView serviceFee = (TextView) view.findViewById(R.id.service_fee);
                                            serviceFee.setText(service);
                                            TextView itemCost = (TextView) view.findViewById(R.id.item_cost);
                                            itemCost.setText(itemFee);
                                            TextView total = (TextView) view.findViewById(R.id.total);
                                            total.setText(totalFee);

                                            TextView code = (TextView) view.findViewById(R.id.code);
                                            code.setText(data.getString("verify_code") + "");
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
