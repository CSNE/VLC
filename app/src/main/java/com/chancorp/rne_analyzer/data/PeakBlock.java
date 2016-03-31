package com.chancorp.rne_analyzer.data;

import com.chancorp.rne_analyzer.helper.Printable;

import java.util.ArrayList;

/**
 * Created by Chan on 3/31/2016.
 */
public class PeakBlock implements Printable {
    ArrayList<PeakGroup> groups=new ArrayList<>();
    public PeakBlock(){

    }
    public void addPeakGroup(PeakGroup pg){
        groups.add(pg);
    }
    public int getBlockSize(){
        return groups.size();
    }

    public boolean verifySymmetry(){
        for(PeakGroup pg:groups){
            if (!pg.verifySymmetry()) return false;
        }
        return true;
    }

    public boolean[] getData(){
        ArrayList<boolean[]> dat=new ArrayList<>();
        for (int i = 0; i < groups.size(); i++) {
            dat.add(groups.get(i).parseData());
        }

        boolean[] res=new boolean[];
        for (int i = 0; i < dat.size(); i++) {

        }

        return res;
    }

    @Override
    public String debugPrint() {
        StringBuilder res=new StringBuilder();
        res.append("[[[Block]]]\n");
        for (PeakGroup pg:groups) {
            res.append(pg.debugPrint());
        }
        return res.toString();
    }
}
