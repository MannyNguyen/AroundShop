package vn.nip.around.Fragment.Payment;


import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.ActionAsync;
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

import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnePayFragment extends BaseFragment implements View.OnClickListener {

    WebView webView;

    public OnePayFragment() {
        // Required empty public constructor
    }


    public static OnePayFragment newInstance(int orderID, String type) {
        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        args.putString("type", type);
        OnePayFragment fragment = new OnePayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_one_pay, container, false);
        }
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            view.findViewById(R.id.back).setOnClickListener(OnePayFragment.this);
            webView = (WebView) view.findViewById(R.id.webview);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setSupportZoom(true);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDisplayZoomControls(false);

            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    showProgress();

                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    try {
                        Uri uri = Uri.parse(url);
                        String status = uri.getQueryParameter("around_status");
                        if (status != null) {
                            if (status.equals("done")) {
                                new ActionCheckPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("order_id"));
                            }

                        }
                        Log.d("URL_PAYMENT", url);
                    } catch (Exception e) {

                    }
                    hideProgress();

                }
            });
            showProgress();
            //get1PayUrl();
            new ActionGet1PayUrl().execute();
            isLoaded = true;
        }

    }

    class ActionGet1PayUrl extends ActionAsync {

        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("order_id", getArguments().getInt("order_id") + ""));
                params.add(new AbstractMap.SimpleEntry("type", URLEncoder.encode(getArguments().getString("type")) + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_1pay_url", params);
                jsonObject = new JSONObject(response);
                return jsonObject;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                final int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    final String url = data.getString("url");
                    webView.loadUrl(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                CustomDialog.Dialog2Button(getActivity(), "", getString(R.string.confirm_payment), getString(R.string.ok), getString(R.string.cancel), new ICallback() {
                    @Override
                    public void excute() {
                        try {
                            if (AppActivity.FLOW == AppActivity.GIFTING) {
                                CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                cartFragment.orderID = 0;
                                cartFragment.hideProgress();
                                getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                            } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                                ConfirmFragment fullOrderFragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                fullOrderFragment.orderID = 0;
                                fullOrderFragment.hideProgress();
                                getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
                break;
        }
    }

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
                int id = (int) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("order_id", id + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/check_1pay_payment", params);
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
                    try {
                        if (AppActivity.FLOW == AppActivity.GIFTING) {
                            CartFragment fragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                            MatchFragment newMatch = MatchFragment.newInstance(fragment.orderID, true, "GIFTING", "", 0, 0, 0, 0, 0);
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, newMatch);
                        } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                            ConfirmFragment fragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                            MatchFragment newMatch = MatchFragment.newInstance(fragment.orderID, true, "PICKUP", "", 0, 0, 0, 0, 0);
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, newMatch);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomDialog.showMessage(getActivity(), "", ErrorHelper.getValueByKey(code), new ICallback() {
                        @Override
                        public void excute() {
                            if (AppActivity.FLOW == AppActivity.PICKUP) {
                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                            } else if (AppActivity.FLOW == AppActivity.GIFTING) {
                                GlobalClass.getActivity().getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                            }
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
