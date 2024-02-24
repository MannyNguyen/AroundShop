package vn.nip.around;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import vn.nip.around.Class.GlobalClass;

public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        GlobalClass.setActivity(LoginActivity.this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AppActivity.class);
        startActivity(intent);
        finish();

    }
}
