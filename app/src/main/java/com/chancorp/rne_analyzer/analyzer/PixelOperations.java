package com.chancorp.rne_analyzer.analyzer;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.chancorp.rne_analyzer.data.Pixel;
import com.chancorp.rne_analyzer.helper.Log2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PixelOperations {

    public static Pixel[] averageRows(Bitmap bmp){
        int w=bmp.getWidth(),
                h=bmp.getHeight();


        Pixel[] pixels =new Pixel[h];

        double r,g,b;
        int clr;
        int[] data=new int[w*h];
        bmp.getPixels(data, 0, w, 0, 0, w, h);
        bmp=null;
        for (int y = 0; y < h; y++) {

            r=0;
            g=0;
            b=0;
            for (int x = 0; x < w; x++) {
                //if (x%5!=0) continue;
                //clr=bmp.getPixel(x,y);
                clr=data[x + y * w];
                r+= Color.red(clr)/255.0;
                g+=Color.green(clr)/255.0;
                b+=Color.blue(clr)/255.0;
            }

            pixels[y]=new Pixel((r/(double)w),(g/(double)w),(b/(double)w));
        }
        bmp=null;
        return pixels;
    }
    public static Pixel[] transformColorSpace(Pixel[] px){
        Log2.log(1,PixelOperations.class,"Transforming Color Space...");
        Pixel[] res=new Pixel[px.length];
        for (int i = 0; i < px.length; i++) {
            res[i]=px[i].toLinear();
        }
        return res;
    }
    
    public static Pixel[] lowPass(Pixel[] px, int lowpassSize){
        Log2.log(1,PixelOperations.class,"Lowpass Filtering...",lowpassSize, px.length);

        Pixel[] res=new Pixel[px.length];
        Pixel current;
        for (int i = 0; i < px.length; i++) {
            current=new Pixel();
            int num=0;
            for (int j = i-lowpassSize; j <= i+lowpassSize; j++) {
                if(j<0 || j>=px.length){
                    continue;
                }
                else{
                    current=current.add(px[j]);
                    num+=1;
                }
            }
            res[i]=current.div(num);
        }
        return res;
    }

    public static Pixel[] lowPassOptimized(Pixel[] px, int lowpassSize){ //TODO
        Log2.log(1,PixelOperations.class,"Lowpass Filtering...",lowpassSize, px.length);

        //Fast Rolling Average

        Pixel[] res=new Pixel[px.length];
        Pixel current=new Pixel();
        int pixelsAdded=0;

        //Initial Data
        for (int i = 0; i < lowpassSize; i++) {
            current.addSelf(px[i]);
            pixelsAdded++;
        }


        for (int i = 0; i < px.length; i++) {
            try{
                current.addSelf(px[i+lowpassSize]);
                pixelsAdded++;
            }catch(ArrayIndexOutOfBoundsException e){}

            try{
                current.subSelf(px[i-lowpassSize]);
                pixelsAdded--;
            }catch(ArrayIndexOutOfBoundsException e){}
            //Log2.log(1,PixelOperations.class,i,pixelsAdded);
            res[i]=current.div(pixelsAdded);
        }
        return res;
    }

    //This expects a SORTED array.... keep that in mind.
    private static double median(List<Double> values){
        if (values.size()%2==0){ //Even... Average the centermost two/.
            return (values.get(values.size()/2)+values.get(values.size()/2-1))/2.0;

        }else{
            return values.get((values.size()-1)/2);
        }
    }


    public static Pixel[] medianFilter(Pixel[] px, int medianRange){
        Log2.log(1,PixelOperations.class,"Median Filtering...",medianRange, px.length);

        Pixel[] res=new Pixel[px.length];

        ArrayList<Double> rVals=new ArrayList<>();
        ArrayList<Double> gVals=new ArrayList<>();
        ArrayList<Double> bVals=new ArrayList<>();

        for (int i = 0; i < px.length; i++) {
            //Log2.log(0,PixelOperations.class,"On Pixel", i);
            rVals.clear();
            gVals.clear();
            bVals.clear();


            for (int j = i-medianRange; j <= i+medianRange; j++) {
                if(j<0 || j>=px.length){
                    continue;
                }
                else{
                    //Log2.log(0,PixelOperations.class,"Adding Pixel", j, px[j].b,rVals.size());
                    rVals.add(px[j].r);
                    gVals.add(px[j].g);
                    bVals.add(px[j].b);
                }
            }

            Collections.sort(rVals);
            Collections.sort(gVals);
            Collections.sort(bVals);

            res[i]= new Pixel(median(rVals),median(gVals),median(bVals));

            //Log2.log(0,PixelOperations.class,"Results", res[i].b);
            //Log2.log(0,PixelOperations.class,"res",res[i].r);
        }
        return res;
    }

    public static Pixel[] copy(Pixel[] px){
        Pixel[] res=new Pixel[px.length];
        for (int i = 0; i < px.length; i++) {
            res[i]=new Pixel(px[i]);
        }
        return res;
    }

    public static Pixel[] derivative(Pixel[] px){
        Log2.log(1,PixelOperations.class,"Calculating Derivative...");

        Pixel[] res=new Pixel[px.length-1];
        Pixel current;
        for (int i = 0; i < px.length-1; i++) {
            res[i]=px[i].sub(px[i+1]);
        }
        return res;
    }

    public static Pixel[] subtractPixels(Pixel[] from, Pixel[] subtract){
        Log2.log(1,PixelOperations.class,"Subtracting Pixels...");
        assert from.length==subtract.length;
        Pixel[] res=new Pixel[from.length];
        for (int i = 0; i < from.length; i++) {
            res[i]=from[i].sub(subtract[i]);
        }
        return res;
    }

}
