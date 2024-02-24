package vn.nip.around.Fragment.Search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.SearchHistoryAdapter;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.SearchTag.SearchFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AutoCompleteSearchFragment extends BaseFragment {

    private final String CATEGORY_SHOP = "CATEGORY_SHOP";

    private final String PRODUCT_LIVE = "PRODUCT_LIVE";
    private final String SHOP_LIVE = "SHOP_LIVE";
    private final String CATEGORY_SHOP_LIVE = "CATEGORY_SHOP_LIVE";

    String keyword;
    String type;
    JSONObject category;
    RecyclerView recycler;

    ActionGetRecent actionGetRecent;
    ActionSearch actionSearch;

    public AutoCompleteSearchFragment() {
        // Required empty public constructor
    }

    public static AutoCompleteSearchFragment newInstance(String keyword, String type) {

        Bundle args = new Bundle();
        args.putString("keyword", keyword);
        args.putString("type", type);
        AutoCompleteSearchFragment fragment = new AutoCompleteSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AutoCompleteSearchFragment newInstance(String keyword, String type, String category) {

        Bundle args = new Bundle();
        args.putString("keyword", keyword);
        args.putString("type", type);
        args.putString("category", category);
        AutoCompleteSearchFragment fragment = new AutoCompleteSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_auto_complete_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            keyword = getArguments().getString("keyword");
            type = getArguments().getString("type");
            category = new JSONObject(getArguments().getString("category"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }

        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        switch (type) {
            case CATEGORY_SHOP:
                view.findViewById(R.id.suppliers_in_category).setVisibility(View.VISIBLE);
                actionGetRecent = new ActionGetRecent();
                actionGetRecent.execute();
                break;
            case CATEGORY_SHOP_LIVE:
                view.findViewById(R.id.suppliers_in_category).setVisibility(View.VISIBLE);
            case SHOP_LIVE:
            case PRODUCT_LIVE:
                actionSearch = new ActionSearch();
                actionSearch.execute();
                break;

        }

        isLoaded = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGetRecent != null) {
            actionGetRecent.cancel(true);
        }
    }

    class ActionGetRecent extends ActionAsync {
        List<Object> shops = new ArrayList<>();

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int idCategory = 0;
                if (category != null) {
                    idCategory = category.getInt("id");
                }
                String response = APIHelper.getSearchInfoKeyword(type, idCategory);
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray arr = data.getJSONArray("shops");
                    if (arr.length() > 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            shops.add(arr.getJSONObject(i));
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
            SearchHistoryAdapter adapter = new SearchHistoryAdapter(AutoCompleteSearchFragment.this, recycler, shops);
            recycler.setAdapter(adapter);
        }
    }


    class ActionSearch extends ActionAsync {
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
                if (category != null) {
                    idCategory = category.getInt("id");
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
            SearchHistoryAdapter adapter = new SearchHistoryAdapter(AutoCompleteSearchFragment.this, recycler, items);
            recycler.setAdapter(adapter);
            hideProgress();
        }
    }

}
