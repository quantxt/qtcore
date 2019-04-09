package com.quantxt.helper.types;

import com.quantxt.interval.Interval;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by matin on 5/20/17.
 */

@Getter
@Setter
public class ExtInterval {

    private String key;
    private String keyGroup;
    private ArrayList<ExtIntervalSimple> extIntervalSimples;

    public ExtInterval() {

    }

    /*
    public String toString(String str){
        return str.substring(start, end);
    }
    */
}
