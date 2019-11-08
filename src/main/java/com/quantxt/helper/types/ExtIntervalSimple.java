package com.quantxt.helper.types;

import com.quantxt.interval.Interval;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;


@Getter
@Setter
public class ExtIntervalSimple extends Interval {

    private QTField.QTFieldType type;
    private Double doubleValue;
    private Float floatValue;
    private Integer intValue;
    private DateTime datetimeValue;
    private String stringValue;
    private Short shortValue;
    private Object customData;

    public ExtIntervalSimple(int start, int end) {
        super(start, end);

    }


    public String toString(String str){
        return str.substring(start, end);
    }

}
