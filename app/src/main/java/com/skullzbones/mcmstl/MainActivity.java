package com.skullzbones.mcmstl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.skullzbones.mcmstl.Networks.LocalVPNService;
import com.skullzbones.mcmstl.ui.ViewPagerAdapter;
import com.skullzbones.mcmstl.ui.main.MainFragment;
import com.skullzbones.mcmstl.ui.support.SupportFragment;

import static com.skullzbones.mcmstl.STORAGE.DATA.LOCAL_BRODCAST_ADDR;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);

        if (savedInstanceState == null) {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            LOCAL_BRODCAST_ADDR = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            setupTabs();

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
        }
    }

    private void setupTabs() {
        viewPager.setAdapter(createCardAdapter());
        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("Main");
                                break;
                            case 1:
                                tab.setText("Support");
                                break;
                            default:
                                break;
                        }
                    }
                }).attach();
    }

    private ViewPagerAdapter createCardAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        return adapter;
    }
}