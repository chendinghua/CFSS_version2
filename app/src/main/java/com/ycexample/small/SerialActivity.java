package com.ycexample.small;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.ychmi.sdk.YcApi;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SerialActivity extends Activity{
	
	YcApi ycapi;
	private Spinner					m_SpinnerSerialNumber;
	private Spinner					m_SpinnerSerialBaute;
	private Spinner					m_SpinnerSerialParityBit;
	private Spinner					m_SpinnerSerialDataBit;
	private Spinner					m_SpinnerSerialStopBit;
	
	private static final String[]	m_serialNumber	  = { "ttySAC0", "ttySAC1", "ttySAC2", "ttySAC3" , "ttySAC4", "ttySAC5"}; 
	
	private static final String[]	m_serialBaute	  = { "110", "300", "600", "1200",
		"2400", "4800", "9600", "14400","19200", "38400", "43000", "56000","57600", "115200",
		"128000", "256000"};
	private static final int[] m_serialBauteInt={ 110, 300, 600, 1200,
		2400, 4800, 9600, 14400,19200, 38400, 43000, 56000,57600, 115200,128000,256000};
	
	private static final String[]	m_serialParityBit = { "None", "Odd", "Even", "Space"};
	private static final int[] m_serialParityBitInt={ 0, 1, 2, 3};
	
	private static final String[]	m_serialDataBit	  = { "5", "6", "7", "8" };
	private static final int[] m_serialDataBitInt={ 5, 6, 7, 8};
	
	private static final String[]	m_serialStopBit	  = { "1", "2"};
	private static final int[] m_serialStopBitInt={1,2};
	
	protected static final String   TAG = "SerialActivity.java :";
	
	private ArrayAdapter<String>	adapterSerialNumber;
	private ArrayAdapter<String>	adapterSerialBaute;
	private ArrayAdapter<String>	adapterSerialParityBit;
	private ArrayAdapter<String>	adapterSerialDataBit;
	private ArrayAdapter<String>	adapterSerialStopBit;
	
	private EditText mSendTex;
	
	private FileDescriptor fp;	
	private FileInputStream mInputStream;
	private FileOutputStream mOutputStream;
	
	private ReadThread mReadThread;
	
	EditText mReception;
	
	CheckBox mSendCheckBox;
	
	CheckBox mReceCheckBox;
	
	private boolean gSerialOpenFlag=false;

	
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) 
			{
				int size;
				if(gSerialOpenFlag)
				{
					try 
					{
						byte[] buffer = new byte[1024];
						if (mInputStream == null) return;					
						size = mInputStream.read(buffer);
						if (size > 0) 
						{
							Log.d("","Sizexxxxxxxx:\n"+size);
							if(gSerialOpenFlag)
								onDataReceived(buffer, size);
						}						
					} 
					catch (IOException e)
					{
						e.printStackTrace();
						return;
					}
			   }
		  }
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serial);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		mReception = (EditText) findViewById(R.id.editTextRece);
		
		mSendTex = (EditText)findViewById(R.id.editTextSend);
		
		mSendCheckBox = (CheckBox)findViewById(R.id.hexcheckboxsend);
		mReceCheckBox = (CheckBox)findViewById(R.id.hexcheckboxrece);
		
		mSendTex.setText("test serial strings");
		//mSendTex.setText("0230313132433034033645");	
		
		ycapi = new YcApi();
		
		 
		
		ycapi.HideNviBar(getWindow().getDecorView());
		
		SerialInit();
		
		//���ذ�ť�Ĳ���
        Button mButtonReturn=(Button)findViewById(R.id.buttonreturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SerialActivity.this.finish();
			}
		});
   	}



	private void SerialInit()
    {		
		final MyFunc hexstr = new MyFunc();
		//��ʼ��Spinner
		SerialNumberSpinner();
		SerialBauteSpinner();
		SerialParityBitSpinner();
		SerialDataBitSpinner();
		SerialStopBitSpinner();
		
		//m_SpinnerSerialParityBit.setEnabled(false);//��ʱ���ε�У��λ
		
		m_SpinnerSerialNumber.setSelection(1);		
		
		final Button clearButton = (Button)findViewById(R.id.buttonClearReceText);
		clearButton.setOnClickListener(new View.OnClickListener() 
		{			
			public void onClick(View arg0) 
			{
				mReception.setText("");
			}
		});
		
		mSendCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(mSendCheckBox.isChecked()){					
					mSendTex.setText("0230313132433034033645");	
				}
				else
				{
					mSendTex.setText("test serial strings");
				}
			}
		});

	
		//�򿪺͹رմ���
		final Button openButton = (Button)findViewById(R.id.buttonOpen);
		openButton.setOnClickListener(new View.OnClickListener()
		{			
			public void onClick(View arg0) 
			{
				
				String path = ycapi.ttySAC1;
				if(gSerialOpenFlag)
				{
					if (mReadThread != null)
						mReadThread.interrupt();
					openButton.setText(getResources().getString(R.string.main2serialopenbutton));
					gSerialOpenFlag=false;
					ycapi.closeCom();					
					m_SpinnerSerialNumber.setEnabled(true);
					m_SpinnerSerialBaute.setEnabled(true);
					m_SpinnerSerialParityBit.setEnabled(true);
					m_SpinnerSerialDataBit.setEnabled(true);
					m_SpinnerSerialStopBit.setEnabled(true);
				}
				else
				{
					switch(m_SpinnerSerialNumber.getSelectedItemPosition())
					{
					case 0:
						path=ycapi.ttySAC0;
						break;
					case 1:
						path=ycapi.ttySAC1;
						break;
					case 2:
						path=ycapi.ttySAC2;
						break;
					case 3:
						path=ycapi.ttySAC3;
						break;
					case 4:
						path=ycapi.ttySAC4;
						break;
					case 5:
						path=ycapi.ttySAC5;
						break;
					default:
						path=ycapi.ttySAC1;
						break;
					}
					//openCom(String path, int baudrate ,int databit,int paritybit,int stopbit);
					fp = ycapi.openCom(path, m_serialBauteInt[m_SpinnerSerialBaute.getSelectedItemPosition()],
							m_serialDataBitInt[m_SpinnerSerialDataBit.getSelectedItemPosition()],
							m_serialParityBitInt[m_SpinnerSerialParityBit.getSelectedItemPosition()],
							m_serialStopBitInt[m_SpinnerSerialStopBit.getSelectedItemPosition()]
							); 
					if (fp == null) 
					{
						Log.e(TAG, "native open returns null");
						try 
						{
							throw new IOException();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gSerialOpenFlag=false;
					}
					else
					{
						mInputStream = new FileInputStream(fp);
						mOutputStream = new FileOutputStream(fp);
						openButton.setText(getResources().getString(R.string.main2serialclosebutton));
						gSerialOpenFlag=true;
						
						m_SpinnerSerialNumber.setEnabled(false);
						m_SpinnerSerialBaute.setEnabled(false);
						m_SpinnerSerialParityBit.setEnabled(false);
						m_SpinnerSerialDataBit.setEnabled(false);
						m_SpinnerSerialStopBit.setEnabled(false);
					}
					
					mReadThread = new ReadThread();
					mReadThread.start();
				}
			}
		});
		
		Button sendButton = (Button)findViewById(R.id.buttonSend);
		sendButton.setOnClickListener(new View.OnClickListener() 
		{			
			public void onClick(View arg0) 
			{	
				if(gSerialOpenFlag)
				{
				try {
					if(mSendCheckBox.isChecked())
						mOutputStream.write(hexstr.HexToByteArr(mSendTex.getText().toString()));
					else					
						mOutputStream.write(mSendTex.getText().toString().getBytes());						
						
					} catch (IOException e) 
					{
					// TODO Auto-generated catch block
					e.printStackTrace();
				    }
			    }				
			}
		});
    }
	
	private void SerialNumberSpinner()
	{
		m_SpinnerSerialNumber = (Spinner) findViewById(R.id.spinnerComNumber);
		
		

		//����ѡ������ArrayAdapter����
		adapterSerialNumber = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_serialNumber);

		//���������б�ķ��
		adapterSerialNumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//��adapter��ӵ�m_Spinner��
		m_SpinnerSerialNumber.setAdapter(adapterSerialNumber);
		
		m_SpinnerSerialNumber.setSelection(0);
		//���Spinner�¼�����
		m_SpinnerSerialNumber.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{			
				//������ʾ��ǰѡ�����
				arg0.setVisibility(View.VISIBLE);				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
			}

		});
	}
	private void SerialBauteSpinner()
	{
		m_SpinnerSerialBaute = (Spinner) findViewById(R.id.spinnerbaute);

		//����ѡ������ArrayAdapter����
		adapterSerialBaute = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_serialBaute);

		//���������б�ķ��
		adapterSerialBaute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//��adapter��ӵ�m_Spinner��
		m_SpinnerSerialBaute.setAdapter(adapterSerialBaute);
		
		m_SpinnerSerialBaute.setSelection(13);

		//���Spinner�¼�����
		m_SpinnerSerialBaute.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{			
				//������ʾ��ǰѡ�����
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
			}

		});
		
		
	}
	private void SerialParityBitSpinner()
	{
		m_SpinnerSerialParityBit = (Spinner) findViewById(R.id.spinnerparitybit);

		//����ѡ������ArrayAdapter����
		adapterSerialParityBit = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_serialParityBit);

		//���������б�ķ��
		adapterSerialParityBit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//��adapter��ӵ�m_Spinner��
		m_SpinnerSerialParityBit.setAdapter(adapterSerialParityBit);
		
		

		//���Spinner�¼�����
		m_SpinnerSerialParityBit.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{			
				//������ʾ��ǰѡ�����
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
			}

		});
	}
	private void SerialDataBitSpinner()
	{
		m_SpinnerSerialDataBit = (Spinner) findViewById(R.id.spinnerdatabit);

		//����ѡ������ArrayAdapter����
		adapterSerialDataBit = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_serialDataBit);

		//���������б�ķ��
		adapterSerialDataBit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//��adapter��ӵ�m_Spinner��
		m_SpinnerSerialDataBit.setAdapter(adapterSerialDataBit);
		
		m_SpinnerSerialDataBit.setSelection(3);
		
		
		//���Spinner�¼�����
		m_SpinnerSerialDataBit.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{			
				//������ʾ��ǰѡ�����
				arg0.setVisibility(View.VISIBLE);
				ycapi.HideNviBar(findViewById(R.id.fullscreen_content));
			}

			private ArrayAdapter<String>	adapterSerialDataBit;

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
				ycapi.HideNviBar(findViewById(R.id.fullscreen_content));
				showToast("xxxxxxxxxxxxxxxxSpinner1: unselected");
			}
		});
	}
	 private void showToast(CharSequence msg) {
         Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
     }
	private void SerialStopBitSpinner()
	{
		m_SpinnerSerialStopBit = (Spinner) findViewById(R.id.spinnerstopbit);

		//����ѡ������ArrayAdapter����
		adapterSerialStopBit = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m_serialStopBit);

		//���������б�ķ��
		adapterSerialStopBit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//��adapter��ӵ�m_Spinner��
		m_SpinnerSerialStopBit.setAdapter(adapterSerialStopBit);
	

		
		//���Spinner�¼�����
		m_SpinnerSerialStopBit.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{			
				//������ʾ��ǰѡ�����
				arg0.setVisibility(View.VISIBLE);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
				// TODO Auto-generated method stub
			}

		});
	}
	
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				MyFunc bytehex = new MyFunc();
				if (mReception != null) {
					if(mReceCheckBox.isChecked())
						mReception.append(bytehex.ByteArrToHex(buffer,0,size));
					else
					    mReception.append(new String(buffer, 0, size));
					
				}
			}
		});
	}
	
	public boolean onTouchEvent(MotionEvent event)
    {
		
        	if (event.getAction() == MotionEvent.ACTION_DOWN)
        	{
        		ycapi.HideNviBar(findViewById(R.id.fullscreen_content));
            	return true;
        	}
        	
       
        return true;
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		//ycapi.closeCom();	
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		/*try {
			Log.e(TAG, "initUart xxxxxxxxxxx");
			initUart();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mReadThread = new ReadThread();
		mReadThread.start();*/ 
		showToast("onResume   Spinner1: unselected");
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		/*try {
			Log.e(TAG, "initUart xxxxxxxxxxx");
			initUart();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mReadThread = new ReadThread();
		mReadThread.start();*/ 
		showToast("onRestart   Spinner1: unselected");
	}
	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		
		super.onDestroy();
	}
}
