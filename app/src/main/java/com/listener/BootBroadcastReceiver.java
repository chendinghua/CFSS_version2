package com.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.activity.InventoryReadActivity;

/**
 * Created by Administrator on 2019/6/10/010.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, InventoryReadActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }

}
