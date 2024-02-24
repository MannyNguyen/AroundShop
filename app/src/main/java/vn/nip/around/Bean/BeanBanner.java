package vn.nip.around.Bean;

import java.util.List;

/**
 * Created by HOME on 8/30/2017.
 */

public class BeanBanner extends BeanBase {
    private int id;
    private String title;
    private String vn_title;
    private int type;
    private int show_number;
    private String start_date;
    private String end_date;
    private List<BeanBannerItem> contents;

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

    public String getVn_title() {
        return vn_title;
    }

    public void setVn_title(String vn_title) {
        this.vn_title = vn_title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getShow_number() {
        return show_number;
    }

    public void setShow_number(int show_number) {
        this.show_number = show_number;
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

    public List<BeanBannerItem> getContents() {
        return contents;
    }

    public void setContents(List<BeanBannerItem> contents) {
        this.contents = contents;
    }
}


