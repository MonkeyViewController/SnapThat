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
import android.widget.EditText;
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
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddFriendsFragment extends Fragment {

    private String currentUser;
    private String currentObjectId;

    private Button btnAddFriendInvite;
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
        btnAddFriendInvite = (Button)rootView.findViewById(R.id.btnAddFriendsInvite);
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

    public void loadAllRequests()
    {
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("objectId",currentObjectId);
        ParseCloud.callFunctionInBackground("getfriendrequests", params, new FunctionCallback<List<STUser>>() {
            @Override
            public void done(List<STUser> usersRequesting, com.parse.ParseException e) {
                hideProgressDialog();

                listAdapter = new FriendRequestListAdapter(getActivity(), usersRequesting, currentObjectId);

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

        if(invitee.equalsIgnoreCase(currentUser))
        {
            etAddFriendInvitee.setError(getString(R.string.error_adding_yourself));
            focusView = etAddFriendInvitee;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            Log.d("MVC", "Proceed to invite via parse, you are user:" + currentUser);

            final ProgressDialog pd = new ProgressDialog(this.getActivity());
            pd.setTitle("Please wait.");
            pd.setMessage("Locating your bro, please wait.");
            pd.show();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("objectId", currentObjectId);
            params.put("invitee", invitee);

            ParseCloud.callFunctionInBackground("sendfriendrequest", params, new FunctionCallback<String>() {
                @Override
                public void done(String result, com.parse.ParseException e) {
                    pd.dismiss();
                    if (e == null) {
                        Log.d("MVC", result);
                        Toast.makeText(getActivity(), "An Invite has been sent", Toast.LENGTH_LONG).show();
                        etAddFriendInvitee.setText("");
                    }
                    else
                    {
                        Log.d("MVC", e.toString());
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
            }});
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = getActivity().getIntent().getStringExtra("username");
        currentObjectId = getActivity().getIntent().getStringExtra("objectId");
    }
}
