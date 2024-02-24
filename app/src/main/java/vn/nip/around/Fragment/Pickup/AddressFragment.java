package vn.nip.around.Fragment.Pickup;


import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import vn.nip.around.Adapter.AddressAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanAddress;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends BaseFragment implements View.OnClickListener {

    public static final String PICKUP = "PICKUP";
    public static final String COD = "COD";
    public static final String CART = "CART";
    public static final String TYPE = "TYPE";
    int position;
    View container, defaultContainer;
    EditText address;
    RecyclerView recycler, recyclerHistory;
    LinearLayout favorites;
    List<BeanAddress> items = new ArrayList<>();
    AddressAdapter adapter;
    Handler handler;
    View removeAddress;
    TextView titleFavorite,titleRecent;

    public AddressFragment() {
        // Required empty public constructor
    }

    public static AddressFragment newInstance(int position, String type) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("type", type);
        AddressFragment fragment = new AddressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_address, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.location));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    container = view.findViewById(R.id.container);
                    defaultContainer = view.findViewById(R.id.default_container);
                    recyclerHistory = (RecyclerView) view.findViewById(R.id.recycler_history);
                    favorites = (LinearLayout) view.findViewById(R.id.favorites);
                    address = (EditText) view.findViewById(R.id.address);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    removeAddress = view.findViewById(R.id.remove);
                    titleFavorite = (TextView) view.findViewById(R.id.title_favorite);
                    titleRecent = (TextView) view.findViewById(R.id.title_recent);
                    adapter = new AddressAdapter(AddressFragment.this, recycler, items);
                    view.findViewById(R.id.pin).setOnClickListener(AddressFragment.this);
                    removeAddress.setOnClickListener(AddressFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addFavorite(StorageHelper.HOME);
                            addFavorite(StorageHelper.WORK);
                            addFavorite(StorageHelper.OTHER);



                            address.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(final Editable s) {
                                    try {
                                        if (s.toString().equals(StringUtils.EMPTY)) {
                                            if (handler != null) {
                                                handler.removeCallbacksAndMessages(null);
                                            }
                                            items.clear();
                                            adapter.notifyDataSetChanged();
                                            defaultContainer.setVisibility(View.VISIBLE);
                                            recycler.setVisibility(View.GONE);
                                            if (removeAddress.getVisibility() == View.GONE) {
                                                return;
                                            }
                                            removeAddress.setVisibility(View.GONE);

                                        } else {
                                            if (handler != null) {
                                                handler.removeCallbacksAndMessages(null);
                                                // Now add a new one
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        getActivity().runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (recycler.getVisibility() == View.VISIBLE) {
                                                                    return;
                                                                }
                                                                defaultContainer.setVisibility(View.GONE);
                                                                recycler.setVisibility(View.VISIBLE);
                                                            }
                                                        });

                                                        items.clear();
                                                        threadAPI(s.toString());
                                                        handler.sendEmptyMessage(1);
                                                    }
                                                }, 500);
                                            }


                                            if (removeAddress.getVisibility() == View.VISIBLE) {
                                                return;
                                            }
                                            removeAddress.setVisibility(View.VISIBLE);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            switch (getArguments().getString("type")) {
                                case PICKUP:
                                    HomeBookFragment homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(getActivity(), HomeBookFragment.class.getName());
                                    if (homeBookFragment != null) {
                                        address.setText(homeBookFragment.points.get(position).getAddress() + "");
                                    }
                                    break;
                                case CART:
                                    CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName());
                                    if (cartFragment != null) {
                                        try {
                                            address.setText(cartFragment.locations.get(position).getString("address") + "");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    break;
                                case COD:
                                    MapCODFragment mapCODFragment = (MapCODFragment) CmmFunc.getFragmentByTag(getActivity(), MapCODFragment.class.getName());
                                    if (mapCODFragment != null) {
                                        address.setText(mapCODFragment.points.get(position).getAddress() + "");
                                    }
                                    break;
                            }


                            container.setTranslationY(AppActivity.WINDOW_HEIGHT / 2);
                            container.animate().translationY(0).setDuration(300).start();
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recycler.setAdapter(adapter);
                            HandlerThread handlerThread = new HandlerThread(AddressFragment.this.getClass().getName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
                            handlerThread.start();
                            handler = new Handler(handlerThread.getLooper()) {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //adapter.notifyDataSetChanged();
                                            }
                                        });

                                    }
                                }
                            };
                        }
                    });
                    getAddressHistory();
                }


            });
            threadInit.start();
            isLoaded = true;
        }
    }


    private void addFavorite(final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String value = StorageHelper.get(key);
                    if (value.equals(StringUtils.EMPTY)) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(value);
                    final String addr = jsonObject.getString("address");
                    final String placeID = jsonObject.getString("place_id");
                    final View row = getActivity().getLayoutInflater().inflate(R.layout.row_address, null);
                    final ImageView icon = (ImageView) row.findViewById(R.id.icon);
                    final TextView name = (TextView) row.findViewById(R.id.name);
                    final TextView address = (TextView) row.findViewById(R.id.address);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final String nameAddr;
                            switch (key) {
                                case StorageHelper.HOME:
                                    nameAddr = getString(R.string.home);
                                    name.setText(nameAddr);
                                    icon.setImageResource(R.drawable.ic_home_unselected);
                                    break;
                                case StorageHelper.WORK:
                                    nameAddr = getString(R.string.work);
                                    name.setText(nameAddr);
                                    icon.setImageResource(R.drawable.ic_work_unselected);
                                    break;
                                case StorageHelper.OTHER:
                                    nameAddr = getString(R.string.other);
                                    name.setText(nameAddr);
                                    icon.setImageResource(R.drawable.ic_place_unselected);
                                    break;
                            }

                            address.setText(addr);
                            row.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                showProgress();
                                                final JSONObject jsonObject = MapHelper.getLatLngFromPlaceID(placeID);
                                                final BeanAddress bean = new BeanAddress();
                                                bean.setLatitude(jsonObject.getDouble("lat"));
                                                bean.setLongitude(jsonObject.getDouble("lng"));
                                                bean.setAddress(addr);
                                                bean.setName(addr);
                                                bean.setPlaceID(placeID);

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        switch (getArguments().getString("type")) {
                                                            case PICKUP:
                                                                FragmentHelper.pop(getActivity());
                                                                HomeBookFragment homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(getActivity(), HomeBookFragment.class.getName());
                                                                int pos = getArguments().getInt("position");
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
                                                                InfoBookFragment.newInstance(obj.getPickup_type(), pos).show(getActivity().getSupportFragmentManager(), InfoBookFragment.class.getName());
                                                                break;
                                                            case CART:
                                                                try {

                                                                    JSONObject jObject = new JSONObject();
                                                                    jObject.put("placeid", placeID);
                                                                    jObject.put("address", addr);
                                                                    jObject.put("latitude", jsonObject.getDouble("lat"));
                                                                    jObject.put("longitude", jsonObject.getDouble("lng"));
                                                                    List<JSONObject> jsonArray = new ArrayList<>();
                                                                    jsonArray.add(jObject);
                                                                    new ActionUpdateDeliveryInfo().execute(jsonArray);
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                                break;
                                                            case COD:
                                                                FragmentHelper.pop(getActivity());
                                                                MapCODFragment mapCODFragment = (MapCODFragment) CmmFunc.getFragmentByTag(getActivity(), MapCODFragment.class.getName());
                                                                int position = getArguments().getInt("position");
                                                                BeanPoint beanPoint = mapCODFragment.points.get(position);
                                                                beanPoint.setLatitude(bean.getLatitude());
                                                                beanPoint.setLongitude(bean.getLongitude());
                                                                beanPoint.setPlaceid(bean.getPlaceID());
                                                                beanPoint.setAddress(bean.getAddress());
                                                                beanPoint.setTag(BeanPoint.DROP);
                                                                if (position > 0) {
                                                                    beanPoint.setTag(BeanPoint.COD);
                                                                }
                                                                mapCODFragment.adapter.notifyDataSetChanged();
                                                                mapCODFragment.reloadMap();
                                                                InfoCODFragment.newInstance(position).show(getActivity().getSupportFragmentManager(), InfoCODFragment.class.getName());
                                                                break;

                                                        }

                                                    }
                                                });

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                hideProgress();
                                            }
                                        }
                                    }).start();
                                }
                            });
                            favorites.addView(row);
                            if (titleFavorite.getVisibility() != View.VISIBLE) {
                                titleFavorite.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void threadAPI(final String input) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json");
                    sb.append("?key=" + CmmVariable.GOOGLE_KEY);
                    sb.append("&input=" + URLEncoder.encode(input, "utf8"));
                    sb.append("&components=");
                    sb.append("country:vn");
                    sb.append("&language=vi");
                    String response = HttpHelper.get(sb.toString(), null);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray predsJsonArray = jsonObject.getJSONArray("predictions");
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        BeanAddress bean = new BeanAddress();
                        bean.setAddress(predsJsonArray.getJSONObject(i).getString("description"));
                        bean.setPlaceID(predsJsonArray.getJSONObject(i).getString("place_id"));
                        JSONObject structured_formatting = new JSONObject(predsJsonArray.getJSONObject(i).getString("structured_formatting"));
                        bean.setName(structured_formatting.getString("main_text"));
                        items.add(bean);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pin:
                FragmentHelper.replaceFragment(getActivity(), R.id.home_content, PinFragment.newInstance(getArguments().getInt("position"), getArguments().getString("type")));
                break;
            case R.id.remove:
                address.setText("");
                address.requestFocus();
                CmmFunc.showKeyboard(getActivity());
                break;
        }
    }

    void getAddressHistory() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = null;
                    String response = APIHelper.getSuggestPickupLocation();
                    jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        final List<BeanAddress> items = new ArrayList<>();
                        JSONArray arr = data.getJSONArray("locations");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject item = arr.getJSONObject(i);
                            BeanAddress beanAddress = new BeanAddress();
                            beanAddress.setName(item.getString("street"));
                            beanAddress.setPlaceID(item.getString("placeid"));
                            beanAddress.setAddress(item.getString("street") + ", " + item.getString("address"));
                            beanAddress.setLatitude(item.getDouble("latitude"));
                            beanAddress.setLongitude(item.getDouble("longitude"));
                            items.add(beanAddress);
                        }

                        final AddressAdapter addressAdapter = new AddressAdapter(AddressFragment.this, recyclerHistory, items);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (items.size() > 0) {
                                        recyclerHistory.setItemViewCacheSize(100);
                                        titleRecent.setVisibility(View.VISIBLE);
                                        recyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        recyclerHistory.setAdapter(addressAdapter);
                                    } else {
                                        titleRecent.setVisibility(View.GONE);
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
        }).start();

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
                    CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName());
                    cartFragment.locations.clear();
                    cartFragment.locations = locations;
                    FragmentHelper.pop(getActivity());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
