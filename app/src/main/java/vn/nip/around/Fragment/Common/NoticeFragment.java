package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends BaseFragment {

    WebView webView;

    public NoticeFragment() {
        // Required empty public constructor
    }

    public static NoticeFragment newInstance(int id, String title) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("title", title);
        NoticeFragment fragment = new NoticeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(getArguments().getString("title"));

        webView = (WebView) view.findViewById(R.id.webview);
//        webView.getSettings().setLoadWithOverviewMode(true);
//        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setSupportZoom(false);
//        webView.getSettings().setBuiltInZoomControls(false);
//        webView.getSettings().setDisplayZoomControls(false);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        webView.getSettings().setDefaultFontSize(CmmFunc.convertDpToPx(getActivity(), 24));
        new ActionGetDetail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    class ActionGetDetail extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String value = APIHelper.getNotificationDetail(getArguments().getInt("id"));
                jsonObject = new JSONObject(value);
            } catch (JSONException e) {
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
                    String detail64 = jsonObject.getJSONObject("data").getString(getString(R.string.server_key_detail));
                    String detail = new String(Base64.decode(detail64, 0));
                    final String mimeType = "text/html";
                    final String encoding = "UTF-8";
                    webView.setAlpha(0);
                    webView.setScaleX(0.9f);
                    webView.setScaleY(0.9f);
                    webView.loadDataWithBaseURL("", detail, mimeType, encoding, "");
                    webView.animate().alpha(1).scaleX(1).scaleY(1).setDuration(400).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }
}
