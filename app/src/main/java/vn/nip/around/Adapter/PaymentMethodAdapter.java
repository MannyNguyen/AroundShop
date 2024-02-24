package vn.nip.around.Adapter;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanPaymentMethod;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.ListPaymentFragment;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    public List<BeanPaymentMethod> list;
    ListPaymentFragment fragment;

    public PaymentMethodAdapter(FragmentActivity activity, ListPaymentFragment fragment, RecyclerView recycler) {
        this.activity = activity;
        this.recycler = recycler;
        this.fragment = fragment;
        createData();
        new ActionGetPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new ActionGetAroundPay().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void createData() {
        list = new ArrayList<>();
        list.add(new BeanPaymentMethod(BeanPaymentMethod.ONLINE, R.drawable.ic_online_unselected, activity.getString(R.string.online_payment), false, "", false));
        list.add(new BeanPaymentMethod(BeanPaymentMethod.AROUND, R.drawable.ic_around_box_unselected, activity.getString(R.string.around_payment), true, "", false));
        list.add(new BeanPaymentMethod(BeanPaymentMethod.CASH, R.drawable.ic_cash_unselected, activity.getString(R.string.cast), false, "", false));
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_method, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanPaymentMethod item = list.get(position);
        holder.title.setText(item.getTitle());

        if (item.isSelected()) {
            holder.title.setTextColor(activity.getResources().getColor(R.color.main));
        } else {
            holder.title.setTextColor(activity.getResources().getColor(R.color.gray_400));
        }
        holder.right.setVisibility(View.GONE);
        switch (item.getType()) {
            case BeanPaymentMethod.ONLINE:
                if (item.isSelected()) {
                    item.setIcon(R.drawable.ic_online_selected);
                } else {
                    item.setIcon(R.drawable.ic_online_unselected);
                }
                holder.icon.setImageDrawable(activity.getResources().getDrawable(item.getIcon()));
                break;
            case BeanPaymentMethod.AROUND:
                holder.right.setVisibility(View.VISIBLE);
                holder.cashAround.setText(item.getCashAround());
                if (item.isSelected()) {
                    item.setIcon(R.drawable.ic_around_box_selected);
                    holder.iconCoinAround.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_coin_around_selected));
                    holder.cashAround.setTextColor(activity.getResources().getColor(R.color.main));
                } else {
                    item.setIcon(R.drawable.ic_around_box_unselected);
                    holder.iconCoinAround.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_coin_around_unselected));
                    holder.cashAround.setTextColor(activity.getResources().getColor(R.color.gray_400));
                }
                holder.icon.setImageDrawable(activity.getResources().getDrawable(item.getIcon()));
                break;
            case BeanPaymentMethod.CASH:
                if (item.isSelected()) {
                    item.setIcon(R.drawable.ic_cash_selected);
                } else {
                    item.setIcon(R.drawable.ic_cash_unselected);
                }
                holder.icon.setImageDrawable(activity.getResources().getDrawable(item.getIcon()));
                break;
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
            BeanPaymentMethod item = list.get(itemPosition);
            if (item != null) {
                String pt = "";
                switch (item.getType()) {
                    case BeanPaymentMethod.ONLINE:
                        pt = "ONLINE";
                        break;
                    case BeanPaymentMethod.AROUND:
                        pt = "AROUND_PAY";
                        break;
                    case BeanPaymentMethod.CASH:
                        pt = "CASH";
                        break;
                }
                if (pt.equals("")) {
                    return;
                }
                new ActionUpdatePayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, pt);
            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }


    }

    class ActionGetPayment extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_payment", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetPayment", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    BeanPaymentMethod.resetSelected(list);
                    fragment.paymentType = jsonObject.getString("payment_type");
                    BeanPaymentMethod bean;
                    switch (fragment.paymentType) {
                        case "ONLINE":
                            bean = BeanPaymentMethod.getByType(BeanPaymentMethod.ONLINE, list);
                            if (bean != null) {
                                bean.setSelected(true);
                            }
                            break;
                        case "AROUND_PAY":
                            bean = BeanPaymentMethod.getByType(BeanPaymentMethod.AROUND, list);
                            if (bean != null) {
                                bean.setSelected(true);
                            }
                            break;
                        case "CASH":
                            bean = BeanPaymentMethod.getByType(BeanPaymentMethod.CASH, list);
                            if (bean != null) {
                                bean.setSelected(true);
                            }
                            break;
                    }
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetPayment", e.getMessage());
            } finally {

            }
        }
    }

    public void updatePayment(final String pt){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    params.add(new AbstractMap.SimpleEntry("payment_type", pt));
                    String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_payment", params);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("code") == 1) {
                        fragment.paymentType = pt;
                        BeanPaymentMethod.resetSelected(list);
                        BeanPaymentMethod bean;
                        switch (pt) {
                            case "ONLINE":
                                bean = BeanPaymentMethod.getByType(BeanPaymentMethod.ONLINE, list);
                                if (bean != null) {
                                    bean.setSelected(true);
                                }
                                break;
                            case "AROUND_PAY":
                                bean = BeanPaymentMethod.getByType(BeanPaymentMethod.AROUND, list);
                                if (bean != null) {
                                    bean.setSelected(true);
                                }
                                break;
                            case "CASH":
                                bean = BeanPaymentMethod.getByType(BeanPaymentMethod.CASH, list);
                                if (bean != null) {
                                    bean.setSelected(true);
                                }
                                break;


                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class ActionUpdatePayment extends ActionAsync {
        String pt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                pt = (String) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("payment_type", pt));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_payment", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionUpdatePayment", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject.getInt("code") == 1) {
                    fragment.paymentType = pt;
                    BeanPaymentMethod.resetSelected(list);
                    BeanPaymentMethod bean;
                    switch (pt) {
                        case "ONLINE":
                            bean = BeanPaymentMethod.getByType(BeanPaymentMethod.ONLINE, list);
                            if (bean != null) {
                                bean.setSelected(true);
                            }
                            break;
                        case "AROUND_PAY":
                            bean = BeanPaymentMethod.getByType(BeanPaymentMethod.AROUND, list);
                            if (bean != null) {
                                bean.setSelected(true);
                            }
                            break;
                        case "CASH":
                            bean = BeanPaymentMethod.getByType(BeanPaymentMethod.CASH, list);
                            if (bean != null) {
                                bean.setSelected(true);
                            }
                            break;


                    }
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionUpdatePayment", e.getMessage());
            } finally {
            }
        }
    }

    class ActionGetAroundPay extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_around_pay", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetPayment", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    long aroundPay = data.getLong("around_pay");
                    if (list == null) {
                        return;
                    }
                    BeanPaymentMethod bean = BeanPaymentMethod.getByType(BeanPaymentMethod.AROUND, list);
                    bean.setCashAround(CmmFunc.formatMoney(aroundPay));
                    notifyDataSetChanged();
                }
            } catch (Exception e) {

            }
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ImageView iconCoinAround;
        TextView title;
        TextView cashAround;
        View right;


        public MenuViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.icon);
            iconCoinAround = (ImageView) view.findViewById(R.id.icon_coin_around);
            title = (TextView) view.findViewById(R.id.title);
            cashAround = (TextView) view.findViewById(R.id.cash_around);
            right = view.findViewById(R.id.right);
        }
    }
}