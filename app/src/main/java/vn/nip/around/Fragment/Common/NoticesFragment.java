package vn.nip.around.Fragment.Common;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Adapter.MenuAdapter;
import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.Bean.BeanMenu;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticesFragment extends BaseFragment implements View.OnClickListener {

    //region Variables

    ViewPager viewPager;
    //endregion

    public static NoticesFragment newInstance(int tab) {
        Bundle args = new Bundle();
        args.putInt("position_tab", tab);
        NoticesFragment fragment = new NoticesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //region Contructors
    public NoticesFragment() {
        // Required empty public constructor
    }
    //endregion

    //region Init
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_notices, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLoaded) {
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.notice));
            view.findViewById(R.id.me).setOnClickListener(NoticesFragment.this);
            view.findViewById(R.id.event).setOnClickListener(NoticesFragment.this);
            viewPager = (ViewPager) view.findViewById(R.id.view_pager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            adapter.addFrag(RecyclerFragment.newInstance(RecyclerFragment.ME), RecyclerFragment.class.getName() + RecyclerFragment.ME);
            adapter.addFrag(RecyclerFragment.newInstance(RecyclerFragment.EVENT), RecyclerFragment.class.getName() + RecyclerFragment.EVENT);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    updateTab(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            adapter.notifyDataSetChanged();

            if (getArguments().getInt("position_tab") == 1) {
                viewPager.setCurrentItem(1);
            }
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Fragment fragment = FragmentHelper.getActiveFragment(getActivity());
        if (!(fragment instanceof NoticesFragment)) {
            return;
        }
        getListFollow();
    }

    //endregion

    //region Events
    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.me:
                if (viewPager.getCurrentItem() == 0) {
                    return;
                }
                viewPager.setCurrentItem(0);
                updateTab(0);
                break;

            case R.id.event:
                if (viewPager.getCurrentItem() == 1) {
                    return;
                }
                viewPager.setCurrentItem(1);
                updateTab(1);
                break;
        }
    }
    //endregion

    //region Methods
    private void updateTab(int position) {
        TextView me = (TextView) getView().findViewById(R.id.me_text);
        TextView event = (TextView) getView().findViewById(R.id.event_text);

        me.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_gray_400));
        event.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_gray_400));

        me.setTextColor(getResources().getColor(R.color.grey_400));
        event.setTextColor(getResources().getColor(R.color.grey_400));
        switch (position) {
            case 0:
                me.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_main));
                me.setTextColor(getResources().getColor(R.color.gray_900));
                break;
            case 1:
                event.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom_main));
                event.setTextColor(getResources().getColor(R.color.gray_900));
                break;

        }
    }

    private void getListFollow() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Map.Entry<String, String>> params = new ArrayList<>();
                    params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                    params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                    params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                    params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                    String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_number_new_info_menu", params);
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        int numberOrder = data.getInt("number_order");
                        final int numberNotice = data.getInt("number_notification");
                        final int numberEvent = data.getInt("number_event");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (numberNotice > 0) {
                                        view.findViewById(R.id.is_new_notify).setVisibility(View.VISIBLE);
                                    } else {
                                        view.findViewById(R.id.is_new_notify).setVisibility(View.GONE);
                                    }

                                    if (numberEvent > 0) {
                                        view.findViewById(R.id.is_new_event).setVisibility(View.VISIBLE);
                                    } else {
                                        view.findViewById(R.id.is_new_event).setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    //endregion


}
