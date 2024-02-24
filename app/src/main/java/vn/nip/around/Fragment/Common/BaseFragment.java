package vn.nip.around.Fragment.Common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import vn.nip.around.Adapter.MenuAdapter;
import vn.nip.around.Bean.BeanMenu;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Fragment.Cart.CartFragment;
import vn.nip.around.Fragment.SearchTag.SearchFragment;
import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.R;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by viminh on 1/9/2017.
 */

public class BaseFragment extends Fragment {
    public boolean isLoaded;
    public View view;
    public FrameLayout layoutProgress;
    protected JSONObject param;
    public Runnable keyBoardShow;
    public Runnable keyBoardHide;
    public Thread threadInit;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CmmFunc.hideKeyboard(getActivity());
            }
        });
        try {
            layoutProgress = (FrameLayout) getView().findViewById(R.id.layout_progress);
        } catch (Exception e) {

        }
        try {
            getActivity().findViewById(R.id.notice).setVisibility(View.GONE);
        } catch (Exception e) {

        }

        View back = view.findViewById(R.id.back);
        if (back != null) {
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.pop(getActivity());
                }
            });
        }

        View search = view.findViewById(R.id.search);
        if (search != null) {
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.addFragment(getActivity(), R.id.home_content, SearchFragment.newInstance());

                }
            });
        }

        View menu = view.findViewById(R.id.menu);
        if (menu != null) {
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }


//        final View mRootView = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
//        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect measureRect = new Rect(); //you should cache this, onGlobalLayout can get called often
//                mRootView.getWindowVisibleDisplayFrame(measureRect);
//                // measureRect.bottom is the position above soft keypad
//                int keypadHeight = mRootView.getRootView().getHeight() - (measureRect.bottom - measureRect.top);
//
//                if (keypadHeight > 500) { // if more than 100 pixels, its probably a keyboard...
//                    if (keyBoardShow != null) {
//                        //keyBoardShow.run();
//                    }
//                } else {
//                    if (keyBoardHide != null) {
//                        //keyBoardHide.run();
//                    }
//                }
//            }
//        });
    }


    @Override
    public void onResume() {
        super.onResume();
        try {

            CmmFunc.hideKeyboard(getActivity());
            ImageButton cart = (ImageButton) view.findViewById(R.id.cart);
            TextView numberCart = (TextView) view.findViewById(R.id.number_cart);
            if (cart != null && numberCart != null) {
                getNumberCart(cart, numberCart);
            }
            View menu = view.findViewById(R.id.menu);
            if (menu != null) {
                getNumber();
            }
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            hideProgress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void manualResume(){

    }

    public void showProgress() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        layoutProgress.setAlpha(1.0f);
                        layoutProgress.setVisibility(View.VISIBLE);
                        //layoutProgress.animate().alpha(1f).setDuration(400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (threadInit != null) {
            threadInit.interrupt();
        }
    }

    public void hideProgress() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        layoutProgress.setAlpha(1f);
                        layoutProgress.animate().alpha(0.0f).setDuration(400);
                        layoutProgress.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getNumberCart(final ImageButton cart, final TextView numberCart) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int number = APIHelper.getNumberCart();
                    //CmmVariable.producteds = APIHelper.getCart();
                    if (number > 0) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    numberCart.setVisibility(View.VISIBLE);
                                    numberCart.setText(number + StringUtils.EMPTY);
                                    cart.setImageResource(R.drawable.ic_cart);
                                    cart.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (CmmFunc.getActiveFragment(GlobalClass.getActivity()) instanceof CartFragment) {
                                                return;
                                            }
                                            FragmentHelper.addFragment(GlobalClass.getActivity(), R.id.home_content, CartFragment.newInstance());
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } else {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    numberCart.setVisibility(View.GONE);
                                    numberCart.setText(StringUtils.EMPTY);
                                    cart.setImageResource(R.drawable.ic_cart_gray);
                                    cart.setOnClickListener(null);
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

    private void getNumber() {
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
                        int numberNotice = data.getInt("number_notification");
                        int numberEvent = data.getInt("number_event");
                        int numberSumNotice = numberEvent + numberNotice;
                        RecyclerView menu = (RecyclerView) getActivity().findViewById(R.id.recycler_menu);
                        final MenuAdapter menuAdapter = (MenuAdapter) menu.getAdapter();
                        if (numberOrder > 0) {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.FOLLOW_JOURNEY, menuAdapter.list);
                            bean.setShowNumber(true);
                            final int n = numberOrder;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.FOLLOW_JOURNEY, menuAdapter.list);
                            bean.setShowNumber(false);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        if (numberSumNotice > 0) {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.NOTICE, menuAdapter.list);
                            bean.setShowNumber(true);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            BeanMenu bean = BeanMenu.getByID(BeanMenu.NOTICE, menuAdapter.list);
                            bean.setShowNumber(false);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        menuAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        final int sum = numberSumNotice + numberOrder;
                        final TextView numberFollow = (TextView) view.findViewById(R.id.number_menu);
                        if (numberFollow != null) {
                            if (sum > 0) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        numberFollow.setVisibility(View.VISIBLE);
                                        numberFollow.setText(sum + "");
                                        Fragment fragment = CmmFunc.getActiveFragment(getActivity());
                                        if (fragment instanceof FollowJourneyFragment) {
                                            if (fragment.getArguments().getBoolean("is_back")) {
                                                numberFollow.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                });
                            } else {
                                numberFollow.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        RecyclerView menu = (RecyclerView) getActivity().findViewById(R.id.recycler_menu);
                        final MenuAdapter menuAdapter = (MenuAdapter) menu.getAdapter();
                        BeanMenu bean = BeanMenu.getByID(BeanMenu.FOLLOW_JOURNEY, menuAdapter.list);
                        bean.setShowNumber(false);
                        BeanMenu beanNotice = BeanMenu.getByID(BeanMenu.NOTICE, menuAdapter.list);
                        beanNotice.setShowNumber(false);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    menuAdapter.notifyDataSetChanged();
                                    TextView numberFollow = (TextView) view.findViewById(R.id.number_follow);
                                    if (numberFollow != null)
                                        numberFollow.setVisibility(View.GONE);
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

}
