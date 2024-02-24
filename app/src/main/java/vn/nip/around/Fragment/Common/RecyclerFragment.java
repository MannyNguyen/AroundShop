package vn.nip.around.Fragment.Common;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.EventAdapter;
import vn.nip.around.Adapter.NoticeAdapter;
import vn.nip.around.Bean.BeanEvent;
import vn.nip.around.Bean.BeanNotice;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerFragment extends BaseFragment {

    public static final String ME = "ME";
    public static final String EVENT = "EVENT";
    List<BeanEvent> events;
    List<BeanNotice> notices;
    Thread init;
    RecyclerView recycler;
    SwipeRefreshLayout refreshLayout;
    int page = 1;

    public RecyclerFragment() {
        // Required empty public constructor
    }

    public static RecyclerFragment newInstance(String instance) {

        Bundle args = new Bundle();
        args.putString("instance", instance);
        RecyclerFragment fragment = new RecyclerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_recycler, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recycler.setLayoutManager(layoutManager);
            switch (getArguments().getString("instance")) {
                case ME:
                    notices = new ArrayList<>();
                    NoticeAdapter noticeAdapter = new NoticeAdapter(getActivity(), recycler, notices);
                    recycler.setAdapter(noticeAdapter);
                    new ActionGetNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
                    break;
                case EVENT:
                    events = new ArrayList<>();
                    EventAdapter eventAdapter = new EventAdapter(getActivity(), this, recycler, events);
                    recycler.setAdapter(eventAdapter);
                    new ActionGetEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
                    break;
            }

            refreshLayout.setRefreshing(false);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
                        switch (getArguments().getString("instance")) {
                            case ME:
                                notices.clear();
                                page = 1;
                                new ActionGetNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
                                break;
                            case EVENT:
                                events.clear();
                                page = 1;
                                new ActionGetEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
                                break;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int size = 0;
                    switch (getArguments().getString("instance")) {
                        case ME:
                            if (notices != null) {
                                size = notices.size();
                            }
                            if (layoutManager.findLastCompletelyVisibleItemPosition() == size - 1 && size > 0) {
                                page += 1;
                                new ActionGetNotification().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
                            }
                            break;
                        case EVENT:
                            if (events != null) {
                                size = events.size();
                            }
                            if (layoutManager.findLastCompletelyVisibleItemPosition() == size - 1 && size > 0) {
                                page += 1;
                                new ActionGetEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page);
                            }
                            break;
                    }

                }
            });
            isLoaded = true;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (init != null) {
            init.interrupt();
        }
    }

    //region Actions
    class ActionGetNotification extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int page = (int) params[0];
                String value = APIHelper.getNotification(page);
                jsonObject = new JSONObject(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    notices.addAll((List<BeanNotice>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanNotice.class));
                    recycler.getAdapter().notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                refreshLayout.setRefreshing(false);
                recycler.getAdapter().notifyDataSetChanged();
            }

        }
    }

    class ActionGetEvent extends ActionAsync {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            JSONObject jsonObject = null;
            try {
                int page = (int) params[0];
                String value = APIHelper.getEvent(page);
                jsonObject = new JSONObject(value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    events.addAll((List<BeanEvent>) CmmFunc.tryParseList(jsonObject.getString("data"), BeanEvent.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                refreshLayout.setRefreshing(false);
                recycler.getAdapter().notifyDataSetChanged();
            }

        }
    }
    //endregion
}
