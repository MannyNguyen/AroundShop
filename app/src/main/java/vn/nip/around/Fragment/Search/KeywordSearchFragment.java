package vn.nip.around.Fragment.Search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.SearchHistoryAdapter;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeywordSearchFragment extends BaseFragment implements View.OnClickListener {

    private final String PRODUCT = "PRODUCT";
    private final String SHOP = "SHOP";


    NestedScrollView scrollView;
    RecyclerView recyclerTrend;
    RecyclerView recyclerHistory;

    ActionGetRecent actionGetRecent;

    public KeywordSearchFragment() {
        // Required empty public constructor
    }

    public static KeywordSearchFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString("type", type);
        KeywordSearchFragment fragment = new KeywordSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_keyword_search, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        recyclerTrend = (RecyclerView) view.findViewById(R.id.recycler_trend);
        recyclerTrend.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerHistory = (RecyclerView) view.findViewById(R.id.recycler_history);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        view.findViewById(R.id.clear_history).setOnClickListener(KeywordSearchFragment.this);
        actionGetRecent = new ActionGetRecent();
        actionGetRecent.execute();
        isLoaded = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGetRecent != null) {
            actionGetRecent.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_history:
                ActionClear actionClear = new ActionClear();
                actionClear.execute();
                break;
        }
    }

    class ActionGetRecent extends ActionAsync {
        List<Object> trends = new ArrayList<>();
        List<Object> histories = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {

                String response = APIHelper.getSearchInfoKeyword(getArguments().getString("type"), 0);
                jsonObject = new JSONObject(response);
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
            SearchHistoryAdapter trendAdapter = new SearchHistoryAdapter(KeywordSearchFragment.this, recyclerTrend, trends);
            recyclerTrend.setAdapter(trendAdapter);
            SearchHistoryAdapter recentAdapter = new SearchHistoryAdapter(KeywordSearchFragment.this, recyclerHistory, histories);
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
                    SearchHistoryAdapter recentAdapter = new SearchHistoryAdapter(KeywordSearchFragment.this, recyclerHistory, new ArrayList<>());
                    recyclerHistory.setAdapter(recentAdapter);
                    view.findViewById(R.id.search_title).setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            hideProgress();
        }
    }
}
