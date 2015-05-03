package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateGameActivity extends Activity {

    private Button btnBeginGame;
    private ArrayList<STUser.Model> participants;
    private ArrayList<String> participantIds;
    private ArrayList<String> participantNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        Log.d("MVC", "CreateGameActivity");

        btnBeginGame = (Button) findViewById(R.id.btnBeginGame);
        btnBeginGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked begin game button");
                registerNewGameWithParse("laptop", ParseUser.getCurrentUser(), participantIds);
            }
        });

        Bundle extras = getIntent().getExtras();
        participants = (ArrayList<STUser.Model>)extras.getSerializable("friendsingame");
        participantNames = new ArrayList<>();
        participantIds = new ArrayList<>();
        for(int i=0; i < participants.size(); i++)
        {
            Log.d("MVC", "Name: " + participants.get(i).getUsername());
            participantNames.add(participants.get(i).getUsername());
            participantIds.add(participants.get(i).getObjectId());
        }

        participantIds.add(ParseUser.getCurrentUser().getObjectId());

        ListView listView = (ListView) findViewById(R.id.invitedFriendsListView);
        ListAdapter listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, participantNames
                );

        listView.setAdapter(listAdapter);
    }

    private void registerNewGameWithParse(String searchTerm, ParseUser creator, ArrayList<String> participantIds){

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("creator", creator.getObjectId());
        params.put("searchItem", searchTerm);
        params.put("participants",participantIds);
        params.put("submissions", new JSONArray());

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Please wait.");
        pd.setMessage("GET READY TO RUMBLE");
        pd.show();

        ParseCloud.callFunctionInBackground("creategame", params, new FunctionCallback<String>() {
            @Override
            public void done(String result, com.parse.ParseException e) {
                pd.dismiss();
                if (e == null) {
                    Log.d("MVC", "success in creating game");
                    Log.d("MVC", result);
                    Toast.makeText(CreateGameActivity.this, "Creating the game.", Toast.LENGTH_LONG).show();

                    //TODO: navigate to the snap fragment so they can begin to play, clear create game fragment from the stack
                } else {
                    Log.d("MVC", "failed to create game");
                    Log.d("MVC", e.getMessage() + " " + e.getCause());
                    Toast.makeText(CreateGameActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
