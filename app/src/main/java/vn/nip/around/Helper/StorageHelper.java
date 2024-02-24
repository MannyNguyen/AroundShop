package vn.nip.around.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by viminh on 10/24/2016.
 */

public class StorageHelper {
    public static final String TUT_BOOK = "TUT_BOOK";
    public static final String TUT_LIST_ITEM = "TUT_LIST_ITEM";
    public static final String TUT_GIFT_ORDER = "TUT_GIFT_ORDER";
    public static final String ORDER_LIST = "ORDER_LIST";
    public static final String SEARCH_HISTORY = "SEARCH_HISTORY";
    public static final String LIST = "LIST";
    public static final String GRID = "GRID";
    public static final String HOME = "home";
    public static final String WORK = "work";
    public static final String OTHER = "other";

    private static SharedPreferences preferences;

    public static SharedPreferences getPreferences() {
        if (preferences == null) {
            init(GlobalClass.getContext());
        }
        return preferences;
    }

    public static void init(Context context) {
        String PREF_FILE_NAME = "Around_User";
        preferences = context.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    }

    public static void resetUser() {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("phone", "");
            editor.putString("token", "");
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveUser", e.getMessage());
        }
    }

    public static void saveToken(String token) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("token", token);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveToken", e.getMessage());
        }
    }

    public static void savePhone(String phone) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("phone", phone);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "savePhone", e.getMessage());
        }
    }

    public static void saveAvatar(String avatar) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("avatar", avatar);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveAvatar", e.getMessage());
        }
    }

    public static void saveCountryCode(String counryCode) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("country_code", counryCode);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveCountryCode", e.getMessage());
        }
    }

    public static void saveConfigVersion(int version) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putInt("config_version", version);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "config_version", e.getMessage());
        }
    }


    public static void saveLanguage(String counryCode) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("language", counryCode);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "saveCountryCode", e.getMessage());
        }
    }

    public static void saveContentError(String content) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("content_error", content);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "content_error", e.getMessage());
        }
    }

    public static String getContentVNError() {
        return getPreferences().getString("content_vn_error", "");
    }

    public static void saveContentVNError(String content) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString("content_vn_error", content);
            editor.commit();
        } catch (Exception e) {
            CmmFunc.showError("StorageHelper", "content_vn_error", e.getMessage());
        }
    }

    public static String getContentError() {
        return getPreferences().getString("content_error", "");
    }

    public static String getPhone() {
        return getPreferences().getString("phone", "");
    }

    public static String getToken() {
        return getPreferences().getString("token", "");
    }

    public static int getConfigVersion() {
        return getPreferences().getInt("config_version", 0);
    }

    public static String getAvatar() {
        return getPreferences().getString("avatar", "");
    }

    public static String getLanguage() {
        return getPreferences().getString("language", "vi");
    }

    public static String getCountryCode() {
        return getPreferences().getString("country_code", "");
    }

    public static void set(String key, String value) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return getPreferences().getString(key, "");
    }

    public static void setTut(String key, boolean value) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getTUT(String key) {
        return getPreferences().getBoolean(key, false);
    }

    public static void setContact(long value) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putLong("Contact", value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getContact() {
        return getPreferences().getLong("Contact", 0);
    }
}
