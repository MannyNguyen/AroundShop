package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanAroundPay;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.AroundWalletFragment;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class AroundPayFeeAdapter extends RecyclerView.Adapter<AroundPayFeeAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    AroundWalletFragment fragment;
    private List<BeanAroundPay> list;


    public AroundPayFeeAdapter(FragmentActivity activity, AroundWalletFragment fragment, RecyclerView recycler, List<BeanAroundPay> list) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_around_pay, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanAroundPay item = list.get(position);
        holder.name.setText(CmmFunc.formatMoney(item.getValue(), false));

        if (item.isSelected()) {
            holder.name.setTextColor(activity.getResources().getColor(R.color.white));
            holder.name.setBackgroundColor(activity.getResources().getColor(R.color.main));
        } else {
            holder.name.setTextColor(activity.getResources().getColor(R.color.gray_600));
            holder.name.setBackground(activity.getResources().getDrawable(R.drawable.border_around_pay));
        }
        int pad = CmmFunc.convertDpToPx(activity, 12);
        holder.name.setPadding(pad, pad, pad, pad);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanAroundPay bean = list.get(itemPosition);
            if (bean != null) {
                BeanAroundPay.reset(list);
                fragment.customFee.setText("");
                fragment.customFee.clearFocus();
                CmmFunc.hideKeyboard(activity);
                bean.setSelected(true);
                notifyDataSetChanged();
                fragment.view.findViewById(R.id.error_message).setVisibility(View.GONE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}