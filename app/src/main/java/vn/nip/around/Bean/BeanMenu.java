package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by viminh on 10/6/2016.
 */

public class BeanMenu extends BeanBase {
    //region Constance
    public static final String PROFILE = "PROFILE";
    public static final String AROUND_PAYMENT = "AROUND_PAYMENT";
    public static final String HISTORY_ORDERS = "HISTORY_ORDERS";
    public static final String FOLLOW_JOURNEY = "FOLLOW_JOURNEY";
    public static final String PROMOTION_CODE = "PROMOTION_CODE";
    public static final String TO_SHIPPER = "TO_SHIPPER";
    public static final String TO_SUPPLIER = "TO_SUPPLIER";
    public static final String NOTICE = "NOTICE";
    //endregion
    private int icon;
    private boolean isShowNumber;
    private String title;
    private String id;

    public BeanMenu() {

    }

    public static BeanMenu getByID(String id, List<BeanMenu> list) {
        for (BeanMenu item : list) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public BeanMenu(String id, String title, int icon) {
        this.setId(id);
        this.setTitle(title);
        this.setIcon(icon);
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isShowNumber() {
        return isShowNumber;
    }

    public void setShowNumber(boolean showNumber) {
        isShowNumber = showNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
