package vn.nip.around.Adapter;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Bean.BeanHomeCat;
import vn.nip.around.Bean.BeanPoint;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Common.IntroFragment;
import vn.nip.around.Fragment.Dialog.MessageDialogFragment;
import vn.nip.around.Fragment.Gift.GiftingHomeFragment;
import vn.nip.around.Fragment.Pickup.HomeBookFragment;
import vn.nip.around.Fragment.Pickup.MapCODFragment;
import vn.nip.around.Fragment.Pickup.PopupCODFragment;
import vn.nip.around.Fragment.Shopping.HomeShoppingFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;
import vn.nip.around.LoginActivity;
import vn.nip.around.R;

/**
 * Created by viminh on 10/6/2016.
 */

public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.MenuViewHolder> implements View.OnClickListener {
    View itemView;
    Fragment fragment;
    RecyclerView recycler;
    public List<BeanHomeCat> list;

    public HomeCategoryAdapter(Fragment fragment, RecyclerView recycler, List<BeanHomeCat> list) {
        this.fragment = fragment;
        this.recycler = recycler;
        this.list = list;

    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home_image, parent, false);
        itemView.setOnClickListener(this);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        try {
            int width = (recycler.getWidth() - CmmFunc.convertDpToPx(fragment.getActivity(), 32)) / 3;
            itemView.setLayoutParams(new FrameLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
            final BeanHomeCat item = list.get(position);
            Picasso.with(fragment.getActivity()).load(item.getClass().getDeclaredField(fragment.getString(R.string.key_image_1)).get(item) + "").into(holder.image);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getId().equals(BeanHomeCat.EGIFT)) {
                        MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                        messageDialogFragment.setMessage(fragment.getString(R.string.comming_soon));
                        messageDialogFragment.show(fragment.getChildFragmentManager(), messageDialogFragment.getClass().getName());
                        return;
                    }

                    if (StorageHelper.getPhone().equals(StringUtils.EMPTY)) {
                        FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, IntroFragment.newInstance(item.getId()));
                        return;
                    }
                    switch (item.getId()) {
                        case BeanHomeCat.GIFTING:
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, GiftingHomeFragment.newInstance());
                            break;

                        case BeanHomeCat.SHOPPING:
                            FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeShoppingFragment.newInstance());
                            break;

                        case BeanHomeCat.EGIFT:
                            MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                            messageDialogFragment.setMessage(fragment.getString(R.string.comming_soon));
                            messageDialogFragment.show(fragment.getChildFragmentManager(), messageDialogFragment.getClass().getName());
                            break;

                        case BeanHomeCat.TRANSPORT:
                            try {
                                List<BeanPoint> points = new ArrayList<BeanPoint>();
                                BeanPoint beanPoint = new BeanPoint();
                                beanPoint.setPickup_type(BeanPoint.TRANSPORT);
                                points.add(beanPoint);
                                points.add(new BeanPoint());
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(points),
                                        GlobalClass.getActivity().getString(R.string.transport)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case BeanHomeCat.PURCHASE:
                            try {
                                List<BeanPoint> points = new ArrayList<BeanPoint>();
                                BeanPoint beanPoint = new BeanPoint();
                                beanPoint.setPickup_type(BeanPoint.PURCHASE);
                                points.add(beanPoint);
                                points.add(new BeanPoint());
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, HomeBookFragment.newInstance(CmmFunc.tryParseObject(points),
                                        GlobalClass.getActivity().getString(R.string.purchase)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case BeanHomeCat.COD:
                            try {
                                List<BeanPoint> points = new ArrayList<>();
                                BeanPoint beanPoint = new BeanPoint();
                                beanPoint.setPickup_type(BeanPoint.DROP);
                                points.add(beanPoint);

                                BeanPoint drop = new BeanPoint();
                                drop.setPickup_type(BeanPoint.COD);
                                points.add(drop);
                                FragmentHelper.addFragment(fragment.getActivity(), R.id.home_content, MapCODFragment.newInstance(CmmFunc.tryParseObject(points)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;


                    }

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

    @Override
    public void onClick(View view) {
        try {
            int itemPosition = recycler.getChildLayoutPosition(view);
            BeanHomeCat item = list.get(itemPosition);
            if (item != null) {
            }
        } catch (Exception e) {
            Log.e("MenuAdapter", "ViMT - onClick: " + e.getMessage());
        }

    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public MenuViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
        }


    }
}