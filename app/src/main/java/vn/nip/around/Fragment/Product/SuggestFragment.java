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

import java.util.List;

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
 */
public class SuggestFragment extends ProductBaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final String SHOPPING = "SHOPPING";
    public static final String GIFTING = "GIFTING";

    public int page = 1;
    ActionAsync actionGet;

    SwipeRefreshLayout refresh;

    public SuggestFragment() {
        // Required empty public constructor
    }

    public static SuggestFragment newInstance(String type, int id, String title, String data, int page) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("type", type);
        args.putString("data", data);
        args.putInt("page", page);
        args.putInt("id", id);
        SuggestFragment fragment = new SuggestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_suggest, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        products = (List<BeanProduct>) CmmFunc.tryParseList(getArguments().getString("data"), BeanProduct.class);
        page = getArguments().getInt("page");
        actionGet = new ActionGetProductSuggest();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    final TextView title = (TextView) view.findViewById(R.id.title);
                    refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    adapter = new ProductAdapter(SuggestFragment.this, recycler, products, ProductAdapter.LIST);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                            if (finalLayoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() >= 5) {
//                                if (actionGet == null) {
//                                    return;
//                                }
//
//                                if(actionGet.getStatus() == AsyncTask.Status.FINISHED){
//
//                                }
//                            }
                            page++;
                            actionGet = new ActionGetProductSuggest();
                            actionGet.execute();
                        }
                    });

                    refresh.setOnRefreshListener(SuggestFragment.this);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            title.setText(getArguments().getString("title"));
                            recycler.setLayoutManager(layoutManager);
                            recycler.setAdapter(adapter);
                        }
                    });
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
        if (!(fragment instanceof SuggestFragment)) {
            return;
        }
        isActive = true;

    }

    @Override
    public void onRefresh() {
        page = 1;
        if (actionGet != null) {
            actionGet.cancel(true);
        }
        products.clear();
        actionGet = new ActionGetProductSuggest();
        actionGet.execute();
    }

    class ActionGetProductSuggest extends ActionAsync {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = null;
                if (getArguments().getString("type").equals(SHOPPING)) {
                    retValue = APIHelper.getSuggestShoppingProduct(getArguments().getInt("id"), page);
                } else if (getArguments().getString("type").equals(GIFTING)) {
                    retValue = APIHelper.getSuggestGiftingProduct(getArguments().getInt("id"), page);
                }

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
                    recycler.getAdapter().notifyDataSetChanged();
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
