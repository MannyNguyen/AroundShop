package vn.nip.around.Bean;

/**
 * Created by HOME on 8/30/2017.
 */

public class BeanBannerItem extends BeanBase {
    public String title;
    public String vn_title;
    private String start_date;
    private String end_date;
    public String image;
    public String vn_image;
    public String description;
    public String vn_description;
    private BeanProduct product;
    private int id_notification;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVn_title() {
        return vn_title;
    }

    public void setVn_title(String vn_title) {
        this.vn_title = vn_title;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVn_description() {
        return vn_description;
    }

    public void setVn_description(String vn_description) {
        this.vn_description = vn_description;
    }

    public BeanProduct getProduct() {
        return product;
    }

    public void setProduct(BeanProduct product) {
        this.product = product;
    }

    public int getId_notification() {
        return id_notification;
    }

    public void setId_notification(int id_notification) {
        this.id_notification = id_notification;
    }
}
