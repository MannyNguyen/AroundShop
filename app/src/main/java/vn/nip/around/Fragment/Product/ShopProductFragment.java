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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 * Get products for shop
 */
public class ShopProductFragment extends ProductBaseFragment implements SwipeRefreshLayout.OnRefreshListener {


    public int page = 1;
    ActionAsync actionGet;


    SwipeRefreshLayout refresh;


    RecyclerView.OnScrollListener onScrollListener;

    public ShopProductFragment() {
        // Required empty public constructor
    }

    public static ShopProductFragment newInstance(int id, String title) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("title", title);
        ShopProductFragment fragment = new ShopProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shop_product, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getArguments().getString("title"));
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    isActive = true;

                    refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    adapter = new ProductAdapter(ShopProductFragment.this, recycler, products);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
                    }

                    final LinearLayoutManager finalLayoutManager = layoutManager;
                    onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() >= 5) {
                                if (actionGet == null) {
                                    return;
                                }

                                if (actionGet.getStatus() == AsyncTask.Status.FINISHED) {
                                    page++;
                                    actionGet = new ActionGetProduct();
                                    actionGet.execute();
                                }

                            }
                        }
                    };
                    recycler.addOnScrollListener(onScrollListener);
                    refresh.setOnRefreshListener(ShopProductFragment.this);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            recycler.setLayoutManager(finalLayoutManager);
                            recycler.setAdapter(adapter);
                        }
                    });

                    actionGet = new ActionGetProduct();
                    actionGet.execute();
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof ShopProductFragment)) {
            return;
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        if (actionGet != null) {
            actionGet.cancel(true);
        }
        products.clear();
        actionGet = new ActionGetProduct();
        actionGet.execute();
    }

    class ActionGetProduct extends ActionAsync {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getShopProduct(getArguments().getInt("id"), page);
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONArray arr = jsonObject.getJSONArray("data");
                    for (int i = 0; i < arr.length(); i++) {
                        BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(arr.getString(i), BeanProduct.class);
                        products.add(bean);
                    }

                    if (arr.length() == 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    recycler.removeOnScrollListener(onScrollListener);
                                    if (page == 1) {
                                        view.findViewById(R.id.no_product_message).setVisibility(View.VISIBLE);
                                        refresh.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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
                    recycler.getAdapter().notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                refresh.setRefreshing(false);
                hideProgress();
            }
        }

    }
}
