package com.spacepalm.meidreader;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by GUACK on 2017-06-28.
 */
public class DataSender {
    private Socket mSocket;

    public DataSender(String url) {
        try {
            if (url == null)
                url = "http://192.168.1.10:3000";

            mSocket = IO.socket(url);
            mSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String evt, String str) {
        mSocket.emit(evt, str);
    }

    public void exit() {
        mSocket.disconnect();
    }
}
