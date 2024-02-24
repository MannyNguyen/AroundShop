package vn.nip.around.Adapter;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import vn.nip.around.Bean.BeanNotice;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    public List<BeanNotice> list;


    public NoticeAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanNotice> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_notice, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            BeanNotice item = list.get(position);
            if (item != null) {
                Object title = CmmFunc.getValueByKey(item, activity.getString(R.string.server_key_title));
                if (title != null) {
                    holder.title.setText(title + "");
                }
                Object description = CmmFunc.getValueByKey(item, activity.getString(R.string.server_key_description));
                if (description != null) {
                    holder.description.setText(description + "");
                }

                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(item.getTime(), df);
                String time = DateTimeHelper.parseDateTime(dateTime);
                holder.time.setText(time);

                if (item.is_read()) {
                    holder.description.setTextColor(activity.getResources().getColor(R.color.gray_600));
                    Typeface customFont = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Regular.ttf");
                    holder.description.setTypeface(customFont);
                    holder.isRead.setVisibility(View.GONE);
                } else {
                    holder.description.setTextColor(activity.getResources().getColor(R.color.gray_900));
                    Typeface customFont = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Semibold.ttf");
                    holder.description.setTypeface(customFont);
                    holder.isRead.setVisibility(View.VISIBLE);
                }
            }
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
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanNotice item = list.get(itemPosition);
            if (item != null) {
                item.setIs_read(true);
                FragmentHelper.addFragment(activity, R.id.home_content, NoticeFragment.newInstance(item.getId(), CmmFunc.getValueByKey(item, activity.getString(R.string.server_key_title)) + ""));
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        CustomTextView description;
        TextView time;
        View isRead;

        public MenuViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (CustomTextView) view.findViewById(R.id.description);
            time = (TextView) view.findViewById(R.id.time);
            isRead = view.findViewById(R.id.is_read);
        }


    }
}