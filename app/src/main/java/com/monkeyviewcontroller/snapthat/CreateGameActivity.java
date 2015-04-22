package com.monkeyviewcontroller.snapthat;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.monkeyviewcontroller.snapthat.Models.STUser;

import java.util.ArrayList;


public class CreateGameActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        //TODO: Pass selected friends from previous activity, UNTESTED

        Intent intent= this.getIntent();
        Bundle bundle = intent.getExtras();

        ArrayList<STUser> passedFriends=
                (ArrayList<STUser>)bundle.getSerializable("invitedUsersBundle");

        String[] invitedFriendsUsernames = new String[passedFriends.size()];


        for(int i = 0; i < passedFriends.size(); i++){
            invitedFriendsUsernames[0] = passedFriends.get(i).getUsername();
        }

        //String[] passedFriends = new String[]{"Friend1","Friend2","Friend3"};

        ListView listView = (ListView) findViewById(R.id.invitedFriendsListView);
        ListAdapter listAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, invitedFriendsUsernames
                );

        listView.setAdapter(listAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_game, menu);
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
