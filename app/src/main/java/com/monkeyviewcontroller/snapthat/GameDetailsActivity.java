package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.monkeyviewcontroller.snapthat.Models.Game;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

public class GameDetailsActivity extends Activity {

    private Game game;
    private ImageView ivHeader;
    private TextView tvSubmissions;
    private ImageView ivFavorite;
    private TextView tvWinner;
    private TextView tvItem;
    private TextView tvTime;
    private TextView tvNumSubmissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_game_details);

        ivHeader = (ImageView) findViewById(R.id.ivHeader);
        tvSubmissions = (TextView) findViewById(R.id.tvSubmissions);
        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        tvWinner = (TextView) findViewById(R.id.tvWinner);
        tvItem = (TextView) findViewById(R.id.tvItem);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvNumSubmissions = (TextView) findViewById(R.id.tvNumSubmissions);

        loadGame();
    }

    public void loadGame()
    {
        Intent intent = this.getIntent();

        if(!intent.hasExtra("pastGameId")) {
            Log.d("MVC", "Loading game has a INVALID ID");
            return;
        }

        ParseQuery<Game> query = ParseQuery.getQuery("Game");
        query.getInBackground(intent.getStringExtra("pastGameId"), new GetCallback<Game>() {
            public void done(Game object, ParseException e) {
                if (e == null) {
                    game = object;
                    loadGameDetails();
                } else {
                    Log.d("MVC", "Something went WRONG when loading the game");
                }
            }
        });
    }

    public void loadGameDetails()
    {
        tvWinner.setText(game.getWinningSubmission().getCreatorUsername());
        tvItem.setText(game.getSearchItem());
        tvTime.setText("3 hr 22 s");
        tvNumSubmissions.setText(String.valueOf(game.getSubmissions().length()));

        Picasso.with(this).
                load("https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg")//winningGame.getWinningSubmission().getPhotoURL()
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(ivHeader);
    }
}
