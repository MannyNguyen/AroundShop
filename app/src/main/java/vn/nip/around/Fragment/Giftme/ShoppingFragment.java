package vn.nip.around.Fragment.Giftme;


import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Pickup.BookFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    int page = 1;
    SwipeRefreshLayout refresh;
    RecyclerView recycler;
    public ProductAdapter adapter;
    public List<BeanProduct> products = new ArrayList<>();
    public AsyncTask actionGet;

    public ShoppingFragment() {
        // Required empty public constructor
    }

    public static ShoppingFragment newInstance() {
        Bundle args = new Bundle();
        ShoppingFragment fragment = new ShoppingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shopping, container, false);
        }
        return view;
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
                        adapter = new ProductAdapter(ShoppingFragment.this, recycler, products);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refresh.setOnRefreshListener(ShoppingFragment.this);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                    layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
                                }
                                recycler.setLayoutManager(layoutManager);
                                recycler.setAdapter(adapter);

                                recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        LinearLayoutManager layout = (LinearLayoutManager) recyclerView.getLayoutManager();
                                        if (layout.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() >= 5) {
                                            if (actionGet == null || !actionGet.isCancelled()) {
                                                return;
                                            }
                                            page++;
                                            actionGet = new ActionGetGiftMe();
                                            actionGet.execute();
                                        }
                                    }
                                });
                                actionGet = new ActionGetGiftMe();
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
    public void onResume() {
        super.onResume();
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof ShoppingFragment)) {
            return;
        }
        updateList();
    }

    private void updateList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CmmVariable.producteds = APIHelper.getCart();
                    for (BeanProduct beanProduct : products) {
                        beanProduct.setIn_cart(0);
                        for (int i : CmmVariable.producteds) {
                            if (beanProduct.getId() == i) {
                                beanProduct.setIn_cart(1);
                            }
                        }
                    }

                    if (recycler != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.getAdapter().notifyDataSetChanged();
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

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
            actionGet = new ActionGetGiftMe();
            actionGet.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ActionGetGiftMe extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refresh.setRefreshing(true);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getGiftMe(page, "SHOPPING");
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
