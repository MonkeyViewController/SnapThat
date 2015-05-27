package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.monkeyviewcontroller.snapthat.Adapters.FullCommentListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Comment;
import com.monkeyviewcontroller.snapthat.com.baoyz.swipemenulistview.SwipeMenu;
import com.monkeyviewcontroller.snapthat.com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.monkeyviewcontroller.snapthat.com.baoyz.swipemenulistview.SwipeMenuItem;
import com.monkeyviewcontroller.snapthat.com.baoyz.swipemenulistview.SwipeMenuListView;
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
    private SwipeMenuListView lvQueryResults;
    private List<Comment> comments;
    private int category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        setContentView(R.layout.activity_comments);

        setupViews();
        createSwipeListView();
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
        lvQueryResults = (SwipeMenuListView) findViewById(R.id.lvQueryResults);
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

    public void createSwipeListView()
    {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu1(menu);
                        break;
                    case 1:
                        createMenu2(menu);
                        break;
                }
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                item1.setWidth(dp2px(90));
                item1.setIcon(R.drawable.ic_reply_white_24dp);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.ic_delete_white_24dp);
                menu.addMenuItem(item2);
            }

            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(
                        getApplicationContext());
                item1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                item1.setWidth(dp2px(90));
                item1.setIcon(R.drawable.ic_reply_white_24dp);
                menu.addMenuItem(item1);
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.ic_report_white_24dp);
                menu.addMenuItem(item2);
            }
        };
        // set creator
        lvQueryResults.setMenuCreator(creator);

        // step 2. listener item click event
        lvQueryResults.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                //we never set the comments variable in loadComments so we can't do comments.get(position)
                final Comment c = listAdapter.getItem(position);

                switch (index) {
                    case 0:
                        // reply
                        Log.d("MVC", "replying from swipe menu.");
                        replyTo(c.getCommenter().getUsername());
                        break;
                    case 1:
                        // delete

                        //if the comment is from us, simply delete
                        if(weAreCommenter(c.getCommenter().getUsername()))
                        {
                            //TODO: progress dialog? remove from adapter then save instead of waiting for save to finish?
                            Log.d("MVC", "deleting our OWN comment from swipe menu.");
                            c.put("deleted",true);
                            c.saveInBackground( new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if( e == null) {
                                        listAdapter.remove(c);
                                        listAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d("MVC", "removing a comment failed " + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            //Report
                            MaterialDialog dialog = new MaterialDialog.Builder(CommentsActivity.this)
                                    .title("Why are you reporting this comment?")
                                    .customView(R.layout.dialog_report, true)
                                    .positiveText("Report")
                                    .negativeText("Cancel")
                                    .positiveColorRes(R.color.ics_blue)
                                    .negativeColorRes(R.color.ics_blue)
                                    .widgetColorRes(R.color.ics_blue)
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            Log.d("MVC", "Clicked report from dialog. Category: " + category);

                                            //TODO: actually implement reporting
                                        }

                                        @Override
                                        public void onNegative(MaterialDialog dialog) {
                                        }
                                    }).build();

                            category = 0; //default category selection

                            ((RadioButton)dialog.findViewById(R.id.radio_spamorscam)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                                    if(isChecked)
                                        category = 0;
                                    else
                                        category = 1;
                                }
                            });

                            dialog.show();
                        }
                        break;
                }
                return false;
            }
        });
    }

    private boolean weAreCommenter(String username)
    {
        return ParseUser.getCurrentUser().getUsername().equals(username);
    }

    private void replyTo(String user)
    {
        String current = etComment.getText().toString();
        int index = TextUtils.indexOf(current, "@" + user);
        if( index == -1) {
            if(TextUtils.isEmpty(current)) {
                etComment.setText("@" + user + " ");
            } else {
                etComment.setText(current + " @" + user + " ");
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void attemptAddComment()
    {
        Log.d("MVC", "Attempting to Add Comment");
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
        query.whereEqualTo("deleted", false);
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
