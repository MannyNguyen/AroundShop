package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanRate;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Common.RateFragment;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanRate> list;
    RateFragment fragment;


    public RateAdapter(FragmentActivity activity, RateFragment fragment, RecyclerView recycler, List<BeanRate> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
        this.fragment = fragment;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_rate, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        if ((position + 1) % 3 == 0) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            layoutParams.setMargins(CmmFunc.convertDpToPx(GlobalClass.getActivity(), 64), 0, CmmFunc.convertDpToPx(GlobalClass.getActivity(), 64), 0);

            holder.itemView.setLayoutParams(layoutParams);
        }
        BeanRate item = list.get(position);
        holder.text.setText(item.getName() + "");
        if (StorageHelper.getLanguage().equals("vi")) {
            holder.text.setText(item.getVn_name() + "");
        }

        if (item.isCheck()) {
            holder.text.setBackground(activity.getResources().getDrawable(R.drawable.solid_gray_400));
            holder.text.setTextColor(activity.getResources().getColor(R.color.white));
        } else {
            holder.text.setBackground(activity.getResources().getDrawable(R.drawable.border_gray_400));
            holder.text.setTextColor(activity.getResources().getColor(R.color.gray_400));
        }

        int pad = CmmFunc.convertDpToPx(activity, 12);
        holder.text.setPadding(0, pad, 0, pad);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanRate item = list.get(itemPosition);
            if (item != null) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setCheck(false);
                }
                list.get(itemPosition).setCheck(true);
                notifyDataSetChanged();
                fragment.confirm.setOnClickListener(fragment);
            }
            switch (itemPosition) {
                case 0:
                    Tracking.excute("C10.5Y");
                    break;
                case 1:
                    Tracking.excute("C10.2Y");
                    break;
                case 2:
                    Tracking.excute("C10.3Y");
                    break;
                case 3:
                    Tracking.excute("C10.6Y");
                    break;
                case 4:
                    Tracking.excute("C10.4Y");
                    break;
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = CmmFunc.getActiveFragment(activity);
                    if (fragment instanceof RateFragment) {
                        CardView confirm = (CardView) fragment.getView().findViewById(R.id.confirm);
                        confirm.setCardBackgroundColor(activity.getResources().getColor(R.color.main));
                        confirm.setOnClickListener((RateFragment) fragment);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("CategoryAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView text;

        public MenuViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
        }
    }
}