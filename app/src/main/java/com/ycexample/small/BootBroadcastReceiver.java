package com.ycexample.small;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class BootBroadcastReceiver extends BroadcastReceiver
{ 
    static final String action_boot="android.intent.action.BOOT_COMPLETED";
    
    @Override
    public void onReceive(Context context, Intent intent) 
    { 
    	
        if (intent.getAction().equals(action_boot))
        {  
        	//Log.d("BootReceiver", "system boot completed"); 
	        Intent ootStartIntent=new Intent(context,YcActivity.class);  
	        /* MyActivity action defined in AndroidManifest.xml */
	        ootStartIntent.setAction("android.intent.action.MAIN");
	
			/* MyActivity category defined in AndroidManifest.xml */
	        ootStartIntent.addCategory("android.intent.category.LAUNCHER");
	
			/*
			 * If activity is not launched in Activity environment, this flag is
			 * mandatory to set
			 */
	        ootStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	           
	        context.startActivity(ootStartIntent);  

        	
        }
    }   
}
