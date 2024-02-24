package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentMethodFragment extends Fragment implements View.OnClickListener, View.OnKeyListener, CompoundButton.OnCheckedChangeListener {

    //region Private variables
    View view;
    CircleProgressBar circleProgressBar;
    RadioGroup radioGroup;
    RadioButton cast;
    RadioButton credit;
    private FrameLayout layoutProgress;
    //endregion

    //region Contructors
    public PaymentMethodFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Override
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_payment_method, container, false);
            layoutProgress = (FrameLayout) view.findViewById(R.id.layout_progress);
            circleProgressBar = (CircleProgressBar) view.findViewById(R.id.progressBar);
            radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
            cast = (RadioButton) view.findViewById(R.id.cast);
            credit = (RadioButton) view.findViewById(R.id.credit_card);
            cast.setOnCheckedChangeListener(PaymentMethodFragment.this);
            credit.setOnCheckedChangeListener(PaymentMethodFragment.this);

            CardView confirm = (CardView) view.findViewById(R.id.confirm);
            confirm.setOnClickListener(this);
            new ActionGetPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(getResources().getString(R.string.payment_methods));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            int id = radioGroup.getCheckedRadioButtonId();
                            if (id == R.id.cast) {
                                jsonObject.put("payment_type", "CASH");
                            } else if (id == R.id.credit_card) {
                                jsonObject.put("payment_type", "ONLINE");

                            }
                            new ActionUpdatePayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, jsonObject);
                        } catch (Exception e) {

                        }
                    }
                });

                break;

            case R.id.cast:
                Tracking.excute("C18.1Y");
                break;
            case R.id.credit_card:
                Tracking.excute("C18.2Y");
                break;
        }
    }
    //endregion

    //region Methods

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                (i == KeyEvent.KEYCODE_ENTER)) {
            CmmFunc.hideKeyboard(getActivity());
            return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cast:
                if (b) {
                    cast.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cast_selected, 0, 0, 0);
                } else {
                    cast.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cast_unselected, 0, 0, 0);

                }
                break;
            case R.id.credit_card:
                if (b) {
                    credit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_online_selected, 0, 0, 0);
                } else {
                    credit.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_online_unselected, 0, 0, 0);

                }
                break;
        }
    }
    //endregion

    //region Actions
    class ActionGetPayment extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_payment", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetPayment", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                radioGroup.clearCheck();
                if (jsonObject == null) {
                    CustomDialog.showMessage(getActivity(), "Error", "Can't get data.");
                    return;
                }
                new ErrorHelper().excute(jsonObject);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    String paymentType = jsonObject.getString("payment_type");
                    if (paymentType.equals("ONLINE")) {
                        credit.setChecked(true);

                    }

                    if (paymentType.equals("CASH")) {
                        cast.setChecked(true);
                    }


                }
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetPayment", e.getMessage());
            } finally {

            }
        }
    }

    class ActionUpdatePayment extends ActionAsync {
        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                JSONObject param = (JSONObject) objects[0];
                String payment_type = param.getString("payment_type");
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("payment_type", payment_type));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_payment", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionUpdatePayment", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject.getInt("code") == 1) {
                    FragmentHelper.pop(getActivity());
                }
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionUpdatePayment", e.getMessage());
            }
        }
    }
    //endregion

}
