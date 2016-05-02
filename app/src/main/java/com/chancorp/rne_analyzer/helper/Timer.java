package com.chancorp.rne_analyzer.helper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chan on 4/30/2016.
 */
public class Timer {
    static HashMap<String,Long> values=new HashMap<>();
    static ArrayList<String> results=new ArrayList<>();

    public static void startTimer(String name){
        values.put(name,System.currentTimeMillis());
    }

    public static void endTimer(String name){
        long start=values.remove(name);
        long elapsed=System.currentTimeMillis()-start;
        String elapsedStr=String.format("%.3f", elapsed/1000.0f);

        results.add(elapsedStr+"sec. ["+name+"]");
    }

    public static String dumpResults(){
        StringBuilder sb=new StringBuilder();
        for (String res:results) {
            sb.append(res);
            sb.append("\n");
        }
        results.clear();
        return sb.toString();
    }

}
