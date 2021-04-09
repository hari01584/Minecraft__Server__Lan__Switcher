package com.skullzbones.mcmstl.ui.main;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skullzbones.mcmstl.Networks.LocalVPNService;
import com.skullzbones.mcmstl.R;
import com.skullzbones.mcmstl.Services.MCPROXY_SERVICE;
import com.skullzbones.mcmstl.util.Utility;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    public boolean waitingForVPNStart;
    private Intent intentStart;

    public static MainFragment newInstance() {
        return new MainFragment();
    }
    private static final int VPN_REQUEST_CODE = 0x0F;

    private static String TAG = "r/MainFragment";
    private EditText ipaddr;
    private EditText port;
    private Button mButton;

    private BroadcastReceiver vpnStateReceiver = new BroadcastReceiver()
        {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (LocalVPNService.BROADCAST_VPN_STATE.equals(intent.getAction()))
            {
                if (intent.getBooleanExtra("running", false))
                    waitingForVPNStart = false;
                if (intent.getBooleanExtra("topStop", false)) {
                    Log.i(TAG, "Stopping VPN");
                    //localVPNService.cleanup();
                    localVPNService.stopSelf();
                    getContext().stopService(intent);
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        ipaddr = v.findViewById(R.id.target_server_ip);
        port = v.findViewById(R.id.target_server_port);
        mButton = v.findViewById(R.id.butn_start_forward);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(vpnStateReceiver,
                new IntentFilter(LocalVPNService.BROADCAST_VPN_STATE));

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        if(mViewModel.ipaddress == null){
            bindButtonListner();
            loadDataFromSharedPref();
        }
        else{
            ipaddr.setText(mViewModel.ipaddress);
            port.setText(String.valueOf(mViewModel.port));
        }
        
        // TODO: Use the ViewModel
    }

    private void bindButtonListner() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.ipaddress = ipaddr.getText().toString();
                if (mViewModel.ipaddress.equals("")) {
                    Utility.ToastErr(getContext(),getResources().getString(R.string.error_invalid_textbox_args));
                    return;
                }

                try {
                    mViewModel.port = Integer.valueOf( (port.getText().toString()!=null)?port.getText().toString():"19132" );
                }
                catch (Exception e){
                    Utility.ToastErr(getContext(),getResources().getString(R.string.error_invalid_integer_args));
                    return;
                }
                startRelay();
            }
        });
    }

    private void startRelay() {
        saveDataToSharedPref();
        try {
            MCPROXY_SERVICE.DEBUG_TARGET_ADDR = InetAddress.getByName(mViewModel.ipaddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        MCPROXY_SERVICE.DEBUG_TARGET_PORT = mViewModel.port;
        startVPN();
    }

    private void loadDataFromSharedPref() {
        SharedPreferences prefs = getContext().getSharedPreferences(
                "dxdinfo", Context.MODE_PRIVATE);
        mViewModel.ipaddress = prefs.getString("ip", "");
        mViewModel.port = prefs.getInt("port",19132);

        ipaddr.setText(mViewModel.ipaddress);
        port.setText(String.valueOf(mViewModel.port));
    }

    private void saveDataToSharedPref() {
        SharedPreferences.Editor prefs = getContext().getSharedPreferences(
                "dxdinfo", Context.MODE_PRIVATE).edit();
        prefs.putString("ip",mViewModel.ipaddress);
        prefs.putInt("port", mViewModel.port);
        prefs.apply();
    }

    private void startVPN()
    {
        Intent vpnIntent = VpnService.prepare(getContext());
        if (vpnIntent != null)
            startActivityForResult(vpnIntent, VPN_REQUEST_CODE);
        else
            onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
    }
    LocalVPNService localVPNService;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK)
        {
            successVPNStart();
        }
    }

    private void successVPNStart() {
        Utility.ToastErr(getContext(),"Successfully Started! Join the server from LAN Tab! :D");
        waitingForVPNStart = true;
        localVPNService = new LocalVPNService();
        intentStart = new Intent(getContext(), localVPNService.getClass());
        intentStart.putExtra("destaddr",mViewModel.ipaddress);
        intentStart.putExtra("destport",mViewModel.port);
        getContext().startService(intentStart);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.mojang.minecraftpe","com.mojang.minecraftpe.MainActivity"));
        startActivity(intent);
    }

}