package com.entry;

/**
 * Created by Administrator on 2019/7/18/018.
 */

public class Inventory {

    private String epc;
    private String rssi;

    public Inventory() {
    }

    public Inventory(String epc, String rssi) {
        this.epc = epc;
        this.rssi = rssi;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
