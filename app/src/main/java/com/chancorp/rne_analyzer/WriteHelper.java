package com.chancorp.rne_analyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

/**
 * Created by Chan on 3/30/2016.
 */
public class WriteHelper {

    public static void writeToFile(Pixel[] dat, Context c, String name) {
        File file = new File(Environment.getExternalStorageDirectory(),name);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            final PrintStream printStream = new PrintStream(out);
            for (int i = 0; i < dat.length; i++) {
                printStream.println(dat[i].toFileRepr());
            }
            printStream.flush();
            printStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
