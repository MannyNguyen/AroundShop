package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PromotionFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    int limitSize = 20;
    //endregion

    //region Contructors
    public PromotionFragment() {
        // Required empty public constructor
    }
    //endregion


    //region init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_promotion, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            init();
            isLoaded = true;
        }
    }

    private void init() {
        CmmFunc.setDelay(400, new ICallback() {
            @Override
            public void excute() {
                try {
                    view.findViewById(R.id.share_container).setOnClickListener(PromotionFragment.this);
                    final EditText promoCode = (EditText) view.findViewById(R.id.promo_code);
                    promoCode.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            if (charSequence.length() == limitSize) {
                                new ActionUpdatePromo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, charSequence.toString());
                            } else {
                                ImageView checkPromo = (ImageView) view.findViewById(R.id.check_promo);
                                checkPromo.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    //promoCode.removeTextChangedListener(PromotionFragment.this);
                    new ActionGetPromo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_container:
                CustomDialog.showMessage(getActivity(), "", getString(R.string.promotion_share_notice));
                break;
        }
    }


    //endregion

    //region Actions
    class ActionGetPromo extends ActionAsync {
        JSONObject jsonObject;

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_promo_code", params);
                jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    EditText promoCode = (EditText) view.findViewById(R.id.promo_code);
                    promoCode.setText(data.getString("promo_code") + "");
                    ImageView checkPromo = (ImageView) view.findViewById(R.id.check_promo);
                    //checkPromo.setVisibility(View.VISIBLE);
                    limitSize = data.getInt("limit_size");
                }

            } catch (Exception e) {

            }
        }
    }

    class ActionUpdatePromo extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            try {
                String promoCode = (String) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("promo_code", promoCode));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_promo_code", params, false);
                jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "", e.getMessage());
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    ImageView checkPromo = (ImageView) view.findViewById(R.id.check_promo);
                    checkPromo.setVisibility(View.VISIBLE);
                    JSONObject data = jsonObject.getJSONObject("data");
                    int value = data.getInt("value");
                    CustomDialog.showMessage(getActivity(), "", getString(R.string.promotion_value, value));
                } else {
                    ImageView checkPromo = (ImageView) view.findViewById(R.id.check_promo);
                    checkPromo.setVisibility(View.GONE);
                    new ErrorHelper().excute(jsonObject);
                }


            } catch (Exception e) {

            } finally {

            }
        }
    }

    //endregion
}
