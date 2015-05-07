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

import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Adapters.HighScoreListAdapter;
import com.monkeyviewcontroller.snapthat.Adapters.PastGameListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class HighScoresFragment extends Fragment {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private HighScoreListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private List<STUser> users;

    public static HighScoresFragment newInstance() {
        HighScoresFragment fragment = new HighScoresFragment();
        return fragment;
    }

    public HighScoresFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the HighScoresView");
        final View rootView = inflater.inflate(R.layout.fragment_highscores, container, false);

        llProgressBar = (LinearLayout)rootView.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout)rootView.findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView)rootView.findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)rootView.findViewById(R.id.tvEmptyList);

        loadHighScores();

        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.d("MVC", "Clicked item at position " + position);
                //TODO: Maybe show win % , total games, total submissions
            }});

        return rootView;
    }


    public void loadHighScores()
    {
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("gethighscores", params, new FunctionCallback<List<STUser>>() {
            @Override
            public void done(List<STUser> usersReturned, com.parse.ParseException e) {
                hideProgressDialog();

                if (e != null) {
                    Log.d("MVC", "get highscores error: " + e + " " + e.getCause());
                } else {
                    Log.d("MVC", "got the highscores");
                    users = usersReturned;
                    listAdapter = new HighScoreListAdapter(getActivity(), users);

                    if (listAdapter.isEmpty()) {
                        //TODO: add a button that when clicked moves to the current games fragment
                        tvEmptyList.setText("No High Scores Are Available");
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
