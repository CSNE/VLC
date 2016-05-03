package com.chancorp.rne_analyzer.helper;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by Chan on 3/30/2016.
 */
public class WriteHelper {
    public interface AsyncListener {
        void asyncStarted();

        void asyncEnded();
    }

    static int asyncCount = 0;
    static AsyncListener al;

    public static void setAsyncListener(AsyncListener al_) {
        al = al_;
    }

    static void startAsync() {
        if (asyncCount == 0) al.asyncStarted();
        asyncCount++;
    }

    static void endAsync() {
        asyncCount--;
        if (asyncCount == 0) al.asyncEnded();
    }


    static boolean skip = false;

    public static void setSkip(boolean s) {
        skip = s;
    }


    private static void writeToFile(String dat, String name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RnE/" + name + ".txt");
        //if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(out);
            pw.print(dat);
            pw.flush();
            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    private static void oldWriteToFile(Printable[] dat, String name) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/RnE/"+name+".txt");
        //if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            final PrintStream printStream = new PrintStream(out);
            for (int i = 0; i < dat.length; i++) {
                printStream.print(""+i+"\t"+dat[i].debugPrint());
            }
            printStream.flush();
            printStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void writeToFileAsyncFast(double[] dat, String name, boolean symmetric) {
        double[] saveData = new double[dat.length];

    }

    private static void forceWriteToFileAsync(Printable[] dat, String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dat.length; i++) {
            sb.append(i).append("\t").append(dat[i].debugPrint());
        }

        writeToFileAsync(sb.toString(), name);
    }

    public static void writeToFileAsync(Printable[] dat, String name) {
        if (!skip) forceWriteToFileAsync(dat, name);
    }

    private static void forceWriteToFileAsync(double[] dat, String name, boolean symmetric) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dat.length; i++) {
            if (!symmetric) {
                sb.append(i).append("\t").append(dat[i]).append("\n");
            } else {
                sb.append(i).append("\t").append(dat[i]).append("\t").append(-dat[i]).append("\n");
            }
        }

        writeToFileAsync(sb.toString(), name);
    }

    public static void writeToFileAsync(double[] dat, String name, boolean symmetric) {
        if (!skip) forceWriteToFileAsync(dat, name, symmetric);
    }

    /*
        private static void writeToFileAsync(Printable[] dat, String name) {
            final Printable[] dat_=dat;
            final String name_=name;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    writeToFileAsync(dat_,name_);
                }
            }).start();
        }
    */
    private static void forceWriteToFileAsync(String dat, String name) {
        final String dat_ = dat;
        final String name_ = name;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startAsync();
                writeToFile(dat_, name_);
                endAsync();
            }
        }).start();
    }

    public static void writeToFileAsync(String dat, String name) {
        if (!skip) forceWriteToFileAsync(dat, name);

    }

    private static void forceWriteToFileAsync(Printable dat, String name) {

        writeToFileAsync(new Printable[]{dat}, name);
    }

    public static void writeToFileAsync(Printable dat, String name) {
        if (!skip) forceWriteToFileAsync(dat, name);
    }


}
