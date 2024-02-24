package vn.nip.around.Bean;

/**
 * Created by HOME on 11/21/2017.
 */

public class BeanSendGift extends BeanBase {
    private String phone;
    private String fullname;
    private String avatar;
    private String birthday;
    private String sms_message;
    private String fb_link;
    private String android_link;
    private String ios_link;
    private boolean isInstall;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSms_message() {
        return sms_message;
    }

    public void setSms_message(String sms_message) {
        this.sms_message = sms_message;
    }

    public String getFb_link() {
        return fb_link;
    }

    public void setFb_link(String fb_link) {
        this.fb_link = fb_link;
    }

    public String getAndroid_link() {
        return android_link;
    }

    public void setAndroid_link(String android_link) {
        this.android_link = android_link;
    }

    public String getIos_link() {
        return ios_link;
    }

    public void setIos_link(String ios_link) {
        this.ios_link = ios_link;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean install) {
        isInstall = install;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
