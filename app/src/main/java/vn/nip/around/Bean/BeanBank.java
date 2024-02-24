package vn.nip.around.Bean;

/**
 * Created by viminh on 1/4/2017.
 */

public class BeanBank extends BeanBase {
    private String bank_name;
    private String bank_code;
    private String bank_image;
    private boolean isColor = false;

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getBank_image() {
        return bank_image;
    }

    public void setBank_image(String bank_image) {
        this.bank_image = bank_image;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
    }
}
