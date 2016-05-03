package com.chancorp.rne_analyzer.analyzer;

import com.chancorp.rne_analyzer.data.Pixel;
import com.chancorp.rne_analyzer.helper.Log2;

/**
 * Created by Chan on 3/30/2016.
 */
public class DataOperations {
    public static final int RED=1;
    public static final int GREEN=2;
    public static final int BLUE=3;
    public static final int AVERAGE=4;
    public static double[] fromPixels(Pixel[] px,int channel){
        double[] res=new double[px.length];
        for (int i = 0; i < px.length; i++) {
            if (channel==RED) res[i]=px[i].r;
            else if (channel==GREEN) res[i]=px[i].g;
            else if (channel==BLUE) res[i]=px[i].b;
            else if (channel==AVERAGE) res[i]=px[i].avg();
        }
        return res;
    }
    public static double average(double[] dat){
        double res=0;
        for (int i = 0; i < dat.length; i++) {
            res+=dat[i];
        }
        return (res/dat.length);
    }
    public static double stdDeviation(double[] dat){
        double avg=average(dat);
        double varianceSum=0;
        for (int i = 0; i < dat.length; i++) {
            varianceSum+=Math.pow(avg - dat[i], 2);
        }
        return Math.sqrt(varianceSum/dat.length);
    }
    public static String colorCode(int type){
        if (type==RED) return "R";
        else if (type==GREEN) return "G";
        else if (type==BLUE) return "B";
        else if (type==AVERAGE) return "AVG";
        else return "?";
    }
    private static double stdDeviation(double[] dat, int from, int to){
        double avg=average(dat);
        double varianceSum=0;
        for (int i = from; i <= to; i++) {
            varianceSum+=Math.pow(avg - dat[i], 2);
        }
        return Math.sqrt(varianceSum/dat.length);
    }
    public static double[] localizedStdDeviation(double[] dat, int size, boolean normalize){
        double[] res=new double[dat.length];
        int start, end;
        for (int i = 0; i < res.length; i++) {
            start=i-size;
            if (start<0) start=0;

            end=i+size;
            if (end>=dat.length) end=dat.length-1;

            res[i]=stdDeviation(dat,start,end)*2*size/(end-start);
        }
        return res;
    }
    public static Pixel[] lowPassOptimized(Pixel[] px, int lowpassSize){
        Log2.log(1,PixelOperations.class,"Lowpass Filtering...",lowpassSize, px.length);

        //Fast Rolling Average

        Pixel[] res=new Pixel[px.length];
        Pixel current=new Pixel();
        int pixelsAdded=0;

        //Initial Data
        for (int i = 0; i < lowpassSize; i++) {
            current.addSelf(px[i]);
            pixelsAdded++;
        }


        for (int i = 0; i < px.length; i++) {
            try{
                current.addSelf(px[i+lowpassSize]);
                pixelsAdded++;
            }catch(ArrayIndexOutOfBoundsException e){}

            try{
                current.subSelf(px[i-lowpassSize]);
                pixelsAdded--;
            }catch(ArrayIndexOutOfBoundsException e){}
            //Log2.log(1,PixelOperations.class,i,pixelsAdded);
            res[i]=current.div(pixelsAdded);
        }
        return res;
    }
    public static double[] localizedStdDeviationFast(double[] dat, int size){
        double[] res=new double[dat.length];

        double varianceSum, pixelSum;
        int numSum=0;

        for (int i = 0; i < size; i++) {

        }

        for (int i = 0; i < res.length; i++) {

        }
        return res;
    }
    public static double[] multiply(double[] dat, double factor){
        double[] res=new double[dat.length];
        for (int i = 0; i < res.length; i++) {
            res[i]=dat[i]*factor;
        }
        return res;
    }
}
