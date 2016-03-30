package com.chancorp.rne_analyzer;

import android.graphics.Bitmap;
import android.graphics.Color;


public class PixelOperations {

    public static Pixel[] feedData(Bitmap bmp){
        int w=bmp.getWidth(),h=bmp.getHeight();


        Pixel[] pixels =new Pixel[h];

        double r,g,b;
        int clr;
        for (int y = 0; y < h; y++) {
            r=0;
            g=0;
            b=0;
            for (int x = 0; x < w; x++) {
                clr=bmp.getPixel(x,y);
                r+= Color.red(clr);
                g+=Color.green(clr);
                b+=Color.blue(clr);
            }

            pixels[y]=new Pixel((float)(r/w),(float)(g/w),(float)(b/w));
        }
        bmp=null;
        return pixels;
    }
    public static Pixel[] transformColorSpace(Pixel[] px){
        for(Pixel p:px){
            p.toLinear();
        }
        return px;
    }
    
    public static Pixel[] lowPass(Pixel[] px, int lowpassSize){
        Pixel[] res=new Pixel[px.length];
        Pixel current;
        for (int i = 0; i < px.length; i++) {
            current=new Pixel();
            int num=0;
            for (int j = i-lowpassSize; j <= i+lowpassSize; j++) {
                if(j<0 || j>=px.length) continue;
                else{
                    current=current.add(px[j]);
                }
            }
            res[i]=current.div(num);
        }
        return res;
    }

    public static Pixel[] derivative(Pixel[] px){
        Pixel[] res=new Pixel[px.length-1];
        Pixel current;
        for (int i = 0; i < px.length-1; i++) {
            res[i]=px[i].sub(px[i+1]);
        }
        return res;
    }

    public static Pixel[] subtractPixels(Pixel[] from, Pixel[] subtract){
        assert from.length==subtract.length;
        Pixel[] res=new Pixel[from.length];
        for (int i = 0; i < from.length; i++) {
            res[i]=from[i].sub(subtract[i]);
        }
        return res;
    }

}
