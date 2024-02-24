package vn.nip.around.Bean;

import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * Created by HOME on 11/23/2017.
 */

public class BeanCSChat extends BeanBase {
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int PRODUCT = 3;
    private String sender_username;
    private String sender_fullname;
    private String sender_avatar;
    private SFSObject chat;
    private String time;

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public String getSender_fullname() {
        return sender_fullname;
    }

    public void setSender_fullname(String sender_fullname) {
        this.sender_fullname = sender_fullname;
    }

    public String getSender_avatar() {
        return sender_avatar;
    }

    public void setSender_avatar(String sender_avatar) {
        this.sender_avatar = sender_avatar;
    }

    public SFSObject getChat() {
        return chat;
    }

    public void setChat(SFSObject chat) {
        this.chat = chat;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
