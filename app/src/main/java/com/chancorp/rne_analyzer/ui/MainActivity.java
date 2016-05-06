package com.chancorp.rne_analyzer.ui;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.chancorp.rne_analyzer.data.Block;
import com.chancorp.rne_analyzer.data.BlockArray;
import com.chancorp.rne_analyzer.helper.ErrorLogger;
import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.R;
import com.chancorp.rne_analyzer.helper.OneTimeTimer;
import com.chancorp.rne_analyzer.helper.Timer;
import com.chancorp.rne_analyzer.helper.WriteHelper;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, WriteHelper.AsyncListener {
    /**
     * A safe way to get an instance of the Camera object.
     */

    CameraPreview mPreview;
    Camera mCamera;

    BlockArray ba = new BlockArray();

    File captured, reference;

    TextView binary, utf8, write,threads, expTime, blockData, blockBinary;

    //StringBuilder tempLog = new StringBuilder();

    boolean directMode = false;


    boolean continueousCaptureMode = false, gotFirstBlock = false;
    int numActiveThreads = 0;


    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log2.log(2, this, "NEW INSTANCE!");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((Button) findViewById(R.id.cap_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.multi_btn)).setOnClickListener(this);
        ((Button) findViewById(R.id.multi_stop)).setOnClickListener(this);
        // Create an instance of Camera
        mCamera = getCameraInstance();


        Camera.Parameters params = mCamera.getParameters();

        Log2.log(2, this, "Supported Exposure Modes:" + params.get("exposure-mode-values"));
        Log2.log(2, this, "Supported White Balance Modes:" + params.get("whitebalance-values"));
        params.set("whitebalance", "cloudy-daylight");
        params.setPictureSize(3264, 2448);
        params.setPreviewSize(640, 480);
        mCamera.setParameters(params);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.frame);
        preview.addView(mPreview);

        captured = new File(Environment.getExternalStorageDirectory(), "CAP.jpeg");
        reference = new File(Environment.getExternalStorageDirectory(), "REF.jpg");

        binary = (TextView) findViewById(R.id.textView2);
        utf8 = (TextView) findViewById(R.id.data_received_text);
        write = (TextView) findViewById(R.id.write);
        expTime = (TextView) findViewById(R.id.exp_time);
        threads=(TextView) findViewById(R.id.threads);

        //blockBinary = (TextView) findViewById(R.id.block_binary);
        blockData = (TextView) findViewById(R.id.block_data);

        WriteHelper.setAsyncListener(this);
        //WriteHelper.setSkip(true);

        Timer.setActiva(false);
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


        if (id == R.id.db_1) {
            startAsyncAnalysis(captured, null);
        } else if (id == R.id.db_2) {
            startAsyncAnalysis(reference, null);
        } else if (id == R.id.db_3) {
            WriteHelper.setSkip(false);
        } else if (id == R.id.db_4) {
            WriteHelper.setSkip(true);
        } else if (id == R.id.db_5) {
            ba.reset();
        } else if (id == R.id.db_6) {
            directMode = true;
        } else if (id == R.id.db_7) {
            directMode = false;
        }

        return super.onOptionsItemSelected(item);
    }

    Handler h = new Handler();
    int delay = 4000; //milliseconds

    private void startAnalysisLoop() {

        h.postDelayed(new Runnable() {
            public void run() {
                if (continueousCaptureMode) {
                    captureAndAnalyze();
                }
                h.postDelayed(this, delay);
            }
        }, delay);

    }

    public void incrementActiveThreads(){
        numActiveThreads++;
        threads.post(new Runnable() {
            @Override
            public void run() {
                threads.setText(""+numActiveThreads);
            }
        });
    }
    public void decrementActiveThreads(){
        numActiveThreads--;
        threads.post(new Runnable() {
            @Override
            public void run() {
                threads.setText(""+numActiveThreads);
            }
        });
    }

    private void startAsyncAnalysis(File f, byte[] b) {
        final long capturedTime = System.currentTimeMillis();
        final File f_ = f;
        final byte[] b_ = b;
        new Thread(new Runnable() {
            @Override
            public void run() {
                incrementActiveThreads();
                resetDisplay();
                Block data = null;
                if (f_ != null) {
                    data = MasterAnalyzer.analyze(f_, MainActivity.this, new MasterAnalyzer.ExposureTimeCallback() {
                        @Override
                        public void callback(String exp) {
                            //tempLog.append(exp);
                        }
                    }, capturedTime);
                } else if (b_ != null) {
                    data = MasterAnalyzer.analyze(b_, capturedTime);
                } else {
                    Log2.log(4, this, "StartAsyncAnalysis called but the File nor byte[] is not null!");
                }

                if (data != null) {
                    /*
                    if (data.verify() &&
                            (data.getData().decodeString().equals("Test") || data.getData().decodeString().equals("ASDF") || data.getData().decodeString().equals("What"))) {

                        tempLog.append("\tSuccess\n");
                    } else {
                        tempLog.append("\tFail\n");
                    }*/

                    if (!data.verify()) {
                        postToast("Parity Check Failed!");
                    } else {
                        postToast("Got data!");
                        if (continueousCaptureMode) {
                            boolean newBlock = ba.addBlock(data);
                            Log2.logAlt(2, this, "Got block.");

                            if (newBlock) {
                                Log2.logAlt(2, this, "It's a new block!");

                                if (data.isFirstBlock()) {
                                    if (gotFirstBlock) {
                                        Log2.logAlt(2, this, "First Block Acquired.");
                                        if (ba.getSize() > 1) {
                                            Log2.logAlt(2, this, "BlocksAquired>1. Breaking.");
                                            continueousCaptureMode = false; //We already have the first block. it looped; break.
                                        }
                                    } else if (data.isSingleBlockMessage()) {
                                        Log2.logAlt(2, this, "Single Block Message. Breaking.");
                                        continueousCaptureMode = false; //We already have the first block. it looped; break.
                                    } else { //First block acquired for the first time.
                                        Log2.logAlt(2, this, "First Block Acquired for the first time.");
                                        ba.reset(); //Fresh start!
                                        ba.addBlock(data);
                                        gotFirstBlock = true; //We got the first block. good.
                                    }
                                }
                            }
                        }

                    }

                    display(data);
                }
                /*
                else{
                    tempLog.append("\tFail\n");
                }*/

                Log2.log(2, this, "Analysis Time", OneTimeTimer.end() / 1000.0f);

                //End analysis. Start again.
                //if (continueousCaptureMode) captureAndAnalyze();
                decrementActiveThreads();
            }
        }).start();

    }

    private void resetDisplay() {
        binary.post(new Runnable() {
            @Override
            public void run() {
                binary.setText("Wait...");
                utf8.setText("Wait...");
            }
        });
    }

    private void postToast(String s) {
        final String s_ = s;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, s_, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setExpTimeVal(String val) {
        final String val_ = val;
        expTime.post(new Runnable() {
            @Override
            public void run() {
                expTime.setText(val_);
            }
        });
    }

    private void display(Block data) {
        final Block data_ = data;
        binary.post(new Runnable() {
            @Override
            public void run() {
                binary.setText(data_.getBlockInformation().toString() + "\n"
                        + data_.getData().toSplitString() + "\n"
                        + data_.getParity().toString());
                utf8.setText(data_.getData().decodeString());
                try {
                    blockData.setText(ba.getFullBits().decodeString());
                } catch (BlockArray.UndecodableBlockException e) {
                    blockData.setText("Undecodable!");
                }
            }
        });


    }


    private void captureAndAnalyze() {
        OneTimeTimer.start();
        // get an image from the camera
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() { //JPEG data
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log2.log(1, this, "Picture Received!");
                if (directMode) {
                    startAsyncAnalysis(null, data);
                } else {
                    try {

                        FileOutputStream fos = new FileOutputStream(captured);
                        fos.write(data);
                        fos.close();
                    } catch (Exception e) {
                        ErrorLogger.log(e);
                    }
                    Log2.log(1, this, "Picture Saved!");

                    startAsyncAnalysis(captured, null);
                }
            }
        });


    }

    @Override
    public void onStop() {
        mCamera.release();
        super.onStop();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cap_btn) {
            captureAndAnalyze();
        } else if (v.getId() == R.id.multi_btn) {
            continueousCaptureMode = true;
            gotFirstBlock = false;
            ba.reset();
            //captureAndAnalyze();
            startAnalysisLoop();
        } else if (v.getId() == R.id.multi_stop) {
            continueousCaptureMode = false;
        }

    }


    @Override
    public void asyncStarted() {
        write.post(new Runnable() {
            @Override
            public void run() {
                write.setText("Writing...");
            }
        });
    }

    @Override
    public void asyncEnded() {
        write.post(new Runnable() {
            @Override
            public void run() {
                write.setText("Write Complete.");
            }
        });
    }
}
