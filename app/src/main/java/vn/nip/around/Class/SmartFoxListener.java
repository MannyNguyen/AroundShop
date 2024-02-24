package vn.nip.around.Class;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sfs2x.client.core.BaseEvent;
import sfs2x.client.entities.User;
import sfs2x.client.requests.ExtensionRequest;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanCSChat;
import vn.nip.around.Bean.BeanChat;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Bean.BeanShipper;
import vn.nip.around.Bean.BeanUser;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.CallCenterFragment;
import vn.nip.around.Fragment.Common.ChatFragment;
import vn.nip.around.Fragment.Common.FollowJourneyFragment;
import vn.nip.around.Fragment.Common.MatchFragment;
import vn.nip.around.Fragment.Common.OrderFollowFragment;
import vn.nip.around.Fragment.Common.RateFragment;
import vn.nip.around.Fragment.Dialog.AddAroundPayDialogFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Dialog.OnePayDialogFragment;
import vn.nip.around.Fragment.Dialog.TryFoundShipperDialogFragment;
import vn.nip.around.Fragment.Payment.PaymentWebViewFragment;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.MapHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Helper.TimerHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by viminh on 10/20/2016.
 */

public abstract class SmartFoxListener {

    //public static boolean domainReconnect = false;

    public void connection(BaseEvent event) {
        Log.d("VIMT", "CONNECTION");
        if ((boolean) event.getArguments().get("success")) {
            //domainReconnect = false;
            try {
                BeanUser bean = new BeanUser();
                bean.setPhone(StorageHelper.getPhone());
                bean.setPassword("");
                SmartFoxHelper.login(bean);
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "", e.getMessage());
            }
        } else {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ConnectivityManager manager = (ConnectivityManager) GlobalClass.getActivity().getSystemService(CONNECTIVITY_SERVICE);
                    boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .isConnectedOrConnecting();
                    boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                            .isConnectedOrConnecting();
                    if (!isWifi && !is3g) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                messageDialogFragment.setMessage(GlobalClass.getActivity().getString(R.string.enable_network));
                                messageDialogFragment.setCallback(new ICallback() {
                                    @Override
                                    public void excute() {
                                        SmartFoxHelper.initSmartFox();
                                    }
                                });
                                messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());

                            }
                        });
                        return;
                    }

                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof MatchFragment) {
                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage(GlobalClass.getActivity().getString(R.string.disconnect_match));
                        messageDialogFragment.setCallback(new ICallback() {
                            @Override
                            public void excute() {
                                try {
                                    if (AppActivity.FLOW == AppActivity.PICKUP) {
                                        GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(HomeBookFragment.class.getName(), 0);
                                    } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                        GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    } else {

                    }
                }
            });

        }

    }

    public void connectionLost(BaseEvent event) {
        if (AppActivity.IS_RECONNECT_SMARTFOX == false) {
            return;
        }
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FollowJourneyFragment followJourneyFragment = FollowJourneyFragment.isExist();
                if (followJourneyFragment != null) {
                    SmartFoxHelper.initSmartFox();
                    return;
                }

                Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof MatchFragment) {
                    MatchFragment matchFragment = (MatchFragment) fragment;
                    if (matchFragment.idRequest != null) {
                        SmartFoxHelper.initSmartFox();
                        return;
                    }

                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage(GlobalClass.getActivity().getString(R.string.disconnect_match));
                    messageDialogFragment.setCallback(new ICallback() {
                        @Override
                        public void excute() {
                            try {
                                if (AppActivity.FLOW == AppActivity.PICKUP) {
                                    GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(HomeBookFragment.class.getName(), 0);
                                } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                    GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());

                } else {

                }
            }
        });


    }

    public void login(BaseEvent event) {
        try {
            SmartFoxHelper.ping(true);
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FollowJourneyFragment followJourneyFragment = FollowJourneyFragment.isExist();
                    if (followJourneyFragment != null) {
                        int orderID = followJourneyFragment.getArguments().getInt("order_id");
                        SFSObject sfsObject = new SFSObject();
                        sfsObject.putUtfString("command", "GET_FOLLOW_JOURNEY");
                        sfsObject.putInt("id_order", orderID);
                        SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                        return;
                    }

                    //Nếu ở màn hình MatchFragment
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof MatchFragment) {
                        if (fragment instanceof MatchFragment) {
                            final MatchFragment matchFragment = (MatchFragment) fragment;
                            if (matchFragment.idRequest != null && matchFragment.isCheckIDRequest) {
                                SmartFoxHelper.checkRequest(matchFragment.idRequest);
                            } else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObject;
                                            jsonObject = new JSONObject(APIHelper.getRequestID());
                                            int code = jsonObject.getInt("code");
                                            if (code == 1) {
                                                JSONObject data = jsonObject.getJSONObject("data");
                                                matchFragment.idRequest = data.getString("id_request");
                                                if (!SmartFoxHelper.getInstance().isConnected()) {
                                                    SmartFoxHelper.initSmartFox();
                                                } else if (SmartFoxHelper.getInstance().isConnecting()) {
                                                    SmartFoxHelper.getInstance().removeAllEventListeners();
                                                    SmartFoxHelper.getInstance().disconnect();
                                                    SmartFoxHelper.initSmartFox();
                                                } else {
                                                    String locations = null;
                                                    final Bundle bundle = matchFragment.getArguments();
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
                                                    boolean isReturnToPickup=false;
                                                    if (bundle.containsKey("return_to_pickup")){
                                                        isReturnToPickup=bundle.getBoolean("return_to_pickup");
                                                    }
                                                    SmartFoxHelper.requestShipper(orderID, matchFragment.idRequest, type, locations, year, month, day, hour, minute, isReturnToPickup);
                                                    matchFragment.isCheckIDRequest = true;
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }

                        }


                        return;
                    }

                    if (fragment instanceof CallCenterFragment) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    SFSObject sfsObject = new SFSObject();
                                    sfsObject.putUtfString("command", "REQUEST_CHAT_CS");
                                    SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                                } catch (Exception e) {
                                }

                            }
                        }).start();
                        return;

                    }


                }
            });
        } catch (Exception e) {

        }
    }

    public void loginError(final BaseEvent event) {
    }

    public void publishMessage(BaseEvent event) {
        PUBLIC_MESSAGE(event);
    }

    public void extensionResponse(BaseEvent event) {
        try {
            String cmd = (String) event.getArguments().get("cmd");
            if (cmd.equals("user")) {
                final SFSObject params = (SFSObject) event.getArguments().get("params");
                String command = params.getUtfString("command");
                Log.d("EXTENTIONS", command);
                switch (command) {
                    case "REQUEST_SHIPPER":
                        REQUEST_SHIPPER(params);
                        break;
                    case "RESPONSE_SHIPPER":
                        RESPONSE_SHIPPER(params);
                        break;
                    case "FINISH_DELIVERY":
                        FINISH_DELIVERY(params);
                        break;

                    case "UPDATE_SHIPPER_POSITION":
                        UPDATE_SHIPPER_POSITION(params);
                        break;
                    case "GET_CHAT_HISTORY":
                        GET_CHAT_HISTORY(params);
                        break;

                    case "REQUEST_NL_PAYMENT":
                        REQUEST_NL_PAYMENT(params);
                        break;
                    case "GET_FOLLOW_JOURNEY":
                        GET_FOLLOW_JOURNEY(params);
                        break;
                    case "CANCEL_ORDER":
                        CANCEL_ORDER(params);
                        break;

                    case "CANCEL_REQUEST_SHIPPER":
                        CANCEL_REQUEST_SHIPPER(params);
                        break;
                    case "CHANGE_REQUEST_SCREEN":
                        CHANGE_REQUEST_SCREEN();
                        break;

                    case "SEND_REQUEST_ID":
                        SEND_REQUEST_ID(params);
                        break;
                    case "CHECK_REQUEST":
                        CHECK_REQUEST(params);
                        break;

                    case "REQUEST_CHAT_CS":
                        REQUEST_CHAT_CS(params);
                        break;
                    case "CHAT_CS":
                        CHAT_CS(params);
                        break;

                    case "GET_CHAT_CS_HISTORY":
                        GET_CHAT_CS_HISTORY(params);
                        break;
                }
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "extensionResponse", e.getMessage());
        }
    }

    //region Extends listener

    private void REQUEST_SHIPPER(final SFSObject params) {
        try {
            if (AppActivity.FLOW == AppActivity.GIFTING) {
                CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                if (fragment != null) {
                    fragment.hideProgress();
                }
            } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                if (fragment != null) {
                    fragment.hideProgress();
                }
            }
            TimerHelper.cancelHandlerRequestShipper();
            final int code = params.getInt("code");
            if (code == 1) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //có vào follow hay không
                        boolean isFollowJourney = params.getBool("is_follow_journey");
                        if (isFollowJourney) {
                            return;
                        } else {
                            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                    messageDialogFragment.setMessage(GlobalClass.getActivity().getResources().getString(R.string.order_is_accepted));
                                    messageDialogFragment.setCallback(new ICallback() {
                                        @Override
                                        public void excute() {
                                            AppActivity.popRoot();
                                            FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, OrderFollowFragment.newInstance());
                                        }
                                    });
                                    messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                                }
                            });
                        }
                    }
                });
                //SUCCESS


            } else {
                if (!CmmFunc.isShowMessage()) {
                    //Thực hiện thanh toán
                    if (params.getBool("is_online_payment")) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (AppActivity.FLOW == AppActivity.GIFTING) {
                                    CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                    cartFragment.orderID = params.getInt("online_payment_order_id");
                                } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                                    ConfirmFragment confirmFragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                    confirmFragment.orderID = params.getInt("online_payment_order_id");
                                }
                            }
                        });
                        DialogFragment onepay = OnePayDialogFragment.newInstance(params.getInt("online_payment_order_id"));
                        onepay.show(GlobalClass.getActivity().getSupportFragmentManager(), onepay.getClass().getName());
                        return;
                    }

                    if (params.getBool("is_not_enough_aroundpay")) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AddAroundPayDialogFragment addAroundPayDialogFragment = new AddAroundPayDialogFragment();
                                addAroundPayDialogFragment.message = ErrorHelper.getValueByKey(code);
                                addAroundPayDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), addAroundPayDialogFragment.getClass().getName());
                            }
                        });
                        return;
                    }

                    if (params.getBool("is_set_schedule_time")) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                messageDialogFragment.setMessage(ErrorHelper.getValueByKey(code));
                                messageDialogFragment.setCallback(new ICallback() {
                                    @Override
                                    public void excute() {
                                        try {
                                            if (AppActivity.FLOW == AppActivity.PICKUP) {
                                                ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                                fragment.hideProgress();
                                                //fragment.getArguments().putBoolean("is_set_delivery_time", true);
                                                fragment.getArguments().putBoolean("cannot_find_shipper", false);
                                                fragment.getArguments().putString("delivery_time", params.getUtfString("set_schedule_time"));
                                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                                                fragment.onResume();
                                            } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                                CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                                fragment.hideProgress();
                                                fragment.getArguments().putBoolean("cannot_find_shipper", false);
                                                fragment.getArguments().putString("delivery_time", params.getUtfString("set_schedule_time"));
                                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                                                fragment.onResume();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                            }
                        });


                        return;
                    }

                    //Lỗi
                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                            if (fragment instanceof MatchFragment) {

                                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                messageDialogFragment.setMessage(ErrorHelper.getValueByKey(code));
                                messageDialogFragment.setCallback(new ICallback() {
                                    @Override
                                    public void excute() {
                                        try {
                                            if (AppActivity.FLOW == AppActivity.PICKUP) {
                                                ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                                if (fragment != null) {
                                                    fragment.hideProgress();
                                                }
                                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                                            } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                                CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                                if (fragment != null) {
                                                    fragment.hideProgress();
                                                }
                                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());

                            }
                        }
                    });

                }
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "REQUEST_SHIPPER", e.getMessage());
        }
    }

    private void RESPONSE_SHIPPER(final SFSObject params) {
        try {
            TimerHelper.cancelHandlerResponseShipper();
            if (AppActivity.FLOW == AppActivity.GIFTING) {
                CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                if (fragment != null) {
                    fragment.hideProgress();
                }
            } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                if (fragment != null) {
                    fragment.hideProgress();
                }
            }
            ;
            final int status = params.getInt("shipper_status");
            final int code = params.getInt("code");
            if (status == 1 && code == 1) {
                AppActivity.popRoot();
                FragmentHelper.replaceFragment(GlobalClass.getActivity(), R.id.home_content, FollowJourneyFragment.newInstance(params.getInt("order_id"), false));
            } else {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                        if (fragment != null) {
                            fragment.hideProgress();
                        }
                    }
                });
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (params.getBool("is_set_schedule_time")) {
                            if (AppActivity.FLOW == AppActivity.PICKUP) {
                                try {
                                    GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                                    final ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                    if (fragment != null) {
                                        fragment.hideProgress();
                                        fragment.getArguments().putBoolean("cannot_find_shipper", true);
                                        fragment.getArguments().putString("delivery_time", params.getUtfString("set_schedule_time"));
                                        TryFoundShipperDialogFragment tryFoundShipperDialogFragment = new TryFoundShipperDialogFragment();
                                        tryFoundShipperDialogFragment.message = ErrorHelper.getValueByKey(code);
                                        tryFoundShipperDialogFragment.addLaterCallback = new ICallback() {
                                            @Override
                                            public void excute() {
                                                try {
                                                    String time = fragment.getArguments().getString("delivery_time");
                                                    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                                    fragment.dateTime = DateTime.parse(time, df);
                                                    fragment.dropDate.setText(fragment.dateTime.toString("hh:mm a EEEE, dd/MM/yyyy"));
                                                    fragment.confirm(false);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        tryFoundShipperDialogFragment.tryAgainCallback = new ICallback() {
                                            @Override
                                            public void excute() {
                                                try {
                                                    fragment.dateTime = null;
                                                    fragment.dropDate.setText(new DateTime().toString("hh:mm a EEEE, dd/MM/yyyy"));
                                                    fragment.confirm(false);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        tryFoundShipperDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), tryFoundShipperDialogFragment.getClass().getName());

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                try {
                                    GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                                    final CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                    if (fragment != null) {
                                        fragment.hideProgress();
                                        fragment.getArguments().putBoolean("cannot_find_shipper", true);
                                        fragment.getArguments().putString("delivery_time", params.getUtfString("set_schedule_time"));
                                        TryFoundShipperDialogFragment tryFoundShipperDialogFragment = new TryFoundShipperDialogFragment();
                                        tryFoundShipperDialogFragment.message = ErrorHelper.getValueByKey(code);
                                        tryFoundShipperDialogFragment.addLaterCallback = new ICallback() {
                                            @Override
                                            public void excute() {
                                                try {
                                                    String time = fragment.getArguments().getString("delivery_time");
                                                    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                                                    LocalDateTime dateTime = LocalDateTime.parse(time, df);
                                                    JSONObject jsonObject = CmmFunc.formatDate(dateTime);
                                                    fragment.day = jsonObject.getInt("day");
                                                    fragment.month = jsonObject.getInt("month");
                                                    fragment.year = jsonObject.getInt("year");
                                                    fragment.hour = jsonObject.getInt("hour");
                                                    fragment.minute = jsonObject.getInt("minute");
                                                    TextView dropDate = (TextView) fragment.getView().findViewById(R.id.drop_date);
                                                    dropDate.setText(jsonObject.getString("value"));
                                                    fragment.confirm(false);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        tryFoundShipperDialogFragment.tryAgainCallback = new ICallback() {
                                            @Override
                                            public void excute() {
                                                try {
                                                    fragment.day = 0;
                                                    fragment.month = 0;
                                                    fragment.year = 0;
                                                    fragment.hour = 0;
                                                    fragment.minute = 0;
                                                    JSONObject jsonObject = CmmFunc.formatDate(new LocalDateTime());
                                                    TextView dropDate = (TextView) fragment.getView().findViewById(R.id.drop_date);
                                                    dropDate.setText(jsonObject.getString("value"));
                                                    fragment.confirm(false);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        tryFoundShipperDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), tryFoundShipperDialogFragment.getClass().getName());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            return;
                        }

                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage(ErrorHelper.getValueByKey(code));
                        messageDialogFragment.setCallback(new ICallback() {
                            @Override
                            public void excute() {
                                if (params.getBool("is_set_schedule_time")) {
                                    if (AppActivity.FLOW == AppActivity.PICKUP) {
                                        ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                        fragment.getArguments().putBoolean("cannot_find_shipper", true);
                                        fragment.getArguments().putString("delivery_time", params.getUtfString("set_schedule_time"));
                                        GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                                        fragment.onResume();
                                    } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                        CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                        fragment.getArguments().putBoolean("cannot_find_shipper", true);
                                        fragment.getArguments().putString("delivery_time", params.getUtfString("set_schedule_time"));
                                        GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                                        fragment.onResume();
                                    }
                                } else if (params.getBool("is_show_follow_list_screen")) {
                                    AppActivity.popRoot();
                                    FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, OrderFollowFragment.newInstance());

                                } else {
                                    if (AppActivity.FLOW == AppActivity.PICKUP) {
                                        ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                        if (fragment != null) {
                                            fragment.hideProgress();
                                        }
                                        GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                                    } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                        CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                        if (fragment != null) {
                                            fragment.hideProgress();
                                        }
                                        GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                                    }
                                }
                            }
                        });
                        messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    }
                });
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "RESPONSE_SHIPPER", e.getMessage());
        }
    }

    private void FINISH_DELIVERY(final SFSObject params) {
        int code = params.getInt("code");
        if (code == 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final Fragment followFragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), FollowJourneyFragment.class.getName());
                        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                        if (fragment instanceof MessageDialogFragment) {
                            if (((MessageDialogFragment) fragment).getName().equals(MessageDialogFragment.FOLLOW_ORDER_STATUS)) {
                                return;
                            }
                        }

                        if (followFragment == null) {
                            return;
                        }

                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setName(MessageDialogFragment.FOLLOW_ORDER_STATUS);
                        messageDialogFragment.setMessage(GlobalClass.getActivity().getResources().getString(R.string.shipper_finish, params.getUtfString("order_code")));
                        messageDialogFragment.setCallback(new ICallback() {
                            @Override
                            public void excute() {
                                Tracking.excute("C9.1Y");
                                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, RateFragment.newInstance(followFragment.getArguments().getInt("order_id")));
                            }
                        });
                        messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), MessageDialogFragment.class.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void UPDATE_SHIPPER_POSITION(final SFSObject params) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Double lat = params.getDouble("shipper_latitude");
                    Double lng = params.getDouble("shipper_longitude");
                    //String description = params.getUtfString("shipper_description");
                    MapHelper.destinations.add(new LatLng(lat, lng));
                    //MapHelper.markerShipper.setTitle(description);
                    FollowJourneyFragment fragment = (FollowJourneyFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), FollowJourneyFragment.class.getName());
                    if (fragment != null) {
                        MapHelper.animateMarker(MapHelper.markerShipper);

                        //shipperPositionTimeInJourney = 60 shipperUpdatePositionTime = 5
//                        if (FollowJourneyFragment.shipperPoints.size() - FollowJourneyFragment.oldSize >= Math.round(CmmVariable.shipperPositionTimeInJourney / CmmVariable.shipperUpdatePositionTime)) {
//                            try {
//                                int startIndex = 0;
//                                if (FollowJourneyFragment.oldSize > 0) {
//                                    startIndex = FollowJourneyFragment.oldSize - 1;
//                                }
//                                JSONObject data = new JSONObject(new MapHelper.DowloadURL(FollowJourneyFragment.shipperPoints.get(startIndex), FollowJourneyFragment.shipperPoints.get(FollowJourneyFragment.shipperPoints.size() - 1)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR).get());
//                                new MapHelper.ActionDrawNew(fragment.map, data, GlobalClass.getActivity().getResources().getColor(R.color.main)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                                FollowJourneyFragment.oldSize = FollowJourneyFragment.shipperPoints.size();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }

//                        final PolylineOptions lineOptions = new PolylineOptions();
//                        lineOptions.add(FollowJourneyFragment.shipperPoints.get(FollowJourneyFragment.shipperPoints.size() - 1));
//                        lineOptions.add(new LatLng(lat,lng));
//                        lineOptions.width(7);
//                        lineOptions.color(GlobalClass.getActivity().getResources().getColor(R.color.main));
//                        fragment.map.addPolyline(lineOptions);
//                        FollowJourneyFragment.shipperPoints.add(new LatLng(lat, lng));


                    }
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "RESET_APP", e.getMessage());
                }


            }
        });
    }

    private void GET_CHAT_HISTORY(final SFSObject params) {

        class getHistory extends AsyncTask<Object, Void, Void> {

            @Override
            protected Void doInBackground(Object... objects) {
                try {
                    CmmVariable.chats.clear();
                    ISFSArray array = (ISFSArray) objects[0];
                    for (int i = 0; i < array.size(); i++) {
                        ISFSObject object = array.getSFSObject(i);
                        BeanChat bean = new BeanChat();
                        bean.setMessage(object.getUtfString("message"));
                        if (bean.getMessage().equals("IMAGE_TYPE")) {
                            byte[] arr = object.getByteArray("chat_description");
                            bean.setImage(object.getByteArray("chat_description"));
                            bean.setBitmap(CmmFunc.getBitmapFromByteArray(arr));
                        } else if (bean.getMessage().equals("TEXT_TYPE")) {
                            bean.setChat_description(object.getUtfString("chat_description"));
                        } else {
                            bean.setUrlImage(object.getUtfString("chat_description"));
                        }
                        String sender = object.getUtfString("sender");
                        if (sender.equals(SmartFoxHelper.getInstance().getMySelf().getName())) {
                            bean.setType(0);
                        } else {
                            bean.setType(1);
                        }
                        CmmVariable.chats.add(bean);
                    }
                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "GET_CHAT_HISTORY", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ChatFragment fragment = (ChatFragment) CmmFunc.getActiveFragment(GlobalClass.getActivity());
                            RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycler);
                            recyclerView.scrollToPosition(CmmVariable.chats.size() - 1);
                            recyclerView.getAdapter().notifyItemInserted(CmmVariable.chats.size());
                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "GET_CHAT_HISTORY", e.getMessage());
                        }
                    }
                });
            }
        }

        ISFSArray array = params.getSFSArray("chat_history");
        if (array == null)
            return;
        new getHistory().execute(array);
    }

    private void PUBLIC_MESSAGE(final BaseEvent event) {
        class get extends AsyncTask<Object, Void, Void> {
            boolean isContinue = false;

            @Override
            protected Void doInBackground(Object... objects) {
                try {
                    BaseEvent event = (BaseEvent) objects[0];
                    User user = (User) event.getArguments().get("sender");

                    if (user.getName().equals(SmartFoxHelper.getInstance().getMySelf().getName()))
                        return null;

                    BeanChat beanChat = new BeanChat();
                    beanChat.setMessage(event.getArguments().get("message") + "");
                    beanChat.setType(1);
                    SFSObject object = (SFSObject) event.getArguments().get("data");

                    if (beanChat.getMessage().equals("TEXT_TYPE")) {
                        String content = object.getUtfString("chat_description");
                        beanChat.setChat_description(content + "");
                    } else if (beanChat.getMessage().equals("IMAGE_TYPE")) {
                        byte[] arr = object.getByteArray("chat_description");
                        beanChat.setImage(arr);
                        beanChat.setBitmap(CmmFunc.getBitmapFromByteArray(arr));

                    }
                    CmmVariable.chats.add(beanChat);
                    isContinue = true;

                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "PUBLIC_MESSAGE", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isContinue) {
                                ChatFragment fragment = (ChatFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ChatFragment.class.getName());
                                if (fragment != null) {
                                    RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(R.id.recycler);
                                    recyclerView.scrollToPosition(CmmVariable.chats.size() - 1);
                                    recyclerView.getAdapter().notifyItemInserted(CmmVariable.chats.size());
                                }
                            }
                        } catch (Exception e) {
                            CmmFunc.showError(getClass().getName(), "PUBLIC_MESSAGE", e.getMessage());
                        }
                    }
                });
            }
        }
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof FollowJourneyFragment) {
                        FollowJourneyFragment followJourneyFragment = ((FollowJourneyFragment) fragment);
                        followJourneyFragment.numberChat += 1;
                        View view = fragment.getView();
                        TextView numberChat = (TextView) view.findViewById(R.id.number_chat);

                        if (numberChat != null) {
                            numberChat.setVisibility(View.VISIBLE);
                            numberChat.setText(followJourneyFragment.numberChat + "");
                        }
                        numberChat.bringToFront();
                    } else if (fragment instanceof ChatFragment) {
                        new get().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, event);
                    }


                } catch (Exception e) {
                    CmmFunc.showError(getClass().getName(), "", e.getMessage());
                }
            }
        });
    }

    private void REQUEST_NL_PAYMENT(final SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                final String url = params.getUtfString("url");
                Fragment fragment = new PaymentWebViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", url);
                fragment.setArguments(bundle);
                FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, fragment);
            } else {
                new ErrorHelper().excute(code);
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "REQUEST_NL_PAYMENT", e.getMessage());
        }
    }

    private void CANCEL_ORDER(final SFSObject params) {
        try {
            int code = params.getInt("code");
            if (code == 1) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                            if (fragment instanceof MessageDialogFragment) {
                                if (((MessageDialogFragment) fragment).getName().equals(MessageDialogFragment.FOLLOW_ORDER_STATUS)) {
                                    return;
                                }
                            }
                            MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                            messageDialogFragment.setName(MessageDialogFragment.FOLLOW_ORDER_STATUS);
                            messageDialogFragment.setMessage(GlobalClass.getActivity().getResources().getString(R.string.order_cancelled, params.getUtfString("order_code")));
                            messageDialogFragment.setCallback(new ICallback() {
                                @Override
                                public void excute() {
                                    AppActivity.popRoot();
                                }
                            });
                            messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                new ErrorHelper().excute(code);
            }
        } catch (Exception e) {
            CmmFunc.showError(getClass().getName(), "CANCEL_ORDER", e.getMessage());
        }
    }

    private void GET_FOLLOW_JOURNEY(final SFSObject params) {

        final int code = params.getInt("code");
        if (code == 1) {
            final SFSObject data = (SFSObject) params.getSFSObject("data");
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final FollowJourneyFragment followJourneyFragment = FollowJourneyFragment.isExist();
                        if (followJourneyFragment != null) {
                            followJourneyFragment.numberChat += data.getInt("new_chat_number");
                            if (followJourneyFragment.getArguments().getInt("order_id") == data.getInt("order_id")) {
                                TextView numberChat = (TextView) followJourneyFragment.view.findViewById(R.id.number_chat);
                                if (followJourneyFragment.numberChat < 1) {
                                    numberChat.setVisibility(View.GONE);
                                } else {
                                    numberChat.setVisibility(View.VISIBLE);
                                    numberChat.setText(followJourneyFragment.numberChat + "");
                                }
                            }
                        }

                        if (followJourneyFragment.shipper != null) {
                            return;
                        }


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final BeanShipper bean = new BeanShipper();
                                    bean.setShipper_fullname(data.getUtfString("shipper_fullname"));
                                    bean.setShipper_name(data.getUtfString("shipper_name"));
                                    bean.setShipper_phone(data.getUtfString("shipper_phone"));
                                    bean.setShipper_avatar(data.getUtfString("shipper_avatar"));
                                    //bean.setShipper_rating(params.getInt("shipper_rating"));
                                    bean.setShipper_latitude(data.getDouble("shipper_latitude"));
                                    bean.setShipper_longitude(data.getDouble("shipper_longitude"));
                                    followJourneyFragment.shipper = bean;
                                    followJourneyFragment.bindBottomData();

                                    followJourneyFragment.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            followJourneyFragment.orderCode.setText(followJourneyFragment.getString(R.string.your_order_code) + " - " + data.getUtfString("verify_code"));
                                            followJourneyFragment.mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        }
                                    });

                                    followJourneyFragment.points = new ArrayList<>();
                                    SFSArray arr = (SFSArray) data.getSFSArray("locations");
                                    for (int i = 0; i < arr.size(); i++) {
                                        BeanPoint point = (BeanPoint) CmmFunc.tryParseJson(arr.getUtfString(i), BeanPoint.class);
                                        followJourneyFragment.points.add(point);
                                    }
                                    followJourneyFragment.draw();

                                    CmmVariable.avatarShipper = CmmFunc.getBitmapFromURL(bean.getShipper_avatar());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    } catch (Exception e) {

                    }
                }
            });
        } else {
//            GlobalClass.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
//                    messageDialogFragment.setMessage(ErrorHelper.getValueByKey(code));
//                    messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
//                }
//            });
        }
    }


    private void CANCEL_REQUEST_SHIPPER(final SFSObject params) {
        final int code = params.getInt("code");

        if (code == 1) {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof MatchFragment) {
                        ((MatchFragment) fragment).hideProgress();
                        if (AppActivity.FLOW == AppActivity.PICKUP) {
                            ConfirmFragment f = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                            if (f != null) {
                                f.hideProgress();
                            }
                            GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                        } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                            CartFragment f = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                            if (f != null) {
                                f.hideProgress();
                            }

                            GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                        }
                    }
                }
            });
        } else {
            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                    if (fragment instanceof MatchFragment) {
                        ((MatchFragment) fragment).hideProgress();
                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage(ErrorHelper.getValueByKey(code));
                        messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    }
                }
            });

        }
    }

    private void CHANGE_REQUEST_SCREEN() {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment matchFragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MatchFragment.class.getName());
                    if (matchFragment != null) {
                        Log.d("CHANGE_REQUEST_SCREEN", "CHANGE_REQUEST_SCREEN");
                        ((MatchFragment) matchFragment).matchContent.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void SEND_REQUEST_ID(final SFSObject params) {
//        GlobalClass.getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Fragment matchFragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MatchFragment.class.getName());
//                    if (matchFragment != null) {
//                        ((MatchFragment) matchFragment).idRequest = params.getUtfString("id_request");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    private void CHECK_REQUEST(final SFSObject params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject data = (SFSObject) params.getSFSObject("data");
                    //Vừa là mã đơn hàng
                    int status = data.getInt("status");
                    //đang tìm shipper
                    if (status == 0) {

                    }
                    //huy
                    else if (status == -1) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                                messageDialogFragment.setMessage(GlobalClass.getActivity().getString(R.string.disconnect_match));
                                messageDialogFragment.setCallback(new ICallback() {
                                    @Override
                                    public void excute() {
                                        try {
                                            if (AppActivity.FLOW == AppActivity.PICKUP) {
                                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                                            } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                            }
                        });
                    } else if (status > 0) {

                        SFSObject sfsObject = new SFSObject();
                        sfsObject.putUtfString("command", "GET_FOLLOW_JOURNEY");
                        sfsObject.putInt("id_order", status);
                        SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void REQUEST_CHAT_CS(final SFSObject params) {

        int code = params.getInt("code");
        if (code != 1) {
            return;
        }

        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Fragment fragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CallCenterFragment.class.getName());
                    if (fragment != null) {
                        CallCenterFragment callCenterFragment = (CallCenterFragment) fragment;
                        SFSObject data = (SFSObject) params.getSFSObject("data");
                        callCenterFragment.room = data.getUtfString("room_name");
                        SmartFoxHelper.getChatCSHistory(callCenterFragment.page);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void CHAT_CS(final SFSObject params) {
        if (params.getUtfString("sender_username").equals(SmartFoxHelper.getInstance().getMySelf().getName())) {
            return;
        }
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Fragment fragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CallCenterFragment.class.getName());
                    if (fragment != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final CallCenterFragment callCenterFragment = (CallCenterFragment) fragment;
                                    BeanCSChat beanCSChat = new BeanCSChat();
                                    beanCSChat.setSender_fullname(params.getUtfString("sender_fullname"));
                                    beanCSChat.setSender_username(params.getUtfString("sender_username"));
                                    beanCSChat.setSender_avatar(params.getUtfString("sender_avatar"));
                                    DateTime dateTime = new DateTime();
                                    beanCSChat.setTime(dateTime.toString(BeanCSChat.FORMAT));
                                    beanCSChat.setChat((SFSObject) params.getSFSObject("chat"));
                                    callCenterFragment.chats.add(beanCSChat);

                                    GlobalClass.getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                int position = callCenterFragment.chats.size() - 1;
                                                callCenterFragment.adapter.notifyItemInserted(position);
                                                if (callCenterFragment.chats.size() - callCenterFragment.layoutManager.findLastCompletelyVisibleItemPosition() < 8) {
                                                    callCenterFragment.recycler.smoothScrollToPosition(position);
                                                } else {
                                                    Toast.makeText(fragment.getActivity(), "You have a new message", Toast.LENGTH_SHORT).show();
                                                }
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


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void GET_CHAT_CS_HISTORY(final SFSObject params) {

        try {
            int code = params.getInt("code");
            if (code != 1) {
                GlobalClass.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Fragment fragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CallCenterFragment.class.getName());
                            if (fragment != null) {
                                final CallCenterFragment callCenterFragment = (CallCenterFragment) fragment;
                                callCenterFragment.hideProgress();
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                return;
            }

            SFSObject data = (SFSObject) params.getSFSObject("data");
            SFSArray chatHistory = (SFSArray) data.getSFSArray("chat_history");
            final List<BeanCSChat> chats = new ArrayList<>();
            for (int i = 0; i < chatHistory.size(); i++) {
                SFSObject sfsObject = (SFSObject) chatHistory.getSFSObject(i);
                BeanCSChat bean = new BeanCSChat();
                bean.setSender_username(sfsObject.getUtfString("sender_username"));
                bean.setSender_fullname(sfsObject.getUtfString("sender_fullname"));
                bean.setSender_avatar(sfsObject.getUtfString("sender_avatar"));
                bean.setChat((SFSObject) sfsObject.getSFSObject("chat"));
                bean.setTime(sfsObject.getUtfString("time"));
                chats.add(bean);
            }

            GlobalClass.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Fragment fragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CallCenterFragment.class.getName());
                        if (fragment != null) {
                            final CallCenterFragment callCenterFragment = (CallCenterFragment) fragment;
                            for (BeanCSChat beanCSChat : chats) {
                                callCenterFragment.chats.add(0, beanCSChat);
                                callCenterFragment.adapter.notifyItemInserted(0);
                            }

                            if (chats.size() > 0) {
                                if (callCenterFragment.page == 1) {
                                    callCenterFragment.recycler.scrollToPosition(callCenterFragment.chats.size() - 1);
                                }
                                callCenterFragment.recycler.addOnScrollListener(callCenterFragment.scrollListener);

                            } else {
                                callCenterFragment.recycler.removeOnScrollListener(callCenterFragment.scrollListener);
                            }
                            callCenterFragment.hideProgress();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

}
