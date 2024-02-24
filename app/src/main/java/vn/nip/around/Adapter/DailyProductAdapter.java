package vn.nip.around.Adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.LoginActivity;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class DailyProductAdapter extends RecyclerView.Adapter<DailyProductAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<BeanProduct> list;
    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public DailyProductAdapter(Fragment fragment, RecyclerView recycler, List<BeanProduct> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_daily_product, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, final int position) {
        try {
            final BeanProduct item = list.get(position);
            if (item != null) {
                holder.name.setText(item.getName() + "");
                holder.price.setText(CmmFunc.formatMoney(item.getPrice(),true));
                if (item.getOld_price() == 0) {
                    holder.oldPrice.setVisibility(View.GONE);
                } else {
                    holder.oldPrice.setText(CmmFunc.formatMoney(item.getOld_price(),true));

                }

                if (item.getSave_percent() == 0) {
                    holder.saleContainer.setVisibility(View.GONE);
                } else {
                    holder.saleContainer.setVisibility(View.VISIBLE);
                    holder.sale.setText(item.getSave_percent() + "%");
                }
                holder.buyNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
                            Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                            fragment.startActivity(intent);
                            return;
                        }
                        new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), position);
                    }
                });


                Picasso.with(fragment.getActivity()).load(item.getImage()).into(holder.image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ActionAddCart extends ActionAsync {
        int position = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((BaseFragment) fragment).showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                position = (int) objects[1];
                String response = APIHelper.addCart(productID, 1);
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
                    list.get(position).setIn_cart(1);
                    notifyDataSetChanged();
                    BaseFragment baseFragment = (BaseFragment) fragment.getParentFragment();
                    final TextView numCart = (TextView) baseFragment.getView().findViewById(R.id.number_cart);
                    ImageButton cart = (ImageButton) baseFragment.getView().findViewById(R.id.cart);
                    baseFragment.getNumberCart(cart, numCart);
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, CartFragment.newInstance());

                } else {
                }
            } catch (Exception e) {

            } finally {
                ((BaseFragment) fragment).hideProgress();
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
                Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                fragment.startActivity(intent);
                return;
            }
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanProduct item = list.get(itemPosition);
            if (item != null) {
                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ProductFragment.newInstance(item.getId(), StringUtils.EMPTY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView oldPrice;
        TextView sale;
        ImageView image;
        Button buyNow;
        View saleContainer;


        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            price = (TextView) view.findViewById(R.id.price);
            oldPrice = (TextView) view.findViewById(R.id.old_price);
            sale = (TextView) view.findViewById(R.id.sale);
            image = (ImageView) view.findViewById(R.id.image);
            saleContainer = view.findViewById(R.id.sale_container);
            buyNow = (Button) view.findViewById(R.id.buy_now);
            oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }


}