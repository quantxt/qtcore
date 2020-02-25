package com.quantxt.helper.types;

import com.quantxt.interval.Interval;

import java.util.ArrayList;

/**
 * Created by matin on 5/20/17.
 */

public class ExtInterval extends Interval {

    private String key;
    private String keyGroup;
    private ArrayList<ExtIntervalSimple> extIntervalSimples;

    public ExtInterval() {
        super();
    }

    public ExtInterval(int start, int end){
        super(start, end);
    }

    public String getKey(){
        return key;
    }

    public String getKeyGroup(){
        return keyGroup;
    }

    public ArrayList<ExtIntervalSimple> getExtIntervalSimples(){
        return extIntervalSimples;
    }

    public void setKey(String key){
        this.key = key;
    }

    public void setKeyGroup(String keyGroup){
        this.keyGroup = keyGroup;
    }

    public void setExtIntervalSimples(ArrayList<ExtIntervalSimple> extIntervalSimples){
        this.extIntervalSimples = extIntervalSimples;
    }
}
