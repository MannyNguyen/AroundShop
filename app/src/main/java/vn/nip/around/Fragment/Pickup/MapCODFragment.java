package vn.nip.around.Fragment.Pickup;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.nip.around.Adapter.PointAdapter;
import vn.nip.around.Adapter.PointCODAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Custom.CustomRecycler;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Pickup.Confirm.*;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapCODFragment extends BaseFragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {
    final int PERMISTION_LOCATION = 1001;
    GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    public List<BeanPoint> points;
    ImageButton cart, myLocation;
    TextView numberCart;
    public View addPoint;

    CustomRecycler recycler;
    public PointCODAdapter adapter;

    public MapCODFragment() {
        // Required empty public constructor
    }

    public static MapCODFragment newInstance(String points) {
        Bundle args = new Bundle();
        if (points != null) {
            args.putString("locations", points);
        }
        MapCODFragment fragment = new MapCODFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_map_cod, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cart = (ImageButton) view.findViewById(R.id.cart);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        myLocation = (ImageButton) view.findViewById(R.id.my_location);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView title = (TextView) view.findViewById(R.id.title);
                points = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("locations"), BeanPoint.class);
                recycler = (CustomRecycler) view.findViewById(R.id.recycler);
                adapter = new PointCODAdapter(MapCODFragment.this, recycler, points);
                addPoint = view.findViewById(R.id.add_point);
                addPoint.setOnClickListener(MapCODFragment.this);
                myLocation.setOnClickListener(MapCODFragment.this);
                view.findViewById(R.id.confirm).setOnClickListener(MapCODFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(getString(R.string.collect));
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                        recycler.setItemViewCacheSize(10);
                        recycler.mMaxHeight = AppActivity.WINDOW_HEIGHT / 3;
                        layoutManager.setStackFromEnd(true);
                        initMap();
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, final String permissions[], final int[] grantResults) {
        switch (requestCode) {
            case PERMISTION_LOCATION:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isPerpermissionForAllGranted = true;
                        for (int i = 0; i < permissions.length; i++) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                isPerpermissionForAllGranted = false;
                            }
                        }
                        if (isPerpermissionForAllGranted) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getLastLocation();
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }

    //region maps
    private void initMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                            fragmentTransaction
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .add(R.id.fragment_map_order, mapFragment, StringUtils.EMPTY)
                                    .commitAllowingStateLoss();
                            mapFragment.getMapAsync(MapCODFragment.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 200);
        int padding50 = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 50);
        map.setPadding(padding50, padding, padding50, padding50);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(MapCODFragment.this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (points.get(0).getAddress().equals(StringUtils.EMPTY)) {
            getLastLocation();
            return;
        } else {
            reloadMap();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void getLastLocation() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        final LocationCallback locationCallback = new LocationCallback();
        if (ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISTION_LOCATION);
            return;
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                if (location != null) {
                    //MapHelper.setCenter(map, new LatLng(location.getLatitude(), location.getLongitude()));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                if (points.get(0).getLatLng() == null) {
                                    BeanPoint bean = MapHelper.getPoint(latLng, points.get(0));
                                    bean.setPickup_type(BeanPoint.DROP);
                                    bean.setTag(0);
                                    points.set(0, bean);
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        reloadMap();
                                        adapter.isError = false;
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        buildAlertMessageNoGps();
    }

    private void buildAlertMessageNoGps() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return;
        }
        CustomDialog.Dialog2Button(getActivity(),
                getString(vn.nip.around.R.string.access_to_location), getString(vn.nip.around.R.string.enable_location), getString(vn.nip.around.R.string.setting), getString(vn.nip.around.R.string.exit),
                new ICallback() {
                    @Override
                    public void excute() {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }, null
        );

    }

    public void reloadMap() {
        map.clear();
        MapHelper.animateCenter(map, points);
        draw();
    }

    private boolean check() {
        try {
            for (int i = 0; i < points.size(); i++) {
                BeanPoint bean = points.get(i);
                if (bean.getAddress().equals(StringUtils.EMPTY)) {
                    adapter.isError = true;
                    recycler.scrollToPosition(i);
                    adapter.notifyItemChanged(i);
                    return false;
                }
                if (bean.getPickup_type() == BeanPoint.DROP) {
                    if (bean.getRecipent_name().equals(StringUtils.EMPTY)) {
                        adapter.isError = true;
                        recycler.scrollToPosition(i);
                        adapter.notifyItemChanged(i);
                        return false;
                    }
                }

                if (bean.getPickup_type() == BeanPoint.COD) {
                    if (bean.getItem_name().equals(StringUtils.EMPTY) || bean.getItem_cost() == 0) {
                        adapter.isError = true;
                        recycler.scrollToPosition(i);
                        adapter.notifyItemChanged(i);
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_point:
                BeanPoint beanPoint = new BeanPoint();
                beanPoint.setPickup_type(BeanPoint.COD);
                points.add(points.size(), beanPoint);
                adapter.isError = false;
                //adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(points.size() - 1);
                recycler.scrollToPosition(points.size() - 1);
                if (points.size() == 3) {
                    //update lai nut remove
                    adapter.notifyItemChanged(1);
                }
                if (points.size() > 9) {
                    addPoint.setVisibility(View.GONE);
                }
                break;

            case R.id.confirm:
                if (check()) {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment.newInstance(0, CmmFunc.tryParseObject(points), StringUtils.EMPTY));
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
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    public void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (points.size() < 2) {
                        return;
                    }
                    List<LatLng> waypoints = new ArrayList<>();
                    for (int i = 0; i < points.size(); i++) {
                        LatLng waypoint = points.get(i).getLatLng();
                        if (waypoint == null) {
                            continue;
                        }
                        waypoints.add(points.get(i).getLatLng());
                    }
                    if (waypoints.size() < 2) {
                        return;
                    }
                    String url = null;
                    if (waypoints.size() == 2) {
                        url = MapHelper.getUrl(waypoints.get(0), waypoints.get(waypoints.size() - 1));
                    } else {
                        url = MapHelper.getUrl(waypoints.get(0), waypoints.get(waypoints.size() - 1), waypoints.subList(1, waypoints.size() - 1));
                    }

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //endregion
}
