package com.quantxt.types;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DictItm {
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
