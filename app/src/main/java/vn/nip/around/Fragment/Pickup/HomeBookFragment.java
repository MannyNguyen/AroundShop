package vn.nip.around.Fragment.Pickup;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vn.nip.around.Adapter.PointAdapter;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Pickup.Confirm.*;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.MainActivity;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeBookFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks {

    //region Variables
    final int PERMISTION_LOCATION = 1001;
    public SupportMapFragment mapFragment;
    public GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    RecyclerView recycler;
    public PointAdapter adapter;
    public List<BeanPoint> points = new ArrayList<>();

    ImageButton cart;
    TextView numberCart;
    public Button addPoint;
    //endregion

    //region Contructors
    public HomeBookFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static HomeBookFragment newInstance(String points, String title) {
        Bundle args = new Bundle();
        args.putString("points", points);
        args.putString("title", title);
        HomeBookFragment fragment = new HomeBookFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home_book, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cart = (ImageButton) view.findViewById(R.id.cart);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getArguments().getString("title"));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (getArguments().getString("points").equals(StringUtils.EMPTY)) {
                            points.add(new BeanPoint());
                            BeanPoint last = new BeanPoint();
                            last.setPickup_type(BeanPoint.DROP);
                            points.add(last);
                        } else {
                            points = (List<BeanPoint>) CmmFunc.tryParseList(getArguments().getString("points"), BeanPoint.class);
                        }

                        points.get(points.size() - 1).setPickup_type(BeanPoint.DROP);

                        recycler = (RecyclerView) view.findViewById(R.id.recycler);
                        addPoint = (Button) view.findViewById(R.id.add_point);
                        adapter = new PointAdapter(HomeBookFragment.this, recycler, points);
                        addPoint.setOnClickListener(HomeBookFragment.this);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (points.size() > 3) {
                                    addPoint.setVisibility(View.GONE);
                                }
                                recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recycler.setAdapter(adapter);
                                initMap();
                                view.findViewById(R.id.confirm).setOnClickListener(HomeBookFragment.this);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            threadInit.start();

            isLoaded = true;
        }
        //getNumberCart(cart, numberCart);

    }


    //endregion

    //region Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 200);
        int padding50 = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 50);
        map.setPadding(padding50, padding, padding50, padding50);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(HomeBookFragment.this)
                .build();
        mGoogleApiClient.connect();


    }
    //endregion

    //region Methods
    private void initMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(CmmVariable.SLEEP);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapFragment = SupportMapFragment.newInstance();
                            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                            fragmentTransaction
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .add(R.id.fragment_map_order, mapFragment, StringUtils.EMPTY)
                                    .commitAllowingStateLoss();
                            mapFragment.getMapAsync(HomeBookFragment.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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

    private void getLastLocation() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        final LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {

                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                if (points.get(points.size() - 1).getLatLng() == null) {
                                    BeanPoint bean = MapHelper.getPoint(latLng, points.get(points.size() - 1));
                                    bean.setTag(3);
                                    points.set(points.size() - 1, bean);
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

    public void reloadMap() {
        map.clear();
        MapHelper.animateCenter(map, points);
        draw();
    }

    public void draw() {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
    }

    private boolean check() {
        try {
            for (int i = 0; i < points.size(); i++) {
                BeanPoint bean = points.get(i);
                if (bean.getAddress().equals(StringUtils.EMPTY)) {
                    adapter.isError = true;
                    adapter.notifyItemChanged(i);
                    return false;
                }

                if (bean.getPickup_type() == BeanPoint.TRANSPORT) {
                    if (bean.getItem_name().equals(StringUtils.EMPTY)) {
                        adapter.isError = true;
                        adapter.notifyItemChanged(i);
                        return false;
                    }
                }

                if (bean.getPickup_type() == BeanPoint.PURCHASE) {
                    if (bean.getItem_name().equals(StringUtils.EMPTY) || bean.getItem_cost() == 0) {
                        adapter.isError = true;
                        adapter.notifyItemChanged(i);
                        return false;
                    }
                }

                if (bean.getPickup_type() == BeanPoint.COD) {
                    if (bean.getItem_name().equals(StringUtils.EMPTY) || bean.getItem_cost() == 0) {
                        adapter.isError = true;
                        adapter.notifyItemChanged(i);
                        return false;
                    }
                }

                if (bean.getPickup_type() == BeanPoint.DROP) {
                    if (bean.getRecipent_name().equals(StringUtils.EMPTY) || bean.getPhone().equals(StringUtils.EMPTY)) {
                        adapter.isError = true;
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
    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_point:
                BeanPoint first = points.get(0);
                if (first == null) {
                    return;
                }
                BeanPoint beanPoint = new BeanPoint();
                beanPoint.setPickup_type(first.getPickup_type());
                points.add(points.size() - 1, beanPoint);
                adapter.isError = false;
                adapter.notifyItemChanged(points.size() - 2);
                if (points.size() > 3) {
                    addPoint.setVisibility(View.GONE);
                }
                break;

            case R.id.confirm:
                if (check()) {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, ConfirmFragment.newInstance(0, CmmFunc.tryParseObject(points), StringUtils.EMPTY));
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (points.get(0).getAddress().equals(StringUtils.EMPTY)) {
            getLastLocation();
        } else {
            reloadMap();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "mGoogleApiClient onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }
    //endregion

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

}
