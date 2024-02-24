package vn.nip.around.Fragment.Gift;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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

import vn.nip.around.Adapter.GiftingCategoryAdapter;
import vn.nip.around.Adapter.GiftingGroupCategoryAdapter;
import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Bean.BeanMainGiftingCategory;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Fragment.Product.SuggestFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiftingCategoryFragment extends BaseFragment implements View.OnClickListener {

    public RecyclerView recycler;
    public RecyclerView recyclerSuggest;
    List<BeanProduct> suggestProducts = new ArrayList<>();
    List<BeanCategory> categories = new ArrayList<>();
    BeanMainGiftingCategory param;
    CardView container;
    int suggestPage = 1;
    AsyncTask actionGetSuggest;


    public GiftingCategoryFragment() {
        // Required empty public constructor
    }

    public static GiftingCategoryFragment newInstance(int type, String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("type", type);
        GiftingCategoryFragment fragment = new GiftingCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GiftingCategoryFragment newInstance(int type, String data, int subId) {
        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("type", type);
        args.putInt("sub_id", subId);
        GiftingCategoryFragment fragment = new GiftingCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_gifting_category, container, false);

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
                    container = (CardView) view.findViewById(R.id.container);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    recyclerSuggest = (RecyclerView) view.findViewById(R.id.recycler_suggest);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerSuggest.setLayoutManager(layoutManager);
                    ProductAdapter adapter = new ProductAdapter(GiftingCategoryFragment.this, recyclerSuggest, suggestProducts, ProductAdapter.RELATE);
                    recyclerSuggest.setAdapter(adapter);
                    new ActionGetCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    actionGetSuggest = new ActionGetProductSuggest();
                    actionGetSuggest.execute();
                    recyclerSuggest.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if (layoutManager.findLastCompletelyVisibleItemPosition() == suggestProducts.size() - 1 && suggestProducts.size() >= 5) {
                                if (actionGetSuggest == null || !actionGetSuggest.isCancelled()) {
                                    return;
                                }
                                suggestPage++;
                                actionGetSuggest = new ActionGetProductSuggest();
                                actionGetSuggest.execute();
                            }
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
        if (!(fragment instanceof GiftingCategoryFragment)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CmmVariable.producteds = APIHelper.getCart();
                    for (BeanProduct beanProduct : suggestProducts) {
                        beanProduct.setIn_cart(0);
                        for (int i : CmmVariable.producteds) {
                            if (beanProduct.getId() == i) {
                                beanProduct.setIn_cart(1);
                            }
                        }
                    }

                    if (recyclerSuggest != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (recyclerSuggest.getAdapter() != null) {
                                    recyclerSuggest.getAdapter().notifyDataSetChanged();
                                }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggest:
                try {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, SuggestFragment.newInstance(SuggestFragment.GIFTING, param.getId(), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(param) + "", CmmFunc.tryParseObject(suggestProducts), suggestPage));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class ActionGetCategory extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getGiftingCategoryContent(param.getId(), 1);
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    categories.clear();
                    categories = (List<BeanCategory>) CmmFunc.tryParseList(data.getString("categories"), BeanCategory.class);
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
                    RecyclerView.Adapter adapter = null;
                    switch (getArguments().getInt("type")) {
                        case BeanMainGiftingCategory.ONE_COLUMN:
                            adapter = new GiftingCategoryAdapter(GiftingCategoryFragment.this, recycler, categories, getArguments().getInt("type"));
                            LinearLayoutManager list = new LinearLayoutManager(getActivity());
                            recycler.setLayoutManager(list);
                            container.setBackgroundResource(R.color.white);
                            break;
                        case BeanMainGiftingCategory.TWO_COLUMN:
                        case BeanMainGiftingCategory.TWO_COLUMN_2:
                            adapter = new GiftingCategoryAdapter(GiftingCategoryFragment.this, recycler, categories, getArguments().getInt("type"));
                            GridLayoutManager grid = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
                            recycler.setLayoutManager(grid);
                            container.setBackgroundResource(R.color.white);
                            break;
                        case BeanMainGiftingCategory.GROUP:
                            adapter = new GiftingGroupCategoryAdapter(GiftingCategoryFragment.this, recycler, categories);
                            LinearLayoutManager group = new LinearLayoutManager(getActivity());
                            recycler.setLayoutManager(group);
                            recycler.setPadding(0, 0, 0, 0);
                            container.setBackgroundResource(android.R.color.transparent);
                            break;
                    }
                    recycler.setAdapter(adapter);
                    recycler.setVisibility(View.VISIBLE);

                    if (!getArguments().containsKey("sub_id")) {
                        return;
                    }
                    switch (getArguments().getInt("type")) {
                        case BeanMainGiftingCategory.ONE_COLUMN:
                        case BeanMainGiftingCategory.TWO_COLUMN:
                        case BeanMainGiftingCategory.TWO_COLUMN_2:
                            BeanCategory beanCategory = BeanCategory.getById(getArguments().getInt("sub_id"), categories);
                            if (beanCategory != null) {
                                FragmentHelper.addFragment(getActivity(), R.id.home_content, TabGiftingFragment.newInstance(CmmFunc.tryParseObject(beanCategory)));
                            }
                            break;
                        case BeanMainGiftingCategory.GROUP:
                            //Check sub id trong sub category,list sub category nằm trong tab category, loop tab category lấy list sub category, lấy item của sub category
                            //so sánh sub_id từ notification server với sub_id trong sub category

                            for (BeanCategory bean : categories) {
                                List<BeanCategory> subs = bean.getSub_categories();
                                BeanCategory sub = BeanCategory.getById(getArguments().getInt("sub_id"), subs);
                                if (sub != null) {
                                    FragmentHelper.addFragment(getActivity(), R.id.home_content, TabGiftingFragment.newInstance(CmmFunc.tryParseObject(sub)));
                                    return;
                                }
                            }
                            break;
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
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
                String retValue = APIHelper.getSuggestGiftingProduct(param.getId(), suggestPage);
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
                    view.findViewById(R.id.suggest).setOnClickListener(GiftingCategoryFragment.this);
                    recyclerSuggest.getAdapter().notifyDataSetChanged();
                    this.cancel(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }

    }
}
