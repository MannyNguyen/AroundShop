package vn.nip.around.Fragment.Shopping;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Bean.BeanShoppingCategory;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.ProductGridLayoutManager;
import vn.nip.around.Custom.ProductListLayoutManager;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductBaseFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Fragment.Search.HomeSearchFragment;
import vn.nip.around.Fragment.SearchTag.SearchFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabShoppingFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    TextView numberCart;
    ImageButton cart;
    TabLayout tab;
    ViewPager pager;
    ImageButton layoutList;
    ViewPagerAdapter adapter;
    BeanShoppingCategory param;
    FrameLayout container;

    //endregion

    //region Contructors
    public TabShoppingFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static TabShoppingFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        TabShoppingFragment fragment = new TabShoppingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TabShoppingFragment newInstance(String data, int subId) {

        Bundle args = new Bundle();
        args.putString("data", data);
        args.putInt("sub_id", subId);
        TabShoppingFragment fragment = new TabShoppingFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_tab_shopping, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = (FrameLayout) view.findViewById(R.id.container);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        cart = (ImageButton) view.findViewById(R.id.cart);
        tab = (TabLayout) view.findViewById(R.id.tab);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            final TextView title = ((TextView) getView().findViewById(R.id.title));
            param = (BeanShoppingCategory) CmmFunc.tryParseJson(getArguments().getString("data"), BeanShoppingCategory.class);
            try {
                title.setText(BeanShoppingCategory.class.getDeclaredField(getString(R.string.key_name)).get(param) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(CmmVariable.SLEEP);
                        pager = (ViewPager) view.findViewById(R.id.pager);
                        layoutList = (ImageButton) view.findViewById(R.id.layout_list);

                        adapter = new ViewPagerAdapter(getChildFragmentManager());
                        for (BeanShoppingCategory bean : param.getSub_categories()) {
                            String title = BeanShoppingCategory.class.getDeclaredField(getString(R.string.key_name)).get(bean) + "";
                            adapter.addFrag(ChildShoppingFragment.newInstance(bean.getId(), title), title);
                        }

                        view.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    FragmentHelper.addFragment(getActivity(), R.id.home_content, SearchFragment.newInstance(getArguments().getString("data")));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    title.setText(BeanShoppingCategory.class.getDeclaredField(getString(R.string.key_name)).get(param) + "");
                                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                        layoutList.setImageResource(R.drawable.ic_grid);
                                    } else {
                                        layoutList.setImageResource(R.drawable.ic_list);
                                    }
                                    pager.setOffscreenPageLimit(param.getSub_categories().size());
                                    pager.setAdapter(adapter);
                                    tab.setupWithViewPager(pager);


                                    UITab(param.getSub_categories());
                                    tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(final TabLayout.Tab tab) {
                                            try {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        View row = tab.getCustomView();
                                                        TextView name = (TextView) row.findViewById(R.id.name);
                                                        name.setTextColor(getResources().getColor(R.color.main));
                                                        //Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                                                        //name.setTypeface(customFont);
                                                    }
                                                });
                                                Fragment fragment = adapter.getItem(tab.getPosition());
                                                final ChildShoppingFragment childShoppingFragment = (ChildShoppingFragment) fragment;
                                                childShoppingFragment.isActive = true;
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            if (!childShoppingFragment.isActive) {
                                                                return;
                                                            }

                                                            if (childShoppingFragment.products.size() == 0) {
                                                                if (childShoppingFragment.actionGetProduct.getStatus() == AsyncTask.Status.PENDING) {
                                                                    childShoppingFragment.page = 1;
                                                                    childShoppingFragment.actionGetProduct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0, 0);
                                                                }
                                                            }
                                                            if (childShoppingFragment.actionGetSuggest.getStatus() == AsyncTask.Status.PENDING) {
                                                                childShoppingFragment.actionGetSuggest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                            }

                                                            if (childShoppingFragment.actionGetShop.getStatus() == AsyncTask.Status.PENDING) {
                                                                childShoppingFragment.actionGetShop.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                                            }

                                                            childShoppingFragment.onResume();
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }, 1000);


                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {
                                            try {
                                                Fragment fragment = adapter.getItem(tab.getPosition());
                                                ChildShoppingFragment childShoppingFragment = (ChildShoppingFragment) fragment;
                                                childShoppingFragment.isActive = false;
                                                View row = tab.getCustomView();
                                                TextView name = (TextView) row.findViewById(R.id.name);
                                                name.setTextColor(getResources().getColor(R.color.gray_900));
                                                //Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Regular) + ".ttf");
                                                //name.setTypeface(customFont);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {
                                        }


                                    });
                                    Fragment fragment = adapter.getItem(0);
                                    if (fragment != null) {
                                        try {
                                            ChildShoppingFragment childShoppingFragment = (ChildShoppingFragment) fragment;
                                            if (childShoppingFragment.actionGetProduct.getStatus() == AsyncTask.Status.PENDING) {
                                                childShoppingFragment.actionGetProduct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0, 0);
                                            }

                                            if (childShoppingFragment.actionGetSuggest.getStatus() == AsyncTask.Status.PENDING) {
                                                childShoppingFragment.actionGetSuggest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            }

                                            if (childShoppingFragment.actionGetShop.getStatus() == AsyncTask.Status.PENDING) {
                                                childShoppingFragment.actionGetShop.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                            }
                                            childShoppingFragment.isActive = true;
                                            childShoppingFragment.onResume();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    layoutList.setOnClickListener(TabShoppingFragment.this);

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (getArguments().containsKey("sub_id")) {
                                                int subId = getArguments().getInt("sub_id");
                                                BeanShoppingCategory bean = BeanShoppingCategory.getById(subId, param.getSub_categories());
                                                pager.setCurrentItem(param.getSub_categories().indexOf(bean));
                                            }
                                        }
                                    }, 2000);

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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
            if (!(fragment instanceof TabShoppingFragment)) {
                return;
            }
            getCurrentChildFragment().onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_list:
                layoutList.setOnClickListener(null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            layoutList.setOnClickListener(TabShoppingFragment.this);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            layoutList.setOnClickListener(TabShoppingFragment.this);
                        }
                    }
                }).start();
                if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                    StorageHelper.set(StorageHelper.ORDER_LIST, StorageHelper.LIST);
                    layoutList.setImageResource(R.drawable.ic_list);

                } else {
                    StorageHelper.set(StorageHelper.ORDER_LIST, StorageHelper.GRID);
                    layoutList.setImageResource(R.drawable.ic_grid);
                }
                getCurrentChildFragment().onResume();
                //updateLayoutList();
                break;
        }
    }
//endregion

    //region Methods
    private void updateLayoutList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (final Fragment fragment : TabShoppingFragment.this.getChildFragmentManager().getFragments()) {
                        if (fragment instanceof ProductBaseFragment) {
                            final ProductBaseFragment giftProductsFragment = (ProductBaseFragment) fragment;
                            LinearLayoutManager layoutManager = (LinearLayoutManager) (giftProductsFragment.recycler.getLayoutManager());
                            final int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                        giftProductsFragment.recycler.setLayoutManager(new ProductGridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                                    } else {
                                        giftProductsFragment.recycler.setLayoutManager(new ProductListLayoutManager(getActivity()));
                                    }

                                    giftProductsFragment.recycler.getAdapter().notifyDataSetChanged();
                                    giftProductsFragment.recycler.scrollToPosition(position);

                                }
                            });

                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    hideProgress();
                }
            }
        }).start();

    }

    private ChildShoppingFragment getCurrentChildFragment() {
        ViewPagerAdapter adapter = (ViewPagerAdapter) pager.getAdapter();
        ChildShoppingFragment fragment = (ChildShoppingFragment) adapter.getItem(pager.getCurrentItem());
        return fragment;
    }

    private void UITab(List<BeanShoppingCategory> items) {
        try {
            for (int i = 0; i < items.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_product, null);
                TextView name = (TextView) row.findViewById(R.id.name);
                name.setText(BeanShoppingCategory.class.getDeclaredField(getString(R.string.key_name)).get(items.get(i)) + "");
                if (i == 0) {
                    name.setTextColor(getResources().getColor(R.color.main));
                    //Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                    //name.setTypeface(customFont);
                }
                tab.getTabAt(i).setCustomView(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion
}
