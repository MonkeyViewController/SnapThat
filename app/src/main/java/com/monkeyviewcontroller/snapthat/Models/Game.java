package com.monkeyviewcontroller.snapthat.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by isaacsiegel on 4/15/15.
 */
@ParseClassName("Game")
public class Game extends ParseObject {


    public Game() {

    }

//    public String getObjectId() {
//        return getObjectId();
//    }

    public String getSearchItem() {
        return getString("searchItem");
    }

    public void setSearchItem(String itemName) {
        put("searchItem", itemName);
    }

    public boolean getGameFinished() {
        return getBoolean("gameFinished");
    }

    public void setGameFinished(boolean isFinished) {
        put("gameFinished", isFinished);
    }

//    public JSONArray getParticipants(){
//        return getJSONArray("participantIDs");
//    }
    public String getCreatorUsername() { return getParseUser("creator").getUsername(); }

    public Date getCreatedDate() { return getCreatedAt(); }

    public void setCreator(ParseUser value) {
        put("creator", value);
    }

    public static ParseQuery<Game> getQuery() {
        return ParseQuery.getQuery(Game.class);
    }

    public  void setWinner(ParseUser winner) { put("winner", winner);}

    public void setParticipants(ArrayList<STUser> participants) { put("participants", participants);}
}