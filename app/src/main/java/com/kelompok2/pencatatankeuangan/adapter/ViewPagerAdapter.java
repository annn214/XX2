package com.kelompok2.pencatatankeuangan.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.kelompok2.pencatatankeuangan.fragment.FragmentBeranda;
import com.kelompok2.pencatatankeuangan.fragment.FragmentInput;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new FragmentInput();
        }
        return new FragmentBeranda();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
