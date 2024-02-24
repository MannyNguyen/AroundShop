package vn.nip.around.Adapter;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;

import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import com.daimajia.swipe.SwipeLayout;
import com.google.android.gms.vision.text.Text;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by viminh on 10/6/2016.
 */

public class ProductedAdapter extends RecyclerView.Adapter<ProductedAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    CartFragment fragment;


    public ProductedAdapter(FragmentActivity activity, CartFragment fragment, RecyclerView recycler, List<BeanProduct> list) {
        this.activity = activity;
        this.fragment = fragment;
        this.recycler = recycler;


    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_producted, parent, false);
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, int position) {
        try {
            BeanProduct item = fragment.products.get(position);
            if (item != null) {

                holder.shopName.setText(item.getShop_name() + "");
                holder.name.setText(item.getName() + "");
                holder.price.setText(CmmFunc.formatMoney(item.getPrice(), true));
                holder.quantity.setText(item.getNumber() + "");
                holder.quantity.setTag(position);
                holder.add.setTag(position);
                holder.delete.setTag(position);
                if (item.getIs_gift() == 1) {
                    holder.isGift.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_gift_selected));
                    //holder.textIsGift.setText(activity.getResources().getString(R.string.gift_box) + " " + CmmVariable.giftBoxFee);
                    holder.textIsGift.setTextColor(activity.getResources().getColor(R.color.main));
                } else {
                    holder.isGift.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_gift_unselected));
                    //holder.textIsGift.setText(activity.getResources().getString(R.string.gift_box));
                    holder.textIsGift.setTextColor(activity.getResources().getColor(R.color.gray_600));
                }
                Picasso.with(activity).load(item.getImage()).resize(200, 200).centerInside().into(holder.image);
                //            if(position == fragment.products.size() - 1){
                //                recycler.setVisibility(View.VISIBLE);
                //            }
                holder.swipeLayout.setOnClickListener(ProductedAdapter.this);
                holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        holder.swipeLayout.setOnClickListener(null);
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        itemView.setOnClickListener(null);
                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {
                        holder.swipeLayout.setOnClickListener(null);
                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                        itemView.setOnClickListener(null);
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                        holder.swipeLayout.setOnClickListener(null);
                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                    holder.swipeLayout.setOnClickListener(ProductedAdapter.this);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                });
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return fragment.products.size();
    }


    @Override
    public void onClick(View view) {
        try {
            if (view.getId() == R.id.swipe_layout) {
                int itemPosition = recycler.getChildLayoutPosition(view);
                BeanProduct item = fragment.products.get(itemPosition);
                if (item != null) {
                    int id = item.getId();
                    String title = "";
                    //TextView textView = (TextView) view.getRootView().findViewById(R.id.title);
//                if (textView != null) {
//                    title = textView.getText().toString();
//                }
                    FragmentHelper.addFragment(activity, R.id.home_content, ProductFragment.newInstance(id, title));
                }
            }

        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView shopName;
        TextView name;
        TextView price;
        ImageButton add;
        ImageButton remove;
        TextView quantity;
        Button delete;
        ImageButton isGift;
        TextView textIsGift;
        View isGiftArea;
        SwipeLayout swipeLayout;


        public MenuViewHolder(View view) {
            super(view);
            swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe_layout);
            image = (ImageView) view.findViewById(R.id.image);
            shopName = (TextView) view.findViewById(R.id.shop_name);
            name = (TextView) view.findViewById(R.id.name);
            price = (TextView) view.findViewById(R.id.price);
            add = (ImageButton) view.findViewById(R.id.add);
            remove = (ImageButton) view.findViewById(R.id.remove);
            quantity = (TextView) view.findViewById(R.id.quantity);
            delete = (Button) view.findViewById(R.id.delete);
            isGift = (ImageButton) view.findViewById(R.id.is_gift);
            textIsGift = (TextView) view.findViewById(R.id.text_is_gift);
            isGiftArea = view.findViewById(R.id.is_gift_area);
            delete.setOnClickListener(this);
            add.setOnClickListener(this);
            isGiftArea.setOnClickListener(this);
            remove.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            int number = Integer.parseInt(quantity.getText() + "");
            switch (view.getId()) {
                case R.id.add:
                    try {
                        BeanProduct item = fragment.products.get(getAdapterPosition());
                        number += 1;
                        new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), number);
                    } catch (Exception e) {

                    }
                    break;
                case R.id.remove:
                    try {
                        final int number1 = number - 1;
                        if (fragment.products.size() == 1 && fragment.products.get(0).getNumber() == 1) {
                            CustomDialog.Dialog2Button(activity, "", activity.getString(R.string.message_remove_last_product), activity.getString(R.string.ok), activity.getString(R.string.cancel), new ICallback() {
                                @Override
                                public void excute() {
                                    BeanProduct item = fragment.products.get(getAdapterPosition());
                                    new ActionRemoveCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), number1);
                                }
                            }, null);
                            return;
                        }
                        BeanProduct item = fragment.products.get(getAdapterPosition());
                        number -= 1;
                        new ActionRemoveCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), number);
                    } catch (Exception e) {

                    }
                    break;

                case R.id.delete:
                    try {
                        if (fragment.products.size() == 1) {
                            CustomDialog.Dialog2Button(activity, "", activity.getString(R.string.message_remove_last_product), activity.getString(R.string.ok), activity.getString(R.string.cancel), new ICallback() {
                                @Override
                                public void excute() {
                                    BeanProduct item = fragment.products.get(getAdapterPosition());
                                    new ActionClearCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId());
                                }
                            }, null);
                            return;
                        }
                        View parentRow = (View) view.getParent();
                        RecyclerView listView = (RecyclerView) parentRow.getParent();
                        final int itemPosition = listView.getChildPosition(parentRow);
                        BeanProduct item = fragment.products.get(itemPosition);
                        new ActionClearCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId());
                    } catch (Exception e) {

                    }
                    break;

                case R.id.is_gift_area:
                    try {
                        Tracking.excute("C15.3Y");
                        BeanProduct item = fragment.products.get(getAdapterPosition());
                        if (item.getIs_gift() == 1) {
                            new ActionUpdateGiftProduct().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), 0);
                        } else {
                            new ActionUpdateGiftProduct().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId(), 1);
                        }

                    } catch (Exception e) {

                    }

                    break;
            }
        }


        //region Actions

        class ActionGetNumberCart extends AsyncTask<Void, Void, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_number_product_in_cart", params);
                    JSONObject jsonObject = new JSONObject(response);
                    return jsonObject;
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "ActionGetCategory", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        if (fragment instanceof CartFragment) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            int numberCart = data.getInt("number");
                            final TextView numCart = (TextView) fragment.getView().findViewById(R.id.number_cart);
                            if (numberCart > 0) {
                                numCart.setText(numberCart + "");
                                numCart.setVisibility(View.VISIBLE);
                                numCart.animate().scaleX(1.3f).scaleY(1.3f).setDuration(200).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        numCart.animate().scaleX(1).scaleY(1).setDuration(200).start();
                                    }
                                }).start();
                                TextView numberItem = (TextView) fragment.view.findViewById(R.id.number_item);
                                numberItem.setText("(" + numberCart + " " + fragment.getString(R.string.items) + ")");

                            } else {
                                numCart.setText("0");
                                numCart.setVisibility(View.GONE);
                            }
                        }

                    }
                } catch (Exception e) {

                } finally {


                }
            }
        }

        class ActionAddCart extends AsyncTask<Object, Void, JSONObject> {

            int currentNumber;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                fragment.showProgress();
            }

            @Override
            protected JSONObject doInBackground(Object... objects) {
                JSONObject jsonObject = null;
                try {
                    int productID = (int) objects[0];
                    int number = (int) objects[1];
                    currentNumber = number;
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                    params.add(new AbstractMap.SimpleEntry("number", 1 + ""));
                    String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/add_product_to_cart", params);
                    jsonObject = new JSONObject(response);
                } catch (Exception e) {

                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    if (jsonObject == null) {
                        return;
                    }
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        quantity.setText(currentNumber + "");
                        fragment.products.get(Integer.parseInt(quantity.getTag() + "")).setNumber(currentNumber);
                        remove.setEnabled(true);
                        new ActionGetCartCost().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new ErrorHelper().excute(jsonObject);

                    }
                    new ActionGetNumberCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {

                } finally {
                    fragment.hideProgress();
                }
            }
        }

        class ActionRemoveCart extends AsyncTask<Object, Void, JSONObject> {
            int currentNumber;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                fragment.showProgress();
            }

            @Override
            protected JSONObject doInBackground(Object... objects) {
                JSONObject jsonObject = null;
                try {
                    int productID = (int) objects[0];
                    int number = (int) objects[1];
                    currentNumber = number;
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                    params.add(new AbstractMap.SimpleEntry("number", 1 + ""));
                    String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/remove_product_from_cart", params);
                    jsonObject = new JSONObject(response);

                } catch (Exception e) {

                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    if (jsonObject == null) {
                        return;
                    }
                    int code = jsonObject.getInt("code");
                    if (code == 1) {

                        new ActionGetCartCost().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        BeanProduct item = fragment.products.get(Integer.parseInt(quantity.getTag() + ""));
                        item.setNumber(currentNumber);
                        //fragment.products.get(Integer.parseInt(quantity.getTag() + "")).setNumber(currentNumber);
                        quantity.setText(currentNumber + "");
                        if (currentNumber < 1) {
                            fragment.products.remove(item);
                            notifyDataSetChanged();
                            if (fragment.products.size() == 0) {
                                FragmentHelper.pop(fragment.getActivity());
                                CartFragment.RECEIVER = null;
                            }


                        }

                    } else {
                        new ErrorHelper().excute(jsonObject);
                    }
                    new ActionGetNumberCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {

                } finally {
                    fragment.hideProgress();
                }
            }
        }

        class ActionClearCart extends AsyncTask<Object, Void, JSONObject> {
            int productID;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                fragment.showProgress();
            }

            @Override
            protected JSONObject doInBackground(Object... objects) {
                JSONObject jsonObject = null;
                try {
                    productID = (int) objects[0];
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                    String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/clear_product_from_cart", params);
                    jsonObject = new JSONObject(response);
                } catch (Exception e) {

                }
                return jsonObject;

            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    if (jsonObject == null) {
                        return;
                    }
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        new ActionGetCartCost().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        new ActionGetNumberCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        fragment.products.remove(getAdapterPosition());
                        recycler.getAdapter().notifyDataSetChanged();
                        if (fragment.products.size() == 0) {
                            FragmentHelper.pop(fragment.getActivity());
                            CartFragment.RECEIVER = null;
                        }


                    } else {
                        new ErrorHelper().excute(jsonObject);
                    }

                } catch (Exception e) {

                } finally {
                    fragment.hideProgress();
                }
            }
        }

        class ActionGetCartCost extends AsyncTask<Void, Void, JSONObject> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected JSONObject doInBackground(Void... voids) {
                JSONObject jsonObject = null;
                try {
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_cart_cost", params);
                    jsonObject = new JSONObject(response);
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "ActionGetCategory", e.getMessage());
                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        TextView itemCost = (TextView) fragment.getView().findViewById(R.id.item_cost);
                        TextView serviceFee = (TextView) fragment.getView().findViewById(R.id.service_fee);
                        TextView shippingFee = (TextView) fragment.getView().findViewById(R.id.shipping_fee);
                        TextView total = (TextView) fragment.getView().findViewById(R.id.total);
                        TextView totalTop = (TextView) fragment.getView().findViewById(R.id.total_top);
                        //RadioButton cast = (RadioButton) itemView.getRootView().findViewById(R.id.cast);
                        //RadioButton credit = (RadioButton) itemView.getRootView().findViewById(R.id.credit_card);
                        JSONObject data = jsonObject.getJSONObject("data");
                        if (data.isNull("total")) {
                            itemCost.setText("");
                            serviceFee.setText("");
                            shippingFee.setText("");
                            total.setText("");
                            totalTop.setText("");
                            // cast.setChecked(false);
                            //cast.setChecked(false);
                        }
                        itemCost.setText(CmmFunc.formatMoney(data.getInt("item_cost"), true));
                        serviceFee.setText(CmmFunc.formatMoney(data.getInt("service_fee"), true));
                        shippingFee.setText(CmmFunc.formatMoney(data.getInt("shipping_fee"), true));
                        total.setText(CmmFunc.formatMoney(data.getInt("total"), true));
                        totalTop.setText(CmmFunc.formatMoney(data.getInt("item_cost"), true));
                        //String paymentType = data.getString("payment_type");
//                        if (paymentType.equals("CASH")) {
//                            cast.setChecked(true);
//                        } else if (paymentType.equals("CREDIT_CARD")) {
//                            credit.setChecked(true);
//                        }
                    }
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "", e.getMessage());
                } finally {
                }
            }
        }

        class ActionUpdateGiftProduct extends ActionAsync {
            int status;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                fragment.showProgress();
            }

            @Override
            protected JSONObject doInBackground(Object... objects) {
                JSONObject jsonObject = null;
                try {
                    int productID = (int) objects[0];
                    status = (int) objects[1];
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                    params.add(new AbstractMap.SimpleEntry("status", status + ""));
                    String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/update_gift_product", params);
                    jsonObject = new JSONObject(response);
                } catch (Exception e) {

                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        fragment.products.get(getAdapterPosition()).setIs_gift(status);
                        notifyDataSetChanged();
                        new ActionGetCartCost().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } catch (Exception e) {
                } finally {
                    fragment.hideProgress();
                }
            }
        }
        //endregion
    }
}