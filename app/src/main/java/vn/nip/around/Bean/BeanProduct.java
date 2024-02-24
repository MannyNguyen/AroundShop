package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by viminh on 12/20/2016.
 */

public class BeanProduct extends BeanBase {
    private int id;
    private String name;
    private String shop_name;
    private String shop_address;
    private String image;
    private int price;
    private int old_price;
    private int save_percent;
    private int rating;
    private int in_cart;
    private int number;
    private int is_gift;
    private boolean is_new;

    public static int getAllNumber(List<BeanProduct> products) {
        if (products == null) {
            return 0;
        }
        int value = 0;
        for (BeanProduct beanProduct : products) {
            value += beanProduct.getNumber();
        }
        return value;
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

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getIn_cart() {
        return in_cart;
    }

    public void setIn_cart(int in_cart) {
        this.in_cart = in_cart;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getIs_gift() {
        return is_gift;
    }

    public void setIs_gift(int is_gift) {
        this.is_gift = is_gift;
    }

    public static BeanProduct getByID(int id, List<BeanProduct> list) {
        for (BeanProduct bean : list) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
    }


    public int getSave_percent() {
        return save_percent;
    }

    public void setSave_percent(int save_percent) {
        this.save_percent = save_percent;
    }

    public boolean is_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    public int getOld_price() {
        return old_price;
    }

    public void setOld_price(int old_price) {
        this.old_price = old_price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
