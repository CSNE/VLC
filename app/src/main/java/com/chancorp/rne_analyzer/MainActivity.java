package com.chancorp.rne_analyzer;

import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /** A safe way to get an instance of the Camera object. */

    CameraPreview mPreview;
    Camera mCamera;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // Create an instance of Camera
        mCamera = getCameraInstance();
        Camera.Parameters params=mCamera.getParameters();
        params.setPictureSize(500,500);
        params.setPreviewSize(500,500);
        mCamera.setParameters(params);

        fab.setOnClickListener(this);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.frame);
        preview.addView(mPreview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
        mCamera.release();
        super.onStop();

    }


    @Override
    public void onClick(View v) {
        // get an image from the camera
        mCamera.takePicture(null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data == null) Toast.makeText(MainActivity.this, "NULL!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(MainActivity.this, "Length: " + data.length, Toast.LENGTH_SHORT).show();
            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data == null) Toast.makeText(MainActivity.this, "JPEG NULL!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(MainActivity.this, "JPEG Length: " + data.length, Toast.LENGTH_SHORT).show();
                Analyzer a=new Analyzer(BitmapFactory.decodeByteArray(data, 0, data.length));
                a.analyze();
            }
        });
    }
}
