package com.quantxt.types;


/**
 * Created by matin on 4/6/17.
 */

@Deprecated
public class BaseNameAlts<T>  {
    private String name;
    private double score;
    private String [] alts;
    private T data;

    public BaseNameAlts(String n, String [] a){
        name = n;
        alts = a;
    }

    public BaseNameAlts(String n, String [] a, T d){
        name = n;
        alts = a;
        data = d;
    }

    public void setScore(double s){
        score = s;
    }

    public double getScore(){
        return score;
    }

    public String getName(){return name;}
    public String [] getAlts(){return alts;}

    public T getData(){return data;}
    public void setData(T o){data = o;}

}
