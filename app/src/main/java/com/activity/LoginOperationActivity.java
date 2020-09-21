package com.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.tools.MyCatchException;
import com.ycexample.small.R;

/**
 * Created by 16486 on 2020/3/10.
 */

public class LoginOperationActivity extends Activity implements View.OnClickListener{

    //操作按钮
    Button btnOperation;
    //设置按钮
    Button btnSetting;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_operation);
        initUI();
        initListener();
        //全局异常信息记录对象初始化
        MyCatchException mException= MyCatchException.getInstance();
        mException.init(getApplicationContext());  //注册
    }

    private void initListener() {
        btnSetting.setOnClickListener(this);
        btnOperation.setOnClickListener(this);

    }

    private void initUI() {
    btnOperation = findViewById(R.id.btn_operation);
    btnSetting = findViewById(R.id.btn_setting);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_operation:
                Intent intent1 = new Intent(this,InputOrOutPutOperationActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_setting:
                /*跳转设置操作页面*/
                Intent intent = new Intent(this,SettingActionUrlActivity.class);
                startActivity(intent);
                break;
        }
    }
}
