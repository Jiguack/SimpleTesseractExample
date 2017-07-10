package com.spacepalm.meidreader;

import android.icu.text.SimpleDateFormat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.socket.client.Socket;

/**
 * Created by GUACK on 2017-06-28.
 */
public class DataSender {
    private Socket mSocket;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

    public String currentTime() {
        String timestamp;
        //if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 24) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            Date resultdate = new Date(System.currentTimeMillis());
            timestamp = sdf.format(resultdate);
        } catch (java.lang.NoClassDefFoundError ncdfe){
            //timestamp = Long.toString(phoneState.getTimestamp());
            timestamp = String.format("%tFT%<tTZ.%<tL",
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));
        }
        return timestamp;
    }

    public DataSender(String url) {
//        try {
//            if (url == null)
//                url = "http://192.168.1.10:3000";
//
//            mSocket = IO.socket(url);
//            mSocket.connect();
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }

        database = FirebaseDatabase.getInstance();
    }

    public void send(String evt, String imei) {
        myRef = database.getReference(imei);
        String t = currentTime();
        JsonData a = new JsonData("INIT", imei, t);

        myRef.setValue(a);
        //mSocket.emit(evt, str);
    }

    public void exit() {
        ///mSocket.disconnect();
    }
}
