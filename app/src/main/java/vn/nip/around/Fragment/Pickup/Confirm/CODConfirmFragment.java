package vn.nip.around.Fragment.Pickup.Confirm;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Adapter.CODPagerAdapter;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Pickup.*;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

import static vn.nip.around.Bean.BeanPoint.PURCHASE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CODConfirmFragment extends BaseFragment implements View.OnClickListener {
    List<BeanPoint> points;

    public LinearLayout returnToPickup, recipentPay;
    View containerLocation;
    TextView dropAddress, dropRecipent, dropNote;
    ImageView dropCall;
    ImageView icExpand;
    ViewPager pager;
    TabLayout tab;
    CODPagerAdapter adapter;
    TextView serviceFee, shippingFee, total, totalCashDelivery;
    public ActionGetEstimate actionGetEstimate;
    ImageButton previous, next;

    public CODConfirmFragment() {
        // Required empty public constructor
    }

    public static CODConfirmFragment newInstance(String locations) {
        Bundle args = new Bundle();
        args.putString("locations", locations);
        CODConfirmFragment fragment = new CODConfirmFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_codconfirm, container, false);
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
                returnToPickup = (LinearLayout) view.findViewById(R.id.return_to_pickup);
                recipentPay = (LinearLayout) view.findViewById(R.id.recipent_pay_fee);
                containerLocation = view.findViewById(R.id.container_location);

                dropAddress = (TextView) view.findViewById(R.id.drop_address);
                dropRecipent = (TextView) view.findViewById(R.id.drop_recipient);
                dropNote = (TextView) view.findViewById(R.id.drop_note);
                dropCall = (ImageView) view.findViewById(R.id.drop_call);

                icExpand = (ImageView) view.findViewById(R.id.ic_expand);

                pager = (ViewPager) view.findViewById(R.id.view_pager);
                tab = (TabLayout) view.findViewById(R.id.tab);
                adapter = new CODPagerAdapter(getActivity(), points.subList(1, points.size()));

                serviceFee = (TextView) view.findViewById(R.id.service_fee);
                shippingFee = (TextView) view.findViewById(R.id.shipping_fee);
                total = (TextView) view.findViewById(R.id.total);
                returnToPickup.setTag(false);
                if (points.get(1).isX15()) {
                    returnToPickup.setTag(true);
                }
                recipentPay.setTag(false);
                returnToPickup.setOnClickListener(CODConfirmFragment.this);
                recipentPay.setOnClickListener(CODConfirmFragment.this);
                view.findViewById(R.id.detail).setOnClickListener(CODConfirmFragment.this);
                dropCall.setOnClickListener(CODConfirmFragment.this);
                view.findViewById(R.id.fee_shipping).setOnClickListener(CODConfirmFragment.this);
                view.findViewById(R.id.fee_service).setOnClickListener(CODConfirmFragment.this);
                previous = (ImageButton) view.findViewById(R.id.previous);
                next = (ImageButton) view.findViewById(R.id.next);
                totalCashDelivery = (TextView) view.findViewById(R.id.total_cash_delivery);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeanPoint drop = points.get(0);
                        dropAddress.setText(drop.getAddress() + StringUtils.EMPTY);
                        dropRecipent.setText(drop.getRecipent_name() + StringUtils.EMPTY);
                        if (!drop.getNote().equals(StringUtils.EMPTY)) {
                            dropNote.setText(drop.getNote() + StringUtils.EMPTY);
                        } else {
                            view.findViewById(R.id.drop_note_area).setVisibility(View.GONE);
                        }
                        pager.setOffscreenPageLimit(points.size() - 1);
                        pager.setAdapter(adapter);
                        tab.setupWithViewPager(pager);

                        Fragment parent = getParentFragment();
                        if (parent != null && parent instanceof ConfirmFragment) {
                            ConfirmFragment confirmFragment = (ConfirmFragment) parent;
                            getEstimate(confirmFragment.dateTime, (Boolean) returnToPickup.getTag());
                        }

                        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                int position = tab.getPosition();
                                previous.setImageResource(R.drawable.ic_arrow_previous_orange);
                                next.setImageResource(R.drawable.ic_arrow_next_orange);
                                if (position == 0) {
                                    previous.setImageResource(R.drawable.ic_arrow_previous);
                                    next.setImageResource(R.drawable.ic_arrow_next_orange);
                                }
                                if (position == adapter.getCount() - 1) {
                                    previous.setImageResource(R.drawable.ic_arrow_previous_orange);
                                    next.setImageResource(R.drawable.ic_arrow_next);
                                }
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });

                        previous.setOnClickListener(CODConfirmFragment.this);
                        next.setOnClickListener(CODConfirmFragment.this);
                        containerLocation.setVisibility(View.GONE);

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
        if (actionGetEstimate != null) {
            actionGetEstimate.cancel(true);
        }
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
            case R.id.return_to_pickup:
                returnToPickup.setTag(!(boolean) returnToPickup.getTag());
                resetLayout(returnToPickup);
                Fragment fragment = getParentFragment();
                if (fragment instanceof ConfirmFragment) {
                    ConfirmFragment confirmFragment = (ConfirmFragment) fragment;
                    getEstimate(confirmFragment.dateTime, (Boolean) returnToPickup.getTag());
                }
                break;
            case R.id.recipent_pay_fee:
                recipentPay.setTag(!(boolean) recipentPay.getTag());
                resetLayout(recipentPay);
                break;
            case R.id.detail:
                if (containerLocation.getVisibility() == View.GONE) {
                    containerLocation.setVisibility(View.VISIBLE);
                    icExpand.animate().rotation(180).setDuration(200).start();
                } else {
                    containerLocation.setVisibility(View.GONE);
                    icExpand.animate().rotation(0).setDuration(200).start();
                }
                break;
            case R.id.drop_call:
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", points.get(0).getPhone(), null));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.previous:
                if (pager.getCurrentItem() == 0) {
                    //pager.setCurrentItem(pager.getChildCount() - 1);
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
                }
                break;
            case R.id.next:
                if (pager.getCurrentItem() == pager.getChildCount() - 1) {
                    // pager.setCurrentItem(0);
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
                break;
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

        // Quay lai dia diem nhan hang + 20.000
        if (layout.getId() == R.id.return_to_pickup) {
            points.get(1).setX15((boolean) layout.getTag());
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
                    totalCashDelivery.setText(CmmFunc.formatMoney(jsonObject.getInt("total_cod"), true));
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
}
