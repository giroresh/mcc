package giroresh.mediacenterclient.fragments;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * FragmentAdapter basically the same as the android example
 */
public class FragmentAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] {"All", "Audio", "Video", "Rom"};
    private Context context;

    public FragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AllFragment.newInstance(position);
            case 1:
                return AudioPageFragment.newInstance(position);
            case 2:
                return VideoPageFragment.newInstance(position);
            case 3:
                return ROMPageFragment.newInstance(position);
            default:
                return AllFragment.newInstance(1);
            }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}