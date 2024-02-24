package vn.nip.around.Class;

import android.os.AsyncTask;
import android.view.View;

import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.R;
import vn.nip.around.Util.NetworkUtil;

import org.json.JSONObject;

/**
 * Created by viminh on 1/5/2017.
 */

public abstract class ActionAsync extends AsyncTask<Object, Void, JSONObject> {

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        try {
            if (jsonObject == null) {
                if (NetworkUtil.getConnectivityStatus(GlobalClass.getContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    View view = GlobalClass.getActivity().findViewById(R.id.notice);
                    if (view.getVisibility() == View.GONE) {
                        GlobalClass.getActivity().findViewById(R.id.notice).setVisibility(View.VISIBLE);
                    }

                } else {
                    CustomDialog.showMessage(GlobalClass.getActivity(), "", GlobalClass.getActivity().getString(R.string.cant_get_data));
                }
                return;
            }
            int code = jsonObject.getInt("code");

            if (code != 1) {
                new ErrorHelper().excute(jsonObject);
                return;
            }
        } catch (Exception e) {

        }
    }
}
