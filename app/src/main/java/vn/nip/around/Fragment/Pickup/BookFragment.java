package vn.nip.around.Fragment.Pickup;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import vn.nip.around.Adapter.MenuAdapter;
import vn.nip.around.Adapter.PlacesAutoCompleteAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanBanner;
import vn.nip.around.Bean.BeanMenu;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Banner.BannerHomeFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.StorageHelper;

import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener {


    //region Variables
    String TAG = "HomeActivity";
    GoogleMap map;
    SupportMapFragment mapFragment;
    public List<BeanPoint> points = new ArrayList<>();
    public LinearLayout barBottom;
    HandlerThread mHandlerThread;
    Handler mThreadHandler;
    public FrameLayout next;
    private PlacesAutoCompleteAdapter mAdapter;
    boolean oldKeyboardState = false;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    Thread initBanner;
    //endregion

    //region Contructors
    public BookFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_book, container, false);
        }
        return view;
    }

    public static BookFragment newInstance(boolean isReorder) {
        Bundle args = new Bundle();
        BookFragment fragment = new BookFragment();
        args.putBoolean("is_reorder", isReorder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AppActivity.FLOW = AppActivity.PICKUP;

            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, CmmFunc.convertDpToPx(getActivity(), 100), Gravity.LEFT | Gravity.BOTTOM);
//                        final ScrollView bottom = (ScrollView) getView().findViewById(R.id.bottom);
//                        bottom.setLayoutParams(params);
//                    }
//                });
//                KeyboardHelper.addKeyboardToggleListener(getActivity(), new KeyboardHelper.SoftKeyboardToggleListener() {
//                    @Override
//                    public void onToggleSoftKeyboard(boolean isVisible) {
//                        try {
//                            if (oldKeyboardState != isVisible) {
//                                oldKeyboardState = isVisible;
//                                if (isVisible) {
//                                    nextHide();
//                                    ScrollView bottom = (ScrollView) getView().findViewById(R.id.bottom);
//                                    ScrollView.LayoutParams params = (ScrollView.LayoutParams) bottom.getLayoutParams();
//                                    params.gravity = Gravity.TOP;
//                                    bottom.setLayoutParams(params);
//                                    next.setVisibility(View.GONE);
//                                    getView().findViewById(R.id.my_location).setVisibility(View.GONE);
//                                } else {
//                                    //ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, CmmFunc.convertDpToPx(getActivity(), 100), Gravity.LEFT | Gravity.BOTTOM);
//                                    ScrollView bottom = (ScrollView) getView().findViewById(R.id.bottom);
//                                    ScrollView.LayoutParams params = (ScrollView.LayoutParams) bottom.getLayoutParams();
//                                    params.gravity = Gravity.BOTTOM;
//                                    bottom.setLayoutParams(params);
//
//                                    //bottom.animate().y(getView().getHeight() - CmmFunc.convertDpToPx(getActivity(), 100)).setDuration(400);
//                                    setAddress();
//                                    if (checkConditionNext()) {
//                                        nextShow();
//                                    }
//                                    next.setVisibility(View.VISIBLE);
//                                    getView().findViewById(R.id.my_location).setVisibility(View.VISIBLE);
//                                }
//                            }
//
//                        } catch (Exception e) {
//
//                        }
//
//                        Log.d("keyboard", "keyboard visible: " + isVisible);
//                    }
//                });
//            }
//        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mThreadHandler == null) {
                    mHandlerThread = new HandlerThread(TAG, android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    mHandlerThread.start();
                    mAdapter = new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item);
                    // Initialize the Handler
                    mThreadHandler = new Handler(mHandlerThread.getLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<JSONObject> results = mAdapter.resultList;
                                        if (results != null && results.size() > 0) {
                                            mAdapter.notifyDataSetChanged();
                                        } else {
                                            mAdapter.notifyDataSetInvalidated();
                                        }
                                    }
                                });

                            }
                        }
                    };
                }
            }
        }).start();
        //Có xét lại trong Profile Fragment
        if (!isLoaded) {
            tut();
            init();
            initMap();
            isLoaded = true;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        // Get rid of our Place API Handlers
        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacksAndMessages(null);
            mHandlerThread.quit();
        }
    }


    private void setToolbar() {
        getListFollow();
        final DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        getView().findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
                Tracking.excute("C12.7Y");

            }
        });
    }

    private void tut() {
        if (!StorageHelper.getTUT(StorageHelper.TUT_BOOK)) {
            final ImageView tut = (ImageView) view.findViewById(R.id.tut);
            tut.setVisibility(View.VISIBLE);
            tut.setAlpha(0.0f);
            tut.setImageResource(getResources().getIdentifier(getString(R.string.tut_book), "drawable", getActivity().getPackageName()));
            tut.animate().alpha(1).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    tut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tut.setVisibility(View.GONE);
                            StorageHelper.setTut(StorageHelper.TUT_BOOK, true);
                        }
                    });
                }
            }).start();

        }
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    next = (FrameLayout) view.findViewById(R.id.next);
                    next.setTag(false);
                    next.setOnClickListener(BookFragment.this);
                    setToolbar();
                    ImageButton myLocation = (ImageButton) getView().findViewById(R.id.my_location);
                    myLocation.setOnClickListener(BookFragment.this);
                    view.findViewById(R.id.fee_shipping).setOnClickListener(BookFragment.this);
                    barBottom = (LinearLayout) view.findViewById(R.id.bar_bottom);
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            try {
                                LatLng latLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                                MapHelper.animateCenter(map, latLng);
                                if (points.get(0).getAddress().equals("")) {
                                    barBottom = (LinearLayout) view.findViewById(R.id.bar_bottom);
                                    barBottom.removeAllViews();
                                    points = new ArrayList<>();
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    addStartPoint(inflater, new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()), false);
                                    addLastPoint(inflater, false);
                                } else {
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    for (BeanPoint bean : points) {
                                        builder.include(bean.getLatLng());
                                    }
                                    LatLngBounds bounds = builder.build();
                                    int padding = CmmFunc.convertDpToPx(GlobalClass.getActivity(), 100);
                                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                    map.animateCamera(cameraUpdate);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                } catch (Exception e) {

                }
            }
        }).start();
    }

    private void initMap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mapFragment = SupportMapFragment.newInstance();
                            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                            ft.add(R.id.container_map, mapFragment)
                                    .commit();
                            //ft.hide(mapFragment);
                            //SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                            mapFragment.getMapAsync(BookFragment.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (initBanner != null) {
                initBanner.interrupt();
            }
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
            if (!(fragment instanceof BookFragment)) {
                return;
            }
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            TextView nextText = (TextView) getView().findViewById(R.id.next_text);
            nextText.setText(getString(R.string.next));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        setAddress();
                        if (checkConditionNext()) {
                            nextShow();
                        } else {
                            nextHide();
                        }
                        next.setVisibility(View.VISIBLE);
                        ScrollView.LayoutParams params = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, CmmFunc.convertDpToPx(getActivity(), 100), Gravity.LEFT | Gravity.BOTTOM);
                        ScrollView bottom = (ScrollView) getView().findViewById(R.id.bottom);
                        bottom.setLayoutParams(params);
                    } catch (Exception e) {

                    }
                }
            });
//            if (!getArguments().getBoolean("is_reorder")) {
//                startLocationUpdates();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CmmVariable.MY_PERMISSION_ACCESS_COURSE_LOCATION:
                //mapReady();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CmmVariable.MY_PERMISSION_ACCESS_LOCATION:
                mapReady();
                break;

        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        try {
            //requestUpdateLocation();
            map = googleMap;
            view.findViewById(R.id.app_bar_layout).setVisibility(View.VISIBLE);
            view.findViewById(R.id.my_location).setVisibility(View.VISIBLE);
            view.findViewById(R.id.next).setVisibility(View.VISIBLE);
            view.findViewById(R.id.fee_shipping).setVisibility(View.VISIBLE);
            view.findViewById(R.id.bottom).setVisibility(View.VISIBLE);

            CmmFunc.setDelay(1000, new ICallback() {
                @Override
                public void excute() {
                    mapReady();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next:
                try {
                    next.setOnClickListener(null);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                next.setOnClickListener(BookFragment.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    if ((boolean) next.getTag()) {
                        Tracking.excute("C5.5Y");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                FragmentHelper.addFragment(getActivity(), R.id.home_content, ConfirmFragment.newInstance(0, CmmFunc.tryParseObject(points), ""));
                            }
                        }).start();
                    } else {
                        for (int i = 0; i < barBottom.getChildCount(); i++) {
                            View child = barBottom.getChildAt(i);
                            View isFullInfo = child.findViewById(R.id.is_full_info);
                            if (isFullInfo != null && isFullInfo.getVisibility() == View.VISIBLE) {
                                final Animation animShake = AnimationUtils.loadAnimation(getContext(), R.anim.anim_shake);
                                isFullInfo.startAnimation(animShake);
                                ScrollView bottom = (ScrollView) getView().findViewById(R.id.bottom);
                                bottom.scrollTo(0, i * CmmFunc.convertDpToPx(getActivity(), 36));
                                return;
                            }
                            if (points.get(points.size() - 1).getAddress().equals("")) {
                                child = barBottom.getChildAt(barBottom.getChildCount() - 1);
                                AutoCompleteTextView address = (AutoCompleteTextView) child.findViewById(R.id.address);
                                if (address != null) {
                                    address.requestFocus();
                                    Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                                    address.setCompoundDrawables(null, null, icError, null);
                                }
                                return;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.my_location:
                startLocationUpdates();
                //MapHelper.animateCenter(map, MapHelper.getCurrentLocation(getActivity()));
                break;
            case R.id.fee_shipping:
                CustomDialog.popupShipFee(getActivity());
                break;
        }
    }


    //endregion

    //region Methods

    private void mapReady() {
        try {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            if (!getArguments().getBoolean("is_reorder")) {
                barBottom.removeAllViews();
                points = new ArrayList<>();
                addStartPoint(inflater, null, false);
                addLastPoint(inflater, false);
                startLocationUpdates();
            } else {
                addStartPoint(inflater, null, true);

                if (points.size() > 2) {
                    addCenterPoint(1, true);
                    if (points.size() > 3) {
                        addCenterPoint(2, true);
                    }
                }

                addLastPoint(inflater, true);
                updateDistance_Draw();

            }

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        CmmVariable.MY_PERMISSION_ACCESS_COURSE_LOCATION);

                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    CmmFunc.hideKeyboard(getActivity());
                }
            });

            getBanners();
            //getActivity().getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startLocationUpdates() {
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Please check permission Location of App", Toast.LENGTH_SHORT).show();
                return;
            }

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                CustomDialog.Dialog2Button(getActivity(),
                        getString(R.string.access_to_location), getString(R.string.enable_location), getString(R.string.setting), getString(R.string.cancel),
                        new ICallback() {
                            @Override
                            public void excute() {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), CmmVariable.MY_PERMISSION_ACCESS_LOCATION);
                            }
                        }, new ICallback() {
                            @Override
                            public void excute() {
                                if (barBottom.getChildCount() > 0) {
                                    return;
                                }
                                //addStartPoint(getActivity().getLayoutInflater(), null, false);
                                //addLastPoint(getActivity().getLayoutInflater(), false);
                            }
                        }
                );
                return;
            }
            mFusedLocationClient.requestLocationUpdates(new LocationRequest(),
                    mLocationCallback,
                    null /* Looper */);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //thêm điểm đầu tiên
    private void addStartPoint(LayoutInflater inflater, LatLng latLng, boolean isReorder) {
        View firstPoint = inflater.inflate(R.layout.row_pickup_first, null);
        final ImageButton add = (ImageButton) firstPoint.findViewById(R.id.add);
        final ImageButton edit = (ImageButton) firstPoint.findViewById(R.id.edit);
        final AutoCompleteTextView address = (AutoCompleteTextView) firstPoint.findViewById(R.id.address);

        handleAutoComplete(address);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    address.setText("");
                }
            }
        });
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    address.clearFocus();
                    JSONObject jsonObject = (JSONObject) parent.getItemAtPosition(position);
                    String description = jsonObject.getString("description");
                    String placeID = jsonObject.getString("place_id");
                    JSONObject latlng = MapHelper.getLatLngFromPlaceID(placeID);
                    address.setText(description);

                    if (description == null) {
                        return;
                    }
                    BeanPoint old = points.get(0);
                    points.remove(0);
                    addPoint(0, description, new LatLng(latlng.getDouble("lat"), latlng.getDouble("lng")),
                            0.0, 0.0, 0, placeID, old.getItem_name(), old.getItem_cost(), old.getNote(), old.getPhone(), true, true, old.getRecipent_name());
                    FragmentHelper.addFragment(getActivity(), R.id.book_fragment, PickupFragment.newInstance(0, false));
                    if (checkConditionNext()) {
                        nextShow();
                    } else {
                        nextHide();
                    }

                    updateDistance_Draw();


                } catch (Exception e) {

                }
            }

        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                address.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        add.setTag("0");
        edit.setTag("0");
        address.setTag("0");
        if (isReorder) {
            address.setText(points.get(0).getAddress());
            firstPoint.findViewById(R.id.is_full_info).setVisibility(View.GONE);
        } else {
            BeanPoint beanPoint = MapHelper.getCurrentPosition(getActivity(), latLng);
            if (beanPoint != null) {
                addPoint(0, beanPoint.getAddress(), beanPoint.getLatLng(),
                        0.0, 0.0, 0, "", "", 0, "", "", true, true, "");

                address.setText(beanPoint.getAddress());
                updateDistance_Draw();
            } else {
                addPoint(0, "", new LatLng(0, 0),
                        0.0, 0.0, 0, "", "", 0, "", "", true, true, "");
            }

        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (points.get(0).getLatLng() == null) {
                    AutoCompleteTextView first = (AutoCompleteTextView) barBottom.getChildAt(0).findViewById(R.id.address);
                    Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                    first.setCompoundDrawables(null, null, icError, null);
                    return;
                }
                if (barBottom.getChildCount() > points.size()) {
                    if (barBottom.getChildCount() == 3) {
                        AutoCompleteTextView last = (AutoCompleteTextView) barBottom.getChildAt(1).findViewById(R.id.address);
                        Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                        icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                        last.setCompoundDrawables(null, null, icError, null);

                    }
                    if (barBottom.getChildCount() == 4) {
                        AutoCompleteTextView last = (AutoCompleteTextView) barBottom.getChildAt(2).findViewById(R.id.address);
                        Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                        icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                        last.setCompoundDrawables(null, null, icError, null);
                    }

                    return;
                }

                if (barBottom.getChildCount() > 3) {
                    Snackbar snackbar = Snackbar
                            .make(barBottom, "A maximum of 4 points", Snackbar.LENGTH_LONG);

                    snackbar.show();
                    return;

                }
                addCenterPoint(0, false);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!points.get(0).getAddress().equals("")) {

                        FragmentHelper.addFragment(getActivity(), R.id.book_fragment, PickupFragment.newInstance(0, false));
                    } else {
//                        AutoCompleteTextView first = (AutoCompleteTextView) barBottom.getChildAt(0).findViewById(R.id.address);
//                        Drawable icError = getResources().getDrawable(R.drawable.ic_error);
//                        icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
//                        first.setCompoundDrawables(null, null, icError, null);
                        address.requestFocus();
                        CmmFunc.showKeyboard(getActivity());
                        return;
                    }


                } catch (Exception e) {
                    String a = e.getMessage();
                }
            }
        });


        barBottom.addView(firstPoint);
    }

    //thêm điểm cuối cùng
    private void addLastPoint(LayoutInflater inflater, boolean isReorder) {
        View firstPoint = inflater.inflate(R.layout.row_drop, null);
        final AutoCompleteTextView address = (AutoCompleteTextView) firstPoint.findViewById(R.id.address);
        ImageButton edit = (ImageButton) firstPoint.findViewById(R.id.edit);
        handleAutoComplete(address);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    address.setText("");
                }
            }
        });
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    address.clearFocus();
                    JSONObject jsonObject = (JSONObject) parent.getItemAtPosition(position);
                    String description = jsonObject.getString("description");
                    String placeID = jsonObject.getString("place_id");
                    JSONObject latlng = MapHelper.getLatLngFromPlaceID(placeID);
                    address.setText(description);
                    BeanPoint old = points.get(points.size() - 1);
                    points.remove(points.size() - 1);
                    if (description == null) {
                        return;
                    }

                    addPoint(points.size(), description, new LatLng(latlng.getDouble("lat"), latlng.getDouble("lng")),
                            0.0, 0.0, 3, placeID, old.getItem_name(), old.getItem_cost(), old.getNote(), old.getPhone(), false, false, old.getRecipent_name());
                    points.get(0).setIsPay(true);
                    points.get(points.size() - 1).setIsPay(false);
                    CmmFunc.hideKeyboard(getActivity());

                    FragmentHelper.addFragment(getActivity(), R.id.book_fragment, PickupFragment.newInstance(points.size() - 1, true));
                    updateDistance_Draw();

                    if (checkConditionNext()) {
                        nextShow();
                    } else {
                        nextHide();
                    }
                } catch (Exception e) {

                }
            }

        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                address.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        address.setTag("3");
        if (isReorder) {
            View isFullInfo =  firstPoint.findViewById(R.id.is_full_info);
            BeanPoint last = points.get(points.size() - 1);
            String addressText = last.getAddress();
            if (last.getPhone().equals("")) {
                isFullInfo.setVisibility(View.VISIBLE);
            } else{
                isFullInfo.setVisibility(View.GONE);
            }
            address.setText(addressText);


        } else {
            BeanPoint bean = new BeanPoint();
            bean.setPickup_type(0);
            bean.setTag(3);
            points.add(points.size(), bean);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String response = APIHelper.getProfile();
                        JSONObject jsonObject = new JSONObject(response);
                        points.get(points.size() - 1).setPhone(jsonObject.getString("phone"));
                        points.get(points.size() - 1).setRecipent_name(jsonObject.getString("fullname"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (points.get(points.size() - 1).getAddress().equals("")) {
                    address.requestFocus();
                    CmmFunc.showKeyboard(getActivity());
//                    Drawable icError = getResources().getDrawable(R.drawable.ic_error);
//                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
//                    address.setCompoundDrawables(null, null, icError, null);
                    return;
                }
                FragmentHelper.addFragment(getActivity(), R.id.book_fragment, PickupFragment.newInstance(points.size() - 1, true));
            }
        });

        barBottom.addView(firstPoint);
    }

    //thêm điểm giữa
    private void addCenterPoint(int index, final boolean isReorder) {
        if (checkConditionNext()) {
            nextShow();
        } else {
            nextHide();
        }
        if (barBottom.getChildCount() > 3) {
            return;
        }

        if (index == 0) {
            index = points.size() - 1;
        }


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.row_pickup, null);
        final ImageButton remove = (ImageButton) view.findViewById(R.id.remove);
        final ImageButton edit = (ImageButton) view.findViewById(R.id.edit);
        final TextView number = (TextView) view.findViewById(R.id.number);
        final View fullInfo = view.findViewById(R.id.is_full_info);
        number.setText((index + 1) + "");
        final AutoCompleteTextView address = (AutoCompleteTextView) view.findViewById(R.id.address);
        handleAutoComplete(address);
        address.setTag(index);
        remove.setTag(index);
        edit.setTag(index);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    address.setText("");
                }
            }
        });
        final int finalIndex1 = index;
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    address.clearFocus();
                    BeanPoint old = new BeanPoint();
                    //Xóa điểm nếu đã tồn tại
                    if (barBottom.getChildCount() >= points.size()) {
                        BeanPoint bean = points.get(Integer.parseInt(address.getTag().toString()));
                        if (bean.getTag() != 3) {
                            old = points.get(Integer.parseInt(address.getTag() + ""));
                            points.remove(Integer.parseInt(address.getTag() + ""));
                        }

                    }
                    JSONObject jsonObject = (JSONObject) parent.getItemAtPosition(position);
                    String description = jsonObject.getString("description");
                    String placeID = jsonObject.getString("place_id");
                    JSONObject latlng = MapHelper.getLatLngFromPlaceID(placeID);
                    address.setText(description);

                    if (description == null) {
                        return;
                    }
                    if (old != null) {
                        String itemName = old.getItem_name();
                        int itemCost = old.getItem_cost();
                        String phone = old.getPhone();
                        String note = old.getNote();
                        String recipientName = old.getRecipent_name();
                        addPoint(Integer.parseInt(address.getTag() + ""), description, new LatLng(latlng.getDouble("lat"), latlng.getDouble("lng")),
                                0.0, 0.0, finalIndex1, placeID, itemName, itemCost, note, phone, false, true, recipientName);
                    } else {
                        addPoint(Integer.parseInt(address.getTag() + ""), description, new LatLng(latlng.getDouble("lat"), latlng.getDouble("lng")),
                                0.0, 0.0, finalIndex1, placeID, "", 0, "", "", false, true, "");
                    }
                    FragmentHelper.addFragment(getActivity(), R.id.book_fragment, PickupFragment.newInstance(finalIndex1, false));
                    if (checkConditionNext()) {
                        nextShow();
                    } else {
                        nextHide();
                    }
                    updateDistance_Draw();

                } catch (Exception e) {

                }
            }

        });

        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                address.setCompoundDrawables(null, null, null, null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tracking.excute("C5.4N");
                if (barBottom.getChildCount() == points.size()) {
                    points.remove(Integer.parseInt(address.getTag().toString()));

                }

                barBottom.removeViewAt(Integer.parseInt(address.getTag().toString()));


                //cập nhật lại điểm thứ 3 thành 2
                if (points.size() > 2) {
                    BeanPoint bean = points.get(1);
                    bean.setTag(1);
                    points.remove(1);
                    points.add(1, bean);
                    View v = barBottom.getChildAt(1);
                    TextView number = (TextView) v.findViewById(R.id.number);
                    ImageButton remove = (ImageButton) v.findViewById(R.id.remove);
                    ImageButton edit = (ImageButton) v.findViewById(R.id.edit);
                    final AutoCompleteTextView address = (AutoCompleteTextView) v.findViewById(R.id.address);
                    address.setTag("1");
                    remove.setTag("1");
                    edit.setTag("1");
                    number.setText("2");

                } else {
                    View child1 = barBottom.getChildAt(0);
                    FrameLayout lineBottom = (FrameLayout) child1.findViewById(R.id.line_bottom);
                    lineBottom.setBackgroundColor(getResources().getColor(R.color.gray_500));
                }
                if (checkConditionNext()) {
                    nextShow();
                } else {
                    nextHide();
                }
                updateDistance_Draw();
            }
        });
        final int finalIndex = index;
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (points.size() < barBottom.getChildCount()) {
//                        AutoCompleteTextView first = (AutoCompleteTextView) barBottom.getChildAt(finalIndex).findViewById(R.id.address);
//                        Drawable icError = getResources().getDrawable(R.drawable.ic_error);
//                        icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
//                        first.setCompoundDrawables(null, null, icError, null);
//                        first.requestFocus();
                        address.requestFocus();
                        CmmFunc.showKeyboard(getActivity());
                        return;
                    } else {
                        //showPickupDialog(index);
                        FragmentHelper.addFragment(getActivity(), R.id.book_fragment, PickupFragment.newInstance(finalIndex, false));
                    }

                } catch (Exception e) {

                }
            }
        });
        if (isReorder) {
            address.setText(points.get(index).getAddress());
            fullInfo.setVisibility(View.GONE);
            number.setText((index + 1) + "");
            View child = barBottom.getChildAt(index - 1);
            FrameLayout lineBottom1 = (FrameLayout) child.findViewById(R.id.line_bottom);
            lineBottom1.setBackgroundColor(getResources().getColor(R.color.main));
            barBottom.addView(view, index);
        } else {
            if (points.size() > 2) {
                View child = barBottom.getChildAt(1);
                FrameLayout lineBottom1 = (FrameLayout) child.findViewById(R.id.line_bottom);
                lineBottom1.setBackgroundColor(getResources().getColor(R.color.main));
            }

            barBottom.addView(view, index);
            ScrollView bottom = (ScrollView) getView().findViewById(R.id.bottom);
            address.setFocusable(true);
            address.requestFocus();
            bottom.smoothScrollTo(0, (barBottom.getChildAt(index + 1).getTop()));
            View child = barBottom.getChildAt(0);
            FrameLayout lineBottom1 = (FrameLayout) child.findViewById(R.id.line_bottom);
            lineBottom1.setBackgroundColor(getResources().getColor(R.color.main));
        }

    }

    //thêm điểm
    private void addPoint(int index, String address, LatLng latLng, Double distance, Double duration, int tag, String placeID, String itemName, int itemCost, String note, String phone, boolean isPay, boolean isPickkup, String recipientName) {
        BeanPoint point = new BeanPoint();
        point.setAddress(address);
        point.setLatLng(latLng);
        point.setDistance(distance);
        point.setTag(tag);
        point.setPlaceid(placeID);
        point.setItem_name(itemName);
        point.setItem_cost(itemCost);
        point.setNote(note);
        point.setPhone(phone);
        point.setIsPay(isPay);
        point.setDuration(duration);
        point.setRecipent_name(recipientName);

        points.add(index, point);

    }

    //vẽ lên map
    public void updateDistance_Draw() {
        map.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < points.size() - 1; i++) {
                        if (points.get(i).getLatLng() != null && points.get(i + 1).getLatLng() != null) {
                            JSONObject data = new JSONObject(new MapHelper.DowloadURL(points.get(i).getLatLng(), points.get(i + 1).getLatLng()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get());
                            Double distance = MapHelper.getValueDistance(data);
                            Double duration = MapHelper.getDuration(data);
                            points.get(i + 1).setDistance(distance);
                            points.get(i + 1).setDuration(duration);
                            //JSONObject routes = new JSONObject(new MapHelper.DowloadURL(points.get(i).getLatLng(), points.get(i + 1).getLatLng()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get());
                            new MapHelper.ActionDraw(map, data, points.get(i).getLatLng(), points.get(i + 1).getLatLng(), getResources().getColor(R.color.main)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get(CmmVariable.timeout, TimeUnit.SECONDS);
                        }

                    }
                } catch (Exception e) {

                } finally {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MapHelper.animateCenter(getActivity(), map, points, 100);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

    }

    //auto complete
    private void handleAutoComplete(AutoCompleteTextView address) {
        address.setThreshold(1);
        address.setAdapter(mAdapter);
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final String value = charSequence.toString();
                // Remove all callbacks and messages
                mThreadHandler.removeCallbacksAndMessages(null);
                // Now add a new one
                mThreadHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Background thread
                        mAdapter.resultList = mAdapter.mPlaceAPI.autocomplete(value);
                        // Post to Main Thread
                        mThreadHandler.sendEmptyMessage(1);
                    }
                }, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //kiểm tra hiển thị nút next
    public boolean checkConditionNext() {
        boolean retValue = false;
        if (!points.get(points.size() - 1).getAddress().equals("") && !points.get(points.size() - 1).getRecipent_name().equals("")) {
            for (int i = 0; i < points.size() - 1; i++) {
                BeanPoint item = points.get(i);
                if (!item.getItem_name().equals("")) {
                    retValue = true;
                } else {
                    return false;
                }
            }
        }


        return retValue;
    }

    //set lại địa chỉ cho các view // dùng trong onBackPress
    private void setAddress() {
        try {
            for (int i = 0; i < points.size(); i++) {
                if (barBottom.getChildCount() > points.size()) {
                    if (points.size() == 2) {
                        barBottom.removeViewAt(1);
                        View view = barBottom.getChildAt(i);
                        FrameLayout lineBottom1 = (FrameLayout) view.findViewById(R.id.line_bottom);
                        lineBottom1.setBackgroundColor(getResources().getColor(R.color.gray_500));

                    } else if (points.size() == 3) {
                        barBottom.removeViewAt(2);

                    }
                }
                View view = barBottom.getChildAt(i);
                AutoCompleteTextView address = (AutoCompleteTextView) view.findViewById(R.id.address);
                address.setText(points.get(i).getAddress());
                address.clearFocus();
            }
        } catch (Exception e) {

        }
    }

    public void nextHide() {
        FrameLayout next = (FrameLayout) getView().findViewById(R.id.next);
        next.setTag(false);
        next.setBackground(getResources().getDrawable(R.drawable.ic_next_off));
        next.setOnClickListener(BookFragment.this);
    }

    public void nextShow() {
        FrameLayout next = (FrameLayout) getView().findViewById(R.id.next);
        next.setTag(true);
        next.setBackground(getResources().getDrawable(R.drawable.ic_next_on));
        next.setOnClickListener(BookFragment.this);
    }

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

    private void getBanners() {
        initBanner = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String value = APIHelper.getBanners(1);
                    JSONObject jsonObject = new JSONObject(value);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        List<BeanBanner> banners = (List<BeanBanner>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanBanner.class);
                        for (BeanBanner beanBanner : banners) {
                            Fragment fragment = BannerHomeFragment.newInstance(CmmFunc.tryParseObject(beanBanner));
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        initBanner.start();
    }
    //endregion
}
