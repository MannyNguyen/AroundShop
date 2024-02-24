package vn.nip.around.Class;

import android.graphics.Bitmap;

import vn.nip.around.Bean.BeanChat;
import vn.nip.around.Bean.BeanPoint;

import com.google.android.gms.location.places.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by viminh on 10/11/2016.
 */

public class CmmVariable {

    public static String linkServer = "http://config.around.vn/dev/info.json";

    public static final String version = "1.0.3";
    //code request
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 101;
    public static final int MY_PERMISSION_ACCESS_CALL_PHONE = 102;
    public static final int MY_PERMISSION_ACCESS_WRITE_EXTERNAL_STORAGE = 103;
    public static final int MY_PERMISSION_ACCESS_LOCATION = 104;
    public static final int MY_PERMISSION_ACCESS_WIFI = 105;
    public static final int READ_EXTERNAL_STORAGE = 106;
    //Server

    //Image
    public static final int IMAGE_CHAT_SIZE = 512;
    public static final int AVATAR_SHOP = 256;
    public static final int AVATAR = 256;

    public static final int SLEEP = 400;

    public static String smartFoxIP;
    public static String smartFoxDomain;
    public static String smartFoxPort;
    public static String restfulIP;
    public static String restfulPort;
    //timeout
    public static int timeout = 30;
    public static List<BeanPoint> points = new ArrayList<>();
    public static List<BeanChat> chats = new ArrayList<>();

    //Error
    public static JSONArray jsonError;
    //Avatar
    public static Bitmap avatarShipper;
    public static Bitmap avatarUser;
    //Info login
    public static int shipperPositionTimeInJourney;
    public static int shipperUpdatePositionTime;

    //Thời gian follow 5s - ngoài follow 10s
    public static int shipperPositionTimeOutJourney;
    public static String GOOGLE_KEY = "";

    public static String getDomainAPI() {
        return restfulIP + ":" + restfulPort;
    }

    //Số lần gọi lại request
    public static int deliveryTime; // 90 min
    public static int unDeliveryTime; // 60 min
    public static int showCancelRequest; // seconds
    public static int maxDay;
    public static int minGiftPaymentCost;

    //Popup anh gia
    public static String popupPriceVN;
    public static String popupPriceEN;

    public static String popupPickupServiceFeeVN;
    public static String popupPickupServiceFeeEN;
    public static String popupGiftingServiceFeeEN;
    public static String popupGiftingServiceFeeVN;

    public static JSONArray onepayTypes;
    public static String phoneService = "";

    public static String giftBoxFee;
    public static int minPurchaseFee;
    public static int maxCodFee;
    public static int minCodeFee;

    public static JSONObject aroundPay;

    public static int minGiftAroundPay;
    public static int maxGiftAroundPay;

    //Danh sách ID sản phẩm trong giỏ hàng
    public static List<Integer> producteds = new ArrayList<>();
}
