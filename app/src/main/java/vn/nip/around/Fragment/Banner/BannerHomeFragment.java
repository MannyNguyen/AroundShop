package vn.nip.around.Fragment.Banner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Bean.BeanBanner;
import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerHomeFragment extends BaseFragment {
    BeanBanner beanBanner;

    public BannerHomeFragment() {
        // Required empty public constructor
    }

    public static BannerHomeFragment newInstance(String data) {

        Bundle args = new Bundle();
        args.putString("data", data);
        BannerHomeFragment fragment = new BannerHomeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            beanBanner = (BeanBanner) CmmFunc.tryParseJson(getArguments().getString("data"), BeanBanner.class);
            if (beanBanner.getType() == 0 || beanBanner.getType() == 1) {
                view = inflater.inflate(R.layout.fragment_banner_home_full, container, false);
            } else {
                view = inflater.inflate(R.layout.fragment_banner_home, container, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
            ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

            switch (beanBanner.getType()) {
                case 2:
                    if (beanBanner.getShow_number() == 1) {
                        for (BeanBannerItem bean : beanBanner.getContents()) {
                            adapter.addFrag(BannerOneFragment.newInstance(CmmFunc.tryParseObject(bean)), "");
                        }
                    } else if (beanBanner.getShow_number() == 4) {
                        int index = beanBanner.getContents().size() / 4;
                        int residual = beanBanner.getContents().size() - index * 4;
                        for (int i = 0; i < index; i++) {
                            List<BeanBannerItem> items = beanBanner.getContents().subList(i * 4, i * 4 + 4);
                            adapter.addFrag(BannerFourFragment.newInstance(CmmFunc.tryParseObject(beanBanner), CmmFunc.tryParseObject(items)), "");
                        }

                        if (residual > 0) {
                            List<BeanBannerItem> items = beanBanner.getContents().subList(index * 4, index * 4 + residual);
                            adapter.addFrag(BannerFourFragment.newInstance(CmmFunc.tryParseObject(beanBanner), CmmFunc.tryParseObject(items)), "");
                        }
                    }

                    break;
                case 0:
                case 1:
                    for (BeanBannerItem bean : beanBanner.getContents()) {
                        adapter.addFrag(BannerImageFragment.newInstance(beanBanner.getType(), CmmFunc.tryParseObject(bean)), "");
                    }
                    break;
                case 3:
                case 4:
                    view.findViewById(R.id.container_normal).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    view.findViewById(R.id.back).setVisibility(View.GONE);
                    for (BeanBannerItem bean : beanBanner.getContents()) {
                        adapter.addFrag(BannerTextFragment.newInstance(beanBanner.getType(), CmmFunc.tryParseObject(bean)), "");
                    }
                    break;
            }

            pager.setAdapter(adapter);
            pager.getAdapter().notifyDataSetChanged();
            tab.setupWithViewPager(pager, false);
            if(tab.getTabCount() < 2){
                tab.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View back = view.findViewById(R.id.back);
        if (back!=null){
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }
    }
}
