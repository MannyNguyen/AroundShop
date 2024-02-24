package vn.nip.around.Bean;

import android.graphics.Bitmap;

import java.text.DateFormat;

/**
 * Created by viminh on 11/9/2016.
 */

public class BeanChat extends BeanBase {
    private String message; //TEXT_TYPE  //IMAGE_TYPE
    private String chat_description; //contnt
    private byte[] image;
    private String urlImage;
    private Bitmap bitmap;
    private String sender;
    private DateFormat time;
    private int type;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChat_description() {
        return chat_description;
    }

    public void setChat_description(String chat_description) {
        this.chat_description = chat_description;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public DateFormat getTime() {
        return time;
    }

    public void setTime(DateFormat time) {
        this.time = time;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
