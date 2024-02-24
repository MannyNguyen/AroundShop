package vn.nip.around.Adapter;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import vn.nip.around.Bean.BeanAddress;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Fragment.Pickup.AddressFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.InfoBookFragment;
import vn.nip.around.Fragment.Pickup.InfoCODFragment;
import vn.nip.around.Fragment.Pickup.MapCODFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MenuViewHolder> {
    View itemView;
    AddressFragment fragment;
    RecyclerView recycler;
    public List<BeanAddress> list;

    public AddressAdapter(AddressFragment fragment, RecyclerView recycler, List<BeanAddress> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_address, parent, false);
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        try {
            final BeanAddress item = list.get(position);
            if (item != null) {
                holder.address.setText(item.getAddress());
                holder.name.setText(item.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    fragment.showProgress();
                                    final BeanAddress bean = list.get(position);
                                    if (item.getLatitude() == 0) {
                                        final JSONObject jsonObject = MapHelper.getLatLngFromPlaceID(item.getPlaceID());
                                        bean.setLatitude(jsonObject.getDouble("lat"));
                                        bean.setLongitude(jsonObject.getDouble("lng"));
                                    }
                                    fragment.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                switch (fragment.getArguments().getString("type")) {
                                                    case AddressFragment.PICKUP:
                                                        FragmentHelper.pop(fragment.getActivity());
                                                        HomeBookFragment homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(fragment.getActivity(), HomeBookFragment.class.getName());
                                                        int pos = fragment.getArguments().getInt("position");
                                                        BeanPoint obj = homeBookFragment.points.get(pos);
                                                        obj.setLatitude(bean.getLatitude());
                                                        obj.setLongitude(bean.getLongitude());
                                                        obj.setPlaceid(bean.getPlaceID());
                                                        obj.setAddress(bean.getAddress());
                                                        obj.setTag(pos);
                                                        if (pos == homeBookFragment.points.size() - 1) {
                                                            homeBookFragment.points.get(pos).setTag(3);
                                                        }
                                                        homeBookFragment.adapter.notifyDataSetChanged();
                                                        homeBookFragment.reloadMap();
                                                        InfoBookFragment.newInstance(obj.getPickup_type(), pos).show(fragment.getActivity().getSupportFragmentManager(), InfoBookFragment.class.getName());
                                                        break;
                                                    case AddressFragment.CART:
                                                        try {
                                                            JSONObject jObject = new JSONObject();
                                                            jObject.put("placeid", item.getPlaceID());
                                                            jObject.put("address", item.getAddress());
                                                            jObject.put("latitude", bean.getLatitude());
                                                            jObject.put("longitude", bean.getLongitude());
                                                            List<JSONObject> jsonArray = new ArrayList<>();
                                                            jsonArray.add(jObject);
                                                            new ActionUpdateDeliveryInfo().execute(jsonArray);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    case AddressFragment.COD:
                                                        FragmentHelper.pop(fragment.getActivity());
                                                        MapCODFragment mapCODFragment = (MapCODFragment) CmmFunc.getFragmentByTag(fragment.getActivity(), MapCODFragment.class.getName());
                                                        int position1 = fragment.getArguments().getInt("position");
                                                        BeanPoint beanPoint = mapCODFragment.points.get(position1);
                                                        beanPoint.setLatitude(bean.getLatitude());
                                                        beanPoint.setLongitude(bean.getLongitude());
                                                        beanPoint.setPlaceid(bean.getPlaceID());
                                                        beanPoint.setAddress(bean.getAddress());
                                                        beanPoint.setTag(BeanPoint.DROP);
                                                        if (position1 > 0) {
                                                            beanPoint.setTag(BeanPoint.COD);
                                                        }

                                                        mapCODFragment.adapter.notifyDataSetChanged();
                                                        mapCODFragment.reloadMap();
                                                        InfoCODFragment.newInstance(position1).show(fragment.getActivity().getSupportFragmentManager(), InfoCODFragment.class.getName());
                                                        break;
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    fragment.hideProgress();
                                }
                            }
                        }).start();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ActionUpdateDeliveryInfo extends ActionAsync {
        List<JSONObject> locations;
        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                locations = (List<JSONObject>) params[0];
                String response = APIHelper.updateDeliveryInfo(locations);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(fragment.getActivity(), CartFragment.class.getName());
                    cartFragment.locations.clear();
                    cartFragment.locations = locations;
                    FragmentHelper.pop(fragment.getActivity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        TextView name;

        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            address = (CustomTextView) view.findViewById(R.id.address);
        }
    }
}