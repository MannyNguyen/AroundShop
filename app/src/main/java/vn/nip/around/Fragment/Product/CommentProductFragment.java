package vn.nip.around.Fragment.Product;


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
import android.widget.TextView;

import vn.nip.around.Adapter.CommentAdapter;
import vn.nip.around.Bean.BeanComment;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentProductFragment extends BaseFragment {

    //region Variables
    public RecyclerView recyclerView;
    public List<BeanComment> comments = new ArrayList<>();
    int page = 1;
    //endregion

    //region Contructors
    public CommentProductFragment() {
        // Required empty public constructor
    }

    public static CommentProductFragment newInstance(int id, boolean isRate) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putBoolean("is_rate", isRate);
        CommentProductFragment fragment = new CommentProductFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_comment_product, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            TextView title = (TextView) getView().findViewById(R.id.title);
            title.setText(getResources().getString(R.string.comments));
            getView().findViewById(R.id.write_comment).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fragment = WriteCommentProductFragment.newInstance(getArguments().getInt("id"), getArguments().getBoolean("is_rate"));
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, fragment);
                }
            });

            CmmFunc.setDelay(400, new ICallback() {
                @Override
                public void excute() {
                    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh);
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            reLoad();
                        }
                    });
                    recyclerView = (RecyclerView) getView().findViewById(R.id.recycler);
                    CommentAdapter adapter = new CommentAdapter(getActivity(), recyclerView, comments);
                    final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if (layoutManager.findLastCompletelyVisibleItemPosition() == comments.size() - 1 && comments.size() > 0) {
                                new ActionGetComment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));
                            }
                        }
                    });
                    reLoad();
                    isLoaded = true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof CommentProductFragment)) {
            return;
        }
        reLoad();
    }

    //endregion

    //region Events
    private void reLoad() {
        try {
            page = 1;
            comments.clear();
            recyclerView.getAdapter().notifyDataSetChanged();
            new ActionGetComment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Actions
    class ActionGetComment extends ActionAsync {

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
                params.add(new AbstractMap.SimpleEntry("page", page + ""));
                String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_product_comment", params);
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
                    List<BeanComment> pros = (List<BeanComment>) CmmFunc.tryParseList(jsonObject.getJSONObject("data").getString("comments"), BeanComment.class);
                    if (pros.size() == 0) {
                        if (page == 1) {
                            view.findViewById(R.id.status).setVisibility(View.VISIBLE);
                        }

                    } else {
                        view.findViewById(R.id.status).setVisibility(View.GONE);
                        view.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
                        for (BeanComment bean : pros) {
                            comments.add(bean);
                        }
                        recyclerView.getAdapter().notifyDataSetChanged();
                        page += 1;
                    }


                }
            } catch (Exception e) {

            } finally {
                hideProgress();
                SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
    //endregion

}
