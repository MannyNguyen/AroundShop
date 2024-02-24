package vn.nip.around.Fragment.Pickup.Confirm;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Adapter.CODPagerAdapter;
import vn.nip.around.Adapter.TransportPurchaseConfirmAdapter;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommonConfirmFragment extends BaseFragment implements View.OnClickListener {

    List<BeanPoint> points;
    //public LinearLayout recipentPay;
    TextView dropAddress, dropRecipent, dropNote;
    //ImageView dropCall;
    TextView serviceFee, shippingFee, total;
    public ActionGetEstimate actionGetEstimate;

    public CommonConfirmFragment() {
        // Required empty public constructor
    }

    public static CommonConfirmFragment newInstance(String locations) {

        Bundle args = new Bundle();
        args.putString("locations", locations);
        CommonConfirmFragment fragment = new CommonConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_common_confirm, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        points = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("locations"), BeanPoint.class);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                //recipentPay = (LinearLayout) view.findViewById(R.id.recipent_pay_fee);
                dropAddress = (TextView) view.findViewById(R.id.drop_address);
                dropRecipent = (TextView) view.findViewById(R.id.drop_recipient);
                dropNote = (TextView) view.findViewById(R.id.drop_note);
                //dropCall = (ImageView) view.findViewById(R.id.drop_call);


                serviceFee = (TextView) view.findViewById(R.id.service_fee);
                shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                total = (TextView) view.findViewById(R.id.total);
                //recipentPay.setTag(false);

                final RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
                final TransportPurchaseConfirmAdapter adapter = new TransportPurchaseConfirmAdapter(CommonConfirmFragment.this, recycler, points.subList(0, points.size() - 1));

                //recipentPay.setOnClickListener(CommonConfirmFragment.this);
                //dropCall.setOnClickListener(CommonConfirmFragment.this);
                view.findViewById(R.id.fee_shipping).setOnClickListener(CommonConfirmFragment.this);
                view.findViewById(R.id.fee_service).setOnClickListener(CommonConfirmFragment.this);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeanPoint drop = points.get(points.size() - 1);
                        dropAddress.setText(drop.getAddress() + StringUtils.EMPTY);
                        dropRecipent.setText(drop.getRecipent_name() + StringUtils.EMPTY);
                        if (!drop.getNote().equals(StringUtils.EMPTY)) {
                            dropNote.setText(drop.getNote() + StringUtils.EMPTY);
                        } else {
                            view.findViewById(R.id.drop_note_area).setVisibility(View.GONE);
                        }
                        Fragment parent = getParentFragment();
                        if (parent != null && parent instanceof ConfirmFragment) {
                            ConfirmFragment confirmFragment = (ConfirmFragment) parent;
                            getEstimate(confirmFragment.dateTime, false);
                        }

                        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recycler.setAdapter(adapter);
                    }
                });
            }
        });
        threadInit.start();

        isLoaded = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fee_shipping:
                CustomDialog.popupShipFee(getActivity());
                break;

            case R.id.fee_service:
                CustomDialog.popupServiceFee(getActivity(), 0);
                break;
            case R.id.recipent_pay_fee:
                //recipentPay.setTag(!(boolean) recipentPay.getTag());
                //resetLayout(recipentPay);
        }
    }

    private void resetLayout(LinearLayout layout) {
        if ((boolean) layout.getTag()) {
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(getResources().getColor(R.color.main));
                    continue;
                }
                if (child instanceof ImageView) {
                    ((ImageView) child).setImageResource(R.drawable.ic_tick_active);
                }
            }

        } else {
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                if (child instanceof TextView) {
                    ((TextView) child).setTextColor(getResources().getColor(R.color.gray_600));
                    continue;
                }
                if (child instanceof ImageView) {
                    ((ImageView) child).setImageResource(R.drawable.ic_tick_inactive);
                }
            }
        }

    }

    public void getEstimate(DateTime dateTime, boolean isReturnPickup) {
        if (actionGetEstimate != null) {
            actionGetEstimate.cancel(true);
        }
        actionGetEstimate = new ActionGetEstimate();
        actionGetEstimate.execute(dateTime, isReturnPickup, CmmFunc.tryParseObject(points));
    }

    class ActionGetEstimate extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                DateTime dateTime = (DateTime) objects[0];
                boolean isReturnPickup = (boolean) objects[1];
                String response;
                if (dateTime == null) {
                    response = APIHelper.estimateCost(0, 0, 0, 0, 0, isReturnPickup, getArguments().getString("locations"));
                } else {
                    response = APIHelper.estimateCost(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), dateTime.getHourOfDay(), dateTime.getMinuteOfHour(), isReturnPickup, getArguments().getString("locations"));
                }


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
                    Fragment fragment = getParentFragment();
                    if (fragment instanceof ConfirmFragment) {
                        ((ConfirmFragment) fragment).totalFee = jsonObject.getInt("total");
                    }
                    shippingFee.setText(CmmFunc.formatMoney(jsonObject.getInt("shipping_fee"), true));
                    serviceFee.setText(CmmFunc.formatMoney(jsonObject.getInt("service_fee"), true));
                    total.setText(CmmFunc.formatMoney(jsonObject.getInt("total"), true));
                    if (points.get(0).getPickup_type() == BeanPoint.PURCHASE) {
                        view.findViewById(R.id.container_purchase_fee).setVisibility(View.VISIBLE);
                    }
                    TextView itemFee = (TextView) view.findViewById(R.id.item_cost);
                    itemFee.setText(CmmFunc.formatMoney(jsonObject.getInt("item_cost"), true));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
}
