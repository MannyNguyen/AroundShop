package vn.nip.around.Bean;

/**
 * Created by viminh on 10/6/2016.
 */

public class BeanRate {
    private int id;
    private String name;
    private String vn_name;
    private boolean isCheck;

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

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getVn_name() {
        return vn_name;
    }

    public void setVn_name(String vn_name) {
        this.vn_name = vn_name;
    }
}
