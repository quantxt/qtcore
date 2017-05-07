package com.quantxt.helper;

import com.quantxt.types.MapSort;
import org.joda.time.DateTime;
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
    private final static HashMap<Pattern, int[]> DATE_PATTERN_MAP = new HashMap<>();
    final private static String DATE_SEPARATOR_STR = "(?:[\\@\\.\\s,\\-\\/\\\\\\|\\&;]+|$)";
    final private static String MONTH_NAME_STR   = "(Jan|Feb|Mar|Apr|May|June|Jul|Aug|Sep|Oct|Nov|Dec)(?:[a-zA-Z]*)";
    final private static String DAY_STR = "([0123][0-9]|[1-9])";
    final private static String MONTH_STR = "([01][0-9]|[1-9])";
    final private static String YEAR_STR = "([12]\\d{3})";

    static {
        DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + MONTH_NAME_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2});
        DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + DAY_STR + DATE_SEPARATOR_STR + MONTH_NAME_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 2, 1});
        DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2});
        DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + YEAR_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + "(?:T|\\s|\\b)"), new int[]{1, 2, 3});
        DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + DAY_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + YEAR_STR + "(?:T|\\s|\\b)"), new int[]{3, 2, 1});
    }

    private static DateTimeParser[] DATE_PARSER = {
            DateTimeFormat.forPattern("yyyy MMM dd").getParser(),
            DateTimeFormat.forPattern("yyyy MM dd").getParser(),
            DateTimeFormat.forPattern("yyyy dd MM").getParser()
    };

    private static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder().append( null, DATE_PARSER ).toFormatter();
    private static DateTimeFormatter DATE_STR_FORMATTER = DateTimeFormat.forPattern( "yyyy-MM-dd");

    private static DateTimeParser[] date_and_time_parsers = {
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss.SSSZ").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ssZ").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss z").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ssz").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mmZ").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm a z").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm a").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm z").getParser(),
            DateTimeFormat.forPattern( "yyyy-MM-dd").getParser()
    };

    private static DateTimeParser[] date_parsers = {
            DateTimeFormat.forPattern( "yyyy-MM-dd").getParser()
    };

    private static DateTimeFormatter date_time_formatter = new DateTimeFormatterBuilder().append( null, date_and_time_parsers ).toFormatter();
    private static DateTimeFormatter date_formatter = new DateTimeFormatterBuilder().append( null, date_parsers ).toFormatter();


    private DateTime date;
    private boolean hasTime;
    private int length;
    private int textLength;
    private int pos;

    private DateResolver(DateTime dt, int length, boolean ht){
        date = dt;
        this.length = length;
        hasTime = ht;
    }

    public static DateTime resolveDate(Document doc) {
        DateTime date;
        Elements timeElem = doc.getElementsByTag("time");
        if (timeElem != null && timeElem.size() > 0) {
            for (Attribute attr : timeElem.first().attributes()) {
                date = findDate(attr.getValue());
                if (date != null) return date;
            }
            date = findDate(timeElem.text());
            if (date != null) return date;

        }
        // get the earliest date when pasre the attributes

        Elements elements = doc.body().select("*");

        for(Element element : elements )
        {
            for (Attribute attr : element.attributes()){
                String text = attr.getValue();
                if (text == null || text.isEmpty() || text.length() > 85 || text.split("\\s+").length > 9) continue;
                date = findDate(text);
                if (date != null) return date;
            }
        }

        date = findDate(doc);
        if (date == null){
            logger.debug("Date was not found");
        }
        return date;
    }

    public DateResolver(String date_str) {
        date = findDate(date_str);
    }

    private static DateResolver normalizeDateStr(String date_string,
                                      Matcher m,
                                      int [] vals)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(m.group(vals[0]))
                .append(" ")
                .append(m.group(vals[1]))
                .append(" ")
                .append(m.group(vals[2]));
        DateTime justDate = DATE_FORMATTER.parseDateTime(sb.toString());
        String date_corrected_str = date_string.replaceAll("^.*?" + m.group(), DATE_STR_FORMATTER.print(justDate)+ " ");
        String date_corrected_str2 = m.replaceAll(DATE_STR_FORMATTER.print(justDate));

        date_corrected_str = date_corrected_str.replaceAll("\\-([1-9])00$" , "\\-0" + "$1" + "00"); // for correcting time zone -500 --> 0500
        date_corrected_str = date_corrected_str.replace("AT" , "EST");
        date_corrected_str = date_corrected_str.replace("ET" , "EST");
        date_corrected_str = date_corrected_str.replace("PT" , "PST");
        date_corrected_str = date_corrected_str.replace("MT" , "MST");
        date_corrected_str = date_corrected_str.replace("CT" , "CST");
        date_corrected_str = date_corrected_str.replaceAll("\\s+at\\s+" , " ");
        date_corrected_str = date_corrected_str.replaceAll("(\\d+)\\s+\\-" , "$1\\-");
        date_corrected_str = date_corrected_str.trim();

        try {
            DateTime date_time = date_time_formatter.parseDateTime(date_corrected_str);
            return new DateResolver(date_time, date_corrected_str.length(), true);
        } catch (Exception exp) {
            try {
                DateTime date_time = date_formatter.parseDateTime(date_corrected_str);
                return new DateResolver(date_time, date_corrected_str.length(), false);
            } catch (Exception e){
                logger.debug("Time is not valid " + e);
            }

        }
        return null;
    }

    private  static DateTime findDate(String date_string){
        if (date_string == null || date_string.length() > 85 || date_string.split("\\s+").length > 9) return null;
        List<DateResolver> allDates = new ArrayList<>();
        for (Map.Entry<Pattern, int[]> e : DATE_PATTERN_MAP.entrySet()) {
            Pattern p = e.getKey();
            Matcher m = p.matcher(date_string);
            if (m.find()) {
                DateResolver dr = normalizeDateStr(date_string, m, e.getValue());
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
            double score = (double) dr.length / (double) dr.textLength
                    *(double) 1 / dr.pos;
            scores.put(dr.date, score);
        }
        Map<DateTime, Double> sorted = MapSort.sortdescByValue(scores);
        Map.Entry<DateTime, Double> entry = sorted.entrySet().iterator().next();

        return entry.getKey();
    }

    private static DateTime findDate(Document doc){
        List<DateResolver> allDates = new ArrayList<>();
        for (Map.Entry<Pattern, int[]> e : DATE_PATTERN_MAP.entrySet()) {
            Pattern p =e.getKey();
            Elements elems = doc.getElementsMatchingOwnText(p);
            if (elems == null || elems.size() == 0) continue;
            for (int i=0; i <elems.size(); i++) {
                Element elem = elems.get(i);
                String date_string = elem.text();
                Matcher m = p.matcher(date_string);
                if (m.find()) {
                    DateResolver dr = normalizeDateStr(date_string, m, e.getValue());
                    if (dr == null) continue;
                    dr.textLength = date_string.length();
                    dr.pos = Math.min(i + 1 , elems.size() - i + 1);
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

        Document doc = Jsoup.connect("a_web_url").get();
        DateTime dt =  DateResolver.resolveDate(doc);
        logger.info("date: " + dt);
    }
}
