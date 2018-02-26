package com.orah.meetesh.campusdock.Services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.orah.meetesh.campusdock.Activities.HomeActivity;
import com.orah.meetesh.campusdock.Classes.Event;
import com.orah.meetesh.campusdock.Utils.Config;
import com.orah.meetesh.campusdock.Utils.EventDB;
import com.orah.meetesh.campusdock.Utils.NotiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.orah.meetesh.campusdock.Utils.Config.TYPE_CLASS;
import static com.orah.meetesh.campusdock.Utils.Config.TYPE_EVENT;

/**
 * Created by ogil on 14/01/18.
 */

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = CustomFirebaseInstanceService.class.getSimpleName();

    private NotiUtil notificationUtils;
    private SharedPreferences pref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null) return;

        pref = this.getSharedPreferences(Config.PREF_NAME, MODE_PRIVATE);
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotiUtil.isAppIsInBackground(getApplicationContext())) {

            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTI);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotiUtil notificationUtils = new NotiUtil(getApplicationContext());
            notificationUtils.playNotificationSound();
        }
    }

    public static final String PREF_EVENT_BASE = "event-";
    public static final String PREF_EVENT_COUNT = "eventCount";

    private void handleDataMessage(JSONObject obj) throws JSONException{
        Log.e(TAG, "pushed json: " + obj.toString());

        JSONObject data = obj.getJSONObject("content");

        final String title = data.getString("title");
        final String message = data.getString("description");
        final String imageUrl = data.getString("url");
        final String timestamp = data.getString("timestamp");
        final JSONObject payload = new JSONObject(data.getString("payload"));

        Log.e(TAG, "title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "payload: " + payload.toString());
        Log.e(TAG, "imageUrl: " + imageUrl);
        Log.e(TAG, "timestamp: " + timestamp);

        //payload is a json which can contain JSON data about event.
        // {"type":"event", "name":"Demo Event", "description":"This is the test description", "date":"JAN 21", "organizer":"FCS", "category":"Demo" }

        switch (payload.getString("type")){
            case TYPE_EVENT :
                EventDB.getIntsance(getApplicationContext()).getEventDao().insert(new Event(payload.getString("name"), payload.getString("description").replace("\r\n", "<br>"), payload.getString("date"), payload.getString("organizer"), payload.getString("category"), payload.getString("url"), payload.getString("created_by")));
                if (!NotiUtil.isAppIsInBackground(getApplicationContext())) {
                    NotiUtil notificationUtils = new NotiUtil(getApplicationContext());
                    notificationUtils.getBitmapFromURL(imageUrl);

                    Intent pushNotification = new Intent(Config.PUSH_NOTI);
                    pushNotification.putExtra(TYPE_EVENT, payload.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                }
                else {
                    Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    resultIntent.setAction(Config.PUSH_NOTI);
                    resultIntent.putExtra(TYPE_EVENT, payload.toString());
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    if (TextUtils.isEmpty(imageUrl)) {
                        showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                    }
                    else {
                        showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                    }
                }
                break;
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotiUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotiUtil(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
