package vn.nip.around.Service;

import android.util.Log;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by viminh on 5/16/2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService  {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        updateToken(refreshedToken);
    }

    private void updateToken(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    APIHelper.updateDeviceToken(token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
