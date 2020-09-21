package com.ycexample.small;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.ychmi.sdk.YcApi;


public class YcActivity extends Activity {
	YcApi ycapi;
	
	public boolean mBeepStatus=false;
	public boolean mLedStatus=false;
	
	AudioManager mAudioManager;
	int max;
	int current;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yc);
		ycapi = new YcApi(); 
		//ycapi.HideNviBarFull();	
		
		ycapi.HideNviBar(getWindow().getDecorView());	
		
		mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		
		//��������ʼ��
		BeepInit();
		
		//״̬�Ƴ�ʼ��
		LedInit();		
			
		//(4)E2PROM��ʼ��
		E2promInit();
		
		//(5)���Ź���ʼ��
		WdogInit();
		
		//(6)IO��ʼ��
		BoardIDInit();
		
		//(7)���ڳ�ʼ��
		SerialInit();
		
		StartAndroidLauncher();	
		
		PowerOff();
		

		//���ذ�ť�Ĳ���
        Button mButtonReturn=(Button)findViewById(R.id.buttonreturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {		
				ycapi.ReturnLauncher(YcActivity.this);							
			}
		});
        addShortCut(YcActivity.this);
	}
	
	public void onBackPressed()//��ⷵ�ؼ��ĺ���
	{
		ycapi.ReturnLauncher(YcActivity.this);
	}
	
	
	//(1)��������ʼ��
	private void BeepInit()
	{
		ImageButton btnBeep = (ImageButton)findViewById(R.id.imageButtonBeep);          
	    btnBeep.setOnTouchListener(new View.OnTouchListener(){   
	    	@Override
	        public boolean onTouch(View v, MotionEvent event) {               
	                if(event.getAction() == MotionEvent.ACTION_DOWN){   
	                	
	                   //�������ð���ʱ�ı���ͼƬ  
	                	if(mBeepStatus)
	                	{
	                		((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.beep2)); 
	                		mBeepStatus=false;
	                		ycapi.SetBeep(false);
	                		
	                	}
	                	else
	                	{
	                		((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.beep)); 
	                		mBeepStatus=true;
	                		ycapi.SetBeep(true);	                		
	                	}
	                }	        
	                return false;       
	        }      
	    });  
	}
	
	
	private void StartAndroidLauncher()
	{
		final ImageButton button= (ImageButton)findViewById(R.id.imageButtonLauncher);
    	button.setOnClickListener(new View.OnClickListener() 
    	{
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_MAIN); 
				intent.addCategory(Intent.CATEGORY_LAUNCHER);             
				ComponentName cn = new ComponentName("com.android.launcher", "com.android.launcher2.Launcher");             
				intent.setComponent(cn); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				startActivity(intent);	 
			}
		});		
	}

	//(2)״̬�Ƴ�ʼ��
	private void LedInit()
	{
		ImageButton btnLed = (ImageButton)findViewById(R.id.imageButtonLed);          
		btnLed.setOnTouchListener(new View.OnTouchListener(){   
	    	@Override
	        public boolean onTouch(View v, MotionEvent event) {               
	                if(event.getAction() == MotionEvent.ACTION_DOWN){       
	                   //�������ð���ʱ�ı���ͼƬ  
	                	if(mLedStatus)
	                	{
	                		((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.led2)); 
	                		mLedStatus=false;
	                		ycapi.SetLed(false);
	                		//ycapi.HideNviBarFull();
	                	}
	                	else
	                	{
	                		((ImageButton)v).setImageDrawable(getResources().getDrawable(R.drawable.led)); 
	                		mLedStatus=true;
	                		ycapi.SetLed(true);
	                		//ycapi.ShowNviBarFull();
	                	}
	                }	        
	                return false;       
	        }      
	    });  
	}
	
	//(2)�ػ�
		private void PowerOff()
		{
			ImageButton btnLed = (ImageButton)findViewById(R.id.imageButtonPowerOff);          
			btnLed.setOnTouchListener(new View.OnTouchListener(){   
		    	@Override
		        public boolean onTouch(View v, MotionEvent event) {               
		                if(event.getAction() == MotionEvent.ACTION_DOWN)
		                {       
		                   
		                	 new AlertDialog.Builder(YcActivity.this).setTitle("ϵͳ��ʾ")//���öԻ������  		                	   
		                	      .setMessage("ȷ���ػ���")//������ʾ������  		                	   
		                	      .setPositiveButton("ȷ��",new DialogInterface.OnClickListener() {//���ȷ����ť    
		                	          @Override  		                	   
		                	          public void onClick(DialogInterface dialog, int which) {//ȷ����ť����Ӧ�¼�  		                	   
		                	              // TODO Auto-generated method stub 
		                	              ycapi.PowerOff();
		                	              finish();
		                	          }  		                	   
		                	      }).setNegativeButton("����",new DialogInterface.OnClickListener() {//��ӷ��ذ�ť  
		                	   
		                	          @Override  		                	   
		                	          public void onClick(DialogInterface dialog, int which) {//��Ӧ�¼�  		                	   
		                	              // TODO Auto-generated method stub  		                	   
		                	             // Log.i("alertdialog"," �뱣�����ݣ�");  		                	   
		                	          }  		                	   
		                	      }).show();//�ڰ�����Ӧ�¼�����ʾ�˶Ի���  
		                	  } 
		                		
		                        
		                return false;       
		        }      
		    });  
		}

	//(4)E2PROM��ʼ��
	public void E2promInit()
	{
		final ImageButton button = (ImageButton)findViewById(R.id.imageButtonE2prom);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(YcActivity.this, E2promActivity.class));
			}
		});		
	}
	//(5)���Ź���ʼ��
	public void WdogInit()
	{		
		final ImageButton button = (ImageButton)findViewById(R.id.imageButtonWDog);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(YcActivity.this, WdogActivity.class));
			}
		});
	}
	//(6)IO��ʼ��
	public void BoardIDInit() 
	{		
		final ImageButton button = (ImageButton)findViewById(R.id.imageButtonID);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(YcActivity.this, IDActivity.class));
			} 
		});
	}
	//(7) ���ڳ�ʼ��   
	public void SerialInit() 
	{		
		final ImageButton button = (ImageButton)findViewById(R.id.imageButtonSerial);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(YcActivity.this, SerialActivity.class));
			}
		});
	}

	
	
	public void addShortCut(Context context) 
	{
		// ���������ͼ���һ����ͼ����,��Ӧ����Receiver��intent-filter�е�action
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		// ���ÿ�ݷ�ʽ������
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
		// �������ظ�����
		shortcut.putExtra("duplicate", false);
		// Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
		Intent intent = new Intent(context, context.getClass());
		intent.setAction(Intent.ACTION_MAIN);
		// ָ����ݷ�ʽָ��������������Ϊintent
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// ָ����ݷ�ʽͼ��
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));
		context.sendBroadcast(shortcut);
	}

}
