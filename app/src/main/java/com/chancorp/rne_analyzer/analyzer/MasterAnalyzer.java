package com.chancorp.rne_analyzer.analyzer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.chancorp.rne_analyzer.data.Bits;
import com.chancorp.rne_analyzer.data.Block;
import com.chancorp.rne_analyzer.data.Peak;
import com.chancorp.rne_analyzer.data.PeakBlock;
import com.chancorp.rne_analyzer.data.Pixel;
import com.chancorp.rne_analyzer.helper.BytesReader;
import com.chancorp.rne_analyzer.helper.EC;
import com.chancorp.rne_analyzer.helper.ErrorLogger;
import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.LogLogger;
import com.chancorp.rne_analyzer.helper.Timer;
import com.chancorp.rne_analyzer.helper.WriteHelper;
import com.chancorp.rne_analyzer.ui.MainActivity;

import java.io.File;
import java.util.List;

/**
 * Created by Chan on 4/23/2016.
 */
public class MasterAnalyzer {
    public interface ExposureTimeCallback{
        void callback(String exp);
    }

    public static Block analyze(byte[] data, long capturedTime){
        LogLogger ll=new LogLogger();
        Log2.addLogListener(ll);

        long start =System.currentTimeMillis();

        Log2.log(2, MasterAnalyzer.class, ">>>Starting analysis...<<<");


        Timer.startTimer("Decode JPEG");

        Bitmap bmp=BitmapFactory.decodeByteArray(data, 0, data.length);
        Timer.endTimer("Decode JPEG");


        //Memory
        Log2.log(1, MasterAnalyzer.class, "ImageAnalyzer Created: w=" + bmp.getWidth() + ", h=" + bmp.getHeight());
        EC.IMAGE_HEIGHT=bmp.getHeight();
        Timer.startTimer("Average Pixels");
        int[] imageraw=PixelOperations.bitmapToInts(bmp);
        int w=bmp.getWidth();
        int h=bmp.getHeight();
        bmp=null;
        System.gc();

        Pixel[] averaged= PixelOperations.averageRows(imageraw, w,h);
        imageraw=null;
        data=null;
        System.gc();

        Timer.endTimer("Average Pixels");
        ImageAnalyzer ia=new ImageAnalyzer(averaged);




        ia.prepareData();
        ia.logData();

        Bits b=channelAnalyze(ia,DataOperations.BLUE);
        b.append(channelAnalyze(ia,DataOperations.RED));

        Log2.log(2,MasterAnalyzer.class,"Analysis complete!",(System.currentTimeMillis()-start)/1000.0f);
        Log2.removeLogListener(ll);
        WriteHelper.writeToFileAsync(ll.flush(),"Log");
        WriteHelper.writeToFileAsync(Timer.dumpResults(),"Timer");
        ll=null;
        try{
            Block res=new Block(b, capturedTime);
            return res;
        }catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public static Block analyze(File source, MainActivity ma, ExposureTimeCallback etc, long capturedTime){

        Timer.startTimer("Read EXIF");


        String et="";
        try {
            ExifInterface ei=new ExifInterface(source.getAbsolutePath());
            Log2.log(1, MasterAnalyzer.class, "Exif Parsed!");

            et=ei.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            etc.callback(et);
            ma.setExpTimeVal(et);
            Log2.log(1, MasterAnalyzer.class, "Exposure Time: "+et);
        } catch (Exception e) {
            ErrorLogger.log(e);
        }

        Timer.endTimer("Read EXIF");

        Timer.startTimer("Read Bitmap to Bytearray");
        byte[] data= BytesReader.readContentIntoByteArray(source);
        Log2.log(1, MasterAnalyzer.class, "Picture Loaded!",data.length);
        Timer.endTimer("Read Bitmap to Bytearray");

        return analyze(data,capturedTime);
    }

    public static Bits channelAnalyze(ImageAnalyzer ia, int colorCode){
        Log2.log(1,MasterAnalyzer.class,"Starting analysis for channel "+DataOperations.colorCode(colorCode));
        List<Peak> peaks=ia.peakAnalyze(colorCode);

        Timer.startTimer("Seperate Blocks"+DataOperations.colorCode(colorCode));
        PeakAnalyzer pa=new PeakAnalyzer(peaks,colorCode);
        pa.group();
        pa.seperateBlocks(EC.millisecToPixels(EC.BLOCK_DELAY));
        PeakBlock peakBlock=pa.trim();
        Timer.endTimer("Seperate Blocks"+DataOperations.colorCode(colorCode));

        Timer.startTimer("Acquire Data"+DataOperations.colorCode(colorCode));
        peakBlock.verifySymmetry();
        List<Boolean> dat= peakBlock.getData(EC.BITS_PER_GROUP, EC.millisecToPixels(EC.BIT_DELAY), colorCode);
        Bits finalData=new Bits(dat);
        Log2.log(1,MasterAnalyzer.class,"Writing final data...");
        WriteHelper.writeToFileAsync(finalData,"d2_"+DataOperations.colorCode(colorCode)+"_FinalDat");
/*
                Block block=new Block(dat);
                block.verify();

                ba.add(block);*/
        Log2.log(2,MasterAnalyzer.class,"Data Received",finalData.toString(),finalData.decodeString());
        Log2.log(1,MasterAnalyzer.class,"Analysis complete for channel "+DataOperations.colorCode(colorCode));
        Timer.endTimer("Acquire Data"+DataOperations.colorCode(colorCode));



        return finalData;
    }
}
