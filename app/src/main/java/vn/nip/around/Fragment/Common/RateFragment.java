package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import vn.nip.around.Adapter.RateAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanRate;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.DeepLinkRouteHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.SmartFoxHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import com.smartfoxserver.v2.entities.data.SFSObject;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.requests.ExtensionRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class RateFragment extends BaseFragment implements View.OnClickListener {

    //region Variavles
    View view;
    public View confirm;
    SeekBar seekBar;
    List<BeanRate> rates = new ArrayList<>();
    //endregion

    //region Contructors
    public RateFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_rate, container, false);
            confirm = view.findViewById(R.id.confirm);
            confirm.setOnClickListener(this);
            view.findViewById(R.id.share).setOnClickListener(this);
        }

        return view;
    }

    public static RateFragment newInstance(int orderID) {

        Bundle args = new Bundle();
        args.putInt("order_id", orderID);
        RateFragment fragment = new RateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isLoaded == false) {
            init();
        }
    }

    private void init() {
        CmmFunc.setDelay(400, new ICallback() {
            @Override
            public void excute() {
                seekBar = (SeekBar) getView().findViewById(R.id.seek_bar);
                final ImageView thumb = (ImageView) getView().findViewById(R.id.thumb);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        Tracking.excute("C10.1Y");
                        boolean isClick = false;
                        switch (i) {
                            case 0:
                                animateChangeIcon(thumb, R.drawable.ic_rate_0);
                                for (BeanRate bean : rates) {
                                    if (bean.isCheck()) {
                                        isClick = true;
                                    }
                                }
                                if (isClick) {
                                    confirm.setAlpha(1);
                                    confirm.setOnClickListener(RateFragment.this);
                                } else {
                                    confirm.setAlpha(0.3f);
                                    confirm.setOnClickListener(null);
                                }
                                break;
                            case 1:
                                animateChangeIcon(thumb, R.drawable.ic_rate_1);
                                for (BeanRate bean : rates) {
                                    if (bean.isCheck()) {
                                        isClick = true;
                                    }
                                }
                                if (isClick) {
                                    confirm.setAlpha(1);
                                    confirm.setOnClickListener(RateFragment.this);
                                } else {
                                    confirm.setAlpha(0.3f);
                                    confirm.setOnClickListener(null);
                                }
                                break;
                            case 2:
                                animateChangeIcon(thumb, R.drawable.ic_rate_2);
                                for (BeanRate bean : rates) {
                                    if (bean.isCheck()) {
                                        isClick = true;
                                    }
                                }
                                if (isClick) {
                                    confirm.setAlpha(1);
                                    confirm.setOnClickListener(RateFragment.this);
                                } else {
                                    confirm.setAlpha(0.3f);
                                    confirm.setOnClickListener(null);
                                }
                                break;
                            case 3:
                                animateChangeIcon(thumb, R.drawable.ic_rate_3);
                                confirm.setAlpha(1);
                                confirm.setOnClickListener(RateFragment.this);
                                break;
                            case 4:
                                animateChangeIcon(thumb, R.drawable.ic_rate_4);
                                confirm.setAlpha(1);
                                confirm.setOnClickListener(RateFragment.this);
                                break;
                        }

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                new ActionGetRateReason().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                isLoaded = true;
            }
        });
    }

    private void animateChangeIcon(final ImageView imageView, int source) {
        try {
            imageView.animate().scaleX(0.5f).scaleY(0.5f).setDuration(300).start();
            imageView.setImageDrawable(getResources().getDrawable(source));
            CmmFunc.setDelay(300, new ICallback() {
                @Override
                public void excute() {
                    imageView.animate().scaleX(1).scaleY(1).setDuration(300).start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    //endregion

    //region Events
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                try {
                    Tracking.excute("C10.7Y");
                    int idReason = 0;
                    for (BeanRate bean : rates) {
                        if (bean.isCheck()) {
                            idReason = bean.getId();
                            break;
                        }
                    }
                    final int start = seekBar.getProgress() + 1;
                    final int finalIdReason = idReason;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            APIHelper.rating(finalIdReason, start, getArguments().getInt("order_id"));
                        }
                    }).start();
                    AppActivity.popRoot();
                } catch (Exception e) {

                }
                break;
            case R.id.share:
                DeepLinkRouteHelper.getInstance().createDialogShareLink(DeepLinkRouteHelper.RATING, seekBar.getProgress() + 1);
                break;
        }
    }
    //endregion

    //region Methods
    private void rating(final int idReason, final int star) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putUtfString("command", "RATING");
                sfsObject.putInt("id_order", getArguments().getInt("order_id"));
                sfsObject.putInt("id_reason", idReason);
                sfsObject.putInt("star", star);
                SmartFoxHelper.getInstance().send(new ExtensionRequest("user", sfsObject));
            }
        }).start();
    }


    class ActionGetRateReason extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_rating_reason", params);
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
                    JSONObject data = jsonObject.getJSONObject("data");
                    String arrays = data.getString("reasons");
                    rates = (List<BeanRate>) CmmFunc.tryParseList(arrays, BeanRate.class);
                    RecyclerView recycler = (RecyclerView) getView().findViewById(R.id.recycler);
                    RateAdapter adapter = new RateAdapter(getActivity(), RateFragment.this, recycler, rates);
                    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2, StaggeredGridLayoutManager.VERTICAL, false);
                    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

                        @Override
                        public int getSpanSize(int position) {
                            return ((position + 1) % 3 == 0 ? 2 : 1);
                        }
                    });
                    recycler.setLayoutManager(layoutManager);
                    recycler.setAdapter(adapter);
                }
            } catch (Exception e) {

            } finally {
                hideProgress();
            }
        }
    }
    //endregion

}
