package vn.nip.around.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Bean.BeanComment;
import vn.nip.around.Bean.BeanImage;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

/**
 * Created by viminh on 10/4/2016.
 */

public class ImagePolicyAdapter extends RecyclerView.Adapter<ImagePolicyAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<JSONObject> list;
    int windowWidth;


    public ImagePolicyAdapter(FragmentActivity activity, RecyclerView recycler, List<JSONObject> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        windowWidth = displayMetrics.widthPixels;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_image_policy, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            JSONObject item = list.get(position);
            holder.image.setMaxWidth((windowWidth - CmmFunc.convertDpToPx(activity, 24)) / 2);
            Picasso.with(activity).load(item.getString(activity.getString(R.string.server_key_image_gift))).into(holder.image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
//            int itemPosition = recycler.getChildLayoutPosition(view);
        } catch (Exception e) {

        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}