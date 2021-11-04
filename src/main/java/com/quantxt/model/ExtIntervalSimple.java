package com.quantxt.model;

import java.time.LocalDateTime;

public class ExtIntervalSimple extends Interval {

    private QTField.DataType type;

    private Double doubleValue;
    private Long intValue;
    private LocalDateTime datetimeValue;

    public ExtIntervalSimple(){
        super();
    }

    public ExtIntervalSimple(int start, int end){
        super(start, end);
    }

    public String toString(String str){
        return str.substring(start, end);
    }

    public QTField.DataType getType() { return type; }
    public Double getDoubleValue(){return doubleValue;}
    public Long getIntValue(){return intValue;}
    public LocalDateTime getDatetimeValue(){return datetimeValue;}

    public void setType(QTField.DataType type){
        this.type = type;
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
}
