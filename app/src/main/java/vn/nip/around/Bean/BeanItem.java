package vn.nip.around.Bean;

/**
 * Created by viminh on 3/7/2017.
 */

public class BeanItem {
    private String item_name;
    private int item_cost;
    private String recipent_name;
    private String note;
    private String phone;
    private boolean is_gift;
    private int item_quantity;
    private String item_image;
    private int item_prepare_time;

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_cost() {
        return item_cost;
    }

    public void setItem_cost(int item_cost) {
        this.item_cost = item_cost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean is_gift() {
        return is_gift;
    }

    public void setIs_gift(boolean is_gift) {
        this.is_gift = is_gift;
    }

    public String getRecipent_name() {
        return recipent_name;
    }

    public void setRecipent_name(String recipent_name) {
        this.recipent_name = recipent_name;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(int item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public int getItem_prepare_time() {
        return item_prepare_time;
    }

    public void setItem_prepare_time(int item_prepare_time) {
        this.item_prepare_time = item_prepare_time;
    }
}
