package vn.nip.around.Helper;

import android.util.Log;

import sfs2x.client.util.ConfigData;
import vn.nip.around.Bean.BeanUser;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.SmartFoxListener;

import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import org.json.JSONArray;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.LoginRequest;

/**
 * Created by viminh on 10/20/2016.
 */

public class SmartFoxHelper {
    //region Variables
    static SmartFoxListener smartFoxListener = new SmartFoxListener() {
    };

    private static SmartFox sfsClient;
    //endregion

    //region Init

    public static SmartFox getInstance() {
        if (sfsClient == null) {
            sfsClient = new SmartFox(true);
            //initListener();
        }
        return sfsClient;
    }

    //endregion

    //region Connect

    public static void initSmartFox() {
        if (getInstance().isConnecting()) {
            sfsClient = null;
        }
        getInstance().removeAllEventListeners();
        getInstance().disconnect();
        getInstance().addEventListener(SFSEvent.CONNECTION, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) {
                Log.d("MMM", "CONNECTION");
                smartFoxListener.connection(baseEvent);
            }
        });

        getInstance().addEventListener(SFSEvent.CONNECTION_LOST, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMM", "CONNECTION_LOST");
                smartFoxListener.connectionLost(baseEvent);

            }
        });

        getInstance().addEventListener(SFSEvent.LOGIN, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMM", "LOGIN");
                smartFoxListener.login(baseEvent);

            }
        });
        getInstance().addEventListener(SFSEvent.LOGIN_ERROR, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMM", "LOGIN_ERROR");
                smartFoxListener.loginError(baseEvent);
            }
        });

        getInstance().addEventListener(SFSEvent.PUBLIC_MESSAGE, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMM", "PUBLIC_MESSAGE");
                smartFoxListener.publishMessage(baseEvent);
            }
        });
        getInstance().addEventListener(SFSEvent.EXTENSION_RESPONSE, new IEventListener() {
            @Override
            public void dispatch(final BaseEvent baseEvent) throws SFSException {
                Log.d("MMM", "EXTENSION_RESPONSE");
                smartFoxListener.extensionResponse(baseEvent);

            }
        });
        getInstance().setReconnectionSeconds(1);
        ConfigData configData = new ConfigData();
        configData.setDebug(true);
        configData.setZone("AroundAppZone");
        configData.setHost(CmmVariable.smartFoxDomain);
        configData.setPort(Integer.parseInt(CmmVariable.smartFoxPort));
        getInstance().connect(configData);

    }
    //endregion

    private static void initListener() {
        //sfsClient.removeAllEventListeners();
        // Add event listeners


    }

    //region Actions

    //region Login

    //region Request shipper
    public static void requestShipper(final int id, final String idRequest, final String type, final String locations,
                                      final int year, final int month, final int day, final int hour, final int minute, final boolean isReturnPickup) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "REQUEST_SHIPPER");
                    sfsObject.putUtfString("type", type);
                    if (locations != null && !locations.equals("")) {
                        SFSArray sfsArray = new SFSArray();
                        JSONArray locationArr = new JSONArray(locations);
                        for (int i = 0; i < locationArr.length(); i++) {
                            sfsArray.addUtfString(locationArr.getString(i));
                        }
                        sfsObject.putSFSArray("locations", sfsArray);
                    }
                    sfsObject.putInt("online_payment_order_id", id);
                    if (idRequest == null) {
                        sfsObject.putUtfString("id_request", "");
                    } else {
                        sfsObject.putUtfString("id_request", idRequest);
                    }

                    sfsObject.putInt("year", year);
                    sfsObject.putInt("month", month);
                    sfsObject.putInt("day", day);
                    sfsObject.putInt("hour", hour);
                    sfsObject.putInt("minute", minute);
                    sfsObject.putBool("return_to_pickup", isReturnPickup);

                    sfsClient.send(new ExtensionRequest("user", sfsObject));

                } catch (Exception e) {

                }
            }
        }).start();

    }

    //endregion

    public static void login(final BeanUser user) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putInt("type", 0);
                sfsObject.putUtfString("country_code", StorageHelper.getCountryCode());
                sfsObject.putUtfString("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext()));
                sfsObject.putUtfString("devicetoken", CmmFunc.getDeviceToken(GlobalClass.getActivity()));
                sfsObject.putUtfString("os", "ANDROID");
                sfsObject.putUtfString("token", StorageHelper.getToken());
                sfsObject.putUtfString("version", CmmVariable.version);
                SmartFoxHelper.getInstance().send(new LoginRequest(user.getPhone(), user.getMD5Password(), "AroundAppZone", sfsObject));
            }
        }).start();
    }
    //endregion

    //region Ping
    public static void ping(final boolean isFirst) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isFirst) {
                        Thread.sleep(TimerHelper.pingTime * 1000);
                    }
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "PING");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                } catch (Exception e) {
                }
                ping(false);
            }
        }).start();
    }
    //endregion

    //region Ping
    public static void cancelFindShipper() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "CANCEL_REQUEST_SHIPPER");
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                } catch (Exception e) {
                }

            }
        }).start();
    }
    //endregion

    public static void checkRequest(final String idRequest) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putUtfString("command", "CHECK_REQUEST");
                    sfsObject.putUtfString("id_request", idRequest);
                    SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
                } catch (Exception e) {
                }

            }
        }).start();
    }

    public static void getFollowJourney(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_FOLLOW_JOURNEY");
                sfsObject.putInt("id_order", id);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
            }
        }).start();

    }

    public static void chatCS(final SFSObject chat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "CHAT_CS");
                sfsObject.putSFSObject("chat", chat);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
            }
        }).start();
    }

    public static void getChatCSHistory(final int page) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "GET_CHAT_CS_HISTORY");
                sfsObject.putInt("page", page);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
            }
        }).start();
    }

}
