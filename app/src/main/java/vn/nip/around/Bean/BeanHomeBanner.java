package vn.nip.around.Bean;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by HOME on 10/17/2017.
 */

public class BeanHomeBanner extends BeanBase {
    public static final int TYPE_NONE = 0;
    public static final int TYPE_PRODUCT = 1;
    public static final int TYPE_SHOPPING = 2;
    public static final int TYPE_GIFT = 3;
    public static final int TYPE_SHOP = 4;
    public static final int TYPE_SERVICE = 5;

    private int id;
    public String image;
    public String vn_image;
    private int type;
    private int position;
    private int id_product;
    private int id_category;
    private int id_shop;
    private String id_service;
    private String location;


    private String category_name;
    private String category_vnname;
    private String shop_name;
    private String shop_vnname;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVn_image() {
        return vn_image;
    }

    public void setVn_image(String vn_image) {
        this.vn_image = vn_image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId_product() {
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public int getId_shop() {
        return id_shop;
    }

    public void setId_shop(int id_shop) {
        this.id_shop = id_shop;
    }

    public String getId_service() {
        return id_service;
    }

    public void setId_service(String id_service) {
        this.id_service = id_service;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_vnname() {
        return category_vnname;
    }

    public void setCategory_vnname(String category_vnname) {
        this.category_vnname = category_vnname;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_vnname() {
        return shop_vnname;
    }

    public void setShop_vnname(String shop_vnname) {
        this.shop_vnname = shop_vnname;
    }
}
