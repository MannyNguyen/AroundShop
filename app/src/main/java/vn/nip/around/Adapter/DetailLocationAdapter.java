package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.nip.around.Bean.BeanItem;
import vn.nip.around.Bean.BeanLocation;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

/**
 * Created by HOME on 7/31/2017.
 */

public class DetailLocationAdapter extends RecyclerView.Adapter<DetailLocationAdapter.OrderViewHolder> {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    List<BeanItem> list;
    String type;

    public DetailLocationAdapter() {
    }

    public DetailLocationAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanItem> list, String type) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
        this.type = type;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_detail_location_gifting, parent, false);
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_detail_location_pickup, parent, false);
        }
        return new OrderViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        if (type.equals("PICKUP")) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        try {
            BeanItem item = list.get(position);
            if (item != null) {
                holder.itemName.setText(item.getItem_name() + "");
                if (type.equals("GIFTING")) {
                    if (item.is_gift()) {
                        holder.textIsGift.setTextColor(activity.getResources().getColor(R.color.main));
                        holder.isGift.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_gift_selected));
                    } else {
                        holder.textIsGift.setTextColor(activity.getResources().getColor(R.color.gray_400));
                        holder.isGift.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_gift_unselected));
                    }
                    holder.number.setText(item.getItem_quantity() + "");
                    Picasso.with(activity).load(item.getItem_image()).into(holder.thumbnail);
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


    public class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView number;
        private TextView textIsGift;
        private ImageView isGift;
        private ImageView thumbnail;

        public OrderViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            number = (TextView) view.findViewById(R.id.number);
            textIsGift = (TextView) view.findViewById(R.id.text_is_gift);
            isGift = (ImageView) view.findViewById(R.id.is_gift);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
