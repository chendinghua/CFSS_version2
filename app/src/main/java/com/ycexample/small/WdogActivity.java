package com.ycexample.small;

import java.util.Timer;
import java.util.TimerTask;

import com.ychmi.sdk.YcApi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class WdogActivity extends Activity{
	
	Timer timer;
	int tempxx=0;
	byte   gWdtInterval=10;
	int    gCounterDog=0;
	YcApi ycapi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wdog);
		
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		ycapi = new YcApi();
		
		ycapi.HideNviBar(getWindow().getDecorView());
		
		WdogInit();
		
		//���ذ�ť�Ĳ���
        Button mButtonReturn=(Button)findViewById(R.id.buttonreturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WdogActivity.this.finish();
			}
		});
	}
	private void WdogInit()
    { 
		final EditText wdtIntervalText = (EditText)findViewById(R.id.editTextWdogSetTime);
		
		
		
		//Android��ʱ���߳��в������UI���в����������Ҫͨ����Ϣ�Ļ���������UI
    	
    	final Handler handler = new Handler()
    	{
    		public void handleMessage(Message msg)
    		{   
    			if(msg.what>=0)
	    		{
    				wdtIntervalText.setText(""+msg.what);
    				
	    		}	
    			super.handleMessage(msg);
    		}   
    	}; 
    	
    	Button startDog = (Button)findViewById(R.id.buttonStartWdog);
    	startDog.setOnClickListener(new View.OnClickListener() 
		{			
			public void onClick(View arg0) 
			{
				tempxx=Integer.parseInt(wdtIntervalText.getText().toString());
				
				
				if((tempxx<=0)||(tempxx>60))
				{				
					Toast.makeText(WdogActivity.this, "��������Ϊ����0��С�ڵ���60", Toast.LENGTH_LONG).show(); 
				}
				else
				{
					gWdtInterval=(byte)tempxx;
					gCounterDog=(int)gWdtInterval;
					ycapi.SetWDog(gWdtInterval);
					timer = new Timer();
					timer.schedule
					(
							new TimerTask()
							{										
								public void run()
								{
									Message msg = new Message();
									msg.what = gCounterDog--;
									handler.sendMessage(msg);											
								}
							}
							,1
							,1000
					);							
					ycapi.StartWDog();
				}
			}
		}); 
    	Button stopDog = (Button)findViewById(R.id.buttonStopWdog);
    	stopDog.setOnClickListener(new View.OnClickListener() 
		{			
			public void onClick(View arg0) 
			{
				ycapi.StopWDog();
				wdtIntervalText.setText(""+gWdtInterval);							
				timer.cancel();	
			}
		});     	
		
		Button feedDog = (Button)findViewById(R.id.buttonFeedWdog);
		feedDog.setOnClickListener(new View.OnClickListener() 
		{			
			public void onClick(View arg0) 
			{
				// TODO Auto-generated method stub
				ycapi.FeedWDog();
				wdtIntervalText.setText(""+gWdtInterval);
				gCounterDog=gWdtInterval;
			}
		}); 
    }
}
