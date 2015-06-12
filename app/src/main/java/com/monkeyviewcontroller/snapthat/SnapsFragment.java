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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

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

    private ListPopupWindow listPopupWindow;
    private Camera mCamera;
    private CameraPreview mPreview;
    private View rootView;
    private  List<Game> currentGames;
    private int selectedTermIndex;
    private TextView tvSelectedTerm;
    private ImageButton activeGamesButton;

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
        setupActiveGamesButton(mPicture);
        setupSelectedTermTextView();
        setupTermsDropdown();
        selectedTermIndex = -1;

        //TODO; front facing camera switcher
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d("MVC", "Snapsfragment is visible");
            Intent intent = getActivity().getIntent();

            if(intent.hasExtra("selectedTermIndex")) {
                Log.d("MVC", "User selected a game in order to here.");

                setTermTextViewFromIndex(intent.getIntExtra("selectedTermIndex", -1));
                intent.removeExtra("selectedTermIndex");
            }
        }
        else {
            Log.d("MVC", "Snapsfragment is not visible");
        }
    }

    private void setTermTextViewFromIndex(final int index)
    {
        if(index == -1)
            return;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("user", ParseUser.getCurrentUser().getObjectId());

        ParseCloud.callFunctionInBackground("getcurrentgames", params, new FunctionCallback<List<Game>>() {
            @Override
            public void done(List<Game> games, com.parse.ParseException e) {
                if (e != null) {
                    Log.d("MVC", "get current games error: " + e + " " + e.getCause());
                } else {
                    Log.d("MVC", "got the current games");
                    currentGames = games;
                    List<String> list = new ArrayList<>();
                    for (Game g: games){
                        list.add(g.getSearchItem());
                    }

                    tvSelectedTerm.setText(list.get(index));
                    selectedTermIndex = index;
                }
            }
        });
    }

    private void setupSelectedTermTextView()
    {
        tvSelectedTerm = (TextView) rootView.findViewById(R.id.tvSelectedTerm);
        tvSelectedTerm.setText("No Game Selected");
    }

    private void setupTermsDropdown(){

        //TODO: adding games and then clicking on the dropdown will not show all games - the dropdown has already been created
        //TODO: must either recreate the dropdown every click, reset the adapter, or find a way to send a notification to recreate popup
        Log.d("MVC", "Setting up dropdown");

        listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setAnchorView(activeGamesButton);

        listPopupWindow.setWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        listPopupWindow.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
        listPopupWindow.setModal(true);
        //listPopupWindow.setHorizontalOffset(-20);
        //listPopupWindow.setVerticalOffset(-40);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("MVC", "List popup selected index: " + position);
                tvSelectedTerm.setText(currentGames.get(position).getSearchItem());
                selectedTermIndex = position;
                listPopupWindow.dismiss();
            }
        });
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

                //TODO: save and transmit oid of selected game to the preview
                String selectedGameOID = currentGames.get(selectedTermIndex).getObjectId();

                Intent intent = new Intent(getActivity(), PhotoPreviewActivity.class);
                //Store selectedGameOID so that we can save to parse in the PhotoPreviewActivity
                intent.putExtra("selectedGameOID",selectedGameOID);
                startActivity(intent);

                selectedTermIndex = -1;
                setupSelectedTermTextView();
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

                //If the user has not selected a game, do not allow them to take a photo.
                if(selectedTermIndex == -1)
                    return;

                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    private void setupActiveGamesButton(final Camera.PictureCallback mPicture){
        activeGamesButton = (ImageButton) rootView.findViewById(R.id.activegames_button);

        activeGamesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("user", ParseUser.getCurrentUser().getObjectId());

                ParseCloud.callFunctionInBackground("getcurrentgames", params, new FunctionCallback<List<Game>>() {
                    @Override
                    public void done(List<Game> games, com.parse.ParseException e) {
                        List<String> list = new ArrayList<>();
                        if (e != null) {
                            Log.d("MVC", "get current games error: " + e + " " + e.getCause());
                        } else {
                            Log.d("MVC", "got the current games");
                            currentGames = games;
                            for (Game g : games) {
                                Log.d("MVC", g.getSearchItem());
                                list.add(g.getSearchItem());
                            }

                            listPopupWindow.setAdapter(new ArrayAdapter(
                                    getActivity(),
                                    android.R.layout.simple_list_item_1, list));
                            listPopupWindow.show();
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
        Log.d("MVC", "Resuming snapfragment");
        super.onResume();
        safeCameraOpenInView(rootView);
    }

   }