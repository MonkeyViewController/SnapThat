package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.monkeyviewcontroller.snapthat.Models.Game;
import com.monkeyviewcontroller.snapthat.Models.STUser;
import com.monkeyviewcontroller.snapthat.Models.Submission;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class PhotoPreviewActivity extends Activity {

    private ImageView photoImageView;
    private ImageButton cancelSubmissionButton;
    private ImageButton previewConfirmButton;
    private String gameOID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        Intent intent = getIntent();
        gameOID = intent.getStringExtra("selectedGameOID");
        Log.i("DEBUG","selectedGameOID is "+ gameOID);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        setPhotoToView();

        previewConfirmButton = (ImageButton) findViewById(R.id.previewConfirmButton);
        previewConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MVC","Confirm Clicked");

                saveToParse();

                Intent intent = new Intent(PhotoPreviewActivity.this, GameSelectionActivity.class);
                startActivity(intent);
            }
        });

        cancelSubmissionButton = (ImageButton) findViewById(R.id.cancelSubmissionButton);
        cancelSubmissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MVC", "Cancel Clicked");
                //Close PhotoPreviewActivity and return to primary view
                finish();
            }
        });
    }

    private void setPhotoToView() {
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

        if(bytes==null)
            return;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        // Use matrix to rotate bitmap image
        // to compensate for 90 manual rotation in preview workaround
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        //TEMP: Set ImageView ontop of preview to display image
        photoImageView.setImageBitmap(rotatedBitmap);

        //String encodedImage = Base64.encodeToString(data, Base64.DEFAULT);
        //Log.i("img",encodedImage);

        //TODO: Transmit photo data to server/parse
        //TODO: display UI on top to cancel or go to next screen(choose which game and send)?
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

    private void saveToParse(){

        //TODO: modify setPhotoToView to save a instance variable containing bitmap to avoid duplicate file I/O
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        //Retrieve photo from file
        retrievePhotoBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] pictureInBytes = stream.toByteArray();
        ParseFile pictureFile = new ParseFile(pictureInBytes);


        final Submission newSubmission = new Submission();

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

                //associate submission with game

                ParseQuery<Game> query = ParseQuery.getQuery("Game");

                // Retrieve the object by id
                query.getInBackground(gameOID, new GetCallback<Game>() {
                    public void done(Game game, ParseException e) {
                        if (e == null) {
                            game.addToSubmissions(newSubmission.getObjectId());
                            game.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.i("DEBUG", "YES");
                                }
                            });
                        }
                    }
                });


                finish();
            }
        });
    }
}
