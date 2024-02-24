package vn.nip.around.Fragment.Banner;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Custom.RoundedTransformation;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerImageFragment extends BaseFragment implements View.OnClickListener {

    BeanBannerItem beanBannerItem;

    public BannerImageFragment() {
        // Required empty public constructor
    }

    public static BannerImageFragment newInstance(int type, String data) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("data", data);
        BannerImageFragment fragment = new BannerImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_banner_image, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            beanBannerItem = (BeanBannerItem) CmmFunc.tryParseJson(getArguments().getString("data"), BeanBannerItem.class);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            Picasso.with(getActivity()).load(CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_image)) + "").transform(new RoundedTransformation(CmmFunc.convertDpToPx(getActivity(), 4), 0)).into(image);
                if (getArguments().getInt("type") == 1) {
                image.setOnClickListener(BannerImageFragment.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
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
