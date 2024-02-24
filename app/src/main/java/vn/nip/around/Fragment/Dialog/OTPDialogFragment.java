package vn.nip.around.Fragment.Dialog;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTPDialogFragment extends BaseDialogFragment implements View.OnClickListener {

    String phoneNumber;
    String countryCode;

    EditText otp;
    Button cancel, ok;
    TextView phone;

    public OTPDialogFragment() {
        // Required empty public constructor
    }

    public static OTPDialogFragment newInstance(String countryCode, String phone) {

        Bundle args = new Bundle();
        args.putString("countryCode", countryCode);
        args.putString("phone", phone);
        OTPDialogFragment fragment = new OTPDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otpdialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCancelable(false);
        countryCode = getArguments().getString("countryCode");
        phoneNumber = getArguments().getString("phone");
        phone = (TextView) view.findViewById(R.id.phone);
        phone.setText(phoneNumber + "");
        otp = (EditText) view.findViewById(R.id.otp);
        ok = (Button) view.findViewById(R.id.ok);
        cancel = (Button) view.findViewById(R.id.cancel);
        ok.setOnClickListener(OTPDialogFragment.this);
        cancel.setOnClickListener(OTPDialogFragment.this);

        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (otp.getCompoundDrawables()[2] == null) {
                    return;
                }
                otp.setCompoundDrawables(null, null, null, null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                String otpText = otp.getText().toString().trim();
                if (otpText.equals(StringUtils.EMPTY)) {
                    Drawable icError = getResources().getDrawable(R.drawable.ic_error);
                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                    otp.setCompoundDrawables(null, null, icError, null);
                    return;
                }
                ok.setOnClickListener(null);
                new ActionVerify().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, otpText);
                break;
            case R.id.cancel:
                dismissAllowingStateLoss();
                break;
        }
    }

    //region Actions
    class ActionVerify extends ActionAsync {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String otp = (String) params[0];
                String response = APIHelper.verify(countryCode, phoneNumber, otp);
                jsonObject = new JSONObject(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    StorageHelper.saveToken(jsonObject.getString("token"));
                    StorageHelper.savePhone(phoneNumber);
                    StorageHelper.saveCountryCode(countryCode);
                    Intent intent = new Intent(getActivity(), AppActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                ok.setOnClickListener(OTPDialogFragment.this);
            }
        }
    }
    //endregion

}
