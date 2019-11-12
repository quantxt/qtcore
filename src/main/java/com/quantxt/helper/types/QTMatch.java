package com.quantxt.helper.types;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class QTMatch {
    private String group;
    private String keyword;
    private String customData;
    private int start;
    private int end;
    private float score;

    public QTMatch(){

    }

    public QTMatch(int s, int e, String k){
        this.start = s;
        this.end = e;
        this.keyword = k;
    }
}
