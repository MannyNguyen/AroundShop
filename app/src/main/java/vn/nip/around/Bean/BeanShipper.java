package vn.nip.around.Bean;

/**
 * Created by viminh on 10/25/2016.
 */

public class BeanShipper extends BeanBase {
    private String shipper_name;
    private String shipper_phone;
    private String shipper_fullname;
    private int shipper_rating;
    private double shipper_latitude;
    private double shipper_longitude;
    private String shipper_avatar;
    private String shipper_country_code;


    private static BeanShipper current;

    public static BeanShipper getCurrent() {
        return current;
    }

    public static void setCurrent(BeanShipper current) {
        BeanShipper.current = current;
    }


    public String getShipper_name() {
        return shipper_name;
    }

    public void setShipper_name(String shipper_name) {
        this.shipper_name = shipper_name;
    }

    public String getShipper_phone() {
        return shipper_phone;
    }

    public void setShipper_phone(String shipper_phone) {
        this.shipper_phone = shipper_phone;
    }

    public String getShipper_fullname() {
        return shipper_fullname;
    }

    public void setShipper_fullname(String shipper_fullname) {
        this.shipper_fullname = shipper_fullname;
    }

    public int getShipper_rating() {
        return shipper_rating;
    }

    public void setShipper_rating(int shipper_rating) {
        this.shipper_rating = shipper_rating;
    }

    public double getShipper_latitude() {
        return shipper_latitude;
    }

    public void setShipper_latitude(double shipper_latitude) {
        this.shipper_latitude = shipper_latitude;
    }

    public double getShipper_longitude() {
        return shipper_longitude;
    }

    public void setShipper_longitude(double shipper_longitude) {
        this.shipper_longitude = shipper_longitude;
    }

    public String getShipper_country_code() {
        return shipper_country_code;
    }

    public void setShipper_country_code(String shipper_country_code) {
        this.shipper_country_code = shipper_country_code;
    }

    public String getShipper_avatar() {
        return shipper_avatar;
    }

    public void setShipper_avatar(String shipper_avatar) {
        this.shipper_avatar = shipper_avatar;
    }
}
