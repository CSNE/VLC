package com.chancorp.rne_analyzer.helper;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Chan on 3/30/2016.
 */
public class WriteHelper {

    public static void writeToFile(Printable[] dat, Context c, String name) {
        File file = new File(Environment.getExternalStorageDirectory(),name+".txt");
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
    }



}
