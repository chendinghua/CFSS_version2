package com.activity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.entry.RidApiList;
import com.serialport.SerialPort;
import com.spiner.AbstractSpinerAdapter;
import com.spiner.SpinerPopWindow;
import com.tools.AlertDialogCallBack;
import com.tools.DialogUtils;
import com.tools.HandlerUtils;
import com.tools.HandlerUtilsCallback;
import com.tools.HandlerUtilsErrorCallback;
import com.tools.InteractiveDataUtil;
import com.tools.InteractiveEnum;
import com.tools.MethodEnum;
import com.tools.PreferenceUtil;
import com.tools.UIHelper;
import com.view.LogList;
import com.view.PopupMenu;
import com.ycexample.small.R;
import com.ychmi.sdk.YcApi;
import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tools.SendCommandUtils;

/**
 * Created by 16486 on 2019/10/16.
 */

public class MainActivity  extends Activity {
    private static final String TAG = "PageInventory";
    private static final boolean DEBUG = true;
    // fixed by lei.li 2016/11/09
    // private LogList mLogList;
    private LogList mLogList;
    // fixed by lei.li 2016/11/09
    private TextView mStartStop;
    // private TextView mRefreshButton;
    private LinearLayout mLayoutRealSet;
    private TextView mSessionIdTextView, mInventoriedFlagTextView;
    private TableRow mDropDownRow1, mDropDownRow2;
    private CheckBox mCbRealSet, mCbRealSession;
    private List<String> mSessionIdList;
    private List<String> mInventoriedFlagList;
    private SpinerPopWindow mSpinerPopWindow1, mSpinerPopWindow2;
    private EditText mRealRoundEditText;

    Context mContext;

    private int mPos1 = -1, mPos2 = -1;
    private TextView mTagsCountText, mTagsTotalText;
    private TextView mTagsSpeedText, mTagsTimeText, mTagsOpTimeText;
    //   private LocalBroadcastManager lbm;
    private long mRefreshTime;
    //    private Context mContext;
    public static SerialPort mSerialPort = null;

    private Button btnPutOn;			//入库
    private Button btnPutOff;			//出库

 //   public Records records;
    public String token;

    public TextView currentUserName;

    public TextView tvFlag;
    int flag;

    Button btnCommit;

    Button btnRefresh;
    //当前扫描状态
    TextView tvScanFlag;


    //当前进度条的时间
    TextView currentTime;
    //当前时间进度条
    SeekBar seekBar;


    Button btnExit;

    YcApi ycApi;
    SendCommandUtils sendCommandUtils;
    FileDescriptor fp;

    ListView inventoryList;

    SimpleAdapter adpater;

    List<HashMap<String,String>> tagList= new ArrayList<>();
    List<HashMap<String,String>> tempList= new ArrayList<>();


    private ViewPager mPager;

    private List<View> listViews;

    private ImageView iv_menu;

    private PopupMenu popupMenu;


    AlertDialog.Builder errorResultDialog;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        mContext = this;
        ycApi = new YcApi();
        fp = ycApi.openCom("dev/ttyAMA3", 115200, 8, 0, 1);
        sendCommandUtils = new SendCommandUtils(fp);
        listViews = new ArrayList<View>();


        mPager = (ViewPager) findViewById(R.id.vPager);



        iv_menu = (ImageView) findViewById(R.id.iv_menu);
        popupMenu = new PopupMenu(this);
        iv_menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupMenu.showLocation(R.id.iv_menu);
                popupMenu.setOnItemClickListener(new com.view.PopupMenu.OnItemClickListener() {

                    @Override
                    public void onClick(PopupMenu.MENUITEM item, String str) {
                        if (item == PopupMenu.MENUITEM.ITEM1) {
                            readerIOHandler.removeCallbacks(readerIORunnable);
                            Intent aboutIntent  = new Intent(MainActivity.this, AboutInfoActivity.class);
                            if (aboutIntent != null) startActivity(aboutIntent);
                        } else if (item ==PopupMenu.MENUITEM.ITEM2) {
                            Log.e("debug", "biiiiiiiiiiiiiiiiiiiiiiiiiiiin");
                            if (str.equals("English")) {
                                PreferenceUtil.commitString("language", "en");
                                Log.e("debug", "biiiiiiiiiiiiiiiiiiiiiiiiiiiin + english");
                            } else if (str.equals("中文")) {
                                PreferenceUtil.commitString("language", "zh");
                                Log.e("debug", "中文");
                            }
                            Intent intent = new Intent(MainActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.this.startActivity(intent);

                        }
                    }
                });
            }
        });

        mSessionIdList = new ArrayList<String>();
        mInventoriedFlagList = new ArrayList<String>();


        inventoryList = (ListView)findViewById(R.id.lv_inventory);

        adpater = new SimpleAdapter(this,tagList,R.layout.activity_main_items,new String[]{"id","epc"},new int[]{R.id.id_text,R.id.epc_text});
        inventoryList.setAdapter(adpater);


        mLogList = (LogList) findViewById(R.id.log_list);
        mStartStop = (TextView) findViewById(R.id.startstop);
        mCbRealSet = (CheckBox) findViewById(R.id.check_real_set);
        mLayoutRealSet = (LinearLayout) findViewById(R.id.layout_real_set);
        mCbRealSession = (CheckBox) findViewById(R.id.check_real_session);


        currentUserName=(TextView)findViewById(R.id.tv_current_userName);
        tvFlag = (TextView)findViewById(R.id.tv_flag);
        btnCommit=(Button) findViewById(R.id.btn_commit);
        btnRefresh = (Button)findViewById(R.id.btn_refresh);
        tvScanFlag = (TextView)findViewById(R.id.tv_scan_flag);
        btnExit=(Button) findViewById(R.id.btn_exit);
        currentTime = (TextView) findViewById(R.id.tv_current_time);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        final SharedPreferences.Editor editor = getSharedPreferences("seek_config",Context.MODE_PRIVATE).edit();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentTime.setText(""+i);
                editor.putInt("progess",i);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        SharedPreferences spSeek =  getSharedPreferences("seek_config",Context.MODE_PRIVATE);
        seekBar.setProgress(spSeek.getInt("progess",5));


        btnPutOn =(Button)findViewById(R.id.btn_inventory_putOn);
        btnPutOff =(Button) findViewById(R.id.btn_inventory_putOff);
        SharedPreferences sp =getSharedPreferences("login_config", Context.MODE_PRIVATE);
     //   records = JSON.parseObject(sp.getString("records",""),Records.class);


        token = sp.getString("token","token");
        Log.d("token", "PageInventoryReal: "+token);
        flag = sp.getInt("flag",1);

        //人库
        if(flag==1){
            tvFlag.setText("入库数量");
            //出库
        }else if(flag==2){
            tvFlag.setText("出库数量");
        }
        btnPutOn.setOnClickListener(setInventoryRealOnClickListener);
        btnPutOff.setOnClickListener(setInventoryRealOnClickListener);

        btnCommit.setOnClickListener(setInventoryRealOnClickListener);

        btnRefresh.setOnClickListener(setInventoryRealOnClickListener);

        btnExit.setOnClickListener(setInventoryRealOnClickListener);

        mSessionIdTextView = (TextView) findViewById(R.id.session_id_text);
        mInventoriedFlagTextView = (TextView) findViewById(R.id.inventoried_flag_text);
        mDropDownRow1 = (TableRow) findViewById(R.id.table_row_session_id);
        mDropDownRow2 = (TableRow) findViewById(R.id.table_row_inventoried_flag);
        mTagsCountText = (TextView) findViewById(R.id.tags_count_text);
        mTagsTotalText = (TextView) findViewById(R.id.tags_total_text);
        mTagsSpeedText = (TextView) findViewById(R.id.tags_speed_text);
        mTagsTimeText = (TextView) findViewById(R.id.tags_time_text);
        mTagsOpTimeText = (TextView) findViewById(R.id.tags_op_time_text);
        //  mTagRealList = (TagRealList) findViewById(id.tag_real_list);
        mRealRoundEditText = (EditText) findViewById(R.id.real_round_text);

        mStartStop.setOnClickListener(setInventoryRealOnClickListener);

        mDropDownRow1.setEnabled(mCbRealSession.isChecked());
        mDropDownRow2.setEnabled(mCbRealSession.isChecked());
        mDropDownRow1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSpinWindow1();
            }
        });
        mDropDownRow2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSpinWindow2();
            }
        });
        mSessionIdList.clear();
        mInventoriedFlagList.clear();
        String[] lists = getResources().getStringArray(R.array.session_id_list);
        for (int i = 0; i < lists.length; i++) {
            mSessionIdList.add(lists[i]);
        }
        lists = getResources().getStringArray(R.array.inventoried_flag_list);
        for (int i = 0; i < lists.length; i++) {
            mInventoriedFlagList.add(lists[i]);
        }

        mSpinerPopWindow1 = new SpinerPopWindow(this);
        mSpinerPopWindow1.refreshData(mSessionIdList, 0);
        mSpinerPopWindow1.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            public void onItemClick(int pos) {
                setSessionIdText(pos);
            }
        });

        mSpinerPopWindow2 = new SpinerPopWindow(this);
        mSpinerPopWindow2.refreshData(mInventoriedFlagList, 0);
        mSpinerPopWindow2.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
            public void onItemClick(int pos) {
                setInventoriedFlagText(pos);
            }
        });

        // updateView();

        mCbRealSet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (mCbRealSet.isChecked()) {
                    mLayoutRealSet.setVisibility(View.VISIBLE);
                } else {
                    mLayoutRealSet.setVisibility(View.GONE);
                }

                mDropDownRow1.setEnabled(mCbRealSession.isChecked());
                mDropDownRow2.setEnabled(mCbRealSession.isChecked());
            }
        });

        mCbRealSession
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        mDropDownRow1.setEnabled(mCbRealSession.isChecked());
                        mDropDownRow2.setEnabled(mCbRealSession.isChecked());
                    }
                });


    }

    Handler updateTitileHanlder= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //正在读取
                case 1:
                    tvScanFlag.setText("扫描中");
                    tvScanFlag.setBackgroundColor(getResources().getColor(R.color.blue));

                    break;
                //未读取
                case 2:
                    tvScanFlag.setText("未扫描");
                    tvScanFlag.setBackgroundColor(getResources().getColor(R.color.red));

                    break;
            }

            super.handleMessage(msg);
        }
    };



    Handler readerIOHandler = new Handler();


    boolean isReaderData =false;

    Runnable readerIORunnable = new Runnable() {
        @Override
        public void run() {



            int[] ios = sendCommandUtils.getIOStatus();
            Log.d("IOStatus", "run:  io0"+ios[0] +"     io1"+ios[1]);
            //红外触发
            if (ios[0] == 1) {

                Message msg = updateTitileHanlder.obtainMessage();
                msg.what = 1;
                updateTitileHanlder.sendMessage(msg);


                //停止红外扫描
                // testHandler.removeCallbacks(testRunnable);
                readerIOHandler.postDelayed(this, (Integer.parseInt(currentTime.getText().toString()) * 1000));
                //触发RFID的数据读取
                //  mStartStop.performClick();
                //开始读取RFID数据
                sendCommandUtils.sendCommand();
                //读取RFID数据显示到listView中
                startHandler.postDelayed(startRunnable, 1);
				// add by lei.li 2016/11/14
                //	startstop();
                isLoop = false;
            } else {
                Message msg = updateTitileHanlder.obtainMessage();
                msg.what = 2;
                updateTitileHanlder.sendMessage(msg);
                if (!isLoop) {
                    //停止读取RFID
                    sendCommandUtils.closeComand();
                    startHandler.removeCallbacks(startRunnable);
                    isLoop = true;
                    if(tagList.size()==0){
                        readerIOHandler.postDelayed(this, 1);
                    }else{
                        btnCommit.performClick();
                    }
                }else{
                    readerIOHandler.postDelayed(this, 1);
                }
            }


        }
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==1){
                super.handleMessage(msg);
                tagList.clear();
                // tempInventoryList.clear();
                tagList.addAll((List<HashMap<String, String>>) msg.obj);
                adpater.notifyDataSetChanged();
                // updateScanCount();
                refreshText();


            }
        }
    };


    Handler startHandler = new Handler();

    //读取RFID数据显示到listView中
    Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            List<String> epcs =    sendCommandUtils.getReaderDatas();
            for(String epc :epcs) {
                int index = -1;
                if(epc.length()>=12) {
                    for (int i = 0; i < tempList.size(); i++) {
                        //判断读取的RFID数据不为空，存储数据集合不为空，并且RFID数据已存在集合中
                        if (epc != null && tempList.get(i).get("epc") != null && tempList.get(i).get("epc").equalsIgnoreCase(epc.substring(0, 10))) {
                            index = i;
                            break;
                        }
                    }
                    if (index == -1) {
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("id", tempList.size() + 1 + "");
                        hashMap.put("epc", epc.substring(0, 10));
                        tempList.add(hashMap);
                    }

                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = tempList;
                    handler.sendMessage(msg);
                }
                //  }
            }
            startHandler.postDelayed(this, 1);
        }
    };


    public void refresh() {
        refreshList();
        refreshText();
        clearText();
        mRealRoundEditText.setText("1");
    }


    @SuppressWarnings("deprecation")
    private void refreshStartStop(boolean start) {
        if (start) {
            mStartStop.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.button_disenabled_background));
            mStartStop.setText(getResources()
                    .getString(R.string.stop_inventory));
        } else {
            mStartStop.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.button_background));
            mStartStop.setText(getResources().getString(
                    R.string.start_inventory));
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            refreshList();
            mHandler.postDelayed(this, 2000);
        }
    };


    private void setSessionIdText(int pos) {
        if (pos >= 0 && pos < mSessionIdList.size()) {
            String value = mSessionIdList.get(pos);
            mSessionIdTextView.setText(value);
            mPos1 = pos;
        }
    }

    private void setInventoriedFlagText(int pos) {
        if (pos >= 0 && pos < mInventoriedFlagList.size()) {
            String value = mInventoriedFlagList.get(pos);
            mInventoriedFlagTextView.setText(value);
            mPos2 = pos;
        }
    }

    private void showSpinWindow1() {
        mSpinerPopWindow1.setWidth(mDropDownRow1.getWidth());
        mSpinerPopWindow1.showAsDropDown(mDropDownRow1);
    }

    private void showSpinWindow2() {
        mSpinerPopWindow2.setWidth(mDropDownRow2.getWidth());
        mSpinerPopWindow2.showAsDropDown(mDropDownRow2);
    }
    private View.OnClickListener setInventoryRealOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()){
                case  R.id.startstop:
                    //   startstop();
                    break;

                case R.id.btn_commit:

                    if(tagList.size()==0){
                        UIHelper.ToastMessage(MainActivity.this,"提交数据不能为空");
                        return;
                    }
                    readerIOHandler.removeCallbacks(readerIORunnable);
                    startHandler.removeCallbacks(startRunnable);
                        isCommit=true;
                        HashMap<String,Object> putOffParam = new HashMap<>();
                        List<RidApiList> ridApiLists = new ArrayList<>();
                        for (int i =0;i<tagList.size();i++){
                            RidApiList ridApiList = new RidApiList();
                            ridApiList.setEpc(tagList.get(i).get("epc"));
                            ridApiLists.add(ridApiList);
                        }
                        putOffParam.put("rfidApiList",ridApiLists);
                        InteractiveDataUtil.interactiveMessage(MainActivity.this,putOffParam,handlerUtils, MethodEnum.PUTBJINFO, InteractiveEnum.POST,token,false);
                    break;
                //清空数据
                case R.id.btn_refresh:
                    //判断红外监听读取的情况才能进行数据的清除
                    if(isLoop) {
                        DialogUtils.showAlertDialog(MainActivity.this, new AlertDialogCallBack() {
                            @Override
                            public void alertDialogFunction(DialogInterface dialog) {
                                refresh();
                                readerIOHandler.removeCallbacks(readerIORunnable);
                                startHandler.removeCallbacks(startRunnable);
                                readerIOHandler.postDelayed(readerIORunnable, 1000);
                                isShow = true;
                            }
                        }, "是否清除数据", null, null);
                    }
                    break;
                case R.id.btn_exit:

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(getString(R.string.alert_diag_title)).
                            setMessage(getString(R.string.are_you_sure_to_exit)).
                            setPositiveButton(getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //close th e module
                                            //  ControlGPIO.newInstance().JNIwriteGPIO(0);
                                            //  testHandler.removeCallbacks(testRunnable);
                                            readerIOHandler.removeCallbacks(readerIORunnable);
                                            startHandler.removeCallbacks(startRunnable);
                                            ycApi.SetIO(1, 1);
                                            ycApi.SetIO(1, 2);
                                            ycApi.SetIO(1, 0);
                                            ycApi.SetBeep(false);
                                            refresh();
											/*Intent intent = new Intent(mContext, InputOrOutPutOperationActivity.class);
											mContext.startActivity(intent);*/
                                            finish();
                                            // getApplication().onTerminate();
                                        }
                                    }).setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setCancelable(false).show();
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        readerIOHandler.removeCallbacks(readerIORunnable);
        startHandler.removeCallbacks(startRunnable);
        ycApi.SetIO(1, 1);
        ycApi.SetIO(1, 2);
        ycApi.SetIO(1, 0);
        ycApi.SetBeep(false);
        refresh();
        finish();
        super.onDestroy();
    }

    HandlerUtils handlerUtils = new HandlerUtils(this, new HandlerUtilsCallback() {
        @Override
        public void handlerExecutionFunction(Message msg) {
            if (isCommit) {
                if (MethodEnum.PUTBJINFO.equals(msg.getData().getString("method"))) {
                    //数据上传成功
                    if (JSON.parseObject(msg.getData().getString("result")).getInteger("code") == 2) {
                        readerIOHandler.removeCallbacks(readerIORunnable);
                        startHandler.removeCallbacks(startRunnable);
                        readerIOHandler.postDelayed(readerIORunnable, 1000);
                        UIHelper.ToastMessage(MainActivity.this, "数据上传成功");
                        refresh();
                    }  else if (JSON.parseObject(msg.getData().getString("result")).getInteger("code") == 1) {

                            isShow = false;
                            errorResultDialog.setMessage(((flag == 1) ? "入库" : "出库") + JSON.parseObject(msg.getData().getString("result")).getString("msg"));
                            errorResultDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                    readerIOHandler.removeCallbacks(readerIORunnable);
                                    startHandler.removeCallbacks(startRunnable);
                                    readerIOHandler.postDelayed(readerIORunnable, 1000);
                                    isShow = true;
                                }
                            });
                            errorResultDialog.show();


                        isStopError = false;
                        errorHandler.removeCallbacks(errorRunnable);
                        errorHandler.postDelayed(errorRunnable, 1);

                        //上传数据为空
                    } else if (JSON.parseObject(msg.getData().getString("result")).getInteger("code") == 0) {
                            isShow=false;
                            errorResultDialog.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info);
                            errorResultDialog.setMessage(((flag == 1) ? "入库" : "出库") + JSON.parseObject(msg.getData().getString("result")).getString("msg"));
                            errorResultDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                    readerIOHandler.removeCallbacks(readerIORunnable);
                                    startHandler.removeCallbacks(startRunnable);
                                    readerIOHandler.postDelayed(readerIORunnable, 1000);
                                    isShow = true;
                                }
                            });
                            errorResultDialog.show();
                        //后面提示声音4次
                        isStopError = false;
                        errorHandler.removeCallbacks(errorRunnable);
                        errorHandler.postDelayed(errorRunnable, 1);

                        //上传数据为空
                    }
                }
                isCommit = false;
            }
        }
    }, new HandlerUtilsErrorCallback() {
        @Override
        public void handlerErrorFunction(Message ms) {
            if (MethodEnum.PUTBJINFO.equals(ms.getData().getString("method"))) {

                //第一次声音
                Thread threadStart = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //蜂鸣器报警
                            ycApi.SetIO(0, 0);
                            ycApi.SetBeep(true);

                            //LED亮
                            ycApi.SetIO(0, 1);
                            ycApi.SetIO(0, 2);

                            Thread.sleep(1000);
                            //关闭蜂鸣器报警
                            ycApi.SetIO(1, 0);
                            ycApi.SetBeep(false);

                            //LED灯灭
                            ycApi.SetIO(1, 1);
                            ycApi.SetIO(1, 2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                threadStart.start();

                errorResultDialog.setTitle("系统提示").setIcon(android.R.drawable.ic_dialog_info);
                errorResultDialog.setMessage("数据接口上传失败");
                errorResultDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        readerIOHandler.removeCallbacks(readerIORunnable);
                        startHandler.removeCallbacks(startRunnable);
                        readerIOHandler.postDelayed(readerIORunnable, 1000);
                        refresh();
                    }
                });
                errorResultDialog.show();



            }
        }
    });
    boolean isShow=true;

    boolean isStopError=false;

    Handler successHandler = new Handler();
    Runnable successRunnable = new Runnable() {
        @Override
        public void run() {
            ycApi.SetIO(0, 1);
            ycApi.SetIO(0, 2);
            ycApi.SetIO(1, 0);
            ycApi.SetBeep(false);
        }
    };

    Handler errorHandler = new Handler();
    Runnable errorRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isStopError) {
                try {
                    ycApi.SetIO(1, 1);
                    ycApi.SetIO(1, 2);
                    isStopError=true;
                    //声音提示4秒
                    for (int i=0;i<3;i++) {

                        if(!isShow) {


                            //蜂鸣器报警
                            ycApi.SetBeep(true);
                            ycApi.SetIO(0, 0);

                            //LED亮
                            ycApi.SetIO(0, 1);
                            ycApi.SetIO(0, 2);

                            Thread.sleep(800);
                            //关闭蜂鸣器报警
                            ycApi.SetIO(1, 0);
                            ycApi.SetBeep(false);

                            //LED灯灭
                            ycApi.SetIO(1, 1);
                            ycApi.SetIO(1, 2);
                            Thread.sleep(800);
                        }else{
                            break;
                        }
                    }
                    errorHandler.postDelayed(this,1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                ycApi.SetIO(0, 1);
                ycApi.SetIO(0, 2);
                ycApi.SetIO(1, 0);
                ycApi.SetBeep(false);

                isStopError=false;
            }

        }
    };

    private void refreshList() {
        //   mTagRealList.refreshList();
        tagList.clear();
        tempList.clear();
        adpater.notifyDataSetChanged();
    }

    //acc the total time;
    private static long TotalTime = System.currentTimeMillis();
    //acc the total time

    private void refreshText() {
        mTagsCountText.setText(String.valueOf(tagList.size()));

    }

    @Override
    protected void onResume() {
        refreshText();
        refreshList();
        // end_add by lei.li 2016/11/09
        refreshStartStop(true);
        successHandler.removeCallbacks(successRunnable);
        successHandler.postDelayed(successRunnable,1);

        //开启红外监控
        readerIOHandler.postDelayed(readerIORunnable,1000);

        errorResultDialog = new AlertDialog.Builder(this);
        super.onResume();
    }



    private void clearText() {
        //   mReaderHelper.setInventoryTotal(0);
        mTagsCountText.setText("0");
        mTagsTotalText.setText("0");
        mTagsSpeedText.setText("0");
        mTagsTimeText.setText("0");
        mTagsOpTimeText.setText("0");

    }

    boolean isLoop=true;
    //提交判断
    boolean isCommit=false;




    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mLogList.tryClose())
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }



}
