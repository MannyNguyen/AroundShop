package vn.nip.around.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanEmoticion;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 * Hien thi trong man hinh CommonConfirmFragment
 */

public class TransportPurchaseConfirmAdapter extends RecyclerView.Adapter<TransportPurchaseConfirmAdapter.MenuViewHolder> {
    View itemView;
    BaseFragment fragment;
    RecyclerView recycler;
    List<BeanPoint> list;

    public TransportPurchaseConfirmAdapter(BaseFragment fragment, RecyclerView recycler, List<BeanPoint> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_detail_pickup_location, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        BeanPoint bean = list.get(position);
        if (bean != null) {
            try {
                holder.dropLocation.append(" " + (position + 1));
                switch (bean.getPickup_type()) {
                    case 1:
                        holder.pickupType.setText(fragment.getString(R.string.transport));
                        break;
                    case 2:
                        holder.pickupType.setText(fragment.getString(R.string.purchase));
                        break;

                }
                switch (position) {
                    case 0:
                        holder.icLocation.setImageResource(R.drawable.ic_location_1);
                        break;
                    case 1:
                        holder.icLocation.setImageResource(R.drawable.ic_location_2);
                        break;
                    case 2:
                        holder.icLocation.setImageResource(R.drawable.ic_location_3);
                        break;
                }

                if (bean.getItem_cost() == 0) {
                    holder.fee.setVisibility(View.GONE);
                } else {
                    holder.fee.setText(CmmFunc.formatMoney(bean.getItem_cost(), true));
                }


                holder.pickupAddress.setText(bean.getAddress());
                holder.itemName.setText(bean.getItem_name());

                holder.paynow15.setVisibility(View.GONE);
                if (bean.isX15()) {
                    holder.paynow15.setVisibility(View.VISIBLE);
                }
                holder.noteArea.findViewById(R.id.note_area).setVisibility(View.GONE);
                if (!bean.getNote().trim().equals("")) {
                    holder.noteArea.setVisibility(View.VISIBLE);
                    holder.noteView.setText(bean.getNote());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView dropLocation, pickupType, fee, pickupAddress, itemName, noteView, paynow15;
        ImageView icLocation;
        View noteArea;

        public MenuViewHolder(View view) {
            super(view);
            dropLocation = (TextView) view.findViewById(R.id.drop_location);
            pickupType = (TextView) view.findViewById(R.id.type_pickup);
            icLocation = (ImageView) view.findViewById(R.id.ic_location);
            fee = (TextView) view.findViewById(R.id.fee);
            pickupAddress = (TextView) view.findViewById(R.id.pickup_addresss);
            itemName = (TextView) view.findViewById(R.id.item_name);
            noteView = (TextView) view.findViewById(R.id.note);
            paynow15 = (TextView) view.findViewById(R.id.pay_now_15);
            noteArea = view.findViewById(R.id.note_area);
        }
    }
}