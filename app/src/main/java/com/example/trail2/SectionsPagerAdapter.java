package com.example.trail2;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position)
        {
            case 0:
                refragments requestsfragment=new refragments();
                return requestsfragment;

            case 1:
                chafragments chatsfragment=new chafragments();
                return chatsfragment;

            case 2:
                frienfragments friendsfragment=new frienfragments();
                return friendsfragment;

            default:
                return null;

        }


    }

    @Override
    public int getCount() {

        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
            { return "REQUESTS";}
            case 1:
            {return  "CHATS";}

            case 2:
            {return "FRIENDS";}


                default:
                {return null;}
        }


    }
}
