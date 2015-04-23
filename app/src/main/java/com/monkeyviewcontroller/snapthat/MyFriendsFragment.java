package com.monkeyviewcontroller.snapthat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

        //TODO: only show button when 1 item is selected, fade when scrolling, fix when it covers bottom row
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
                ArrayList<STUser> selectedFriends = new ArrayList<STUser>();
                Log.d("MVC", "Clicked the FAB button");

                for(int i=0; i < selected.length; i++)
                {
                    if(selected[i])
                    {
                        Log.d("MVC", "Selected : " + friends.get(i).getUsername() + " " + friends.get(i).getObjectId());
                        selectedFriends.add(friends.get(i));
                    }
                }

                //TODO: Get list of participating users to add to the new game.
                //TODO: change/add a new field for list of participants
                // Still deciding on Pointers to ParseUsers, or just dealing with OID strings
                //Note:changing use of parse schema may require you to delete old tables in your account
                Intent intent = new Intent(getActivity(), CreateGameActivity.class);

                //TODO: Test serializable bundling when friend requests work again
                Bundle bundle = new Bundle();
                bundle.putSerializable("invitedUsersBundle", selectedFriends);
                intent.putExtras(bundle);
                startActivity(intent);

                //registerNewGameWithParse("monkey", ParseUser.getCurrentUser(), false);
            }
        });

        return rootView;
    }


    private void registerNewGameWithParse(String searchTerm, ParseUser creator, boolean isGameFinished){
        // 1
        Game game = new Game();
        game.setCreator(creator);
        game.setSearchItem(searchTerm);
        game.setGameFinished(isGameFinished);

        // 2
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(false);
        game.setACL(acl);

        // 3
        game.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //finish();
            }
        });
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
                    listAdapter = new FriendListAdapter(getActivity(), friends);

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
