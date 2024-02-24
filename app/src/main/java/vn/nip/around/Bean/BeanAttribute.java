package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by viminh on 2/15/2017.
 */

public class BeanAttribute {
    private String id_attribute;
    public String name_attribute;
    public String vn_name_attribute;
    private List<BeanData> data;

    public String getId_attribute() {
        return id_attribute;
    }

    public void setId_attribute(String id_attribute) {
        this.id_attribute = id_attribute;
    }


    public List<BeanData> getData() {
        return data;
    }

    public void setData(List<BeanData> data) {
        this.data = data;
    }

    public String getName_attribute() {
        return name_attribute;
    }

    public void setName_attribute(String name_attribute) {
        this.name_attribute = name_attribute;
    }

    public String getVn_name_attribute() {
        return vn_name_attribute;
    }

    public void setVn_name_attribute(String vn_name_attribute) {
        this.vn_name_attribute = vn_name_attribute;
    }

    public static class BeanData {
        private String id_data;
        private String name_data;
        private boolean isCheck = false;

        public String getId_data() {
            return id_data;
        }

        public void setId_data(String id_data) {
            this.id_data = id_data;
        }

        public String getName_data() {
            return name_data;
        }

        public void setName_data(String name_data) {
            this.name_data = name_data;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }
}


