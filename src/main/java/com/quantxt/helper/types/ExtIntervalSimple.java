package com.quantxt.helper.types;

import com.quantxt.interval.Interval;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ExtIntervalSimple extends Interval {

    private QTField.QTFieldType type;
    private Double doubleValue;
    private Float floatValue;
    private Long intValue;
    private LocalDateTime datetimeValue;
    private String stringValue;
    private Short shortValue;
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

}
