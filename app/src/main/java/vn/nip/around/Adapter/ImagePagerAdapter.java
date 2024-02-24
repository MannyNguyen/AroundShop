package vn.nip.around.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import vn.nip.around.Bean.BeanImage;
import vn.nip.around.R;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by viminh on 10/4/2016.
 */

public class ImagePagerAdapter extends PagerAdapter {
    Activity activity;
    LayoutInflater mLayoutInflater;
    List<BeanImage> list;
    int layout;

    public ImagePagerAdapter() {

    }

    public ImagePagerAdapter(Activity activity, List<BeanImage> list) {
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.list = list;
    }

    public ImagePagerAdapter(Activity activity, List<BeanImage> list, int layout) {
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.list = list;
        this.layout = layout;
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
        itemView = mLayoutInflater.inflate(R.layout.row_image, container, false);
        if (this.layout != 0) {
            itemView = mLayoutInflater.inflate(layout, container, false);
        }
        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
        Picasso.with(activity).load(list.get(position).getUrl()).centerInside().resize(512, 512).into(imageView);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

