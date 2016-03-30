package com.chancorp.rne_analyzer;

/**
 * Created by Chan on 3/28/2016.
 */
public class Pixel {
    float r,g,b;
    public Pixel(float r, float g, float b){
        this.r=r;
        this.g=g;
        this.b=b;
    }
    public Pixel(){
        this.r=0;
        this.g=0;
        this.b=0;
    }
    public void toGamma(){
        this.r=toGamma(r);
        this.g=toGamma(g);
        this.b=toGamma(b);
    }
    public void toLinear(){
        this.r=toLinear(r);
        this.g=toLinear(g);
        this.b=toLinear(b);
    }
    private float toGamma(float val){
        if (val<0.0031308) return (float)Math.pow(12.92,val);
        else return 1.055f*(float)Math.pow(val,(1/2.4))-0.055f;

    }
    private float toLinear(float val){
        if (val<=0.04045) return val/12.92f;
        else return (float)Math.pow(((val+0.055)/1.055),2.4);
    }
    public Pixel add(float r, float g,float b){
        return new Pixel(this.r+r,this.b+b,this.g+g);
        /*
        this.r+=r;
        this.g+=g;
        this.b+=b;*/
    }
    public Pixel add(Pixel p){
        return new Pixel(this.r+p.r,this.b+p.b,this.g+p.g);
        /*
        this.r+=p.r;
        this.g+=p.g;
        this.b+=p.b;*/
    }
    public Pixel div(float n){
        return new Pixel(this.r/n,this.b/n,this.g/n);
        /*
        this.r/=n;
        this.g/=n;
        this.b/=n;*/
    }
    public Pixel sub(Pixel p){
        return new Pixel(this.r-p.r,this.b-p.b,this.g-p.g);
        /*
        this.r-=p.r;
        this.g-=p.g;
        this.b-=p.b;*/
    }
    public String toFileRepr(){
        return ""+this.r+"\t"+this.g+"\t"+this.b;
    }
    public float avg(){
        return (r+g+b)/3.0f;
    }

}
/*
class Pixel():
    def __init__(self,r=0,g=0,b=0):
        self.r=r
        self.g=g
        self.b=b

    def __add__(self, right):
        if type(right) is not Pixel:
            raise TypeError("Pixel can only be added with another Pixel!")
        return Pixel(self.r+right.r,self.g+right.g,self.b+right.b)
    def __iadd__(self, right):
        if type(right) is not Pixel:
            raise TypeError("Pixel can only be added with another Pixel!")
        self.r+=right.r
        self.g+=right.g
        self.b+=right.b
        return self
    def __sub__(self, right):
        if type(right) is not Pixel:
            raise TypeError("Pixel can only be subtracted with another Pixel!")
        return Pixel(self.r-right.r,self.g-right.g,self.b-right.b)
    def __mul__(self, right):
        if (type(right) is float) or (type(right) is int):
            return Pixel(self.r*right,self.g*right,self.b*right)
        elif (type(right) is Pixel):
            return Pixel(self.r*right.r,self.g*right.g,self.b*right.b)
        else:
            raise TypeError("Pixel can only be multiplied with a number or another Pixel!")
    def __truediv__(self, right):
        if (type(right) is float) or (type(right) is int):
            return Pixel(self.r/right,self.g/right,self.b/right)
        else:
            raise TypeError("Pixel can only be divided with a number!")


    def to_linear(self,val):
        if val<=0.04045:
            return val/12.92
        else:
            return ((val+0.055)/1.055)**2.4
    def to_gamma(self,val):
        if val<=0.0031308:
            return 12.92*val
        else:
            return 1.055*val**(1/2.4)-0.055
    def convert_to_linear(self):
        self.r=self.to_linear(self.r)
        self.g=self.to_linear(self.g)
        self.b=self.to_linear(self.b)
    def convert_to_gamma(self,rgb):
        self.r=self.to_gamma(self.r)
        self.g=self.to_gamma(self.g)
        self.b=self.to_gamma(self.b)
    def to_float(self,rgb):
        return (rgb[0]/255,rgb[1]/255,rgb[2]/255)
    def luminosity(self):
        return (self.r+self.g+self.b)/3
    def to_string(self):
        return str(self.r)+"\t"+str(self.g)+"\t"+str(self.b)
    def from_string(self,s):
        ss=s.split("\t")
        self.r=float(ss[0])
        self.g=float(ss[1])
        self.b=float(ss[2])
 */
