package vn.nip.around;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import vn.nip.around.Adapter.MenuAdapter;
import vn.nip.around.Class.ActionAsync;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.CustomDialog;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.Class.Tracking;
import vn.nip.around.Fragment.Common.HomeFragment;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Helper.HttpHelper;
import vn.nip.around.Helper.StorageHelper;
import vn.nip.around.Interface.ICallback;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by HOME on 6/22/2017.
 */

public class BaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //overridePendingTransition(android.R.anim.fade_in, 0);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    protected void init() {
        DrawerLayout drawer = (DrawerLayout) findViewById(vn.nip.around.R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawer,         /* DrawerLayout object */
                null,  /* nav drawer icon to replace 'Up' caret */
                vn.nip.around.R.string.navigation_drawer_open,  /* "open drawer" description */
                vn.nip.around.R.string.navigation_drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);


            }


            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                CmmFunc.hideKeyboard(BaseActivity.this);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.home_content);
//                frameLayout.setTranslationX(slideOffset * 200);
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawer.setDrawerListener(mDrawerToggle);
        RecyclerView menu = (RecyclerView) findViewById(vn.nip.around.R.id.recycler_menu);
        MenuAdapter menuAdapter = new MenuAdapter(this, menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        menu.setLayoutManager(layoutManager);
        menu.setAdapter(menuAdapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    NavigationView navigationView = (NavigationView) findViewById(vn.nip.around.R.id.nav_view);
                    View selection = navigationView.findViewById(R.id.header_menu);
                    selection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DrawerLayout drawer = (DrawerLayout) GlobalClass.getActivity().findViewById(vn.nip.around.R.id.drawer_layout);
                            drawer.closeDrawer(GravityCompat.START);
                            CmmFunc.setDelay(350, new ICallback() {
                                @Override
                                public void excute() {
                                    try {
                                        Fragment fragment = CmmFunc.getActiveFragment(GlobalClass.getActivity());
                                        if (fragment == null || fragment instanceof HomeFragment) {
                                            return;
                                        }
                                        AppActivity.popRoot();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                    findViewById(vn.nip.around.R.id.notice).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            findViewById(vn.nip.around.R.id.notice).setVisibility(View.GONE);
                        }
                    });

                    ImageButton logout = (ImageButton) navigationView.findViewById(vn.nip.around.R.id.logout);
                    logout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Tracking.excute("C16.7N");
                            CustomDialog.Dialog2Button(BaseActivity.this, getString(vn.nip.around.R.string.log_out), getString(vn.nip.around.R.string.message_logout), getString(vn.nip.around.R.string.ok), getString(vn.nip.around.R.string.cancel), new ICallback() {
                                @Override
                                public void excute() {
                                    new ActionLogout().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            }, null);
                        }
                    });

                    ImageButton help = (ImageButton) navigationView.findViewById(vn.nip.around.R.id.help);
                    help.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", CmmVariable.phoneService, null));
                            startActivity(intent);
                        }
                    });


                } catch (Exception e) {

                }
            }
        }).start();


    }


    //region Actions
    public class ActionLogout extends ActionAsync {

        @Override
        protected JSONObject doInBackground(Object... objects) {
            JSONObject jsonObject = null;
            try {
                List<Map.Entry<String, String>> params = new ArrayList<>();
                params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
                params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
                params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
                params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
                String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/logout", params, false);
                jsonObject = new JSONObject(response);
                return jsonObject;
            } catch (Exception e) {
                CmmFunc.showError(getClass().getName(), "", e.getMessage());
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                int code = jsonObject.getInt("code");
                if (code == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            StorageHelper.resetUser();
                            StorageHelper.set(StorageHelper.HOME, StringUtils.EMPTY);
                            StorageHelper.set(StorageHelper.WORK, StringUtils.EMPTY);
                            StorageHelper.set(StorageHelper.OTHER, StringUtils.EMPTY);

                            Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).start();

                }
            } catch (Exception e) {

            } finally {

            }
        }
    }


    //endregion
}
