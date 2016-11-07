package com.example.gvolf.lightshow;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * Created by gvolf on 8/8/15.
 */
public class BtManage extends Thread{
    private OutputStream out = null;
    private InputStream in = null;
    private Handler handler;
    //private LinkedList<Byte> stack;

    //here input are the byte codes defined
    public final byte prog = 0x01;
    public final byte beat = 0x00;
    public final byte step = 0x02;
    public final byte running = 0x04;
    public final byte mode = 0x05;


    //here are output the byte codes defined
    public static byte[] beat_plus = {0x00};
    public static byte[] beat_minus = {0x01};
    public static byte[] prog_plus = {0x02};
    public static byte[] prog_minus = {0x03};
    public static byte[] stop_start = {0x04};
    public static byte[] get_all = {10};

    public BtManage(BluetoothSocket socket) {
        try {
            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        } catch (IOException exp) { }
    }

    public void send (byte[] bytes) {
        try {
            out.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run(){
        int b;
        int bytes;
        byte[] buffer = new byte[1024];
        Bundle bundle = new Bundle();
        while (handler == null) {}
        while (true){
            String result = null;
            try {
                bytes = in.read(buffer);
                    Message msg = handler.obtainMessage();
                    bundle.putByteArray("msg", buffer);
                    bundle.putInt("size", bytes);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private String decodeBytes(byte[] bytes){
        switch (bytes[0]){
            case prog:
                return "prog " + String.valueOf(bytes[1]+1);
            case step:
                return "step " + String.valueOf(bytes[1]);
            case beat:
                return "beat " + String.valueOf(bytes[1]) + "0";
            default:
                return "damn " + Integer.toBinaryString(bytes[0]);
        }
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

}
