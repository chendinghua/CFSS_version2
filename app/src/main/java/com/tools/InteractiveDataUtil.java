package com.tools;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tools.HttpUtils;
import com.tools.InteractiveEnum;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/1/15/015.
 */

public class InteractiveDataUtil {
    public static void interactiveMessage(final Context context, final HashMap<String,Object> params, final Handler handler, final String method, final String interactiveType, final String token,final boolean isErrorInfo){
        new Thread(new Runnable() {
            @Override
            public  void run() {
                String result="";
                HttpUtils httpUtils = new HttpUtils();
                if(interactiveType.equals(InteractiveEnum.GET)){
                    result =httpUtils.sendGetMessage(params,method);
                }else if(interactiveType.equals(InteractiveEnum.POST)){
                    result = httpUtils.sendPostMessage(context,params,method,token,isErrorInfo);
                }else if(interactiveType.equals(InteractiveEnum.UPLOAD)){
                    result=httpUtils.uploadImage(params,method);
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();

                if(result.trim().equals("")){
                    msg.what=-1;
                }else {
                  /*  JSONObject object = JSON.parseObject(result);
                   msg.what = object.getInteger("code");*/
                    bundle.putString("result",result);
                    msg.what=1;
                }
                bundle.putString("method",method);
                msg.setData(bundle);
                if(handler!=null)
                    handler.sendMessage(msg);
            }
        }).start();

    }
}
