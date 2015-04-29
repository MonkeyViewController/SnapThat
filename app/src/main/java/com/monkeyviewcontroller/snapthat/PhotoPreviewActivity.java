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

import com.monkeyviewcontroller.snapthat.Models.STUser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        setPhotoToView();

        previewConfirmButton = (ImageButton) findViewById(R.id.previewConfirmButton);
        previewConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MVC","Confirm Clicked");

                Intent intent = new Intent(PhotoPreviewActivity.this, GameSelectionActivity.class);
                startActivity(intent);
            }
        });

        cancelSubmissionButton = (ImageButton) findViewById(R.id.cancelSubmissionButton);
        cancelSubmissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MVC","Cancel Clicked");
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
}
