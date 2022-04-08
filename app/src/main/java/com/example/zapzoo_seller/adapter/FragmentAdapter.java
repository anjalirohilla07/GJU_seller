package com.example.zapzoo_seller.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.zapzoo_seller.fragments.AcceptedFragment;
import com.example.zapzoo_seller.fragments.OrderFragment;
import com.example.zapzoo_seller.fragments.PaymentFragment;
import com.example.zapzoo_seller.fragments.ProgressFragment;
import com.example.zapzoo_seller.fragments.ReceivedFragment;

public class FragmentAdapter extends FragmentPagerAdapter {

    private int tabNo;

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior,int tabNo) {
        super(fm, behavior);
        this.tabNo=tabNo;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0 : return new ReceivedFragment();
            case 1 : return new AcceptedFragment();
            case 2 : return new PaymentFragment();
            case 3 : return new ProgressFragment();
            case 4 : return new OrderFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabNo;
    }
}
