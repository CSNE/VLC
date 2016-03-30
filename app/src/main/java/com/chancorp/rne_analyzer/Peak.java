package com.chancorp.rne_analyzer;

/**
 * Created by Chan on 3/30/2016.
 */
public class Peak implements Printable{
    public static final int RISING=54;
    public static final int FALLING=92;

    int type;
    int coordinate;
    double value;
    public Peak(int type, int coordinate, double value){
        this.type=type;
        this.coordinate=coordinate;
        this.value=value;
    }


    @Override
    public String debugPrint() {
        return ""+type+"\t"+coordinate+"\t"+value;
    }

}
