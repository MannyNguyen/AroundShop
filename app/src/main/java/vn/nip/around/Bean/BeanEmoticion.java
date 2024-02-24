package vn.nip.around.Bean;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HOME on 11/28/2017.
 */

public class BeanEmoticion {
    private static final int SIZE = 27;
    private static Map map;
    private String key;
    private int idResource;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getIdResource() {
        return idResource;
    }

    public void setIdResource(int idResource) {
        this.idResource = idResource;
    }

    public static String createKey(int position) {
        return "<:" + position + ">";
    }

    public static Map getMap(Activity activity) {
        if (map == null) {
            String packageName = activity.getPackageName();
            map = new HashMap();
            for (int i = 1; i <= SIZE; i++) {
                String key = createKey(i);
                int id = activity.getResources().getIdentifier("e_" + i, "drawable", packageName);
                BeanEmoticion beanEmoticion = new BeanEmoticion();
                beanEmoticion.setKey(key);
                beanEmoticion.setIdResource(id);
                map.put(key, beanEmoticion);
            }
        }
        return map;
    }

}
