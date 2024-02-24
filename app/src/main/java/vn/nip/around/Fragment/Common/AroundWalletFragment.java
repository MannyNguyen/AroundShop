package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.AroundPayFeeAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanAroundPay;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Dialog.AroundPayDialogFragment;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundWalletFragment extends BaseFragment implements View.OnClickListener {

    public List<BeanAroundPay> prices;
    RecyclerView recycler;
    public EditText customFee;
    Button confirm;

    public AroundWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_around_wallet, container, false);
        }
        return view;
    }

    public static AroundWalletFragment newInstance() {

        Bundle args = new Bundle();

        AroundWalletFragment fragment = new AroundWalletFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppActivity.FLOW == AppActivity.PICKUP) {
                    ConfirmFragment confirmFragment = (ConfirmFragment) CmmFunc.getFragmentByTag(getActivity(), ConfirmFragment.class.getName());
                    if (confirmFragment != null) {
                        getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                        confirmFragment.listPaymentFragment.onResume();
                    } else {
                        FragmentHelper.pop(getActivity());
                    }
                } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                    CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName());
                    if (CmmFunc.getFragmentByTag(getActivity(), CartFragment.class.getName()) != null) {
                        getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                        cartFragment.listPaymentFragment.reload();
                    } else {
                        FragmentHelper.pop(getActivity());
                    }
                } else {
                    FragmentHelper.pop(getActivity());
                }

            }
        });

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getString(R.string.around_payment));

        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        customFee = (EditText) view.findViewById(R.id.custom_fee);
        customFee.setText("");
        confirm = (Button) view.findViewById(R.id.confirm);
        confirm.setOnClickListener(AroundWalletFragment.this);
        customFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                view.findViewById(R.id.error_message).setVisibility(View.GONE);
                BeanAroundPay.reset(prices);
                recycler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        customFee.removeTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                view.findViewById(R.id.error_message).setVisibility(View.GONE);
                BeanAroundPay.reset(prices);
                recycler.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                confirm.setOnClickListener(null);
                showProgress();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            confirm.setOnClickListener(AroundWalletFragment.this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                String value = customFee.getText().toString().trim();
                if (BeanAroundPay.getSelected(prices) == null && value.equals("")) {
                    view.findViewById(R.id.error_message).setVisibility(View.VISIBLE);
                    hideProgress();
                    return;
                }

                if (!value.equals("")) {
                    try {
                        int val = Integer.parseInt(value.replaceAll(",", "").trim());
                        int min = CmmVariable.aroundPay.getInt("min_around_pay_payment");
                        int max = CmmVariable.aroundPay.getInt("max_around_pay_payment");
                        if (val > max || val < min) {
                            view.findViewById(R.id.error_message).setVisibility(View.VISIBLE);
                            hideProgress();
                            return;
                        }
                        DialogFragment aroundPay = AroundPayDialogFragment.newInstance(val);
                        aroundPay.show(GlobalClass.getActivity().getSupportFragmentManager(), aroundPay.getClass().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    int val = BeanAroundPay.getSelected(prices).getValue();
                    DialogFragment aroundPay = AroundPayDialogFragment.newInstance(val);
                    aroundPay.show(GlobalClass.getActivity().getSupportFragmentManager(), aroundPay.getClass().getName());
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActionGetAroundPay().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }



    class ActionGetAroundPay extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... voids) {
            try {
                String response = APIHelper.getAroundPayment();
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetPayment", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    long aroundPay = data.getLong("around_pay");
                    if (aroundPay == 0) {
                        view.findViewById(R.id.container_1).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.container_2).setVisibility(View.GONE);
                        view.findViewById(R.id.message_no_around_pay).setVisibility(View.VISIBLE);
                    } else {
                        view.findViewById(R.id.container_1).setVisibility(View.GONE);
                        view.findViewById(R.id.container_2).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.message_no_around_pay).setVisibility(View.GONE);
                        final TextView totalFee = (TextView) view.findViewById(R.id.total_fee);
                        totalFee.setText(CmmFunc.formatMoney(aroundPay, false));
                    }
                    prices = new ArrayList<>();
                    JSONArray arr = new JSONArray(CmmVariable.aroundPay.getString("prices"));
                    for (int i = 0; i < arr.length(); i++) {
                        BeanAroundPay bean = new BeanAroundPay();
                        if (i == 0) {
                            bean.setSelected(true);
                        }
                        bean.setValue(arr.getInt(i));
                        prices.add(bean);
                    }
                    //List<Integer> prices = new ArrayList<Integer>("")
                    if (prices.size() > 0) {
                        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
                        AroundPayFeeAdapter adapter = new AroundPayFeeAdapter(getActivity(), AroundWalletFragment.this, recycler, prices);
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                    }
                }
            } catch (Exception e) {

            } finally {
                hideProgress();
            }
        }
    }
}
