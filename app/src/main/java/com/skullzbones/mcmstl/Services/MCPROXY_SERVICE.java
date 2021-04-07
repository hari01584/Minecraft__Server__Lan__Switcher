package com.skullzbones.mcmstl.Services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.skullzbones.mcmstl.R;
import com.skullzbones.mcmstl.STORAGE.DATA;


/*
 * Linux command to send UDP:
 * #socat - UDP-DATAGRAM:192.168.1.255:11111,Proxy,sp=11111
 */
public class MCPROXY_SERVICE extends Service {
    private static final String TAG = "r/MCMPROXY";
    private static final String CHANNELID = "ChannelMCProxo";
    public static String MCPROXY = "MCMPROXY";
    Integer port = 25625;

    public static InetAddress LOCAL_IP;
    public static int LOCAL_PORT = 44964;

    public static InetAddress DEBUG_TARGET_ADDR;
    public static int DEBUG_TARGET_PORT = 19132;
    //Boolean shouldListenForUDPProxy = false;
    DatagramSocket socket;
    boolean isFirstTime = true;
    private void listenAndWaitAndThrowIntent() throws Exception {
        byte[] recvBuf = new byte[2056];
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        Log.e("UDP", "Waiting for UDP Proxy");
        socket.receive(packet);

        if(isFirstTime){
            ProxyIntent();
            fillProxyVariables(packet);
            isFirstTime = false;
        }

        Log.d("UDP", "Got UDB from " + packet.getAddress() +":"+ packet.getPort()+":"+LOCAL_PORT);
        RelayData(packet);
    }

    private void fillProxyVariables(DatagramPacket packet) {
        try {
            LOCAL_IP = InetAddress.getByName(packet.getAddress().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOCAL_PORT = packet.getPort();
        Log.i(TAG,"Init local variables "+LOCAL_IP+":"+LOCAL_PORT);
        Log.i(TAG,"Remote local variables "+DEBUG_TARGET_ADDR+":"+DEBUG_TARGET_PORT);
    }

    private void RelayData(DatagramPacket packet) throws Exception {
        InetAddress sender = packet.getAddress();
        int senderPort = packet.getPort();
        if(sender.equals(LOCAL_IP)){
            packet.setAddress(DEBUG_TARGET_ADDR);
            packet.setPort(DEBUG_TARGET_PORT);
            Log.d(TAG,"<");
            socket.send(packet);
        }
        else if (senderPort == DEBUG_TARGET_PORT){
            packet.setAddress(LOCAL_IP);
            packet.setPort(LOCAL_PORT);
            Log.d(TAG,">");
            socket.send(packet);
        }
    }

    private void ProxyIntent() {
        Intent intent = new Intent(MCPROXY_SERVICE.MCPROXY);
    }

    private InetAddress getProxyAddress() throws IOException {
        return InetAddress.getByName("0.0.0.0");
    }


    Thread UDPProxyThread;

    void startListenForUDPProxy() {
        UDPProxyThread = new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress ProxyIP = getProxyAddress();
                    if (socket == null || socket.isClosed()) {
                        socket = new DatagramSocket(port, ProxyIP);
                        socket.setBroadcast(true);
                    }
                    while (shouldRestartSocketListen) {
                        listenAndWaitAndThrowIntent();
                    }
                    //if (!shouldListenForUDPProxy) throw new ThreadDeath();
                } catch (Exception e) {
                    Log.i("UDP", "no longer listening for UDP Proxys cause of error " + e.getMessage());
                }
                socket.close();
            }
        });
        UDPProxyThread.start();
    }

    private Boolean shouldRestartSocketListen=true;

    void stopListen() {
        shouldRestartSocketListen = false;
        socket.close();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"Closing Service MCPROXY");
        RaknetPing.stopRakNET();
        stopListen();
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shouldRestartSocketListen = true;
        startListenForUDPProxy();
        startMyOwnForeground();
        Log.i("UDP", "Service started");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        RaknetPing.stopRakNET();
        stopListen();
        stopSelf();
    }
    private void startMyOwnForeground(){
        String NOTIFICATION_CHANNEL_ID = "com.skullzbones.mstls";
        String channelName = "MC Relay Proxy";
        NotificationChannel chan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Minecraft Relay Proxy Is Still Running..")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}