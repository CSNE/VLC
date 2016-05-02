package com.chancorp.rne_analyzer.helper;

public class EC { //Experiment Constants
    public static int LOWPASS_SIZE=3;
    public static int DERIVATIVE_LOWPASS=300;
    public static int BITS_PER_GROUP=4;
    public static double BIT_DELAY=0.13;
    public static double GROUP_DELAY=1;
    public static double BLOCK_DELAY=1.5;
    public static double ROLLING_TIME=31.9;
    public static int IMAGE_HEIGHT=2448;
    public static double TOO_CLOSE=0.5;
    public static double TOO_ZERO=1.5;
    public static int STD_DEVIATION_RANGE=500;
    public static double millisecToPixels(double millisec){
        return IMAGE_HEIGHT*(millisec/ROLLING_TIME);
    }
    public static double pixelsToMillisec(double pixels){
        return pixels/IMAGE_HEIGHT*ROLLING_TIME;
    }
}
