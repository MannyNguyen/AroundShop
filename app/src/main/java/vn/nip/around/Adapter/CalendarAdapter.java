package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import vn.nip.around.Bean.BeanDate;
import vn.nip.around.R;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanDate> list;

    public CalendarAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanDate> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_calendar, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanDate item = list.get(position);

        holder.name.setText(item.getNameDay() + "");
        holder.number.setText(item.getDay() + "");
        if (!item.isActive()) {
            holder.name.setTextColor(activity.getResources().getColor(R.color.gray_400));
            holder.number.setTextColor(activity.getResources().getColor(R.color.gray_400));
            holder.bottom.setVisibility(View.INVISIBLE);
            return;
        }

        if (item.isSelected()) {
            holder.bottom.setVisibility(View.VISIBLE);
            holder.name.setTextColor(activity.getResources().getColor(R.color.main));
            holder.number.setTextColor(activity.getResources().getColor(R.color.main));

        } else {
            holder.bottom.setVisibility(View.INVISIBLE);
            holder.name.setTextColor(activity.getResources().getColor(R.color.gray_600));
            holder.number.setTextColor(activity.getResources().getColor(R.color.gray_600));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanDate item = list.get(itemPosition);
            if (item != null && item.isActive()) {
                //FrameLayout bottom = (FrameLayout) recycler.getChildAt(itemPosition).findViewById(R.id.bottom);
                //bottom.setVisibility(View.VISIBLE);
                BeanDate.resetSelected(list);
                list.get(itemPosition).setSelected(true);
                recycler.smoothScrollToPosition(itemPosition);
                notifyDataSetChanged();
            }
        } catch (Exception e) {
        }

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        FrameLayout bottom;

        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            number = (TextView) view.findViewById(R.id.number);
            bottom = (FrameLayout) view.findViewById(R.id.bottom);
        }
    }
}