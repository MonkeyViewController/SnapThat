package com.monkeyviewcontroller.snapthat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.baoyz.widget.PullRefreshLayout;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Adapters.CurrentGameListAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CurrentGamesFragment extends Fragment {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private CurrentGameListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private List<Game> currentGames;
    //private PullRefreshLayout layout;

    public static CurrentGamesFragment newInstance() {
        CurrentGamesFragment fragment = new CurrentGamesFragment();
        return fragment;
    }

    public CurrentGamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the CurrentGamesView");
        final View rootView = inflater.inflate(R.layout.fragment_currentgames, container, false);

        llProgressBar = (LinearLayout)rootView.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout)rootView.findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView)rootView.findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)rootView.findViewById(R.id.tvEmptyList);


        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.d("MVC", "Checked item at position " + position);
                //launch SubmissionHistory activity
//                String gameOID =currentGames.get(position).getObjectId();
//
//                Intent intent = new Intent(getActivity(), GameSubmissionsActivity.class);
//                intent.putExtra("gameOID",gameOID);
//                startActivity(intent);

            }});

        //layout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        //Load Initial list of games
        loadAllCurrentGames();

        //setupPullDownRefresh();
        return rootView;
    }

//    private void setupPullDownRefresh(){
//        // listen refresh event
//        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.i("MVC", "Pull Down Refresh on Current Games Activated");
//
//                loadAllCurrentGames();
//            }
//        });
//    }


    //Pass layout so that we can end the pull down refresh loading animation
    public void loadAllCurrentGames()
    {
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user",ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("getcurrentgames", params, new FunctionCallback<List<Game>>() {
            @Override
            public void done(List<Game> games, com.parse.ParseException e) {
                hideProgressDialog();

                if(e!=null) {
                    Log.d("MVC", "get current games error: " + e + " " + e.getCause());
                }
                else {
                    Log.d("MVC", "got the current games");
                    currentGames = games;
                    listAdapter = new CurrentGameListAdapter(getActivity(), games);

                    if (listAdapter.isEmpty()) {
                        //TODO: add a button that when clicked moves to the friends fragment
                        tvEmptyList.setText("No Active Games, create one!");
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        lvQueryResults.setAdapter(listAdapter);
                    }


                }
                //layout.setRefreshing(false);

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
        this.getActivity().registerReceiver(new SyncActivityReceiver(), new IntentFilter("com.monkeyviewcontroller.snapthat"));
    }

    class SyncActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("push", "sync broadcast received.");
            loadAllCurrentGames();
        }
    }
}
