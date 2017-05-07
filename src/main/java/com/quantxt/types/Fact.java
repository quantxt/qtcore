package com.quantxt.types;

import java.util.Objects;

/**
 * Created by matin on 4/1/17.
 */
public class Fact {
    private String key;
    private Object val;

    public Fact(String k, Object v){
        key = k;
        val = v;
    }

    public String getKey(){
        return key;
    }

    public Object getVal(){
        return val;
    }
}
