package com.monkeyviewcontroller.snapthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.monkeyviewcontroller.snapthat.Adapters.CurrentGameListAdapter;
import com.monkeyviewcontroller.snapthat.Models.Game;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseUser;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SnapsFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private View rootView;

    public static SnapsFragment newInstance() {
        SnapsFragment fragment = new SnapsFragment();
        return fragment;
    }

    public  SnapsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the snap tab");
        rootView = inflater.inflate(R.layout.fragment_snaps, container, false);
        safeCameraOpenInView(rootView);

        Camera.PictureCallback mPicture = setupImageCapture();

        setupCaptureButton(mPicture);
        setupFriendsButton(mPicture);
        setupGamesButton(mPicture);
        setupActiveGamesButton(mPicture);

        //TODO; front facing camera switcher
        return rootView;
    }

    private Camera.PictureCallback setupImageCapture(){
        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("MVC","Byte array created");

                File outputDir = getActivity().getCacheDir(); // context being the Activity pointer
                try {
                    File outputFile = new File(outputDir, "photopreview.tmp");
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
                    bos.write(data);
                    bos.flush();
                    bos.close();
                    Log.d("MVC","Byte array written to cache file, size: " + outputFile.length() + outputFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getActivity(), PhotoPreviewActivity.class);
                startActivity(intent);
            }
        };

        return mPicture;
    }

    private void setupCaptureButton(final Camera.PictureCallback mPicture){
        ImageButton captureButton = (ImageButton) rootView.findViewById(R.id.capture_button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Camera", "Capture Button Clicked");
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    private void setupFriendsButton(final Camera.PictureCallback mPicture){
        ImageButton friendsButton = (ImageButton) rootView.findViewById(R.id.friends_button);

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MVC", "Friends Button Clicked");
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.pager);
                vp.setCurrentItem(0, true);

            }
        });
    }

    private void setupGamesButton(final Camera.PictureCallback mPicture){
        ImageButton gamesButton = (ImageButton) rootView.findViewById(R.id.games_button);

        gamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MVC", "Games Button Clicked");
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.pager);
                vp.setCurrentItem(2, true);
            }
        });
    }

    private void setupActiveGamesButton(final Camera.PictureCallback mPicture){
        ImageButton activeGamesButton = (ImageButton) rootView.findViewById(R.id.activegames_button);

        activeGamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("MVC", "Active Games Button Clicked");

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("user", ParseUser.getCurrentUser().getObjectId());

                ParseCloud.callFunctionInBackground("getcurrentgamesterms", params, new FunctionCallback<List<String>>() {
                    @Override
                    public void done(List<String> terms, com.parse.ParseException e) {

                        if (e != null) {
                            Log.d("MVC", "get current games terms error: " + e + " " + e.getCause());
                        } else {
                            Log.d("MVC", "got the current games terms");

                            for(String s: terms)
                            {
                                Log.d("MVC", "Terms: " + s);
                            }
                        }
                    }
                });
            }
        });
    }

    public boolean safeCameraOpenInView(View view)
    {
        boolean qOpened = false;
        releaseCameraAndPreview();
        mCamera = getCameraInstance();
        qOpened = (mCamera != null);
        mPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
        FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        return qOpened;
    }

    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        safeCameraOpenInView(rootView);
    }

    /*
    public void cameraSetup(){


        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);


        //Sets context so that FrameLayout preview can find camera_preview
        setContentView(R.layout.fragment_snaps);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        if(preview != null){
            Log.d("Camera", "Preview instantiated");
        }



        //ERROR: NEXT LINE CAUSES APP CRASH
        preview.addView(mPreview);

        if(preview != null){
            Log.d("Camera", "Preview instantiated");
        }

        //Prep for taking a pic
        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Camera","Byte array created");

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                //Transmit Photo as byte[] or bitmap here
            }
        };

        Button bttn = (Button) findViewById(R.id.capture_button);

        bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, mPicture);

            }
        });

    }



    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try
        {
            // release the camera immediately on pause event
            //releaseCamera();
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i("Test", "On Resume .....");

        try
        {
            mCamera = getCameraInstance();
            mCamera.setPreviewCallback(null);
            mPreview =  new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);

        } catch (Exception e){
            Log.d("Camera", "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Test", "On Start .....");

    }
    */
}