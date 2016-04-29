package com.chancorp.rne_analyzer.data;

import java.util.List;

/**
 * Created by Chan on 3/29/2016.
 */
public class Block {
    boolean[] data;
    boolean[] blockFlags;
    boolean[] parity;
    public Block(List<Boolean> data){

    }
    public boolean verify(){
        return false;
    }
}
