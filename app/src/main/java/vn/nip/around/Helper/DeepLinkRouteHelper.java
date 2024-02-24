package vn.nip.around.Helper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.apache.commons.lang.StringUtils;

import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Product.ProductFragment;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import static android.R.attr.id;

/**
 * Created by HOME on 09/04/2018.
 */

public class DeepLinkRouteHelper {
    public static final String PRODUCT = "product";
    public static final String RATING = "rating";
    public static final String COMMENT = "comment";
    private static DeepLinkRouteHelper instance;

    public static DeepLinkRouteHelper getInstance() {
        if (instance == null) {
            instance = new DeepLinkRouteHelper();
        }
        return instance;
    }


    private Uri createDynamicLink(Uri baseUri) {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(baseUri)
                .setDynamicLinkDomain("dze2k.app.goo.gl")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("vn.nip.around").setAppStoreId("1251779291").build())

                .buildDynamicLink();
        Uri result = dynamicLink.getUri();
        return result;
    }

    public void createDialogShareLink(Object... objects) {
        if (!appInstalledOrNot(GlobalClass.getActivity().getString(R.string.facebook_pakage_name))) {
            try {
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                messageDialogFragment.setMessage(GlobalClass.getActivity().getString(R.string.no_install_facebook));
                messageDialogFragment.setOutToHide(true);

                messageDialogFragment.setCallback(new ICallback() {
                    @Override
                    public void excute() {
                        try {
                            GlobalClass.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + GlobalClass.getActivity().getString(R.string.facebook_pakage_name))));
                        } catch (Exception anfe) {
                            GlobalClass.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + GlobalClass.getActivity().getString(R.string.facebook_pakage_name))));
                        }
                    }
                });
                messageDialogFragment.show(GlobalClass.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        String url = "http://shop.around.vn/share.php";
        String type = (String) objects[0];
        switch (type) {
            case PRODUCT:
                url += "?id=" + objects[1];
                url += "&type=" + type;
                url += "&product_name=" + objects[2];
                break;
            case RATING:
                url += "?type=" + type;
                url += "&level=" + objects[1];
                break;
        }
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(url))
                .setDynamicLinkDomain("dze2k.app.goo.gl")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("vn.nip.around").setAppStoreId("1251779291").build())
                .buildDynamicLink();

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(dynamicLink.getUri())
                .build();
        ShareDialog shareDialog = new ShareDialog(GlobalClass.getActivity());
        if (shareDialog.canShow(content)) {
            shareDialog.show(content, ShareDialog.Mode.NATIVE);
        }


    }


    public void createDialogSharePhoto(int id, String type) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("http")
                .authority("around.vn")
                .appendQueryParameter("id", id + "")
                .appendQueryParameter("type", type).build();
        Uri shortUri = DeepLinkRouteHelper.getInstance().createDynamicLink(uri);
        SharePhoto.Builder builder1 = new SharePhoto.Builder();
        builder1.setBitmap(BitmapFactory.decodeResource(GlobalClass.getActivity().getResources(),
                R.drawable.ic_call_now));
        SharePhoto sharePhoto = builder1.build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(sharePhoto)
                .build();
        ShareDialog shareDialog = new ShareDialog(GlobalClass.getActivity());
        if (shareDialog.canShow(content)) {
            shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
        }
    }

    public void route(Uri uri) {
        String type = uri.getQueryParameter("type");
        if (type != null) {
            switch (type) {
                case PRODUCT:
                    FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, ProductFragment.newInstance(Integer.parseInt(uri.getQueryParameter("id")), uri.getQueryParameter("product_name")));
                    break;
                case RATING:
                    break;
                case COMMENT:
                    break;
            }
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = GlobalClass.getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
