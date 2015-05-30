package com.monkeyviewcontroller.snapthat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
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
    private String gameOID;
    private byte[] photoData;
    private Bitmap bitmap;
    private Bitmap rotatedBitmap;
    private Bitmap resizedBitmap;
    private CircularProgressButton circularButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        Intent intent = getIntent();
        gameOID = intent.getStringExtra("selectedGameOID");
        Log.i("DEBUG","selectedGameOID is "+ gameOID);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        photoData = getPhotoData();
        setPhotoToView();

        circularButton1 = (CircularProgressButton) findViewById(R.id.circularButton1);
        circularButton1.setIndeterminateProgressMode(true);
        circularButton1.setStrokeColor(000000);
        circularButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circularButton1.getProgress() == 0) {
                    circularButton1.setProgress(50);
                    Log.d("MVC","Upload Clicked");
                    saveSubmission(); // calls Finish()
                } else if (circularButton1.getProgress() == 100) {
                    circularButton1.setProgress(0);
                } else {
                    circularButton1.setProgress(100);
                }
            }
        });
    }

    private byte[] getPhotoData()
    {
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

        return bytes;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int desiredWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scale = ((float) desiredWidth) / width;

        Matrix matrix = new Matrix();

        matrix.postScale(scale, scale);

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private void setPhotoToView() {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        if(photoData==null)
        {
            Log.d("MVC", "Byte array is null when attempting to set photo preview.");
            Toast.makeText(PhotoPreviewActivity.this, "SnapThat was not able to create a photo preview. Take another Snap and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        bitmap = BitmapFactory.decodeByteArray(photoData, 0, photoData.length);

        // Use matrix to rotate bitmap image
        // to compensate for 90 manual rotation in preview workaround
        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        Log.d("MVC", "Width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        photoImageView.setImageBitmap(rotatedBitmap);
        bitmap.recycle();
    }

    private void resizeBitmap()
    {
        resizedBitmap = getResizedBitmap(rotatedBitmap, 960);
        rotatedBitmap.recycle();
    }

    private ParseFile getParsePictureFile(Bitmap bitmap){
        if(photoData==null)
        {
            Log.d("MVC", "Byte array is null when attempting to save file to parse. Exiting");
            Toast.makeText(PhotoPreviewActivity.this, "SnapThat was not able to submit your photo. Take another Snap and try again.", Toast.LENGTH_LONG).show();
            finish();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] scaledData = bos.toByteArray();
        ParseFile file = new ParseFile("submission_photo.jpg",scaledData);
        return file;
    }

    private ParseFile getParseThumbnailFile(Bitmap bitmap){
        if(photoData==null)
        {
            Log.d("MVC", "Byte array is null when attempting to save file to parse. Exiting");
            Toast.makeText(PhotoPreviewActivity.this, "SnapThat was not able to submit your photo. Take another Snap and try again.", Toast.LENGTH_LONG).show();
            finish();
        }
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, bitmap.getWidth(), 450);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] scaledData = bos.toByteArray();
        ParseFile file = new ParseFile("thumbnail_photo.jpg",scaledData);
        return file;
    }

    private void saveSubmission(){

        resizeBitmap();
        ParseFile pictureFile = getParsePictureFile(resizedBitmap);
        ParseFile thumbnailFile = getParseThumbnailFile(resizedBitmap);
        resizedBitmap.recycle();

        final Submission newSubmission = new Submission();

        newSubmission.setPicture(pictureFile);
        newSubmission.setThumbnail(thumbnailFile);
        newSubmission.setCreator(ParseUser.getCurrentUser());
        newSubmission.setHasProcessed(false);
        newSubmission.setIsValid(false);
        newSubmission.put("forGame", ParseObject.createWithoutData("Game", gameOID));

        newSubmission.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                //TODO:Show progress indicator, photo upload takes time

                //associate submission with game
                associateGameToSubmission(newSubmission);
            }
        });
    }

    private void associateGameToSubmission(final Submission newSubmission){
        ParseQuery<Game> query = ParseQuery.getQuery("Game");

        query.getInBackground(gameOID, new GetCallback<Game>() {
            public void done(Game game, ParseException e) {
                if (e == null) {
                    game.addToSubmissions(newSubmission);
                    game.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null){
                                Log.i("MVC", "Submission has been saved successfully");
                                circularButton1.setProgress(100);
                                Toast.makeText(PhotoPreviewActivity.this, "Submission Successful!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                circularButton1.setProgress(-1);
                                Toast.makeText(PhotoPreviewActivity.this, "Submission Error!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}

