package com.quantxt.helper;

import com.quantxt.interval.Interval;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 10/7/18.
 */
public class PercentSolver {

    private static Pattern PERCENT = Pattern.compile("[\\.\\d]+\\%");

    public ArrayList<Interval> resolvePercent(String str){

        ArrayList<Interval> percents = new ArrayList<>();
        Matcher m = PERCENT.matcher(str);
        while (m.find()){
            percents.add(new Interval(m.start(), m.end()));
        }
        return percents;
    }
}
