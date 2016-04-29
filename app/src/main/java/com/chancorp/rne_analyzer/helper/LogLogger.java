package com.chancorp.rne_analyzer.helper;

/**
 * Created by Chan on 4/28/2016.
 */
public class LogLogger implements Log2.LogListener {
    StringBuilder sb=new StringBuilder();
    @Override
    public void log(String s) {
        sb.append(s);
        sb.append("\n");
    }
    public String flush(){
        return sb.toString();
    }
}
