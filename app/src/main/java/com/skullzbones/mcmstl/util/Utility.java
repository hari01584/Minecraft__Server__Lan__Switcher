package com.skullzbones.mcmstl.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.skullzbones.mcmstl.R;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.skullzbones.mcmstl.STORAGE.DATA.RAKNET_PING_PACKET;


public class Utility {



    public static void ToastErr(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String ComposePingPacket(){
        return RAKNET_PING_PACKET;
    }

    public static InetAddress getLocalAddress() {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        //return inetAddress.getHostAddress().toString();
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("SALMAN", ex.toString());
        }
        return null;
    }


}
