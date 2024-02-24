package vn.nip.around.Adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import vn.nip.around.Fragment.SearchTag.DetailSearchFragment;
import vn.nip.around.Fragment.SearchTag.SearchFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.MenuViewHolder> {
    View itemView;

    //KeywordSearchFragment or AutoCompleteSearchFragment
    Fragment fragment;
    RecyclerView recycler;
    public List<Object> list;


    public SearchHistoryAdapter(Fragment fragment, RecyclerView recycler, List<Object> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

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
            Object object = list.get(position);
            if (object instanceof String) {
                final String item = (String) object;
                holder.text.setText(item + "");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fragment instanceof SearchFragment) {
                            SearchFragment searchFragment = (SearchFragment) fragment;
                            String category = null;
                            if (searchFragment.getArguments().containsKey("data")) {
                                category = searchFragment.getArguments().getString("data");
                            }
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, DetailSearchFragment.newInstance(searchFragment.TYPE, item, category, null));
                        }
                        if (fragment instanceof DetailSearchFragment) {
                            DetailSearchFragment detailSearchFragment = (DetailSearchFragment) fragment;
                            detailSearchFragment.searchContent.setText(item);
                            detailSearchFragment.resetListContent();

                        }

                    }
                });
                return;
            }

            if (object instanceof JSONObject) {
                final JSONObject jsonObject = (JSONObject) object;
                final String keyword = jsonObject.getString("name") + "";
                holder.text.setText(keyword);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fragment instanceof SearchFragment) {
                            SearchFragment searchFragment = (SearchFragment) fragment;
                            String category = null;
                            if (searchFragment.getArguments().containsKey("data")) {
                                category = searchFragment.getArguments().getString("data");
                            }
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, DetailSearchFragment.newInstance(searchFragment.PRODUCT, "", category, jsonObject.toString()));
                        }
                    }
                });
                return;
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
        TextView text;

        public MenuViewHolder(View view) {
            super(view);
            text = (TextView) view.findViewById(R.id.text);
        }


    }
}