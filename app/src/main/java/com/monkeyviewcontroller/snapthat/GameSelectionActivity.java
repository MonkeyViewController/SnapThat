package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
import com.monkeyviewcontroller.snapthat.Models.Submission;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                retrievePhotoBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] pictureInBytes = stream.toByteArray();
                ParseFile pictureFile = new ParseFile(pictureInBytes);



                Submission newSubmission = new Submission();

                newSubmission.setPicture(pictureFile);
                newSubmission.setCreator(ParseUser.getCurrentUser());
                newSubmission.setHasProcessed(false);
                newSubmission.setIsValid(false);

                newSubmission.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.i("MVC", "Submission has been saved successfully");
                        //TODO:Return to main view
                        //TODO:Show progress indicator, photo upload takes time
                        //TODO:Refactor above code
                        //TODO: Update Game Object to reflect submission
                        finish();
                    }
                });

                finish();


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

    private Bitmap retrievePhotoBitmap() {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        byte[] bytes = null;
        try {
            Log.d("MVC", "Lets retrieve the preview");
            File inputDir = new File(getCacheDir(), "photopreview.tmp");
            int size = (int) inputDir.length();
            Log.d("MVC", "File Size: " + size);
            Log.d("MVC", "File Path: " + inputDir.getAbsolutePath());
            bytes = new byte[size];
            new FileInputStream(inputDir).read(bytes);
            //THIS ISN'T GUARANTEED TO READ THE WHOLE FILE
            Log.d("MVC", "Byte array retrieved from cache file");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

//        if(bytes==null)
//            return new Bitmap();

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // Use matrix to rotate bitmap image
        // to compensate for 90 manual rotation in preview workaround
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return  rotatedBitmap;

    }

}
