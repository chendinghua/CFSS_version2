package com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.activity.LoginActivity;

/**
 * Created by Administrator on 2019/1/18/018.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    //重写onReceive方法
    @Override
    public void onReceive(Context context, Intent intent) {
        //后边的XXX.class就是要启动的服务
     /*   Intent service = new Intent(context, ExampleService.class);
        context.stopService(service);
        Log.d("stopReceiver", "onReceive: ");*/

     Intent intent1 = new Intent(context, LoginActivity.class);
        context.startActivity(intent1);

        /*  Intent service = new Intent(context, ExampleService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isStart",true);
        service.putExtras(bundle);
        context.startService(service);
        Log.d("start", "开机自动服务自动启动.....");
        Intent taskService = new Intent(context, AppListener.class);
        context.startService(taskService);
      */  //启动应用，参数为需要自动启动的应用的包名
       /* Intent intent =  getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);*/
    }

}