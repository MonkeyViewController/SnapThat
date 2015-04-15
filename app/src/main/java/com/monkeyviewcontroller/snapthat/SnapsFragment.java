package com.monkeyviewcontroller.snapthat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SnapsFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private View rootView;

    public static SnapsFragment newInstance() {
        SnapsFragment fragment = new SnapsFragment();
        return fragment;
    }

    public SnapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MVC", "Creating the snap tab");
        rootView = inflater.inflate(R.layout.fragment_snaps, container, false);
        safeCameraOpenInView(rootView);

        Camera.PictureCallback mPicture = setupImageCapture();

        setupCaptureButton(mPicture);

        return rootView;
    }

    private Camera.PictureCallback setupImageCapture(){
        final Camera.PictureCallback mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Camera","Byte array created");

                Matrix matrix = new Matrix();
                matrix.postRotate(90);

                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                // Use matrix to rotate bitmap image
                // to compensate for 90 manual rotation in preview workaround
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                //TEMP: Set ImageView ontop of preview to display image
                ImageView iv = (ImageView) rootView.findViewById(R.id.imageViewOfPicture);
                iv.setImageBitmap(rotatedBitmap);

                //String encodedImage = Base64.encodeToString(data, Base64.DEFAULT);
                //Log.i("img",encodedImage);

                //TODO: Transmit photo data to server/parse
            }
        };

        return mPicture;
    }

    private void setupCaptureButton(final Camera.PictureCallback mPicture){
        Button captureButton = (Button) rootView.findViewById(R.id.capture_button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Camera", "Capture Button Clicked");
                mCamera.takePicture(null, null, mPicture);
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