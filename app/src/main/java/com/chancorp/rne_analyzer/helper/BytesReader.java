package com.chancorp.rne_analyzer.helper;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Chan on 4/23/2016.
 */
public class BytesReader {



        public static byte[] readContentIntoByteArray(File file)
        {
            FileInputStream fileInputStream = null;
            byte[] bFile = new byte[(int) file.length()];
            try
            {
                //convert file into array of bytes
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bFile);
                fileInputStream.close();
                for (int i = 0; i < bFile.length; i++)
                {
                    //System.out.print((char) bFile[i]);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return bFile;
        }

}
