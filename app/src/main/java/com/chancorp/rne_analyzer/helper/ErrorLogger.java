package com.chancorp.rne_analyzer.helper;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ErrorLogger {
    public static void log(Exception e){

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        Log.e("CS_RnE","Error Handled:\n"+sw.toString());

    }
}
