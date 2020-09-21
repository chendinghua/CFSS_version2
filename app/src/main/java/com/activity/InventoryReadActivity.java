package com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tools.InteractiveDataUtil;
import com.tools.InteractiveEnum;
import com.tools.MethodEnum;
import com.tools.ShowLoadingDialog;
import com.view.UpdateDeviceNameDialog;
import com.ycexample.small.R;
import com.ychmi.sdk.YcApi;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tools.SendCommandUtils;

public class InventoryReadActivity extends Activity  implements View.OnClickListener{
    //文件描述符对象
    private FileDescriptor fp;
    ListView lvInventory;
    Button btnInventory;
    Button btnClear;
    Button btnCommit;
    SendCommandUtils commandUtils ;
    List<HashMap<String,String>> inventoryList = new ArrayList<HashMap<String,String>>();
    List<HashMap<String,String>> tempInventoryList = new ArrayList<HashMap<String,String>>();
    SimpleAdapter adpater;
    RadioButton rbAutoReader;
    RadioButton rbNoAutoReader;
    TextView tvScanCount;
    TextView etDeviceName;          //设备名
    Button btnSaveDeviceName;       //保存设备名按钮
    SharedPreferences sp ;
    SharedPreferences.Editor saveSp ;
    YcApi ycApi;
    //当前时间进度条
    SeekBar seekBar;
    TextView currentTime;
    UpdateDeviceNameDialog.Builder builder;
    UpdateDeviceNameDialog mDialog;
    TextView tvScanFlag;
    List<String> scanList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_read);
        ycApi= new YcApi();
        //9寸大平板电脑 DB9-1:ttyAMA1   DB9-2 是9寸大平板的调试串口不可使用        ----------------7寸小平板 ttyAMA3
        fp =  ycApi.openCom("dev/ttyS3",115200,8,0,1);
        if(fp!=null) {
            commandUtils = new SendCommandUtils(fp);
        }else{
            Toast.makeText(this,"串口连接失败",Toast.LENGTH_SHORT).show();
        }

        handler.postDelayed(stopLedIoRunnable,1);
        handler.postDelayed(stopSuccessIoRunnable,1);

        sp = getSharedPreferences("passageway_door_device_config", Context.MODE_PRIVATE);
        saveSp = sp.edit();

        initUI();

        etDeviceName.setText(sp.getString("device_info",""));
        seekBar.setProgress(sp.getInt("progess",5));

        currentTime.setText(sp.getInt("progess",5)+"");


        scanList.add("100100000000000000000001");
        scanList.add("100100000000000000000002");
        scanList.add("100100000000000000000003");
        scanList.add("100100000000000000000004");
        scanList.add("100100000000000000000005");



        adpater = new SimpleAdapter(this,inventoryList,R.layout.activity_inventory_read_items,new String[]{"epc"},new int []{R.id.tv_inventory_epc});
        lvInventory.setAdapter(adpater);
        //   initData();

        initListener();
        readerIOHandler.postDelayed(readerIORunnable,1000);
    }

    private void initData() {
        HashMap<String,String > tempMap1=new HashMap<>();
        tempMap1.put("epc","100123456789010A");
        HashMap<String,String > tempMap2=new HashMap<>();
        tempMap2.put("epc","100123456789010B");
        HashMap<String,String > tempMap3=new HashMap<>();
        tempMap3.put("epc","100123456789010C");

        inventoryList.add(tempMap1);
        inventoryList.add(tempMap2);
        inventoryList.add(tempMap3);
        adpater.notifyDataSetChanged();
    }

    Handler  updateButtonHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case -1:
                    tvScanFlag.setText("扫描中");
                    //LED灯
                    //     handler.postDelayed(startLedIoRunnable,1);
                    tvScanFlag.setBackgroundColor(getResources().getColor(R.color.blue));

                    btnInventory.setText("停止");
                    break;
                case 1:
                    tvScanFlag.setText("未扫描");
                    tvScanFlag.setBackgroundColor(getResources().getColor(R.color.red));

                    btnInventory.setText("读取");


                    break;
            }
        }
    };

    Handler clearHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            inventoryList.clear();
            tempInventoryList.clear();
            adpater.notifyDataSetChanged();
            updateScanCount();
        }
    };
    int errorIoTime=2000;
    long errorCurrentTime;
    boolean isReaderData = false;
    Handler readerIOHandler = new Handler();
    Runnable readerIORunnable = new Runnable() {
        @Override
        public void run() {
            //判断是否停止读取RFID数据
            if(isReaderData){

                List<String> listData = new ArrayList<>();

                for (int i =0;i<inventoryList.size();i++){
                    listData.add(inventoryList.get(i).get("epc"));
                }
                //判断数据是否为空
                if(listData.size()>0) {
                    errorCurrentTime = System.currentTimeMillis();
                    isError=true;
                    int successCount = 0;
                    for (int i = 0;i<listData.size();i++){
                        for (int j = 0;j<scanList.size();j++){
                            if(listData.get(i).equals(scanList.get(j))){
                                successCount++;
                                break;
                            }
                        }
                    }
                    Log.d("scanCount", "successCount "+successCount+"             scanList.size()    "+scanList.size());
                    if(successCount==scanList.size() && listData.size()==scanList.size()){
                        handler.postDelayed(stopErrorLedRunnable,1);
                    }else{
                        handler.postDelayed(stopErrorIoRunnable,1);
                    }
                }      clearHandler.sendMessage(new Message());
                Log.d("restarts", "run: ");
                Message msg = updateButtonHandler.obtainMessage();
                msg.what=1;
                //修改通道门读取状态背景颜色和信息
                updateButtonHandler.sendMessage(msg);
                //停止读取RFID
                commandUtils.closeComand();
                startHandler.removeCallbacks(startRunnable);
                //设置为非读取状态
                isReaderData=false;
                //重复循环读取IO状态
                readerIOHandler.postDelayed(this, 1);
                return;
            }else {
                int[] ios = commandUtils.getIOStatus();

                if (ios[0] == 1) {
                    //停止报警
                    if(!isError && errorCurrentTime+errorIoTime>System.currentTimeMillis()){
                        readerIOHandler.postDelayed(this, 1);

                        return;
                    } else if (!isError) {
                        //关闭报警
                        handler.removeCallbacks(stopErrorIoRunnable);
                        //   isError=false;
                        successHandler.postDelayed(stopSuccessIoRunnable,1);
                        //关闭led灯
                        handler.postDelayed(stopLedIoRunnable,1);
                        //  isError=true;

                    }

                    isReaderData = true;
                    //   btnInventory.setText("停止");
                    Message msg = updateButtonHandler.obtainMessage();
                    msg.what=-1;
                    updateButtonHandler.sendMessage(msg);
                    //开始读取RFID数据
                    commandUtils.sendCommand();
                    //读取RFID数据显示到listView中
                    startHandler.postDelayed(startRunnable, 1);
                    //读取5秒中
                    readerIOHandler.postDelayed(this, seekBar.getProgress()*1000);
                    return;
                } else if (ios[0] == 0) {
                    //循环读取IO数据
                    readerIOHandler.postDelayed(this, 1);
                    return;
                }
            }

            readerIOHandler.postDelayed(this, 1);
        }
    };
    private void initListener() {
        btnInventory.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnCommit.setOnClickListener(this);
        rbAutoReader.setOnClickListener(this);
        rbNoAutoReader.setOnClickListener(this);
        btnSaveDeviceName.setOnClickListener(this);
        //设置读取时间长度
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentTime.setText(""+i);
                saveSp.putInt("progess",i);
                saveSp.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                super.handleMessage(msg);
                inventoryList.clear();
                // tempInventoryList.clear();
                inventoryList.addAll((List<HashMap<String, String>>) msg.obj);
                adpater.notifyDataSetChanged();
                updateScanCount();
            }
        }
    };
    private void showTwoButtonDialog(String alertText, String confirmText, String cancelText, View.OnClickListener conFirmListener, View.OnClickListener cancelListener) {
        mDialog = builder.setMessage(alertText)
                .setPositiveButton(confirmText, conFirmListener)
                .setNegativeButton(cancelText, cancelListener)
                .createTwoButtonDialog();

        builder.setCurrentDeviceName(etDeviceName.getText().toString());
        mDialog.show();
    }

    private void initUI() {
        tvScanFlag = findViewById(R.id.tv_scan_flag);
        builder = new UpdateDeviceNameDialog.Builder(this);
        currentTime = findViewById(R.id.tv_current_time);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        etDeviceName = findViewById(R.id.tv_current_deviceName);
        btnSaveDeviceName = findViewById(R.id.btn_save_device_name);
        tvScanCount = (TextView)findViewById(R.id.tv_scan_count);
        lvInventory = (ListView) findViewById(R.id.lv_inventory_data);
        btnInventory = (Button) findViewById(R.id.btn_inventory);
        btnClear = (Button)findViewById(R.id.btn_clear);
        rbAutoReader  =(RadioButton)findViewById(R.id.rb_autoReader);
        rbNoAutoReader = (RadioButton)findViewById(R.id.rb_NoAuto_reader);
        btnCommit  = (Button) findViewById(R.id.btn_commit);
    }
    Handler startHandler = new Handler();
    //读取RFID数据显示到listView中
    Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            List<String> epcs =    commandUtils.getReaderDatas();
            for(String epc :epcs) {

                int index = -1;
                for (int i = 0; i < tempInventoryList.size(); i++) {
                    //判断读取的RFID数据不为空，存储数据集合不为空，并且RFID数据已存在集合中
                    if (epc != null && tempInventoryList.get(i).get("epc") != null && tempInventoryList.get(i).get("epc").equalsIgnoreCase(epc)) {
                        index = i;
                        break;
                    }

                }
                if (index == -1 && !"".equals(epc.trim()) && epc.startsWith("1001")) {
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("epc", epc);
                    tempInventoryList.add(hashMap);
                }
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = tempInventoryList;
                handler.sendMessage(msg);
                //  }
            }
            startHandler.postDelayed(this, 1);
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            //读取时间
            case R.id.btn_inventory:
                //    commandUtils.closeComand(fp);

                if("读取".equals(btnInventory.getText().toString())) {
                    btnInventory.setText("停止");
                    commandUtils.sendCommand();
                    startHandler.postDelayed(startRunnable,1);
                }else{
                    btnInventory.setText("读取");
                    commandUtils.closeComand();
                    startHandler.removeCallbacks(startRunnable);
                }

                break;
            //清除数据
            case R.id.btn_clear:
                btnInventory.setText("读取");
                commandUtils.closeComand();
                startHandler.removeCallbacks(startRunnable);
                tempInventoryList.clear();
                inventoryList.clear();
                adpater.notifyDataSetChanged();
                updateScanCount();
                break;
            //自动读取数据
            case R.id.rb_autoReader:
                readerIOHandler.removeCallbacks(readerIORunnable);
                btnInventory.setEnabled(false);
                btnClear.setEnabled(false);
                btnInventory.setText("读取");
                startHandler.removeCallbacks(startRunnable);
                commandUtils.closeComand();
                readerIOHandler.postDelayed(readerIORunnable,1);
                break;
            //手动读取数据
            case R.id.rb_NoAuto_reader:
                readerIOHandler.removeCallbacks(readerIORunnable);
                commandUtils.closeComand();
                startHandler.removeCallbacks(startRunnable);
                isReaderData=false;
                btnInventory.setEnabled(true);

                btnClear.setEnabled(true);
                btnInventory.setText("读取");
                readerIOHandler.removeCallbacks(readerIORunnable);
                break;
            case R.id.btn_commit:

                List<String> listData = new ArrayList<>();

                for (int i =0;i<inventoryList.size();i++){
                    listData.add(inventoryList.get(i).get("epc"));
                }

                //        ShowLoadingDialog.show(popupWindow,InventoryReadActivity.this);



                HashMap<String,Object> sendMap = new HashMap<>();
                sendMap.put("door",etDeviceName.getText().toString());
                sendMap.put("data",JSON.toJSONString(listData));
                //  InteractiveDataUtil.interactiveMessage(sendMap,sendHandler, MethodEnum.SENDDATA, InteractiveEnum.POST);
                break;

            case  R.id.btn_save_device_name:
                readerIOHandler.removeCallbacks(readerIORunnable);
                commandUtils.closeComand();
                startHandler.removeCallbacks(startRunnable);
                isReaderData=false;
                btnInventory.setEnabled(true);
                btnClear.setEnabled(true);
                btnInventory.setText("读取");
                readerIOHandler.removeCallbacks(readerIORunnable);
                inventoryList.clear();
                tempInventoryList.clear();
                adpater.notifyDataSetChanged();
                updateScanCount();

                tvScanFlag.setText("未扫描");
                tvScanFlag.setBackgroundColor(getResources().getColor(R.color.red));

                showTwoButtonDialog("请输入新的设备编号", "确认", "取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etDeviceName.setText(builder.getDeviceName());
                        saveSp.putString("device_info",builder.getDeviceName());
                        saveSp.apply();
                        mDialog.dismiss();

                        readerIOHandler.removeCallbacks(readerIORunnable);
                        btnInventory.setEnabled(false);
                        btnClear.setEnabled(false);
                        btnInventory.setText("读取");
                        startHandler.removeCallbacks(startRunnable);
                        commandUtils.closeComand();
                        readerIOHandler.postDelayed(readerIORunnable,1);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();

                        readerIOHandler.removeCallbacks(readerIORunnable);
                        btnInventory.setEnabled(false);
                        btnClear.setEnabled(false);
                        btnInventory.setText("读取");
                        startHandler.removeCallbacks(startRunnable);
                        commandUtils.closeComand();
                        readerIOHandler.postDelayed(readerIORunnable,1);
                    }
                });

                break;
        }
    }
    //是否提示报警
    boolean isError=false;

    Handler sendHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(InventoryReadActivity.this,"数据提交成功",Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    errorCurrentTime = System.currentTimeMillis();
                    isError=true;
                    handler.postDelayed(stopErrorIoRunnable,1);
                    JSONObject jsonResult = JSON.parseObject(msg.getData().getString("result"));
                    Toast.makeText(InventoryReadActivity.this,jsonResult.getString("Message"),Toast.LENGTH_SHORT).show();
                    break;
            }
            handler.postDelayed(stopLedIoRunnable,1);

        }
    };

    //开启LED灯
    Runnable startLedIoRunnable = new Runnable() {
        @Override
        public void run() {
            ycApi.SetIO(0, 1);
            ycApi.SetIO(0, 2);
        }
    };
    long errorTime = 2000;


    //关闭LED灯
    Runnable stopLedIoRunnable = new Runnable() {
        @Override
        public void run() {
            ycApi.SetIO(1,1);
            ycApi.SetIO(1,2);
        }
    };
    Runnable stopErrorLedRunnable = new Runnable() {
        @Override
        public void run() {
            if(isError) {
                ycApi.SetIO(0, 1);
                ycApi.SetIO(0, 2);
                isError = false;
                handler.postDelayed(this,errorTime);
            }else{
                isError=true;
                ycApi.SetIO(1,1);
                ycApi.SetIO(1,2);
            }
        }
    };
    //错误报警提示
    Runnable stopErrorIoRunnable = new Runnable() {
        @Override
        public void run() {
            if(isError) {
                ycApi.SetIO(0, 0);
                isError = false;
                handler.postDelayed(this,errorTime);
            }else{
                isError=true;
                ycApi.SetIO(1,0);
            }
        }
    };

    Handler successHandler = new Handler();
    //停止错误报警提示
    Runnable stopSuccessIoRunnable = new Runnable() {
        @Override
        public void run() {

            ycApi.SetIO(1,0);
            isError=true;
        }
    };
    @Override
    protected void onDestroy() {
        startHandler.removeCallbacks(startRunnable);
        commandUtils.onDestroy();
        super.onDestroy();
    }

    public void updateScanCount(){
        tvScanCount.setText(""+inventoryList.size());

    }

}
