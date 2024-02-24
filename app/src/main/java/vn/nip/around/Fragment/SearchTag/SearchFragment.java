package vn.nip.around.Fragment.SearchTag;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.SearchHistoryAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener, View.OnKeyListener {
    public final String PRODUCT = "PRODUCT";
    public final String SHOP = "SHOP";
    //Khi vào từ 1 category
    private final String CATEGORY_SHOP = "CATEGORY_SHOP";

    private final String PRODUCT_LIVE = "PRODUCT_LIVE";
    private final String SHOP_LIVE = "SHOP_LIVE";
    private final String CATEGORY_SHOP_LIVE = "CATEGORY_SHOP_LIVE";
    public String TYPE = PRODUCT;

    TextView searchContent;
    Handler handler;

    TextView tabProduct, tabShop;
    FrameLayout lineSelected;


    RecyclerView recyclerTrend, recyclerHistory, recyclerAutocomplete;
    NestedScrollView scrollView;


    ActionGetRecent actionGetRecent;
    ActionSearch actionSearch;
    ActionClear actionClear;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {

        Bundle args = new Bundle();

        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchFragment newInstance(String data) {
        //id - name & vn_name
        Bundle args = new Bundle();
        args.putString("data", data);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_search2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isLoaded) {
            return;
        }

        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                searchContent = (TextView) view.findViewById(R.id.search_content);
                tabProduct = (TextView) view.findViewById(R.id.tab_product);
                tabShop = (TextView) view.findViewById(R.id.tab_shop);
                lineSelected = (FrameLayout) view.findViewById(R.id.line_selected);

                scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
                recyclerTrend = (RecyclerView) view.findViewById(R.id.recycler_trend);
                recyclerHistory = (RecyclerView) view.findViewById(R.id.recycler_history);
                recyclerAutocomplete = (RecyclerView) view.findViewById(R.id.recycler_autocomplete);

                view.findViewById(R.id.clear_history).setOnClickListener(SearchFragment.this);

                HandlerThread handlerThread = new HandlerThread(SearchFragment.this.getClass().getName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                        }
                    }
                };

                final TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(final Editable s) {
                        try {
                            if (handler != null) {
                                handler.removeCallbacksAndMessages(null);
                            }
                            // Now add a new one
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String keyword = searchContent.getText().toString();
                                                ruleSearch(keyword);
                                            }
                                        });

                                        handler.sendEmptyMessage(1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                tabProduct.setOnClickListener(SearchFragment.this);
                tabShop.setOnClickListener(SearchFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (getArguments().containsKey("data")) {
                                JSONObject data = new JSONObject(getArguments().getString("data"));
                                searchContent.setHint(getString(R.string.searchin) + " " + data.getString(getString(R.string.key_name)));
                            }
                            lineSelected.setMinimumWidth(AppActivity.WINDOW_WIDTH / 2);
                            updateTab(PRODUCT);

                            recyclerTrend.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerAutocomplete.setLayoutManager(new LinearLayoutManager(getActivity()));

                            searchContent.setOnKeyListener(SearchFragment.this);
                            searchContent.addTextChangedListener(textWatcher);

                            actionGetRecent = new ActionGetRecent();
                            actionGetRecent.execute(TYPE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGetRecent != null) {
            actionGetRecent.cancel(true);
        }
        if (actionSearch != null) {
            actionSearch.cancel(true);
        }
        if (actionClear != null) {
            actionClear.cancel(true);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (searchContent != null) {
//            searchContent.requestFocus();
//            CmmFunc.showKeyboard(getActivity());
//        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void updateTab(String type) {
        if (type.equals(TYPE)) {
            return;
        }
        Typeface bold = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
        Typeface regular = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Regular) + ".ttf");
        switch (type) {
            case PRODUCT:
                view.findViewById(R.id.suppliers_in_category).setVisibility(View.GONE);
                tabShop.setTextColor(getResources().getColor(R.color.gray_900));
                tabShop.setTypeface(regular);
                tabProduct.setTextColor(getResources().getColor(R.color.main));
                tabProduct.setTypeface(bold);
                lineSelected.animate().translationX(0).setDuration(300).start();
                break;

            case CATEGORY_SHOP:
                view.findViewById(R.id.suppliers_in_category).setVisibility(View.VISIBLE);
                tabProduct.setTextColor(getResources().getColor(R.color.gray_900));
                tabProduct.setTypeface(regular);
                tabShop.setTextColor(getResources().getColor(R.color.main));
                tabShop.setTypeface(bold);
                lineSelected.animate().translationX(AppActivity.WINDOW_WIDTH / 2).setDuration(300).start();
                break;
            case SHOP:
                view.findViewById(R.id.suppliers_in_category).setVisibility(View.GONE);
                tabProduct.setTextColor(getResources().getColor(R.color.gray_900));
                tabProduct.setTypeface(regular);
                tabShop.setTextColor(getResources().getColor(R.color.main));
                tabShop.setTypeface(bold);
                lineSelected.animate().translationX(AppActivity.WINDOW_WIDTH / 2).setDuration(300).start();
                break;
        }

        TYPE = type;
        String content = searchContent.getText().toString();
        ruleSearch(content);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_product:
                updateTab(PRODUCT);
                break;
            case R.id.tab_shop:
                if (getArguments().containsKey("data")) {
                    updateTab(CATEGORY_SHOP);
                } else {
                    updateTab(SHOP);
                }
                break;
            case R.id.clear_history:
                actionClear = new ActionClear();
                actionClear.execute();
                break;
        }
    }

    private void ruleSearch(String content) {
        if (content.isEmpty()) {
            try {
                actionGetRecent = new ActionGetRecent();
                if (getArguments().containsKey("data")) {
                    JSONObject data = new JSONObject(getArguments().getString("data"));
                    actionGetRecent.execute(TYPE, data.getInt("id"));
                } else {
                    actionGetRecent.execute(TYPE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                actionSearch = new ActionSearch();
                String t = StringUtils.EMPTY;
                switch (TYPE) {
                    case PRODUCT:
                        t = PRODUCT_LIVE;
                        break;
                    case SHOP:
                        t = SHOP_LIVE;
                        break;
                    case CATEGORY_SHOP:
                        t = CATEGORY_SHOP_LIVE;
                        break;
                }
                if (getArguments().containsKey("data")) {
                    //Nếu tìm trong category thì ở tab product không hiển thị search live
                    if (t == PRODUCT_LIVE) {
                        scrollView.setVisibility(View.GONE);
                        recyclerAutocomplete.setVisibility(View.GONE);
                        return;
                    }
                    JSONObject data = new JSONObject(getArguments().getString("data"));
                    actionSearch.execute(t, content, data.getInt("id"));
                } else {
                    actionSearch.execute(t, content, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.search_content:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String value = searchContent.getText().toString().trim();
                        if (value.equals(StringUtils.EMPTY)) {
                            return false;
                        }
                        searchContent.clearFocus();
                        CmmFunc.hideKeyboard(getActivity());
                        switch (TYPE) {
//                            case PRODUCT:
//                                FragmentHelper.pop(getActivity());
//                                FragmentHelper.addFragment(getActivity(), R.id.home_content, vn.nip.around.Fragment.SearchTag.DetailSearchFragment.newInstance(TYPE, value, getArguments().getString("data"), null));
//                                break;
                            default:
                                FragmentHelper.addFragment(getActivity(), R.id.home_content, vn.nip.around.Fragment.SearchTag.DetailSearchFragment.newInstance(TYPE, value, getArguments().getString("data"), null));
                                break;
                        }
                        return true;
                    }
                }
        }
        return false;
    }

    class ActionGetRecent extends ActionAsync {
        List<Object> trends = new ArrayList<>();
        List<Object> histories = new ArrayList<>();
        List<Object> shops = new ArrayList<>();
        String type;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int idCategory = 0;
                type = (String) params[0];
                if (params.length > 1) {
                    idCategory = (int) params[1];
                }
                String response = APIHelper.getSearchInfoKeyword(type, idCategory);
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (type.equals(CATEGORY_SHOP)) {
                        JSONArray arr = data.getJSONArray("shops");
                        if (arr.length() > 0) {
                            for (int i = 0; i < arr.length(); i++) {
                                shops.add(arr.getJSONObject(i));
                            }
                        }
                        return jsonObject;
                    }
                    JSONArray populars = data.getJSONArray("populars");
                    for (int i = 0; i < populars.length(); i++) {
                        String item = populars.getString(i);
                        trends.add(item);
                    }

                    JSONArray recents = data.getJSONArray("recents");
                    if (recents.length() > 0) {
                        for (int i = 0; i < recents.length(); i++) {
                            String item = recents.getString(i);
                            histories.add(item);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            recyclerAutocomplete.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            if (type.equals(CATEGORY_SHOP)) {
                recyclerAutocomplete.setVisibility(View.VISIBLE);
                SearchHistoryAdapter adapter = new SearchHistoryAdapter(SearchFragment.this, recyclerAutocomplete, shops);
                recyclerAutocomplete.setAdapter(adapter);
            } else {
                scrollView.setVisibility(View.VISIBLE);
                SearchHistoryAdapter trendAdapter = new SearchHistoryAdapter(SearchFragment.this, recyclerTrend, trends);
                recyclerTrend.setAdapter(trendAdapter);
                SearchHistoryAdapter recentAdapter = new SearchHistoryAdapter(SearchFragment.this, recyclerHistory, histories);
                recyclerHistory.setAdapter(recentAdapter);
                view.findViewById(R.id.search_title).setVisibility(View.GONE);
                if (histories.size() > 0) {
                    view.findViewById(R.id.search_title).setVisibility(View.VISIBLE);
                }
            }
            hideProgress();
        }
    }

    class ActionSearch extends ActionAsync {
        String type;
        List<Object> items = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int idCategory = 0;
                type = (String) params[0];
                String keyword = (String) params[1];
                if (params.length > 2) {
                    idCategory = (int) params[2];
                }
                String response = APIHelper.searchInfo(type, keyword, 1, 0, idCategory);
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {

                    switch (type) {
                        case PRODUCT_LIVE:
                            JSONArray data = jsonObject.getJSONArray("data");
                            if (data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    items.add(data.getString(i));
                                }
                            }
                            break;
                        case SHOP_LIVE:
                            JSONArray shops = jsonObject.getJSONArray("shops");
                            if (shops.length() > 0) {
                                for (int i = 0; i < shops.length(); i++) {
                                    items.add(shops.getJSONObject(i));
                                }
                            }
                            break;
                        case CATEGORY_SHOP_LIVE:
                            JSONArray data1 = jsonObject.getJSONArray("data");
                            if (data1.length() > 0) {
                                for (int i = 0; i < data1.length(); i++) {
                                    items.add(data1.getJSONObject(i));
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (scrollView.getVisibility() != View.GONE) {
                scrollView.setVisibility(View.GONE);
            }
            if (recyclerAutocomplete.getVisibility() != View.VISIBLE) {
                recyclerAutocomplete.setVisibility(View.VISIBLE);
            }
            SearchHistoryAdapter adapter = new SearchHistoryAdapter(SearchFragment.this, recyclerAutocomplete, items);
            recyclerAutocomplete.setAdapter(adapter);

            hideProgress();
        }
    }

    class ActionClear extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String response = APIHelper.clearSearchHistory(getArguments().getString("type"));
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
                    SearchHistoryAdapter recentAdapter = new SearchHistoryAdapter(SearchFragment.this, recyclerHistory, new ArrayList<>());
                    recyclerHistory.setAdapter(recentAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideProgress();
        }
    }
}
