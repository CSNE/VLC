package com.chancorp.rne_analyzer.data;

import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.Printable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chan on 3/31/2016.
 */
public class PeakGroup implements Printable{
    List<Peak> peaks=new ArrayList<>();
    public PeakGroup(){

    }
    public void addPeak(Peak p){
        peaks.add(p);
    }
    public int getEndPixel(){
        int endPixel=-1;
        for (Peak peak:peaks){
            if (peak.coordinate>endPixel) endPixel=peak.coordinate;
        }
        return endPixel;
    }
    public int getStartPixel(){
        int startPixel=100000;
        for (Peak peak:peaks){
            if (peak.coordinate<startPixel) startPixel=peak.coordinate;
        }
        return startPixel;
    }

    public boolean verifySymmetry(){
        int upper=0, lower=0;
        for (Peak p:peaks) {
           if (p.type==Peak.UPPER) {
               upper++;
           }else if (p.type==Peak.LOWER){
               lower++;
           }else{
               Log2.log(4,this,"Invalid Peak Type!");
           }
        }
        return upper==lower;
    }

    private boolean peakExistsInRange(double startCoords, double endCoords){
        for(Peak p:peaks){
            if (p.coordinate<endCoords && p.coordinate>startCoords){
                return true;
            }
        }
        return false;
    }

    //Assumes peaks[0] is a aligner bit.
    private boolean[] getData(List<Peak> peaks, int bitsPerGroup, double bitSpacing){
        double alignerCoords=peaks.get(0).coordinate;
        for (int i = 1; i < bitsPerGroup; i++) {
            
        }
    }

    //BitsPerGroup Includes the aligner bit.
    //bitSpacing: target bit spacing in pixels.
    public boolean[] getData(int bitsPerGroup, double bitSpacing){
        List<Peak> lowerPeaks=new ArrayList<>();
        List<Peak> upperPeaks=new ArrayList<>();


        boolean[] res=new boolean[bitsPerGroup-1];


    }

    @Override
    public String debugPrint() {
        StringBuilder res=new StringBuilder();
        res.append("Group:\n");
        for(Peak p:peaks){
            res.append("  ");
            res.append(p.debugPrint());
        }
        return res.toString();
    }
}
