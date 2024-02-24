package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by HOME on 8/3/2017.
 */

public class BeanAroundPay {
    private boolean isSelected;
    private int value;

    public static void reset(List<BeanAroundPay> list) {
        for (BeanAroundPay bean : list) {
            bean.setSelected(false);
        }
    }

    public static BeanAroundPay getSelected(List<BeanAroundPay> list) {
        for (BeanAroundPay bean : list) {
            if (bean.isSelected()) {
                return bean;
            }
        }
        return null;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
