package com.entry;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Administrator on 2019/7/27/027.
 */
public enum Cache {

    INSTANCE;

    public BlockingDeque<String> list = new LinkedBlockingDeque<>();


    public void put(String str) {
        try {
            list.put(str);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String take(){
        String str = null;
        try {
            str =  list.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return str;
    }

    public boolean isEmpty(){
        return list.isEmpty();
    }
}