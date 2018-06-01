package com.quantxt.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quantxt.types.MapSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantxt.helper.types.ExtInterval;

public class StringUtil {

    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public static boolean isEmpty(String value) {
        return value == null || value.replaceAll("\\s*", "").equals("");
    }

    public static String removePrnts(String str) {
        str = str.replaceAll("\\([^\\)]+\\)", " ");
        str = str.replaceAll("([\\.])+$", " $1");
        str = str.replaceAll("\\s+", " ");
        str = str.replaceAll("[”“]", "\"");
        return str;
    }

    private static ExtInterval findSpanHelper(String str,
                                              int startPos,
                                              List<String> tokenList)
    {
        if (tokenList.size() == 0) {
            return null;
        }
        int strLength = str.length();
        int end = startPos;
        for (int c = 0; c < tokenList.size(); c++) {
            String t = tokenList.get(c);
            int pos = str.indexOf(t, end);
            if (pos < 0) {
                return null;
            }
            end = pos + t.length();
            if (end > strLength) return null;
        }

        return new ExtInterval(startPos, end);
    }

    public static ExtInterval findSpan(String str, List<String> tokenList) {
        if (tokenList == null || tokenList.size() == 0)
            return null;

        Map<ExtInterval, Integer> allMatahces = new HashMap<>();
        String firstToken = tokenList.get(0);
        int cursor = 0;
        while (cursor >=0){
            int pos = str.indexOf(firstToken, cursor);
            if (pos < 0) break;
            ExtInterval ext = findSpanHelper(str, pos, tokenList);
            if (ext != null){
                allMatahces.put(ext, ext.getEnd() - ext.getStart());
            }
            cursor = pos + firstToken.length();
        }
        //find shortest ..
        // if there multiple shorts then we pick the last one (the one closer to the end of utterance)
        Map<ExtInterval, Integer> sorted = MapSort.sortByValue(allMatahces);
        int shortestLnegth = sorted.entrySet().iterator().next().getValue();
        ExtInterval res = sorted.entrySet().iterator().next().getKey();

        for (Map.Entry<ExtInterval, Integer> e : sorted.entrySet()){
            int length = e.getValue();
            if (length != shortestLnegth) break;
            ExtInterval key = e.getKey();
            if (key.getStart() > res.getStart()){
                res = key;
            }
        }

        return res;

    }
}
