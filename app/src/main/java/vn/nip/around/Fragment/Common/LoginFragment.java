package vn.nip.around.Fragment.Common;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.Tracking;

import vn.nip.around.Fragment.Dialog.OTPDialogFragment;
import vn.nip.around.Fragment.Dialog.OnePayDialogFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment implements View.OnClickListener {

    //region Private Variables
    final String countryCode = "84";
    EditText name, phone;
    Button confirm;
    Drawable icError;
    //endregion

    //region Constructor
    public LoginFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_login, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isLoaded == false) {
                    name = (EditText) view.findViewById(R.id.name);
                    phone = (EditText) view.findViewById(R.id.phone);
                    confirm = (Button) view.findViewById(R.id.confirm);
                    icError = getResources().getDrawable(R.drawable.ic_error);
                    icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                    confirm.setOnClickListener(LoginFragment.this);
                    view.findViewById(R.id.support).setOnClickListener(LoginFragment.this);

                    name.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (name.getCompoundDrawables()[2] == null) {
                                return;
                            }
                            name.setCompoundDrawables(null, null, null, null);
                        }
                    });

                    phone.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (phone.getCompoundDrawables()[2] == null) {
                                return;
                            }
                            phone.setCompoundDrawables(null, null, null, null);
                        }
                    });
                    isLoaded = true;
                }
            }
        }).start();

    }

    //endregion

    //region Method
    private void confirm() {
        CmmFunc.hideKeyboard(getActivity());
        String fullName = name.getText().toString().trim();
//        if (fullName.equals(StringUtils.EMPTY)) {
//            name.setCompoundDrawables(null, null, icError, null);
//            name.requestFocus();
//            return;
//        }
        String phoneNumber = phone.getText().toString().trim();
        if (!Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumber.length() < 9 || phoneNumber.length() > 11) {
            phone.setCompoundDrawables(null, null, icError, null);
            phone.requestFocus();
            return;
        }
        new ActionLogin().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, countryCode, phoneNumber, fullName);

    }
    //endregion

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                confirm();
                break;

            case R.id.support:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", CmmVariable.phoneService, null));
                startActivity(intent);
                break;
        }
    }
    //endregion

    //region Actions
    class ActionLogin extends ActionAsync {
        String phoneNumber, country;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                country = (String) params[0];
                phoneNumber = (String) params[1];
                String fullName = (String) params[2];
                String response = APIHelper.login(country, phoneNumber, fullName);
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
                    OTPDialogFragment.newInstance(country, phoneNumber).show(getActivity().getSupportFragmentManager(), OnePayDialogFragment.class.getName());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
    //endregion
}


