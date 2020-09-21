package com.test;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.entry.RidApiList;
import com.rscja.utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/8/5/005.
 */

public class TestMain {
    public static void main(String[] args) {
        int s=0x00^0x0A^0x82^0x00^0x00; //6A
     //   System.out.println(yihuo(s));
      //  System.out.println(Integer.toBinaryString(Integer.valueOf(s+"",16)));
      //  System.out.println(s);

        List<RidApiList> api = new ArrayList<>();
        RidApiList r = new RidApiList();
        r.setEpc("1232321");
        api.add(r);

        System.out.println(JSON.toJSONString(api));

     //   System.out.println(xor(s,"0D"));
    }
    public static String yihuo(String content) {
        content = change(content);
        String[] b = content.split(" ");
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
        if(a<10){
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();
        }
        return Integer.toHexString(a);
    }
    public static String change(String content) {
        String str = "";
        for (int i = 0; i < content.length(); i++) {
            if (i % 2 == 0) {
                str += " " + content.substring(i, i + 1);
            } else {
                str += content.substring(i, i + 1);
            }
        }
        return str.trim();
    }

    private static String xor(String strHex_X,String strHex_Y){
        //将x、y转成二进制形式
        String anotherBinary=Integer.toBinaryString(Integer.valueOf(strHex_X,16));
        String thisBinary=Integer.toBinaryString(Integer.valueOf(strHex_Y,16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if(anotherBinary.length() != 8){
            for (int i = anotherBinary.length(); i <8; i++) {
                anotherBinary = "0"+anotherBinary;
            }
        }
        if(thisBinary.length() != 8){
            for (int i = thisBinary.length(); i <8; i++) {
                thisBinary = "0"+thisBinary;
            }
        }
        //异或运算
        for (int i=0;i<anotherBinary.length();i++){
            //如果相同位置数相同，则补0，否则补1
            if(thisBinary.charAt(i)==anotherBinary.charAt(i))
                result+="0"; else{
                result+="1";
            }
        }
        Log.e("code",result);
        return Integer.toHexString(Integer.parseInt(result, 2));
    }

}
