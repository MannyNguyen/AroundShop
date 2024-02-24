package vn.nip.around.Fragment.Search_old;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.nip.around.Adapter.SearchLiveAdapter;
import vn.nip.around.Adapter.ViewPagerAdapter;
import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Fragment.Common.BaseFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment implements View.OnKeyListener {

    TabLayout tab;
    ViewPager pager;
    ViewPagerAdapter adapter;
    EditText searchContent;
    RecyclerView recycler;
    View container;
    List<String> recents = new ArrayList<>();
    Handler handler;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tab = (TabLayout) view.findViewById(R.id.tab);
        pager = (ViewPager) view.findViewById(R.id.pager);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isLoaded) {
            threadInit = new Thread(new Runnable() {
                @Override
                public void run() {
                    searchContent = (EditText) view.findViewById(R.id.search_content);
                    recycler = (RecyclerView) view.findViewById(R.id.recycler);
                    container = view.findViewById(R.id.container);
                    adapter = new ViewPagerAdapter(getChildFragmentManager());
                    adapter.addFrag(ChildSearchFragment.newInstance(ChildSearchFragment.PRODUCT), getString(R.string.product));
                    adapter.addFrag(ChildSearchFragment.newInstance(ChildSearchFragment.SHOP), getString(R.string.shop));
                    HandlerThread handlerThread = new HandlerThread(SearchFragment.this.getClass().getName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    handlerThread.start();
                    handler = new Handler(handlerThread.getLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                });
                            }
                        }
                    };
                    final TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(final Editable s) {
                            try {
                                if (handler != null) {
                                    handler.removeCallbacksAndMessages(null);
                                }

                                if (s.toString().equals(StringUtils.EMPTY)) {
                                    recycler.setVisibility(View.GONE);
                                    container.setVisibility(View.VISIBLE);
                                } else {
                                    // Now add a new one
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (recycler.getVisibility() == View.VISIBLE) {
                                                            return;
                                                        }
                                                        recycler.setVisibility(View.VISIBLE);
                                                        container.setVisibility(View.GONE);
                                                    }
                                                });

                                                recents.clear();
                                                search(s.toString());
                                                handler.sendEmptyMessage(1);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 500);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                pager.setTranslationY(AppActivity.WINDOW_HEIGHT / 2);
                                pager.animate().translationY(0).setDuration(300).start();
                                pager.setAdapter(adapter);
                                tab.setupWithViewPager(pager);
                                pager.setOffscreenPageLimit(adapter.getCount());
                                UITab(2);
                                container.setVisibility(View.VISIBLE);
                                tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                                    @Override
                                    public void onTabSelected(final TabLayout.Tab tab) {
                                        try {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    View row = tab.getCustomView();
                                                    TextView name = (TextView) row.findViewById(R.id.name);
                                                    name.setTextColor(getResources().getColor(R.color.main));
                                                    Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                                                    name.setTypeface(customFont);
                                                }
                                            });
                                            String text = searchContent.getText().toString();
                                            if (searchContent.getText().toString().equals(StringUtils.EMPTY)) {
                                                recycler.setVisibility(View.GONE);
                                                container.setVisibility(View.VISIBLE);
                                            } else {
                                                recycler.setVisibility(View.VISIBLE);
                                                container.setVisibility(View.GONE);
                                                search(text);
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onTabUnselected(final TabLayout.Tab tab) {
                                        try {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    View row = tab.getCustomView();
                                                    TextView name = (TextView) row.findViewById(R.id.name);
                                                    name.setTextColor(getResources().getColor(R.color.gray_900));
                                                    Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Regular) + ".ttf");
                                                    name.setTypeface(customFont);
                                                }
                                            });

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onTabReselected(TabLayout.Tab tab) {
                                    }


                                });
                                searchContent.setOnKeyListener(SearchFragment.this);
                                recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                                recycler.setVisibility(View.GONE);
                                searchContent.addTextChangedListener(textWatcher);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            threadInit.start();
            isLoaded = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        try {
//            CmmFunc.setDelay(1000, new ICallback() {
//                @Override
//                public void excute() {
//                    try {
//
//                        if (FragmentHelper.getActiveFragment(getActivity()) instanceof HomeSearchFragment) {
//                            getActivity().getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//                            searchContent.requestFocus();
//                            CmmFunc.showKeyboard(getActivity());
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            CmmFunc.hideKeyboard(getActivity());
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UITab(int size) {
        try {
            for (int i = 0; i < size; i++) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.tab_product, null);
                TextView name = (TextView) row.findViewById(R.id.name);
                name.setText(adapter.getPageTitle(i));
                if (i == 0) {
                    name.setTextColor(getResources().getColor(R.color.main));
                    Typeface customFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.OpenSans_Semibold) + ".ttf");
                    name.setTypeface(customFont);
                }
                tab.getTabAt(i).setCustomView(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (v.getId()) {
            case R.id.search_content:
                if ((event.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        String value = searchContent.getText().toString().trim();
                        if (value.equals(StringUtils.EMPTY)) {
                            return false;
                        }
                        searchContent.clearFocus();
                        CmmFunc.hideKeyboard(getActivity());
                        String type = "PRODUCT";
                        if (tab.getSelectedTabPosition() == 1) {
                            type = "SHOP";
                        }
                        FragmentHelper.addFragment(getActivity(), R.id.home_content, DetailSearchFragment.newInstance(type, value));
                        return true;
                    }
                }
        }
        return false;
    }

    private void search(final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    showProgress();
                    recents.clear();
                    int selected = tab.getSelectedTabPosition();
                    String tab = "PRODUCT_LIVE";
                    String type = "PRODUCT";
                    if (selected == 1) {
                        tab = "SHOP_LIVE";
                        type = "SHOP";
                    }
                    final String response = APIHelper.searchInfo(tab, key, 1);
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("code");
                    if (code == 1) {
                        final JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            String s = data.getString(i);
                            recents.add(s);
                        }

                        final String finalType = type;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (data.length() > 0) {
                                        view.findViewById(R.id.no_product_message).setVisibility(View.GONE);
                                        recycler.setVisibility(View.VISIBLE);
                                        SearchLiveAdapter adapter = new SearchLiveAdapter(SearchFragment.this, recycler, recents, finalType);
                                        recycler.setAdapter(adapter);
                                        recycler.getAdapter().notifyDataSetChanged();
                                    } else {
                                        recycler.setVisibility(View.GONE);
                                        view.findViewById(R.id.no_product_message).setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    hideProgress();
                }
            }
        }).start();
    }
}
