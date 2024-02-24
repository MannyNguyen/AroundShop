package vn.nip.around.Fragment.SendGift;


import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import vn.nip.around.Bean.BeanSendGift;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.CircleTransform;
import vn.nip.around.Custom.CustomMoneyEditText;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendGiftAroundPayFragment extends BaseFragment implements View.OnClickListener {

    BeanSendGift param;
    CustomMoneyEditText money;
    EditText message;
    Drawable icError;
    TextView errorMessage;

    public SendGiftAroundPayFragment() {
        // Required empty public constructor
    }

    public static SendGiftAroundPayFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        SendGiftAroundPayFragment fragment = new SendGiftAroundPayFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_send_gift_around_pay, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        param = (BeanSendGift) CmmFunc.tryParseJson(getArguments().getString("data"), BeanSendGift.class);
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
                    final ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
                    final TextView name = (TextView) view.findViewById(R.id.name);
                    money = (CustomMoneyEditText) view.findViewById(R.id.money);
                    message = (EditText) view.findViewById(R.id.message);
                    errorMessage = (TextView) view.findViewById(R.id.message_error);
                    icError = getResources().getDrawable(R.drawable.ic_error);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(getActivity()).load(param.getAvatar()).transform(new CircleTransform()).into(avatar);
                            name.setText(param.getFullname());
                            icError.setBounds(0, 0, CmmFunc.convertDpToPx(getActivity(), 16), CmmFunc.convertDpToPx(getActivity(), 16));
                            errorMessage.setText(getString(R.string.min_max_send_gift_around, CmmFunc.formatMoney(CmmVariable.minGiftAroundPay), CmmFunc.formatMoney(CmmVariable.maxGiftAroundPay)));
                            view.findViewById(R.id.confirm).setOnClickListener(SendGiftAroundPayFragment.this);

                            money.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (errorMessage.getVisibility() == View.GONE) {
                                        return;
                                    }
                                    errorMessage.setVisibility(View.GONE);
                                }
                            });
                        }
                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                long value = money.getValue();
                if (value < CmmVariable.minGiftAroundPay || value > CmmVariable.maxGiftAroundPay) {
                    //money.setCompoundDrawables(null, null, icError, null);
                    errorMessage.setVisibility(View.VISIBLE);
                    return;
                }
                errorMessage.setVisibility(View.GONE);
                new ActionCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, param.getPhone(), value, message.getText().toString().trim());
                break;
        }
    }

    class ActionCheck extends ActionAsync {
        String sms;

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
                sms = (String) params[2];
                String response = APIHelper.checkGiftAroundPay(phone, value);
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
                    String data = jsonObject.getString("data");
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, ConfirmSendGiftAroundPayFragment.newInstance(data, sms));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
}
