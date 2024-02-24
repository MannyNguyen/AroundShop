package vn.nip.around.Bean;

/**
 * Created by HOME on 12/4/2017.
 */

public class BeanSearchShop extends BeanBase {
    private int shop_id;
    private int id;
    public String shop_name;
    private String name;
    private String shop_avatar;
    public String shop_vn_name;


    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_avatar() {
        return shop_avatar;
    }

    public void setShop_avatar(String shop_avatar) {
        this.shop_avatar = shop_avatar;
    }

    public String getShop_vn_name() {
        return shop_vn_name;
    }

    public void setShop_vn_name(String shop_vn_name) {
        this.shop_vn_name = shop_vn_name;
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
}
