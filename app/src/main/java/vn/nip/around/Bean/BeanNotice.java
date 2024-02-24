package vn.nip.around.Bean;

/**
 * Created by HOME on 8/28/2017.
 */

public class BeanNotice extends BeanBase {
    private int id;
    public String title;
    public String vn_title;
    public String description;
    public String vn_description;
    private String time;
    private boolean is_read;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVn_title() {
        return vn_title;
    }

    public void setVn_title(String vn_title) {
        this.vn_title = vn_title;
    }

    public String getVn_description() {
        return vn_description;
    }

    public void setVn_description(String vn_description) {
        this.vn_description = vn_description;
    }

    public boolean is_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }
}
