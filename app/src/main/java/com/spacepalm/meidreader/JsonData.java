package com.spacepalm.meidreader;

/**
 * Created by GUACK on 2017-07-05.
 */

public class JsonData {
    public String status;
    public String imei;
    public String time;

    public JsonData() {}

    public JsonData(String _status, String _imei, String _time) {
        this.imei = _imei;
        this.time = _time;
        this.status = _status;
    }
}