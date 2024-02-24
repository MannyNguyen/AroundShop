package vn.nip.around.Fragment.Gift;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Bean.BeanShoppingCategory;
import vn.nip.around.Class.CmmAnimation;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Fragment.SearchTag.SearchFragment;
import vn.nip.around.Fragment.Shopping.TabShoppingFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabGiftingFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    TextView numberCart;
    ImageButton cart;
    TabLayout tab;
    ViewPager pager;
    ImageButton layoutList;
    ViewPagerAdapter adapter;
    BeanCategory param;
    FrameLayout container;

    //endregion

    public TabGiftingFragment() {
        // Required empty public constructor
    }

    public static TabGiftingFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("data", data);
        TabGiftingFragment fragment = new TabGiftingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_tab_gifting, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        param = (BeanCategory) CmmFunc.tryParseJson(getArguments().getString("data"), BeanCategory.class);
        container = (FrameLayout) view.findViewById(R.id.container);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        cart = (ImageButton) view.findViewById(R.id.cart);
        tab = (TabLayout) view.findViewById(R.id.tab);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            try {
                TextView title = (TextView) view.findViewById(R.id.title);
                title.setText(BeanCategory.class.getDeclaredField(getString(R.string.key_name)).get(param) + "");
                threadInit = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pager = (ViewPager) view.findViewById(R.id.pager);
                            layoutList = (ImageButton) view.findViewById(R.id.layout_list);


                            adapter = new ViewPagerAdapter(getChildFragmentManager());
                            for (BeanCategory bean : param.getTab_categories()) {
                                adapter.addFrag(ChildGiftingFragment.newInstance(param.getId(), bean.getId()), BeanCategory.class.getDeclaredField(getString(R.string.key_name)).get(bean) + "");
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
                                        if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                            layoutList.setImageResource(R.drawable.ic_grid);
                                        } else {
                                            layoutList.setImageResource(R.drawable.ic_list);
                                        }
                                        pager.setAdapter(adapter);
                                        tab.setupWithViewPager(pager);
                                        UITab(param.getTab_categories());

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
                                                        }
                                                    });
                                                    Fragment fragment = adapter.getItem(tab.getPosition());
                                                    final ChildGiftingFragment childShoppingFragment = (ChildGiftingFragment) fragment;
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
                                                                        childShoppingFragment.actionGetProduct.execute();
                                                                    }
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
                                                    ChildGiftingFragment childShoppingFragment = (ChildGiftingFragment) fragment;
                                                    childShoppingFragment.isActive = false;
                                                    View row = tab.getCustomView();
                                                    TextView name = (TextView) row.findViewById(R.id.name);
                                                    name.setTextColor(getResources().getColor(R.color.gray_900));
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
                                                ChildGiftingFragment childShoppingFragment = (ChildGiftingFragment) fragment;
                                                if (childShoppingFragment.actionGetProduct.getStatus() == AsyncTask.Status.PENDING) {
                                                    childShoppingFragment.actionGetProduct.execute();
                                                }


                                                childShoppingFragment.isActive = true;
                                                childShoppingFragment.onResume();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        layoutList.setOnClickListener(TabGiftingFragment.this);
                                        view.findViewById(R.id.sort).setOnClickListener(TabGiftingFragment.this);


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
            } catch (Exception e) {
                e.printStackTrace();
            }
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof TabGiftingFragment)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getCurrentChildFragment().onResume();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ChildGiftingFragment getCurrentChildFragment() {
        ViewPagerAdapter adapter = (ViewPagerAdapter) pager.getAdapter();
        ChildGiftingFragment fragment = (ChildGiftingFragment) adapter.getItem(pager.getCurrentItem());
        return fragment;
    }

    private void UITab(List<BeanCategory> items) {
        try {
            for (int i = 0; i < items.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_product, null);
                TextView name = (TextView) row.findViewById(R.id.name);
                name.setText(BeanCategory.class.getDeclaredField(getString(R.string.key_name)).get(items.get(i)) + "");
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
                            layoutList.setOnClickListener(TabGiftingFragment.this);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            layoutList.setOnClickListener(TabGiftingFragment.this);
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
                break;
            case R.id.sort:
                try {
                    ChildGiftingFragment fragment = getCurrentChildFragment();
                    if (fragment != null) {
                        fragment.page = 1;
                        if (fragment.sort.equals(fragment.INCREASE)) {
                            fragment.sort = fragment.DESCREASE;
                        } else {
                            fragment.sort = fragment.INCREASE;
                        }
                        fragment.onRefresh();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }

    }
}
