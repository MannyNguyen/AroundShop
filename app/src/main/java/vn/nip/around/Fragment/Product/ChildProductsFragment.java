package vn.nip.around.Fragment.Product;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildProductsFragment extends ProductBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public String tab;
    int page = 1;
    SwipeRefreshLayout refresh;
    public ProductAdapter adapter;
    int id;
    public AsyncTask actionGet;

    public ChildProductsFragment() {
        // Required empty public constructor
    }

    public static ChildProductsFragment newInstance(String tab, int id) {

        Bundle args = new Bundle();
        args.putString("tab", tab);
        args.putInt("id", id);
        ChildProductsFragment fragment = new ChildProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_child_products, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab = getArguments().getString("tab");
        id = getArguments().getInt("id");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
                        recycler = (RecyclerView) view.findViewById(R.id.recycler);
                        adapter = new ProductAdapter(ChildProductsFragment.this, recycler, products);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh.setOnRefreshListener(ChildProductsFragment.this);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                    layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
                                }
                                recycler.setLayoutManager(layoutManager);
                                recycler.setAdapter(adapter);

                                final LinearLayoutManager finalLayoutManager = layoutManager;
                                recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() >= 5) {
                                            if (actionGet == null || !actionGet.isCancelled()) {
                                                return;
                                            }
                                            page++;
                                            actionGet = new ActionGetProduct();
                                            actionGet.execute();
                                        }
                                    }
                                });
                                actionGet = new ActionGetProduct();
                                actionGet.execute();
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
    }

    @Override
    public void onRefresh() {
        try {
            tab = getArguments().getString("tab");
            products.clear();
            adapter.notifyDataSetChanged();
            if (actionGet != null) {
                actionGet.cancel(true);
            }
            page = 1;
            actionGet = new ActionGetProduct();
            actionGet.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    class ActionGetProduct extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refresh.setRefreshing(true);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getProducts(id, page, tab);
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    String type = jsonObject.getString("type");
                    if (type.equals("product")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(data.getString(i), BeanProduct.class);
                            products.add(bean);
                        }
                    }
                }
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    adapter.notifyDataSetChanged();
                    this.cancel(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                refresh.setRefreshing(false);

            }
        }

    }

}
