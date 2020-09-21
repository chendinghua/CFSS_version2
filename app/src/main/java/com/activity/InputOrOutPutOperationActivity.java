package com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.ycexample.small.R;

/**
 * Created by Administrator on 2019/4/17/017.
 */

public class InputOrOutPutOperationActivity extends Activity implements View.OnClickListener{

    Button input;
    Button outPut;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_or_output_operation);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        input =(Button)findViewById(R.id.btn_input);
        outPut = (Button) findViewById(R.id.btn_output);
        input.setOnClickListener(this);
        outPut.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_input:
                SharedPreferences.Editor editor=getSharedPreferences("login_config", Context.MODE_PRIVATE).edit();
                editor.putInt("flag",1);
                editor.apply();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_output:
                SharedPreferences.Editor editor1=getSharedPreferences("login_config", Context.MODE_PRIVATE).edit();
                editor1.putInt("flag",2);
                editor1.apply();
                Intent intent1 = new Intent(this,MainActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
