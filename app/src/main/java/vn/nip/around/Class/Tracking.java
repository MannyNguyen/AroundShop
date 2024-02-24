package vn.nip.around.Class;

import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HOME on 5/10/2017.
 */

public class Tracking {
    public static void excute(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("devicetoken", CmmFunc.getDeviceToken(GlobalClass.getActivity())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    params.add(new AbstractMap.SimpleEntry("action", id + ""));
                    String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/send_action_info", params, false);
                    if(response.equals("")){

                    }
                } catch (Exception e) {

                }
            }
        }).start();

    }
}
