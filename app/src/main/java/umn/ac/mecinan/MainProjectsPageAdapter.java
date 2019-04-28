package umn.ac.mecinan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class MainProjectsPageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public MainProjectsPageAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.numOfTabs = tabCount;

        Log.d("EZRA", "Start Main Project Adapter");
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MainOngoingFragment();
            case 1:
                return new MainPastFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}