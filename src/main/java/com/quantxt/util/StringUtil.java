package com.quantxt.util;

import java.util.ArrayList;
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

        int end = str.length();
        while (str.length() >= end) {
            int start = str.substring(0, end).lastIndexOf(tokenList.get(0));
            if (start == -1) {
                logger.error("no first match");
                return null;
            }
            end = start;
            int shift = start;
            for (int c = 0; c < tokenList.size(); c++) {
                String t = tokenList.get(c);
    //            String str2search = str.substring(shift);
                int n_start = str.indexOf(t, shift);
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
                } else {
                    shift = 0;
                    break;
                }
            }
            if (shift > 0) {
                return new ExtInterval(start, shift);
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String str = "Gilead Sciences Company Profile Gilead Sciences, Inc. "
                + "is a research-based biopharmaceutical company that discovers, "
                + "develops and commercializes medicines in areas of unmet medical need .";
        List<String> tokenlist = new ArrayList<>();
        tokenlist.add("medicines");
        tokenlist.add("in");
        tokenlist.add("areas");
        tokenlist.add("of");
        tokenlist.add("unmet");
        ExtInterval ex = findSpan(str, tokenlist);
        logger.info(str.substring(ex.getStart(), ex.getEnd()));

        tokenlist = new ArrayList<>();
        tokenlist.add("Gilead");
        tokenlist.add("Sciences");
        tokenlist.add("Company");
        tokenlist.add("Profile");
        tokenlist.add("Gilead");
        tokenlist.add("Sciences");


        ex = findSpan(str, tokenlist);
        logger.info(str.substring(ex.getStart(), ex.getEnd()));
    }

}
