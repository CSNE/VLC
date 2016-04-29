package com.chancorp.rne_analyzer.ui;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chancorp.rne_analyzer.analyzer.MasterAnalyzer;
import com.chancorp.rne_analyzer.data.Bits;
import com.chancorp.rne_analyzer.data.BlockArray;
import com.chancorp.rne_analyzer.helper.ErrorLogger;
import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.R;
import com.chancorp.rne_analyzer.helper.WriteHelper;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, WriteHelper.AsyncListener{
    /** A safe way to get an instance of the Camera object. */

    CameraPreview mPreview;
    Camera mCamera;

    BlockArray ba=new BlockArray();

    File captured, reference;

    TextView binary, utf8,writing;

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
        Log2.log(2,this,"NEW INSTANCE!");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button fab = (Button) findViewById(R.id.cap_btn);
        // Create an instance of Camera
        mCamera = getCameraInstance();
        Camera.Parameters params=mCamera.getParameters();
        params.setPictureSize(3264,2448);
        params.setPreviewSize(640,480);
        mCamera.setParameters(params);

        fab.setOnClickListener(this);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.frame);
        preview.addView(mPreview);

        captured =new File(Environment.getExternalStorageDirectory(),"CAP.jpeg");
        reference =new File(Environment.getExternalStorageDirectory(),"REF.jpg");

        binary=(TextView) findViewById(R.id.textView2);
        utf8=(TextView) findViewById(R.id.textView);
        writing=(TextView)findViewById(R.id.textView3);

        WriteHelper.setAsyncListener(this);
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
        }else if (id == R.id.db_1) {
            startAsyncAnalysis(captured);
        }else if (id == R.id.db_2) {
            startAsyncAnalysis(reference);
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAsyncAnalysis(File f){
        final File f_=f;
        new Thread(new Runnable() {
            @Override
            public void run() {
                display(MasterAnalyzer.analyze(f_));
            }
        }).start();
    }

    private void display(Bits data){
        final Bits data_=data;
        binary.post(new Runnable() {
            @Override
            public void run() {
                binary.setText(data_.toSplitString());
                utf8.setText(data_.decodeString());
            }
        });

    }

    @Override
    public void onStop(){
        mCamera.release();
        super.onStop();

    }


    @Override
      public void onClick(View v) {
        // get an image from the camera
        mCamera.takePicture(null, new Camera.PictureCallback() {  //Raw Data
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (data == null) Toast.makeText(MainActivity.this, "NULL!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(MainActivity.this, "Length: " + data.length, Toast.LENGTH_SHORT).show();
            }
        }, null, new Camera.PictureCallback() { //JPEG data
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log2.log(1, this, "Picture Received!");

                try {

                    FileOutputStream fos = new FileOutputStream(captured);
                    fos.write(data);
                    fos.close();
                } catch (Exception e) {
                    ErrorLogger.log(e);
                }
                Log2.log(1, this, "Picture Saved!");

                startAsyncAnalysis(captured);
            }
        });
    }


    @Override
    public void asyncStarted() {
        writing.post(new Runnable() {
            @Override
            public void run() {
                writing.setText("Writing...");
            }
        });
    }

    @Override
    public void asyncEnded() {
        writing.post(new Runnable() {
            @Override
            public void run() {
                writing.setText("Write Complete.");
            }
        });
    }
}
