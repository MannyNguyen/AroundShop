package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import vn.nip.around.Adapter.OrderHistoryAdapter;
import vn.nip.around.Bean.BeanOrder;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
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
public class OrderHistoryFragment extends BaseFragment {
    public FrameLayout framePan;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }


    //region Override
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_history, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getResources().getString(R.string.order_history));
            framePan = (FrameLayout) view.findViewById(R.id.frame_pan);
            init();
        }
    }

    private void init() {
        if (!isLoaded) {
            new ActionGetOrderHistoty().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            isLoaded = true;
        }
    }

    //endregion

    //region Actions
    class ActionGetOrderHistoty extends ActionAsync {
        List<BeanOrder> items = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(Object... voids) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("type", "HISTORY"));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_order_info", params);
                JSONObject jsonObject = new JSONObject(response);
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        BeanOrder bean = (BeanOrder) CmmFunc.tryParseJson(jsonArray.getString(i), BeanOrder.class);
                        items.add(bean);
                    }
                }
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "ActionGetOrderHistoty", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            CmmFunc.setDelay(400, new ICallback() {
                @Override
                public void excute() {
                    try {
                        int code = jsonObject.getInt("code");
                        if (code == 1) {
                            if (items.size() > 0) {
                                RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
                                recycler.setVisibility(View.VISIBLE);
                                layoutProgress = (FrameLayout) view.findViewById(R.id.layout_progress);
                                OrderHistoryAdapter adapter = new OrderHistoryAdapter(OrderHistoryFragment.this, recycler, items);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                recycler.setLayoutManager(layoutManager);
                                recycler.setAdapter(adapter);
                            } else {
                                TextView status = (TextView) view.findViewById(R.id.status);
                                status.setVisibility(View.VISIBLE);
                            }

                        }
                    } catch (Exception e) {
                        CmmFunc.showError(getClass().getName(), "ActionGetOrderHistoty", e.getMessage());
                    } finally {
                        layoutProgress.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
    //endregion


}
