package vn.nip.around.Adapter;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import vn.nip.around.Bean.BeanShop;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Fragment.Shopping.ChildShoppingFragment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.MenuViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<BeanShop> list;


    public ShoppingAdapter(Fragment fragment, RecyclerView recycler, List<BeanShop> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_shopping, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            final BeanShop item = list.get(position);
            if (item != null) {
                holder.check.setImageResource(R.drawable.ic_uncheck);
                holder.title.setText(BeanShop.class.getDeclaredField(fragment.getString(R.string.key_name)).get(item) + " (" + item.getNumber() + " " + fragment.getString(R.string.product) + ")");
                if (item.isSelected()) {
                    holder.check.setImageResource(R.drawable.ic_check);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (BeanShop bean : list) {
                            if (bean.isSelected()) {
                                bean.setSelected(false);
                            }
                        }
                        item.setSelected(true);
                        notifyDataSetChanged();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView check;

        public MenuViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            check = (ImageView) view.findViewById(R.id.check);
        }


    }
}