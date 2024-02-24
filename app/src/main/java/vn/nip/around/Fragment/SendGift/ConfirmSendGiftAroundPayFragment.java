package vn.nip.around.Fragment.SendGift;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.AroundWalletFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

public class ConfirmSendGiftAroundPayFragment extends BaseFragment implements View.OnClickListener {

    JSONObject param;
    TextView money, deposit, phone, name, serviceFee, total;

    public ConfirmSendGiftAroundPayFragment() {
        // Required empty public constructor
    }

    public static ConfirmSendGiftAroundPayFragment newInstance(String data, String message) {

        Bundle args = new Bundle();
        args.putString("data", data);
        args.putString("message", message);
        ConfirmSendGiftAroundPayFragment fragment = new ConfirmSendGiftAroundPayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_confirm_send_gift_around_pay, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            param = new JSONObject(getArguments().getString("data"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.send_gift));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    money = (TextView) view.findViewById(R.id.money);
                    deposit = (TextView) view.findViewById(R.id.deposit);
                    phone = (TextView) view.findViewById(R.id.phone);
                    name = (TextView) view.findViewById(R.id.name);
                    serviceFee = (TextView) view.findViewById(R.id.service_fee);
                    total = (TextView) view.findViewById(R.id.total);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (param != null) {
                                    money.setText(CmmFunc.formatMoney(param.getLong("around_pay"), false));
                                    deposit.setText(CmmFunc.formatMoney(param.getLong("value"), true));
                                    serviceFee.setText(CmmFunc.formatMoney(param.getLong("fee"), true));
                                    total.setText(CmmFunc.formatMoney(param.getLong("total"), true));
                                    phone.setText(param.getString("phone"));
                                    name.setText(param.getString("fullname"));
                                    view.findViewById(R.id.confirm).setOnClickListener(ConfirmSendGiftAroundPayFragment.this);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActionGetAroundPayment().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                try {
                    new ActionSend().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param.getString("phone"), param.getLong("value"), getArguments().getString("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class ActionSend extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String phone = (String) params[0];
                long value = (long) params[1];
                String message = (String) params[2];
                String response = APIHelper.sendGiftAroundPay(phone, value, message);
                jsonObject = new JSONObject(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            //super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage(getString(R.string.success_send_gift_around_pay, CmmFunc.formatMoney(param.getLong("value"))));
                    messageDialogFragment.setCallback(new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.pop(getActivity(), SendGiftFragment.class.getName());
                        }
                    });
                    messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());

                } else {
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setOutToHide(true);
                    messageDialogFragment.setTitleOk(getString(R.string.top_up));
                    messageDialogFragment.setMessage(ErrorHelper.getValueByKey(code));
                    messageDialogFragment.setCallback(new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, AroundWalletFragment.newInstance());
                        }
                    });
                    messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }

    class ActionGetAroundPayment extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getAroundPayment();
                JSONObject jsonObject = new JSONObject(retValue);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    long aroundPay = data.getLong("around_pay");
                    money.setText(CmmFunc.formatMoney(aroundPay, false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
