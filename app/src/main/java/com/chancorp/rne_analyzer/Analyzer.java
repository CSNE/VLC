package com.chancorp.rne_analyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Chan on 3/30/2016.
 */
public class Analyzer {
    Pixel[] averaged, gCorrected, lowPassed, derivative, derivativeLowpass, derivativeLowpassSubtracted;
    Context c;

    public Analyzer(Bitmap bmp, Context c){
        Log2.log(1,this,"Analyzer Created: w="+bmp.getWidth()+", h="+bmp.getHeight());
        averaged=PixelOperations.feedData(bmp);
        this.c=c;
    }
    public void prepareData(){
        Log2.log(1,this,"Prepping data...");
        gCorrected=PixelOperations.transformColorSpace(averaged);
        Log2.log(1,this,"Gamma Correction done.");
        lowPassed=PixelOperations.lowPass(gCorrected, 3);
        Log2.log(1,this,"Lowpass done.");
        derivative=PixelOperations.derivative(lowPassed);
        Log2.log(1,this,"Derivative done.");
        derivativeLowpass=PixelOperations.lowPass(derivative, 300);
        Log2.log(1,this,"Derivative Lowpass done.");
        derivativeLowpassSubtracted=PixelOperations.subtractPixels(derivative, derivativeLowpass);
        Log2.log(1,this,"Lowpass Subtraction done.");
    }
    public void logData(){
        Log2.log(1,this,"Writing...");
        WriteHelper.writeToFile(averaged, c, "1_Averaged");
        WriteHelper.writeToFile(gCorrected,c,"2_GammaCorrected");
        WriteHelper.writeToFile(lowPassed,c,"3_Lowpass");
        WriteHelper.writeToFile(derivative,c,"4_Derivative");
        WriteHelper.writeToFile(derivativeLowpass,c,"5_DerivativeLowpass");
        WriteHelper.writeToFile(derivativeLowpassSubtracted,c,"6_DerivativeLowpassSubtracted");
        Log2.log(1, this, "Written.");
    }


    public void peakAnalyze(int type){
        Log2.log(1,this,"Starting Peak Analysis...");
        double[] dat=DataOperations.fromPixels(derivativeLowpassSubtracted,type);
        double stdDeviation=DataOperations.stdDeviation(dat);

        ArrayList<Peak> peakCandidates=new ArrayList<>();
        boolean rising=false;
        for (int i = 1; i < dat.length; i++) {
            if (!rising && dat[i]-dat[i-1]>=0){
                rising=true;
                peakCandidates.add(new Peak(Peak.RISING,i-1,dat[i-1]));
            }else if (rising && dat[i]-dat[i-1]<0){
                rising=false;
                peakCandidates.add(new Peak(Peak.FALLING,i-1,dat[i-1]));
            }
        }

        WriteHelper.writeToFile(peakCandidates.toArray(new Peak[peakCandidates.size()]), c, "a1_PeakCandidates");
        double zeroThreshold=stdDeviation*1.5;
        for (Iterator<Peak> iterator = peakCandidates.iterator(); iterator.hasNext();) {
            Peak p = iterator.next();
            if (!(p.value<zeroThreshold && p.value>-zeroThreshold)) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }

        WriteHelper.writeToFile(peakCandidates.toArray(new Peak[peakCandidates.size()]), c, "a2_Validate_Zero");


        double closeThreshold=stdDeviation*0.5;
        double prevVal=10000;
        for (Iterator<Peak> iterator = peakCandidates.iterator(); iterator.hasNext();) {
            Peak p = iterator.next();
            if (Math.abs(p.value-prevVal)<closeThreshold) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }

        WriteHelper.writeToFile(peakCandidates.toArray(new Peak[peakCandidates.size()]), c, "a3_Validate_Close");
        Log2.log(1, this, "Peak Analysis Done.");
    }
}
