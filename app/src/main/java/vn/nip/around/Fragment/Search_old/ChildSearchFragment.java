package vn.nip.around.Fragment.Search_old;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildSearchFragment extends BaseFragment implements View.OnClickListener {

    public static final String PRODUCT = "PRODUCT";
    public static final String SHOP = "SHOP";


    RecyclerView trend;
    RecyclerView history;
    public List<String> histories = new ArrayList<>();
    public List<String> trends = new ArrayList<>();
    public List<BeanProduct> products = new ArrayList<>();
    public String key;
    AsyncTask actionGet;


    public ChildSearchFragment() {
        // Required empty public constructor
    }

    public static ChildSearchFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString("type", type);
        ChildSearchFragment fragment = new ChildSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_child_search, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        trend = (RecyclerView) view.findViewById(R.id.recycler_trend);
        history = (RecyclerView) view.findViewById(R.id.recycler_history);
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (!isLoaded) {
//            threadInit = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    final SearchHistoryAdapter historyAdapter = new SearchHistoryAdapter(ChildSearchFragment.this, history, histories);
//                    final SearchHistoryAdapter trendAdapter = new SearchHistoryAdapter(ChildSearchFragment.this, trend, trends);
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            history.setLayoutManager(new LinearLayoutManager(getActivity()));
//                            history.setAdapter(historyAdapter);
//                            trend.setLayoutManager(new LinearLayoutManager(getActivity()));
//                            trend.setAdapter(trendAdapter);
//                            view.findViewById(R.id.clear_history).setOnClickListener(ChildSearchFragment.this);
//                            actionGet = new ActionGet();
//                            actionGet.execute();
//
//
//                        }
//                    });
//                }
//            });
//            threadInit.start();
//            isLoaded = true;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGet != null) {
            actionGet.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_history:
                new ActionClear().execute();
                break;
        }
    }

    class ActionGet extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
//            try {
//                String response = APIHelper.getSearchInfoKeyword(getArguments().getString("type"));
//                jsonObject = new JSONObject(response);
//                int code = jsonObject.getInt("code");
//                if (code == 1) {
//                    JSONObject data = jsonObject.getJSONObject("data");
//                    JSONArray populars = data.getJSONArray("populars");
//                    for (int i = 0; i < populars.length(); i++) {
//                        String item = populars.getString(i);
//                        trends.add(item);
//                    }
//
//                    JSONArray recents = data.getJSONArray("recents");
//                    if (recents.length() > 0) {
//                        for (int i = 0; i < recents.length(); i++) {
//                            String item = recents.getString(i);
//                            histories.add(item);
//                        }
//                    } else {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                view.findViewById(R.id.search_area).setVisibility(View.GONE);
//                            }
//                        });
//                    }
//
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            trend.getAdapter().notifyDataSetChanged();
            history.getAdapter().notifyDataSetChanged();
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
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    histories.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            history.getAdapter().notifyDataSetChanged();
            hideProgress();
        }
    }

}
