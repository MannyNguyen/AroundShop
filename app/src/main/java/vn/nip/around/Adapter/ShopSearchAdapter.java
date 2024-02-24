package vn.nip.around.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import vn.nip.around.Bean.BeanSearchShop;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.SearchTag.DetailSearchFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class ShopSearchAdapter extends RecyclerView.Adapter<ShopSearchAdapter.MenuViewHolder> {
    View itemView;
    BaseFragment fragment;
    RecyclerView recycler;
    List<BeanSearchShop> list;

    public ShopSearchAdapter(BaseFragment fragment, RecyclerView recycler, List<BeanSearchShop> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_shop, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MenuViewHolder holder, final int position) {
        try {
            final BeanSearchShop item = list.get(position);
            if (item != null) {
                holder.name.setText(item.getShop_name() + "");
                Picasso.with(fragment.getActivity()).load(item.getShop_avatar()).centerInside().resize(CmmVariable.AVATAR_SHOP, CmmVariable.AVATAR_SHOP).into(holder.image);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, DetailSearchFragment.newInstance("PRODUCT",
                            "", fragment.getArguments().getString("category"), CmmFunc.tryParseObject(item)));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.avatar);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
}