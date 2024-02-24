package vn.nip.around.Helper;

/**
 * Created by viminh on 10/11/2016.
 */

public class StringHelper {
    //region Check null or empty
    public static boolean isNullOrEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
    //endregion
}
