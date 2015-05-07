package com.monkeyviewcontroller.snapthat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.monkeyviewcontroller.snapthat.Adapters.PastGameListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class PastGamesFragment extends Fragment {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private PastGameListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private List<Game> pastGames;

    public static PastGamesFragment newInstance() {
        PastGamesFragment fragment = new PastGamesFragment();
        return fragment;
    }

    public PastGamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the PastGamesView");
        final View rootView = inflater.inflate(R.layout.fragment_pastgames, container, false);

        llProgressBar = (LinearLayout)rootView.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout)rootView.findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView)rootView.findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)rootView.findViewById(R.id.tvEmptyList);

        loadPastGames();

        //TODO: only show button when 1 item is selected, fade when scrolling, fix when it covers bottom row
        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.d("MVC", "Clicked item at position " + position);

            }});

        return rootView;
    }


    public void loadPastGames()
    {
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user",ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("getpastgames", params, new FunctionCallback<List<Game>>() {
            @Override
            public void done(List<Game> games, com.parse.ParseException e) {
                hideProgressDialog();

                if(e!=null) {
                    Log.d("MVC", "get past games error: " + e + " " + e.getCause());
                }
                else {
                    Log.d("MVC", "got the past games");
                    pastGames = games;
                    listAdapter = new PastGameListAdapter(getActivity(), games);

                    if (listAdapter.isEmpty()) {
                        //TODO: add a button that when clicked moves to the current games fragment
                        tvEmptyList.setText("No Games Are Finished!");
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        lvQueryResults.setAdapter(listAdapter);
                    }
                }
            }
        });
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
