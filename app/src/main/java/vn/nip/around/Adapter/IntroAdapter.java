package vn.nip.around.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.nip.around.Bean.BeanImage;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.LoginActivity;
import vn.nip.around.R;

/**
 * Created by viminh on 10/4/2016.
 */

public class IntroAdapter extends PagerAdapter {
    Activity activity;
    LayoutInflater mLayoutInflater;
    List<Integer> list;
    int layout;

    public IntroAdapter() {

    }

    public IntroAdapter(Activity activity, List<Integer> list) {
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = null;
        itemView = mLayoutInflater.inflate(R.layout.row_intro, container, false);
        if (this.layout != 0) {
            itemView = mLayoutInflater.inflate(layout, container, false);
        }
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        imageView.setImageResource(list.get(position));
        container.addView(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GlobalClass.getActivity(), LoginActivity.class);
                GlobalClass.getActivity().startActivity(intent);
            }
        });
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

