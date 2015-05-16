package com.monkeyviewcontroller.snapthat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MyFriendsFragment extends Fragment {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private FriendListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private FloatingActionButton fab;
    private Boolean[] selected;
    private List<STUser> friends;
    private int category;
    private String[] indoorTerms = {"Mouse","Fan", "Cup", "Knife", "Plate", "Table", "Chair", "Sink", "Banana", "Apple", "Peanut Butter"};
    private String[] outdoorTerms = {"Dog", "Cat", "Tree", "Sign", "Field Goal", "Tennis Ball"};

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

                int selectedCount = 0;
                Log.d("MVC", "Clicked the FAB button");

                displayGameConfirmationPopup(getSelectedFriends());
            }
        });

        return rootView;
    }

    public ArrayList<STUser> getSelectedFriends()
    {
        ArrayList<STUser> temp = new ArrayList<>();
        for(int i=0; i < selected.length; i++)
        {
            if(selected[i])
            {
                temp.add(friends.get(i));
            }
        }
        return temp;
    }

    public ArrayList<String> getParticipantIds()
    {
        ArrayList<String> temp = new ArrayList<>();
        for(STUser st: getSelectedFriends())
        {
            temp.add(st.getObjectId());
        }
        temp.add(ParseUser.getCurrentUser().getObjectId());
        return temp;
    }

    public void displayGameConfirmationPopup(ArrayList<STUser> selectedFriends)
    {
        Log.d("MVC", "Displaying Popup");

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title("Almost There")
                .customView(R.layout.dialog_creategame, true)
                .positiveText("Let's Play!")
                .negativeText("Cancel")
                .positiveColorRes(R.color.ics_blue)
                .negativeColorRes(R.color.ics_blue)
                .widgetColorRes(R.color.ics_blue)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        Log.d("MVC", "Clicked play from dialog. Category: " + category);
                        registerNewGameWithParse(getParticipantIds(), category);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        ((RadioButton)dialog.findViewById(R.id.radio_indoors)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked)
                    category = 0;
                else
                    category = 1;
            }
        });

        if(selectedFriends.size()==1)
            ((TextView)dialog.findViewById(R.id.tvFriendCount)).setText("1 friend :D");
        else
            ((TextView)dialog.findViewById(R.id.tvFriendCount)).setText(selectedFriends.size() + " friends :D");

        dialog.show();
    }

    private String getRandomSearchItem(int category)
    {
        Random r = new Random();
        if(category==1)
        {
            return indoorTerms[r.nextInt(indoorTerms.length)];
        }
        else
        {
            return outdoorTerms[r.nextInt(outdoorTerms.length)];
        }
    }

    private void registerNewGameWithParse(ArrayList<String> participantIds, int category){

        HashMap<String, Object> params = new HashMap<String, Object>();
        final String searchItem = getRandomSearchItem(category);
        params.put("creator", ParseUser.getCurrentUser().getObjectId());
        params.put("searchItem", searchItem);
        params.put("participants",participantIds);
        params.put("submissions", new JSONArray());
        params.put("creatorUsername", ParseUser.getCurrentUser().getUsername());

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setTitle("Please wait.");
        pd.setMessage("The game is being created.");
        pd.show();

        ParseCloud.callFunctionInBackground("creategame", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, com.parse.ParseException e) {
                pd.dismiss();
                if (e == null) {
                    Log.d("MVC", "success in creating game");
                    Log.d("MVC", "Game OID: " + result);

                    Toast.makeText(getActivity(), "The game has started! Snap a " + searchItem + ".", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("MVC", "failed to create game");
                    Log.d("MVC", e.getMessage() + " " + e.getCause());
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
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
                    Collections.sort(friends);
                    listAdapter = new FriendListAdapter(getActivity(), friends, fab);
                    SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(listAdapter);

                    if (listAdapter.isEmpty()) {
                        tvEmptyList.setText("No Friends, Go Add Some!");
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        animationAdapter.setAbsListView(lvQueryResults);
                        lvQueryResults.setAdapter(animationAdapter);
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
