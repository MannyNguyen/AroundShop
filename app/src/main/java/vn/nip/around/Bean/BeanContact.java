package vn.nip.around.Bean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HOME on 10/4/2017.
 */

public class BeanContact {

    private static List<BeanContact> list;

    public static List<BeanContact> get(Context context) {
        if (list == null) {
            list = getContacts(context);
        }
        return list;
    }

    private static Map map;

    //private String id;
    private String name = StringUtils.EMPTY;
    private String phone1 = StringUtils.EMPTY;
    private String phone2 = StringUtils.EMPTY;
    private String phone3 = StringUtils.EMPTY;
    private String mail = StringUtils.EMPTY;
    //private Bitmap photo;
    //private Uri photoURI;

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

//    public Bitmap getPhoto() {
//        return photo;
//    }
//
//    public void setPhoto(Bitmap photo) {
//        this.photo = photo;
//    }
//
//    public Uri getPhotoURI() {
//        return photoURI;
//    }
//
//    public void setPhotoURI(Uri photoURI) {
//        this.photoURI = photoURI;
//    }

    private static List<BeanContact> getContacts(Context ctx) {
        List<BeanContact> list = new ArrayList<>();
        try {
            ContentResolver contentResolver = ctx.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        //                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                        //                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));

                        //                    Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id));
                        //                    Uri pURI = Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                        //                    Bitmap photo = null;
                        //                    if (inputStream != null) {
                        //                        photo = BitmapFactory.decodeStream(inputStream);
                        //                    }
                        while (cursorInfo.moveToNext()) {
                            BeanContact info = new BeanContact();
                            String phone = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (phone != null) {
                                phone = phone.replaceAll("\\D", "");
                                phone = phone.replaceAll("&", "");
                                phone.replace("|", "");
                                phone = phone.replace("|", "");
                            }
                            info.setPhone1(phone + "");
                            info.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) + "");
                            //info.setMail(cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)) + "");
                            list.add(info);
                        }

                        cursorInfo.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Map getMapContact(Context ctx) {
        if (map != null) {
            return map;
        }
        try {
            ContentResolver contentResolver = ctx.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        while (cursorInfo.moveToNext()) {
                            BeanSendGift info = new BeanSendGift();
                            String phone = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (phone != null) {
                                phone = phone.replaceAll("\\D", "");
                                phone = phone.replaceAll("&", "");
                                phone.replace("|", "");
                                phone = phone.replace("|", "");
                            }
                            info.setPhone(phone + "");
                            info.setFullname(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)) + "");
                            //info.setMail(cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)) + "");
                            map.put(phone, info);
                        }

                        cursorInfo.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getPhone3() {
        return phone3;
    }

    public void setPhone3(String phone3) {
        this.phone3 = phone3;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
