package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by HOME on 11/6/2017.
 */

public class BeanShoppingCategory extends BeanBase {
    private int id;
    public String name;
    public String image;
    public String vn_image;
    public String vn_name;
    private List<BeanShoppingCategory> sub_categories;

    public static BeanShoppingCategory getById(int id, List<BeanShoppingCategory> items) {
        for (BeanShoppingCategory bean : items) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVn_image() {
        return vn_image;
    }

    public void setVn_image(String vn_image) {
        this.vn_image = vn_image;
    }

    public String getVn_name() {
        return vn_name;
    }

    public void setVn_name(String vn_name) {
        this.vn_name = vn_name;
    }

    public List<BeanShoppingCategory> getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(List<BeanShoppingCategory> sub_categories) {
        this.sub_categories = sub_categories;
    }
}
