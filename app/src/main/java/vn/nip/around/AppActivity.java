package vn.nip.around;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.commons.lang.StringUtils;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Banner.BannerHomeFragment;
import vn.nip.around.Fragment.Common.AroundPayWebviewFragment;
import vn.nip.around.Fragment.Common.AroundWalletFragment;
import vn.nip.around.Fragment.Common.CallCenterFragment;
import vn.nip.around.Fragment.Common.FollowJourneyFragment;
import vn.nip.around.Fragment.Common.HomeFragment;
import vn.nip.around.Fragment.Common.MatchFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.RateFragment;
import vn.nip.around.Fragment.Gift.GiftingHomeFragment;
import vn.nip.around.Fragment.Gift.TabGiftingFragment;
import vn.nip.around.Fragment.Giftme.ShoppingFragment;
import vn.nip.around.Fragment.Payment.OnePayFragment;
import vn.nip.around.Fragment.Pickup.Confirm.ConfirmFragment;
import vn.nip.around.Fragment.SearchTag.DetailSearchFragment;
import vn.nip.around.Fragment.Shopping.HomeShoppingFragment;
import vn.nip.around.Fragment.Shopping.TabShoppingFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;

public class AppActivity extends BaseActivity {

    //region Variables
    public static String PICKUP = "PICKUP";
    public static String GIFTING = "GIFTING";
    public static String FLOW;
    public static int WINDOW_HEIGHT;
    public static int WINDOW_WIDTH;
    public static boolean IS_RECONNECT_SMARTFOX;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        GlobalClass.setActivity(this);
        StorageHelper.init(getApplicationContext());
        init();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WINDOW_HEIGHT = displayMetrics.heightPixels;
        WINDOW_WIDTH = displayMetrics.widthPixels;
        sendContact();
        updateToken();
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getWindow().setFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        popRoot();

    }

    @Override
    public void onBackPressed() {
        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            Fragment fragment = CmmFunc.getActiveFragment(AppActivity.this);
            //Selection
            if (fragment instanceof HomeFragment) {
                try {
                    CustomDialog.Dialog2Button(AppActivity.this, "", getString(R.string.message_exit), getString(R.string.ok), getString(R.string.cancel), new ICallback() {
                        @Override
                        public void excute() {
                            SmartFoxHelper.getInstance().disconnect();
                            finishAffinity();
                        }
                    }, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            if (fragment instanceof MatchFragment) {
                return;
            }

            if (fragment instanceof FollowJourneyFragment) {
                if (!fragment.getArguments().getBoolean("is_back")) {
                    return;
                }
            }

            if (fragment instanceof AroundWalletFragment) {
                ConfirmFragment confirmFragment = (ConfirmFragment) CmmFunc.getFragmentByTag(this, ConfirmFragment.class.getName());
                if (confirmFragment != null) {
                    this.getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                    confirmFragment.listPaymentFragment.onResume();
                    return;
                }

                CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(this, CartFragment.class.getName());
                if (cartFragment != null) {
                    this.getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                    cartFragment.listPaymentFragment.onResume();
                    return;
                }

            }

            if (fragment instanceof AroundPayWebviewFragment) {
                CustomDialog.Dialog2Button(AppActivity.this, "", getString(R.string.confirm_payment), getString(R.string.ok), getString(R.string.cancel), new ICallback() {
                    @Override
                    public void excute() {
                        FragmentHelper.pop(AppActivity.this, AroundWalletFragment.class.getName());
                    }
                }, null);

                return;
            }


            if (fragment instanceof OnePayFragment) {
                CustomDialog.Dialog2Button(AppActivity.this, "", getString(R.string.confirm_payment), getString(R.string.ok), getString(R.string.cancel), new ICallback() {
                    @Override
                    public void excute() {
                        try {
                            if (AppActivity.FLOW == AppActivity.GIFTING) {
                                CartFragment cartFragment = (CartFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), CartFragment.class.getName());
                                cartFragment.orderID = 0;
                                cartFragment.hideProgress();
                                AppActivity.this.getSupportFragmentManager().popBackStackImmediate(CartFragment.class.getName(), 0);
                            } else if (AppActivity.FLOW == AppActivity.PICKUP) {
                                ConfirmFragment fullOrderFragment = (ConfirmFragment) CmmFunc.getFragmentByTag(GlobalClass.getActivity(), ConfirmFragment.class.getName());
                                fullOrderFragment.orderID = 0;
                                fullOrderFragment.hideProgress();
                                AppActivity.this.getSupportFragmentManager().popBackStackImmediate(ConfirmFragment.class.getName(), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
                return;
            }

            if (fragment instanceof CallCenterFragment) {
                CallCenterFragment callCenterFragment = (CallCenterFragment) fragment;
                if (callCenterFragment.emoticionRecycler.getVisibility() == View.VISIBLE) {
                    callCenterFragment.emoticionRecycler.setVisibility(View.GONE);
                    return;
                }
            }

            if (fragment instanceof DetailSearchFragment) {
                try {
                    DetailSearchFragment detailSearchFragment = (DetailSearchFragment) fragment;
                    if (detailSearchFragment.getArguments().getString("type").equals(detailSearchFragment.PRODUCT_IN_CATEGORY) || detailSearchFragment.getArguments().getString("type").equals(detailSearchFragment.PRODUCT)) {
                        detailSearchFragment.view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                                    if (fragment instanceof TabShoppingFragment) {
                                        FragmentHelper.pop(AppActivity.this, TabShoppingFragment.class.getName());
                                        return;
                                    }

                                    if (fragment instanceof TabGiftingFragment) {
                                        FragmentHelper.pop(AppActivity.this, TabGiftingFragment.class.getName());
                                        return;
                                    }
                                    if (fragment instanceof HomeShoppingFragment) {
                                        FragmentHelper.pop(AppActivity.this, HomeShoppingFragment.class.getName());
                                        return;
                                    }
                                    if (fragment instanceof GiftingHomeFragment) {
                                        FragmentHelper.pop(AppActivity.this, GiftingHomeFragment.class.getName());
                                        return;
                                    }
                                }
                                AppActivity.popRoot();
                                return;
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (fragment instanceof BannerHomeFragment) {
                GlobalClass.getActivity().getSupportFragmentManager().popBackStack();
                return;
            }

            FragmentHelper.pop(AppActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void popRoot() {
        try {
            GlobalClass.getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, HomeFragment.newInstance());
            //FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, RateFragment.newInstance(2));
            IS_RECONNECT_SMARTFOX = false;
            SmartFoxHelper.getInstance().disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void sendContact() {
        if (ContextCompat.checkSelfPermission(AppActivity.this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                APIHelper.sendContact(getApplicationContext());
//                if (StorageHelper.getContact() == 0) {
//                    StorageHelper.setContact(System.currentTimeMillis());
//                    APIHelper.sendContact(getApplicationContext());
//                    return;
//                }
//                DateTime oldDate = new DateTime(StorageHelper.getContact());
//                DateTime now = new DateTime();
//                if (oldDate.plusDays(7).getMillis() <= now.getMillis()) {
//                    StorageHelper.setContact(System.currentTimeMillis());
//                    APIHelper.sendContact(getApplicationContext());
//                    return;
//                }

            }
        }).start();


    }

    private void updateToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String token = FirebaseInstanceId.getInstance().getToken();
                    Log.d("PUSH_TOKEN", token);
                    if (token != null) {
                        APIHelper.updateDeviceToken(token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IS_RECONNECT_SMARTFOX = true;
        Fragment fragment = CmmFunc.getActiveFragment(AppActivity.this);
        if (fragment instanceof FollowJourneyFragment) {
            SmartFoxHelper.initSmartFox();
        }
    }


}
