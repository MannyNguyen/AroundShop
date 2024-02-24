package vn.nip.around.Adapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import vn.nip.around.Bean.BeanBank;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Payment.ListBankFragment;
import vn.nip.around.Fragment.Payment.PaymentWebViewFragment;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by viminh on 10/6/2016.
 */

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    int orderID;
    String paymentCode;
    private List<BeanBank> list;


    public BankAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanBank> list, int orderID, String paymentCode) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
        this.orderID = orderID;
        this.paymentCode = paymentCode;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bank, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanBank item = list.get(position);
        if (position == 0) {
            list.get(position).setColor(true);
        }
        if (position == 1) {
            list.get(position).setColor(false);
        }
        if (position > 1) {
            boolean beforeColor = list.get(position - 2).isColor();
            list.get(position).setColor(!beforeColor);
        }

        if (!item.isColor()) {
            holder.container.setCardBackgroundColor(activity.getResources().getColor(R.color.white));
        } else {
            holder.container.setCardBackgroundColor(activity.getResources().getColor(R.color.gray_300));
        }
        Picasso.with(activity).load(item.getBank_image()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(final View view) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment fragment = CmmFunc.getActiveFragment(activity);
                    if (fragment instanceof ListBankFragment) {
                        FrameLayout layoutProgress = (FrameLayout) fragment.getView().findViewById(R.id.layout_progress);
                        layoutProgress.setVisibility(View.VISIBLE);
                    }
                    int itemPosition = recycler.getChildLayoutPosition(view);
                    BeanBank item = list.get(itemPosition);
                    if (item != null) {
                        String bankCode = item.getBank_code();
                        new ActionGetNLURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, orderID, paymentCode, bankCode);
                    }
                } catch (Exception e) {
                    Log.e("CategoryAdapter", "ViMT - onClick: " + e.getMessage());
                }
            }
        });


    }

    class ActionGetNLURL extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            try {
                int orderID = (int) objects[0];
                String paymentCode = (String) objects[1];
                String bankCode = (String) objects[2];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("order_id", orderID + ""));
                params.add(new AbstractMap.SimpleEntry("payment_code", paymentCode + ""));
                params.add(new AbstractMap.SimpleEntry("bank_code", bankCode + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_nganluong_url", params);
                jsonObject = new JSONObject(response);
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetNLURL", e.getMessage());
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    String url = data.getString("url");
                    Bundle bundle = new Bundle();
                    bundle.putString("data", url);
                    Fragment fragment = new PaymentWebViewFragment();
                    fragment.setArguments(bundle);
                    FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, fragment);
                } else {
                    new ErrorHelper().excute(jsonObject);
                }
            } catch (Exception e) {

            }
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView container;

        public MenuViewHolder(View view) {
            super(view);
            container = (CardView) view.findViewById(R.id.container);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}