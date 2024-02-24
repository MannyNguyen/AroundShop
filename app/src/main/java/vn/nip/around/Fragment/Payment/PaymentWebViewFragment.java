package vn.nip.around.Fragment.Payment;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.MatchFragment;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
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
public class PaymentWebViewFragment extends BaseFragment {

    //region Variables
    View view;
    //endregion

    //region Contructors
    public PaymentWebViewFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_payment_webview, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(getResources().getString(R.string.payment_methods));
        init();
    }

    private void init() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                CmmFunc.setDelay(400, new ICallback() {
                    @Override
                    public void excute() {
                        try {
                            String url = getArguments().getString("data");
                            WebView webView = (WebView) view.findViewById(R.id.webview);
                            webView.loadUrl(url);
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    hideProgress();
                                    try {
                                        String id = null;
                                        Uri uri = Uri.parse(url);
                                        String status = uri.getQueryParameter("around_status");
                                        if (status != null) {
                                            if (status.equals("success")) {
                                                id = uri.getQueryParameter("id");
                                            } else if (status.equals("cancel")) {
                                                id = uri.getQueryParameter("id");
                                            }
                                            if (id != null) {
                                                new ActionCheckPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id);
                                            }
                                        }

                                    } catch (Exception e) {

                                    }
                                }

                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    super.onPageStarted(view, url, favicon);
                                    showProgress();
                                }
                            });
                        } catch (Exception e) {

                        }
                    }
                });
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    //endregion

    //region Actions
    class ActionCheckPayment extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            try {
                String id = (String) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id", id));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/check_nganluong_payment", params);
                jsonObject = new JSONObject(response);
            } catch (Exception e) {

            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {

                    if (AppActivity.FLOW == AppActivity.GIFTING) {
                        CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                        MatchFragment matchFragment = (MatchFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MatchFragment.class.getName());
                        MatchFragment newMatch = MatchFragment.newInstance(fragment.orderID, true, "GIFTING", "", matchFragment.getArguments().getInt("year"), 0, 0, 0, 0);
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, newMatch);
                    } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                        ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                        MatchFragment matchFragment = (MatchFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), MatchFragment.class.getName());
                        MatchFragment newMatch = MatchFragment.newInstance(fragment.orderID, true, "", "", matchFragment.getArguments().getInt("year"), 0, 0, 0, 0);
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, newMatch);
                    }
                } else {
                    CustomDialog.showMessage(getActivity(), "", ErrorHelper.getValueByKey(code), new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.pop(getActivity());
                        }
                    });
                }

            } catch (Exception e) {

            } finally {
                hideProgress();
            }
        }
    }
    //endregion

}
