package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanComment;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.FullOrder.CODFullOrderFragment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class TabCODAdapter extends RecyclerView.Adapter<TabCODAdapter.ViewHolder> implements View.OnClickListener {
    View itemView;
    BaseFragment fragment;
    RecyclerView recycler;
    public List<BeanTabCOD> list;
    int width;
    int size;


    public TabCODAdapter(BaseFragment fragment, RecyclerView recycler, int sizePoint) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.size = sizePoint;
        list = new ArrayList<>();
        list.add(new BeanTabCOD("Điểm 1", "1st"));
        list.add(new BeanTabCOD("Điểm 2", "2nd"));
        list.add(new BeanTabCOD("Điểm 3", "3rd"));
        list.add(new BeanTabCOD("Điểm 4", "4th"));
        list.add(new BeanTabCOD("Điểm 5", "5th"));
        list.add(new BeanTabCOD("Điểm 6", "6th"));
        list.add(new BeanTabCOD("Điểm 7", "7th"));
        list.add(new BeanTabCOD("Điểm 8", "8th"));
        list.add(new BeanTabCOD("Điểm 9", "9th"));
        list.get(0).setSelected(true);

        for (int i = 0; i < sizePoint - 1; i++) {
            list.get(i).setEnable(true);
        }
        width = AppActivity.WINDOW_WIDTH - CmmFunc.convertDpToPx(fragment.getActivity(), 64);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_tab_cod, parent, false);
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            final BeanTabCOD item = list.get(position);
            if (item != null) {
                holder.line.setVisibility(View.VISIBLE);
                if (position == list.size() - 1) {
                    holder.line.setVisibility(View.GONE);
                }
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width / 4, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                holder.itemView.setLayoutParams(params);
                holder.text.setText(BeanTabCOD.class.getDeclaredField(fragment.getString(R.string.key_name)).get(item) + StringUtils.EMPTY);
                if (!item.isEnable()) {
                    holder.container.setBackgroundResource(0);
                    holder.itemView.setAlpha(0.1f);
                    holder.itemView.setOnClickListener(null);
                    return;
                }
                holder.itemView.setAlpha(1);
                if (item.isSelected()) {
                    holder.container.setBackgroundColor(fragment.getResources().getColor(R.color.main));
                    holder.text.setTextColor(fragment.getResources().getColor(R.color.white));
                    holder.itemView.setOnClickListener(null);
                } else {
                    holder.container.setBackgroundColor(0);
                    holder.text.setTextColor(fragment.getResources().getColor(R.color.gray_600));
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (BeanTabCOD beanTabCOD : list) {
                                if (beanTabCOD.isSelected()) {
                                    beanTabCOD.setSelected(false);
                                }
                            }

                            item.setSelected(true);
                            notifyDataSetChanged();
                            if (fragment instanceof CODFullOrderFragment) {
                                CODFullOrderFragment codFullOrderFragment = (CODFullOrderFragment) fragment;
                                codFullOrderFragment.setData(position);
                            }

                            if (fragment instanceof CODFullOrderFragment) {
                                CODFullOrderFragment codFullOrderFragment = (CODFullOrderFragment) fragment;
                                codFullOrderFragment.setColorArrow(position, size - 2);
                            }
                        }
                    });
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        TextView text;
        FrameLayout line;

        public ViewHolder(View view) {
            super(view);
            container = (LinearLayout) view.findViewById(R.id.container);
            text = (TextView) view.findViewById(R.id.text);
            line = (FrameLayout) view.findViewById(R.id.line);
        }
    }

    public class BeanTabCOD {
        public String name;
        public String vn_name;
        private boolean isSelected;
        private boolean isEnable;

        public BeanTabCOD(String name, String vnName) {
            this.setVn_name(name);
            this.setName(vnName);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVn_name() {
            return vn_name;
        }

        public void setVn_name(String vn_name) {
            this.vn_name = vn_name;
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public void setEnable(boolean enable) {
            isEnable = enable;
        }
    }
}

