package com.chancorp.rne_analyzer.data;

import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.Printable;
import com.chancorp.rne_analyzer.helper.WritePrinter;

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
        Log2.log(0,this,"PeakExistsInRange",startCoords,endCoords,peaks.size());
        for(Peak p:peaks){
            if (p.coordinate<endCoords && p.coordinate>startCoords){
                return true;
            }
        }
        return false;
    }

    //Assumes peaks[0] is a aligner bit.
    private List<Boolean> getData(List<Peak> peaks, int bitsPerGroup, double bitSpacing){
        Log2.log(0,this,"      GetData called...", bitsPerGroup, bitSpacing,peaks.size());
        try {
            double alignerCoords = peaks.get(0).coordinate;

            List<Boolean> res = new ArrayList<>();
            for (int i = 1; i < bitsPerGroup; i++) {
                res.add(peakExistsInRange(alignerCoords + (i - 0.5) * bitSpacing, alignerCoords + (i + 0.5) * bitSpacing));
            }
            return res;
        }catch(IndexOutOfBoundsException e){
            Log2.log(4,this,"AIOOB : No Peaks in PeakGroup?");
            return new ArrayList<>();
        }


    }

    private boolean compareBoolList(List<Boolean> a,List<Boolean> b){
        if (a.size()!=b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i)!=b.get(i)) return false;
        }
        return true;
    }

    //BitsPerGroup Includes the aligner bit.
    //bitSpacing: target bit spacing in pixels.
    public List<Boolean> getData(int bitsPerGroup, double bitSpacing, WritePrinter wp){
        Log2.log(1,this,"   GetData called...", bitsPerGroup, bitSpacing);

        List<Peak> lowerPeaks=new ArrayList<>();
        List<Peak> upperPeaks=new ArrayList<>();

        for (int i = 0; i < peaks.size(); i++) {
            if (peaks.get(i).type==Peak.LOWER) lowerPeaks.add(peaks.get(i));
            else upperPeaks.add(peaks.get(i));
        }

        for (int i = 0; i < lowerPeaks.size(); i++) {
            wp.writeln(lowerPeaks.get(i).toString());
        }
        for (int i = 0; i < upperPeaks.size(); i++) {
            wp.writeln(upperPeaks.get(i).toString());
        }

        List<Boolean> lowerData=getData(lowerPeaks,bitsPerGroup,bitSpacing);
        List<Boolean> upperData=getData(upperPeaks,bitsPerGroup,bitSpacing);

        wp.writeln("Lowerdata "+new Bits(lowerData));
        wp.writeln("UpperData "+new Bits(upperData));
        wp.writeln("");

        if (!lowerData.equals(upperData)){
            Log2.log(3,this,"Lower Peaks and Upper Peaks mismatch. Returning the Upper Peaks.");
            Log2.log(3,this,debugPrint());
        }



        return upperData;


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
