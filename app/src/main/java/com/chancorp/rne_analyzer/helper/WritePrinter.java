package com.chancorp.rne_analyzer.helper;

/**
 * Created by Chan on 4/23/2016.
 */
public class WritePrinter implements Printable{
    @Override
    public String debugPrint() {
        return sb.toString();
    }

    StringBuilder sb=new StringBuilder();

    public void writeln(String s){
        sb.append(s);
        sb.append("\n");
    }


}
