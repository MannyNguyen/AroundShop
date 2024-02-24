package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

import java.util.List;

/**
 * Created by viminh on 10/7/2016.
 */

public class FullOrderAdapter extends RecyclerView.Adapter<FullOrderAdapter.OrderViewHolder> {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    List<BeanPoint> list;

    public FullOrderAdapter() {
    }

    public FullOrderAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanPoint> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_full_order, parent, false);
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_full_order_drop, parent, false);
        }
        return new OrderViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size() - 1) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        try {
            BeanPoint item = list.get(position);
            if (item != null) {
                holder.address.setText(item.getAddress() + "");
                holder.itemName.setText(item.getItem_name() + "");
                holder.number.setText((position + 1) + "");
            }
        } catch (Exception e) {
            CmmFunc.showError("OrderAdapter", "onBindViewHolder", e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView address;
        private TextView itemName;
        private TextView number;
        FrameLayout container;

        public OrderViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.location);
            itemName = (TextView) view.findViewById(R.id.package_name);
            number = (TextView) view.findViewById(R.id.number);
            container = (FrameLayout) view.findViewById(R.id.container);
        }
    }
}