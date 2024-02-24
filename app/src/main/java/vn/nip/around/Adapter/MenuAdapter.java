package vn.nip.around.Adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import vn.nip.around.Bean.BeanMenu;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Common.AroundWalletFragment;
import vn.nip.around.Fragment.Common.NoticesFragment;
import vn.nip.around.Fragment.Common.OrderFollowFragment;
import vn.nip.around.Fragment.Common.OrderHistoryFragment;
import vn.nip.around.Fragment.Common.PromotionFragment;
import vn.nip.around.Fragment.Common.SignToShipFragment;
import vn.nip.around.Fragment.Common.SupplierFragment;
import vn.nip.around.Fragment.Profile.ProfileFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> implements View.OnClickListener {


    View itemView;
    FragmentActivity activity;
    RecyclerView recycler;
    public List<BeanMenu> list;

    public MenuAdapter() {
        loadMenu();
    }

    public MenuAdapter(FragmentActivity activity, RecyclerView recycler) {
        this.activity = activity;
        this.recycler = recycler;
        loadMenu();
    }

    private void loadMenu() {
        list = new ArrayList<>();
        list.add(new BeanMenu(BeanMenu.PROFILE, activity.getResources().getString(R.string.profile), R.drawable.ic_menu_profile));
        list.add(new BeanMenu(BeanMenu.AROUND_PAYMENT, activity.getResources().getString(R.string.around_payment), R.drawable.ic_menu_payment));
        list.add(new BeanMenu(BeanMenu.FOLLOW_JOURNEY, activity.getResources().getString(R.string.follow_journey), R.drawable.ic_menu_follow_journey));
        list.add(new BeanMenu(BeanMenu.HISTORY_ORDERS, activity.getResources().getString(R.string.history_order), R.drawable.ic_menu_history));
        list.add(new BeanMenu(BeanMenu.NOTICE, activity.getResources().getString(R.string.notice), R.drawable.ic_menu_ring));
        list.add(new BeanMenu(BeanMenu.PROMOTION_CODE, activity.getResources().getString(R.string.promotion_code), R.drawable.ic_menu_promotion));
        list.add(new BeanMenu(BeanMenu.TO_SHIPPER, activity.getResources().getString(R.string.to_become_shipper), R.drawable.ic_menu_toshipper));
        list.add(new BeanMenu(BeanMenu.TO_SUPPLIER, activity.getResources().getString(R.string.to_become_supplier), R.drawable.ic_menu_tosupplier));
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_menu, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        BeanMenu item = list.get(position);
        holder.title.setText(item.getTitle());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.thumb.setImageDrawable(activity.getDrawable(item.getIcon()));
        } else {
            holder.thumb.setImageDrawable(activity.getResources().getDrawable(item.getIcon()));
        }
        if (item.isShowNumber()) {
            holder.notice.setVisibility(View.VISIBLE);
        } else {
            holder.notice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(final View view) {
        try {
            DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            CmmFunc.setDelay(350, new ICallback() {
                @Override
                public void excute() {
                    int itemPosition = recycler.getChildLayoutPosition(view);
                    BeanMenu item = list.get(itemPosition);
                    if (item != null) {
                        switch (item.getId()) {
                            case BeanMenu.PROFILE:
                                Tracking.excute("C16.1Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, ProfileFragment.newInstance());
                                break;
                            case BeanMenu.PROMOTION_CODE:
                                Tracking.excute("C16.2Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, new PromotionFragment());
                                break;
                            case BeanMenu.HISTORY_ORDERS:
                                Tracking.excute("C16.3Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, new OrderHistoryFragment());
                                break;
                            case BeanMenu.FOLLOW_JOURNEY:
                                Tracking.excute("C16.4Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, new OrderFollowFragment());
                                break;
                            case BeanMenu.NOTICE:
                                Tracking.excute("C16.4Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, NoticesFragment.newInstance(0));
                                break;
                            case BeanMenu.AROUND_PAYMENT:
                                Tracking.excute("C16.5Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, new AroundWalletFragment());
                                break;
                            case BeanMenu.TO_SHIPPER:
                                Tracking.excute("C16.6Y");
                                FragmentHelper.addFragment(activity, R.id.home_content, new SignToShipFragment());
                                break;
                            case BeanMenu.TO_SUPPLIER:
                                FragmentHelper.addFragment(activity, R.id.home_content, new SupplierFragment());
                                break;
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView thumb;
        private View notice;

        public MenuViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            thumb = (ImageView) view.findViewById(R.id.thumbnail);
            notice = view.findViewById(R.id.notice);
        }
    }
}