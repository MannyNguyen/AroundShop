package vn.nip.around.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanEmoticion;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.CallCenterFragment;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class EmoticionAdapter extends RecyclerView.Adapter<EmoticionAdapter.MenuViewHolder> {
    View itemView;
    CallCenterFragment fragment;
    RecyclerView recycler;
    Map map;

    public EmoticionAdapter(CallCenterFragment fragment, RecyclerView recycler) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.map = BeanEmoticion.getMap(fragment.getActivity());
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_emoticion, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        try {
            final BeanEmoticion item = (BeanEmoticion) map.get(BeanEmoticion.createKey(position + 1));
            if (item != null) {
                Bitmap bm = BitmapFactory.decodeResource(fragment.getResources(), item.getIdResource());
                bm = CmmFunc.resizeBitmap(bm, CmmFunc.convertDpToPx(fragment.getActivity(), 30));
                holder.image.setImageBitmap(bm);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fragment.text.getText().toString().equals(StringUtils.EMPTY)) {
                            fragment.text.setText(item.getKey());
                        } else {
                            fragment.text.append(item.getKey());
                        }

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        return map.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
        }
    }
}