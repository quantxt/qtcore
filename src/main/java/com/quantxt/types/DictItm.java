package com.quantxt.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DictItm implements Serializable {
    private static final long serialVersionUID = -1878032088113144067L;

    private String key;
    private List<String> value;

    public DictItm(String k, List<String> v){
        key = k;
        value = v;
    }

    public String getKey(){
        return key;
    }

    public List<String> getValue(){
        return value;
    }

    public void setKey(String key){
        this.key = key;
    }

    public void setValue(List<String> value){
        this.value = value;
    }

    public DictItm(String k, String v1, String... values){
        key = k;
        value = new ArrayList<>();
        value.add(v1);
        for (String v : values){
            value.add(v);
        }
    }

}
