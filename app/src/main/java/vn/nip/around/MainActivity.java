package vn.nip.around;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Helper.TimerHelper;
import vn.nip.around.Interface.ICallback;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    //region Variables
    final int PERMISSION_CONTACT = 100;
    //endregion

    //region Init
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        GlobalClass.setActivity(MainActivity.this);
        StorageHelper.init(getApplicationContext());
        //MainActivity.this.getIntent().getExtras().get("main_picture")
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StorageHelper.init(getApplicationContext());
                    if (StorageHelper.getLanguage().equals(StringUtils.EMPTY)) {
                        String lang = Locale.getDefault().getLanguage();
                        StorageHelper.saveLanguage(lang);
                    }
                    Locale locale = new Locale(StorageHelper.getLanguage());
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkCondition();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CONTACT:
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
                        }
                    }
                }).start();
                break;
        }

        new ActionGetAppConfig().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CmmVariable.MY_PERMISSION_ACCESS_WIFI:
                checkCondition();
                break;
        }
    }
    //endregion

    //region Methods

    private boolean checkCondition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                    boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .isConnected();
                    boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                            .isConnected();
                    if (!isWifi && !is3g) {
                        openNetwork();
                        return;
                    }
                    //TRUE
                    new ActionGetIP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    private void routers() {
        try {
            Intent intent = new Intent(MainActivity.this, AppActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Bundle bundle = MainActivity.this.getIntent().getExtras();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            Uri data = getIntent().getData();
            if (data != null) {
                intent.setData(data);
            }
            startActivity(intent);
            finish();
            sendLoginInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openNetwork() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CustomDialog.Dialog2Button(MainActivity.this,
                        getString(vn.nip.around.R.string.access_to_network), getString(vn.nip.around.R.string.enable_network), getString(vn.nip.around.R.string.wifi), getString(vn.nip.around.R.string.threeg),
                        new ICallback() {
                            @Override
                            public void excute() {
                                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), CmmVariable.MY_PERMISSION_ACCESS_WIFI);
                            }
                        }, new ICallback() {
                            @Override
                            public void excute() {
                                startActivityForResult(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS), CmmVariable.MY_PERMISSION_ACCESS_WIFI);
                            }
                        }
                );
            }
        });
    }
    //endregion

    //region Actions
    private class ActionGetAppConfig extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("error_version", StorageHelper.getConfigVersion() + ""));
                params.add(new AbstractMap.SimpleEntry("app_version", CmmVariable.version));
                params.add(new AbstractMap.SimpleEntry("app_os", "ANDROID"));
                //params.add(new AbstractMap.SimpleEntry("language", "ENGLISH"));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_app_config", params);
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
                JSONObject data = jsonObject.getJSONObject("data");
                CmmVariable.onepayTypes = data.getJSONArray("payment");

                //region order
                JSONObject order = data.getJSONObject("order");
                CmmVariable.giftBoxFee = order.getString("giftbox_fee");
                CmmVariable.minPurchaseFee = order.getInt("min_item_cost_purchase");
                CmmVariable.maxCodFee = order.getInt("max_item_cost_cod");
                CmmVariable.minCodeFee = order.getInt("min_item_cost_cod");
                //endregion

                //region aroundpay
                CmmVariable.aroundPay = data.getJSONObject("aroundpay");
                CmmVariable.minGiftAroundPay = CmmVariable.aroundPay.getInt("min_gift_around_pay");
                CmmVariable.maxGiftAroundPay = CmmVariable.aroundPay.getInt("max_gift_around_pay");
                //endregion

                //region support
                JSONObject support = data.getJSONObject("support");
                CmmVariable.phoneService = support.getString("customer_service_phone");
                //endregion

                //region smartfox
                JSONObject smartfox = data.getJSONObject("smartfox");
                TimerHelper.pingTime = smartfox.getInt("ping_time");
                TimerHelper.requestShipperTimeout = smartfox.getInt("request_shipper_timeout");
                TimerHelper.responseShipperTimeout = smartfox.getInt("response_shipper_timeout");
                //thời gian để user vẽ đường đi của shipper trong Journey
                CmmVariable.shipperPositionTimeInJourney = smartfox.getInt("draw_shipper_position_time_in_journey");
                //thời gian để user vẽ đường đi của shipper ngoai Journey
                CmmVariable.shipperPositionTimeOutJourney = smartfox.getInt("draw_shipper_position_time_out_journey");
                //thời gian shipper gửi vị trí lên server trong journey
                CmmVariable.shipperUpdatePositionTime = smartfox.getInt("shipper_update_postion_time_in_journey");

                //Thời gian delay delivery
                CmmVariable.deliveryTime = smartfox.getInt("set_schedule_control_time");
                //Thời gian chưa set delay delivery
                CmmVariable.unDeliveryTime = smartfox.getInt("set_unschedule_control_time");
                //Thời gian hiển thị nút cancel màn hình Match
                CmmVariable.showCancelRequest = smartfox.getInt("show_cancel_request_shipper_time");

                CmmVariable.maxDay = smartfox.getInt("max_delivery_day");
                CmmVariable.minGiftPaymentCost = smartfox.getInt("min_gifting_payment_cost");
                //endregion

                //region image
                JSONObject image = data.getJSONObject("image");
                CmmVariable.popupPriceVN = image.getString("popup_price_vn");
                CmmVariable.popupPriceEN = image.getString("popup_price_en");

                CmmVariable.popupGiftingServiceFeeVN = image.getString("popup_gifting_service_fee_vn");
                CmmVariable.popupGiftingServiceFeeEN = image.getString("popup_gifting_service_fee_en");
                CmmVariable.popupPickupServiceFeeEN = image.getString("popup_pickup_service_fee_en");
                CmmVariable.popupPickupServiceFeeVN = image.getString("popup_pickup_service_fee_vn");
                //endregion

                JSONObject googleKey = data.getJSONObject("google_key");
                CmmVariable.GOOGLE_KEY = googleKey.getString("android_key");

                //region error
                JSONObject error = data.getJSONObject("error");
                int statusError = error.getInt("status");
                if (statusError == 0) {
                    if (StorageHelper.getLanguage().equals("vi")) {
                        CmmVariable.jsonError = new JSONArray(StorageHelper.getContentVNError());
                    } else {
                        CmmVariable.jsonError = new JSONArray(StorageHelper.getContentError());
                    }
                } else if (statusError == 1) {
                    StorageHelper.saveConfigVersion(error.getInt("version"));
                    StorageHelper.saveContentVNError(error.getString("vn_content"));
                    StorageHelper.saveContentError(error.getString("content"));
                    if (StorageHelper.getLanguage().equals("vi")) {
                        CmmVariable.jsonError = new JSONArray(StorageHelper.getContentVNError());
                    } else {
                        CmmVariable.jsonError = new JSONArray(StorageHelper.getContentError());
                    }
                }
                //endregion

                //region maintenance
                JSONObject maintenance = data.getJSONObject("maintenance");
                int statusMaintenance = maintenance.getInt("status");
                if (statusMaintenance == 1) {
                    String message = maintenance.getString("message");
                    if (StorageHelper.getLanguage().equals("vi")) {
                        message = maintenance.getString("vn_message");
                    }
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage(message);
                    messageDialogFragment.setCallback(new ICallback() {
                        @Override
                        public void excute() {
                            GlobalClass.getActivity().finishAffinity();
                        }
                    });
                    messageDialogFragment.show(getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                    return;
                }
                //endregion

                //region update
                final JSONObject update = data.getJSONObject("update");
                int statusUpdate = update.getInt("status");
                if (statusUpdate == 1) {
                    String message = update.getString("message");
                    if (StorageHelper.getLanguage().equals("vi")) {
                        message = update.getString("vn_message");
                    }

                    CustomDialog.Dialog2Button(MainActivity.this, "", message, getString(vn.nip.around.R.string.ok), getString(vn.nip.around.R.string.cancel), new ICallback() {
                        @Override
                        public void excute() {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(update.getString("url")));
                                startActivity(i);
                                finish();
                            } catch (Exception e) {

                            }
                        }
                    }, new ICallback() {
                        @Override
                        public void excute() {
                            routers();
                        }
                    });

                } else if (statusUpdate == 2) {
                    String message = update.getString("message");
                    if (StorageHelper.getLanguage().equals("vi")) {
                        message = update.getString("vn_message");
                    }
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage(message);
                    messageDialogFragment.setCallback(new ICallback() {
                        @Override
                        public void excute() {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(update.getString("url")));
                                startActivity(i);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    messageDialogFragment.show(getSupportFragmentManager(), messageDialogFragment.getClass().getName());

                } else if (statusUpdate == 0) {

                    routers();
                }

                //endregion
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void sendLoginInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("os", "ANDROID"));
                params.add(new AbstractMap.SimpleEntry("os_version", "ANDROID" + "|" + Build.MANUFACTURER + "|" + Build.MODEL + "|" + Build.VERSION.SDK_INT + "|" + Build.VERSION.RELEASE));
                params.add(new AbstractMap.SimpleEntry("version", CmmVariable.version));
                HttpHelper.post(CmmVariable.getDomainAPI() + "/user/send_login_info", params, false);
            }
        }).start();
    }

    class ActionGetIP extends AsyncTask<Object, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Object... objects) {
            try {
                String response = HttpHelper.get(CmmVariable.linkServer, null);
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("version").equals(CmmVariable.version)) {
                        CmmVariable.smartFoxIP = jsonObject.getString("smartfox_ip");
                        CmmVariable.smartFoxDomain = jsonObject.getString("smartfox_domain");
                        CmmVariable.smartFoxPort = jsonObject.getInt("smartfox_port") + "";
                        CmmVariable.restfulIP = jsonObject.getString("restful_ip");
                        CmmVariable.restfulPort = jsonObject.getInt("restful_port") + "";
                    }
                }
                return jsonArray;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if (jsonArray != null) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    new ActionGetAppConfig().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_CONTACT);
                } else {
                    new ActionGetAppConfig().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }

            }
        }
    }
    //endregion
}
