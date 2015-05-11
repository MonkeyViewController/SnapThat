package com.monkeyviewcontroller.snapthat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.Submission;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GameSubmissionsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_submissions);

        Intent intent = getIntent();
        String gameOID = intent.getStringExtra("gameOID");
        Log.i("MVC", "Retrieved gameOID: " + gameOID);

        ParseQuery<Game> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameOID, new GetCallback<Game>() {
            public void done(Game game, ParseException e) {
                if (e == null) {
                    //Populate view with submissions
                    ParseFile winningImage;
                    Submission winningSubmission = game.getWinningSubmission();
                    JSONArray allSubmissions = game.getSubmissions();

                    //Extract images
                    if(winningSubmission == null){
                        Log.i("MVC", "Cannot populate winningSubmission, no winner yet.");
                    }
                    else{
                        winningImage = winningSubmission.getPicture();
                    }

                    List<ParseFile> allImages = new ArrayList<>();
                    for(int i = 0; i < allSubmissions.length(); i++){
                        //Get images/usernames out of allSubmissions JSONArray

                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_submissions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
