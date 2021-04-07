package com.skullzbones.mcmstl.Services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.skullzbones.mcmstl.MainActivity;
import com.skullzbones.mcmstl.util.Utility;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import static com.skullzbones.mcmstl.STORAGE.DATA.LOCAL_BRODCAST_ADDR;
import static com.skullzbones.mcmstl.STORAGE.DATA.MC_GAME_PORT;


public class RaknetPing
{
    private static final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(1);

    private static final String TAG = "localvpn.App.RaknetPing";
    private static boolean toPing = true;
    public static void startPing(){
        if(!toPing) return;
        toPing = false;

        byte[] packetByte = new byte[1024];
        try {
            packetByte = Hex.decodeHex(Utility.ComposePingPacket());
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        DatagramSocket sock = null;

        try {
            sock = new DatagramSocket();
            Log.i(TAG,"Local Bind "+sock.getLocalAddress().getHostName()+" "+sock.getLocalPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket dp = null;

        try {
            //LOCAL_BRODCAST_ADDR
            dp = new DatagramPacket(packetByte, packetByte.length, InetAddress.getByName("127.0.0.1"), MC_GAME_PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        final DatagramSocket s = sock;
        final DatagramPacket p = dp;


        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    s.send(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public static void stopRakNET(){
        scheduler.shutdownNow();
    }

}