package com.monkeyviewcontroller.snapthat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NearbyGamesFragment extends Fragment {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private FriendListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private Boolean[] selected;

    public static NearbyGamesFragment newInstance() {
        NearbyGamesFragment fragment = new NearbyGamesFragment();
        return fragment;
    }

    public NearbyGamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the NearbyGamesView");
        final View rootView = inflater.inflate(R.layout.fragment_nearbygames, container, false);

        llProgressBar = (LinearLayout)rootView.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout)rootView.findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView)rootView.findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)rootView.findViewById(R.id.tvEmptyList);

        loadNearbyGames();

        //TODO: only show button when 1 item is selected, fade when scrolling, fix when it covers bottom row
        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.d("MVC", "Clicked item at position " + position);

            }});

        return rootView;
    }


    public void loadNearbyGames()
    {
        showProgressDialog();

        //TODO: Actually implement this on the cloud
        /*
        ParseCloud.callFunctionInBackground("getcurrentgames", params, new FunctionCallback<List<STUser>>() {
            @Override
            public void done(List<STUser> users, com.parse.ParseException e) {
                hideProgressDialog();
                friends = users;
                listAdapter = new FriendListAdapter(getActivity(), friends, currentObjectId);

                if (listAdapter.isEmpty()) {
                    tvEmptyList.setText("No Friends, Go Add Some!");
                    llEmptyList.setVisibility(View.VISIBLE);
                } else {
                    lvQueryResults.setAdapter(listAdapter);
                    selected = new Boolean[friends.size()];
                    Arrays.fill(selected, false);
                }
            }
        });*/
    }

    private void showProgressDialog() {
        llProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        llProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
