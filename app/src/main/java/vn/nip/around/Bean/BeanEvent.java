package vn.nip.around.Bean;

/**
 * Created by HOME on 8/29/2017.
 */

public class BeanEvent extends BeanBase {
    private int id;
    public String image;
    public String vn_image;
    private int type;
    private int id_content;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId_content() {
        return id_content;
    }

    public void setId_content(int id_content) {
        this.id_content = id_content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVn_image() {
        return vn_image;
    }

    public void setVn_image(String vn_image) {
        this.vn_image = vn_image;
    }
}
