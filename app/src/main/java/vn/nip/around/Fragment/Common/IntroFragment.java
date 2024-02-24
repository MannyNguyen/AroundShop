package vn.nip.around.Fragment.Common;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.ImagePagerAdapter;
import vn.nip.around.Adapter.IntroAdapter;
import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Bean.BeanBanner;
import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Bean.BeanHomeCat;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Banner.BannerFourFragment;
import vn.nip.around.Fragment.Banner.BannerImageFragment;
import vn.nip.around.Fragment.Banner.BannerOneFragment;
import vn.nip.around.Fragment.Banner.BannerTextFragment;
import vn.nip.around.Fragment.Pickup.InfoBookFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragment extends BaseFragment implements View.OnClickListener {

    List<Integer> items = new ArrayList<>();

    public IntroFragment() {
        // Required empty public constructor
    }

    public static IntroFragment newInstance(String type) {

        Bundle args = new Bundle();
        args.putString("type", type);
        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_intro, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
                    final ViewPager pager = (ViewPager) view.findViewById(R.id.view_pager);

                    if (StorageHelper.getLanguage().equals("vi")) {
                        items.add(R.drawable.intro1_vn);
                        items.add(R.drawable.intro2_vn);
                        items.add(R.drawable.intro3_vn);
                        items.add(R.drawable.intro4_vn);
                        items.add(R.drawable.intro5_vn);
                        items.add(R.drawable.intro6_vn);
                    } else {
                        items.add(R.drawable.intro1_en);
                        items.add(R.drawable.intro2_en);
                        items.add(R.drawable.intro3_en);
                        items.add(R.drawable.intro4_en);
                        items.add(R.drawable.intro5_en);
                        items.add(R.drawable.intro6_en);
                    }

                    final IntroAdapter adapter = new IntroAdapter(getActivity(), items);
                    view.findViewById(R.id.outer).setOnClickListener(IntroFragment.this);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pager.setAdapter(adapter);
                            tab.setupWithViewPager(pager, false);
                            if (tab.getTabCount() < 2) {
                                tab.setVisibility(View.GONE);
                            }

                            switch (getArguments().getString("type")) {
                                case BeanHomeCat.GIFTING:
                                    pager.setCurrentItem(2, true);
                                    break;
                                case BeanHomeCat.TRANSPORT:
                                case BeanHomeCat.PURCHASE:
                                case BeanHomeCat.COD:
                                    pager.setCurrentItem(5, true);
                                    break;
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadInit.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.outer:
                FragmentHelper.pop(getActivity());
                break;
        }
    }
}
