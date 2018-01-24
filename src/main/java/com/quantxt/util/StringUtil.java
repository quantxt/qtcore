package com.quantxt.util;

import java.util.List;

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
        return str;
    }

    public static ExtInterval findSpan(String str, List<String> tokenList) {
        if (tokenList == null || tokenList.size() == 0)
            return null;

        int shift = 0;
        int end = str.length();
        while (str.length() >= end) {
            // String str_part = str.substring(0, end);
            int start = str.substring(0, end).lastIndexOf(tokenList.get(0));
            if (start == -1) {
                logger.error("no first match");
                return null;
            }
            end = start;

            for (int c = 0; c < tokenList.size(); c++) {
                String t = tokenList.get(c);
                // int t_length = t.length();
                int n_start = str.indexOf(t, start);
                if (n_start >= 0) {
                    // str = str.substring(n_start);
                    int sh = str.indexOf(' ', n_start);
                    if (sh > 0) {
                        shift = sh;
                    } else if (sh == -1 && (c == tokenList.size() - 1)) {
                        shift = str.length();
                    } else {
                        // wrong token
                        shift = 0;
                        break;
                    }
                }
            }
            if (shift > 0) {
                return new ExtInterval(start, shift);
            }
        }
        return null;
    }

}
