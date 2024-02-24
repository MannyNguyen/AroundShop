package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanProductRelate;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class ProductRelateAdapter extends RecyclerView.Adapter<ProductRelateAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanProductRelate> list;


    public ProductRelateAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanProductRelate> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_relate, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanProductRelate item = list.get(position);
        if (item != null) {
            Picasso.with(activity).load(item.getImage()).into(holder.image);
            holder.name.setText(item.getName() + "");
            holder.price.setText(CmmFunc.formatMoney(item.getPrice()) + "");
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
            BeanProductRelate item = list.get(itemPosition);
            if (item != null) {
                int id = item.getId();
                String title = "";
                TextView textView = (TextView) view.getRootView().findViewById(R.id.title);
                if (textView != null) {
                    title = textView.getText().toString();
                }
                FragmentHelper.addFragment(activity, R.id.home_content, ProductFragment.newInstance(id, title));
            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        TextView name;
        TextView price;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
            price = (TextView) view.findViewById(R.id.price);
        }


    }
}