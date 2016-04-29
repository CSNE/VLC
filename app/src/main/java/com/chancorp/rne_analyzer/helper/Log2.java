package com.chancorp.rne_analyzer.helper;


import android.util.Log;

import java.util.ArrayList;

//Logging Convenience Class
public class Log2 {
    public interface LogListener{
        void log(String s);
    }
    public static final String LOG_TAG="CS_RnE";
    public static void log(int level,Object callingClass,Object... arguments){
        if (callingClass!=null) log(level,callingClass.getClass(), arguments);
        else log(level,null, arguments);
    }
    static ArrayList<LogListener> lls=new ArrayList<>();
    public static void addLogListener(LogListener ll){
        lls.add(ll);
    }
    private static void notifyLogListeners(String s){
        for(LogListener ll:lls){
            ll.log(s);
        }
    }
    public static void removeLogListener(LogListener ll){
        lls.remove(ll);
    }
    public static void log(int level,Class callingClass,Object... arguments){
        StringBuilder log=new StringBuilder();
        log.append("[From ");
        if (callingClass!=null) log.append(callingClass.getSimpleName());
        else log.append("NULL");
        log.append("]");
        for (Object arg:arguments){
            log.append(" | ");
            try {
                log.append(arg.toString());
            }catch(NullPointerException e){
                log.append("NULL");
            }
        }

        switch (level){
            case 0:
                Log.v(LOG_TAG, log.toString());
                notifyLogListeners("[VERBOSE] "+log.toString());
                break;
            case 1:
                Log.d(LOG_TAG,log.toString());
                notifyLogListeners("[DEBUG] "+log.toString());
                break;
            case 2:
                Log.i(LOG_TAG,log.toString());
                notifyLogListeners("[INFORMATION] "+log.toString());
                break;
            case 3:
                Log.w(LOG_TAG,log.toString());
                notifyLogListeners("[WARNING] "+log.toString());
                break;
            case 4:
                Log.e(LOG_TAG,log.toString());
                notifyLogListeners("[ERROR] "+log.toString());
                break;
        }
    }
}

