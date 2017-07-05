package com.spacepalm.meidreader;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.socket.client.Socket;

/**
 * Created by GUACK on 2017-06-28.
 */
public class DataSender {
    private Socket mSocket;
    private DatabaseReference myRef;
    private FirebaseDatabase database;

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

    public void send(String evt, String str) {
        myRef = database.getReference(str);
        myRef.setValue("{\"status\": 0 }");
        //mSocket.emit(evt, str);
    }

    public void exit() {
        ///mSocket.disconnect();
    }
}
