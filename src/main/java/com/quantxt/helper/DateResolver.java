package com.quantxt.helper;

import com.google.gson.internal.LinkedHashTreeMap;
import com.quantxt.helper.types.DateStrHelper;
import com.quantxt.types.MapSort;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 3/22/17.
 */
public class DateResolver {

    private static Logger logger = LoggerFactory.getLogger(DateResolver.class);
    private final static List<DateStrHelper> DATE_PATTERN_MAP = new ArrayList<>();
    final private static String DATE_SEPARATOR_STR = "(?:[\\@\\.\\s,\\-\\/\\\\\\|\\&;]+|$)";
    final private static String MONTH_NAME_STR   = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(?:[a-zA-Z]*)";
    final private static String DAY_STR = "([0123][0-9]|[1-9])";
    final private static String MONTH_STR = "([01][0-9]|[1-9])";
    final private static String YEAR_STR = "([12]\\d{3})";

    static {
 //       DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + MONTH_NAME_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2});
        /* try in this order
        Month/dd/YY
        MM/dd/YY
        YY/MM/dd
        dd/Month/yy
        dd/MM/yy
         */
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile(MONTH_NAME_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile(MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile(YEAR_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + "(?:T|\\s|\\b)"), new int[]{1, 2, 3}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile(DAY_STR + DATE_SEPARATOR_STR + MONTH_NAME_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 2, 1}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile(DAY_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + YEAR_STR + "(?:T|\\s|\\b)"), new int[]{3, 2, 1}));
    }

    private static DateTimeParser[] DATE_PARSER = {
            DateTimeFormat.forPattern("yyyy MMM dd").getParser(),
            DateTimeFormat.forPattern("yyyy MM dd").getParser()/*,
            DateTimeFormat.forPattern("yyyy dd MM").getParser()*/
    };

    private static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder().append( null, DATE_PARSER ).toFormatter();
    private static DateTimeFormatter DATE_STR_FORMATTER = DateTimeFormat.forPattern( "yyyy-MM-dd");

    private static DateTimeParser[] date_and_time_parsers = {
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSZ").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssZ").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss z").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ssz").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a z").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd hh:mm a").getParser(),

            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm z").getParser(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mmZ").getParser(),
    };

    private static DateTimeParser[] date_parsers = {
            DateTimeFormat.forPattern( "yyyy-MM-dd").getParser()
    };

    private static DateTimeFormatter date_time_formatter = new DateTimeFormatterBuilder().append( null, date_and_time_parsers ).toFormatter();
    private static DateTimeFormatter date_formatter = new DateTimeFormatterBuilder().append( null, date_parsers ).toFormatter();


    private DateTime date;
    private int length;
    private int textLength;
    private int pos;

    private DateResolver(DateTime dt, int length, boolean ht){
        date = dt;
        this.length = length;
    }

    private static String cleanTitle(String ttl){
        ttl = ttl.replaceAll("\\s+[\\|\\-]\\s+.*$", "");
        ttl = ttl.replaceAll("\\s+\\|\\s*$", "");
        return ttl;
    }

    public static DateTime resolveDate(Document doc) {
        DateTime date = resolveDateHelper(doc);
        if (date != null){
            date = date.withZone(DateTimeZone.UTC);
        }
        return date;
    }

    private static DateTime resolveDateHelper(Document doc) {
        DateTime date;
        // get the earliest date when pasre the attributes
        // but after the title!
        String title = cleanTitle(doc.title());
        Elements titleMatching  = doc.body().select("*:containsOwn(" + title + ")");
        List<Element> elements = doc.body().select("*");
        List<Element> afterTitle = new ArrayList<>();
        List<Element> beforeTitle = new ArrayList<>();
        if (titleMatching != null){
            for (Element matchingElem : titleMatching) {
                int matchedLevel = ArticleBodyResolver.getLevel(matchingElem);
                int levelLowRange = matchedLevel - 2;
                int levelHighRange = matchedLevel + 4;
                for (int i = 0; i < elements.size(); i++) {
                    Element e = elements.get(i);
                    if (!e.equals(matchingElem)) continue;
      //              int indexBefore = Math.max(0, i - 10);
      //              int indexAfter = Math.min(elements.size(), i + 100);
                    int numAdded = 0;
                    int index = i-1;
                    while (numAdded < 10 && index>=0){
                        Element eb = elements.get(index--);
                        int level = ArticleBodyResolver.getLevel(eb);
                        if (level > levelHighRange || level < levelLowRange) continue;
                        numAdded++;
                        beforeTitle.add(eb);
                    }
                    numAdded = 0;
                    index = i-1;
                    while (numAdded < 100 && index < elements.size()){
                        Element eb = elements.get(index++);
                        int level = ArticleBodyResolver.getLevel(eb);
              //          logger.info(level + " : " + eb.text());
                        if (level > levelHighRange || level < levelLowRange) continue;
                        numAdded++;
                        afterTitle.add(eb);
                    }
   //                 beforeTitle.addAll(elements.subList(indexBefore, i));
   //                 afterTitle.addAll(elements.subList(i, indexAfter));
                    break;
                }
            }
        }


        if (afterTitle.size() != 0){
            //we didn't find the title!
            elements = afterTitle;
        }


        for(Element element : elements ){
            if (!element.tagName().equals("time")) continue;
            for (Attribute attr : element.attributes()) {
                date = findDate(attr.getValue());
                if (date != null) return date;
            }
            date = findDate(element.text());
            if (date != null) return date;
        }

        for(Element element : elements )
        {
            for (Attribute attr : element.attributes()){
                String text = attr.getValue();
                if (text == null || text.isEmpty() ) continue;
                String [] parts = text.split("\\s+");
                for (String p :parts) {
                    if ( p.length() > 30) {
                        text = null;
                        break;
                    }
                }
                if (text == null) continue;
                date = findDate(text);
                if (date != null) {
         //           logger.info(element.cssSelector() + " " + attr + " " + text);
                    return date;
                }
            }
        }

        date = findDate(elements);
        // if date is null try before title elements:
        if (date == null) {
            date = findDate(beforeTitle);
        }
        if (date == null){
            logger.debug("Date was not found");
        }
        return date;
    }

    public DateResolver(String date_str) {
        date = findDate(date_str);
    }

    public static DateTime resolveDateStr(String date_str) {
        return findDate(date_str);
    }

    private static DateResolver normalizeDateStr(String date_string,
                                      Matcher m,
                                      int [] vals)
    {
        StringBuilder sb = new StringBuilder();
        // check the day and month numbers are valid
        if (vals[0] > 2060) return null;
        if (vals[1] > 12) return null;
        if (vals[2] > 31) return null;

        sb.append(m.group(vals[0]))
                .append(" ")
                .append(m.group(vals[1]))
                .append(" ")
                .append(m.group(vals[2]));
        DateTime justDate = null;
        try {
            justDate = DATE_FORMATTER.parseDateTime(sb.toString());
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        if (justDate == null) return null;

        String matched = m.group();
        int potentialTimeInString = date_string.indexOf(matched) + matched.length();
        String date_corrected_str = DATE_STR_FORMATTER.print(justDate)+ " " + date_string.substring(potentialTimeInString);
        //     String date_corrected_str = date_string.replaceAll("^.*?" + m.group(), DATE_STR_FORMATTER.print(justDate)+ " ");
   //     String date_corrected_str2 = m.replaceAll(DATE_STR_FORMATTER.print(justDate));

        date_corrected_str = date_corrected_str.replaceAll("\\-([1-9])00$" , "\\-0" + "$1" + "00"); // for correcting time zone -500 --> 0500
        date_corrected_str = date_corrected_str.replace("AT" , "EST");
        date_corrected_str = date_corrected_str.replace("ET" , "EST");
        date_corrected_str = date_corrected_str.replace("PT" , "PST");
        date_corrected_str = date_corrected_str.replace("MT" , "MST");
        date_corrected_str = date_corrected_str.replace("CT" , "CST");
        date_corrected_str = date_corrected_str.replaceAll("\\s+at\\s+" , " ");
        date_corrected_str = date_corrected_str.replaceAll("(\\d+)\\s+\\-" , "$1\\-");
        date_corrected_str = date_corrected_str.replace("CT" , "CST");
        date_corrected_str = date_corrected_str.replaceAll("(\\d+)\\s*(am|pm|AM|PM)\\s+(UTC|EST|PST|CST|MST)?.*$", "$1 $2 $3");
        // this is ba rule from here : http://giftedviz.com/2017/05/17/bank-of-england-holds-rates-in-7-1-vote/
        // 17 May 2017, 10:58 | Darnell Patrick
        date_corrected_str = date_corrected_str.replaceAll("\\|\\s+.*$", "");
        date_corrected_str = date_corrected_str.trim();

        try {
            DateTime date_time = date_time_formatter.parseDateTime(date_corrected_str);
            return new DateResolver(date_time, date_corrected_str.length(), true);
        } catch (Exception exp) {
            try {
                DateTime date_time = date_formatter.parseDateTime(date_corrected_str);
                // if we don't find the time zone set it to GMT
                date_time = date_time.withZoneRetainFields(DateTimeZone.UTC);
                return new DateResolver(date_time, date_corrected_str.length(), false);
            } catch (Exception e){
                logger.debug("Time is not valid " + e);
            }

        }
        return null;
   //     return new DateResolver(justDate, date_corrected_str.length(), false);
    }

    private  static DateTime findDate(String date_string){
        if (date_string == null || date_string.length() > 400 || date_string.split("\\s+").length > 20) return null;
        List<DateResolver> allDates = new ArrayList<>();
        for (DateStrHelper e : DATE_PATTERN_MAP) {
            Pattern p = e.pattern;
            Matcher m = p.matcher(date_string);
            if (m.find()) {
                DateResolver dr = normalizeDateStr(date_string, m, e.digits);
                if (dr == null) continue;
                dr.textLength = date_string.length();
                allDates.add(dr);
            }
        }
        if (allDates.size() == 0) {
            return null;
        }
        return getBestMatch(allDates);
    }

    private static DateTime getBestMatch(List<DateResolver> allDates){
        Map<DateTime, Double> scores = new HashMap<>();
        for (DateResolver dr : allDates){
   //         double score = (double) dr.length / (double) dr.textLength
   //                 *(double) 1 / dr.pos;
            scores.put(dr.date, (double) dr.pos);
        }
        Map<DateTime, Double> sorted = MapSort.sortByValue(scores);
        Map.Entry<DateTime, Double> entry = sorted.entrySet().iterator().next();

        return entry.getKey();
    }

    private static DateTime findDate(List<Element> elements){
        List<DateResolver> allDates = new ArrayList<>();
        for (int i=0; i <elements.size(); i++) {
            Element elem = elements.get(i);
            String date_string = elem.ownText();
            if (date_string == null || date_string.isEmpty()) continue;
   //         logger.info(date_string);
            for (DateStrHelper e : DATE_PATTERN_MAP) {
                Pattern p = e.pattern;
                Matcher m = p.matcher(date_string);
                if (m.find()) {
                    DateResolver dr = normalizeDateStr(date_string, m, e.digits);
                    if (dr == null) continue;
                    dr.textLength = date_string.length();
                    dr.pos = i;
                    allDates.add(dr);
                }
            }
        }
        if (allDates.size() == 0) {
            return null;
        }
        return getBestMatch(allDates);
    }

    public static void main(String[] args) throws Exception {
        String txt  = "FW: Interprint Inc; Morten Enterprises Inc - Wind Submission; Eff 7/15/2018";
        DateTime dt = DateResolver.resolveDateStr(txt);
        if (dt != null) {
            logger.info(dt.toString());
        } else {
            logger.info("Date was not found");
        }
    }
}
