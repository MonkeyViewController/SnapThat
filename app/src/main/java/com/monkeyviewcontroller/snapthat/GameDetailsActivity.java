package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.monkeyviewcontroller.snapthat.Adapters.BasicCommentListAdapter;
import com.monkeyviewcontroller.snapthat.Adapters.FullCommentListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Comment;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.Like;
import com.monkeyviewcontroller.snapthat.Views.ObservableScrollView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class GameDetailsActivity extends Activity {

    private Game game;
    private String gameId;
    private ImageView ivHeader;
    private TextView tvSubmissions;
    private ImageView ivLike;
    private TextView tvWinner;
    private TextView tvItem;
    private TextView tvTime;
    private TextView tvNumSubmissions;
    private ObservableScrollView svGameDetails;
    private FloatingActionButton fabExpand;
    private LinearLayout llGameDetails;
    private ImageView ivComment;
    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private BasicCommentListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private TextView tvDivider;
    private TextView tvCommentStatus;
    private Like like;
    private boolean isLiked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_game_details);

        setupViews();
        getGameId();
        loadGame();
        loadComments();
        loadLikeStatus();
    }

    public void setupViews()
    {
        Log.d("MVC", "setupViews");
        tvSubmissions = (TextView) findViewById(R.id.tvSubmissions);
        ivLike = (ImageView) findViewById(R.id.ivLike);
        tvWinner = (TextView) findViewById(R.id.tvWinner);
        tvItem = (TextView) findViewById(R.id.tvItem);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvNumSubmissions = (TextView) findViewById(R.id.tvNumSubmissions);

        Log.d("MVC", "setupViews2");
        ivHeader = (ImageView) findViewById(R.id.ivHeader);
        svGameDetails = (ObservableScrollView) findViewById(R.id.svGameDetails);
        fabExpand = (FloatingActionButton) findViewById(R.id.fabExpand);
        llGameDetails = (LinearLayout) findViewById(R.id.llGameDetails);
        ivComment = (ImageView) findViewById(R.id.ivComment);
        tvCommentStatus = (TextView) findViewById(R.id.tvCommentStatus);
        tvDivider = (TextView) findViewById(R.id.tvDivider);
        Log.d("MVC", "setupViews3");

        llProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout) findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView) findViewById(R.id.lvQueryResults);
        tvEmptyList = (TextView)  findViewById(R.id.tvEmptyList);

        svGameDetails.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y) {
                fabExpand.setTranslationY(-y);
            }
        });

        ivComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked the comment button");
                Intent intent = new Intent(GameDetailsActivity.this, CommentsActivity.class);
                intent.putExtra("pastGameId", gameId);
                startActivity(intent);
            }
        });

        tvSubmissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked submissions button");
            }
        });

        ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked like button");

                if(isLiked) {
                    like.deleteInBackground();
                    ivLike.setImageResource(R.drawable.ic_favorite_outline_white_24dp);
                    isLiked = false;
                } else {
                    like = new Like();
                    like.forGame(gameId);
                    like.fromUser(ParseUser.getCurrentUser());

                    like.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            try {
                                like.fetchIfNeeded();
                            } catch (ParseException e1) {
                                Log.d("MVC", "FetchIfNeeded on like failed.");
                                e1.printStackTrace();
                            }

                            ivLike.setImageResource(R.drawable.ic_favorite_white_24dp);
                            isLiked = true;
                        }
                    });
                }
            }
        });

        Log.d("MVC", "Finished setting up view.");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadComments();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView, BasicCommentListAdapter listAdapter) {

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public void loadComments()
    {
        if(TextUtils.isEmpty(gameId))
        {
            Log.d("MVC", "GameId not properly received.");
            //Tell the user that there are no comments(no ID so we cant get comments)
            tvEmptyList.setText("Could not retrieve the comments. Navigate to the prior page and try again.");
            llEmptyList.setVisibility(View.VISIBLE);
            return;
        }

        Log.d("MVC", "Loading the comments.");
        ParseQuery<Comment> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo("forGame", ParseObject.createWithoutData("Game", gameId));
        query.orderByDescending("createdAt");
        query.setLimit(5);
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                hideProgressDialog();
                if (e == null) {
                    Log.d("MVC", "Retrieved " + comments.size() + " comments");
                    Collections.reverse(comments);
                    listAdapter = new BasicCommentListAdapter(GameDetailsActivity.this, comments);
                    SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(listAdapter);

                    if (listAdapter.isEmpty()) {
                        tvEmptyList.setText("no comments");
                        llEmptyList.setVisibility(View.VISIBLE);
                        tvDivider.setVisibility(View.GONE);
                    } else {
                        Log.d("MVC", tvCommentStatus + " " + tvDivider);
                        //TODO: check if comments exceed the 5 we get by default, if so set tvCommentStatus to 'view all # comments'
                        //
                        //While this feature isn't created yet
                        tvCommentStatus.setVisibility(View.GONE);
                        //
                        llEmptyList.setVisibility(View.GONE);
                        tvDivider.setVisibility(View.VISIBLE);
                        animationAdapter.setAbsListView(lvQueryResults);
                        lvQueryResults.setAdapter(animationAdapter);
                        setListViewHeightBasedOnChildren(lvQueryResults, listAdapter);

                        svGameDetails.post(new Runnable() {
                            @Override
                            public void run() {
                                svGameDetails.scrollTo(0, 0);
                            }
                        });

                    }
                } else {
                    Log.d("MVC", "Error: " + e.getMessage());
                }
            }
        });
    }

    //TODO: potentially need to lock this so that a user can not attempt to like before we know the status
    public void loadLikeStatus()
    {
        if(TextUtils.isEmpty(gameId))
        {
            Log.d("MVC", "GameId not properly received.");
            //Tell the user that there are no comments(no ID so we cant get comments)
            //In reality the user should even be in this screen if the gameId is not valid
            return;
        }

        Log.d("MVC", "Loading the like status.");
        ParseQuery<Like> query = ParseQuery.getQuery("Like");
        query.whereEqualTo("forGame", ParseObject.createWithoutData("Game", gameId));
        query.whereEqualTo("fromUser", ParseUser.getCurrentUser());

        query.getFirstInBackground(new GetCallback<Like>() {
            public void done(Like object, ParseException e) {
                like = object;
                if (like == null) {
                    Log.d("MVC", "The user has NOT liked the game.");
                    isLiked = false;
                } else {
                    Log.d("MVC", "The user HAS liked the game.");
                    isLiked = true;
                    ivLike.setImageResource(R.drawable.ic_favorite_white_24dp);
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

    public void getGameId() {
        Intent intent = this.getIntent();

        if(!intent.hasExtra("pastGameId")) {
            Log.d("MVC", "Loading game has a INVALID ID");
            gameId = "";
            //TODO: Show popup that alerts them of an issue and then call finish to end activity.
        }

        gameId = intent.getStringExtra("pastGameId");
    }

    public void loadGame()
    {
        Log.d("MVC", "Loading the game.");

        ParseQuery<Game> query = ParseQuery.getQuery("Game");
        query.getInBackground(gameId, new GetCallback<Game>() {
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
        long millis = game.getWinningSubmission().getCreatedDate().getTime() - game.getCreatedDate().getTime();
        int hours = (int) millis/(1000 * 60 * 60);
        int mins = (int) (millis/(1000*60)) % 60;

        String timeText = "";

        if(mins == 1) {
            timeText = "1 min";
        } else if( mins > 1) {
            timeText += mins + " min";
        }

        if (hours == 1) {
            timeText = "1 hr " + timeText;
        } else if(hours > 1 ) {
            timeText = hours + " hrs " + timeText;
        }

        tvTime.setText(timeText);

        tvNumSubmissions.setText(String.valueOf(game.getSubmissions().length()));

        //"https://cms-assets.tutsplus.com/uploads/users/21/posts/19431/featured_image/CodeFeature.jpg"
        Picasso.with(this).
                load(game.getWinningSubmission().getPhotoURL())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(ivHeader);
    }
}
