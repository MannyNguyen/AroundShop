package vn.nip.around.Fragment.Search_old;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Adapter.ShopSearchAdapter;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Bean.BeanSearchShop;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailSearchFragment extends BaseFragment implements View.OnKeyListener, View.OnClickListener {

    public static final String PRODUCT = "PRODUCT";
    public static final String SHOP = "SHOP";

    public TextView searchContent;
    RecyclerView recycler;
    LinearLayoutManager layoutManager;

    AsyncTask actionGet;
    int page = 1;

    List<BeanProduct> products = new ArrayList<>();
    List<BeanSearchShop> shops = new ArrayList<>();

    RecyclerView.OnScrollListener onScrollListener;

    public DetailSearchFragment() {
        // Required empty public constructor
    }

    public static DetailSearchFragment newInstance(String type, String key) {

        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("key", key);
        DetailSearchFragment fragment = new DetailSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_detail_search, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        searchContent = (TextView) view.findViewById(R.id.search_content);

    }

    @Override
    public void onResume() {
        super.onResume();
        searchContent.setText(getArguments().getString("key"));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    layoutManager = new LinearLayoutManager(getActivity());
                    RecyclerView.Adapter adapter = null;
                    if (getArguments().getString("type").equals(PRODUCT)) {
                        adapter = new ProductAdapter(DetailSearchFragment.this, recycler, products, ProductAdapter.LIST);
                    } else if (getArguments().getString("type").equals(SHOP)) {
                        adapter = new ShopSearchAdapter(DetailSearchFragment.this, recycler, shops);
                    }
                    final RecyclerView.Adapter finalAdapter = adapter;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            recycler.setLayoutManager(layoutManager);
                            recycler.setAdapter(finalAdapter);
                            searchContent.setOnKeyListener(DetailSearchFragment.this);
                            searchContent.setOnClickListener(DetailSearchFragment.this);
                            onScrollListener = new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
                                    if (layoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() >= 5) {
                                        if (actionGet == null) {
                                            return;
                                        }
                                        if (actionGet.getStatus() == AsyncTask.Status.FINISHED) {
                                            page++;
                                            actionGet = new ActionGet();
                                            actionGet.execute(getArguments().getString("key"));
                                        }

                                    }
                                }
                            };
                            recycler.addOnScrollListener(onScrollListener);

                            actionGet = new ActionGet();
                            actionGet.execute(getArguments().getString("key"));
                        }
                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionGet != null) {
            actionGet.cancel(true);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        switch (v.getId()) {
//            case R.id.search_content:
//                if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        CmmFunc.hideKeyboard(getActivity());
//                        String value = searchContent.getText().toString().trim();
//                        if (value.equals(StringUtils.EMPTY) || value.equals(getArguments().getString("key"))) {
//                            return false;
//                        }
//
//                        FragmentHelper.addFragment(getActivity(), R.id.home_content, DetailSearchFragment.newInstance(getArguments().getString("type"), value));
//                        return true;
//                    }
//                }
//        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_content:
                FragmentHelper.pop(getActivity());
                break;
        }
    }

    class ActionGet extends ActionAsync {
        @Override
        protected void onPreExecute() {
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String type = getArguments().getString("type");
                String key = (String) params[0];
                String response = APIHelper.searchInfo(getArguments().getString("type"), key, page);
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    if (type.equals(SHOP)) {
                        List<BeanSearchShop> items = (List<BeanSearchShop>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanSearchShop.class);
                        if (items.size() == 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recycler.removeOnScrollListener(onScrollListener);
                                    if (page == 1) {
                                        recycler.setVisibility(View.GONE);
                                        view.findViewById(R.id.no_product_message).setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                        }
                        for (BeanSearchShop item : items) {
                            shops.add(item);
                        }
                    } else if (type.equals(PRODUCT)) {
                        List<BeanProduct> items = (List<BeanProduct>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanProduct.class);

                        if (items.size() == 0) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    recycler.removeOnScrollListener(onScrollListener);
                                    if (page == 1) {
                                        recycler.setVisibility(View.GONE);
                                        view.findViewById(R.id.no_product_message).setVisibility(View.VISIBLE);
                                    }
                                }
                            });

                        }
                        for (BeanProduct item : items) {
                            products.add(item);
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
            recycler.getAdapter().notifyDataSetChanged();
            hideProgress();
        }
    }


}
