package vn.nip.around.Helper;

import android.provider.MediaStore;

import vn.nip.around.Class.CmmFunc;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by viminh on 10/17/2016.
 */

public class HttpHelper {

    //region POST

    public static String post(String url, List<Map.Entry<String, String>> params) {
        String retValue = "";

        String uri = url;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        List nameValuePairs = new ArrayList();
        if (params != null) {
            for (Map.Entry<String, String> param : params) {
                nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            return null;
        }
        return retValue;
    }

    public static String post(String url, List<Map.Entry<String, String>> params, boolean isParam) {
        String retValue = "";

        String uri = url;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uri);
        List nameValuePairs = new ArrayList();
        if (params != null) {
            for (Map.Entry<String, String> param : params) {
                nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
            }
        }
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
            httpPost.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            return null;
        }
        return retValue;
    }

    //endregion

    //region GET
    public static String get(String url, List<Map.Entry<String, String>> params) {
        String retValue = "";
        String uri = url;
        if (params != null) {
            for (Map.Entry<String, String> param : params) {
                if (!uri.contains("?")) {
                    uri += "?" + param.getKey() + "=" + param.getValue();
                } else {
                    uri += "&" + param.getKey() + "=" + param.getValue();
                }

            }
        }
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            retValue = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            return null;
        }
        return retValue;
    }
    //endregion

    //region Post file
    public static String postFile(String url, List<Map.Entry<String, String>> params, List<Map.Entry<String, File>> files) {
        String retValue = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            if (files != null) {
                for (Map.Entry<String, File> param : files) {
                    multipartEntityBuilder.addBinaryBody(
                            param.getKey(),
                            new FileInputStream(param.getValue()),
                            ContentType.APPLICATION_OCTET_STREAM,
                            param.getValue().getName());
                }
            }
            if (params != null) {
                for (Map.Entry<String, String> param : params) {
                    multipartEntityBuilder.addTextBody(param.getKey(), param.getValue(), ContentType.TEXT_PLAIN.withCharset("UTF-8"));
                }
            }
            postRequest.setEntity(multipartEntityBuilder.build());
            HttpResponse response = httpClient.execute(postRequest);
            retValue = EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retValue;
    }
    //endregion
}
