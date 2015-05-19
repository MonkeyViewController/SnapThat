package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.monkeyviewcontroller.snapthat.Adapters.FullCommentListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Comment;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Collections;
import java.util.List;

public class CommentsActivity extends Activity {

    private String gameId;
    //private TextView tvWinner;
    //private TextView tvItem;
    private EditText etComment;
    private ImageView ivAddComment;
    private LinearLayout llProgressBar;
    private LinearLayout llEmptyList;
    private FullCommentListAdapter listAdapter;
    private TextView tvEmptyList;
    private ListView lvQueryResults;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_comments);

        setupViews();
        getGameId();
        loadComments();
    }

    public void setupViews()
    {
        //tvWinner = (TextView) findViewById(R.id.tvWinner);
        //tvItem = (TextView) findViewById(R.id.tvItem);
        etComment = (EditText) findViewById(R.id.etComment);
        ivAddComment = (ImageView) findViewById(R.id.ivAddComment);

        llProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        llEmptyList = (LinearLayout) findViewById(R.id.llEmptyList);
        lvQueryResults = (ListView) findViewById(R.id.lvQueryResults);
        int temp = (int) (48 * getResources().getDisplayMetrics().density);
        lvQueryResults.setPadding(lvQueryResults.getPaddingLeft(),lvQueryResults.getPaddingTop(), lvQueryResults.getPaddingRight(),temp);
        tvEmptyList = (TextView)  findViewById(R.id.tvEmptyList);

        ivAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MVC", "Clicked the add comment button " + etComment.getText());
                attemptAddComment();
            }
        });
    }

    public void attemptAddComment()
    {
        Log.d("MVC", "Attempting to Log In");
        etComment.setError(null);

        boolean cancel = false;
        View focusView = null;

        String message = etComment.getText().toString();

        if(TextUtils.isEmpty(message))
        {
            etComment.setError(getString(R.string.error_field_required));
            focusView = etComment;
            cancel = true;
        }

        if(message.length() > 250)
        {
            etComment.setError(getString(R.string.error_field_comment_length));
            focusView = etComment;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            Log.d("MVC", "Proceed to add comment via parse.");

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Please wait.");
            pd.setMessage("Adding comment, please wait.");
            pd.show();

            final Comment c = new Comment();
            c.setGame(gameId);
            c.setCommenter(ParseUser.getCurrentUser());
            c.setMessage(message);
            c.setDeleted(false);
            c.setReports(0);

            c.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    pd.dismiss();
                    etComment.setText("");

                    try {
                        c.fetchIfNeeded();
                    } catch (ParseException e1) {
                        Log.d("MVC", "FetchIfNeeded on comment failed.");
                        e1.printStackTrace();
                    }
                    if(listAdapter.getCount()==0)
                    {
                        llEmptyList.setVisibility(View.GONE);
                        lvQueryResults.setAdapter(listAdapter);
                    }

                    listAdapter.add(c);
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
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
        query.setLimit(15);
        query.findInBackground(new FindCallback<Comment>() {
            public void done(List<Comment> comments, ParseException e) {
                hideProgressDialog();
                if (e == null) {
                    Log.d("MVC", "Retrieved " + comments.size() + " comments");
                    Collections.reverse(comments);
                    listAdapter = new FullCommentListAdapter(CommentsActivity.this, comments);

                    if (listAdapter.isEmpty()) {
                        tvEmptyList.setText("No comments, be the first to add one!");
                        llEmptyList.setVisibility(View.VISIBLE);
                    } else {
                        lvQueryResults.setAdapter(listAdapter);
                        lvQueryResults.post(new Runnable() {
                            @Override
                            public void run() {
                                lvQueryResults.setSelection(lvQueryResults.getAdapter().getCount() - 1);
                            }
                        });
                    }
                } else {
                    Log.d("MVC", "Error: " + e.getMessage());
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
        }

        gameId = intent.getStringExtra("pastGameId");
    }
}
