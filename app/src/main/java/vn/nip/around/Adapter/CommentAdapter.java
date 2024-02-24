package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanComment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.R;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    private List<BeanComment> list;


    public CommentAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanComment> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_comment, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanComment item = list.get(position);
        if (item != null) {
            Picasso.with(activity).load(item.getUser_avatar()).into(holder.avatar);
            holder.name.setText(item.getUser_name() + "");
            holder.comment.setText(item.getComment() + "");
            DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(item.getTime(), df);
            String time = DateTimeHelper.parseDateTime(dateTime);
            holder.time.setText(time);
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
//            BeanComment item = list.get(itemPosition);
//            if (item != null) {
//                int id = item.getId();
//                Fragment fragment = new ProductFragment();
//                Bundle bundle = new Bundle();
//                bundle.putInt("id", id);
//                fragment.setArguments(bundle);
//                FragmentHelper.addFragment(activity, R.id.home_content, fragment);
//            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView time;
        TextView comment;

        public MenuViewHolder(View view) {
            super(view);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            name = (TextView) view.findViewById(R.id.user_name);
            time = (TextView) view.findViewById(R.id.time);
            comment = (TextView) view.findViewById(R.id.comment);
        }


    }
}