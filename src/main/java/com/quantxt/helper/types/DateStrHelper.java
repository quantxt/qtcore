package com.quantxt.helper.types;

import java.util.regex.Pattern;

/**
 * Created by matin on 3/23/18.
 */
public class DateStrHelper {
    public Pattern pattern;
    public int[] digits;

    public DateStrHelper(Pattern p , int [] d){
        pattern = p;
        digits = d;
    }
}
