package vn.nip.around.Fragment.Giftme;


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

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiftMeFragment extends BaseFragment implements View.OnClickListener {
    TextView numberCart;
    ImageButton cart;
    TabLayout tab;
    ViewPager pager;
    ImageButton layoutList;

    public GiftMeFragment() {
        // Required empty public constructor
    }

    public static GiftMeFragment newInstance() {
        Bundle args = new Bundle();
        GiftMeFragment fragment = new GiftMeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_gift_me, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        cart = (ImageButton) view.findViewById(R.id.cart);
        tab = (TabLayout) view.findViewById(R.id.tab);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final TextView title = ((TextView) getView().findViewById(R.id.title));
                        pager = (ViewPager) view.findViewById(R.id.pager);
                        layoutList = (ImageButton) view.findViewById(R.id.layout_list);
                        pager.setOffscreenPageLimit(2);
                        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
                        adapter.addFrag(DailyFragment.newInstance(), getString(R.string.daily));
                        adapter.addFrag(ShoppingFragment.newInstance(), getString(R.string.shopping));
                        layoutList.setOnClickListener(GiftMeFragment.this);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                title.setText(getString(R.string.gift_me));
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
                                        View row = tab.getCustomView();
                                        TextView name = (TextView) row.findViewById(R.id.name);
                                        name.setTextColor(getResources().getColor(R.color.main));
                                        //Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                                        //name.setTypeface(customFont);
                                        Fragment fragment = adapter.getItem(tab.getPosition());
                                        if (fragment != null) {
                                            fragment.onResume();
                                        }

                                        if(tab.getPosition() == 0){
                                            view.findViewById(R.id.disable_layout_list).setVisibility(View.VISIBLE);
                                        } else{
                                            view.findViewById(R.id.disable_layout_list).setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onTabUnselected(TabLayout.Tab tab) {
                                        View row = tab.getCustomView();
                                        TextView name = (TextView) row.findViewById(R.id.name);
                                        name.setTextColor(getResources().getColor(R.color.gray_900));
                                        // Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Regular) + ".ttf");
                                        //name.setTypeface(customFont);
                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {
                                    }

                                });
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
        //getNumberCart(cart, numberCart);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_list:
                if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                    StorageHelper.set(StorageHelper.ORDER_LIST, StorageHelper.LIST);
                    layoutList.setImageResource(R.drawable.ic_list);

                } else {
                    StorageHelper.set(StorageHelper.ORDER_LIST, StorageHelper.GRID);
                    layoutList.setImageResource(R.drawable.ic_grid);
                }
                updateLayoutList();
                break;
            case R.id.cart:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, CartFragment.newInstance());
                break;
        }
    }

    private void updateLayoutList() {
        try {
            for (Fragment fragment : this.getChildFragmentManager().getFragments()) {
                if (fragment instanceof ShoppingFragment) {
                    ShoppingFragment shoppingFragment = (ShoppingFragment) fragment;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) (shoppingFragment.recycler.getLayoutManager());
                    int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        shoppingFragment.recycler.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                    } else {
                        shoppingFragment.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }

                    shoppingFragment.recycler.getAdapter().notifyDataSetChanged();
                    shoppingFragment.recycler.scrollToPosition(position);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UITab() {
        for (int i = 0; i < tab.getTabCount(); i++) {
            View row = getActivity().getLayoutInflater().inflate(R.layout.tab_product, null);
            TextView name = (TextView) row.findViewById(R.id.name);
            name.setText(pager.getAdapter().getPageTitle(i) + "");
            if (i == 0) {
                name.setTextColor(getResources().getColor(R.color.main));
            }
            tab.getTabAt(i).setCustomView(row);
        }
    }

}
