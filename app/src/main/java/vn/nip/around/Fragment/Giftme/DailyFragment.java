package vn.nip.around.Fragment.Giftme;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.DailyHistoryAdapter;
import vn.nip.around.Adapter.DailyProductAdapter;
import vn.nip.around.Bean.BeanDaily;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DailyFragment extends BaseFragment {

    RecyclerView recyclerDaily;
    RecyclerView recyclerProduct;
    int page = 1;
    AsyncTask actionGet;

    List<BeanDaily> orders = new ArrayList<>();
    List<BeanProduct> products = new ArrayList<>();
    DailyProductAdapter adapter;

    NestedScrollView scrollView;

    public DailyFragment() {
        // Required empty public constructor
    }

    public static DailyFragment newInstance() {
        Bundle args = new Bundle();
        DailyFragment fragment = new DailyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_daily, container, false);
        }
        return view;
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
                        scrollView = (NestedScrollView) view.findViewById(R.id.scroll_view);
                        recyclerDaily = (RecyclerView) view.findViewById(R.id.recycler_daily_history);
                        recyclerProduct = (RecyclerView) view.findViewById(R.id.recycler_daily_product);
                        adapter = new DailyProductAdapter(DailyFragment.this, recyclerProduct, products);

                        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                            @Override
                            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                                if (scrollY > oldScrollY) {
                                    //Log.i(TAG, "Scroll DOWN");
                                }
                                if (scrollY < oldScrollY) {
                                    //Log.i(TAG, "Scroll UP");
                                }

                                if (scrollY == 0) {
                                    //Log.i(TAG, "TOP SCROLL");
                                }

                                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                                    Log.i("BOTTOM", "BOTTOM SCROLL");
                                    if (!actionGet.isCancelled()) {
                                        return;
                                    }
                                    page++;
                                    actionGet = new ActionGetGiftMe();
                                    actionGet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }
                        });
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerDaily.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerProduct.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recyclerProduct.setAdapter(adapter);
                                actionGet = new ActionGetGiftMe();
                                actionGet.execute();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    class ActionGetGiftMe extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            try {
                final String retValue = APIHelper.getGiftMe(page, "DAILY");
                JSONObject jsonObject = new JSONObject(retValue);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (page == 1) {
                        orders = (List<BeanDaily>) CmmFunc.tryParseList(data.getString("orders"), BeanDaily.class);
                        final DailyHistoryAdapter dailyHistoryAdapter = new DailyHistoryAdapter(DailyFragment.this, recyclerDaily, orders);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    recyclerDaily.setAdapter(dailyHistoryAdapter);
                                    recyclerDaily.setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.more_container).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.history_container).setVisibility(View.GONE);
                                    if (orders.size() > 0) {
                                        view.findViewById(R.id.history_container).setVisibility(View.VISIBLE);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    JSONArray arr = data.getJSONArray("products");
                    for (int i = 0; i < arr.length(); i++) {
                        BeanProduct bean = (BeanProduct) CmmFunc.tryParseJson(arr.getString(i), BeanProduct.class);
                        products.add(bean);
                    }
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
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    recyclerProduct.getAdapter().notifyDataSetChanged();
                    recyclerProduct.setVisibility(View.VISIBLE);
                    this.cancel(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }
}
