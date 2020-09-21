package com.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtils {
    // private  String PATH = "http://www.rfid-barcode.net:9002/api";
    //  private  static String PATH = "http://192.168.1.34:9001/api";
//   private String PATH ="http://192.168.1.123:9002//api";
// private String PATH ="http://123.56.7.226:8100";
 //   private String PATH="http://39.108.121.64:8100";
//       private String PATH="http://47.105.217.172:8100";
    private String PATH="http://59.36.8.106:8100";

    private URL url;
    public HttpUtils() {
        // TODO Auto-generated constructor stub
    }
    /**
     * @param params 填写的url的参数
     * @param method 调用的api接口的路径
     * @return
     */
    public String sendPostMessage(Context context, Map<String,Object> params, String method,String token,boolean isErrorInfo){
        try {
            SharedPreferences sp = context.getSharedPreferences("setting_action_url_config", Context.MODE_PRIVATE);
            if(!isErrorInfo) {
                url = new URL(sp.getString("actionUrl", "http://rfid.haiyangli.cn:8081/dagl") + method);
            }else{
                url = new URL("http://www.rfid-barcode.net:9002/api"+method);
            }
            Log.d("JSON",  params+"");
            byte[] mydata;
            try {
                if(!isErrorInfo) {

                    Log.d("JSON", JSON.toJSONString(params.get("rfidApiList")));
                    mydata = JSON.toJSONString(params.get("rfidApiList")).getBytes();
                }else{
                    Log.d("JSON", JSON.toJSONString(params));
                    mydata= JSON.toJSONString(params).getBytes();
                }
            }catch (com.alibaba.fastjson.JSONException e){
                e.printStackTrace();
                return "{ \"Success\": false}" ;
            }
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1200000);
            connection.setDoInput(true);//表示从服务器获取数据
            connection.setDoOutput(true);//表示向服务器写数据
            //获得上传信息的字节大小以及长度
            connection.setRequestMethod("POST");

            //是否使用缓存
            connection.setUseCaches(false);
            //表示设置请求体的类型是文本类型
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", String.valueOf(mydata.length));
            connection.setRequestProperty("timestamp",transForMilliSecond(new Date())+"");
            if(token!=null)
                connection.setRequestProperty("Authorization",token);

            connection.connect();   //连接，不写也可以。。？？有待了解
            //获得输出流，向服务器输出数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(mydata,0,mydata.length);
            outputStream.flush();
            //获得服务器响应的结果和状态码
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                return changeInputeStream(connection.getInputStream(),"utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
    public String sendGetMessage(Map<String,Object> params, String method){
        try {
            int index = -1;
            StringBuffer paths = new StringBuffer(PATH + method);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (index == -1) {
                    paths.append("?" + entry.getKey() + "=" + entry.getValue());
                    index = 1;
                } else {
                    paths.append("&" + entry.getKey() + "=" + entry.getValue());
                }
            }
            url = new URL(paths.toString());
            Log.d("url", "sendGetMessage: " + paths.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求超时时间
            conn.setConnectTimeout(1200000);// 5秒
            // 设置发送请求方式
            conn.setRequestMethod("GET");
            // 获取服务器返回的状态码
            int code = conn.getResponseCode();
            // 200表明请求成功
            if (code == 200) {
                String conent =   changeInputeStream(conn.getInputStream(),"utf-8");
                Log.d("url", "sendGetMessage: "+conent);
                return conent;
            }
        }catch (IOException e){
            Log.d("url", "sendGetMessage:  error");
        }
        return "";
    }

    /**
     * 将一个输入流转换成字符串
     * @param inputStream
     * @param encode
     * @return
     */
    private static String changeInputeStream(InputStream inputStream, String encode) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();
                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(),encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 日期转时间戳
     * @param date
     * @return
     */
    public static Integer transForMilliSecond(Date date){
        if(date==null) return null;
        return (int)(date.getTime()/1000);
    }
    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     * @return  手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getDeviceId();
        }
        return null;
    }
    /**
     * 获取外网的IP(要访问Url，要放到后台线程里处理)
     * @param @return
     * @return String
     * @throws
     * @Title: GetNetIp
     * @Description:
     */
    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {
            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    strber.append(line + "\n");
                }
                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        Log.e("getNetIp", ipLine);
        return ipLine;
    }
    public static String TAG="upLoading";
    public String uploadImage(Map<String,Object> params, String method) {
        Log.d(TAG, "upload begin");
        HttpURLConnection connection = null;
        DataOutputStream dos = null;
        FileInputStream fin = null;
        String boundary = "---------------------------265001916915724";
        // 真机调试的话，这里url需要改成电脑ip
        // 模拟机用10.0.0.2,127.0.0.1被tomcat占用了
        String urlServer = PATH+method;
        String lineEnd = "\r\n";
        //String pathOfPicture = "/sdcard/aaa.jpg";
        int bytesAvailable, bufferSize, bytesRead;
        int maxBufferSize = 1*1024*512;
        byte[] buffer = null;
        try {
            Log.d(TAG, "try");
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            // 允许向url流中读写数据
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);

            // 启动post方法
            connection.setRequestMethod("POST");

            // 设置请求头内容
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "text/plain");

            // 伪造请求头
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            File fileName =  (File)params.get("file");
            if(fileName==null)
            {
                return "{ \"Success\": false}";
            }
            // 开始伪造POST Data里面的数据
            dos = new DataOutputStream(connection.getOutputStream());
            fin = new FileInputStream(fileName);

            Log.d(TAG, "开始上传images");
            //--------------------开始伪造上传images.jpg的信息-----------------------------------
            String fileMeta = "--" + boundary + lineEnd +
                    "Content-Disposition: form-data; name=\"uploadedPicture\"; filename=\"" + fileName.getName() + "\"" + lineEnd +
                    "Content-Type: image/png" + lineEnd + lineEnd;
            // 向流中写入fileMeta
            dos.write(fileMeta.getBytes());
            // 取得本地图片的字节流，向url流中写入图片字节流
            bytesAvailable = fin.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fin.read(buffer, 0, bufferSize);
            while(bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fin.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fin.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd+lineEnd);
            //--------------------伪造images.jpg信息结束-----------------------------------
            Log.d(TAG, "结束上传");

            // POST Data结束
            dos.writeBytes("--" + boundary + "--");
            // Server端返回的信息
            System.out.println("" + connection.getResponseCode());
            System.out.println("" + connection.getResponseMessage());
            Log.d(TAG, "upload: "+connection.getResponseMessage());
            Log.d(TAG, "upload: "+connection.getResponseCode());
            if(connection.getResponseCode()==200){
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len = 0;
                byte[] buffer1 = new byte[1024];
                while((len = inputStream.read(buffer1 ))!=-1){
                    bos.write(buffer1, 0, len);
                }
                bos.flush();
                inputStream.close();
                bos.close();
                String result =  new String(bos.toByteArray());
                Log.d("upLoading", "run: "+result);
                return  result;
            }
            if (dos != null) {
                dos.flush();
                dos.close();
            }
            Log.d(TAG, "upload success-----------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "upload fail");
        }
        return "{ \"Success\": false}";
    }

}