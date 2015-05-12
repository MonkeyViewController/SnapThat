package com.monkeyviewcontroller.snapthat;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Adapters.FriendRequestListAdapter;
import com.monkeyviewcontroller.snapthat.Models.FriendRequest;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AddFriendsFragment extends Fragment {

    private ImageButton btnAddFriendInvite;
    private EditText etAddFriendInvitee;
    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private FriendRequestListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;

    public static AddFriendsFragment newInstance() {
        AddFriendsFragment fragment = new AddFriendsFragment();
        return fragment;
    }

    public AddFriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the AddFriendView");
        final View rootView = inflater.inflate(R.layout.fragment_addfriends, container, false);

        llProgressBar = (LinearLayout)rootView.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout)rootView.findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView)rootView.findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)rootView.findViewById(R.id.tvEmptyList);
        etAddFriendInvitee = (EditText)rootView.findViewById(R.id.etAddFriendsInvitee);
        btnAddFriendInvite = (ImageButton)rootView.findViewById(R.id.btnAddFriendsInvite);
        setViewBackgroundWithoutResettingPadding(btnAddFriendInvite);
        btnAddFriendInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriend();
            }
        });

        loadAllRequests();

        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MVC" , " I clicked something in add friends adapter....");
            }
        });

        return rootView;
    }

    public static void setViewBackgroundWithoutResettingPadding(final View v) {
        final int paddingBottom = v.getPaddingBottom(), paddingLeft = v.getPaddingLeft();
        final int paddingRight = v.getPaddingRight(), paddingTop = v.getPaddingTop();
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    public void loadAllRequests()
    {
        Log.d("MVC", "Loading all requests");
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("getfriendrequests", params, new FunctionCallback<List<STUser>>() {
            @Override
            public void done(List<STUser> usersRequesting, com.parse.ParseException e) {
                hideProgressDialog();

                if(e!=null)
                {
                    Log.d("MVC", "load all requests error: " + e);
                } else {
                    Collections.sort(usersRequesting);
                    listAdapter = new FriendRequestListAdapter(getActivity(), usersRequesting);

                    if (listAdapter.isEmpty()) {
                        tvEmptyList.setText("No Pending Requests");
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

    public void inviteFriend()
    {
        Log.d("MVC", "Attempting to invite friend");
        etAddFriendInvitee.setError(null);

        boolean cancel = false;
        View focusView = null;

        String invitee = etAddFriendInvitee.getText().toString();

        if(TextUtils.isEmpty(invitee))
        {
            etAddFriendInvitee.setError(getString(R.string.error_field_required));
            focusView = etAddFriendInvitee;
            cancel = true;
        }

        String username = ParseUser.getCurrentUser().getUsername();
        if(invitee.equalsIgnoreCase(username))
        {
            etAddFriendInvitee.setError(getString(R.string.error_adding_yourself));
            focusView = etAddFriendInvitee;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            Log.d("MVC", "Proceed to invite via parse, you are user:" + username);

            final ProgressDialog pd = new ProgressDialog(this.getActivity());
            pd.setTitle("Please wait.");
            pd.setMessage("Locating your bro, please wait.");
            pd.show();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("user", ParseUser.getCurrentUser().getObjectId());
            params.put("invitee", invitee);
            params.put("inviter_username", ParseUser.getCurrentUser().getUsername());

            ParseCloud.callFunctionInBackground("sendfriendrequest", params, new FunctionCallback<String>() {
                @Override
                public void done(String result, com.parse.ParseException e) {
                    pd.dismiss();
                    if (e == null) {
                        Log.d("MVC", "success");
                        Log.d("MVC", result);
                        Toast.makeText(getActivity(), "An Invite has been sent", Toast.LENGTH_LONG).show();
                        etAddFriendInvitee.setText("");
                    }
                    else
                    {
                        Log.d("MVC", "fail");
                        Log.d("MVC", e.getMessage() + " " + e.getCause());
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            }});
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
