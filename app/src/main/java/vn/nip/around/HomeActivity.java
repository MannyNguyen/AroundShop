package vn.nip.around;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.firebase.iid.FirebaseInstanceId;

import vn.nip.around.Helper.APIHelper;
import vn.nip.around.Helper.FragmentHelper;
import vn.nip.around.Service.FirebaseIDService;

public class HomeActivity extends BaseActivity {


    //region Init
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(vn.nip.around.R.layout.activity_home);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            w.setStatusBarColor(getResources().getColor(android.R.color.transparent));
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            findViewById(R.id.pad_status_bar).setVisibility(View.VISIBLE);
//            findViewById(R.id.pad_status_bar_left_menu).setVisibility(View.VISIBLE);
//        } else {
//            findViewById(R.id.pad_status_bar).setVisibility(View.GONE);
//            findViewById(R.id.pad_status_bar_left_menu).setVisibility(View.GONE);
//        }
    }

    //endregion


    @Override
    protected void onStart() {
        super.onStart();

    }




}
