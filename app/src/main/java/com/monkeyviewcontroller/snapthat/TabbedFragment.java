package com.monkeyviewcontroller.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabbedFragment extends Fragment {
    private FragmentTabHost mTabHost;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static TabbedFragment newInstance(int sectionNumber) {
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

        View rootView = inflater.inflate(R.layout.fragment_tabs,container, false);

        mTabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        Bundle bundle = this.getArguments();
        int position = bundle.getInt(ARG_SECTION_NUMBER);

        if(position==0)
        {
            mTabHost.addTab(mTabHost.newTabSpec("myfriendsfragment").setIndicator("My Friends"),
                    MyFriendsFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("addfriendsfragment").setIndicator("Add Friends"),
                    AddFriendsFragment.class, null);
        }
        else if(position==1)
        {
            mTabHost.addTab(mTabHost.newTabSpec("currentgamesfragment").setIndicator("Current"),
                    CurrentGamesFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("pastgamesfragment").setIndicator("Past"),
                    PastGamesFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("nearbygamesfragment").setIndicator("Nearby"),
                    NearbyGamesFragment.class,null);
        }

        mTabHost.setCurrentTab(0);

        return rootView;
    }
}