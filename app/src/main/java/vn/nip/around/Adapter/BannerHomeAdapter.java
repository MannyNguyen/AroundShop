package vn.nip.around.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Bean.BeanHomeBanner;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.HomeFragment;
import vn.nip.around.Fragment.Gift.GiftingHomeFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Pickup.MapCODFragment;
import vn.nip.around.Fragment.Product.ProductsFragment;
import vn.nip.around.Fragment.Giftme.ShoppingFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Product.ShopProductFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.LoginActivity;
import vn.nip.around.R;

/**
 * Created by viminh on 10/4/2016.
 */

public class BannerHomeAdapter extends PagerAdapter {

    final String SHOPPING = "shopping";
    final String GIFTING = "gifting";
    final String EGIFT = "egift";
    final String TRANSPORT = "transport";
    final String PURCHASE = "purchase";
    final String COD = "cod";


    Fragment fragment;
    LayoutInflater mLayoutInflater;
    List<BeanHomeBanner> list;

    public BannerHomeAdapter() {

    }

    public BannerHomeAdapter(Fragment fragment, List<BeanHomeBanner> list) {
        this.fragment = fragment;
        mLayoutInflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_image, container, false);
        final BeanHomeBanner item = list.get(position);
        if (item != null) {
            try {
                ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
                Picasso.with(fragment.getActivity()).load(item.getClass().getDeclaredField(fragment.getString(R.string.key_image)).get(item) + "").into(imageView);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        container.addView(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
                    Intent intent = new Intent(fragment.getActivity(), LoginActivity.class);
                    fragment.startActivity(intent);
                    return;
                }

                switch (item.getType()) {
                    case BeanHomeBanner.TYPE_NONE:
                        break;
                    case BeanHomeBanner.TYPE_PRODUCT:
                        new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item.getId_product());
                        break;
                    case BeanHomeBanner.TYPE_SHOPPING:
                        BeanCategory bean = new BeanCategory();
                        bean.setName(item.getCategory_name());
                        bean.setVn_name(item.getCategory_vnname());
                        bean.setId(item.getId_category());
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ProductsFragment.newInstance(CmmFunc.tryParseObject(bean)));
                        break;

                    case BeanHomeBanner.TYPE_GIFT:
//                        BeanCategory beanCategory = new BeanCategory();
//                        beanCategory.setName(item.getCategory_name());
//                        beanCategory.setVn_name(item.getCategory_vnname());
//                        beanCategory.setId(item.getId_category());
//                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ProductsFragment.newInstance(CmmFunc.tryParseObject(beanCategory)));
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, GiftingHomeFragment.newInstance());

                        break;

                    //Lấy sản phẩm theo shop
                    case BeanHomeBanner.TYPE_SHOP:
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ShopProductFragment.newInstance(item.getId_shop(), item.getShop_name()));
                        break;

                    case BeanHomeBanner.TYPE_SERVICE:
                        switch (item.getId_service()) {
                            case SHOPPING:
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, ShoppingFragment.newInstance());
                                break;
                            case GIFTING:
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, GiftingHomeFragment.newInstance());
                                break;
                            case EGIFT:
                                //FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, GiftHomeFragment.newInstance());
                                break;

                            case COD:
                                try {
                                    if (item.getLocation().equals(StringUtils.EMPTY)) {
                                        List<BeanPoint> points = new ArrayList<>();
                                        BeanPoint beanPoint = new BeanPoint();
                                        beanPoint.setPickup_type(BeanPoint.DROP);
                                        points.add(beanPoint);

                                        BeanPoint drop = new BeanPoint();
                                        drop.setPickup_type(BeanPoint.COD);
                                        points.add(drop);
                                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(points)));
                                    } else{
                                        List<BeanPoint> points = new ArrayList<>();
                                        BeanPoint drop = new BeanPoint();
                                        drop.setPickup_type(BeanPoint.COD);
                                        points.add(drop);

                                        points.add((BeanPoint) CmmFunc.tryParseJson(item.getLocation(), BeanPoint.class));
                                        points.add(new BeanPoint());
                                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(points)));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;

                            case PURCHASE:
                                try {
                                    if (item.getLocation().equals(StringUtils.EMPTY)) {
                                        List<BeanPoint> points = new ArrayList<BeanPoint>();
                                        BeanPoint beanPoint = new BeanPoint();
                                        beanPoint.setPickup_type(BeanPoint.PURCHASE);
                                        points.add(beanPoint);
                                        points.add(new BeanPoint());
                                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(points),
                                                GlobalClass.getActivity().getString(R.string.purchase)));
                                    } else{
                                        List<BeanPoint> points = new ArrayList<>();
                                        points.add((BeanPoint) CmmFunc.tryParseJson(item.getLocation(), BeanPoint.class));
                                        points.add(new BeanPoint());
                                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(points),
                                                GlobalClass.getActivity().getString(R.string.purchase)));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                            case TRANSPORT:
                                try {
                                    if (item.getLocation().equals(StringUtils.EMPTY)) {
                                        List<BeanPoint> points = new ArrayList<BeanPoint>();
                                        BeanPoint beanPoint = new BeanPoint();
                                        beanPoint.setPickup_type(BeanPoint.TRANSPORT);
                                        points.add(beanPoint);
                                        points.add(new BeanPoint());
                                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(points),
                                                GlobalClass.getActivity().getString(R.string.transport)));
                                    } else{
                                        List<BeanPoint> points = new ArrayList<>();
                                        points.add((BeanPoint) CmmFunc.tryParseJson(item.getLocation(), BeanPoint.class));
                                        points.add(new BeanPoint());
                                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(points),
                                                GlobalClass.getActivity().getString(R.string.transport)));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                        break;
                }
            }
        });
        return itemView;
    }

    class ActionAddCart extends ActionAsync {

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
                    HomeFragment homeFragment = (HomeFragment) fragment;
                    homeFragment.getNumberCart(homeFragment.cart, homeFragment.numberCart);
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, CartFragment.newInstance());

                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

