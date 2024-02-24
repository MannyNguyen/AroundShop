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
public class ChildGiftingFragment extends ProductBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public final String INCREASE = "PRICE_INCREASE";
    public final String DESCREASE = "PRICE_DECREASE";

    public String sort = INCREASE;

    int page = 1;
    SwipeRefreshLayout refresh;
    AsyncTask actionGetProduct;
    int idCategory;
    int idTab;

    public ChildGiftingFragment() {
        // Required empty public constructor
    }

    public static ChildGiftingFragment newInstance(int idCategory, int idTab) {

        Bundle args = new Bundle();
        args.putInt("id_category", idCategory);
        args.putInt("id_tab", idTab);
        ChildGiftingFragment fragment = new ChildGiftingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_child_gifting, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idCategory = getArguments().getInt("id_category");
        idTab = getArguments().getInt("id_tab");
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        actionGetProduct = new ActionGetProduct();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {

            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        adapter = new ProductAdapter(ChildGiftingFragment.this, recycler, products);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    showProgress();
                                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                        GridLayoutManager lm = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
                                        recycler.setLayoutManager(lm);
                                    } else {
                                        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
                                        recycler.setLayoutManager(lm);
                                    }

                                    recycler.setAdapter(adapter);
                                    refresh.setOnRefreshListener(ChildGiftingFragment.this);

                                    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
                                            if (layoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() >= 5) {
                                                if (actionGetProduct == null) {
                                                    return;
                                                }
                                                if (actionGetProduct.getStatus() == AsyncTask.Status.FINISHED) {
                                                    page++;
                                                    actionGetProduct = new ActionGetProduct();
                                                    actionGetProduct.execute();
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





    @Override
    public void onRefresh() {
        if (actionGetProduct == null) {
            return;
        }

        if (actionGetProduct.getStatus() != AsyncTask.Status.FINISHED) {
            actionGetProduct.cancel(true);
        }

        page = 1;
        products.clear();
        actionGetProduct = new ActionGetProduct();
        actionGetProduct.execute();

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
                String retValue = APIHelper.getGiftingCategoryProduct(idCategory, idTab, sort, page);
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
                    } else {
                        refresh.setVisibility(View.VISIBLE);
                    }
                    recycler.getAdapter().notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
                refresh.setRefreshing(false);
            }
        }

    }
}
