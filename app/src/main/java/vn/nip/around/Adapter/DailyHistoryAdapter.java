package vn.nip.around.Adapter;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanDaily;
import vn.nip.around.Bean.BeanOrder;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.FullOrder.FullOrderFragment;
import vn.nip.around.Fragment.Common.FullOrderCommonFragment;
import vn.nip.around.Fragment.Common.HomeFragment;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Fragment.Giftme.DailyFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.MapCODFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class DailyHistoryAdapter extends RecyclerView.Adapter<DailyHistoryAdapter.MenuViewHolder> {

    public static final String PICKUP = "PICKUP";
    public static final String GIFTING = "GIFTING";
    View itemView;
    DailyFragment fragment;
    RecyclerView recycler;
    public List<BeanDaily> list;
    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public DailyHistoryAdapter(DailyFragment fragment, RecyclerView recycler, List<BeanDaily> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_daily_history, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, int position) {
        try {
            final BeanDaily item = list.get(position);
            if (item != null) {
                holder.serviceName.setText(item.getClass().getDeclaredField(fragment.getString(R.string.key_service_name)).get(item) + "");
                holder.name.setText(item.getItem_name() + "");
                holder.address.setText(item.getAddress() + "");
                DateTime dateTime = DateTime.parse(item.getTime(), df);
                String time = dateTime.toString("hh:mm a dd/MM/yyyy");
                holder.date.setText(time);

                holder.reOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
//                            if (item.getOrder_type().equals(PICKUP)) {
//                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(item.getLocations())));
//                            } else if (item.getOrder_type().equals(GIFTING)) {
//                                new ActionReorderGifting().execute(item.getId());
//                            }
                            //holder.reOrder.setClickable(false);
                            new CheckOrderPickupType().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, FullOrderFragment.newInstance(item.getId()));
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

    class ActionReorderGifting extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int orderID = (int) params[0];
                String value = APIHelper.reoderGifting(orderID);
                jsonObject = new JSONObject(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, CartFragment.newInstance());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                fragment.hideProgress();
            }
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView name;
        TextView address;
        TextView date;
        Button reOrder;

        public MenuViewHolder(View view) {
            super(view);
            serviceName = (TextView) view.findViewById(R.id.service_name);
            name = (TextView) view.findViewById(R.id.name);
            address = (TextView) view.findViewById(R.id.address);
            date = (TextView) view.findViewById(R.id.date);
            reOrder = (Button) view.findViewById(R.id.reorder);
        }
    }

    class CheckOrderPickupType extends ActionAsync {
        BeanDaily beanDaily;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            beanDaily = (BeanDaily) objects[0];
            try {
                String value = APIHelper.checkOrderPickupType(beanDaily.getId());
                jsonObject = new JSONObject(value);
            } catch (Exception e) {
                e.printStackTrace();
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
                    int isReOrder = data.getInt("is_reorder");
                    if (isReOrder == 1) {
                        if (beanDaily.getOrder_type().equals(PICKUP)) {
                            if (beanDaily.getLocations().get(1).getPickup_type() == BeanPoint.COD) {
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(beanDaily.getLocations())));
                            } else {
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(beanDaily.getLocations()),
                                        beanDaily.getService_name()));
                            }
                        } else if (beanDaily.getOrder_type().equals(GIFTING)) {
                            new ActionReorderGifting().execute(beanDaily.getId());
                        }
                    }
                    if (isReOrder == 0) {
                        CustomDialog.showMessage(GlobalClass.getActivity(), "", GlobalClass.getActivity().getString(R.string.cannot_reorder));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                fragment.hideProgress();
            }
        }
    }
}