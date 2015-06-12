package com.monkeyviewcontroller.snapthat;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TabbedFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static TabbedFragment newInstance(int sectionNumber) {
        Log.d("MVC", "ViewPageSec: " + sectionNumber);
        TabbedFragment fragment = new TabbedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TabbedFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager,container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            Bundle bundle = this.getArguments();
            int position = bundle.getInt(ARG_SECTION_NUMBER);
            setupViewPager(viewPager, position);
        }

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager, int position) {

        Adapter adapter = new Adapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(null);

        if(position == 0)
        {
            adapter.addFragment(new MyFriendsFragment(), "My Friends");
            adapter.addFragment(new AddFriendsFragment(), "Add Friends");
        }
        else if(position == 1)
        {
            adapter.addFragment(new CurrentGamesFragment(), "Current");
            adapter.addFragment(new PastGamesFragment(), "Past");
            adapter.addFragment(new HighScoresFragment(), "Leaderboard");
        }

        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}