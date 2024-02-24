package vn.nip.around.Fragment.Shopping;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Adapter.HomeShoppingAdapter;
import vn.nip.around.Bean.BeanMainGiftingCategory;
import vn.nip.around.Bean.BeanShoppingCategory;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeShoppingFragment extends BaseFragment {

    ImageButton cart;
    TextView numberCart;

    public HomeShoppingFragment() {
        // Required empty public constructor
    }

    public static HomeShoppingFragment newInstance() {

        Bundle args = new Bundle();

        HomeShoppingFragment fragment = new HomeShoppingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static HomeShoppingFragment newInstance(int idTab, int subId) {

        Bundle args = new Bundle();
        args.putInt("id_select", idTab);
        args.putInt("sub_id", subId);
        HomeShoppingFragment fragment = new HomeShoppingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home_shopping, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cart = (ImageButton) view.findViewById(R.id.cart);
        numberCart = (TextView) view.findViewById(R.id.number_cart);
        //getNumberCart(cart, numberCart);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            try {
                final TextView title = (TextView) view.findViewById(R.id.title);
                title.setText(getString(R.string.shopping));
                threadInit = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(CmmVariable.SLEEP);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new ActionGet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                threadInit.start();
                isLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ActionGet extends ActionAsync {
        List<BeanShoppingCategory> items;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                String response = APIHelper.getShoppingMainCategory();
                jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    items = (List<BeanShoppingCategory>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanShoppingCategory.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
                            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                            HomeShoppingAdapter adapter = new HomeShoppingAdapter(HomeShoppingFragment.this, recycler, items);
                            recycler.setAdapter(adapter);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            hideProgress();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (getArguments().containsKey("id_select")) {
                            BeanShoppingCategory bean = BeanShoppingCategory.getById(getArguments().getInt("id_select"), items);
                            if (bean != null) {
                                TabShoppingFragment tabShoppingFragment = TabShoppingFragment.newInstance(CmmFunc.tryParseObject(bean), getArguments().getInt("sub_id"));
                                FragmentHelper.addFragment(getActivity(), R.id.home_content, tabShoppingFragment);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
