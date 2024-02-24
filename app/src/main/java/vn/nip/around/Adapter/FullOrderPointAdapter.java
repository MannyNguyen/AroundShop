package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import vn.nip.around.Bean.BeanItem;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.ViewFullOrderFragment;
import vn.nip.around.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viminh on 10/7/2016.
 */

public class FullOrderPointAdapter extends RecyclerView.Adapter<FullOrderPointAdapter.OrderViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    List<BeanPoint> list;
    boolean isShow_gift;

    public FullOrderPointAdapter() {
    }

    public FullOrderPointAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanPoint> list, boolean isShow_gift) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
        this.isShow_gift = isShow_gift;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_full_order_pickup, parent, false);
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_full_oder_drop_view, parent, false);
        } else if (viewType == 0) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_full_order_pickup_first, parent, false);
        }
        itemView.setOnClickListener(this);
        return new OrderViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size() - 1) {
            return 1;
        } else if (position == 0) {
            return 0;
        }
        return 2;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        try {
            BeanPoint item = list.get(position);
            if (item != null) {
                holder.address.setText(item.getAddress() + "");
                if (holder.number != null) {
                    holder.number.setText((position + 1) + "");
                }
            }
            if (position == 0) {
                setDefault();
            }

        } catch (Exception e) {
            CmmFunc.showError("OrderAdapter", "onBindViewHolder", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void setDefault() {
        BeanPoint item = list.get(0);
//        View child = recycler.getChildAt(0);
        FrameLayout isSelected = (FrameLayout) itemView.findViewById(R.id.is_selected);
        isSelected.setVisibility(View.GONE);
        if (item != null) {
            Fragment fragment = CmmFunc.getActiveFragment(activity);
            if (fragment instanceof ViewFullOrderFragment) {
                RecyclerView recycler = (RecyclerView) fragment.getView().findViewById(R.id.recycler_location_items);
                List<BeanItem> items = new ArrayList<>();
                for (BeanItem i : item.getLocation_items()) {
                    items.add(i);
                }
                BeanItem bean = new BeanItem();
                bean.setNote(item.getNote());
                bean.setPhone(item.getPhone());
                bean.setItem_name(item.getItem_name());
                bean.setRecipent_name(item.getRecipent_name());
                items.add(bean);

                FullOrderItemAdapter adapter = new FullOrderItemAdapter(activity, recycler, items, isShow_gift);
                LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                recycler.setLayoutManager(layoutManager);
                recycler.setAdapter(adapter);
            }
        }
    }


    @Override
    public void onClick(View view) {
        try {

            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanPoint item = list.get(itemPosition);
            View child = recycler.getChildAt(itemPosition);
            for (int i = 0; i < list.size() - 1; i++) {
                View child1 = recycler.getChildAt(i);
                FrameLayout isSelected = (FrameLayout) child1.findViewById(R.id.is_selected);
                isSelected.setVisibility(View.VISIBLE);
            }
            FrameLayout isSelected = (FrameLayout) child.findViewById(R.id.is_selected);
            isSelected.setVisibility(View.GONE);
            if (item != null) {
                Fragment fragment = CmmFunc.getActiveFragment(activity);
                if (fragment instanceof ViewFullOrderFragment) {
                    RecyclerView recycler = (RecyclerView) fragment.getView().findViewById(R.id.recycler_location_items);
                    List<BeanItem> items = new ArrayList<>();
                    for (BeanItem i : item.getLocation_items()) {
                        items.add(i);
                    }
                    BeanItem bean = new BeanItem();
                    bean.setNote(item.getNote());
                    bean.setPhone(item.getPhone());
                    bean.setRecipent_name(item.getRecipent_name());
                    bean.setItem_name(item.getItem_name());
                    items.add(bean);
                    FullOrderItemAdapter adapter = new FullOrderItemAdapter(activity, recycler, items, isShow_gift);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            Log.e("FullOrderPointAdapter", "ViMT - onClick: " + e.getMessage());
        }
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView number;

        public OrderViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);
            try {
                number = (TextView) view.findViewById(R.id.number);
            } catch (Exception e) {

            }


        }
    }
}