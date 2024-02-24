package vn.nip.around.Fragment.Common;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import vn.nip.around.Adapter.MenuAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanMenu;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Bean.BeanShipper;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Custom.CircleTransform;
import vn.nip.around.Fragment.Common.FullOrder.FullOrderFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowJourneyFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    public int numberChat;
    //region Private variables
    public GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    public BeanShipper shipper;
    public List<BeanPoint> points;
    public TextView orderCode;
    public BottomSheetBehavior mBottomSheetBehavior;
    //endregion

    //region Contructors
    public FollowJourneyFragment() {
        // Required empty public constructor
    }
    //endregion

    public static FollowJourneyFragment newInstance(int orderID, boolean isBack) {
        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        args.putBoolean("is_back", isBack);
        FollowJourneyFragment fragment = new FollowJourneyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static FollowJourneyFragment isExist() {
        FollowJourneyFragment fragment = (FollowJourneyFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), FollowJourneyFragment.class.getName());
        if (fragment != null) {
            return fragment;
        }
        return null;
    }

    //region Create view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_follow_journey, container, false);
            MapHelper.resetDestination();
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageButton menu = (ImageButton)view.findViewById(R.id.menu);
        if (!getArguments().getBoolean("is_back")) {

            menu.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu));
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CmmFunc.hideKeyboard(getActivity());
                    DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                    if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                        mDrawerLayout.closeDrawers();
                    } else {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            });
        } else {
            menu.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentHelper.pop(getActivity());
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {

                @Override
                public void run() {
                    final View bottomSheet = view.findViewById(R.id.bottom_sheet1);
                    orderCode = (TextView) view.findViewById(R.id.order_code);
                    view.findViewById(R.id.new_order).setOnClickListener(FollowJourneyFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(FollowJourneyFragment.this);
                            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            mBottomSheetBehavior.setPeekHeight(CmmFunc.convertDpToPx(getActivity(), 44));
                        }
                    });

                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
            if (!(fragment instanceof FollowJourneyFragment)) {
                return;
            }
            getOrderStatus();
            if (map != null) {
                if (!SmartFoxHelper.getInstance().isConnected()) {
                    SmartFoxHelper.initSmartFox();
                } else {
                    SmartFoxHelper.getFollowJourney(getArguments().getInt("order_id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        //DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    //endregion

    private void init() {
        CmmFunc.setDelay(400, new ICallback() {
            @Override
            public void excute() {
                try {
                    FrameLayout newOrder = (FrameLayout) view.findViewById(R.id.new_order);
                    newOrder.setOnClickListener(FollowJourneyFragment.this);
                    shipper = (BeanShipper) CmmFunc.tryParseJson(getArguments().getString("shipper"), BeanShipper.class);
                    View bottomSheet = view.findViewById(R.id.bottom_sheet1);
                    mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    mBottomSheetBehavior.setPeekHeight(CmmFunc.convertDpToPx(getActivity(), 44));


                    TextView name = (TextView) view.findViewById(R.id.name);
                    name.setText(shipper.getShipper_fullname());

                    ImageButton myLocation = (ImageButton) view.findViewById(R.id.my_location);
                    myLocation.setOnClickListener(FollowJourneyFragment.this);
                    view.findViewById(R.id.shipper_location).setOnClickListener(FollowJourneyFragment.this);


                    ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
                    Picasso.with(getActivity()).load(shipper.getShipper_avatar()).transform(new CircleTransform()).into(avatar);
                    View fullOder = view.findViewById(R.id.full_order);
                    fullOder.setOnClickListener(FollowJourneyFragment.this);
                    View call = view.findViewById(R.id.call);
                    call.setOnClickListener(FollowJourneyFragment.this);
                    View chat = view.findViewById(R.id.chat);
                    chat.setOnClickListener(FollowJourneyFragment.this);
                    final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(FollowJourneyFragment.this);

                    isLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //region Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        buildGoogleApiClient();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                map.setMyLocationEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!SmartFoxHelper.getInstance().isConnected()) {
            SmartFoxHelper.initSmartFox();
        } else {
            SmartFoxHelper.getFollowJourney(getArguments().getInt("order_id"));
        }

        view.findViewById(R.id.button_container).setVisibility(View.VISIBLE);
//        try {
//            drawToMap();
//            MapHelper.addMarkerShipper(map, shipper);
//        } catch (Exception e) {
//
//        }
    }

    public void bindBottomData() {
        if (shipper != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final TextView name = (TextView) view.findViewById(R.id.name);
                    final ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
                    view.findViewById(R.id.my_location).setOnClickListener(FollowJourneyFragment.this);
                    view.findViewById(R.id.shipper_location).setOnClickListener(FollowJourneyFragment.this);
                    view.findViewById(R.id.full_order).setOnClickListener(FollowJourneyFragment.this);
                    view.findViewById(R.id.call).setOnClickListener(FollowJourneyFragment.this);
                    view.findViewById(R.id.chat).setOnClickListener(FollowJourneyFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            name.setText(shipper.getShipper_fullname());
                            Picasso.with(getActivity()).load(shipper.getShipper_avatar()).transform(new CircleTransform()).into(avatar);
                            MapHelper.addMarkerShipper(map, shipper);
                        }
                    });
                }
            }).start();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(FollowJourneyFragment.this)
                        .addApi(LocationServices.API)
                        .build();
                mGoogleApiClient.connect();
            }
        }).start();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //endregion

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], final int[] grantResults) {
        switch (requestCode) {
            case MapHelper.MY_PERMISSIONS_REQUEST_LOCATION: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                            // permission was granted. Do the
                            // contacts-related task you need to do.
                            if (ContextCompat.checkSelfPermission(getActivity(),
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                                if (mGoogleApiClient == null) {
                                    buildGoogleApiClient();
                                }
                                MapHelper.map.setMyLocationEnabled(true);
                            }

                        } else {

                            // Permission denied, Disable the functionality that depends on getActivity() permission.
                            Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
                // If request is cancelled, the result arrays are empty.

                return;
            }
        }
    }

    //region Draw to map

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            //Nếu là marker shipper
            if (marker.getId().equals(MapHelper.markerShipper.getId())) {
                String a = marker.getTitle();
                return false;
            } else {


            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "onMarkerClick", e.getMessage());
        }
        return false;
    }
    //endregion

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.call:
                try {
                    Tracking.excute("C8.4Y");
                    String phone = shipper.getShipper_phone() + "";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "onClick", e.getMessage());
                }
                break;
            case R.id.my_location:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (BeanPoint bean : points) {
                                if (bean.getLatLng() != null) {
                                    builder.include(bean.getLatLng());
                                }
                            }
                            // builder.include(MapHelper.getCurrentPosition(getActivity()).getLatLng());
                            LatLngBounds bounds = builder.build();
                            int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 120);
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            map.animateCamera(cameraUpdate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
            case R.id.shipper_location:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MapHelper.animateCenter(map, new LatLng(shipper.getShipper_latitude(), shipper.getShipper_longitude()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
                break;
            case R.id.chat:
                try {
                    Fragment fragment = new ChatFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("shipper", getArguments().getString("shipper"));
                    bundle.putInt("order_id", getArguments().getInt("order_id"));
                    fragment.setArguments(bundle);
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "onClick", e.getMessage());
                }
                break;
            case R.id.new_order:
                Tracking.excute("C8.1Y");
                AppActivity.popRoot();
                break;
            case R.id.full_order:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Tracking.excute("C8.3Y");
                            FullOrderFragment fragment = FullOrderFragment.newInstance(getArguments().getInt("order_id"));
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                        } catch (Exception e) {

                        }
                    }
                }).start();
                break;
        }
    }

    public void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < points.size() - 1; i++) {
                        try {
                            BeanPoint origin = points.get(i);
                            BeanPoint dest = points.get(i + 1);
                            if (origin.getLatLng() != null && dest.getLatLng() != null) {
                                String url = MapHelper.getUrl(origin.getLatLng(), dest.getLatLng());
                                JSONObject jsonObject = new JSONObject(HttpHelper.get(url, null));
                                List<List<HashMap<String, String>>> routes = MapHelper.parse(jsonObject);
                                ArrayList<LatLng> latLngs;
                                PolylineOptions lineOptions = null;
                                // Traversing through all the routes
                                for (int j = 0; j < routes.size(); j++) {
                                    latLngs = new ArrayList<>();
                                    lineOptions = new PolylineOptions();

                                    // Fetching i-th route
                                    List<HashMap<String, String>> path = routes.get(j);

                                    // Fetching all the points in i-th route
                                    for (int t = 0; t < path.size(); t++) {
                                        HashMap<String, String> point = path.get(t);

                                        double lat = Double.parseDouble(point.get("lat"));
                                        double lng = Double.parseDouble(point.get("lng"));
                                        LatLng position = new LatLng(lat, lng);
                                        latLngs.add(position);
                                    }

                                    // Adding all the points in the route to LineOptions
                                    lineOptions.addAll(latLngs);
                                    lineOptions.width(5);
                                    lineOptions.color(getResources().getColor(R.color.main));

                                }

                                // Drawing polyline in the Google Map for the i-th route
                                if (lineOptions != null) {
                                    final PolylineOptions finalLineOptions = lineOptions;
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            map.addPolyline(finalLineOptions);
                                        }
                                    });

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    MapHelper.animateCenter(getActivity(), map, points, 120);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    //region Actions
    private void getListFollow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_number_new_info_menu", params);
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int numberOrder = data.getInt("number_order");
                        int numberNotice = data.getInt("number_notification");
                        int numberEvent = data.getInt("number_event");
                        int numberSumNotice = numberEvent + numberNotice;
                        RecyclerView menu = (RecyclerView) getActivity().findViewById(R.id.recycler_menu);
                        final MenuAdapter menuAdapter = (MenuAdapter) menu.getAdapter();
                        if (numberOrder > 0) {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.FOLLOW_JOURNEY, menuAdapter.list);
                            bean.setShowNumber(true);
                            final int n = numberOrder;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.FOLLOW_JOURNEY, menuAdapter.list);
                            bean.setShowNumber(false);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        if (numberSumNotice > 0) {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.NOTICE, menuAdapter.list);
                            bean.setShowNumber(true);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.NOTICE, menuAdapter.list);
                            bean.setShowNumber(false);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        final int sum = numberSumNotice + numberOrder;
                        if (sum > 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView numberFollow = (TextView) view.findViewById(R.id.number_follow);
                                    numberFollow.setVisibility(View.VISIBLE);
                                    numberFollow.setText(sum + "");
                                }
                            });
                        } else {
                            TextView numberFollow = (TextView) view.findViewById(R.id.number_follow);
                            numberFollow.setVisibility(View.GONE);
                        }
                    } else {
                        RecyclerView menu = (RecyclerView) getActivity().findViewById(R.id.recycler_menu);
                        final MenuAdapter menuAdapter = (MenuAdapter) menu.getAdapter();
                        BeanMenu bean = BeanMenu.getByID(BeanMenu.FOLLOW_JOURNEY, menuAdapter.list);
                        bean.setShowNumber(false);
                        BeanMenu beanNotice = BeanMenu.getByID(BeanMenu.NOTICE, menuAdapter.list);
                        beanNotice.setShowNumber(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    menuAdapter.notifyDataSetChanged();
                                    TextView numberFollow = (TextView) view.findViewById(R.id.number_follow);
                                    numberFollow.setVisibility(View.GONE);
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

    //region Methods
    private void getOrderStatus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String value = APIHelper.getOrderStatus(getArguments().getInt("order_id"));
                    JSONObject jsonObject = new JSONObject(value);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        final JSONObject data = jsonObject.getJSONObject("data");
                        int status = data.getInt("status");
                        switch (status) {
                            //Đang follow
                            case 0:
                                break;
                            //Hoàn thành
                            case 1:
                                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Fragment fragment = CmmFunc.getActiveFragment(getActivity());
                                            if (fragment instanceof MessageDialogFragment) {
                                                if (((MessageDialogFragment) fragment).getName().equals(MessageDialogFragment.FOLLOW_ORDER_STATUS)) {
                                                    return;
                                                }
                                            }

                                            MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                            messageDialogFragment.setName(MessageDialogFragment.FOLLOW_ORDER_STATUS);
                                            messageDialogFragment.setMessage(GlobalClass.getActivity().getResources().getString(R.string.shipper_finish, data.getString("order_code")));
                                            messageDialogFragment.setCallback(new ICallback() {
                                                @Override
                                                public void excute() {
                                                    FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, RateFragment.newInstance(getArguments().getInt("order_id")));
                                                }
                                            });
                                            messageDialogFragment.show(getFragmentManager(), messageDialogFragment.getClass().getName());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
                            //Hủy
                            case -1:

                                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Fragment fragment = CmmFunc.getActiveFragment(getActivity());
                                            if (fragment instanceof MessageDialogFragment) {
                                                if (((MessageDialogFragment) fragment).getName().equals(MessageDialogFragment.FOLLOW_ORDER_STATUS)) {
                                                    return;
                                                }
                                            }
                                            MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                            messageDialogFragment.setName(MessageDialogFragment.FOLLOW_ORDER_STATUS);
                                            messageDialogFragment.setMessage(GlobalClass.getActivity().getResources().getString(R.string.order_cancelled, data.getString("order_code")));
                                            messageDialogFragment.setCallback(new ICallback() {
                                                @Override
                                                public void excute() {
                                                    Tracking.excute("C9.1Y");
                                                    AppActivity.popRoot();
                                                }
                                            });
                                            messageDialogFragment.show(getFragmentManager(), messageDialogFragment.getClass().getName());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    //endregion
}