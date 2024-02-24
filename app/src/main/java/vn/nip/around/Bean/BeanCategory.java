package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by viminh on 12/20/2016.
 */

public class BeanCategory {
    protected int id;
    public String name;
    public String image;
    public String vn_image;
    public String vn_name;

    private int recommendation;
    private boolean isColor = false;
    private String type;

    private List<BeanCategory> sub_categories;
    private List<BeanCategory> tab_categories;

    public static BeanCategory getById(int id, List<BeanCategory> items) {
        for (BeanCategory bean : items) {
            if (bean.getId() == id) {
                return bean;
            }
        }
        return null;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(int recommendation) {
        this.recommendation = recommendation;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BeanCategory> getSub_categories() {
        return sub_categories;
    }

    public void setSub_categories(List<BeanCategory> sub_categories) {
        this.sub_categories = sub_categories;
    }

    public List<BeanCategory> getTab_categories() {
        return tab_categories;
    }

    public void setTab_categories(List<BeanCategory> tab_categories) {
        this.tab_categories = tab_categories;
    }


}
