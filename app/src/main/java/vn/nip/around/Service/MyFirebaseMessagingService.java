package vn.nip.around.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import vn.nip.around.AppActivity;
import vn.nip.around.Class.CmmFunc;
import vn.nip.around.Class.GlobalClass;
import vn.nip.around.MainActivity;
import vn.nip.around.R;
import vn.nip.around.Util.NotificationUtils;

/**
 * Created by viminh on 5/16/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private NotificationUtils notificationUtils;
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        // TODO: Handle FCM messages here.
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
//
//        try {
//            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
//            notiStyle.setSummaryText(remoteMessage.getNotification().getBody());
//            if (remoteMessage.getData().containsKey("main_picture")) {
//                notiStyle.bigPicture(CmmFunc.getBitmapFromURL(remoteMessage.getData().get("main_picture") + ""));
//            }
//            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Intent configureIntent = new Intent(getApplicationContext(), MainActivity.class);
//            configureIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingClearScreenIntent = PendingIntent.getActivity(getApplicationContext(), 0, configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    //.setLargeIcon(bigIcon)
//                    .setContentTitle(remoteMessage.getNotification().getTitle())
//                    .setContentText(remoteMessage.getNotification().getBody())
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setOngoing(true)
//                    .setContentIntent(pendingClearScreenIntent)
//                    .setStyle(notiStyle);
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(0, notificationBuilder.build());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage == null)
            return;

        // Default
        if (remoteMessage.getNotification() != null) {
            //Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            //handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                //JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
//                Log.e("JSON_OBJECT", object.toString());
                handleDataMessage(object);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleDataMessage(JSONObject data) {
        Log.e(TAG, "push json: " + data.toString());
        try {
            //JSONObject data = json.getJSONObject("data");
            String title = data.optString("title");
            String message = data.optString("body");
            String type = data.optString("deep_link_type");
            //boolean isBackground = data.getBoolean("is_background");
            String mainPicture = data.optString("main_picture");
            String timestamp = new DateTime().toString("yyyy-MM-dd HH:mm:ss");

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(NotificationUtils.PUSH_NOTIFICATION);
//                pushNotification.putExtra("type", type);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                //notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
            }
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("type", type);
            resultIntent.putExtra("id", data.optString("id", "0"));
            resultIntent.putExtra("subId", data.optString("sub_id", "0"));

            // check for image attachment
            if (TextUtils.isEmpty(mainPicture)) {
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            } else {
                // image is present, show notification with image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, mainPicture);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

}
