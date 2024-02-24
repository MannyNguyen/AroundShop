package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Bean.BeanAddress;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Pickup.AddressFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.InfoBookFragment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.MenuViewHolder> implements View.OnClickListener {

    final int FIRST = 1;
    final int CENTER = 2;
    final int LAST = 3;

    View itemView;
    HomeBookFragment fragment;
    RecyclerView recycler;
    private List<BeanPoint> list;

    public boolean isError;


    public PointAdapter(HomeBookFragment fragment, RecyclerView recycler, List<BeanPoint> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_book_center, parent, false);
        if (viewType == FIRST) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_book_first, parent, false);
        } else if (viewType == LAST) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_book_last, parent, false);
        }
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, final int position) {
        final BeanPoint item = list.get(position);
        final int pos = list.indexOf(item);
        if (item != null) {
            holder.address.setText(item.getAddress());
            holder.edit.setImageResource(R.drawable.ic_pickup_edit);
            holder.address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, AddressFragment.newInstance(list.indexOf(item), AddressFragment.PICKUP));
                }
            });

            switch (item.getPickup_type()) {
                case BeanPoint.TRANSPORT:
                    holder.address.setHint(fragment.getString(R.string.hint_transport));
                    break;
                case BeanPoint.PURCHASE:
                    holder.address.setHint(fragment.getString(R.string.hint_purchase));
                    break;
                case BeanPoint.COD:
                    holder.address.setHint(fragment.getString(R.string.hint_cod));
                    break;
                case BeanPoint.DROP:
                    holder.address.setHint(fragment.getString(R.string.drop_location));
                    break;
            }

            if (getItemViewType(pos) == CENTER) {
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = list.indexOf(item);
                        list.remove(item);
                        notifyItemRemoved(pos);
                        if (fragment.addPoint.getVisibility() == View.GONE) {
                            fragment.addPoint.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < fragment.points.size(); i++) {
                            BeanPoint bean = fragment.points.get(i);
                            bean.setTag(i);
                            int index = fragment.points.indexOf(bean);
                            if (index == fragment.points.size() - 1) {
                                bean.setTag(3);
                            }
                        }

                        fragment.reloadMap();

                    }
                });
            }
            if (holder.edit != null) {
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int pos = list.indexOf(item);
                        if (item.getAddress().equals(StringUtils.EMPTY)) {
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, AddressFragment.newInstance(pos, AddressFragment.PICKUP));
                            return;
                        }
                        InfoBookFragment.newInstance(item.getPickup_type(), pos).show(fragment.getActivity().getSupportFragmentManager(), InfoBookFragment.class.getName());
                    }
                });
            }
            if (isError) {
                if (item.getAddress() == null || item.getAddress().equals("")) {
                    showError(holder.address);
                } else {
                    if (pos == list.size() - 1) {
                        if (item.getRecipent_name().equals(StringUtils.EMPTY)) {
                            holder.edit.setImageResource(R.drawable.ic_pickup_edit_red);
                            showError(holder.edit);
                        }
                    } else {
                        if (item.getItem_name().equals("")) {
                            holder.edit.setImageResource(R.drawable.ic_pickup_edit_red);
                            showError(holder.edit);
                        }
                    }

                }
            }


        }
    }

    private void showError(View error) {
        error.setVisibility(View.VISIBLE);
        final Animation animShake = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.anim_shake);
        error.startAnimation(animShake);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FIRST;
        }
        if (position == list.size() - 1) {
            return LAST;
        }
        return CENTER;
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanPoint item = list.get(itemPosition);
//            if (item != null) {
//            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        ImageButton remove;
        ImageButton edit;

        public MenuViewHolder(View view) {
            super(view);
            address = (TextView) view.findViewById(R.id.address);
            remove = (ImageButton) view.findViewById(R.id.remove);
            edit = (ImageButton) view.findViewById(R.id.edit);
        }


    }
}