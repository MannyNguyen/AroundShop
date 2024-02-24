package vn.nip.around.Class;

import android.util.Log;

import vn.nip.around.Helper.StorageHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by viminh on 1/16/2017.
 */

public class PlaceAPI {

    private static final String TAG = PlaceAPI.class.getSimpleName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";


    public ArrayList<JSONObject> autocomplete(String input) {
        ArrayList<JSONObject> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + CmmVariable.GOOGLE_KEY);
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&components=");
            sb.append("country:vn");
            sb.append("&language=vi");


            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Log.d(TAG, jsonResults.toString());

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<JSONObject>();
            for (int i = 0; i < predsJsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("description", predsJsonArray.getJSONObject(i).getString("description"));
                jsonObject.put("place_id", predsJsonArray.getJSONObject(i).getString("place_id"));
                JSONObject structured_formatting = new JSONObject(predsJsonArray.getJSONObject(i).getString("structured_formatting"));
                jsonObject.put("name", structured_formatting.getString("main_text"));
                resultList.add(jsonObject);
            }
            if (!StorageHelper.get("home").equals("")) {
                JSONObject jsonObject = new JSONObject(StorageHelper.get("home"));
                String address = jsonObject.getString("address");
                if (input.indexOf("h") > -1 || input.indexOf("ho") > -1 || input.indexOf("hom") > -1 || input.indexOf("home") > -1 || address.indexOf(input) > -1 || unAccent(address).indexOf(input) > -1) {
                    JSONObject home = jsonObject;
                    home.put("description", jsonObject.get("address"));
                    home.put("name", "HOME");
                    resultList.add(0, home);
                }
            }

            if (!StorageHelper.get("work").equals("")) {
                JSONObject jsonObject = new JSONObject(StorageHelper.get("work"));
                String address = jsonObject.getString("address");
                if (input.indexOf("w") > -1 || input.indexOf("wo") > -1 || input.indexOf("wor") > -1 || input.indexOf("work") > -1
                        || address.indexOf(input) > -1 || unAccent(address).indexOf(input) > -1) {
                    JSONObject home = jsonObject;
                    home.put("description", jsonObject.get("address"));
                    home.put("name", "WORK");
                    resultList.add(0, home);
                }
            }

            if (!StorageHelper.get("place").equals("")) {
                JSONObject jsonObject = new JSONObject(StorageHelper.get("place"));
                String address = jsonObject.getString("address");
                if (input.indexOf("pl") > -1 || input.indexOf("pla") > -1 || input.indexOf("plac") > -1 || input.indexOf("place") > -1 ||
                        address.indexOf(input) > -1 || unAccent(address).indexOf(input) > -1) {
                    JSONObject home = jsonObject;
                    home.put("description", jsonObject.get("address"));
                    home.put("name", "PLACE");
                    resultList.add(0, home);
                }

            }

        } catch (Exception e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    private String unAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
