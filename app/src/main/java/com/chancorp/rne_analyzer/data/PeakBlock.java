package com.chancorp.rne_analyzer.data;

import com.chancorp.rne_analyzer.analyzer.DataOperations;
import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.Printable;
import com.chancorp.rne_analyzer.helper.WriteHelper;
import com.chancorp.rne_analyzer.helper.WritePrinter;

import java.util.ArrayList;
import java.util.List;

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

    public List<Boolean> getData(int bitsPerGroup, double bitSpacing, int colorCode){
        WritePrinter wp=new WritePrinter();
        Log2.log(1,this,"GetData called...");
        ArrayList<Boolean> dat=new ArrayList<>();

        for (int i = 0; i < groups.size(); i++) {
            dat.addAll(groups.get(i).getData(bitsPerGroup,bitSpacing,wp));
        }

        int n=0;
        while (dat.size()%8!=0){
            n++;
            dat.remove(dat.size()-1);
        }

        Log2.log(2,this,"Removed "+n+" trailing bits.");

        WriteHelper.writeToFileAsync(wp,"d1_"+ DataOperations.colorCode(colorCode)+"_Data_Acquisition");

        return dat;
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
