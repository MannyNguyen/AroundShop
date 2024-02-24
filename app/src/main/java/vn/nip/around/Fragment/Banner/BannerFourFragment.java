package vn.nip.around.Fragment.Banner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import vn.nip.around.Adapter.BannerAdapter;
import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Bean.BeanProduct;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerFourFragment extends BaseFragment {

    Thread init;
    BeanBannerItem beanBannerItem;
    List<BeanBannerItem> products;

    public BannerFourFragment() {
        // Required empty public constructor
    }

    public static BannerFourFragment newInstance(String data, String products) {

        Bundle args = new Bundle();
        args.putString("data", data);
        args.putString("products", products);
        BannerFourFragment fragment = new BannerFourFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_banner_four, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    beanBannerItem = (BeanBannerItem) CmmFunc.tryParseJson(getArguments().getString("data"), BeanBannerItem.class);
                    products = (List<BeanBannerItem>) CmmFunc.tryParseList(getArguments().getString("products"), BeanBannerItem.class);
                    final TextView titleBanner = (TextView) view.findViewById(R.id.title_banner);
                    final TextView startDate = (TextView) view.findViewById(R.id.start_date);
                    final TextView endDate = (TextView) view.findViewById(R.id.end_date);
                    DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                    final LocalDateTime start = LocalDateTime.parse(beanBannerItem.getStart_date(), df);
                    final LocalDateTime end = LocalDateTime.parse(beanBannerItem.getEnd_date(), df);
                    final RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                titleBanner.setText(CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_title)) + "");
                                startDate.setText(start.getDayOfMonth() + "/" + start.getMonthOfYear());
                                endDate.setText(end.getDayOfMonth() + "/" + end.getMonthOfYear());

                                GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
                                recycler.setLayoutManager(layoutManager);
                                BannerAdapter adapter = new BannerAdapter(getActivity(), recycler, products);
                                recycler.setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        init.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (init != null) {
            init.interrupt();
        }
    }
}
