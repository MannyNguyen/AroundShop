package vn.nip.around.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.util.List;

import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

/**
 * Created by viminh on 10/4/2016.
 */

public class CODPagerAdapter extends PagerAdapter {
    Activity activity;
    LayoutInflater mLayoutInflater;
    List<BeanPoint> list;


    public CODPagerAdapter(Activity activity, List<BeanPoint> list) {
        mLayoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.list = list;
    }


    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.row_item_cod_confirm, container, false);
        final BeanPoint item = list.get(position);
        if (item != null) {
            TextView index = (TextView) itemView.findViewById(R.id.position);
            TextView address = (TextView) itemView.findViewById(R.id.address);
            TextView recipent = (TextView) itemView.findViewById(R.id.recipent);
            TextView itemName = (TextView) itemView.findViewById(R.id.item_name);
            TextView itemCost = (TextView) itemView.findViewById(R.id.item_cost);
            ImageView call = (ImageView) itemView.findViewById(R.id.call);
            index.setText((position + 1) + StringUtils.EMPTY);
            address.setText(item.getAddress() + StringUtils.EMPTY);
            recipent.setText(item.getRecipent_name() + StringUtils.EMPTY);
            if (!item.getItem_name().equals(StringUtils.EMPTY)) {
                itemName.setText(item.getItem_name() + StringUtils.EMPTY);
            }
            itemCost.setText(CmmFunc.formatMoney(item.getItem_cost(), true));

            if (!item.getPhone().equals(StringUtils.EMPTY)) {
                call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.getPhone() + StringUtils.EMPTY, null));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                call.setImageResource(R.drawable.ic_uncall);
            }

        }
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((CardView)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((CardView) object);
    }
}

