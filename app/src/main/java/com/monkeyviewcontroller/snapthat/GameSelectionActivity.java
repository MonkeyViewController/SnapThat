package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Adapters.FriendListAdapter;
import com.monkeyviewcontroller.snapthat.Adapters.GameSelectionListAdapter;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GameSelectionActivity extends Activity {

    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private ListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private Boolean[] selected;
    private List<String> terms;
    private ImageButton gameSelectionConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_selection);

        llProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout) findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView )findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView) findViewById(R.id.tvEmptyList);

        gameSelectionConfirmButton = (ImageButton) findViewById(R.id.gameSelectionConfirmButton);
        gameSelectionConfirmButton.setVisibility(View.GONE);
        gameSelectionConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked the game selection confirm button");

                for(int i=0; i < selected.length; i++)
                {
                    if(selected[i])
                    {
                        Log.d("MVC", "Selected index: " + i + " is: " + terms.get(i));
                    }
                }
                //TODO: send image to parse, objectID, gameList
            }
        });

        loadAllGameTerms();

        lvQueryResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {
                Log.d("MVC", "Checked item at position " + position);

                CheckedTextView c = (CheckedTextView) view.findViewById(android.R.id.text1);
                selected[position] = c.isChecked();

                gameSelectionConfirmButton.setVisibility(View.GONE);
                for(boolean b: selected)
                {
                    if(b)
                    {
                        gameSelectionConfirmButton.setVisibility(View.VISIBLE);
                    }
                }
            }});
    }

    public void loadAllGameTerms()
    {
        showProgressDialog();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("getcurrentgamesterms", params, new FunctionCallback<List<String>>() {
            @Override
            public void done(List<String> gameterms, com.parse.ParseException e) {
                hideProgressDialog();

                if (e != null) {
                    Log.d("MVC", "load all game terms error: " + e + " " + e.getCause());
                } else {
                    terms = gameterms;
                    listAdapter = new GameSelectionListAdapter(GameSelectionActivity.this, terms);

                    if (listAdapter.isEmpty()) {
                        tvEmptyList.setText("No Active Games, Go Create One!");
                        llEmptyList.setVisibility(View.VISIBLE);
                        //TODO: button to return to friends fragment so they can create a game
                    } else {
                        lvQueryResults.setAdapter(listAdapter);
                        selected = new Boolean[terms.size()];
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

}
