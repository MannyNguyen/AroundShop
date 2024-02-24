package vn.nip.around.Fragment.Common;


import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import vn.nip.around.Adapter.BannerHomeAdapter;
import vn.nip.around.Adapter.HomeCategoryAdapter;
import vn.nip.around.Bean.BeanBanner;
import vn.nip.around.Bean.BeanHomeBanner;
import vn.nip.around.Bean.BeanHomeCat;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Banner.BannerHomeFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.FullOrder.FullOrderFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.MapCODFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Fragment.Giftme.GiftMeFragment;
import vn.nip.around.Fragment.Search.HomeSearchFragment;
import vn.nip.around.Fragment.SearchTag.SearchFragment;
import vn.nip.around.Fragment.SendGift.SendGiftFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DeepLinkRouteHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.NotificationRouteHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.LoginActivity;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    public static final int RE_ORDER = 1;
    public static final int GIFTING = 3;
    public static final int PRODUCT = 2;

    ImageButton ask, sendGift;
    ImageView titleGroup1, titleGroup2;
    RecyclerView recyclerGroup1, recyclerGroup2;
    ViewPager topPager, bottomPager;
    TabLayout topTab;
    FrameLayout giftMeContainer, framePan;

    public ImageButton cart;
    public TextView numberCart;
    Timer timer;

    //endregion

    //region Contructors
    public HomeFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Instance
    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        cart = (ImageButton) view.findViewById(R.id.cart);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            ask = (ImageButton) view.findViewById(R.id.ask);
            framePan = (FrameLayout) view.findViewById(R.id.frame_pan);
            sendGift = (ImageButton) view.findViewById(R.id.send_gift);
            titleGroup1 = (ImageView) view.findViewById(R.id.title_group_1);
            titleGroup2 = (ImageView) view.findViewById(R.id.title_group_2);
            recyclerGroup1 = (RecyclerView) view.findViewById(R.id.group_1);
            recyclerGroup2 = (RecyclerView) view.findViewById(R.id.group_2);
            topPager = (ViewPager) view.findViewById(R.id.pager_top);
            bottomPager = (ViewPager) view.findViewById(R.id.pager_bottom);
            topTab = (TabLayout) view.findViewById(R.id.tab_top);
            giftMeContainer = (FrameLayout) view.findViewById(R.id.gift_me_container);
            ask.setOnClickListener(HomeFragment.this);
            sendGift.setOnClickListener(HomeFragment.this);
            view.findViewById(R.id.menu).setOnClickListener(HomeFragment.this);
            view.findViewById(R.id.cart).setOnClickListener(HomeFragment.this);
            view.findViewById(R.id.search_content).setOnClickListener(HomeFragment.this);
            recyclerGroup1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerGroup2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //SmartFoxHelper.getInstance().disconnect();
        try {
            //view.findViewById(R.id.home_fragment).setVisibility(View.VISIBLE);
            //getNumberCart(cart, numberCart);
            new ActionInit().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new ActionGetAroundPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            if (topTab.getTabCount() == 0) {
                getBanner();
            }


            if (StorageHelper.getPhone().equals("")){
                return;
            }

            //Push Notification
            Bundle bundle = getActivity().getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey("type")) {
                    new NotificationRouteHelper().route(getActivity(), bundle);
                }
            }

//            Bundle bundle = new Bundle();
//            bundle.putString("type","CategoriesShopping");
//            bundle.putString("id","10001");
//            bundle.putString("subId", "10028");
//            new NotificationRouteHelper().route(getActivity(), bundle);

            //DeepLink share
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getActivity().getIntent())
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            if (pendingDynamicLinkData == null) {
                                return;
                            }
                            Uri deepLink = pendingDynamicLinkData.getLink();
                            DeepLinkRouteHelper.getInstance().route(deepLink);
                        }
                    }).addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("", "getDynamicLink:onFailure", e);
                }
            });

            //Schema
            Uri data = getActivity().getIntent().getData();
            if (data != null) {
                int id = Integer.parseInt(data.getLastPathSegment());
                FragmentHelper.addFragment(getActivity(), R.id.home_content, ProductFragment.newInstance(id, getString(R.string.product)));
                getActivity().getIntent().setData(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //view.findViewById(R.id.home_fragment).setVisibility(View.GONE);
    }

    @Override
    public void manualResume() {
        getBanner();
    }

    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        if (!isLogin() && v.getId() != R.id.more_product && v.getId() != R.id.more_reorder) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            return;
        }
        switch (v.getId()) {
            case R.id.menu:
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                break;


            case R.id.more_product:
            case R.id.more_reorder:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, GiftMeFragment.newInstance());
                break;

            case R.id.top_up_1:
            case R.id.top_up_2:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, AroundWalletFragment.newInstance());
                break;

            case R.id.search_content:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, SearchFragment.newInstance());
                break;

            case R.id.ask:
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setPackage(getString(R.string.messenger_pakage_name));
                    intent.setData(Uri.parse("https://m.me/1429829030418509"));
                    startActivity(intent);
                } catch (Exception e) {
                    MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                    messageDialogFragment.setMessage(getString(R.string.no_install_messenger));
                    messageDialogFragment.setOutToHide(true);

                    messageDialogFragment.setCallback(new ICallback() {
                        @Override
                        public void excute() {
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getString(R.string.messenger_pakage_name))));
                            } catch (Exception anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getString(R.string.messenger_pakage_name))));
                            }
                        }
                    });
                    messageDialogFragment.show(getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                }
                //FragmentHelper.addFragment(getActivity(), R.id.home_content, CallCenterFragment.newInstance());
                break;
            case R.id.send_gift:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, SendGiftFragment.newInstance());
                break;

            case R.id.login_container:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }).start();
                break;

            case R.id.follow_container:
                FragmentHelper.addFragment(getActivity(), R.id.home_content, OrderFollowFragment.newInstance());
                break;

        }
    }
    //endregion

    //region Methods
    private void getBanner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String retValue = APIHelper.getMainBanner();
                    JSONObject jsonObject = new JSONObject(retValue);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        List<BeanHomeBanner> banners = (List<BeanHomeBanner>) CmmFunc.tryParseList(data.getString("slide_banner"), BeanHomeBanner.class);
                        final List<BeanHomeBanner> bannerTop = new ArrayList<>();
                        final List<BeanHomeBanner> bannerBottom = new ArrayList<>();
                        for (BeanHomeBanner bean : banners) {
                            if (bean.getPosition() == 1) {
                                bannerTop.add(bean);
                            } else if (bean.getPosition() == 2) {
                                bannerBottom.add(bean);
                            }
                        }
                        final BannerHomeAdapter topAdapter = new BannerHomeAdapter(HomeFragment.this, bannerTop);
                        final BannerHomeAdapter bottomAdapter = new BannerHomeAdapter(HomeFragment.this, bannerBottom);

                        List<BeanBanner> popupBanners = (List<BeanBanner>) CmmFunc.tryParseList(data.getString("popup_banner"), BeanBanner.class);
                        for (BeanBanner bean : popupBanners) {
                            Fragment fragment = BannerHomeFragment.newInstance(CmmFunc.tryParseObject(bean));
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                topPager.setOffscreenPageLimit(bannerTop.size());
                                bottomPager.setOffscreenPageLimit(bannerBottom.size());
                                topPager.setAdapter(topAdapter);
                                bottomPager.setAdapter(bottomAdapter);
                                topTab.setupWithViewPager(topPager);
                                if (timer != null) {
                                    timer.cancel();
                                }
                                timer = new Timer();
                                timer.scheduleAtFixedRate(new TimerTask() {
                                    @Override
                                    public void run() {
                                        autoChangeSlide(topPager);
                                        autoChangeSlide(bottomPager);
                                    }
                                }, 5000, 5000);

                                if (bannerBottom.size() == 0) {
                                    view.findViewById(R.id.bottom_container).setVisibility(View.GONE);
                                } else {
                                    view.findViewById(R.id.bottom_container).setVisibility(View.VISIBLE);
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

    private void getPopupBanner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    String value = APIHelper.getBanners(2);
                    JSONObject jsonObject = new JSONObject(value);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        List<BeanBanner> banners = (List<BeanBanner>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanBanner.class);

                        for (BeanBanner beanBanner : banners) {
                            Fragment fragment = BannerHomeFragment.newInstance(CmmFunc.tryParseObject(beanBanner));
                            FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Thread autoChangeSlide(final ViewPager pager) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int position = 0;
                    if (pager.getCurrentItem() < pager.getChildCount() - 1) {
                        position = pager.getCurrentItem() + 1;
                    }
                    final int finalPosition = position;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(finalPosition);
                            //autoChangeSlide(pager);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return thread;
    }

    private boolean isLogin() {
        if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
            return false;
        }
        return true;
    }

    //endregion

    //region Actions
    class ActionInit extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String retValue = APIHelper.getMainScreen();
                jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    final JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray groups = data.getJSONArray("groups");
                    final JSONObject group1 = groups.getJSONObject(0);
                    JSONObject group2 = groups.getJSONObject(1);
                    final String imgSendGift = data.getString(getString(R.string.key_send_gift_1));
                    final String imgAsk = data.getString(getString(R.string.key_ask_1));
                    final String imgTitleGroup1 = group1.getString(getString(R.string.key_title_1));
                    final String imgTitleGroup2 = group2.getString(getString(R.string.key_title_1));
                    List<BeanHomeCat> group1s = (List<BeanHomeCat>) CmmFunc.tryParseList(group1.getString("functions"), BeanHomeCat.class);
                    final List<BeanHomeCat> group2s = (List<BeanHomeCat>) CmmFunc.tryParseList(group2.getString("functions"), BeanHomeCat.class);
                    final HomeCategoryAdapter adapter1 = new HomeCategoryAdapter(HomeFragment.this, recyclerGroup1, group1s);
                    final HomeCategoryAdapter adapter2 = new HomeCategoryAdapter(HomeFragment.this, recyclerGroup2, group2s);

                    final JSONObject giftMeInfo = data.getJSONObject("gift_me_info");
                    final int type = giftMeInfo.getInt("type");
                    switch (type) {
                        case PRODUCT:
                            final BeanProduct beanProduct = (BeanProduct) CmmFunc.tryParseJson(data.getString("gift_me_info"), BeanProduct.class);
                            final View viewProduct = getActivity().getLayoutInflater().inflate(R.layout.child_home_fragment_product, null);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ImageView productImage = (ImageView) viewProduct.findViewById(R.id.product_image);
                                        View percentContainer = viewProduct.findViewById(R.id.sale_container);
                                        TextView productSale = (TextView) viewProduct.findViewById(R.id.product_sale);
                                        TextView productName = (TextView) viewProduct.findViewById(R.id.product_name);
                                        TextView productPrice = (TextView) viewProduct.findViewById(R.id.product_price);
                                        TextView productOldPrice = (TextView) viewProduct.findViewById(R.id.product_old_price);


                                        viewProduct.findViewById(R.id.more_product).setOnClickListener(HomeFragment.this);
                                        viewProduct.findViewById(R.id.buy_now).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!isLogin()) {
                                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                    startActivity(intent);
                                                    return;
                                                }
                                                new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beanProduct.getId());
                                            }
                                        });

                                        if (beanProduct.getSave_percent() == 0) {
                                            percentContainer.setVisibility(View.GONE);
                                        } else {
                                            percentContainer.setVisibility(View.VISIBLE);
                                            productSale.setText("-" + beanProduct.getSave_percent());
                                        }

                                        productName.setText(beanProduct.getName());
                                        productPrice.setText(CmmFunc.formatMoney(beanProduct.getPrice()));
                                        productOldPrice.setVisibility(View.GONE);
                                        if (beanProduct.getOld_price() > 0) {
                                            productOldPrice.setVisibility(View.VISIBLE);
                                            productOldPrice.setPaintFlags(productOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                            productOldPrice.setText(CmmFunc.formatMoney(beanProduct.getOld_price()));
                                        }


                                        Picasso.with(getActivity()).load(beanProduct.getImage()).into(productImage);
                                        giftMeContainer.removeAllViews();
                                        giftMeContainer.addView(viewProduct);

                                        viewProduct.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    FragmentHelper.addFragment(getActivity(), R.id.home_content, ProductFragment.newInstance(giftMeInfo.getInt("id"), ""));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;

                        case RE_ORDER:
                        case GIFTING:
                            final View viewReorder = getActivity().getLayoutInflater().inflate(R.layout.child_home_fragment_reorder, null);
                            final String points = giftMeInfo.getString("locations");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        TextView serviceName = (TextView) viewReorder.findViewById(R.id.service_name);
                                        TextView productName = (TextView) viewReorder.findViewById(R.id.product_name);
                                        TextView productPrice = (TextView) viewReorder.findViewById(R.id.product_price);
                                        TextView address = (TextView) viewReorder.findViewById(R.id.address);

                                        viewReorder.findViewById(R.id.more_reorder).setOnClickListener(HomeFragment.this);
                                        viewReorder.findViewById(R.id.re_order).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    framePan.setVisibility(View.VISIBLE);
                                                    viewReorder.findViewById(R.id.re_order).setClickable(false);
                                                    new CheckOrderPickupType().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, giftMeInfo.getInt("id"),
                                                            type, giftMeInfo, points);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        productName.setText(giftMeInfo.getString("item_name"));
                                        address.setText(giftMeInfo.getString("address"));
                                        productPrice.setText(CmmFunc.formatMoney(giftMeInfo.getInt("total"), true));
                                        serviceName.setText(giftMeInfo.getString(getString(R.string.key_service_name)));
                                        giftMeContainer.removeAllViews();
                                        giftMeContainer.addView(viewReorder);

                                        viewReorder.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    FragmentHelper.addFragment(getActivity(), R.id.home_content, FullOrderFragment.newInstance(giftMeInfo.getInt("id")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Picasso.with(getActivity()).load(imgSendGift).into(sendGift);
                                Picasso.with(getActivity()).load(imgAsk).into(ask);
                                Picasso.with(getActivity()).load(imgTitleGroup1).into(titleGroup1);
                                Picasso.with(getActivity()).load(imgTitleGroup2).into(titleGroup2);
                                recyclerGroup1.setAdapter(adapter1);
                                recyclerGroup2.setAdapter(adapter2);

                                int numberOrder = data.getInt("number_order");
                                View followContainer = view.findViewById(R.id.follow_container);
                                view.findViewById(R.id.bottom_container).setVisibility(View.VISIBLE);
                                if (numberOrder > 0) {
                                    followContainer.setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.pager_bottom_container).setVisibility(View.GONE);
                                    TextView txtNum = (TextView) view.findViewById(R.id.number_order);
                                    txtNum.setText("(" + numberOrder + ")");
                                    followContainer.setOnClickListener(HomeFragment.this);
                                } else {
                                    view.findViewById(R.id.pager_bottom_container).setVisibility(View.VISIBLE);
                                    followContainer.setVisibility(View.GONE);
                                }
                                //getBanner();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            } catch (Exception e) {

            }
            return jsonObject;
        }

    }

    class ActionGetAroundPayment extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                String retValue = APIHelper.getAroundPayment();
                JSONObject jsonObject = new JSONObject(retValue);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                View loginContainer = view.findViewById(R.id.login_container);
                View aroundFeeContainer = view.findViewById(R.id.around_fee_container);
                View aroundNoFeeContainer = view.findViewById(R.id.around_no_fee_container);
                loginContainer.setVisibility(View.GONE);
                aroundFeeContainer.setVisibility(View.GONE);
                aroundNoFeeContainer.setVisibility(View.GONE);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    long aroundPay = data.getLong("around_pay");
                    if (aroundPay > 0) {
                        TextView aroundFee = (TextView) view.findViewById(R.id.around_fee);
                        aroundFee.setText(CmmFunc.formatMoney(aroundPay, false));
                        aroundFeeContainer.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.top_up_1).setOnClickListener(HomeFragment.this);
                    } else {
                        aroundNoFeeContainer.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.top_up_2).setOnClickListener(HomeFragment.this);
                    }
                } else {
                    if (StorageHelper.getPhone().equals("")) {
                        loginContainer.setVisibility(View.VISIBLE);
                        loginContainer.setOnClickListener(HomeFragment.this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ActionAddCart extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                String response = APIHelper.addCart(productID, 1);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, CartFragment.newInstance());
                }
            } catch (Exception e) {

            } finally {
                hideProgress();
            }
        }
    }

    class ActionReorderGifting extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int orderID = (int) params[0];
                String value = APIHelper.reoderGifting(orderID);
                jsonObject = new JSONObject(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, CartFragment.newInstance());
                    hideProgress();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class CheckOrderPickupType extends ActionAsync {
        int type, idOrder;
        JSONObject giftMeInfo;
        String points;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            framePan.setVisibility(View.VISIBLE);
            framePan.setClickable(false);
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            idOrder = (int) objects[0];
            type = (int) objects[1];
            giftMeInfo = (JSONObject) objects[2];
            points = (String) objects[3];
            try {
                String value = APIHelper.checkOrderPickupType(idOrder);
                jsonObject = new JSONObject(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    int isReOrder = data.getInt("is_reorder");
                    if (isReOrder == 1) {
                        int pickupType = data.getInt("pickup_type");
                        switch (pickupType) {
                            case 1:
                            case 2:
                                FragmentHelper.addFragment(getActivity(), R.id.home_content, HomeBookFragment.newInstance(points, ""));
                                break;

                            case 3:
                                FragmentHelper.addFragment(getActivity(), R.id.home_content, MapCODFragment.newInstance(points));
                                break;
                            case 4:
                                new ActionReorderGifting().execute(giftMeInfo.getInt("id"));
                                break;
                        }
                    } else {
                        CustomDialog.showMessage(GlobalClass.getActivity(), "", getString(R.string.cannot_reorder));
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                hideProgress();
            }
        }
    }

    //endregion
}
