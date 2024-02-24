package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by HOME on 11/7/2017.
 */

public class BeanShop extends BeanBase {
    private int id;
    public String name;
    public String vn_name;
    private int number;
    private boolean isSelected;

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static BeanShop getSelectedShop(List<BeanShop> list) {
        if (list == null) {
            return null;
        }
        for (BeanShop bean : list) {
            if (bean.isSelected()) {
                return bean;
            }
        }

        return null;
    }
}
