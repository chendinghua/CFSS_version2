package com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tools.HandlerUtils;
import com.tools.HandlerUtilsCallback;
import com.tools.InteractiveDataUtil;
import com.tools.InteractiveEnum;
import com.tools.MethodEnum;
import com.tools.NetUtil;
import com.tools.UIHelper;
import com.ycexample.small.R;
import com.ychmi.sdk.YcApi;

import java.util.HashMap;

/**
 * Created by Administrator on 2019/4/2/002.
 */

public class LoginActivity extends Activity  implements View.OnClickListener{
    EditText userName;
    EditText pwd;
    Button btnLogin;

    YcApi ycApi = new YcApi();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        initUI();
        btnLogin.setOnClickListener(this);
        SharedPreferences sp=getSharedPreferences("login_config", Context.MODE_PRIVATE);
        userName.setText(sp.getString("username",null));
        pwd.setText(sp.getString("password",null));
        isLoading=true;
        errorHandler.postDelayed(errorRunnable,1);

    }
    private void initUI() {
        userName =(EditText)findViewById(R.id.et_login_userName);
        pwd = (EditText)findViewById(R.id.et_login_pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }
    //第一次点击事件发生的时间
    private long mExitTime;
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                isLoading=false;
                errorHandler.postDelayed(errorRunnable,1);

                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        }
        return true;

    }

    boolean isLoading=true;

    Handler errorHandler = new Handler();
    Runnable errorRunnable = new Runnable() {
        @Override
        public void run() {
            if(isLoading) {
                ycApi.SetIO(0, 1);
                ycApi.SetIO(0, 2);
                ycApi.SetIO(1, 0);
            }else{
                ycApi.SetIO(1, 1);
                ycApi.SetIO(1, 2);
                ycApi.SetIO(1, 0);
            }

        }
    };

    @Override
    protected void onRestart() {
        isLoading=true;
        errorHandler.postDelayed(errorRunnable,1);
        super.onRestart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                if(!NetUtil.ping() && !NetUtil.isNetworkConnected(this)){
                    Toast.makeText(this,"无网络连接",Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String,Object> loginParam = new HashMap<>();
                loginParam.put("code",userName.getText().toString());
                loginParam.put("password",pwd.getText().toString());
                loginParam.put("flag",3);

                InteractiveDataUtil.interactiveMessage(LoginActivity.this,loginParam,new HandlerUtils(this, new HandlerUtilsCallback() {
                    @Override
                    public void handlerExecutionFunction(Message msg) {
                        switch (JSON.parseObject(msg.getData().getString("result")).getInteger("code")){
                            //登录成功
                            case 200:
                                UIHelper.ToastMessage(LoginActivity.this,JSON.parseObject(msg.getData().getString("result")).getString("message"));
                                SharedPreferences.Editor editor=getSharedPreferences("login_config", Context.MODE_PRIVATE).edit();
                                editor.putString("records",JSON.parseObject(msg.getData().getString("result")).getString("records"));
                                editor.putString("token",JSON.parseObject(msg.getData().getString("result")).getString("token"));
                                editor.putString("username",userName.getText().toString());
                                editor.putString("password",pwd.getText().toString());
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this,InputOrOutPutOperationActivity.class);
                                startActivity(intent);
                                break;
                            //用户锁定
                            case 201:
                                //密码错误
                            case 202:
                                //用户名不存在
                            case 203:
                                UIHelper.ToastMessage(LoginActivity.this,JSON.parseObject(msg.getData().getString("result")).getString("message"));
                                break;
                        }
                    }
                }), MethodEnum.LOGINCATE, InteractiveEnum.POST,null,false);

                break;
        }
    }
}
