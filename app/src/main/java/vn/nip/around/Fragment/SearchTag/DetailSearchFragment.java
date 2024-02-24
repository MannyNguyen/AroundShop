package vn.nip.around.Fragment.SearchTag;


import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Adapter.SearchHistoryAdapter;
import vn.nip.around.Adapter.ShopSearchAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Bean.BeanSearchShop;
import vn.nip.around.Bean.BeanShop;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.HomeFragment;
import vn.nip.around.Fragment.Gift.GiftingHomeFragment;
import vn.nip.around.Fragment.Gift.TabGiftingFragment;
import vn.nip.around.Fragment.Giftme.ShoppingFragment;
import vn.nip.around.Fragment.Product.ShopProductFragment;
import vn.nip.around.Fragment.Shopping.HomeShoppingFragment;
import vn.nip.around.Fragment.Shopping.TabShoppingFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailSearchFragment extends BaseFragment implements View.OnClickListener, View.OnKeyListener {

    public final String PRODUCT = "PRODUCT";
    public final String SHOP = "SHOP";
    public final String PRODUCT_IN_SHOP = "PRODUCT_IN_SHOP";
    public final String PRODUCT_IN_CATEGORY = "PRODUCT_IN_CATEGORY";
    public final String PRODUCT_IN_CATEGORY_SHOP = "PRODUCT_IN_CATEGORY_SHOP";

    TextView hashTagContent, messageEmpty;
    View hashTagContainer, hashTagRemove;
    public EditText searchContent;
    RecyclerView recyclerTrend, recyclerHistory;
    NestedScrollView scrollView;

    JSONObject category, shop;
    int page = 1;

    ActionSearch actionSearch;
    ActionGetRecent actionGetRecent;
    ActionClear actionClear;


    RecyclerView recycler;
    LinearLayoutManager layoutManager;
    List items = new ArrayList();
    RecyclerView.OnScrollListener onScrollListener;

    String keyword;

    public DetailSearchFragment() {
        // Required empty public constructor
    }

    public static DetailSearchFragment newInstance(String type, String keyword, String category, String shop) {

        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("keyword", keyword);
        args.putString("category", category);
        args.putString("shop", shop);
        DetailSearchFragment fragment = new DetailSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_detail_search2, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        keyword = getArguments().getString("keyword");
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getArguments().getString("category") != null) {
                        category = new JSONObject(getArguments().getString("category"));
                    }

                    if (getArguments().getString("shop") != null) {
                        shop = new JSONObject(getArguments().getString("shop"));
                    }
                    searchContent = (EditText) view.findViewById(R.id.search_content_detail);
                    hashTagContainer = view.findViewById(R.id.hash_tag_container);
                    hashTagContent = (TextView) view.findViewById(R.id.hash_tag_content);
                    hashTagRemove = view.findViewById(R.id.hash_tag_remove);
                    messageEmpty = (TextView) view.findViewById(R.id.message_empty);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    scrollView = (NestedScrollView) view.findViewById(R.id.detail_scroll_view);
                    recyclerTrend = (RecyclerView) view.findViewById(R.id.detail_recycler_trend);
                    recyclerHistory = (RecyclerView) view.findViewById(R.id.detail_recycler_history);
                    view.findViewById(R.id.clear_history).setOnClickListener(DetailSearchFragment.this);
                    view.findViewById(R.id.detail_cancel).setOnClickListener(DetailSearchFragment.this);
                    layoutManager = new LinearLayoutManager(getActivity());

                    onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (layoutManager.findLastCompletelyVisibleItemPosition() == items.size() - 1 && items.size() >= 5) {
                                try {
                                    if (actionSearch == null) {
                                        return;
                                    }

                                    if (actionSearch.getStatus() == AsyncTask.Status.FINISHED) {
                                        actionSearch = new ActionSearch();
                                        actionSearch.execute(keyword);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    };


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                hashTagContainer.setVisibility(View.GONE);
                                searchContent.setText(getArguments().getString("keyword"));

                                if (category != null) {
                                    searchContent.setHint(getString(R.string.searchin) + " " + category.getString(getString(R.string.key_name)));
                                } else{
                                    if(shop!=null){
                                        searchContent.setHint(getString(R.string.searchin) + " " + shop.getString("name"));
                                    }
                                }
                                if (shop != null) {
                                    try {
                                        hashTagContainer.setVisibility(View.VISIBLE);
                                        hashTagContent.setText(shop.getString("name"));
                                        hashTagRemove.setOnClickListener(DetailSearchFragment.this);
                                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) hashTagContainer.getLayoutParams();
                                        layoutParams.width = AppActivity.WINDOW_WIDTH / 3;
                                        hashTagContainer.setLayoutParams(layoutParams);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                searchContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        messageEmpty.setVisibility(View.GONE);
                                        if (hasFocus) {
                                            String type = getArguments().getString("type");

                                            view.findViewById(R.id.detail_cancel).setVisibility(View.VISIBLE);
                                            if (!getArguments().getString("type").equals(SHOP) && !getArguments().getString("type").equals(PRODUCT)) {
                                                recycler.setVisibility(View.GONE);
                                                scrollView.setVisibility(View.GONE);
                                                return;
                                            }
                                            scrollView.setVisibility(View.VISIBLE);
                                            recycler.setVisibility(View.GONE);
                                        } else {
                                            view.findViewById(R.id.detail_cancel).setVisibility(View.GONE);
                                            recycler.setVisibility(View.VISIBLE);
                                            scrollView.setVisibility(View.GONE);
                                        }
                                    }
                                });

                                recycler.setLayoutManager(layoutManager);
                                searchContent.setOnKeyListener(DetailSearchFragment.this);

                                recyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerTrend.setLayoutManager(new LinearLayoutManager(getActivity()));
                                actionSearch = new ActionSearch();
                                actionSearch.execute(getArguments().getString("keyword"));
                                actionGetRecent = new ActionGetRecent();
                                actionGetRecent.execute();

                                if (getArguments().getString("type").equals(PRODUCT_IN_CATEGORY) || getArguments().getString("type").equals(PRODUCT)) {
                                    view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            for (int i = getActivity().getSupportFragmentManager().getFragments().size() - 1; i >= 0; i--) {
                                                Fragment fragment = getActivity().getSupportFragmentManager().getFragments().get(i);
                                                if (fragment instanceof TabShoppingFragment) {
                                                    FragmentHelper.pop(getActivity(), TabShoppingFragment.class.getName());
                                                    return;
                                                }

                                                if (fragment instanceof TabGiftingFragment) {
                                                    FragmentHelper.pop(getActivity(), TabGiftingFragment.class.getName());
                                                    return;
                                                }
                                                if (fragment instanceof HomeShoppingFragment) {
                                                    FragmentHelper.pop(getActivity(), HomeShoppingFragment.class.getName());
                                                    return;
                                                }
                                                if (fragment instanceof GiftingHomeFragment) {
                                                    FragmentHelper.pop(getActivity(), GiftingHomeFragment.class.getName());
                                                    return;
                                                }


                                            }
                                            AppActivity.popRoot();
                                            return;
                                        }
                                    });
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionSearch != null) {
            actionSearch.cancel(true);
        }
        if (actionGetRecent != null) {
            actionGetRecent.cancel(true);
        }
        if (actionClear != null) {
            actionClear.cancel(true);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hash_tag_remove:

                hashTagContainer.setVisibility(View.GONE);
                searchContent.setHint(getString(R.string.hint_search_home));
//                page = 1;
//                items.clear();
//                recycler.getAdapter().notifyDataSetChanged();
//                if (actionSearch != null) {
//                    actionSearch.cancel(true);
//                }
//                actionSearch = new ActionSearch();
//                actionSearch.execute(searchContent.getText().toString().trim());
                searchContent.requestFocus();
                CmmFunc.hideKeyboard(getActivity());
                break;
            case R.id.clear_history:
                actionClear = new ActionClear();
                actionClear.execute();
                break;
            case R.id.detail_cancel:
                try {

                    if (shop != null) {
                        hashTagContainer.setVisibility(View.VISIBLE);
                        hashTagContent.setText(shop.getString("name"));
                        hashTagRemove.setOnClickListener(DetailSearchFragment.this);
                        if(category == null){
                            searchContent.setHint(getString(R.string.searchin) + " " + shop.getString("name"));
                        }
                    }
                    if(category != null){
                        searchContent.setHint(getString(R.string.searchin) + " " + category.getString(getString(R.string.key_name)));
                    }
                    searchContent.clearFocus();
                    searchContent.setText(keyword);
                    CmmFunc.hideKeyboard(getActivity());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.search_content_detail:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (hashTagContainer.getVisibility() == View.GONE) {
                            shop = null;
                            getArguments().putString("shop", null);
                        }

                        String str = searchContent.getText().toString().trim();
                        if (str.equals(StringUtils.EMPTY)) {
                            searchContent.requestFocus();
                            return true;
                        }
                        resetListContent();
                        return true;
                    }
                }
        }
        return false;
    }

    public void resetListContent() {
        try {
            recycler.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            keyword = searchContent.getText().toString().trim();

            searchContent.clearFocus();

            if (keyword.equals(StringUtils.EMPTY)) {
                if (getArguments().getString("type").equals(SHOP)) {
                    scrollView.setVisibility(View.VISIBLE);
                }

                recycler.setVisibility(View.GONE);
                return;
            }
            page = 1;
            items.clear();
            if (actionSearch != null) {
                actionSearch.cancel(true);
            }
            actionSearch = new ActionSearch();
            actionSearch.execute(keyword);
            CmmFunc.hideKeyboard(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ActionSearch extends ActionAsync {
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
                String keyword = (String) params[0];
                type = SHOP;
                int idCate = 0;
                int idShop = 0;
                if (getArguments().getString("type").equals(PRODUCT)) {
                    if (category == null && shop == null) {
                        type = PRODUCT;
                    } else {
                        if (category == null) {
                            type = PRODUCT_IN_SHOP;
                            idShop = shop.getInt("id");
                        } else if (shop == null) {
                            type = PRODUCT_IN_CATEGORY;
                            idCate = category.getInt("id");
                        } else {
                            type = PRODUCT_IN_CATEGORY_SHOP;
                            idShop = shop.getInt("id");
                            idCate = category.getInt("id");
                        }
                    }
                } else {
                    if (category != null) {
                        idCate = category.getInt("id");
                    }
                }
                String response = APIHelper.searchInfo(type, keyword, page, idShop, idCate);
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (type.equals(SHOP)) {
                        for (int i = 0; i < data.length(); i++) {
                            items.add(CmmFunc.tryParseJson(data.getString(i), BeanSearchShop.class));
                        }

                    } else {
                        for (int i = 0; i < data.length(); i++) {
                            items.add(CmmFunc.tryParseJson(data.getString(i), BeanProduct.class));
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
            try {
                int code = jsonObject.getInt("code");
                if (code != 1) {
                    return;
                }

                if (items.size() == 0) {
                    messageEmpty.setVisibility(View.VISIBLE);
                    recycler.setVisibility(View.GONE);
                } else {
                    messageEmpty.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                }
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() > 0) {
                    if (page == 1) {
                        recycler.addOnScrollListener(onScrollListener);
                    }

                    if (recycler.getAdapter() != null) {
                        recycler.getAdapter().notifyDataSetChanged();
                    } else {
                        if (type.equals(SHOP)) {
                            ShopSearchAdapter shopSearchAdapter = new ShopSearchAdapter(DetailSearchFragment.this, recycler, items);
                            recycler.setAdapter(shopSearchAdapter);
                        } else {
                            ProductAdapter productAdapter = new ProductAdapter(DetailSearchFragment.this, recycler, items);
                            recycler.setAdapter(productAdapter);
                        }

                    }

                    page++;
                } else {
                    recycler.removeOnScrollListener(onScrollListener);
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }


    class ActionGetRecent extends ActionAsync {
        List<Object> trends = new ArrayList<>();
        List<Object> histories = new ArrayList<>();
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
                type = getArguments().getString("type"); //only SHOP
                if (category != null) {
                    idCategory = category.getInt("id");
                }
                String response = APIHelper.getSearchInfoKeyword(type, idCategory);
                jsonObject = new JSONObject(response);
                if (!type.equals(SHOP) && !type.equals(PRODUCT)) {
                    return jsonObject;
                }
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
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
            SearchHistoryAdapter trendAdapter = new SearchHistoryAdapter(DetailSearchFragment.this, recyclerTrend, trends);
            recyclerTrend.setAdapter(trendAdapter);
            SearchHistoryAdapter recentAdapter = new SearchHistoryAdapter(DetailSearchFragment.this, recyclerHistory, histories);
            recyclerHistory.setAdapter(recentAdapter);
            view.findViewById(R.id.search_title).setVisibility(View.GONE);
            if (histories.size() > 0) {
                view.findViewById(R.id.search_title).setVisibility(View.VISIBLE);
            }
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
                    SearchHistoryAdapter recentAdapter = new SearchHistoryAdapter(DetailSearchFragment.this, recyclerHistory, new ArrayList<>());
                    recyclerHistory.setAdapter(recentAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideProgress();
        }
    }
}
