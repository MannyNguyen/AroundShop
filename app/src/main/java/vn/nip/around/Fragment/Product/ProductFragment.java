package vn.nip.around.Fragment.Product;


import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import vn.nip.around.Adapter.AttributeAdapter;
import vn.nip.around.Adapter.ImagePagerAdapter;
import vn.nip.around.Adapter.ImagePolicyAdapter;
import vn.nip.around.Adapter.ProductAdapter;
import vn.nip.around.Bean.BeanAttribute;
import vn.nip.around.Bean.BeanImage;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DeepLinkRouteHelper;
import vn.nip.around.Helper.ErrorHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductFragment extends BaseFragment implements View.OnClickListener {

    //region Variables
    public boolean isRate;
    int currentAttribute = 0;
    List<BeanAttribute> attributes = new ArrayList<>();
    List<BeanProduct> products = new ArrayList<>();
    int pageProduct = 1;
    ImageButton cart;
    TextView numberCart, description;
    View container;
    //endregion

    //region Contructors
    public ProductFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init

    public static ProductFragment newInstance(int id, String title) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("title", title);
        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_product, container, false);
        }
        return view;
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        container = view.findViewById(R.id.container);
        //RatingBar rating_12 = (RatingBar) view.findViewById(R.id.rating_12);
        //LayerDrawable stars = (LayerDrawable) rating_12.getProgressDrawable();
        //stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.main), PorterDuff.Mode.SRC_ATOP);
        //stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.gray_400), PorterDuff.Mode.SRC_ATOP);
        if (!isLoaded) {
            CmmFunc.setDelay(400, new ICallback() {
                @Override
                public void excute() {
                    try {
                        cart = (ImageButton) view.findViewById(R.id.cart);
                        numberCart = (TextView) view.findViewById(R.id.number_cart);
                        description= (TextView) view.findViewById(R.id.description);
                        new ActionGetDetail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));

                        //getNumberCart(cart, numberCart);

                        CardView addToCart = (CardView) view.findViewById(R.id.add_to_cart);
                        addToCart.setOnClickListener(ProductFragment.this);

                        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_relate);
                        ProductAdapter adapter = new ProductAdapter(ProductFragment.this, recyclerView, products, ProductAdapter.RELATE);
                        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                if (layoutManager.findLastCompletelyVisibleItemPosition() == products.size() - 1 && products.size() > 0) {
                                    new ActionGetRelate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));
                                }
                            }
                        });

                        Button allComment = (Button) view.findViewById(R.id.all_comment);
                        allComment.setOnClickListener(ProductFragment.this);
                        view.findViewById(R.id.comment_area).setOnClickListener(ProductFragment.this);
                        new ActionGetRelate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();
        new ActionGetDetail().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));
    }


    //region Events

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_to_cart:
                try {
                    Tracking.excute("C14.4Y");
                    JSONArray atts = new JSONArray();
                    for (BeanAttribute att : attributes) {
                        List<BeanAttribute.BeanData> items = att.getData();
                        for (BeanAttribute.BeanData item : items) {
                            if (item.isCheck()) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id_attribute", att.getId_attribute());
                                jsonObject.put("id_data", item.getId_data());
                                atts.put(jsonObject);
                            }
                        }
                    }
                    new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"), atts);
                } catch (Exception e) {

                }
                break;
//            case R.id.next_image:
//                try {
//                    Tracking.excute("C14.1Y");
//                    ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager);
//                    pager.setCurrentItem(pager.getCurrentItem() + 1);
//                } catch (Exception e) {
//
//                }
//                break;
//            case R.id.previous_image:
//                try {
//                    Tracking.excute("C14.1Y");
//                    ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager);
//                    pager.setCurrentItem(pager.getCurrentItem() - 1);
//                } catch (Exception e) {
//
//                }
//                break;
            case R.id.btn_like:
                new ActionLike().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));
                break;

            case R.id.btn_share:
                DeepLinkRouteHelper.getInstance().createDialogShareLink(DeepLinkRouteHelper.PRODUCT, getArguments().getInt("id"), getArguments().getString("title"));
                break;

            case R.id.buy_now:
                try {
                    Tracking.excute("C14.3Y");
                    JSONArray atts = new JSONArray();
                    for (BeanAttribute att : attributes) {
                        List<BeanAttribute.BeanData> items = att.getData();
                        for (BeanAttribute.BeanData item : items) {
                            if (item.isCheck()) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id_attribute", att.getId_attribute());
                                jsonObject.put("id_data", item.getId_data());
                                atts.put(jsonObject);
                            }
                        }
                    }
                    JSONObject data = new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"), atts).get();
                    int code = data.getInt("code");
                    if (code == 1) {
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, new CartFragment());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cart:
                Tracking.excute("C14.5Y");
                FragmentHelper.addFragment(getActivity(), R.id.home_content, CartFragment.newInstance());
                break;

            case R.id.all_comment:
            case R.id.comment_area:
                Fragment fragment = CommentProductFragment.newInstance(getArguments().getInt("id"), isRate);
                FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                break;

            case R.id.read_more:
                view.findViewById(R.id.read_more).setVisibility(View.INVISIBLE);
//                TextView description = (TextView) view.findViewById(R.id.description);
                description.setEllipsize(null);
                description.setMaxLines(Integer.MAX_VALUE);
                break;
        }
    }
    //endregion

    //region Actions
    class ActionAddCart extends AsyncTask<Object, Void, JSONObject> {
        int productID;

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                productID = (int) objects[0];
                JSONArray attributes = (JSONArray) objects[1];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                params.add(new AbstractMap.SimpleEntry("number", 1 + ""));
                params.add(new AbstractMap.SimpleEntry("attributes", attributes.toString()));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/add_product_to_cart", params, false);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject == null) {
                    return;
                }
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    getNumberCart(cart, numberCart);
                    if (CartFragment.RECEIVER != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                APIHelper.updateDropInfo(CartFragment.RECEIVER.getFullname(), CartFragment.RECEIVER.getPhone(), "");
                                CartFragment.RECEIVER = null;
                            }
                        }).start();
                    }
                } else {
                    new ErrorHelper().excute(jsonObject);
                }
            } catch (Exception e) {

            }
        }
    }

    class ActionGetDetail extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_product_detail", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                if (jsonObject == null) {
                    return;
                }
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    TextView title = (TextView) view.findViewById(R.id.title);
                    title.setText(data.getString("name") + "");
                    TextView name = (TextView) view.findViewById(R.id.name);
                    name.setText(data.getString("name") + "");
                    TextView price = (TextView) view.findViewById(R.id.new_price);
                    price.setText(CmmFunc.formatMoney(data.getString("price"), true));
                    TextView oldPrice = (TextView) view.findViewById(R.id.old_price);
                    if (data.getInt("old_price") == 0) {
                        oldPrice.setVisibility(View.GONE);
                    } else {
                        oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        oldPrice.setText(CmmFunc.formatMoney(data.getInt("old_price") + "", true));
                    }

                    if (data.getInt("prepare_time") == 0) {
                        view.findViewById(R.id.prepare_container).setVisibility(View.GONE);
                    } else {
                        view.findViewById(R.id.prepare_container).setVisibility(View.VISIBLE);
                        TextView prepareTime = (TextView) view.findViewById(R.id.prepare_time);
                        prepareTime.setText(data.getInt("prepare_time") + " " + getString(R.string.hour));
                    }

                    isRate = false;
                    if (data.getBoolean("is_rate")) {
                        isRate = true;
                    }
                    LinearLayout ratingContainer = (LinearLayout) view.findViewById(R.id.rating_container);
                    ratingContainer.removeAllViews();
                    for (int i = 0; i < data.getInt("rating"); i++) {
                        ImageView img = new ImageView(getActivity());
                        int pad = CmmFunc.convertDpToPx(getActivity(), 4);
                        img.setPadding(pad, pad, pad, pad);
                        Picasso.with(getActivity()).load(R.drawable.ic_star_16_main).into(img);
                        ratingContainer.addView(img);
                    }
                    for (int i = 0; i < 5 - data.getInt("rating"); i++) {
                        ImageView img = new ImageView(getActivity());
                        int pad = CmmFunc.convertDpToPx(getActivity(), 4);
                        img.setPadding(pad, pad, pad, pad);
                        Picasso.with(getActivity()).load(R.drawable.ic_star_16_gray).into(img);
                        ratingContainer.addView(img);
                    }

                    TextView totalRating = (TextView) view.findViewById(R.id.total_rating);
                    totalRating.setText("(" + data.getString("total_rating") + ")");

                    int savePrice = data.getInt("save_price");
                    if (savePrice > 0) {
                        view.findViewById(R.id.save_area).setVisibility(View.VISIBLE);
                        String saveValue = CmmFunc.formatMoney(savePrice) + " (" + data.getInt("save_percent") + "%)";
                        TextView save = (TextView) view.findViewById(R.id.save);
                        save.setText(saveValue);
                    }

                    ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager);
                    List<BeanImage> images = (List<BeanImage>) CmmFunc.tryParseList(data.getString("images"), BeanImage.class);
                    ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(getActivity(), images, R.layout.row_image_center_inside);
                    pager.setAdapter(imagePagerAdapter);
                    TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
                    if (images.size() > 1) {
                        tab.setupWithViewPager(pager);
                    } else {
                        tab.setVisibility(View.GONE);
                    }


                    TextView description = (TextView) view.findViewById(R.id.description);
                    description.setText(data.getString("description") + "");
                    View readMore = view.findViewById(R.id.read_more);
                    if (description.getLineCount() < 5) {
                        readMore.setVisibility(View.INVISIBLE);
                        description.setEllipsize(null);
                    } else {
                        description.setMaxLines(5);
                        description.setEllipsize(TextUtils.TruncateAt.END);
                        readMore.setVisibility(View.VISIBLE);
                        readMore.setOnClickListener(ProductFragment.this);
                    }


                    ImageButton btnLike = (ImageButton) view.findViewById(R.id.btn_like);
                    if (data.getBoolean("is_like")) {
                        btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_selected));
                    } else {
                        btnLike.setOnClickListener(ProductFragment.this);
                    }
                    TextView numberLike = (TextView) view.findViewById(R.id.number_like);
                    numberLike.setText(data.getString("total_like") + "");
                    TextView numberComment = (TextView) view.findViewById(R.id.number_comment);
                    if (!data.getString("total_comment").equals("0")){
                        numberComment.setVisibility(View.VISIBLE);
                        numberComment.setText(data.getString("total_comment") + "");
                    }
                    view.findViewById(R.id.btn_share).setOnClickListener(ProductFragment.this);

                    TextView shopName = (TextView) view.findViewById(R.id.shop_name);
                    shopName.setText(data.getString("shop_name") + "");

                    TextView textPolicy = (TextView) view.findViewById(R.id.text_policy);
                    textPolicy.setText(data.getJSONObject("text_policy").getString(getString(R.string.server_key_title)) + "");

                    List<JSONObject> imagePolicies = (List<JSONObject>) CmmFunc.tryParseList(data.getString("image_policy"), JSONObject.class);
                    RecyclerView recyclerPolicy = (RecyclerView) view.findViewById(R.id.recipent_policy);
                    LinearLayoutManager layoutManagerPolicy = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
                    ImagePolicyAdapter adapterPolicy = new ImagePolicyAdapter(getActivity(), recyclerPolicy, imagePolicies);
                    recyclerPolicy.setLayoutManager(layoutManagerPolicy);
                    recyclerPolicy.setAdapter(adapterPolicy);

                    CardView buyNow = (CardView) view.findViewById(R.id.buy_now);
                    buyNow.setOnClickListener(ProductFragment.this);

                    attributes = (List<BeanAttribute>) CmmFunc.tryParseList(data.getString("attributes"), BeanAttribute.class);
                    if (attributes.size() == 0) {
                        view.findViewById(R.id.attribute_container).setVisibility(View.GONE);
                    }
                    RecyclerView recyclerAttribute = (RecyclerView) view.findViewById(R.id.recycler_attribute);
                    AttributeAdapter attributeAdapter = new AttributeAdapter(getActivity(), recyclerAttribute, attributes);
                    LinearLayoutManager attributeLayout = new LinearLayoutManager(getActivity());
                    recyclerAttribute.setLayoutManager(attributeLayout);
                    recyclerAttribute.setAdapter(attributeAdapter);
                    recyclerAttribute.setMinimumHeight(CmmFunc.convertDpToPx(getActivity(), 56) * attributes.size());

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ActionLike extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/like_product", params, false);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetCategory", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    ImageButton btnLike = (ImageButton) view.findViewById(R.id.btn_like);
                    btnLike.setOnClickListener(null);
                    btnLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_selected));
                    TextView numberLike = (TextView) view.findViewById(R.id.number_like);
                    int currentNumber = Integer.parseInt(numberLike.getText().toString());
                    numberLike.setText((currentNumber + 1) + "");
                }
            } catch (Exception e) {

            } finally {
                hideProgress();
            }
        }
    }

    class ActionGetRelate extends ActionAsync {
        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                params.add(new AbstractMap.SimpleEntry("page", pageProduct + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_related_product", params);
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetCategory", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    List<BeanProduct> pros = (List<BeanProduct>) CmmFunc.tryParseList(jsonObject.getJSONObject("data").getString("products"), BeanProduct.class);
                    for (BeanProduct bean : pros) {
                        products.add(bean);
                    }
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_relate);
                    recyclerView.getAdapter().notifyDataSetChanged();
                    pageProduct += 1;
                }
            } catch (Exception e) {

            } finally {

            }
        }
    }
    //endregion
}
