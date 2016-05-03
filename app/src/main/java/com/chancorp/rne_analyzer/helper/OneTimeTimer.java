package com.chancorp.rne_analyzer.helper;

/**
 * Created by Chan on 5/3/2016.
 */
public class OneTimeTimer {
    static long start;
    public static void start(){
        start=System.currentTimeMillis();
    }
    public static long end(){
        return System.currentTimeMillis()-start;
    }
}
