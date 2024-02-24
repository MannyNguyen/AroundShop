package vn.nip.around.Bean;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HOME on 11/6/2017.
 */

public class BeanSort extends BeanBase {
    public static final int INCREASE = 1;
    public static final int DECREASE = 2;
    private int id;
    public String vn_name;
    public String name;

    public BeanSort() {

    }

    public BeanSort(int id, String vn_name, String name) {
        this.id = id;
        this.vn_name = vn_name;
        this.name = name;
    }

    public static List<BeanSort> create() {
        List<BeanSort> sorts = new ArrayList<>();
        sorts.add(new BeanSort(0, "Tất cả", "All"));
        sorts.add(new BeanSort(1, "Tăng dần", "Increase"));
        sorts.add(new BeanSort(2, "Giảm dần", "Decrease"));
        return sorts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVn_name() {
        return vn_name;
    }

    public void setVn_name(String vn_name) {
        this.vn_name = vn_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
