package vn.nip.around.Helper;

import android.content.Intent;
import android.support.v4.app.Fragment;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.FollowJourneyFragment;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.LoginActivity;
import vn.nip.around.MainActivity;
import vn.nip.around.R;

import org.json.JSONObject;

/**
 * Created by viminh on 12/12/2016.
 */

public class ErrorHelper {
    public static String getValueByKey(int key) {
        try {
            for (int i = 0; i < CmmVariable.jsonError.length(); i++) {
                JSONObject jsonObject = CmmVariable.jsonError.getJSONObject(i);
                if (key == jsonObject.getInt("id")) {
                    //return (String) jsonObject.get(jsonObject.names().getString(0));
                    return jsonObject.getString("description");
                }
            }
        } catch (Exception e) {

        }
        return "";
    }

    public void excute(JSONObject jsonObject) {
        try {
            int code = jsonObject.getInt("code");
            if (code != 1) {
                switch (code) {
                    case 0:
                        error(jsonObject);
                        return;
                    case -1:
                        reLogin();
                        return;
                    case -2:
                        followJourney();
                        return;
                    default:
                        message(code);
                        return;
                }
            }

        } catch (Exception e) {

        }
    }

    private void followJourney() {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CmmFunc.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.error), getValueByKey(-2), new ICallback() {
                        @Override
                        public void excute() {
                            Fragment fragment = CmmFunc.getFragmentByTag(GlobalClass.getActivity(), FollowJourneyFragment.class.getName());
                            if (fragment == null) {
                                Intent intent = new Intent(GlobalClass.getActivity(), MainActivity.class);
                                GlobalClass.getActivity().startActivity(intent);
                                GlobalClass.getActivity().finish();
                            } else {
                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(FollowJourneyFragment.class.getName(), 0);
                            }
                        }
                    });

                } catch (Exception e) {

                }
            }
        });

    }

    public void excute(int code) {
        switch (code) {
            case -2:
                followJourney();
                return;
            case -1:
                reLogin();
                return;
            default:
                message(code);
                return;
        }

    }


    private void reLogin() {
        Intent intent = new Intent(GlobalClass.getActivity(), LoginActivity.class);
        GlobalClass.getActivity().startActivity(intent);
        GlobalClass.getActivity().finish();
        StorageHelper.resetUser();
    }

    private void error(final JSONObject jsonObject) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.error), getValueByKey(0));
                } catch (Exception e) {

                }
            }
        });
    }

    private void message(final int key) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    CustomDialog.showMessage(GlobalClass.getActivity(), GlobalClass.getActivity().getResources().getString(R.string.error), getValueByKey(key));
                } catch (Exception e) {

                }
            }
        });
    }


}
