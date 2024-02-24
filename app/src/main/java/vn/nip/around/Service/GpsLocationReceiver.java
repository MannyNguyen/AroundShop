package vn.nip.around.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.MainActivity;
import vn.nip.around.R;

/**
 * Created by viminh on 12/16/2016.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
//            if (GlobalClass.getActivity() instanceof MainActivity) {
//                return;
//            }
//            try {
//                CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.error), GlobalClass.getActivity().getResources().getString(R.string.your_gps_disabled), new ICallback() {
//                    @Override
//                    public void excute() {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent intent = new Intent(GlobalClass.getActivity(), MainActivity.class);
//                                GlobalClass.getActivity().startActivity(intent);
//                                GlobalClass.getActivity().finish();
//                            }
//                        }).start();
//                    }
//                });
//            } catch (Exception e) {
//
//            }
//        }
    }
}
