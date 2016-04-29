package com.chancorp.rne_analyzer.analyzer;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import com.chancorp.rne_analyzer.data.Bits;
import com.chancorp.rne_analyzer.data.Peak;
import com.chancorp.rne_analyzer.data.PeakBlock;
import com.chancorp.rne_analyzer.helper.BytesReader;
import com.chancorp.rne_analyzer.helper.EC;
import com.chancorp.rne_analyzer.helper.ErrorLogger;
import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.LogLogger;
import com.chancorp.rne_analyzer.helper.WriteHelper;

import java.io.File;
import java.util.List;

/**
 * Created by Chan on 4/23/2016.
 */
public class MasterAnalyzer {
    public static Bits analyze(File source){
        LogLogger ll=new LogLogger();
        Log2.addLogListener(ll);

        long start =System.currentTimeMillis();

        Log2.log(2, MasterAnalyzer.class, ">>>Starting analysis...<<<");

        byte[] data= BytesReader.readContentIntoByteArray(source);


        Log2.log(1, MasterAnalyzer.class, "Picture Loaded!",data.length);

        try {
            ExifInterface ei=new ExifInterface(source.getAbsolutePath());
            Log2.log(1, MasterAnalyzer.class, "Exif Parsed!");

            String et=ei.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
            Log2.log(1, MasterAnalyzer.class, "Exposure Time: "+et);
        } catch (Exception e) {
            ErrorLogger.log(e);
        }

        ImageAnalyzer ia=new ImageAnalyzer(BitmapFactory.decodeByteArray(data, 0, data.length));
        ia.prepareData();
        ia.logData();

        Bits b=channelAnalyze(ia,DataOperations.BLUE);
        b.append(channelAnalyze(ia,DataOperations.RED));

        Log2.log(2,MasterAnalyzer.class,"Analysis complete!",(System.currentTimeMillis()-start)/1000.0f);
        Log2.removeLogListener(ll);
        WriteHelper.writeToFileAsync(ll.flush(),"Log");
        ll=null;
        return b;
    }

    public static Bits channelAnalyze(ImageAnalyzer ia, int colorCode){
        Log2.log(1,MasterAnalyzer.class,"Starting analysis for channel "+DataOperations.colorCode(colorCode));
        List<Peak> peaks=ia.peakAnalyze(colorCode);

        PeakAnalyzer pa=new PeakAnalyzer(peaks,colorCode);
        pa.group();
        pa.seperateBlocks(EC.millisecToPixels(EC.BLOCK_DELAY));
        PeakBlock peakBlock=pa.trim();


        peakBlock.verifySymmetry();
        List<Boolean> dat= peakBlock.getData(EC.BITS_PER_GROUP, EC.millisecToPixels(EC.BIT_DELAY), colorCode);
        Bits finalData=new Bits(dat);
        Log2.log(1,MasterAnalyzer.class,"Writing final data...");
        WriteHelper.writeToFile(finalData,"d2_"+DataOperations.colorCode(colorCode)+"_FinalDat");
/*
                Block block=new Block(dat);
                block.verify();

                ba.add(block);*/
        Log2.log(2,MasterAnalyzer.class,"Data Received",finalData.toString(),finalData.decodeString());
        Log2.log(1,MasterAnalyzer.class,"Analysis complete for channel "+DataOperations.colorCode(colorCode));



        return finalData;
    }
}