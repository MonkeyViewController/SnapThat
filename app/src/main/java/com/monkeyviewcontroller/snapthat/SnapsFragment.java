package com.monkeyviewcontroller.snapthat;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.SurfaceHolder.Callback;

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
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private View rootView;
    private  List<Game> currentGames;
    private int selectedTermIndex;
    private TextView tvSelectedTerm;
    private ImageButton activeGamesButton;
    private static final int RED = 0xffFF8080;
    private static final int WHITE = 0xffFFFFFF;

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

    /*@Override
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
    }*/

    public void openCamera()
    {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                Log.d("MVC", "No camera with exception: " + e.getMessage());
                Toast.makeText(getActivity(), "No camera detected",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /*private void setTermTextViewFromIndex(final int index)
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
    }*/

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
        listPopupWindow.setAnchorView(surfaceView);

        listPopupWindow.setWidth((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics()));
        listPopupWindow.setHeight((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()));
        listPopupWindow.setModal(true);
        listPopupWindow.setHorizontalOffset((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 198, getResources().getDisplayMetrics()));
        listPopupWindow.setVerticalOffset((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -260, getResources().getDisplayMetrics()));

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
                if(selectedTermIndex == -1) {
                    ValueAnimator colorAnim = ObjectAnimator.ofInt(tvSelectedTerm, "textColor", WHITE, RED);
                    colorAnim.setDuration(250);
                    colorAnim.setEvaluator(new ArgbEvaluator());
                    colorAnim.setRepeatCount(3);
                    colorAnim.setRepeatMode(ValueAnimator.REVERSE);

                    /*AnimatorSet scaleDown = new AnimatorSet();

                    ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(tvSelectedTerm, "scaleX", 1.2f);
                    ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(tvSelectedTerm, "scaleY", 1.2f);
                    scaleUpX.setDuration(300);
                    scaleUpY.setDuration(300);

                    scaleDown.play(scaleUpX).with(scaleUpY);*/
                    colorAnim.start();
                    return;
                }

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
        surfaceView = (SurfaceView) view.findViewById(R.id.camera_surface_view);
        holder = surfaceView.getHolder();
        holder.addCallback(new Callback() {

            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null) {
                        mCamera.setDisplayOrientation(90);
                        mCamera.setPreviewDisplay(holder);
                        mCamera.startPreview();
                    }
                } catch (IOException e) {
                    Log.d("MVC", "\"Error setting up preview",e);
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                // nothing to do here
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                holder.removeCallback(this);
            }
        });
        return qOpened;
    }


    private void releaseCameraAndPreview() {

        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
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

    /*@Override
    public void onResume() {
        super.onResume();
        Log.d("MVC", "snapOnResume");
        openCamera();

        if(mCamera !=null)
            mCamera.startPreview();
    }

    @Override
    public void onPause() {
        Log.d("MVC", "snapOnPause");
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }
        super.onPause();
    }*/
}