package com.quantxt.helper.types;

import com.quantxt.interval.Interval;


public class QTMatch extends Interval {
    private String group;
    private String keyword;
    private String customData;
    private float score;

    public QTMatch(int start, int end) {
        super(start, end);

    }

    public float getScore() {
        return score;
    }

    public String getCustomData() {
        return customData;
    }

    public String getGroup() {
        return group;
    }

    public String getKeyword() {
        return keyword;
    }

    public QTMatch(int s, int e, String k){
        super(s, e);
        this.keyword = k;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
