package com.chancorp.rne_analyzer.data;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Chan on 3/29/2016.
 */
public class Block {

    public static class CustomComparator implements Comparator<Block> {
        @Override // negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
        public int compare(Block o1, Block o2) {

            if (o1.timeCaptured<o2.timeCaptured) return -1;
            else if (o1.timeCaptured==o2.timeCaptured) return 0;
            else return 1;
        }
    }


    Bits raw;
    Bits blockInfor,data,parity; //Don't modifiy this.

    long timeCaptured;


    public Block(Bits raw, long timeCaptured){
        this.raw=raw;
        this.blockInfor=raw.subBits(0,2);
        this.data=raw.subBits(2,raw.getSize()-2);
        this.parity=raw.subBits(raw.getSize()-2,raw.getSize());

        this.timeCaptured=timeCaptured;
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
    public boolean isSingleBlockMessage(){
        return getBlockInformation().getBitAt(0) && getBlockInformation().getBitAt(1);
    }
    public boolean isOddNumberedBlock(){
        return getBlockInformation().getBitAt(1);
    }



    public boolean verify(){
        int evenSum=0, oddSum=0;
        boolean checkFailed=false;
        boolean evenParity, oddParity;

        Bits body=new Bits(blockInfor);
        body.append(data);

        for (int i = 0; i < body.getSize(); i++) {
            if (i%2==0){ //Even
                evenSum+=body.getBitAt(i)?1:0;
            }else{
                oddSum+=body.getBitAt(i)?1:0;
            }
        }

        evenParity=evenSum%2!=0;
        oddParity=oddSum%2!=0;

        if (parity.getBitAt(0) != evenParity){ //Even Parity
           return false;
        }
        if (parity.getBitAt(1) != oddParity){ //Even Parity
            return false;
        }



        return true;
    }
}
