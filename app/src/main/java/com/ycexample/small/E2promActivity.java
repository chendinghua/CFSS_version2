package com.ycexample.small;
import java.io.UnsupportedEncodingException;

import com.ychmi.sdk.YcApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;


public class E2promActivity extends Activity{	

	YcApi ycapi; 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_e2prom);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		ycapi = new YcApi();
		
		ycapi.HideNviBar(getWindow().getDecorView());
		
		E2promInit();
		
		//���ذ�ť�Ĳ���
        Button mButtonReturn=(Button)findViewById(R.id.buttonreturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				E2promActivity.this.finish();
			}
		});
	}
	private void E2promInit()
    {  
		final EditText wE2promText = (EditText)findViewById(R.id.editTextWriteE2prom);
		final EditText rE2promText = (EditText)findViewById(R.id.editTextReadE2prom);
		Button wE2prom = (Button)findViewById(R.id.buttonWriteE2prom);
		final String s = "8,xx�й�";
		wE2prom.setOnClickListener(new View.OnClickListener()
		{			
			public void onClick(View arg0) 
			{	
		        try {
					//String s2=new String(s.getBytes("gbk"),"gbk");
		        	String s2=wE2promText.getText().toString();
					//wE2promText.setText(s2);
		        	String s3=new String(s2.getBytes("gbk"),"gbk");
		        	String s4=new String(s3.getBytes("utf-8"),"utf-8");
		        	rE2promText.setText(s4);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});  
		    
    }
}
