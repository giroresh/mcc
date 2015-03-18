package giroresh.mediacenterclient;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import giroresh.mediacenterclient.layout.SlidingTabLayout;

/**
 * Based on the android example
 * The Playlist is now a sliding tab
 * the actual playlist work is done within the fragments
 */
public class Playlist extends FragmentActivity implements SlidingTabLayout.TabColorizer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlistfragact);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), Playlist.this));

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(this);
    }

    /**
     * @param position position of the selected fragment/tab
     *
     * @return return the color of the indicator used when {@code position} is selected.
     */
    @Override
    public int getIndicatorColor(int position) {
        switch (position) {
            case 0:
                return Color.BLUE;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.WHITE;
            case 3:
                return Color.CYAN;
            default:
                return Color.GREEN;
        }
    }
}