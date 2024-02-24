package vn.nip.around.Fragment.Shopping;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Adapter.ShopSpinnerAdapter;
import vn.nip.around.Adapter.SortAdapter;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Bean.BeanShop;
import vn.nip.around.Bean.BeanSort;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.ProductGridLayoutManager;
import vn.nip.around.Custom.ProductListLayoutManager;
import vn.nip.around.Fragment.Giftme.ShoppingFragment;
import vn.nip.around.Fragment.Product.SuggestFragment;
import vn.nip.around.Fragment.Product.ProductBaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChildShoppingFragment extends ProductBaseFragment implements View.OnClickListener {

    NestedScrollView scrollView;
    public RecyclerView recyclerSuggest;
    List<BeanProduct> suggestProducts = new ArrayList<>();
    int suggestPage = 1;
    public int page = 1;
    public List<BeanShop> shops = new ArrayList<>();
    AsyncTask actionGetSuggest;
    AsyncTask actionGetProduct;
    AsyncTask actionGetShop;
    ImageButton sort;
    public TextView shop;

    int currentSort;
    int currentShop;

    FrameLayout container;

    RecyclerView.OnScrollListener onScrollListener;

    public ChildShoppingFragment() {
        // Required empty public constructor
    }

    public static ChildShoppingFragment newInstance(int id, String title) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("title", title);
        ChildShoppingFragment fragment = new ChildShoppingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_child_shopping, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = (FrameLayout) view.findViewById(R.id.child_shopping_fragment);
        sort = (ImageButton) view.findViewById(R.id.sort);
        sort.setOnClickListener(ChildShoppingFragment.this);
        shop = (TextView) view.findViewById(R.id.shop);
        actionGetProduct = new ActionGetProduct();
        actionGetSuggest = new ActionGetProductSuggest();
        actionGetShop = new ActionGetShop();
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerSuggest = (RecyclerView) view.findViewById(R.id.recycler_suggest);
    }

    @Override
    public void onStart() {
        super.onStart();
        scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
        if (!isLoaded) {
            showProgress();
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Thread.sleep(CmmVariable.SLEEP);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

                        final ProductAdapter adapterSuggest = new ProductAdapter(ChildShoppingFragment.this, recyclerSuggest, suggestProducts, ProductAdapter.RELATE);
                        adapter = new ProductAdapter(ChildShoppingFragment.this, recycler, products);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {


                                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                        ProductGridLayoutManager lm = new ProductGridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
                                        recycler.setLayoutManager(lm);
                                    } else {
                                        ProductListLayoutManager lm = new ProductListLayoutManager(getActivity());
                                        recycler.setLayoutManager(lm);
                                    }

                                    recycler.setAdapter(adapter);

                                    recyclerSuggest.setLayoutManager(layoutManager);
                                    recyclerSuggest.setAdapter(adapterSuggest);

                                    onScrollListener = new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            if (layoutManager.findLastCompletelyVisibleItemPosition() == suggestProducts.size() - 1 && suggestProducts.size() >= 5) {
                                                if (actionGetSuggest == null) {
                                                    return;
                                                }
                                                if (actionGetSuggest.getStatus() == AsyncTask.Status.FINISHED) {
                                                    suggestPage++;
                                                    actionGetSuggest = new ActionGetProductSuggest();
                                                    actionGetSuggest.execute();
                                                }

                                            }
                                        }
                                    };
                                    //recyclerSuggest.addOnScrollListener(onScrollListener);

                                    scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                                        @Override
                                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                            if (scrollY > oldScrollY) {
                                                //Log.i(TAG, "Scroll DOWN");
                                            }
                                            if (scrollY < oldScrollY) {
                                                //Log.i(TAG, "Scroll UP");
                                            }

                                            if (scrollY == 0) {
                                                //Log.i(TAG, "TOP SCROLL");
                                            }

                                            if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                                Log.i("BOTTOM", "BOTTOM SCROLL");
                                                if (actionGetSuggest.getStatus() == AsyncTask.Status.FINISHED) {
                                                    page++;
                                                    actionGetProduct = new ActionGetProduct();
                                                    int idShop = 0;
                                                    BeanShop beanShop = BeanShop.getSelectedShop(shops);
                                                    if (beanShop != null) {
                                                        idShop = beanShop.getId();
                                                    }
                                                    actionGetProduct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentSort, idShop);
                                                }

                                            }
                                        }
                                    });
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
    }


    public void updateLayout() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    RecyclerView.LayoutManager layoutManager = recycler.getLayoutManager();
                    if (layoutManager.getClass().getName().equals(ProductListLayoutManager.class.getName()) && StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.LIST)) {
                        return;
                    }
                    if (layoutManager.getClass().getName().equals(ProductGridLayoutManager.class.getName()) && StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        return;
                    }
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    if (!isActive) {
                        return;
                    }
                    final int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.LIST)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.setLayoutManager(new ProductListLayoutManager(getActivity()));
                            }
                        });

                    }
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recycler.setLayoutManager(new ProductGridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                            }
                        });

                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!isActive) {
                                    return;
                                }
                                recycler.getAdapter().notifyDataSetChanged();
                                recycler.scrollToPosition(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggest:
                try {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, SuggestFragment.newInstance(SuggestFragment.SHOPPING, getArguments().getInt("id"), getArguments().getString("title") + "", CmmFunc.tryParseObject(suggestProducts), suggestPage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.shop:
                try {
                    ShoppingsFragment shoppingFragment = ShoppingsFragment.newInstance();
                    shoppingFragment.childShoppingFragment = ChildShoppingFragment.this;
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, shoppingFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.sort:
                try {
                    if (actionGetProduct != null) {
                        actionGetProduct.cancel(true);
                    }

                    if (currentSort == BeanSort.INCREASE) {
                        currentSort = BeanSort.DECREASE;
                    } else {
                        currentSort = BeanSort.INCREASE;
                    }
                    page = 1;
                    products.clear();
                    int idShop = 0;
                    BeanShop beanShop = BeanShop.getSelectedShop(shops);
                    if (beanShop != null) {
                        idShop = beanShop.getId();
                    }

                    actionGetProduct = new ActionGetProduct();
                    actionGetProduct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentSort, idShop);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void refresh() {
        try {
            page = 1;
            int idShop = 0;
            products.clear();
            recycler.getAdapter().notifyDataSetChanged();
            BeanShop beanShop = BeanShop.getSelectedShop(shops);
            if (beanShop != null) {
                idShop = beanShop.getId();
            }
            shop.setText(beanShop.getName() + "");
            actionGetProduct = new ActionGetProduct();
            actionGetProduct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentSort, idShop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ActionGetProductSuggest extends ActionAsync {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getSuggestShoppingProduct(getArguments().getInt("id"), suggestPage);
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray arr = data.getJSONArray("products");
                    for (int i = 0; i < arr.length(); i++) {
                        BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(arr.getString(i), BeanProduct.class);
                        suggestProducts.add(bean);
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
                    recyclerSuggest.getAdapter().notifyDataSetChanged();
                    view.findViewById(R.id.suggest).setOnClickListener(ChildShoppingFragment.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

    }

    public class ActionGetProduct extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                int priceType = (int) params[0];
                int idShop = (int) params[1];
                String retValue = APIHelper.getShoppingCategoryContent(getArguments().getInt("id"), priceType, idShop, page);
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray arr = data.getJSONArray("products");
                    if (page == 1) {
                        products.clear();
                    }
                    for (int i = 0; i < arr.length(); i++) {
                        BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(arr.getString(i), BeanProduct.class);
                        products.add(bean);
                    }

                    if (arr.length() == 0) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//                                    @Override
//                                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//                                    }
//                                });
//                            }
//                        });

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
                    if (page == 1 && products.size() == 0) {
                        view.findViewById(R.id.no_product_message).setVisibility(View.VISIBLE);
                        //recycler.removeOnScrollListener(onScrollListener);

                    } else {
                        scrollView.setVisibility(View.VISIBLE);
                    }
                    recycler.getAdapter().notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
                //??? Khong show list item
                recycler.getAdapter().notifyDataSetChanged();
            }

        }

    }

    class ActionGetShop extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                jsonObject = null;
                String response = APIHelper.getShoppingCategoryShop(getArguments().getInt("id"));
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    shops = (List<BeanShop>) CmmFunc.tryParseList(data.getString("shops"), BeanShop.class);
                    if (shops.size() > 0) {
                        shops.get(0).setSelected(true);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shop.setText(shops.get(0).getName() + "" );
                            shop.setOnClickListener(ChildShoppingFragment.this);
                        }
                    });


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }
    }
}
