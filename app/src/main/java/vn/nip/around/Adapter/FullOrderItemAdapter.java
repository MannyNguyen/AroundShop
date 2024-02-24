package vn.nip.around.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanItem;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

import java.util.List;

/**
 * Created by viminh on 10/7/2016.
 */

public class FullOrderItemAdapter extends RecyclerView.Adapter<FullOrderItemAdapter.OrderViewHolder> {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    List<BeanItem> list;
    boolean isShowGift;

    public FullOrderItemAdapter() {
    }

    public FullOrderItemAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanItem> list, boolean isShowGift) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
        this.isShowGift = isShowGift;
    }


    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_location_item, parent, false);
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_location_recipent, parent, false);
        }
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
            final BeanItem item = list.get(position);
            if (item != null) {
                if (holder.itemName != null) {
                    holder.itemName.setText(item.getItem_name() + "");
                }

                if (!item.getNote().equals("")) {
                    if (holder.containerNote != null) {
                        holder.containerNote.setVisibility(View.VISIBLE);
                        holder.note.setText(item.getNote() + "");
                    }
                }
                if (holder.recipentName != null) {
                    holder.recipentName.setText(item.getRecipent_name() + "");
                }

                if (holder.isGift != null) {
                    if (isShowGift == false) {
                        holder.isGift.setVisibility(View.GONE);
                    } else {
                        holder.isGift.setVisibility(View.VISIBLE);
                        if (item.is_gift()) {
                            holder.isGift.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_plus_gift));
                        } else {
                            holder.isGift.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_plus_gift_disable));
                        }
                    }
                }

                if (holder.recipentNameText != null) {
                    if (isShowGift == false) {
                        holder.recipentNameText.setText(activity.getString(R.string.contact_name));
                    } else {
                        holder.recipentNameText.setText(activity.getString(R.string.shop_name));
                    }
                }

                if(holder.call!=null){
                    final String phone = item.getPhone();
                    holder.call.setTag(phone);
                    holder.call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                            activity.startActivity(intent);
                        }
                    });
                }

                if(holder.itemIndex != null){
                    holder.itemIndex.append(" " + (position + 1));
                }

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
        TextView itemName;
        TextView note;
        TextView recipentName;
        TextView recipentNameText;
        TextView itemIndex;
        ImageView isGift;
        ImageView call;
        View containerNote;

        public OrderViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            note = (TextView) view.findViewById(R.id.note);
            recipentName = (TextView) view.findViewById(R.id.recipent_name);
            recipentNameText = (TextView) view.findViewById(R.id.recipent_name_text);
            itemIndex = (TextView) view.findViewById(R.id.item_index);
            isGift = (ImageView) view.findViewById(R.id.is_gift);
            call = (ImageView) view.findViewById(R.id.call);
            containerNote = view.findViewById(R.id.container_note);
        }
    }
}