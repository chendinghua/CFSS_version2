package com.ychmi.sdk;

import java.io.FileDescriptor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class YcApi
{
	public int IO_POLLING_MODE = 0;
	public int IO_INTR_MODE    = 1;

	public int IO_INTR_LOW_LEVEL_TRIGGERED    =    0;
	public int IO_INTR_HIGH_LEVEL_TRIGGERED   =    1;
	public int IO_INTR_FALLING_EDGE_TRIGGERED =    2;
	public int IO_INTR_RISING_EDGE_TRIGGERED  =    3;
	public int IO_INTR_BOTH_EDGE_TRIGGERED    =    4;
	
	public String ttySAC0 = "dev/ttyAMA0";//串口0 此为调试串口
	public String ttySAC1 = "dev/ttyAMA1";//串口1
	public String ttySAC2 = "dev/ttyAMA2";//串口2
	public String ttySAC3 = "dev/ttyAMA3";//串口3
	public String ttySAC4 = "dev/ttyAMA4";//串口4
	public String ttySAC5 = "dev/ttyAMA5";//串口5
	
	//LED 状态灯控制
    public native int 		SetLed(boolean flag);
    
    //蜂鸣器 状态控制
    public native int 		SetBeep(boolean flag);   
    
    //看门狗控制
    public native int 		StartWDog();
    public native int 		SetWDog(byte timeInterval);
    public native int 		FeedWDog();
    public native int 		StopWDog();
    
    //输入输出IO口控制
    public native boolean 	SetIO(int level , int ioNum);
    public native boolean 	SetIoMode(int ioNum,int ioMode ,int triggeredMode);
    public native int    	GetIO(int ioNum,int flag);
    
    //背光设置函数，得到当前背光值函数
    public native boolean   SetBackLight(int dx);
    public native int    	GetBackLight();
    //背光自动关闭设置函数
    public native boolean	StartAutoBacklight(int s);
    public native boolean	StopAutoBacklight();
    
    
    //串口控制
    public native FileDescriptor openCom(String path, int baudrate ,int databit,int paritybit,int stopbit);
    public native FileDescriptor openComNonBlock(String path, int baudrate ,int databit,int paritybit,int stopbit);
   	public native void      closeCom();
   	public native void      closeComFd(FileDescriptor mFileDescriptor);
   	
   	//I2C 控制
    public native int 		OpenI2C(String nodeName); 
    public native int 		ReadI2C(int fileHander , int slaveAddr, int subaddr, int bufArr[], int len);
    public native int 		WriteI2C(int fileHander, int slaveAddr, int subaddr, int bufArr[], int len); 
    public native void 		CloseI2C(int fileHander);
    
    //唯一BoardID
    public  native boolean 	GetBoardID(int id[]);  
    //E2PROM控制
    public native int 		WriteE2PROM(int subaddr, String buf, int len);
    public native String 	ReadE2PROM(int subaddr, int len);
    

    
    //设置网络
    public native static  boolean  SetNetWork(int eth,int dhcp,String ip,String submask,String gateway,String dns1,String dns2);
    
    //发送命令
    public native  boolean  SendCmd(String cmd);
    
    //关机命令
    public native  int      PowerOff();
    


    //导航栏控制方法1
    public void HideNviBar(View contentView) {    	
    	contentView.setSystemUiVisibility(
    	              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    	              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    	             | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    	             | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
    	             | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
    	             | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    	             | 0x00002000);    	
	}
	public void ShowNviBar(View contentView) {	
		contentView.setSystemUiVisibility(
	            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

	}
	//导航栏控制方法2
	public void HideNviBarFull() {    	
		SendCmd("service call activity 42 s16 com.android.systemui");   	
	}
	public void ShowNviBarFull() {	
		SendCmd("am startservice -n com.android.systemui/.SystemUIService");
	}
	public void ReturnLauncher(Context context)
	{
		Intent intent = new Intent(Intent.ACTION_MAIN); 
		intent.addCategory(Intent.CATEGORY_LAUNCHER);             
		ComponentName cn = new ComponentName("com.android.launcher", "com.android.launcher2.Launcher");             
		intent.setComponent(cn); 
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);	 
	}

	public void StartEth(int eth,int dhcp,String ip,String submask,String gateway,String dns1,String dns2)
	{
		SetNetWork(0,dhcp,ip,submask,gateway,dns1,dns2);
	}

    static
    {
        System.loadLibrary("ycapi");
    }
}