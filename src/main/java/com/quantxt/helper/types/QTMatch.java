package com.quantxt.helper.types;

import com.quantxt.interval.Interval;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class QTMatch extends Interval {
    private String group;
    private String keyword;
    private String customData;
    private float score;

    public QTMatch(int start, int end) {
        super(start, end);

    }

    public QTMatch(int s, int e, String k){
        super(s, e);
        this.keyword = k;
    }
}
