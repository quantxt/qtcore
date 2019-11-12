package com.quantxt.types;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DictItm implements Serializable {
    private static final long serialVersionUID = -1878032088113144067L;

    private String key;
    private List<String> value;

    public DictItm(String k, List<String> v){
        key = k;
        value = v;
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
