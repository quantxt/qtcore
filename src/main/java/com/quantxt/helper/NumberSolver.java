package com.quantxt.helper;

import com.quantxt.interval.Interval;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 10/7/18.
 */
public class NumberSolver {

    private static Pattern NUMBER  = Pattern.compile("([\\s+|^](\\d[,\\.\\d]*)|(\\(\\d[,\\d]*\\)))(?:\\s*(hundred|thousand|million|billion|M|B)[\\s,\\.;]+)?");

    public ArrayList<Interval> resolveNumber(String str){

        ArrayList<Interval> numbers = new ArrayList<>();
        Matcher m = NUMBER.matcher(str);
        while (m.find()){
            numbers.add(new Interval(m.start(), m.end()));
        }
        return numbers;
    }
}
