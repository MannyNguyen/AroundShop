package vn.nip.around.Fragment.Banner;


import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerOneFragment extends BaseFragment implements View.OnClickListener {

    Thread init;
    BeanBannerItem beanBannerItem;

    public static BannerOneFragment newInstance(String beanBannerItem) {

        Bundle args = new Bundle();
        args.putString("data", beanBannerItem);
        BannerOneFragment fragment = new BannerOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public BannerOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_banner_one, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init = new Thread(new Runnable() {
            @Override
            public void run() {
                beanBannerItem = (BeanBannerItem) CmmFunc.tryParseJson(getArguments().getString("data"), BeanBannerItem.class);
                final BeanProduct beanProduct = beanBannerItem.getProduct();
                final TextView titleBanner = (TextView) view.findViewById(R.id.title_banner);
                final TextView startDate = (TextView) view.findViewById(R.id.start_date);
                final TextView endDate = (TextView) view.findViewById(R.id.end_date);
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                final LocalDateTime start = LocalDateTime.parse(beanBannerItem.getStart_date(), df);
                final LocalDateTime end = LocalDateTime.parse(beanBannerItem.getEnd_date(), df);
                final ImageView imageView = (ImageView) view.findViewById(R.id.image);
                final TextView sale = (TextView) view.findViewById(R.id.sale);
                final TextView title = (TextView) view.findViewById(R.id.title);
                final TextView price = (TextView) view.findViewById(R.id.price);
                final TextView oldPrice = (TextView) view.findViewById(R.id.old_price);
                final TextView description = (TextView) view.findViewById(R.id.description);
                view.findViewById(R.id.buy_now).setOnClickListener(BannerOneFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            titleBanner.setText(CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_title)) + "");
                            startDate.setText(start.getDayOfMonth() + "/" + start.getMonthOfYear());
                            endDate.setText(end.getDayOfMonth() + "/" + end.getMonthOfYear());
                            Picasso.with(getActivity()).load(beanProduct.getImage()).into(imageView);
                            if (beanProduct.getSave_percent() == 0) {
                                view.findViewById(R.id.sale_container).setVisibility(View.GONE);
                            } else {
                                sale.setText(beanProduct.getSave_percent() + "%");
                            }
                            title.setText(beanProduct.getName() + "");
                            price.setText(CmmFunc.formatMoney(beanProduct.getPrice()));
                            if (beanProduct.getOld_price() == 0) {
                                oldPrice.setVisibility(View.GONE);
                            } else {
                                oldPrice.setText(CmmFunc.formatMoney(beanProduct.getOld_price()));
                                oldPrice.setPaintFlags(oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }

                            description.setText(CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_description)) + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        init.start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (init != null) {
            init.interrupt();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_now:
                new ActionAddCart().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, beanBannerItem.getProduct().getId());
                break;
        }
    }

    class ActionAddCart extends ActionAsync {

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
                params.add(new AbstractMap.SimpleEntry("number", 1 + ""));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/add_product_to_cart", params);
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
                    FragmentHelper.pop(getActivity());
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, CartFragment.newInstance());
                }
            } catch (Exception e) {

            }
        }
    }
}
