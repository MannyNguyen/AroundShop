package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.SmartFoxHelper;

import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;
import vn.nip.around.Util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchFragment extends BaseFragment implements View.OnClickListener {

    //region Private variables
    public View matchContent;
    public String idRequest;
    public boolean isCheckIDRequest;
    //endregion

    public MatchFragment() {
        // Required empty public constructor
    }

    public static MatchFragment newInstance(int orderID, boolean isShow, String type, String locations, int year, int month, int day, int hour, int minute) {
        Bundle args = new Bundle();
        args.putInt("orderID", orderID);
        args.putString("locations", locations);
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        args.putString("type", type);
        args.putBoolean("is_show", isShow);
        MatchFragment fragment = new MatchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MatchFragment newInstance(int orderID, boolean isShow, String type, String locations, int year, int month, int day, int hour, int minute, boolean returnToPickup) {
        Bundle args = new Bundle();
        args.putInt("orderID", orderID);
        args.putString("locations", locations);
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putInt("hour", hour);
        args.putInt("minute", minute);
        args.putString("type", type);
        args.putBoolean("is_show", isShow);
        args.putBoolean("return_to_pickup", returnToPickup);
        MatchFragment fragment = new MatchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_match, container, false);
        }
        return view;
    }

    public static MatchFragment isExist() {
        MatchFragment fragment = (MatchFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MatchFragment.class.getName());
        if (fragment != null) {
            return fragment;
        }
        return null;
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppActivity.IS_RECONNECT_SMARTFOX = true;
        if (!isLoaded) {
            matchContent = view.findViewById(R.id.match_content);
            init();
        }
    }

    private void init() {
        final Bundle bundle = getArguments();
        try {
            if (bundle.getInt("year") == 0) {
                if (bundle.getInt("orderID") > 0) {

                    matchContent.setVisibility(View.VISIBLE);
                }
            }
            if (getArguments().getBoolean("is_show")) {
                View matchContent = getView().findViewById(R.id.match_content);
                matchContent.setVisibility(View.VISIBLE);
            }

            Animation connectingAnimation = AnimationUtils.loadAnimation(GlobalClass.getContext(), R.anim.anim_scale_alpha);
            view.findViewById(R.id.loading).startAnimation(connectingAnimation);
            Animation connectingAnimation1 = AnimationUtils.loadAnimation(GlobalClass.getContext(), R.anim.anim_scale_alpha);
            view.findViewById(R.id.loading1).startAnimation(connectingAnimation1);


            final View cancel = view.findViewById(R.id.cancel);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getArguments().getInt("orderID") == 0) {
                        try {
                            Thread.sleep(CmmVariable.showCancelRequest * 1000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cancel.setVisibility(View.VISIBLE);
                                    cancel.setOnClickListener(MatchFragment.this);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cancel.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            }).start();

            new ActionGetRequestID().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


            isLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                showProgress();
                SmartFoxHelper.cancelFindShipper();
                break;
        }
    }


    public class ActionGetRequestID extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                JSONObject jsonObject;
                jsonObject = new JSONObject(APIHelper.getRequestID());
                return jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject == null) {
                    try {
                        if (NetworkUtil.getConnectivityStatus(GlobalClass.getContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
                            View view = GlobalClass.getActivity().findViewById(R.id.notice);
                            if (view.getVisibility() == View.GONE) {
                                GlobalClass.getActivity().findViewById(R.id.notice).setVisibility(View.VISIBLE);
                            }

                        } else {
                            CustomDialog.showMessage(GlobalClass.getActivity(), "", GlobalClass.getActivity().getString(R.string.cant_get_data), new ICallback() {
                                @Override
                                public void excute() {
                                    FragmentHelper.pop(getActivity());
                                }
                            });
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    idRequest = data.getString("id_request");
                    if (!SmartFoxHelper.getInstance().isConnected()) {
                        SmartFoxHelper.initSmartFox();
                    } else if (SmartFoxHelper.getInstance().isConnecting()) {
                        SmartFoxHelper.getInstance().removeAllEventListeners();
                        SmartFoxHelper.getInstance().disconnect();
                        SmartFoxHelper.initSmartFox();
                    } else {
                        String locations = null;
                        final Bundle bundle = getArguments();
                        if (bundle.containsKey("locations")) {
                            locations = bundle.getString("locations");
                        }
                        int orderID = bundle.getInt("orderID");
                        int year = bundle.getInt("year");
                        int month = bundle.getInt("month");
                        int day = bundle.getInt("day");
                        int hour = bundle.getInt("hour");
                        int minute = bundle.getInt("minute");
                        String type = bundle.getString("type");
                        boolean isReturn = false;
                        if(getArguments().containsKey("return_to_pickup")){
                            isReturn = getArguments().getBoolean("return_to_pickup");
                        }
                        SmartFoxHelper.requestShipper(orderID, idRequest, type, locations, year, month, day, hour, minute, isReturn);
                        isCheckIDRequest = true;
                    }
                } else {
                    CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.error), ErrorHelper.getValueByKey(code), new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.pop(getActivity());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
}
