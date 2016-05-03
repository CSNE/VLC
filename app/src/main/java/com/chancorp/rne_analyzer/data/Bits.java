package com.chancorp.rne_analyzer.data;

import com.chancorp.rne_analyzer.helper.Log2;
import com.chancorp.rne_analyzer.helper.Printable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

//I probably should have used BitSet but nah
public class Bits implements Printable{
    List<Boolean> bits;
    public Bits(List<Boolean> bits){
        this.bits =new ArrayList<>(bits);
    }
    public Bits(){
        this.bits=new ArrayList<>();
    }

    public void append(List<Boolean> bits){
        this.bits.addAll(bits);
    }
    public void append(Bits bits){
        append(bits.bits);
    }

    public Bits subBits(int start, int end){
        return new Bits(bits.subList(start,end));
    }

    public int getSize(){
        return bits.size();
    }
    public boolean getBitAt(int i){
        return bits.get(i);
    }

    public byte[] toByteArray() {
        byte[] toReturn = new byte[bits.size() / 8];
        for (int entry = 0; entry < toReturn.length; entry++) {
            for (int bit = 0; bit < 8; bit++) {
                if (bits.get(entry * 8 + bit)) {
                    toReturn[entry] |= (128 >> bit);
                }
            }
        }

        return toReturn;
    }

    public String decodeString(){
        try {
            return new String(toByteArray(), "UTF-8");
        }catch (UnsupportedEncodingException e){
            Log2.log(4,this,"Decode failed due to UnsupportedEncodingException");
            return "";
        }
    }

    @Override
    public String debugPrint() {
        StringBuilder res=new StringBuilder();
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i)) res.append("1");
            else res.append("0");
        }
        res.append("\n");
        return res.toString();
    }

    @Override
    public String toString(){
        StringBuilder res=new StringBuilder();
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i)) res.append("1");
            else res.append("0");
        }
        return res.toString();
    }

    public String toSplitString(){
        StringBuilder res=new StringBuilder();
        for (int i = 0; i < bits.size(); i++) {
            if (i!=0 && i%8==0) res.append("\n");
            if (bits.get(i)) res.append("1");
            else res.append("0");
        }
        return res.toString();
    }
}
