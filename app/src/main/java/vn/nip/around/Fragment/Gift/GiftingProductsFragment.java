package vn.nip.around.Fragment.Gift;


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
import vn.nip.around.Bean.BeanMainGiftingCategory;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Product.ProductBaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiftingProductsFragment extends ProductBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    int page = 1;
    SwipeRefreshLayout refresh;
    public AsyncTask actionGet;
    BeanMainGiftingCategory param;

    public GiftingProductsFragment() {
        // Required empty public constructor
    }

    public static GiftingProductsFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        GiftingProductsFragment fragment = new GiftingProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_gifting_products, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        param = (BeanMainGiftingCategory) CmmFunc.tryParseJson(getArguments().getString("data"), BeanMainGiftingCategory.class);
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
                        adapter = new ProductAdapter(GiftingProductsFragment.this, recycler, products);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh.setOnRefreshListener(GiftingProductsFragment.this);
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
                String retValue = APIHelper.getGiftingCategoryContent(param.getId(), page);
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray arr = data.getJSONArray("products");
                    for (int i = 0; i < arr.length(); i++) {
                        BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(arr.getString(i), BeanProduct.class);
                        products.add(bean);
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
