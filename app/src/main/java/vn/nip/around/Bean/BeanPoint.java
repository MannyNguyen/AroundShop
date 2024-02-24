package vn.nip.around.Bean;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by viminh on 10/10/2016.
 * Điêm trên map
 */

public class BeanPoint {

    public static final int TRANSPORT = 1;
    public static final int PURCHASE = 2;
    public static final int COD = 3;
    public static final int DROP = 0;
    public static final int SHOP = 4;

    //Địa chỉ
    private int id;
    private String address = "";
    private String placeid;

    //Khoảng cách với điểm trước đó
    private Double distance = 0.0;


    private String recipent_name = "";
    //Note (pickup package here - note)
    private String note = "";

    //Là điểm trả tiền - Chỉ ở vị trí đầu và cuối
    private boolean ispay = false;

    //Vị trí trong list
    private int role;

    private int status;
    private Double latitude;
    private Double longitude;
    private String phone = "";

    private String item_name = "";
    private int item_cost;
    private double duration;
    private boolean ispickup;
    private List<BeanItem> location_items;

    private int pickup_type = 1;

    private boolean x15;
    private int bill_price;
    private String bill_image;
    private String shipper_fullname;
    private String shipper_phone;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isPay() {
        return ispay;
    }

    public void setIsPay(boolean pay) {
        ispay = pay;
    }

    public int getTag() {
        return role;
    }

    public void setTag(int tag) {
        this.role = tag;
    }

    public LatLng getLatLng() {
        if (latitude == null || longitude == null)
            return null;
        return new LatLng(latitude, longitude);
    }

    public void setLatLng(LatLng latLng) {
        if (latLng == null)
            return;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }


    public String getDistanceKm() {
        return CmmFunc.metToKilomet(getDistance()) + "\nkm";
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceid() {
        return placeid;
    }

    public void setPlaceid(String placeid) {
        this.placeid = placeid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

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

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BeanItem> getLocation_items() {
        return location_items;
    }

    public void setLocation_items(List<BeanItem> location_items) {
        this.location_items = location_items;
    }

    public String getRecipent_name() {
        return recipent_name;
    }

    public void setRecipent_name(String recipent_name) {
        this.recipent_name = recipent_name;
    }

    public int getPickup_type() {
        return pickup_type;
    }

    public void setPickup_type(int pickup_type) {
        this.pickup_type = pickup_type;
    }

    public boolean isX15() {
        return x15;
    }

    public void setX15(boolean x15) {
        this.x15 = x15;
    }

    public int getBill_price() {
        return bill_price;
    }

    public void setBill_price(int bill_price) {
        this.bill_price = bill_price;
    }

    public String getBill_image() {
        return bill_image;
    }

    public void setBill_image(String bill_image) {
        this.bill_image = bill_image;
    }

    public String getShipper_fullname() {
        return shipper_fullname;
    }

    public void setShipper_fullname(String shipper_fullname) {
        this.shipper_fullname = shipper_fullname;
    }

    public String getShipper_phone() {
        return shipper_phone;
    }

    public void setShipper_phone(String shipper_phone) {
        this.shipper_phone = shipper_phone;
    }

    public boolean ispickup() {
        return ispickup;
    }

    public void setIspickup(boolean ispickup) {
        this.ispickup = ispickup;
    }
}
