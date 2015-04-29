package com.monkeyviewcontroller.snapthat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyFriendsFragment extends Fragment {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private FriendListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private FloatingActionButton fab;
    private Boolean[] selected;
    private List<STUser> friends;

    public static AddFriendsFragment newInstance() {
        AddFriendsFragment fragment = new AddFriendsFragment();
        return fragment;
    }

    public MyFriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the AddFriendView");
        final View rootView = inflater.inflate(R.layout.fragment_myfriends, container, false);

        llProgressBar = (LinearLayout)rootView.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout)rootView.findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView)rootView.findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)rootView.findViewById(R.id.tvEmptyList);
        fab = (FloatingActionButton)rootView.findViewById(R.id.pink_icon);
        fab.setVisibility(View.GONE);

        loadAllFriends();

        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.d("MVC", "Checked item at position " + position);

                CheckBox c = (CheckBox) view.findViewById(R.id.cbMyFriends);

                if (c.isChecked()) {
                    c.setChecked(false);
                } else {
                    c.setChecked(true);
                }

                selected[position] = c.isChecked();
                fab.setVisibility(View.GONE);
                for(boolean b: selected)
                {
                    if(b)
                    {
                        fab.setVisibility(View.VISIBLE);
                    }
                }
        }});

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<STUser.Model> selectedFriends = new ArrayList<>();
                Log.d("MVC", "Clicked the FAB button");

                for(int i=0; i < selected.length; i++)
                {
                    if(selected[i])
                    {
                        Log.d("MVC", "Selected : " + friends.get(i).getUsername() + " " + friends.get(i).getObjectId());
                        selectedFriends.add(friends.get(i).getModel());
                    }
                }

                Intent intent = new Intent(getActivity(), CreateGameActivity.class);
                intent.putExtra("friendsingame", selectedFriends);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void loadAllFriends()
    {
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user",ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("getfriends", params, new FunctionCallback<List<STUser>>() {
            @Override
            public void done(List<STUser> users, com.parse.ParseException e) {
                hideProgressDialog();

                if(e!=null) {
                    Log.d("MVC", "load all friends error: " + e + " " + e.getCause());
                } else {
                    friends = users;
                    listAdapter = new FriendListAdapter(getActivity(), friends, fab);

                    if (listAdapter.isEmpty()) {
                        tvEmptyList.setText("No Friends, Go Add Some!");
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        lvQueryResults.setAdapter(listAdapter);
                        selected = new Boolean[friends.size()];
                        Arrays.fill(selected, false);
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
