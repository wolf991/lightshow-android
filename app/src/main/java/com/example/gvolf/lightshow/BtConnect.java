package com.example.gvolf.lightshow;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;



/**
 * Yes, gvolf created it, now shut up for fucks sake!!!
 */
public class BtConnect extends Thread{
    public static int REQUEST_ENABLE_BT = 1;
    public static Handler handler;
    public static BluetoothSocket socket;
    public static BtManage btManager;

    static BluetoothAdapter BtAdapter = BluetoothAdapter.getDefaultAdapter();

    public static void connectToLightShow(Handler handler) {
        BtConnect.handler = handler;
        new BtConnect().start();
    }

    public void run() {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("msg", "STARTPROG");
        msg.setData(bundle);
        handler.sendMessage(msg);
        Set<BluetoothDevice> pDevices = BtAdapter.getBondedDevices();
        BluetoothDevice lightShow = null;
        for (BluetoothDevice device : pDevices) {
            if (device.getName().equals("HC-06"))
                lightShow = device;
        }
        if (lightShow == null) {
            bundle.putString("msg", "Error: LightShow not paired with device.");
            msg = handler.obtainMessage();
            msg.setData(bundle);
            handler.sendMessage(msg);
        } else{
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            try {
                socket = lightShow.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                bundle.putString("msg", "Error: Can't create socket.");
                msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
                return;
            }

            try {
                socket.connect();
            } catch (IOException e) {
                bundle.putString("msg", "Error: Can't connect to LightShow, is it on?");
                msg = handler.obtainMessage();
                msg.setData(bundle);
                handler.sendMessage(msg);
                return;
            }

            bundle.putString("msg", "OK");
            msg = handler.obtainMessage();
            msg.setData(bundle);
            btManager = new BtManage(socket);
            btManager.start();
            handler.sendMessage(msg);
        }
    }

    public static void cancelConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
