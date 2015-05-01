package com.monkeyviewcontroller.snapthat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.monkeyviewcontroller.snapthat.Adapters.CurrentGameListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    List<Game> currentGames;

    public List<Game> getCurrentGames(){
        if (currentGames == null){
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("user", ParseUser.getCurrentUser().getObjectId());

            ParseCloud.callFunctionInBackground("getcurrentgames", params, new FunctionCallback<List<Game>>() {
                @Override
                public void done(List<Game> games, com.parse.ParseException e) {
                    if (e != null) {
                        Log.d("MVC", "get current games error: " + e + " " + e.getCause());
                    } else {
                        Log.d("MVC", "got the current games");
                        currentGames = games;
                        Log.d("DEBUG", "Done getting current");



                    }
                }
            });
        }

        if(currentGames == null){
            Log.i("DEBUG", "currentGames is still null");
        }
        return currentGames;
    }

    public void setCurrentGames(List<Game> games){
        currentGames = games;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getCurrentGames();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.d("MVC", "On Create .....");

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position==0)
                return TabbedFragment.newInstance(0);
            else if(position==1)
                return SnapsFragment.newInstance();
            else if(position==2)
                return TabbedFragment.newInstance(1);
            else
                return PlaceholderFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }
}