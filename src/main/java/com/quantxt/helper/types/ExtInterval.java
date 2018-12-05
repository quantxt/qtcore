package com.quantxt.helper.types;

import com.quantxt.interval.Interval;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by matin on 5/20/17.
 */
public class ExtInterval extends Interval {

    public enum ExtType {VERB, NOUN, MONEY, NUMBER, DATE, PERCENT}

    private ExtType type;
    private ArrayList<ArrayList<String>> key;  // so each ey can be a series of string.. category --> sub category -- >name
    private Double numbervalue;
    private Integer intvalue;
    private DateTime datevalue;
    private Object customData;

    public ExtInterval(int start, int end) {
        super(start, end);
        key = new ArrayList<>();
    }

    public void setType(ExtType t){
        type = t;
    }

    public ExtType getType(){
        return type;
    }

    public void setCustomData(Object o){customData = o;}

    public Object getCustomData(){
        return customData;
    }

    public void addKey(String s){
        ArrayList<String> arr = new ArrayList<>();
        arr.add(s);
        key.add(arr);
    }

    public void addKey(ArrayList<String> arr){
        key.add(arr);
    }

    public void setNumbervalue(double d){
        numbervalue = d;
    }

    public void setIntvalue(int s){
        intvalue = s;
    }

    public void setDatevalue(DateTime s){
        datevalue = s;
    }

    public ArrayList<ArrayList<String>> getKey(){
        return key;
    }

    public Double getNumbervalue(){
        return numbervalue;
    }

    public Integer getIntvalue(){
        return intvalue;
    }

    public DateTime getDatevalue(){
        return datevalue;
    }

    public String toString(String str){
        return str.substring(start, end);
    }

}
