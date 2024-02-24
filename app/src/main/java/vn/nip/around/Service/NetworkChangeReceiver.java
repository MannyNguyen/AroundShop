package vn.nip.around.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.MainActivity;
import vn.nip.around.R;
import vn.nip.around.Util.NetworkUtil;

/**
 * Created by viminh on 12/16/2016.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean isConnectSmartfox = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (GlobalClass.getActivity() instanceof MainActivity) {
            return;
        }

        try {
            if (NetworkUtil.getConnectivityStatus(GlobalClass.getContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
                View view = GlobalClass.getActivity().findViewById(R.id.notice);
                if (view.getVisibility() == View.GONE) {
                    GlobalClass.getActivity().findViewById(R.id.notice).setVisibility(View.VISIBLE);
                }

                if (isConnectSmartfox == false) {
                    isConnectSmartfox = true;
                    SmartFoxHelper.getInstance().disconnect();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(10000);
                                isConnectSmartfox = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

            } else {
                isConnectSmartfox = false;
                if (AppActivity.IS_RECONNECT_SMARTFOX == false) {
                    return;
                }
                SmartFoxHelper.initSmartFox();
            }

        } catch (Exception e) {

        }
    }


}
