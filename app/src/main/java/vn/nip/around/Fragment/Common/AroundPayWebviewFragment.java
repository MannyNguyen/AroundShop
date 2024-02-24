package vn.nip.around.Fragment.Common;


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

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AroundPayWebviewFragment extends BaseFragment implements View.OnClickListener {

    WebView webView;
    String tranRef;

    public AroundPayWebviewFragment() {
        // Required empty public constructor
    }

    public static AroundPayWebviewFragment newInstance(String type, int fee) {
        Bundle args = new Bundle();
        args.putInt("fee", fee);
        args.putString("type", type);
        AroundPayWebviewFragment fragment = new AroundPayWebviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_around_pay_webview, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            getView().findViewById(R.id.back).setOnClickListener(AroundPayWebviewFragment.this);
            webView = (WebView) getView().findViewById(R.id.webview);
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
                    Log.d("AROUND", url);
                    showProgress();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    hideProgress();
                    try {
                        String id = null;
                        Uri uri = Uri.parse(url);
                        String status = uri.getQueryParameter("around_status");
                        if (status != null) {
                            if (status.equals("done")) {
                                new ActionCheckPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("order_id"));
                            }
                            if (id != null) {

                            }
                        }

                    } catch (Exception e) {

                    }

                }
            });
            showProgress();
            //get1PayUrl();
            new ActionGetAroundPayUrl().execute();
            isLoaded = true;
        }

    }

    class ActionGetAroundPayUrl extends ActionAsync {

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
                params.add(new AbstractMap.SimpleEntry("value", getArguments().getInt("fee") + ""));
                params.add(new AbstractMap.SimpleEntry("type", URLEncoder.encode(getArguments().getString("type")) + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_around_pay_url", params);
                jsonObject = new JSONObject(response);
                return jsonObject;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    final String url = data.getString("url");
                    tranRef = data.getString("trans_ref");
                    webView.loadUrl(url);
                } else {
                    CustomDialog.showMessage(getActivity(), "", ErrorHelper.getValueByKey(code), new ICallback() {
                        @Override
                        public void excute() {
                            getActivity().getSupportFragmentManager().popBackStackImmediate(AroundWalletFragment.class.getName(), 0);
                        }
                    });
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
                        FragmentHelper.pop(getActivity(), AroundWalletFragment.class.getName());
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
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("trans_ref", tranRef + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/check_around_pay_payment", params);
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
                    FragmentHelper.pop(getActivity(), AroundWalletFragment.class.getName());
                } else {
                    CustomDialog.showMessage(getActivity(), "", ErrorHelper.getValueByKey(code), new ICallback() {
                        @Override
                        public void excute() {
                            FragmentHelper.pop(getActivity(), AroundWalletFragment.class.getName());
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
