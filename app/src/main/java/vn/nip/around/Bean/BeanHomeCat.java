package vn.nip.around.Bean;

/**
 * Created by HOME on 10/17/2017.
 */

public class BeanHomeCat extends BeanBase {
    public static final String COD = "cod";
    public static final String TRANSPORT = "transport";
    public static final String PURCHASE = "purchase";
    public static final String GIFTING = "gifting";
    public static final String SHOPPING = "shopping";
    public static final String EGIFT = "egift";

    private String id;
    public String image;
    public String image_vn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_vn() {
        return image_vn;
    }

    public void setImage_vn(String image_vn) {
        this.image_vn = image_vn;
    }
}
