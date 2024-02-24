package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanOrder;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.FollowJourneyFragment;
import vn.nip.around.Fragment.Common.FullOrder.FullOrderFragment;
import vn.nip.around.Fragment.Common.FullOrderCommonFragment;
import vn.nip.around.Fragment.Common.OrderFollowFragment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

import com.smartfoxserver.v2.entities.data.SFSObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import sfs2x.client.requests.ExtensionRequest;

/**
 * Created by viminh on 10/6/2016.
 */

public class OrderFollowAdapter extends RecyclerView.Adapter<OrderFollowAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanOrder> list;
    OrderFollowFragment fragment;
    public OrderFollowAdapter(FragmentActivity activity, OrderFollowFragment fragment, RecyclerView recycler, List<BeanOrder> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.fragment = fragment;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_follow, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            BeanOrder item = list.get(position);
            if (item != null) {
                if (item.getStatus() == 0) {
                    holder.status.setTextColor(activity.getResources().getColor(R.color.gifting));
                    holder.status.setText(activity.getResources().getString(R.string.processing));
                } else if (item.getStatus() == -2) {
                    holder.status.setTextColor(activity.getResources().getColor(R.color.main));
                    holder.status.setText(activity.getResources().getString(R.string.scheduled));
                }
                holder.total.setText(CmmFunc.formatMoney(item.getTotal(), true) + "");
                holder.type.setText(BeanOrder.getStringType(item));
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getCreate_date(), df);
                holder.date.setText(DateTimeHelper.parseDate(dateTime));
                holder.time.setText(dateTime.toString("hh:mm"));
                holder.ampm.setText(dateTime.toString("a"));
                holder.orderCode.setText(item.getOrder_code());

                if (item.getType().equals("GIFTING")) {
                    holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_history_gifting));
                    holder.line.setBackgroundColor(activity.getResources().getColor(R.color.gifting));
                } else {
                    holder.icon.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_history_pickup));
                    holder.line.setBackgroundColor(activity.getResources().getColor(R.color.main));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            //fragment.showProgress();
            //itemView.setOnClickListener(null);
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanOrder item = list.get(itemPosition);
            if (item != null) {
                if (item.getStatus() == 0) {
//                    FollowJourneyFragment fragment = FollowJourneyFragment.isExist();
//                    if(fragment != null){
//                        FragmentHelper.removeFragment(activity, fragment);
//                    }

                    FragmentHelper.replaceFragment(activity, R.id.home_content, FollowJourneyFragment.newInstance(item.getId(), true));

                } else if (item.getStatus() == -2) {
                    FullOrderFragment fragment = FullOrderFragment.newInstance(item.getId());
                    FragmentHelper.addFragment(activity, R.id.home_content, fragment);
                }
            }
        } catch (Exception e) {
            Log.e("OrderHistoryAdapter", "ViMT - onClick: " + e.getMessage());
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView total;
        private TextView status;
        private TextView date;
        private TextView time;
        private TextView ampm;
        private TextView orderCode;
        private ImageView icon;
        FrameLayout line;

        public MenuViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            total = (TextView) view.findViewById(R.id.total);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            ampm = (TextView) view.findViewById(R.id.ampm);
            orderCode = (TextView) view.findViewById(R.id.order_code);
            status = (TextView) view.findViewById(R.id.status);
            icon = (ImageView) view.findViewById(R.id.icon);
            line = (FrameLayout) view.findViewById(R.id.line);
        }
    }
}