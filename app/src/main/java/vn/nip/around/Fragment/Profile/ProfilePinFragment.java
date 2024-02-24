package vn.nip.around.Fragment.Profile;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import vn.nip.around.Bean.BeanAddress;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfilePinFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, GoogleMap.OnCameraMoveListener {

    GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    TextView address;
    BeanAddress beanAddress;

    public ProfilePinFragment() {
        // Required empty public constructor
    }

    public static ProfilePinFragment newInstance(String tab) {

        Bundle args = new Bundle();
        args.putString("tab", tab);
        ProfilePinFragment fragment = new ProfilePinFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_pin, container, false);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        address = (TextView) view.findViewById(R.id.pin_address);
        view.findViewById(R.id.confirm).setOnClickListener(ProfilePinFragment.this);
        initMap();
    }

    private void initMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .add(R.id.fragment_pin, mapFragment, StringUtils.EMPTY)
                                    .commitAllowingStateLoss();
                            mapFragment.getMapAsync(ProfilePinFragment.this);
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
        map.setOnCameraIdleListener(ProfilePinFragment.this);
        map.setOnCameraMoveListener(ProfilePinFragment.this);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(ProfilePinFragment.this)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onCameraIdle() {
        final LatLng center = new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                beanAddress = MapHelper.getPointByLatLng(center);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (beanAddress.getAddress() == null) {
                            address.setText(getString(R.string.loading_location));
                            return;
                        }
                        address.setText(beanAddress.getAddress() + "");

                    }
                });
            }
        }).start();
        Log.d("CENTER_Idle", center.latitude + "-" + center.longitude);
    }

    @Override
    public void onCameraMove() {
        try {
            beanAddress = null;
            address.setText(getString(R.string.loading_location));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getLastLocation() {
        final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        final LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
//                if (locationResult != null) {
//                    if (center != null) {
//                        return;
//                    }
//                    center = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
//                    MapHelper.animateCenter(map, center);
//                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };
        if (ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationRequest locationRequest = LocationRequest.create()
                .setNumUpdates(1)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(0);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                final LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            beanAddress = MapHelper.getPointByLatLng(center);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (beanAddress.getAddress() == null) {
                                        address.setText("");
                                        return;
                                    }
                                    address.setText(beanAddress.getAddress() + "");
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                MapHelper.setCenter(map, center);
            }
        });
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                try {
                    if (beanAddress == null || beanAddress.getAddress() == null) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("address", beanAddress.getAddress());
                    jsonObject.put("place_id", beanAddress.getPlaceID());
                    StorageHelper.set(getArguments().getString("tab").toLowerCase(), jsonObject.toString());
                    FragmentHelper.pop(getActivity(), ProfileFragment.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
