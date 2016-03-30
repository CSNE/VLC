package com.chancorp.rne_analyzer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Chan on 3/30/2016.
 */
public class Analyzer {
    Pixel[] averaged, gCorrected, lowPassed, derivative, derivativeLowpass, derivativeLowpassSubtracted;
    public Analyzer(Bitmap bmp){
        averaged=PixelOperations.feedData(bmp);
    }
    public void prepareData(){
        gCorrected=PixelOperations.transformColorSpace(averaged);
        lowPassed=PixelOperations.lowPass(gCorrected, 3);
        derivative=PixelOperations.derivative(lowPassed);
        derivativeLowpass=PixelOperations.lowPass(derivative, 300);
        derivativeLowpassSubtracted=PixelOperations.subtractPixels(derivative, derivativeLowpass);
    }
    public void logData(Context c){
        WriteHelper.writeToFile(averaged,c,"1_Averaged");
        WriteHelper.writeToFile(gCorrected,c,"2_GammaCorrected");
        WriteHelper.writeToFile(lowPassed,c,"3_Lowpass");
        WriteHelper.writeToFile(derivative,c,"4_Derivative");
        WriteHelper.writeToFile(derivativeLowpass,c,"5_DerivativeLowpass");
        WriteHelper.writeToFile(derivativeLowpassSubtracted,c,"6_DerivativeLowpassSubtracted");
    }
    public void peakAnalyze(){

    }
}
