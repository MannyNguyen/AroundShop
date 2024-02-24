package vn.nip.around.Fragment.Common;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import vn.nip.around.Adapter.DetailLocationAdapter;
import vn.nip.around.Bean.BeanItem;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmAnimation;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailLocationFragment extends BaseFragment implements View.OnClickListener {

    public static final String PICKUP = "PICKUP";
    public static final String GIFTING = "GIFTING";
    RecyclerView recycler;
    List<BeanPoint> locations;
    FullOrderCommonFragment fullOrderCommonFragment;

    public DetailLocationFragment() {
        // Required empty public constructor
        Bundle args = new Bundle();
        args.putString("type", "");
        args.putString("data", "");
        setArguments(args);
    }

    public static DetailLocationFragment newInstance(String type, String data) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("data", data);
        DetailLocationFragment fragment = new DetailLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_detail_location, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullOrderCommonFragment = (FullOrderCommonFragment) getParentFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    locations = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("data"), BeanPoint.class);
                    if (locations == null) {
                        return;
                    }

                    locations.remove(locations.size() - 1);
                    view.findViewById(R.id.place_1).setOnClickListener(DetailLocationFragment.this);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler_location_items);
                    recycler.setHasFixedSize(true);
                    bindData(0);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (locations.size() < 2) {
                                Button place2 = (Button) view.findViewById(R.id.place_2);
                                Button place3 = (Button) view.findViewById(R.id.place_3);
                                place2.setTextColor(getResources().getColor(R.color.gray_300));
                                place3.setTextColor(getResources().getColor(R.color.gray_300));
                                place2.setEnabled(false);
                                place3.setEnabled(false);
                            } else if (locations.size() < 3) {
                                Button place3 = (Button) view.findViewById(R.id.place_3);
                                place3.setEnabled(false);
                                place3.setTextColor(getResources().getColor(R.color.gray_300));
                                view.findViewById(R.id.place_2).setOnClickListener(DetailLocationFragment.this);
                            } else {
                                view.findViewById(R.id.place_2).setOnClickListener(DetailLocationFragment.this);
                                view.findViewById(R.id.place_3).setOnClickListener(DetailLocationFragment.this);
                            }
                            TextView recipientTitle = (TextView) view.findViewById(R.id.recipent_title);
                            switch (getArguments().getString("type")) {
                                case PICKUP:
                                    recipientTitle.setText(getString(R.string.contact_name));
                                    break;
                                case GIFTING:
                                    view.findViewById(R.id.type_pickup).setVisibility(View.GONE);
                                    view.findViewById(R.id.note_area).setVisibility(View.GONE);
                                    recipientTitle.setText(getString(R.string.shop_name));
                                    break;
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.place_1:
                bindData(0);
                break;
            case R.id.place_2:
                bindData(1);
                break;
            case R.id.place_3:
                bindData(2);
                break;
        }
    }

    private void bindData(final int position) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fullOrderCommonFragment != null) {
                    View dropContainer = fullOrderCommonFragment.view.findViewById(R.id.drop_container);
                    if (position == locations.size() - 1) {
                        dropContainer.setVisibility(View.VISIBLE);
                    } else {
                        dropContainer.setVisibility(View.GONE);
                    }
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recycler.setLayoutManager(layoutManager);
                Button place1 = (Button) view.findViewById(R.id.place_1);
                Button place2 = (Button) view.findViewById(R.id.place_2);
                Button place3 = (Button) view.findViewById(R.id.place_3);
                ImageView icLocation = (ImageView) view.findViewById(R.id.ic_location);

                place1.setTextColor(getResources().getColor(R.color.gray_600));
                place1.setBackground(getResources().getDrawable(R.drawable.transport_unselected));

                if (place2.isEnabled()) {
                    place2.setTextColor(getResources().getColor(R.color.gray_600));
                    place2.setBackground(getResources().getDrawable(R.drawable.collect_unselected));
                }
                if (place3.isEnabled()) {
                    place3.setTextColor(getResources().getColor(R.color.gray_600));
                    place3.setBackground(getResources().getDrawable(R.drawable.purchase_unselected));
                }


                switch (position) {
                    case 0:
                        place1.setTextColor(getResources().getColor(R.color.white));
                        place1.setBackground(getResources().getDrawable(R.drawable.transport_selected));
                        icLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_1));
                        break;
                    case 1:
                        place2.setTextColor(getResources().getColor(R.color.white));
                        place2.setBackground(getResources().getDrawable(R.drawable.collect_selected));
                        icLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_2));
                        break;
                    case 2:
                        place3.setTextColor(getResources().getColor(R.color.white));
                        place3.setBackground(getResources().getDrawable(R.drawable.purchase_selected));
                        icLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_3));
                        break;
                }
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                final BeanPoint bean = locations.get(position);
                final String address = bean.getAddress();
                final String recipient = bean.getRecipent_name();
                final String phone = bean.getPhone();
                final int type = bean.getPickup_type();
                final String note = bean.getNote();
                final int itemcost = bean.getItem_cost();
                final TextView pickupAddress = (TextView) view.findViewById(R.id.pickup_addresss);
                final TextView recipentName = (TextView) view.findViewById(R.id.recipent_name);
                final ImageButton phoneView = (ImageButton) view.findViewById(R.id.call);
                final LinearLayout containerItem = (LinearLayout) view.findViewById(R.id.container_item);
                final View payNow15 = view.findViewById(R.id.pay_now_15);
                final View actualBillContainer = view.findViewById(R.id.actual_money_container);
                final View imgBillContainer = view.findViewById(R.id.img_bill_container);
                final View imgBill = view.findViewById(R.id.img_bill);
                final TextView actualMoney = (TextView) view.findViewById(R.id.actual_money);
                final TextView realityMessage = (TextView) view.findViewById(R.id.reality_title);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pickupAddress.setText(address);
                            recipentName.setText(recipient);
                            if (recipient.equals("") && phone.equals("")) {
                                view.findViewById(R.id.contact_container).setVisibility(View.GONE);
                            }
                            if (phone.equals("")) {
                                phoneView.setImageDrawable(getResources().getDrawable(R.drawable.ic_uncall));
                                phoneView.setOnClickListener(null);
                            } else {
                                view.findViewById(R.id.contact_container).setVisibility(View.VISIBLE);
                                phoneView.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_now));
                                phoneView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                                        startActivity(intent);
                                    }
                                });
                            }


                            switch (getArguments().getString("type")) {
                                case PICKUP:
                                    TextView pickupType = (TextView) view.findViewById(R.id.type_pickup);
                                    switch (type) {
                                        case 1:
                                            pickupType.setText(getString(R.string.transport));
                                            break;
                                        case 2:
                                            pickupType.setText(getString(R.string.purchase));
                                            realityMessage.setText(getString(R.string.reality_purchase));
                                            break;
                                        case 3:
                                            pickupType.setText(getString(R.string.collect));
                                            realityMessage.setText(getString(R.string.reality_cod));
                                            break;
                                    }
                                    TextView noteView = (TextView) view.findViewById(R.id.note);
                                    if (!note.trim().equals("")) {
                                        view.findViewById(R.id.note_area).setVisibility(View.VISIBLE);
                                        noteView.setText(note);
                                    } else {
                                        view.findViewById(R.id.note_area).setVisibility(View.GONE);
                                    }

                                    TextView itemCost = (TextView) view.findViewById(R.id.fee);
                                    if (itemcost == 0) {
                                        itemCost.setVisibility(View.GONE);
                                    } else {
                                        itemCost.setVisibility(View.VISIBLE);
                                        itemCost.setText(CmmFunc.formatMoney(itemcost, true));
                                    }


//                                    DetailLocationAdapter adapter = new DetailLocationAdapter(getActivity(), recycler, bean.getLocation_items(), PICKUP);
//                                    recycler.setAdapter(adapter);
                                    containerItem.removeAllViews();
                                    View row1 = getActivity().getLayoutInflater().inflate(R.layout.row_detail_location_pickup, null);
                                    TextView name = (TextView) row1.findViewById(R.id.item_name);
                                    name.setText(bean.getLocation_items().get(0).getItem_name());
                                    containerItem.addView(row1);

                                    break;
                                case GIFTING:
                                    //DetailLocationAdapter adapter1 = new DetailLocationAdapter(getActivity(), recycler, bean.getLocation_items(), GIFTING);
                                    //recycler.setAdapter(adapter1);

                                    containerItem.removeAllViews();
                                    for (BeanItem item : bean.getLocation_items()) {
                                        View row = getActivity().getLayoutInflater().inflate(R.layout.row_detail_location_gifting, null);
                                        TextView itemName = (TextView) row.findViewById(R.id.item_name);
                                        TextView number = (TextView) row.findViewById(R.id.number);
                                        TextView textIsGift = (TextView) row.findViewById(R.id.text_is_gift);
                                        TextView fee = (TextView) row.findViewById(R.id.fee);
                                        ImageView isGift = (ImageView) row.findViewById(R.id.is_gift);
                                        ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);
                                        itemName.setText(item.getItem_name());
                                        if (item.is_gift()) {
                                            row.findViewById(R.id.is_gift_area).setVisibility(View.VISIBLE);
                                            textIsGift.setTextColor(getResources().getColor(R.color.main));
                                            //textIsGift.setText(getString(R.string.gift_box) + " " + CmmVariable.giftBoxFee);
                                            isGift.setImageDrawable(getResources().getDrawable(R.drawable.ic_gift_selected));
                                        } else {
                                            row.findViewById(R.id.is_gift_area).setVisibility(View.INVISIBLE);
                                            //textIsGift.setTextColor(getResources().getColor(R.color.gray_400));
                                            //isGift.setImageDrawable(getResources().getDrawable(R.drawable.ic_gift_unselected));
                                        }
                                        number.setText(item.getItem_quantity() + "");
                                        Picasso.with(getActivity()).load(item.getItem_image()).resize(200, 200).into(thumbnail);
                                        if (bean.getLocation_items().indexOf(item) == bean.getLocation_items().size() - 1) {
                                            row.findViewById(R.id.line).setVisibility(View.GONE);
                                        }

                                        if (item.getItem_prepare_time() == 0) {
                                            row.findViewById(R.id.prepare_container).setVisibility(View.GONE);
                                        } else {
                                            row.findViewById(R.id.prepare_container).setVisibility(View.VISIBLE);
                                            TextView prepare = (TextView) row.findViewById(R.id.prepare_time);
                                            prepare.setText(item.getItem_prepare_time() + " " + getString(R.string.hour));
                                        }
                                        fee.setText(CmmFunc.formatMoney(item.getItem_cost(), true));

                                        containerItem.addView(row);
                                    }
                                    break;
                            }
                            payNow15.setVisibility(View.GONE);
                            if (bean.isX15()) {
                                payNow15.setVisibility(View.VISIBLE);
                            }
                            actualBillContainer.setVisibility(View.GONE);
                            if (bean.getBill_price() > 0) {
                                actualBillContainer.setVisibility(View.VISIBLE);
                                actualMoney.setText(CmmFunc.formatMoney(bean.getBill_price(), true));
                            }

                            imgBillContainer.setVisibility(View.GONE);
                            if (!bean.getBill_image().equals(StringUtils.EMPTY)) {
                                imgBillContainer.setVisibility(View.VISIBLE);
                                imgBill.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((BaseFragment) getParentFragment()).showProgress();
                                                Bitmap bitmap = CmmFunc.getBitmapFromURL(bean.getBill_image());
                                                ImageFragment imageFragment = ImageFragment.newInstance();
                                                imageFragment.bitmap = bitmap;
                                                FragmentHelper.addFragment(getActivity(), R.id.home_content, imageFragment);
                                                ((BaseFragment) getParentFragment()).hideProgress();
                                            }
                                        }).start();


                                    }
                                });
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

    }
}
