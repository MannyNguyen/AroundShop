package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by viminh on 12/20/2016.
 */

public class BeanMainGiftingCategory {
    public static final int PRODUCT = 0;
    public static final int ONE_COLUMN = 1;
    public static final int TWO_COLUMN = 2;
    public static final int TWO_COLUMN_2 = 4;
    public static final int GROUP = 3;
    private int id;
    public String name;
    public String vn_name;
    private int layout;
    public static BeanMainGiftingCategory getById(int id, List<BeanMainGiftingCategory> items) {
        for (BeanMainGiftingCategory bean : items) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }
    public static int index(int id, List<BeanMainGiftingCategory> items) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVn_name() {
        return vn_name;
    }

    public void setVn_name(String vn_name) {
        this.vn_name = vn_name;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }
}
