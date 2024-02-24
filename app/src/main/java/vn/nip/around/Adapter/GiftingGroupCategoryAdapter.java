package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class GiftingGroupCategoryAdapter extends RecyclerView.Adapter<GiftingGroupCategoryAdapter.MenuViewHolder> implements View.OnClickListener {


    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<BeanCategory> list;


    public GiftingGroupCategoryAdapter(Fragment fragment, RecyclerView recycler, List<BeanCategory> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_group, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            BeanCategory item = list.get(position);
            if (item != null) {
                holder.title.setText(BeanCategory.class.getDeclaredField(fragment.getString(R.string.key_name)).get(item) + "");
                GiftingCategoryAdapter adapter = new GiftingCategoryAdapter(fragment, recycler, item.getSub_categories(), 3);
                LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getActivity(), LinearLayoutManager.HORIZONTAL, false);
                holder.recycler.setLayoutManager(layoutManager);
                holder.recycler.setAdapter(adapter);
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
            BeanCategory item = list.get(itemPosition);
            if (item != null) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recycler;
        TextView title;

        public MenuViewHolder(View view) {
            super(view);
            recycler = (RecyclerView) view.findViewById(R.id.recycler);
            title = (TextView) view.findViewById(R.id.title);
        }


    }
}