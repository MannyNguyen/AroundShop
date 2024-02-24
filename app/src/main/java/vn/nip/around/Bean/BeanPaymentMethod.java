package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by HOME on 7/27/2017.
 */

public class BeanPaymentMethod {
    public static final int ONLINE = 1;
    public static final int AROUND = 2;
    public static final int CASH = 3;

    private int type;
    private int icon;
    private String title;
    private boolean isRight;
    private String cashAround;
    private boolean isSelected;

    public BeanPaymentMethod() {

    }

    public BeanPaymentMethod(int type, int icon, String title, boolean isRight, String cashAround, boolean isSelected) {
        this.setType(type);
        this.setIcon(icon);
        this.setTitle(title);
        this.setIsRight(isRight);
        this.setCashAround(cashAround);
        this.setSelected(isSelected);
    }

    public static BeanPaymentMethod getByType(int type, List<BeanPaymentMethod> list) {
        if (list == null)
            return null;
        for (BeanPaymentMethod bean : list) {
            if (bean.getType() == type) {
                return bean;
            }
        }
        return null;
    }

    public static void resetSelected(List<BeanPaymentMethod> list) {
        if (list == null)
            return;
        for (BeanPaymentMethod bean : list) {
            bean.setSelected(false);
        }
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getIsRight() {
        return isRight;
    }

    public void setIsRight(boolean isRight) {
        this.isRight = isRight;
    }

    public String getCashAround() {
        return cashAround;
    }

    public void setCashAround(String cashAround) {
        this.cashAround = cashAround;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
