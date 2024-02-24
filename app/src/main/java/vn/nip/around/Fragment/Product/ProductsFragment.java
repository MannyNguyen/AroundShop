package vn.nip.around.Fragment.Product;


import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    public static final String HOT = "HOT";
    public static final String ALL = "ALL";
    public static final String PRICE_INCREASE = "PRICE_INCREASE";
    public static final String PRICE_DECREASE = "PRICE_DECREASE";


    TextView numberCart;
    ImageButton cart;
    BeanCategory param;
    TabLayout tab;
    ViewPager pager;
    ImageButton layoutList;
    //endregion

    //region Contrucstor
    public ProductsFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static ProductsFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        ProductsFragment fragment = new ProductsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_products, container, false);
        }
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        param = (BeanCategory) CmmFunc.tryParseJson(getArguments().getString("data"), BeanCategory.class);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        cart = (ImageButton) view.findViewById(R.id.cart);
        tab = (TabLayout) view.findViewById(R.id.tab);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            try {
                final TextView title = ((TextView) getView().findViewById(R.id.title));
                title.setText(param.getClass().getDeclaredField(getString(R.string.server_key_name_gift)).get(param) + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(400);
                        pager = (ViewPager) view.findViewById(R.id.pager);
                        layoutList = (ImageButton) view.findViewById(R.id.layout_list);
                        pager.setOffscreenPageLimit(3);
                        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
                        adapter.addFrag(ChildProductsFragment.newInstance(ALL, param.getId()), getString(R.string.all));
                        //adapter.addFrag(ChildProductsFragment.newInstance(HOT, param.getId()), getString(R.string.hot));
                        //adapter.addFrag(ChildProductsFragment.newInstance(PRICE_INCREASE, param.getId()), getString(R.string.price));

                        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {
                                Fragment fragment = adapter.getItem(position);
                                fragment.onResume();
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });


                        layoutList.setOnClickListener(ProductsFragment.this);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    pager.setAdapter(adapter);
                                    tab.setupWithViewPager(pager);

                                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                        layoutList.setImageResource(R.drawable.ic_grid);
                                    } else {
                                        layoutList.setImageResource(R.drawable.ic_list);
                                    }
                                    UITab();

                                    tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                        @Override
                                        public void onTabSelected(TabLayout.Tab tab) {
                                            try {
                                                View row = tab.getCustomView();
                                                TextView name = (TextView) row.findViewById(R.id.name);
                                                name.setTextColor(getResources().getColor(R.color.main));
                                                Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                                                name.setTypeface(customFont);

                                                Fragment fragment = adapter.getItem(tab.getPosition());
                                                ((ChildProductsFragment) fragment).isActive = true;
                                                fragment.onResume();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onTabUnselected(TabLayout.Tab tab) {
                                            View row = tab.getCustomView();
                                            TextView name = (TextView) row.findViewById(R.id.name);
                                            name.setTextColor(getResources().getColor(R.color.gray_900));
                                            Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Regular) + ".ttf");
                                            name.setTypeface(customFont);
                                        }

                                        @Override
                                        public void onTabReselected(TabLayout.Tab tab) {
                                            if (tab.getPosition() == ProductsFragment.this.tab.getTabCount() - 1) {
                                                Fragment fragment = adapter.getItem(tab.getPosition());
                                                if (fragment != null) {
                                                    ChildProductsFragment childProductsFragment = (ChildProductsFragment) fragment;
                                                    if (childProductsFragment.getArguments().getString("tab").equals(PRICE_INCREASE)) {
                                                        childProductsFragment.getArguments().putString("tab", PRICE_DECREASE);
                                                    } else if (childProductsFragment.getArguments().getString("tab").equals(PRICE_DECREASE)) {
                                                        childProductsFragment.getArguments().putString("tab", PRICE_INCREASE);
                                                    }

                                                    childProductsFragment.onRefresh();

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

        getNumberCart();

    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment f = FragmentHelper.getActiveFragment(getActivity());
        if (!(f instanceof ProductsFragment)) {
            return;
        }
        try {
            Fragment fragment = ((ViewPagerAdapter) pager.getAdapter()).getItem(pager.getCurrentItem());
            ChildProductsFragment childProductsFragment = (ChildProductsFragment) fragment;
            childProductsFragment.isActive = true;
            fragment.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                FragmentHelper.pop(getActivity());
                break;
            case R.id.layout_list:
                if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                    StorageHelper.set(StorageHelper.ORDER_LIST, StorageHelper.LIST);
                    layoutList.setImageResource(R.drawable.ic_list);

                } else {
                    StorageHelper.set(StorageHelper.ORDER_LIST, StorageHelper.GRID);
                    layoutList.setImageResource(R.drawable.ic_grid);
                }
//                updateLayoutList();
                updateLayoutList();
                break;

            case R.id.cart:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, CartFragment.newInstance());
                break;


        }
    }

    //endregion

    //region Methods
    private void UITab() {
        for (int i = 0; i < tab.getTabCount(); i++) {
            View row = getActivity().getLayoutInflater().inflate(R.layout.tab_product, null);
            TextView name = (TextView) row.findViewById(R.id.name);
            switch (i) {
                case 0:
                    name.setText(getString(R.string.all));
                    name.setTextColor(getResources().getColor(R.color.main));
                    Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                    name.setTypeface(customFont);
                    break;
                case 1:
                    name.setText(getString(R.string.hot));
                    break;
                case 2:
                    name.setText(getString(R.string.price));
                    break;
            }
            tab.getTabAt(i).setCustomView(row);
        }
    }

    private void getNumberCart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int number = APIHelper.getNumberCart();
                    if (number > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    numberCart.setVisibility(View.VISIBLE);
                                    numberCart.setText(number + StringUtils.EMPTY);
                                    cart.setImageResource(R.drawable.ic_cart);
                                    cart.setOnClickListener(ProductsFragment.this);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    numberCart.setVisibility(View.GONE);
                                    numberCart.setText(StringUtils.EMPTY);
                                    cart.setImageResource(R.drawable.ic_cart_gray);
                                    cart.setOnClickListener(null);
                                } catch (Exception e) {
                                    e.printStackTrace();
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

    private void updateLayoutList() {
        try {
            for (Fragment fragment : this.getChildFragmentManager().getFragments()) {
                if (fragment instanceof ProductBaseFragment) {
                    ChildProductsFragment childProductsFragment = (ChildProductsFragment) fragment;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) (childProductsFragment.recycler.getLayoutManager());
                    int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        childProductsFragment.recycler.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                    } else {
                        childProductsFragment.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }

                    childProductsFragment.recycler.getAdapter().notifyDataSetChanged();
                    childProductsFragment.recycler.scrollToPosition(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion
}
