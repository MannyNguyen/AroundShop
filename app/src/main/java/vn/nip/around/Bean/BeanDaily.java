package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by HOME on 10/24/2017.
 */

public class BeanDaily extends BeanBase {
    private int id;
    public String service_name;
    public String vn_service_name;
    private String item_name;
    private String address;
    private int total;
    private String time;
    private String order_type;

    private List<BeanPoint> locations;

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getVn_service_name() {
        return vn_service_name;
    }

    public void setVn_service_name(String vn_service_name) {
        this.vn_service_name = vn_service_name;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<BeanPoint> getLocations() {
        return locations;
    }

    public void setLocations(List<BeanPoint> locations) {
        this.locations = locations;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }
}
