package vn.nip.around.Adapter;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanOrder;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.FullOrder.FullOrderFragment;
import vn.nip.around.Fragment.Common.FullOrderCommonFragment;
import vn.nip.around.Fragment.Common.OrderHistoryFragment;
import vn.nip.around.Fragment.Pickup.BookFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.MapCODFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static vn.nip.around.Class.GlobalClass.getActivity;
import static vn.nip.around.Class.GlobalClass.getContext;
import static vn.nip.around.Helper.ErrorHelper.getValueByKey;

/**
 * Created by viminh on 10/6/2016.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    OrderHistoryFragment fragment;
    RecyclerView recycler;
    private List<BeanOrder> list;

    public OrderHistoryAdapter(OrderHistoryFragment fragment, RecyclerView recycler, List<BeanOrder> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_history, parent, false);
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        try {
            final BeanOrder item = list.get(position);
            if (item != null) {
                if (item.getStatus() == 1) {
                    holder.status.setTextColor(fragment.getActivity().getResources().getColor(R.color.green_500));
                    holder.status.setText(fragment.getActivity().getResources().getString(R.string.successfully));
                } else if (item.getStatus() == -1) {
                    holder.status.setTextColor(fragment.getActivity().getResources().getColor(R.color.red_500));
                    holder.status.setText(fragment.getActivity().getResources().getString(R.string.cancelled));
                }
                holder.address.setText(item.getAddress() + "");
                holder.recipientName.setText(item.getRecipent_name() + "");
                holder.total.setText(CmmFunc.formatMoney(item.getTotal(), true) + "");

                holder.type.setText(BeanOrder.getStringType(item));

                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getCreate_date(), df);
                holder.date.setText(DateTimeHelper.parseDate(dateTime));
                holder.time.setText(dateTime.toString("hh:mm"));
                holder.ampm.setText(dateTime.toString("a"));
                //holder.orderCode.setText(item.getOrder_code());

                if (item.getType().equals("GIFTING")) {
                    holder.containerReorder.setVisibility(View.VISIBLE);
                    holder.reOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        new ActionReorderGifting().execute(item.getId());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    });
                } else {
                    holder.containerReorder.setVisibility(View.VISIBLE);
                    holder.reOrder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.reOrder.setClickable(false);
                            new CheckOrderPickupType().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
                        }
                    });
                }

                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Tracking.excute("C19.1Y");
                            BeanOrder item = list.get(position);
                            if (item != null) {
                                //FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, FullOrderFragment.newInstance(item.getId()));
                                new CheckOrderPickupType2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Tracking.excute("C19.1Y");
                            final BeanOrder item = list.get(position);
                            if (item != null) {
                                new ActionDeleteOrder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ActionReorderGifting extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            } finally {
            }
        }
    }

//    class ActionReOrder extends ActionAsync {
//        BeanOrder item;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            fragment.showProgress();
//        }
//
//        @Override
//        protected JSONObject doInBackground(Object... objects) {
//            JSONObject jsonObject = null;
//            try {
//                item = (BeanOrder) objects[0];
//                String value = APIHelper.reOrder(item.getId());
//                jsonObject = new JSONObject(value);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return jsonObject;
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject jsonObject) {
//            super.onPostExecute(jsonObject);
//            try {
//                int code = jsonObject.getInt("code");
//                if (code == 1) {
//                    JSONObject data = jsonObject.getJSONObject("data");
//                    AppActivity.popRoot();
//                    if (item.getLocations().get(1).getPickup_type() == BeanPoint.COD) {
//                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(item.getLocations())));
//
//                    } else {
//                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(item.getLocations()),
//                                BeanOrder.getStringType(item,1)));
//                    }
////                    isReorder = data.getInt("is_reorder");
////                    if (isReorder == 1) {
////                        AppActivity.popRoot();
////                        if (item.getLocations().get(1).getPickup_type() == BeanPoint.COD) {
////                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(item.getLocations())));
////
////                        } else {
////                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(item.getLocations())));
////                        }
////                    }
////                    if (isReorder == 0) {
////                        CustomDialog.showMessage(GlobalClass.getActivity(), "", "message loi QC");
////                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                fragment.hideProgress();
//            }
//        }
//    }

    class ActionDeleteOrder extends ActionAsync {
        BeanOrder bean;

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            bean = (BeanOrder) params[0];
            try {
                String value = APIHelper.deleteHistoryOrder(bean.getId());
                jsonObject = new JSONObject(value);
            } catch (JSONException e) {
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
                    int position = list.indexOf(bean);
                    list.remove(position);
                    notifyItemRemoved(position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CheckOrderPickupType extends ActionAsync {
        BeanOrder beanOrder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            beanOrder = (BeanOrder) objects[0];
            try {
                String value = APIHelper.checkOrderPickupType(beanOrder.getId());
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
                        if (beanOrder.getLocations().get(1).getPickup_type() == BeanPoint.COD) {
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(beanOrder.getLocations())));
                        } else {
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(beanOrder.getLocations()),
                                    BeanOrder.getStringType(beanOrder)));
                        }
                    }
                    if (isReOrder == 0) {
                        CustomDialog.showMessage(GlobalClass.getActivity(), "", getActivity().getString(R.string.cannot_reorder));
                        fragment.hideProgress();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                fragment.hideProgress();
            }
        }
    }

    class CheckOrderPickupType2 extends ActionAsync {
        BeanOrder beanOrder;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            beanOrder = (BeanOrder) objects[0];
            try {
                String value = APIHelper.checkOrderPickupType(beanOrder.getId());
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
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, FullOrderFragment.newInstance(beanOrder.getId()));
                        fragment.hideProgress();
                    }
                    if (isReOrder == 0) {
                        CustomDialog.showMessage(GlobalClass.getActivity(), "", getActivity().getString(R.string.cannot_reorder));
                        fragment.hideProgress();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                fragment.hideProgress();
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
            fragment.showProgress();
            Tracking.excute("C19.1Y");
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanOrder item = list.get(itemPosition);
            if (item != null) {
                FullOrderFragment fullOrderCommonFragment = FullOrderFragment.newInstance(item.getId());
                FragmentHelper.addFragment(fullOrderCommonFragment.getActivity(), R.id.home_content, fullOrderCommonFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView type;
        private TextView total;
        private TextView status;
        private TextView date;
        private TextView time;
        private TextView ampm;
        private TextView reOrder;
        private TextView recipientName;
        private TextView address;
        private FrameLayout containerReorder;
        View container;
        View delete;

        public MenuViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.type);
            total = (TextView) view.findViewById(R.id.total);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            reOrder = (TextView) view.findViewById(R.id.reorder);
            containerReorder = (FrameLayout) view.findViewById(R.id.container_reorder);
            ampm = (TextView) view.findViewById(R.id.ampm);
            recipientName = (TextView) view.findViewById(R.id.recipent_name);
            address = (TextView) view.findViewById(R.id.address);
            status = (TextView) view.findViewById(R.id.status);
            container = view.findViewById(R.id.container);
            delete = view.findViewById(R.id.delete);
        }
    }
}