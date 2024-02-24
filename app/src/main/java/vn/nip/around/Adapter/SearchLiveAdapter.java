package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import vn.nip.around.Fragment.Search_old.DetailSearchFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class SearchLiveAdapter extends RecyclerView.Adapter<SearchLiveAdapter.MenuViewHolder> {
    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<String> list;
    String type;


    public SearchLiveAdapter(Fragment fragment, RecyclerView recycler, List<String> list, String type) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
        this.type = type;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_search_history, parent, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            final String item = list.get(position);
            holder.text.setText(item + "");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, DetailSearchFragment.newInstance(type, item));
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
        TextView text;

        public MenuViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
        }


    }
}