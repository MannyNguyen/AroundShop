package vn.nip.around.Fragment.Product;


import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WriteCommentProductFragment extends BaseFragment implements View.OnClickListener {


    public WriteCommentProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_write_comment_product, container, false);
        }
        return view;
    }

    public static WriteCommentProductFragment newInstance(int id, boolean is_rate) {

        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putBoolean("is_rate", is_rate);
        WriteCommentProductFragment fragment = new WriteCommentProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.rating);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.main), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(getResources().getColor(R.color.gray_400), PorterDuff.Mode.SRC_ATOP);
        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(getResources().getString(R.string.comments));
        view.findViewById(R.id.submit).setOnClickListener(WriteCommentProductFragment.this);
        if (getArguments().getBoolean("is_rate")) {
            ratingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                RatingBar ratingBar = (RatingBar) getView().findViewById(R.id.rating);
                EditText content = (EditText) getView().findViewById(R.id.content);
                int star = ratingBar.getProgress();
                if (getArguments().getBoolean("is_rate")) {
                    star = 0;
                }
                String message = content.getText().toString();
                if (message.trim().equals("")) {
                    return;
                }
                new ActionComment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getArguments().getInt("id"), star, content.getText().toString());
                break;
        }
    }

    //region Actions
    class ActionComment extends ActionAsync {
        int star;

        @Override
        protected JSONObject doInBackground(Object... objects) {
            try {
                int productID = (int) objects[0];
                star = (int) objects[1];
                String comment = (String) objects[2];
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
                params.add(new AbstractMap.SimpleEntry("star", star + ""));
                params.add(new AbstractMap.SimpleEntry("comment", comment + ""));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/rate_comment_product", params, false);
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
                    FragmentHelper.pop(getActivity());
                    if (star > 0) {
                        Fragment comment = CmmFunc.getFragmentByTag(getActivity(), CommentProductFragment.class.getName());
                        comment.getArguments().putBoolean("is_rate", true);
                    }

                }
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "", e.getMessage());
            } finally {

            }
        }
    }
    //endregion
}
