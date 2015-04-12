package com.monkeyviewcontroller.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SnapsFragment extends Fragment {

    public static SnapsFragment newInstance() {
        SnapsFragment fragment = new SnapsFragment();
        return fragment;
    }

    public SnapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the snap tab");

        View rootView = inflater.inflate(R.layout.fragment_snaps, container, false);
        return rootView;
    }
}