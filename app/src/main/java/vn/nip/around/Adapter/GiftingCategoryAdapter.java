package vn.nip.around.Adapter;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Bean.BeanCategory;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Custom.CustomTextView;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Gift.GiftingCategoryFragment;
import vn.nip.around.Fragment.Gift.TabGiftingFragment;
import vn.nip.around.Helper.DateTimeHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class GiftingCategoryAdapter extends RecyclerView.Adapter<GiftingCategoryAdapter.MenuViewHolder> {

    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;

    int pad;

    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<BeanCategory> list;
    int type;


    public GiftingCategoryAdapter(Fragment fragment, RecyclerView recycler, List<BeanCategory> list, int type) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;
        this.type = type;
        pad = CmmFunc.convertDpToPx(fragment.getActivity(), 2);
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_category_image, parent, false);
        //itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            final BeanCategory item = list.get(position);
            if (item != null) {
                //holder.name.setVisibility(View.GONE);
                if (type == TWO) {
                    //holder.name.setVisibility(View.VISIBLE);
                    //holder.name.setText(BeanCategory.class.getDeclaredField(fragment.getString(R.string.key_name)).get(item) + "");
                }
                if (type == THREE) {
                    //holder.name.setVisibility(View.VISIBLE);
                    //holder.name.setText(BeanCategory.class.getDeclaredField(fragment.getString(R.string.key_name)).get(item) + "");
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(AppActivity.WINDOW_WIDTH / 3, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                    holder.itemView.setLayoutParams(params);
                    holder.itemView.setPadding(pad, pad, pad, pad);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.getTab_categories().size() == 0) {
                            MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                            messageDialogFragment.setMessage(fragment.getString(R.string.no_product));
                            messageDialogFragment.show(fragment.getChildFragmentManager(), messageDialogFragment.getClass().getName());
                            return;
                        }
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, TabGiftingFragment.newInstance(CmmFunc.tryParseObject(item)));
                    }
                });
                Picasso.with(fragment.getActivity()).load(BeanCategory.class.getDeclaredField(fragment.getString(R.string.key_image)).get(item) + "").into(holder.image);
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
    public int getItemViewType(int position) {
        return type;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            name = (TextView) view.findViewById(R.id.name);
        }


    }
}