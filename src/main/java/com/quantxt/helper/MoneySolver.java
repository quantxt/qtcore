package com.quantxt.helper;

import com.quantxt.interval.Interval;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 10/7/18.
 */
public class MoneySolver {

    private static Pattern MONEY   = Pattern.compile("([\\s+|^](\\$|\\p{Sc})\\s*((\\d[,\\.\\d]*)|(\\(\\d[,\\.\\d]*\\))))(?:\\s*(hundred|thousand|million|billion|M|B)[\\s,\\.;]+)?");

    public ArrayList<Interval> resolveMoney(String str){

        ArrayList<Interval> monies = new ArrayList<>();
        Matcher m = MONEY.matcher(str);
        while (m.find()){
            monies.add(new Interval(m.start(), m.end()));
        }
        return monies;
    }

}
