package com.tools;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Administrator on 2019/1/16/016.
 */

public class HandlerUtils extends Handler {
    private HandlerUtilsCallback callback;
    private HandlerUtilsErrorCallback errorCallback;
    private Context context;
    public HandlerUtils(Context context, HandlerUtilsCallback callback) {
        this.callback=callback;
        this.context=context;
    }
    public HandlerUtils(Context context, HandlerUtilsCallback callback, HandlerUtilsErrorCallback errorCallback){
        this.context=context;
        this.callback=callback;
        this.errorCallback=errorCallback;
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what){
            case 1:
                callback.handlerExecutionFunction(msg);
                break;
            case -1:
                if(context!=null)
                    if(errorCallback!=null){
                     errorCallback.handlerErrorFunction(msg);
                    }else {
                        UIHelper.ToastMessage(context,"访问远程服务有误");
                    }
                break;

        }
        super.handleMessage(msg);
    }
}
