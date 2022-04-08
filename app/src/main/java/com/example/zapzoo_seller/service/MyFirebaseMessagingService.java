package com.example.zapzoo_seller.service;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.zapzoo_seller.NotificationFunc;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private SharedPreferences preferences;
    private NotificationFunc func;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        func = new NotificationFunc(getApplicationContext());
        func.sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        preferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("fcmToken", s);
        editor.commit();
    }
}
