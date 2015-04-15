package com.monkeyviewcontroller.snapthat;

import android.app.ProgressDialog;
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

import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

public class MyFriendsFragment extends Fragment {

    private String currentUser;
    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private FriendListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;

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

        loadAllFriends();

        //TODO: only show button when 1 item is selected, fade when scrolling, fix when it covers bottom row
        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d("MVC", "checked item" + position);

                CheckBox c = (CheckBox) view.findViewById(R.id.cbMyFriends);

                if (c.isChecked()) {
                    c.setChecked(false);

                } else {
                    c.setChecked(true);

                }
            }
        });

        return rootView;
    }

    public void loadAllFriends()
    {
        showProgressDialog();

        final ParseQuery<FriendRequest> friendRequestsOne = ParseQuery.getQuery(FriendRequest.class);
        friendRequestsOne.whereEqualTo("friendTwo", currentUser);
        friendRequestsOne.whereEqualTo("status", 1);

        final ParseQuery<FriendRequest> friendRequestsTwo = ParseQuery.getQuery(FriendRequest.class);
        friendRequestsTwo.whereEqualTo("friendOne", currentUser);
        friendRequestsTwo.whereEqualTo("status", 1);

        final ParseQuery<FriendRequest> combined = ParseQuery.or(Arrays.asList(friendRequestsOne, friendRequestsTwo));

        combined.findInBackground(new FindCallback<FriendRequest>() {
            public void done(List<FriendRequest> friendRequest, ParseException exception) {
                hideProgressDialog();

                listAdapter = new FriendListAdapter(getActivity(), friendRequest);

                if (listAdapter.isEmpty()) {
                    tvEmptyList.setText("No Pending Requests");
                    llEmptyList.setVisibility(View.VISIBLE);
                } else {
                    lvQueryResults.setAdapter(listAdapter);
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
        currentUser = getActivity().getIntent().getStringExtra("username");
    }
}
