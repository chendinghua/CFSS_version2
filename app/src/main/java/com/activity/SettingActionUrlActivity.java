package com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ycexample.small.R;

/**  设置访问路径页面
 * Created by 16486 on 2020/3/10.
 */
public class SettingActionUrlActivity extends Activity {

    EditText etInputActionUrl;

    Button btnSaveActionUrl;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    RadioButton no,yes;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_url);
        sp=getSharedPreferences("setting_action_url_config", Context.MODE_PRIVATE);
        editor = sp.edit();
        initUI();
        if(sp.getBoolean("isTest",false)){
            yes.setChecked(true);
            no.setChecked(false);
        }else{
            yes.setChecked(false);
            no.setChecked(true);
        }



        etInputActionUrl.setText(sp.getString("actionUrl","http://rfid.haiyangli.cn:8081/dagl"));

    }

    private void initUI() {
        yes= findViewById(R.id.rb_isTest_yes);
        no = findViewById(R.id.rb_isTest_no);

        etInputActionUrl = findViewById(R.id.et_input_action_url);
        btnSaveActionUrl = findViewById(R.id.btn_save_action_url);
        btnSaveActionUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("actionUrl",etInputActionUrl.getText().toString());
                editor.putBoolean("isTest",yes.isChecked());

                editor.apply();
                Toast.makeText(SettingActionUrlActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
