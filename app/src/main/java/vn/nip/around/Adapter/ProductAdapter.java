package vn.nip.around.Adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.LoginActivity;
import vn.nip.around.R;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MenuViewHolder> implements View.OnClickListener {

    public static final int LIST = 1;
    public static final int GRID = 2;
    public static final int RELATE = 3;

    Fragment fragment;
    RecyclerView recycler;
    private List<BeanProduct> list;
    int type;
    int size;

    public ProductAdapter(Fragment fragment, RecyclerView recycler, List<BeanProduct> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    public ProductAdapter(Fragment fragment, RecyclerView recycler, List<BeanProduct> list, int type) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
        this.type = type;

    }


    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_product_list, parent, false);

                break;
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_product_grid, parent, false);

                break;
            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_product_relate, parent, false);

                break;
        }

        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {

            BeanProduct item = list.get(position);
            if (item != null) {
                size = CmmFunc.convertDpToPx(fragment.getActivity(), 100);
                if (getItemViewType(position) == 2) {
                    holder.name.setText(item.getName() + "");
                    holder.price.setText(CmmFunc.formatMoney(item.getPrice(), true));
                    if (item.getOld_price() == 0) {
                        holder.oldPrice.setVisibility(View.GONE);
                    } else {
                        holder.oldPrice.setVisibility(View.VISIBLE);
                        holder.oldPrice.setText(CmmFunc.formatMoney(item.getOld_price(), true));
                        holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }

                    Picasso.with(fragment.getActivity()).load(item.getImage()).resize(size, size).centerInside().into(holder.image);
                    if (item.is_new()) {
                        holder.newContainer.setVisibility(View.VISIBLE);
                    } else {
                        holder.newContainer.setVisibility(View.GONE);
                    }

                    if (item.getSave_percent() == 0) {
                        holder.saleContainer.setVisibility(View.GONE);
                    } else {
                        holder.saleContainer.setVisibility(View.VISIBLE);
                        holder.sale.setText(item.getSave_percent() + "%");
                    }
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(AppActivity.WINDOW_WIDTH / 3, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                    holder.itemView.setLayoutParams(params);
                    return;
                }

                holder.shopName.setText(item.getShop_name() + "");
                holder.name.setText(item.getName() + "");
                holder.price.setText(CmmFunc.formatMoney(item.getPrice(), true));
                holder.rating.setRating(item.getRating());
                holder.inCart.setChecked(false);
                holder.inCart.setTag(position);
                if (item.getIn_cart() == 1) {
                    holder.inCart.setChecked(true);
                }
                Picasso.with(fragment.getActivity()).load(item.getImage()).resize(size, size).centerInside().into(holder.image);
                if (item.is_new()) {
                    holder.newContainer.setVisibility(View.VISIBLE);
                    //                if (StorageHelper.getLanguage().equals("vi")) {
                    //                    holder.newImage.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_new));
                    //                } else if (StorageHelper.getLanguage().equals("en")) {
                    //                    holder.newImage.setImageDrawable(fragment.getResources().getDrawable(R.drawable.ic_new));
                    //                }
                } else {
                    holder.newContainer.setVisibility(View.GONE);
                }

                if (item.getOld_price() == 0) {
                    holder.oldPrice.setVisibility(View.GONE);
                } else {
                    holder.oldPrice.setVisibility(View.VISIBLE);
                    holder.oldPrice.setText(CmmFunc.formatMoney(item.getOld_price(), true));
                    holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }

                if (item.getSave_percent() == 0) {
                    holder.saleContainer.setVisibility(View.GONE);
                } else {
                    holder.saleContainer.setVisibility(View.VISIBLE);
                    holder.sale.setText(item.getSave_percent() + "%");
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
    public int getItemViewType(int position) {
        if (type == LIST) {
            return 0;
        } else if (type == GRID) {
            return 1;
        } else if (type == RELATE) {
            return 2;
        }
        if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
            return 1;
        }
        return 0;
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
                int id = item.getId();
                String title = "";
                TextView textView = (TextView) view.getRootView().findViewById(R.id.title);
                if (textView != null) {
                    title = textView.getText().toString();
                }
                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ProductFragment.newInstance(id, title));
            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView shopName;
        TextView name;

        RatingBar rating;
        CheckBox inCart;
        TextView price;
        //LinearLayout ratingContainer;
        TextView sale;
        FrameLayout saleContainer;
        FrameLayout newContainer;
        ImageView newImage;
        TextView oldPrice;


        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            shopName = (TextView) view.findViewById(R.id.shop_name);
            name = (TextView) view.findViewById(R.id.name);

            rating = (RatingBar) view.findViewById(R.id.rating);
            //ratingContainer = (LinearLayout) view.findViewById(R.id.rating_container);
            inCart = (CheckBox) view.findViewById(R.id.in_cart);
            price = (TextView) view.findViewById(R.id.price);
            oldPrice = (TextView) view.findViewById(R.id.old_price);
            sale = (TextView) view.findViewById(R.id.sale);
            saleContainer = (FrameLayout) view.findViewById(R.id.sale_container);
            newContainer = (FrameLayout) view.findViewById(R.id.new_container);
            newImage = (ImageView) view.findViewById(R.id.new_image);

            if (inCart != null) {

                inCart.setOnClickListener(this);
            }

        }


        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.in_cart:
                    try {
                        if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
                            Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                            fragment.startActivity(intent);
                            return;
                        }
                        BeanProduct item = list.get(getAdapterPosition());
                        //Do control thay đổi trước khi nhấn vào
                        if (inCart.isChecked() == true) {
                            new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId());
                        } else {
                            new ActionClearCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId());

                        }
                    } catch (Exception e) {

                    }
                    break;

            }

        }


        //region Actions
        class ActionAddCart extends ActionAsync {

            @Override
            protected JSONObject doInBackground(Object... objects) {
                try {
                    int productID = (int) objects[0];
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
                    if (jsonObject == null) {
                        inCart.setChecked(false);
                        return;
                    }
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        list.get(getAdapterPosition()).setIn_cart(1);
                        BaseFragment baseFragment = (BaseFragment) fragment.getParentFragment();
                        if (baseFragment != null) {
                            final TextView numCart = (TextView) baseFragment.getView().findViewById(R.id.number_cart);
                            ImageButton cart = (ImageButton) baseFragment.getView().findViewById(R.id.cart);
                            baseFragment.getNumberCart(cart, numCart);
                        } else {
                            final TextView numCart = (TextView) fragment.getView().findViewById(R.id.number_cart);
                            ImageButton cart = (ImageButton) fragment.getView().findViewById(R.id.cart);
                            ((BaseFragment)fragment).getNumberCart(cart, numCart);
                        }
                        if (CartFragment.RECEIVER != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    APIHelper.updateDropInfo(CartFragment.RECEIVER.getFullname(), CartFragment.RECEIVER.getPhone(), "");
                                    CartFragment.RECEIVER = null;
                                }
                            }).start();
                        }

                    } else {
                        inCart.setChecked(false);
                    }
                } catch (Exception e) {

                }
            }
        }

        class ActionClearCart extends AsyncTask<Object, Void, JSONObject> {

            @Override
            protected JSONObject doInBackground(Object... objects) {
                try {
                    int productID = (int) objects[0];
                    String response = APIHelper.removeCart(productID);
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
                    if (jsonObject == null) {
                        inCart.setChecked(true);
                        return;
                    }
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        list.get(getAdapterPosition()).setIn_cart(0);
                        BaseFragment baseFragment = (BaseFragment) fragment.getParentFragment();
                        if (baseFragment != null) {
                            final TextView numCart = (TextView) baseFragment.getView().findViewById(R.id.number_cart);
                            ImageButton cart = (ImageButton) baseFragment.getView().findViewById(R.id.cart);
                            baseFragment.getNumberCart(cart, numCart);
                        } else {
                            final TextView numCart = (TextView) fragment.getView().findViewById(R.id.number_cart);
                            ImageButton cart = (ImageButton) fragment.getView().findViewById(R.id.cart);
                            ((BaseFragment)fragment).getNumberCart(cart, numCart);
                        }


                    } else {
                        new ErrorHelper().excute(jsonObject);
                        inCart.setChecked(true);
                    }
                } catch (Exception e) {

                }
            }
        }

        //endregion
    }
}