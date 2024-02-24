package vn.nip.around.Adapter;

import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Banner.BannerHomeFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    public List<BeanBannerItem> list;


    public BannerAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanBannerItem> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_banner, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, int position) {
        try {
            final BeanBannerItem item = list.get(position);
            if (item != null) {
                BeanProduct beanProduct = item.getProduct();


                if (beanProduct.getSave_percent() == 0) {
                    holder.saleContainer.setVisibility(View.GONE);
                } else {
                    holder.sale.setText(beanProduct.getSave_percent() + "%");
                }
                holder.title.setText(beanProduct.getName() + "");
                holder.price.setText(CmmFunc.formatMoney(beanProduct.getPrice()));
                if (beanProduct.getOld_price() == 0) {
                    holder.oldPrice.setVisibility(View.GONE);
                } else {
                    holder.oldPrice.setText(CmmFunc.formatMoney(beanProduct.getOld_price()));
                    holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

//                holder.buyNow.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        activity.getSupportFragmentManager().popBackStack();
//                        new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getProduct().getId());
//                    }
//                });
                holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                Picasso.with(activity).load(beanProduct.getImage()).resize(CmmFunc.convertDpToPx(activity, 400), CmmFunc.convertDpToPx(activity, 400)).centerInside().into(holder.image);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.gravity = Gravity.BOTTOM;
                holder.detailContainer.setLayoutParams(layoutParams1);

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
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanBannerItem item = list.get(itemPosition);
            if (item != null) {
                activity.getSupportFragmentManager().popBackStack();
                new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getProduct().getId());
            }

        } catch (Exception e) {
            Log.e("BannerAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    class ActionAddCart extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                params.add(new AbstractMap.SimpleEntry("number", 1 + ""));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/add_product_to_cart", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {

            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {

                int code = jsonObject.getInt("code");
                if (code == 1) {
                    Fragment fragment = CmmFunc.getActiveFragment(activity);
                    if (fragment instanceof BannerHomeFragment) {
                        activity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    CmmFunc.setDelay(400, new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.addFragment(activity, R.id.home_content, CartFragment.newInstance());
                        }
                    });

                }
            } catch (Exception e) {

            }
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView oldPrice;
        TextView price;
        TextView sale;
        FrameLayout saleContainer;
        View buyNow;
        LinearLayout detailContainer;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            title = (TextView) view.findViewById(R.id.title);
            oldPrice = (TextView) view.findViewById(R.id.old_price);
            price = (TextView) view.findViewById(R.id.price);
            sale = (TextView) view.findViewById(R.id.sale);
            saleContainer = (FrameLayout) view.findViewById(R.id.sale_container);
            buyNow = view.findViewById(R.id.buy_now);
            detailContainer = (LinearLayout) view.findViewById(R.id.detail_container);

            int fourDP = CmmFunc.convertDpToPx(activity, 4);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, recycler.getHeight() / 2 - fourDP * 2);
            layoutParams.setMargins(fourDP, fourDP, fourDP, fourDP);
            itemView.setLayoutParams(layoutParams);
        }


    }
}