package com.chancorp.rne_analyzer.data;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Chan on 3/29/2016.
 */
public class BlockArray {

    public static class UndecodableBlockException extends Exception{
        public UndecodableBlockException(String s){
            super(s);
        }

    }
    ArrayList<Block> data=new ArrayList<>();
    public BlockArray(){

    }

    public int getSize(){
        return data.size();
    }

    public boolean addBlock(Block b){ //Returns if the block added is a new block.

        try{
            if(this.data.get(this.data.size()-1).getBlockInformation().equals(b.getBlockInformation())) { //Same block
                return false;
            }
        }catch(IndexOutOfBoundsException e ){

        }

        this.data.add(b);

        Collections.sort(this.data,new Block.CustomComparator());

        return true;



        /*
        try{
            if(this.data.get(this.data.size()-1).isOddNumberedBlock()!=this.data.get(this.data.size()-2).isOddNumberedBlock()){
                return true;
            }else if (this.data.get(this.data.size()-1).isFirstBlock()!=this.data.get(this.data.size()-2).isFirstBlock()){
                return true;
            }else{
                return false;
            }
        }catch(IndexOutOfBoundsException e ){
            return true;
        }
        */
    }
    public Bits getFullBits() throws UndecodableBlockException{
        Bits res=new Bits();
        int startBlock=-1;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isFirstBlock()){
                startBlock=i;
                break;
            }
        }
        if (startBlock==-1) throw new UndecodableBlockException("There is no start block!");

        int i=startBlock;
        boolean blockNumberParity=!data.get(startBlock).isOddNumberedBlock();
        while (true) {
            if (data.get(i).isOddNumberedBlock()==blockNumberParity){ //Same parity >> duplicate!
                //pass
            }else{
                blockNumberParity=data.get(i).isOddNumberedBlock();

                res.append(data.get(i).getData());
            }

            i++;
            i=i%data.size();

            if (data.get(i).isFirstBlock()) break;
        }
        return res;
    }
    public void reset(){
        this.data.clear();
    }
}
