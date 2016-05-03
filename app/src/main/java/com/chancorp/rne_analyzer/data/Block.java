package com.chancorp.rne_analyzer.data;

import java.util.List;

/**
 * Created by Chan on 3/29/2016.
 */
public class Block {
    Bits raw;
    Bits blockInfor,data,parity; //Don't modifiy this.
    public Block(Bits raw){
        this.raw=raw;
        this.blockInfor=raw.subBits(0,2);
        this.data=raw.subBits(2,raw.getSize()-2);
        this.parity=raw.subBits(raw.getSize()-2,raw.getSize());
    }

    public Bits getData(){
        return this.data;
    }
    public Bits getBlockInformation(){
        return this.blockInfor;
    }
    public Bits getParity(){
        return this.parity;
    }

    public boolean isFirstBlock(){
        return getBlockInformation().getBitAt(0);
    }
    public boolean isOddNumberedBlock(){
        return getBlockInformation().getBitAt(1);
    }



    public boolean verify(){
        return false;
    }
}
