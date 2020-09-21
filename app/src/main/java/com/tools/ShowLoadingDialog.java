package com.tools;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ycexample.small.R;

/**
 * Created by Administrator on 2019/5/22/022.
 */

public class ShowLoadingDialog {

    static Handler   resultHandler;
    public static void show(final PopupWindow popupWindow, final Activity activity){
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(false);
        View view = LayoutInflater.from(activity).inflate(R.layout.popup,null);
        popupWindow.setContentView(view);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER,0,0);
        resultHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(popupWindow.isShowing()) {
                    Toast.makeText(activity, "请求超时",Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                }
            }
        };
   //     new Handler().postDelayed(runnable,10000);
    }

    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
                  resultHandler.sendMessage(null);
        }
    };
}
