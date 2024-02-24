package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by viminh on 1/3/2017.
 */

public class BeanPayment extends BeanBase {
    private String payment_name;
    private String payment_code;
    private List<BeanBank> payment_data;

    public String getPayment_name() {
        return payment_name;
    }

    public void setPayment_name(String payment_name) {
        this.payment_name = payment_name;
    }

    public String getPayment_code() {
        return payment_code;
    }

    public void setPayment_code(String payment_code) {
        this.payment_code = payment_code;
    }


    public List<BeanBank> getPayment_data() {
        return payment_data;
    }

    public void setPayment_data(List<BeanBank> payment_data) {
        this.payment_data = payment_data;
    }
}
