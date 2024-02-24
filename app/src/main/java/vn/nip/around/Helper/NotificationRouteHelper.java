package vn.nip.around.Helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.AroundWalletFragment;
import vn.nip.around.Fragment.Common.FollowJourneyFragment;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Fragment.Common.NoticesFragment;
import vn.nip.around.Fragment.Gift.GiftingHomeFragment;
import vn.nip.around.Fragment.Giftme.ShoppingFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Fragment.Shopping.HomeShoppingFragment;
import vn.nip.around.Fragment.Shopping.TabShoppingFragment;
import vn.nip.around.R;
import vn.nip.around.Util.NotificationUtils;

/**
 * Created by HOME on 04/04/2018.
 */

public class NotificationRouteHelper {

    final String HOME_SCRREEN = "HomeScreen";
    final String EGIFT = "EGift";
    final String CAT_GIFT = "CategoriesGifting";
    final String CAT_SHOP = "CategoriesShopping";
    final String NOTICE = "ThongBao_CuaToi";
    final String EVENT = "ThongBao_SuKien";
    final String FOLLOW = "FollowJourney";
    final String WALLET = "ViAround";
    final String DETAILSCREEN = "DetailScreen";

    Context context;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public NotificationRouteHelper() {

    }

    public NotificationRouteHelper(Context context) {
        this.context = context;
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(NotificationUtils.REGISTRATION_COMPLETE)) {
                    //FirebaseMessaging.getInstance().subscribeToTopic(NotificationUtils.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(NotificationUtils.PUSH_NOTIFICATION)) {
                    String type = intent.getStringExtra("type");
                }
            }
        };
    }

    public void register() {
        if (mRegistrationBroadcastReceiver == null) {
            return;
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationUtils.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NotificationUtils.PUSH_NOTIFICATION));
        //NotificationUtils.clearNotifications(context);
    }

    public void unRegister() {
        if (mRegistrationBroadcastReceiver == null) {
            return;
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void route(FragmentActivity activity, Bundle bundle) {
        String type = bundle.getString("type");

        switch (type) {
            case HOME_SCRREEN:
                break;
            case EGIFT:
                break;
            case CAT_GIFT:
                int sId = bundle.getInt("subId");
                FragmentHelper.addFragment(activity, R.id.home_content, GiftingHomeFragment.newInstance(sId));
                break;
            case CAT_SHOP: // sub_id
                int idSelect = Integer.parseInt(bundle.getString("id"));
                int subId = Integer.parseInt(bundle.getString("subId"));
                FragmentHelper.addFragment(activity, R.id.home_content, HomeShoppingFragment.newInstance(idSelect, subId));
                break;
            case NOTICE:
                FragmentHelper.addFragment(activity, R.id.home_content, NoticesFragment.newInstance(0));
                break;
            case EVENT:
                FragmentHelper.addFragment(activity, R.id.home_content, NoticesFragment.newInstance(1));
                break;
            case FOLLOW:
                int idFollow = Integer.parseInt(bundle.getString("id"));
                FragmentHelper.addFragment(activity, R.id.home_content, FollowJourneyFragment.newInstance(idFollow, true));
                break;
            case WALLET:
                FragmentHelper.addFragment(activity, R.id.home_content, AroundWalletFragment.newInstance());
                break;
            case DETAILSCREEN:
                int idProduct = Integer.parseInt(bundle.getString("id"));
                FragmentHelper.addFragment(activity, R.id.home_content, ProductFragment.newInstance(idProduct, ""));
                break;
        }
        activity.getIntent().removeExtra("type");
    }
}
