package vn.nip.around;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.smartfoxserver.v2.exceptions.SFSException;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.util.ConfigData;
import vn.nip.around.Helper.SmartFoxHelper;

public class TestActivity extends AppCompatActivity {
    SmartFox client = new SmartFox();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                connect();
            }
        });
        connect();
    }

    private void connect(){
        //client.disconnect();
        client.removeAllEventListeners();
        client.addEventListener(SFSEvent.CONNECTION, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent)  {
                try {
                    Log.d("MMMM", "CONNECTION" + client.isConnected());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        client.addEventListener(SFSEvent.CONNECTION_LOST, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMMM", "CONNECTION_LOST");
            }
        });
        client.addEventListener(SFSEvent.LOGIN, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMMM", "LOGIN");

            }
        });
        client.addEventListener(SFSEvent.LOGIN_ERROR, new IEventListener() {
            @Override
            public void dispatch(BaseEvent baseEvent) throws SFSException {
                Log.d("MMMM", "LOGIN_ERROR");
            }
        });

        final ConfigData configData = new ConfigData();
        configData.setZone("AroundAppZone");
        configData.setPort(9933);
        configData.setUseBBox(false);
        configData.setHost("admin.around.vn");
        //configData.setHost("61.28.227.149");

        //test
        //configData.setHost("61.28.226.151");
        client.connect(configData);
    }

}
