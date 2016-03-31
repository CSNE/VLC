package com.chancorp.rne_analyzer.analyzer;

import android.content.Context;

import com.chancorp.rne_analyzer.data.PeakBlock;
import com.chancorp.rne_analyzer.data.PeakGroup;
import com.chancorp.rne_analyzer.data.Peak;
import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.WriteHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Receives a list of peaks, and makes it into a single PeakBlock.
public class PeakAnalyzer {
    List<Peak> peaks;
    List<PeakGroup> groups;
    List<PeakBlock> blocks;
    Context c;

    public PeakAnalyzer(List<Peak> peaks, Context c){
        this.peaks=peaks;
        this.c=c;
    }
    public void group(){
        Log2.log(1, this, "Grouping Peaks...");
        groups=new ArrayList<>();
        PeakGroup peakGroup =null;
        for (int i = 0; i < peaks.size()-1; i++) {
            if (peakGroup ==null) peakGroup =new PeakGroup();
            
            peakGroup.addPeak(peaks.get(i));
            
            if (peaks.get(i+1).type==Peak.LOWER && peaks.get(i).type==Peak.UPPER){
                groups.add(peakGroup);
                peakGroup =null;
            }
        }
        WriteHelper.writeToFile(groups.toArray(new PeakGroup[groups.size()]),c,"p1_Grouped");
    }

    public void seperateBlocks(double blockDelayThresholdInPixels){
        Log2.log(1, this, "Seperating Blocks...");
        blocks=new ArrayList<>();
        PeakBlock peakBlock=null;

        for (int i = 0; i < groups.size()-1; i++) {
            if (peakBlock ==null) peakBlock =new PeakBlock();

            peakBlock.addPeakGroup(groups.get(i));

            if ((groups.get(i+1).getStartPixel()-groups.get(i).getEndPixel())>blockDelayThresholdInPixels){
                blocks.add(peakBlock);
                peakBlock =null;
            }
        }
        WriteHelper.writeToFile(blocks.toArray(new PeakBlock[blocks.size()]),c,"p2_Blocks");
    }

    public PeakBlock trim(){
        int maxBlockSize=0;
        for (PeakBlock pb : blocks){
            if (pb.getBlockSize()>maxBlockSize) maxBlockSize=pb.getBlockSize();
        }


        for (Iterator<PeakBlock> iterator = blocks.iterator(); iterator.hasNext();) {
            PeakBlock pb = iterator.next();
            if (pb.getBlockSize()<maxBlockSize) {
                // Remove the current element from the iterator and the list.
                iterator.remove();
            }
        }

        WriteHelper.writeToFile(blocks.toArray(new PeakBlock[blocks.size()]),c,"p3_ValidBlocks");
        return blocks.get(0);

    }
}
