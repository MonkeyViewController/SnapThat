package com.monkeyviewcontroller.snapthat;

/**
 * Created by isaacsiegel on 5/25/15.
 */
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

public class PushReceiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Log.i("push", "received message: " + intent.getExtras());
        context.sendBroadcast(new Intent("com.monkeyviewcontroller.snapthat"));
    }
}