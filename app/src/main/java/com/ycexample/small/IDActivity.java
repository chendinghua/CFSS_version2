package com.ycexample.small;
import com.ychmi.sdk.YcApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class IDActivity extends Activity
{
	
	YcApi ycapi;	
	//��ѯģʽʹ��
	EditText boardId ;	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_id);	
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	   //��ѯģʽʹ��
		boardId = (EditText)findViewById(R.id.editTextBoardID);	      
		
		ycapi = new YcApi();	
		
		ycapi.HideNviBar(getWindow().getDecorView());
		
		IdInit();
		
		//���ذ�ť�Ĳ���
        Button mButtonReturn=(Button)findViewById(R.id.buttonreturn);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				IDActivity.this.finish();
			}
		});
	}
	private void IdInit()
    { 
		
		int[] data = new int[4];
		int i;

		ycapi.GetBoardID(data);	
		for(i=0;i<3;i++)
		//boardId.append("0x"+Integer.toHexString(data[i])+":");
			boardId.append("0x"+String.format("%08x", data[i])+":");
		//boardId.append("0x"+Integer.toHexString(data[3]));
		boardId.append("0x"+String.format("%08x", data[i]));
    }
	
}//public class IoActivity extends Activity
