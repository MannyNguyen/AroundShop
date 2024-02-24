package vn.nip.around.Fragment.Gift;


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

import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Bean.BeanMainGiftingCategory;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Product.ProductBaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GiftingHomeFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    TextView numberCart;
    ImageButton cart;
    TabLayout tab;
    ViewPager pager;
    ImageButton layoutList;
    ViewPagerAdapter adapter;
    View disableLayoutList;

    //endregion

    //region Contructors
    public GiftingHomeFragment() {
        // Required empty public constructor
    }

    //endregion

    //region Instance
    public static GiftingHomeFragment newInstance() {

        Bundle args = new Bundle();

        GiftingHomeFragment fragment = new GiftingHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static GiftingHomeFragment newInstance(int subId) {

        Bundle args = new Bundle();
        args.putInt("sub_id", subId);
        GiftingHomeFragment fragment = new GiftingHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_gifting_home, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        cart = (ImageButton) view.findViewById(R.id.cart);
        tab = (TabLayout) view.findViewById(R.id.tab);
        TextView title = ((TextView) getView().findViewById(R.id.title));
        title.setText(getString(R.string.gifting));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(CmmVariable.SLEEP);
                        pager = (ViewPager) view.findViewById(R.id.pager);
                        layoutList = (ImageButton) view.findViewById(R.id.layout_list);
                        disableLayoutList = view.findViewById(R.id.disable_layout_list);
                        pager.setOffscreenPageLimit(2);
                        adapter = new ViewPagerAdapter(getChildFragmentManager());

                        layoutList.setOnClickListener(GiftingHomeFragment.this);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                                    layoutList.setImageResource(R.drawable.ic_grid);
                                } else {
                                    layoutList.setImageResource(R.drawable.ic_list);
                                }
                                new ActionGet().execute();
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

    //endregion

    //region Methods
    private void UITab(List<BeanMainGiftingCategory> items) {
        try {
            for (int i = 0; i < items.size(); i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_product, null);
                TextView name = (TextView) row.findViewById(R.id.name);
                name.setText(BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(items.get(i)) + "");
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

    private void updateLayoutList() {
        try {
            for (Fragment fragment : this.getChildFragmentManager().getFragments()) {
                if (fragment instanceof ProductBaseFragment) {
                    ProductBaseFragment giftProductsFragment = (ProductBaseFragment) fragment;
                    LinearLayoutManager layoutManager = (LinearLayoutManager) (giftProductsFragment.recycler.getLayoutManager());
                    int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (StorageHelper.get(StorageHelper.ORDER_LIST).equals(StorageHelper.GRID)) {
                        giftProductsFragment.recycler.setLayoutManager(new GridLayoutManager(fragment.getActivity(), 2, LinearLayoutManager.VERTICAL, false));
                    } else {
                        giftProductsFragment.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }

                    giftProductsFragment.recycler.getAdapter().notifyDataSetChanged();
                    giftProductsFragment.recycler.scrollToPosition(position);
                }
            }
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
    //endregion

    //region Actions
    class ActionGet extends ActionAsync {

        @Override
        protected JSONObject doInBackground(final Object... params) {
            try {
                String response = APIHelper.getGiftingMainCategory();
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    final List<BeanMainGiftingCategory> items = (List<BeanMainGiftingCategory>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanMainGiftingCategory.class);
                    for (BeanMainGiftingCategory item : items) {
                        if (getArguments().containsKey("sub_id") && getArguments().getInt("sub_id") > 0) {
                            int subId = getArguments().getInt("sub_id");
                            switch (item.getLayout()) {
                                case BeanMainGiftingCategory.PRODUCT:
                                    adapter.addFrag(GiftingProductsFragment.newInstance(CmmFunc.tryParseObject(item)), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                                case BeanMainGiftingCategory.ONE_COLUMN:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.ONE_COLUMN, CmmFunc.tryParseObject(item), subId), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                                case BeanMainGiftingCategory.TWO_COLUMN:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.TWO_COLUMN, CmmFunc.tryParseObject(item), subId), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                                case BeanMainGiftingCategory.TWO_COLUMN_2:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.TWO_COLUMN_2, CmmFunc.tryParseObject(item), subId), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                                case BeanMainGiftingCategory.GROUP:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.GROUP, CmmFunc.tryParseObject(item), subId), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                            }
                        } else {
                            switch (item.getLayout()) {
                                case BeanMainGiftingCategory.PRODUCT:
                                    adapter.addFrag(GiftingProductsFragment.newInstance(CmmFunc.tryParseObject(item)), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;

                                case BeanMainGiftingCategory.ONE_COLUMN:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.ONE_COLUMN, CmmFunc.tryParseObject(item)), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;

                                case BeanMainGiftingCategory.TWO_COLUMN:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.TWO_COLUMN, CmmFunc.tryParseObject(item)), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                                case BeanMainGiftingCategory.TWO_COLUMN_2:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.TWO_COLUMN_2, CmmFunc.tryParseObject(item)), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;

                                case BeanMainGiftingCategory.GROUP:
                                    adapter.addFrag(GiftingCategoryFragment.newInstance(BeanMainGiftingCategory.GROUP, CmmFunc.tryParseObject(item)), BeanMainGiftingCategory.class.getDeclaredField(getString(R.string.key_name)).get(item) + "");
                                    break;
                            }
                        }


                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setOffscreenPageLimit(items.size());
                            pager.setAdapter(adapter);
                            tab.setupWithViewPager(pager);
                            UITab(items);
                            tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                @Override
                                public void onTabSelected(TabLayout.Tab tab) {
                                    View row = tab.getCustomView();
                                    TextView name = (TextView) row.findViewById(R.id.name);
                                    name.setTextColor(getResources().getColor(R.color.main));
                                    Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                                    name.setTypeface(customFont);
                                    Fragment fragment = adapter.getItem(tab.getPosition());
                                    if (fragment != null) {
                                        fragment.onResume();
                                    }

                                    if (fragment instanceof GiftingProductsFragment) {
                                        disableLayoutList.setVisibility(View.GONE);
                                    } else {
                                        if (disableLayoutList.getVisibility() == View.VISIBLE) {
                                            return;
                                        }
                                        disableLayoutList.setVisibility(View.VISIBLE);
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
                                }


                            });

                            if (getArguments().containsKey("sub_id")) {
                                if (getArguments().getInt("sub_id") == 0) {
                                    pager.setCurrentItem(tab.getTabCount() - 1);
                                }
                            }
                        }
                    });
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

        }
    }
    //endregion


}
