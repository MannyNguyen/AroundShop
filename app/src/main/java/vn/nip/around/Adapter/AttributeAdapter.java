package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanAttribute;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class AttributeAdapter extends RecyclerView.Adapter<AttributeAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    List<BeanAttribute> list;


    public AttributeAdapter(FragmentActivity activity, RecyclerView recycler, List<BeanAttribute> list) {
        this.activity = activity;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_attribute, parent, false);
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            BeanAttribute item = list.get(position);
            holder.name.setText(item.getClass().getDeclaredField(activity.getString(R.string.server_key_name_attribute)).get(item) + "");
            LinearLayoutManager layoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            AttributeItemAdapter adapter = new AttributeItemAdapter(activity, holder.recycler, item.getData());
            holder.recycler.setLayoutManager(layoutManager);
            holder.recycler.setAdapter(adapter);
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

    }


    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView previous;
        ImageView next;
        RecyclerView recycler;

        public MenuViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            previous = (ImageView) view.findViewById(R.id.previous);
            next = (ImageView) view.findViewById(R.id.next_attribute);
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            next.setOnClickListener(this);
            previous.setOnClickListener(this);
            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int itemPosition = recycler.getChildLayoutPosition(itemView);
                    List<BeanAttribute.BeanData> datas = list.get(itemPosition).getData();
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == datas.size() - 1) {
                        next.setOnClickListener(null);
                        next.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_next_attribute_disable));
                    } else if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        previous.setOnClickListener(null);
                        previous.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_previous_attribute_disable));
                    } else {
                        next.setOnClickListener(MenuViewHolder.this);
                        previous.setOnClickListener(MenuViewHolder.this);
                        next.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_next_attribute_enable));
                        previous.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_previous_attribute_enable));
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.previous:
                    recycler.smoothScrollToPosition(0);
                    break;
                case R.id.next_attribute:
                    int itemPosition = recycler.getChildLayoutPosition(itemView);
                    List<BeanAttribute.BeanData> datas = list.get(itemPosition).getData();
                    recycler.smoothScrollToPosition(datas.size() - 1);
                    break;
            }
        }
    }
}