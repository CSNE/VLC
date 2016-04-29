package com.chancorp.rne_analyzer.analyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;

import com.chancorp.rne_analyzer.data.Peak;
import com.chancorp.rne_analyzer.data.Pixel;
import com.chancorp.rne_analyzer.helper.EC;
import com.chancorp.rne_analyzer.helper.WriteHelper;
import com.chancorp.rne_analyzer.helper.Log2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Receives a Bitmap and makes a list of peaks.
public class ImageAnalyzer {
    Pixel[] averaged, gCorrected, lowPassed, derivative,
            derivativeLowpass, derivativeLowpassSubtracted, derivativeLowpassSubtractedMedian;


    public ImageAnalyzer(Bitmap bmp){
        Log2.log(1, this, "ImageAnalyzer Created: w=" + bmp.getWidth() + ", h=" + bmp.getHeight());
        EC.IMAGE_HEIGHT=bmp.getHeight();

        averaged= PixelOperations.feedData(bmp);

    }

    public void prepareData(){
        Log2.log(1,this,"Prepping data...");
        gCorrected=PixelOperations.transformColorSpace(averaged);
        Log2.log(1,this,"Gamma Correction done.");
        lowPassed=PixelOperations.lowPass(gCorrected, EC.LOWPASS_SIZE);
        Log2.log(1,this,"Lowpass done.");
        derivative=PixelOperations.derivative(lowPassed);
        Log2.log(1,this,"Derivative done.");
        derivativeLowpass=PixelOperations.lowPass(derivative, EC.DERIVATIVE_LOWPASS);
        //derivativeLowpass=PixelOperations.copy(derivative);
        Log2.log(1,this,"Derivative Lowpass done.");
        derivativeLowpassSubtracted=PixelOperations.subtractPixels(derivative, derivativeLowpass);
        Log2.log(1,this,"Lowpass Subtraction done.");
        derivativeLowpassSubtractedMedian=PixelOperations.medianFilter(derivativeLowpassSubtracted,1);
        Log2.log(1,this,"Median done.");
    }
    public void logData(){
        Log2.log(1,this,"Writing...");
        WriteHelper.writeToFile(averaged, "1_Averaged");
        WriteHelper.writeToFile(gCorrected,"2_GammaCorrected");
        WriteHelper.writeToFile(lowPassed,"3_Lowpass");
        WriteHelper.writeToFile(derivative,"4_Derivative");
        WriteHelper.writeToFile(derivativeLowpass,"5_DerivativeLowpass");
        WriteHelper.writeToFile(derivativeLowpassSubtracted,"6_DerivativeLowpassSubtracted");
        WriteHelper.writeToFile(derivativeLowpassSubtractedMedian,"7_DerivativeLowpassSubtractedMedian");
        Log2.log(1, this, "Written.");
    }


    public List<Peak> peakAnalyze(int colorCode){
        Log2.log(1,this,"Starting Peak Analysis...");
        double[] dat=DataOperations.fromPixels(derivativeLowpassSubtractedMedian,colorCode);
        //double stdDeviation=DataOperations.stdDeviation(dat);
        double[] stdDeviations=DataOperations.localizedStdDeviation(dat,EC.STD_DEVIATION_RANGE);
        //Log2.log(2, this, "SDev: " + stdDeviation);

        ArrayList<Peak> peakCandidates=new ArrayList<>();
        boolean rising=false;
        for (int i = 1; i < dat.length; i++) {
            if (!rising && dat[i]-dat[i-1]>=0){
                rising=true;
                peakCandidates.add(new Peak(Peak.LOWER,i-1,dat[i-1]));
            }else if (rising && dat[i]-dat[i-1]<0){
                rising=false;
                peakCandidates.add(new Peak(Peak.UPPER,i-1,dat[i-1]));
            }
        }

        WriteHelper.writeToFile(peakCandidates.toArray(new Peak[peakCandidates.size()]), "a1_"+DataOperations.colorCode(colorCode)+"_PeakCandidates");



        //double closeThreshold=stdDeviation*EC.TOO_CLOSE;
        double[] closeThresholds=DataOperations.multiply(stdDeviations,EC.TOO_CLOSE);
        WriteHelper.writeToFile(closeThresholds,"t1_"+DataOperations.colorCode(colorCode)+"_CloseThresholds",true);
        //Log2.log(2,this,"Close Threshold: "+closeThreshold);
        double prevVal=10000;
        for (Iterator<Peak> iterator = peakCandidates.iterator(); iterator.hasNext();) {
            Peak p = iterator.next();
            if (Math.abs(p.value-prevVal)<closeThresholds[p.coordinate]) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
            prevVal=p.value;
        }



        WriteHelper.writeToFile(peakCandidates.toArray(new Peak[peakCandidates.size()]), "a2_"+DataOperations.colorCode(colorCode)+"_Validate_Close");
        Log2.log(1, this, "Peak Analysis Done.");



        //double zeroThreshold=stdDeviation*EC.TOO_ZERO;
        double[] zeroThresholds=DataOperations.multiply(stdDeviations,EC.TOO_ZERO);
        WriteHelper.writeToFile(zeroThresholds,"t2_"+DataOperations.colorCode(colorCode)+"_ZeroThresholds",true);
        //Log2.log(2,this,"Zero Threshold: "+zeroThreshold);
        int iterations=0, removals=0;
        for (Iterator<Peak> iterator = peakCandidates.iterator(); iterator.hasNext();) {
            iterations++;
            Peak p = iterator.next();
            if (p.value<zeroThresholds[p.coordinate] && p.value>-zeroThresholds[p.coordinate] ||
                    (p.type==Peak.LOWER && p.value>zeroThresholds[p.coordinate])||
                    (p.type==Peak.UPPER && p.value<zeroThresholds[p.coordinate])) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
                removals++;
            }
        }
        Log2.log(2,this,"Removals: "+removals+", Iterations: "+iterations);



        WriteHelper.writeToFile(peakCandidates.toArray(new Peak[peakCandidates.size()]), "a3_"+DataOperations.colorCode(colorCode)+"_Validate_Zero(Final)");


        return peakCandidates;
    }
}
