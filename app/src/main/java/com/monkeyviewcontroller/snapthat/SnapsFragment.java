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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SnapsFragment extends Fragment {

    private Spinner termsSpinner;
    private Camera mCamera;
    private CameraPreview mPreview;
    private View rootView;
    private  List<Game> games;

    public static SnapsFragment newInstance() {
        SnapsFragment fragment = new SnapsFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //Wait until snapsFragment is visible, then build the Spinner
            //Game data has already been downloaded
            setupTermsDropdown();
        }
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

    private void setupTermsDropdown(){
        termsSpinner = (Spinner) rootView.findViewById(R.id.termsSpinner);
        List<String> list = new ArrayList<>();

        games = ((MainActivity)getActivity()).getCurrentGames();

        for (Game g: games){
            list.add(g.getSearchItem());
        }

        Log.i("DEBUG", getActivity().toString());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        termsSpinner.setAdapter(dataAdapter);
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

                int selectedTermIndex = termsSpinner.getSelectedItemPosition();

                //TODO: save and transmit oid of selected game to the preview
                String selectedGameOID = games.get(selectedTermIndex).getObjectId();


                Intent intent = new Intent(getActivity(), PhotoPreviewActivity.class);
                //Store selectedGameOID so that we can save to parse in the PhotoPreviewActivity
                intent.putExtra("selectedGameOID",selectedGameOID);
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
                Log.d("Camera", "Capture Button Clicked");

                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    private void setupFriendsButton(final Camera.PictureCallback mPicture){
        ImageButton friendsButton = (ImageButton) rootView.findViewById(R.id.friends_button);

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("MVC", "Friends Button Clicked");
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
                Log.d("MVC", "Games Button Clicked");
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
                Log.d("MVC", "Active Games Button Clicked");

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

   }