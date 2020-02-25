package com.quantxt.helper.types;

import com.quantxt.interval.Interval;
import com.quantxt.helper.types.QTField.QTFieldType;
import java.time.LocalDateTime;

public class ExtIntervalSimple extends Interval {

    private QTFieldType type;
    private Double doubleValue;
    private Long intValue;
    private LocalDateTime datetimeValue;
    private String stringValue;
    private String customData;

    public ExtIntervalSimple(){
        super();
    }

    public ExtIntervalSimple(int start, int end){
        super(start, end);
    }

    public String toString(String str){
        return str.substring(start, end);
    }

    public QTFieldType getType() { return type; }
    public Double getDoubleValue(){return doubleValue;}
    public Long getIntValue(){return intValue;}
    public LocalDateTime getDatetimeValue(){return datetimeValue;}
    public String getStringValue(){return stringValue;}
    public String getCustomData(){return customData;}

    public void setType(QTFieldType type){
        this.type = type;
    }

    public void setCustomData(String customData) {
        this.customData = customData;
    }

    public void setDatetimeValue(LocalDateTime datetimeValue) {
        this.datetimeValue = datetimeValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public void setIntValue(Long intValue) {
        this.intValue = intValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
