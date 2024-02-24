package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.nip.around.Bean.BeanShoppingCategory;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Shopping.TabShoppingFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class HomeShoppingAdapter extends RecyclerView.Adapter<HomeShoppingAdapter.MenuViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<BeanShoppingCategory> list;


    public HomeShoppingAdapter(Fragment fragment, RecyclerView recycler, List<BeanShoppingCategory> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home_shopping, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            final BeanShoppingCategory item = list.get(position);
            if (item != null) {
                holder.name.setText(BeanShoppingCategory.class.getDeclaredField(fragment.getString(R.string.key_name)).get(item) + "");
                Picasso.with(fragment.getActivity()).load(BeanShoppingCategory.class.getDeclaredField(fragment.getString(R.string.key_image)).get(item) + "").into(holder.thumbnail);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, TabShoppingFragment.newInstance(CmmFunc.tryParseObject(item)));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView name;

        public MenuViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            name = (TextView) view.findViewById(R.id.name);
        }


    }
}