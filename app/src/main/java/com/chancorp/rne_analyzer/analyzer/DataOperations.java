package com.chancorp.rne_analyzer.analyzer;

import com.chancorp.rne_analyzer.data.Pixel;

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
    public static double stdDeviation(double[] dat, int from, int to){
        double avg=average(dat);
        double varianceSum=0;
        for (int i = from; i <= to; i++) {
            varianceSum+=Math.pow(avg - dat[i], 2);
        }
        return Math.sqrt(varianceSum/dat.length);
    }
    public static double[] localizedStdDeviation(double[] dat, int size){
        double[] res=new double[dat.length];
        int start, end;
        for (int i = 0; i < res.length; i++) {
            start=i-size;
            if (start<0) start=0;

            end=i+size;
            if (end>=dat.length) end=dat.length-1;

            res[i]=stdDeviation(dat,start,end);
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
