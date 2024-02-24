package vn.nip.around.Helper;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import vn.nip.around.Bean.BeanContact;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.CmmVariable;
import vn.nip.around.Class.GlobalClass;

/**
 * Created by HOME on 8/23/2017.
 */

public class APIHelper {
    private static void paramDefault(List<Map.Entry<String, String>> params) {
        params.add(new AbstractMap.SimpleEntry("country_code", StorageHelper.getCountryCode()));
        params.add(new AbstractMap.SimpleEntry("phone", StorageHelper.getPhone()));
        params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
        params.add(new AbstractMap.SimpleEntry("token", StorageHelper.getToken()));
        Log.d("phone", StorageHelper.getPhone());
        Log.d("token", StorageHelper.getToken());
    }

    public static String login(String countryCode, String phone, String fullName) {
        String response = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        params.add(new AbstractMap.SimpleEntry("country_code", countryCode));
        params.add(new AbstractMap.SimpleEntry("phone", phone));
        params.add(new AbstractMap.SimpleEntry("fullname", fullName));
        params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
        params.add(new AbstractMap.SimpleEntry("devicetoken", StringUtils.EMPTY));
        params.add(new AbstractMap.SimpleEntry("os", "ANDROID"));
        response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/auth", params, false);
        return response;
    }

    public static String verify(String countryCode, String phone, String otp) {
        String response = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        params.add(new AbstractMap.SimpleEntry("country_code", countryCode));
        params.add(new AbstractMap.SimpleEntry("phone", phone));
        params.add(new AbstractMap.SimpleEntry("otp", otp));
        params.add(new AbstractMap.SimpleEntry("deviceid", CmmFunc.getDeviceID(GlobalClass.getContext())));
        params.add(new AbstractMap.SimpleEntry("devicetoken", StringUtils.EMPTY));
        params.add(new AbstractMap.SimpleEntry("os", "ANDROID"));
        response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/verify", params, false);
        return response;
    }

    public static String getMainScreen() {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_main_function", params);
        return value;
    }

    public static String getMainBanner() {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_main_banner", params);
        return value;
    }


    public static String registerSupplier(String fullName, String storeName, String address, String phone, String email, String product, String website, String facebook, int type, List<File> files) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("supplier_fullname", fullName));
        params.add(new AbstractMap.SimpleEntry("supplier_shopname", storeName));
        params.add(new AbstractMap.SimpleEntry("supplier_address", address));
        params.add(new AbstractMap.SimpleEntry("supplier_phone", phone));
        params.add(new AbstractMap.SimpleEntry("supplier_email", email));
        params.add(new AbstractMap.SimpleEntry("supplier_product", product));
        params.add(new AbstractMap.SimpleEntry("supplier_website", website));
        params.add(new AbstractMap.SimpleEntry("supplier_facebook", facebook));
        params.add(new AbstractMap.SimpleEntry("supplier_type", type + ""));
        List<Map.Entry<String, File>> filess = new ArrayList<>();
        for (int i = 0; i < files.size() - 1; i++) {
            filess.add(new AbstractMap.SimpleEntry("image" + (i + 1), files.get(i)));
        }
        value = HttpHelper.postFile(CmmVariable.getDomainAPI() + "/user/signup_to_supplier", params, filess);
        return value;
    }

    public static String rating(int idReason, int star, int idOrder) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_reason", idReason + ""));
        params.add(new AbstractMap.SimpleEntry("star", star + ""));
        params.add(new AbstractMap.SimpleEntry("id_order", idOrder + ""));
        value = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/rating_12", params, false);
        return value;
    }

    public static String updateDropInfo(String name, String phone, String note) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("recipent_name", name + ""));
        params.add(new AbstractMap.SimpleEntry("recipent_phone", phone + ""));
        params.add(new AbstractMap.SimpleEntry("recipent_note", note + ""));
        value = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/update_drop_info", params, false);
        return value;
    }

    public static String getNotification(int page) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_notification", params);
        return value;
    }

    public static String getNotificationDetail(int id) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id", id + ""));
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_notification_detail", params);
        return value;
    }

    public static String getEvent(int page) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_event", params);
        return value;
    }

    public static String getBanners(int position) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("position", position + ""));
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_banner", params);
        return value;
    }

    public static String addProductToCart(int idProduct, int number) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_product", idProduct + ""));
        params.add(new AbstractMap.SimpleEntry("number", number + ""));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/add_product_to_cart", params);
        return response;
    }

    public static String getBannerDetail(int id) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_banner_detail", params);
        return response;
    }

    public static String getOrderStatus(int id) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_order", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_order_status", params);
        return response;
    }

    public static String getFullOrder(int id) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_order", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_full_order", params);
        return response;
    }

    public static String getProfile() {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_profile", params);
        return value;
    }

    public static String getAroundPayment() {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_around_pay", params);
        return value;
    }

    public static String deleteHistoryOrder(int orderID) {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_order", orderID + ""));

        value = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/delete_history_order", params);
        return value;
    }

    public static String sendContact(Context context) {
        String value = null;
        try {
            List<Map.Entry<String, String>> params = new ArrayList<>();
            paramDefault(params);
            List<BeanContact> contacts = BeanContact.get(context);
            String json = CmmFunc.tryParseObject(contacts);
            byte[] bytes = json.getBytes("UTF-8");
            String base64 = Base64.encodeToString(bytes, Base64.NO_WRAP);
            params.add(new AbstractMap.SimpleEntry("contact", base64));
            value = HttpHelper.postFile(CmmVariable.getDomainAPI() + "/user/upload_contact", params, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getRequestID() {
        String value = null;
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        value = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_request_order_id", params);
        return value;
    }

    public static String getProducts(int id, int page, String tab) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_category", id + ""));
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("tab", tab + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_category_content", params);
        return response;
    }

    public static String estimateCost(int year, int month, int day, int hour, int minute, boolean isReturnPickup, String locations) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("minute", minute + ""));
        params.add(new AbstractMap.SimpleEntry("day", day + ""));
        params.add(new AbstractMap.SimpleEntry("month", month + ""));
        params.add(new AbstractMap.SimpleEntry("hour", hour + ""));
        params.add(new AbstractMap.SimpleEntry("year", year + ""));
        params.add(new AbstractMap.SimpleEntry("return_to_pickup", isReturnPickup + ""));
        params.add(new AbstractMap.SimpleEntry("locations", locations));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/estimate_cost", params);
        return response;
    }

    public static String getGiftMe(int page, String tab) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("tab", tab + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_gift_me", params);
        return response;
    }

    public static String getGiftingCategoryContent(int id, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("id_category", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_gifting_category_content", params);
        return response;
    }

    public static String getShoppingCategoryContent(int id, int priceType, int idShop, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("id_category", id + ""));
        params.add(new AbstractMap.SimpleEntry("price_type", priceType + ""));
        params.add(new AbstractMap.SimpleEntry("id_shop", idShop + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_shopping_category_content", params);
        return response;
    }

    public static String getSuggestGiftingProduct(int id, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("id_category", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_gifting_suggest_product", params);
        return response;
    }

    public static String getSuggestShoppingProduct(int id, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("id_category", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_shopping_suggest_product", params);
        return response;
    }

    public static String getShoppingCategoryShop(int id) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_category", id + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_shopping_category_shop", params);
        return response;
    }

    public static int getNumberCart() {
        try {
            List<Map.Entry<String, String>> params = new ArrayList<>();
            paramDefault(params);
            String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_number_product_in_cart", params);
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt("code");
            if (code == 1) {
                JSONObject data = jsonObject.getJSONObject("data");
                int number = data.getInt("number");
                return number;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Integer> getCart() {
        List<Integer> items = new ArrayList<>();
        try {
            List<Map.Entry<String, String>> params = new ArrayList<>();
            paramDefault(params);
            String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_cart", params);
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt("code");
            if (code == 1) {

                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray products = data.getJSONArray("product");
                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);
                    items.add(product.getInt("id"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public static String addCart(int productID, int number) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
        params.add(new AbstractMap.SimpleEntry("number", 1 + ""));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/add_product_to_cart", params);
        return response;
    }

    public static String removeCart(int productID) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_product", productID + ""));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/clear_product_from_cart", params);
        return response;
    }

    public static String getGiftingMainCategory() {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_gifting_main_category", params);
        return response;
    }

    public static String getShoppingMainCategory() {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_shopping_main_category", params);
        return response;
    }

    public static String getGiftingCategoryProduct(int idCategory, int idTab, String sort, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("id_category", idCategory + ""));
        params.add(new AbstractMap.SimpleEntry("id_tab_category", idTab + ""));
        params.add(new AbstractMap.SimpleEntry("sort", sort + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_gifting_category_product", params);
        return response;
    }

    public static String getSendGift() {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_send_gift", params);
        return response;
    }

    public static String checkSendGift(String phone) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("contact_phone", phone));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/check_send_gift", params, false);
        return response;
    }

    public static String getSearchInfoKeyword(String type, int idCategory) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("type", type));
        params.add(new AbstractMap.SimpleEntry("id_category", idCategory + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_search_info_keyword", params);
        return response;
    }


    public static String clearSearchHistory(String type) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("type", type));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/clear_search_history", params);
        return response;
    }

    public static String searchInfo(String type, String key, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("type", type));
        params.add(new AbstractMap.SimpleEntry("keyword", URLEncoder.encode(key)));
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/search_info", params);
        return response;
    }

    public static String searchInfo(String type, String key, int page, int idShop, int idCategory) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("type", type));
        params.add(new AbstractMap.SimpleEntry("keyword", URLEncoder.encode(key)));
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        params.add(new AbstractMap.SimpleEntry("id_category", idCategory + ""));
        params.add(new AbstractMap.SimpleEntry("id_shop", idShop + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/search_info", params);
        return response;
    }

    public static String getShopProduct(int id, int page) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_shop", id + ""));
        params.add(new AbstractMap.SimpleEntry("page", page + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/get_shop_product", params);
        return response;
    }

    public static String getSuggestPickupLocation() {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_suggest_pickup_location", params);
        return response;
    }

    public static String checkGiftAroundPay(String phone, long money) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("friend_phone", phone));
        params.add(new AbstractMap.SimpleEntry("value", money + ""));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/check_gift_around_pay", params);
        return response;
    }

    public static String sendGiftAroundPay(String phone, long money, String message) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("friend_phone", phone));
        params.add(new AbstractMap.SimpleEntry("value", money + ""));
        params.add(new AbstractMap.SimpleEntry("message", message));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/send_gift_around_pay", params);
        return response;
    }

    public static String updateDeliveryInfo(List<JSONObject> locations) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("type", "LOCATION"));
        JSONArray jsonArray = new JSONArray();
        for (JSONObject jsonObject : locations) {
            jsonArray.put(jsonObject);
        }
        params.add(new AbstractMap.SimpleEntry("locations", jsonArray.toString()));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/update_delivery_info", params);
        return response;
    }

    public static String updateDeviceToken(String token) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("devicetoken", StorageHelper.getToken()));
        params.add(new AbstractMap.SimpleEntry("firebase_devicetoken", token));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/user/update_device_token", params);
        return response;
    }

    public static String reoderGifting(int order_id) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("order_id", order_id + ""));
        String response = HttpHelper.post(CmmVariable.getDomainAPI() + "/gifting/reorder", params, false);
        return response;
    }

    public static String checkCart() {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/gifting/check_cart", params);
        return response;
    }

    public static String reOrder(int orderId) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("order_id", orderId + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/get_order_status", params);
        return response;
    }

    public static String checkOrderPickupType(int id_order) {
        List<Map.Entry<String, String>> params = new ArrayList<>();
        paramDefault(params);
        params.add(new AbstractMap.SimpleEntry("id_order", id_order + ""));
        String response = HttpHelper.get(CmmVariable.getDomainAPI() + "/user/check_order_pickup_type", params);
        return response;
    }
}
