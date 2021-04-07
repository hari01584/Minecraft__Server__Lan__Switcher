package com.skullzbones.mcmstl.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skullzbones.mcmstl.ui.main.MainFragment;
import com.skullzbones.mcmstl.ui.support.SupportFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 2;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull @Override public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MainFragment();
            case 1:
                return new SupportFragment();
            default:
                return null;
        }
    }

    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}