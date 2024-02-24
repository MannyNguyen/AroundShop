package vn.nip.around.Adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanAttribute;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class AttributeItemAdapter extends RecyclerView.Adapter<AttributeItemAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    List<BeanAttribute.BeanData> list;

    int parentWidth;

    public AttributeItemAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanAttribute.BeanData> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_attribute_item, parent, false);
        parentWidth = parent.getWidth();
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            BeanAttribute.BeanData item = list.get(position);
            String name = item.getName_data();
            if (name.contains("#")) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(CmmFunc.convertDpToPx(activity, 32), CmmFunc.convertDpToPx(activity, 32));
                holder.container.setLayoutParams(layoutParams);
                holder.text.setText("");
                if (name.toLowerCase().equals("#fff") || name.toLowerCase().equals("#ffffff")) {
                    holder.container.setBackground(activity.getResources().getDrawable(R.drawable.border_circle_gray));
                    if (item.isCheck()) {
                        holder.text.setBackground(activity.getResources().getDrawable(R.drawable.ic_check_gray_attribute));
                    } else {
                        holder.text.setBackground(null);
                    }

                } else {
                    BitmapDrawable bitmap = new BitmapDrawable(activity.getResources(), createBitmap(name));
                    holder.container.setBackground(bitmap);
                    if (item.isCheck()) {
                        holder.text.setBackground(activity.getResources().getDrawable(R.drawable.ic_check_white_attribute));
                    } else {
                        holder.text.setBackground(null);
                    }
                }

            } else {
                holder.text.setText(item.getName_data());
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CmmFunc.convertDpToPx(activity, 32));
                int pad = CmmFunc.convertDpToPx(activity, 8);
                holder.container.setPadding(pad, 0, pad, 0);
                holder.container.setLayoutParams(layoutParams);
                if (item.isCheck()) {
                    holder.container.setBackgroundColor(activity.getResources().getColor(R.color.main));
                    holder.text.setTextColor(activity.getResources().getColor(R.color.white));
                } else {
                    holder.container.setBackground(activity.getResources().getDrawable(R.drawable.border_item_attribute));
                    holder.text.setTextColor(activity.getResources().getColor(R.color.gray_400));
                }
            }


            //holder.container.setMinimumWidth(parentWidth / 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap createBitmap(String color) {
        int size = CmmFunc.convertDpToPx(activity, 100);
        Bitmap output = Bitmap.createBitmap(size,
                size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, size,
                size);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(output, rect, rect, paint);
        return output;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanAttribute.BeanData item = list.get(itemPosition);
            for (BeanAttribute.BeanData bean : list) {
                bean.setCheck(false);
            }
            item.setCheck(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        FrameLayout container;

        public MenuViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
            container = (FrameLayout) view.findViewById(R.id.container);
        }
    }
}