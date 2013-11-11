package com.twtchnz.OneClickPhoto;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

import java.io.UnsupportedEncodingException;

public class SocketConnectionActivity extends FragmentActivity {

    private final WebSocketConnection mConnection = new WebSocketConnection();

    private final ConnectionHandler connectionHandler = new ConnectionHandler();

    private OneClickPhotoMain oneClickPhotoMain;

    public SocketConnectionActivity(OneClickPhotoMain oneClickPhotoMain) {
        this.oneClickPhotoMain = oneClickPhotoMain;
    }

    public void start() {
        try {
            mConnection.connect(OneClickPhotoUtils.WEB_SOCKET_URL, connectionHandler);
        } catch (WebSocketException e) {
            Log.d(OneClickPhotoUtils.APPTAG, e.toString());
        }
    }

    public void sendBinaryMessage(byte[] payload) {
        if(mConnection.isConnected())
            mConnection.sendBinaryMessage(payload);
    }

    public void disconnect() {
        if(mConnection.isConnected())
            mConnection.disconnect();
    }

    private class ConnectionHandler extends WebSocketHandler {

        @Override
        public void onOpen() {
            Log.d(OneClickPhotoUtils.APPTAG, "Status: Connected to " + OneClickPhotoUtils.WEB_SOCKET_URL);
            oneClickPhotoMain.processBitmapToSend();
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            try {
                String str = new String(payload, "UTF-8");
                Log.d(OneClickPhotoUtils.APPTAG, "Got echo: " + str);
            } catch (UnsupportedEncodingException e) {
                Log.d(OneClickPhotoUtils.APPTAG, e.toString());
            }
        }

        @Override
        public void onClose(int code, String reason) {
            Log.d(OneClickPhotoUtils.APPTAG, "Connection lost.");
            Log.d(OneClickPhotoUtils.APPTAG, String.valueOf(code) + " " +  reason);

            oneClickPhotoMain.pictureSent();
        }
    }
}
