package vn.nip.around.Fragment.Banner;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import vn.nip.around.Bean.BeanBannerItem;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Fragment.Common.NoticeFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BannerTextFragment extends BaseFragment {

    Thread threadInit;
    BeanBannerItem beanBannerItem;

    public BannerTextFragment() {
        // Required empty public constructor
    }

    public static BannerTextFragment newInstance(int type, String beanBannerItem) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putString("data", beanBannerItem);
        BannerTextFragment fragment = new BannerTextFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            return inflater.inflate(R.layout.fragment_banner_text, container, false);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (threadInit != null) {
            threadInit.interrupt();
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView title = (TextView) view.findViewById(R.id.title);
                final TextView description = (TextView) view.findViewById(R.id.description);
                final Button btnView = (Button) view.findViewById(R.id.view);
                beanBannerItem = (BeanBannerItem) CmmFunc.tryParseJson(getArguments().getString("data"), BeanBannerItem.class);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_title)) + "");
                        description.setText(CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_description)) + "");
                        if (getArguments().getInt("type") == 4) {
                            btnView.setVisibility(View.VISIBLE);
                            btnView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FragmentHelper.pop(getActivity());
                                    FragmentHelper.addFragment(getActivity(), R.id.home_content, NoticeFragment.newInstance(beanBannerItem.getId_notification(), CmmFunc.getValueByKey(beanBannerItem, getString(R.string.server_key_title)) + ""));
                                }
                            });
                        }
                    }
                });
            }
        });
        threadInit.start();
    }
}
