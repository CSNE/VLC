package com.chancorp.rne_analyzer.analyzer;

import android.graphics.Bitmap;

import com.chancorp.rne_analyzer.data.Peak;
import com.chancorp.rne_analyzer.data.Pixel;
import com.chancorp.rne_analyzer.helper.EC;
import com.chancorp.rne_analyzer.helper.Timer;
import com.chancorp.rne_analyzer.helper.WriteHelper;
import com.chancorp.rne_analyzer.helper.Log2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Receives a Bitmap and makes a list of peaks.
public class ImageAnalyzer {
    Pixel[] averaged, gCorrected, lowPassed, derivative,
            derivativeLowpass,derivativeLowpassOpti, derivativeLowpassSubtracted, derivativeLowpassSubtractedMedian;


    public ImageAnalyzer(Pixel[] averaged){
        this.averaged=averaged;
    }

    public void prepareData(){
        Log2.log(1,this,"Prepping data...");
        Timer.startTimer("Gamma");
        gCorrected=PixelOperations.transformColorSpace(averaged);
        Timer.endTimer("Gamma");
        Timer.startTimer("Lowpass");
        Log2.log(1,this,"Gamma Correction done.");
        lowPassed=PixelOperations.lowPass(gCorrected, EC.LOWPASS_SIZE);
        Timer.endTimer("Lowpass");
        Timer.startTimer("Derivative");
        Log2.log(1,this,"Lowpass done.");
        derivative=PixelOperations.derivative(lowPassed);
        Timer.endTimer("Derivative");
        Timer.startTimer("DerivativeLowpass");
        Log2.log(1,this,"Derivative done.");
        //derivativeLowpass=PixelOperations.lowPass(derivative, EC.DERIVATIVE_LOWPASS);
        derivativeLowpass=PixelOperations.lowPassOptimized(derivative, EC.DERIVATIVE_LOWPASS);
        //derivativeLowpass=PixelOperations.copy(derivative);
        Timer.endTimer("DerivativeLowpass");
        Timer.startTimer("Subtract");
        Log2.log(1,this,"Derivative Lowpass done.");
        derivativeLowpassSubtracted=PixelOperations.subtractPixels(derivative, derivativeLowpass);
        Timer.endTimer("Subtract");
        Timer.startTimer("Median");
        Log2.log(1,this,"Lowpass Subtraction done.");
        derivativeLowpassSubtractedMedian=PixelOperations.medianFilter(derivativeLowpassSubtracted,1);
        Timer.endTimer("Median");
        Log2.log(1,this,"Median done.");
    }
    public void logData(){
        Timer.startTimer("Writing Data");
        Log2.log(1,this,"Writing...");
        WriteHelper.writeToFileAsync(averaged, "1_Averaged");
        WriteHelper.writeToFileAsync(gCorrected,"2_GammaCorrected");
        WriteHelper.writeToFileAsync(lowPassed,"3_Lowpass");
        WriteHelper.writeToFileAsync(derivative,"4_Derivative");
        WriteHelper.writeToFileAsync(derivativeLowpass,"5_DerivativeLowpass");
        //WriteHelper.writeToFileAsync(derivativeLowpassOpti,"tmp_DerivativeLowpassOptimized");
        WriteHelper.writeToFileAsync(derivativeLowpassSubtracted,"6_DerivativeLowpassSubtracted");
        WriteHelper.writeToFileAsync(derivativeLowpassSubtractedMedian,"7_DerivativeLowpassSubtractedMedian");
        Log2.log(1, this, "Written.");
        Timer.endTimer("Writing Data");
    }


    public List<Peak> peakAnalyze(int colorCode){
        Log2.log(1,this,"Starting Peak Analysis...");
        Timer.startTimer("PixelToDouble"+DataOperations.colorCode(colorCode));
        double[] dat=DataOperations.fromPixels(derivativeLowpassSubtractedMedian,colorCode);
        Timer.endTimer("PixelToDouble"+DataOperations.colorCode(colorCode));
        Timer.startTimer("StdDeviation"+DataOperations.colorCode(colorCode));
        //double stdDeviation=DataOperations.stdDeviation(dat);
        double[] stdDeviations=DataOperations.localizedStdDeviation(dat,EC.STD_DEVIATION_RANGE,true);
        //Log2.log(2, this, "SDev: " + stdDeviation);
        Timer.endTimer("StdDeviation"+DataOperations.colorCode(colorCode));
        Timer.startTimer("Get peak candidates"+DataOperations.colorCode(colorCode));

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
        Timer.endTimer("Get peak candidates"+DataOperations.colorCode(colorCode));
        Timer.startTimer("Write peak candidates"+DataOperations.colorCode(colorCode));
        WriteHelper.writeToFileAsync(peakCandidates.toArray(new Peak[peakCandidates.size()]), "a1_"+DataOperations.colorCode(colorCode)+"_PeakCandidates");
        Timer.endTimer("Write peak candidates"+DataOperations.colorCode(colorCode));
        Timer.startTimer("Validate Close"+DataOperations.colorCode(colorCode));


        //double closeThreshold=stdDeviation*EC.TOO_CLOSE;
        double[] closeThresholds=DataOperations.multiply(stdDeviations,EC.TOO_CLOSE);
        WriteHelper.writeToFileAsync(closeThresholds,"t1_"+DataOperations.colorCode(colorCode)+"_CloseThresholds",true);
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


        Timer.endTimer("Validate Close"+DataOperations.colorCode(colorCode));
        Timer.startTimer("Validate Zero"+DataOperations.colorCode(colorCode));
        WriteHelper.writeToFileAsync(peakCandidates.toArray(new Peak[peakCandidates.size()]), "a2_"+DataOperations.colorCode(colorCode)+"_Validate_Close");
        Log2.log(1, this, "Peak Analysis Done.");



        //double zeroThreshold=stdDeviation*EC.TOO_ZERO;
        double[] zeroThresholds=DataOperations.multiply(stdDeviations,EC.TOO_ZERO);
        WriteHelper.writeToFileAsync(zeroThresholds,"t2_"+DataOperations.colorCode(colorCode)+"_ZeroThresholds",true);
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


        Timer.endTimer("Validate Zero"+DataOperations.colorCode(colorCode));
        WriteHelper.writeToFileAsync(peakCandidates.toArray(new Peak[peakCandidates.size()]), "a3_"+DataOperations.colorCode(colorCode)+"_Validate_Zero(Final)");


        return peakCandidates;
    }
}
