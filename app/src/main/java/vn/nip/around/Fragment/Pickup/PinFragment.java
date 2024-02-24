package vn.nip.around.Fragment.Pickup;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Bean.BeanAddress;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, GoogleMap.OnCameraMoveListener {
    final int PERMISTION_LOCATION = 1001;
    final int INTENT_LOCATION = 1002;
    boolean isOpenIntentSetting = false;
    GoogleMap map;
    GoogleApiClient mGoogleApiClient;
    TextView address;
    BeanAddress beanAddress;

    public PinFragment() {
        // Required empty public constructor
    }

    public static PinFragment newInstance(int position, String type) {

        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("type", type);
        PinFragment fragment = new PinFragment();
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
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.pin_location));
            address = (TextView) view.findViewById(R.id.pin_address);
            view.findViewById(R.id.confirm).setOnClickListener(PinFragment.this);
            initMap();
            isLoaded = true;
        }

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
                            mapFragment.getMapAsync(PinFragment.this);
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
        map.setOnCameraIdleListener(PinFragment.this);
        map.setOnCameraMoveListener(PinFragment.this);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(PinFragment.this)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    public void onCameraIdle() {
        final LatLng center = new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    beanAddress = MapHelper.getPointByLatLng(center);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (beanAddress == null || beanAddress.getAddress() == null) {
                                address.setText(getString(R.string.loading_location));
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
        Log.d("CENTER_Idle", center.latitude + "-" + center.longitude);
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GlobalClass.getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISTION_LOCATION);
            } else {
                if (isOpenIntentSetting == true) {
                    return;
                }
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                messageDialogFragment.setMessage("Please turn on permission Location!");
                messageDialogFragment.setCallback(new ICallback() {
                    @Override
                    public void excute() {
                        try {
                            //Open the specific App Info page:
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                            startActivityForResult(intent, INTENT_LOCATION);
                            isOpenIntentSetting = true;

                        } catch (Exception e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivityForResult(intent, INTENT_LOCATION);
                            isOpenIntentSetting = true;
                        }
                    }
                });
                messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
            }
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
                            if (beanAddress != null) {
                                return;
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        beanAddress = MapHelper.getPointByLatLng(center);
                                        if (beanAddress == null) {
                                            return;
                                        }
                                        if (beanAddress.getAddress() == null) {
                                            address.setText("");
                                            return;
                                        }
                                        address.setText(beanAddress.getAddress() + "");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                            getLastLocation();
                        }
                    }
                }).start();

                break;
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, final Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (INTENT_LOCATION):
                getLastLocation();
        }
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
                    if (beanAddress == null) {
                        return;
                    }

                    if (beanAddress.getAddress() == null || beanAddress.getAddress().equals(StringUtils.EMPTY)) {
                        return;
                    }
                    if (getArguments().getString("type").equals(AddressFragment.PICKUP)) {
                        HomeBookFragment homeBookFragment = (HomeBookFragment) CmmFunc.getFragmentByTag(getActivity(), HomeBookFragment.class.getName());
                        int pos = getArguments().getInt("position");
                        BeanPoint obj = homeBookFragment.points.get(pos);
                        obj.setLatitude(beanAddress.getLatitude());
                        obj.setLongitude(beanAddress.getLongitude());
                        obj.setPlaceid(beanAddress.getPlaceID());
                        obj.setAddress(beanAddress.getAddress());
                        obj.setTag(pos);
                        if (pos == homeBookFragment.points.size() - 1) {
                            obj.setTag(3);
                        }
                        homeBookFragment.adapter.notifyDataSetChanged();
                        homeBookFragment.reloadMap();

                        FragmentHelper.pop(getActivity(), HomeBookFragment.class.getName());
                        InfoBookFragment.newInstance(obj.getPickup_type(), pos).show(getActivity().getSupportFragmentManager(), InfoBookFragment.class.getName());
                    } else if (getArguments().getString("type").equals(AddressFragment.CART)) {
                        JSONObject jObject = new JSONObject();
                        jObject.put("placeid", beanAddress.getPlaceID());
                        jObject.put("address", beanAddress.getAddress());
                        jObject.put("latitude", beanAddress.getLatitude());
                        jObject.put("longitude", beanAddress.getLongitude());
                        List<JSONObject> jsonArray = new ArrayList<>();
                        jsonArray.add(jObject);
                        new ActionUpdateDeliveryInfo().execute(jsonArray);
                    } else if (getArguments().getString("type").equals(AddressFragment.COD)) {
                        MapCODFragment mapCODFragment = (MapCODFragment) CmmFunc.getFragmentByTag(getActivity(), MapCODFragment.class.getName());
                        int pos = getArguments().getInt("position");
                        BeanPoint obj = mapCODFragment.points.get(pos);
                        obj.setLatitude(beanAddress.getLatitude());
                        obj.setLongitude(beanAddress.getLongitude());
                        obj.setPlaceid(beanAddress.getPlaceID());
                        obj.setAddress(beanAddress.getAddress());
                        obj.setTag(BeanPoint.DROP);
                        if (pos > 0) {
                            obj.setTag(BeanPoint.COD);
                        }
                        mapCODFragment.adapter.notifyDataSetChanged();
                        mapCODFragment.reloadMap();

                        FragmentHelper.pop(getActivity(), MapCODFragment.class.getName());
                        InfoCODFragment.newInstance(pos).show(getActivity().getSupportFragmentManager(), InfoCODFragment.class.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
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
                    FragmentHelper.pop(getActivity(), cartFragment.getClass().getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
