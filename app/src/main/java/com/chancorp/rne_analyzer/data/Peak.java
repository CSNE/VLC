package com.chancorp.rne_analyzer.data;

import com.chancorp.rne_analyzer.helper.Printable;

/**
 * Created by Chan on 3/30/2016.
 */
public class Peak implements Printable {
    public static final int LOWER=54;
    public static final int UPPER =92;

    public int type;
    public int coordinate;
    public double value;
    public Peak(int type, int coordinate, double value){
        this.type=type;
        this.coordinate=coordinate;
        this.value=value;
    }


    @Override
    public String debugPrint() {
        return ""+type+"\t"+coordinate+"\t"+value+"\n";
    }

    @Override
    public String toString(){
        return debugPrint();
    }
}
