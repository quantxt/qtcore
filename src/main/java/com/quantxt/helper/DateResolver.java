package com.quantxt.helper;

import com.quantxt.helper.types.DateStrHelper;
import com.quantxt.helper.types.ExtIntervalSimple;
import com.quantxt.helper.types.QTField;
import com.quantxt.types.MapSort;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 3/22/17.
 */

@Slf4j
public class DateResolver {

    final private static String EnglishShortMonths = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
    private final static List<DateStrHelper> DATE_PATTERN_MAP = new ArrayList<>();
    final private static String DATE_SEPARATOR_STR = "(?:[\\@\\.\\s,\\-\\/\\(\\)\\\\\\|\\&;]+|$)";
    final private static String MONTH_NAME_STR   = EnglishShortMonths +"(?:[a-zA-Z]*)";
    final private static String DAY_STR = "([0123][0-9]|[1-9])";
    final private static String MONTH_STR = "([01][0-9]|[1-9])";
    final private static String YEAR_STR = "([12]\\d{3})";   // 4 digit year
    final private static String YEAR_SHORT = "([01]\\d|[6789]\\d)";  // 2 digit year


    final private static Pattern MonthFormat = Pattern.compile(EnglishShortMonths, Pattern.CASE_INSENSITIVE);

    static {
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile("("+MONTH_NAME_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + ")" + DATE_SEPARATOR_STR, Pattern.CASE_INSENSITIVE), new int[]{4, 2, 3}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile("("+MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + ")" + DATE_SEPARATOR_STR, Pattern.CASE_INSENSITIVE), new int[]{4, 2, 3}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile("("+YEAR_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + ")" + "(?:T|\\s|\\b)"), new int[]{2, 3, 4}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile("("+DAY_STR + DATE_SEPARATOR_STR + MONTH_NAME_STR + DATE_SEPARATOR_STR + YEAR_STR + ")" + DATE_SEPARATOR_STR, Pattern.CASE_INSENSITIVE), new int[]{4, 3, 2}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile("("+DAY_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + YEAR_STR + ")" + "(?:T|\\s|\\b)"), new int[]{4, 3, 2}));
        DATE_PATTERN_MAP.add(new DateStrHelper(Pattern.compile("(?:^|[^\\d]+)("+MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_SHORT + ")" + DATE_SEPARATOR_STR, Pattern.CASE_INSENSITIVE), new int[]{4, 2, 3}));
    }

    private static DateTimeFormatter [] DATE_FORMATTER = new DateTimeFormatter []{
            DateTimeFormatter.ofPattern("yyyy MMM d"),
            DateTimeFormatter.ofPattern("yyyy M d")
    };

    private static DateTimeFormatter DATE_STR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static DateTimeFormatter [] date_time_formatter = new DateTimeFormatter[]{
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[X]"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS'Z'"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS'Z'"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss'Z'"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssz"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm X z"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a[ z]"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[X]"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm'Z'")
    };

    private static DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDateTime date;
    private int length;
    private int textLength;
    private int pos;

    private DateResolver(LocalDateTime dt, int length, boolean ht){
        date = dt;
        this.length = length;
    }

    private static String cleanTitle(String ttl){
        if (ttl == null) return null;
        ttl = ttl.replaceAll("\\s+[\\|\\-]\\s+.*$", "");
        ttl = ttl.replaceAll("\\s+\\|\\s*$", "");
        return ttl;
    }

    public static LocalDateTime resolveDate(Document doc) {
        LocalDateTime date = resolveDateHelper(doc);
    //    if (date != null){
    //        date = date.(ZoneOffset.UTC);
    //    }
        return date;
    }

    private static String encodeText(String text){
        text = text.replace("'", "\\'");
        return text;
    }

    private static LocalDateTime resolveDateHelper(Document doc) {
        LocalDateTime date;
        // get the earliest date when pasre the attributes
        // but after the title!
        String title = cleanTitle(doc.title());
        List<Element> afterTitle = new ArrayList<>();
        List<Element> beforeTitle = new ArrayList<>();
        List<Element> elements = doc.body().select("*");
        if (title != null && !title.isEmpty()) {
            Elements titleMatching = doc.body().select("*:containsOwn(" + encodeText(title) + ")");
            if (titleMatching != null) {
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
                        int index = i - 1;
                        while (numAdded < 10 && index >= 0) {
                            Element eb = elements.get(index--);
                            int level = ArticleBodyResolver.getLevel(eb);
                            if (level > levelHighRange || level < levelLowRange) continue;
                            numAdded++;
                            beforeTitle.add(eb);
                        }
                        numAdded = 0;
                        index = i - 1;
                        while (numAdded < 100 && index < elements.size()) {
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


            if (afterTitle.size() != 0) {
                //we didn't find the title!
                elements = afterTitle;
            }
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
            log.debug("Date was not found");
        }
        return date;
    }

    public DateResolver(String date_str) {
        date = findDate(date_str);
    }

    public static LocalDateTime resolveDateStr(String date_str) {
        return findDate(date_str);
    }

    private static DateResolver normalizeDateStr(String date_string,
                                      Matcher m,
                                      int [] vals)
    {
        StringBuilder jsutDateStr = new StringBuilder();
        // check the day and month numbers are valid
        //validate month
        String month = m.group(vals[1]);
        int year_int = 0;
        try {
            String year = m.group(vals[0]);
            year_int = Integer.parseInt(year);
            if (year_int > 2060) return null;
            if (year_int < 30){ //2030
                year_int += 2000;
            } else if (year_int > 30 && year_int < 100){
                year_int += 1900;
            }

            if (Integer.parseInt(month) > 12) return null;
            if (Integer.parseInt(m.group(vals[2])) > 31) return null;
        } catch (NumberFormatException ne){
            Matcher matcher = MonthFormat.matcher(month);
            if (!matcher.find()) return null;
        }

        if (year_int == 0) return null;

        jsutDateStr.append(year_int)
                .append(" ")
                .append(m.group(vals[1]))
                .append(" ")
                .append(m.group(vals[2]));
        LocalDateTime justDate = null;

        for (DateTimeFormatter dtf : DATE_FORMATTER) {
            try {
                TemporalAccessor temporalAccessor = dtf.parseBest(jsutDateStr.toString(), LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    justDate = (LocalDateTime) temporalAccessor;
                } else {
                    justDate = ((LocalDate) temporalAccessor).atStartOfDay();
                }
                break;
            } catch (Exception e) {
            //    log.error("Date was not recognized '{}'", sb.toString());
            }
        }

        if (justDate == null) {
            log.error("Date was not recognized '{}'", jsutDateStr.toString());
            return null;
        }

        String matched = m.group();
        int potentialTimeInString = date_string.indexOf(matched) + matched.length();
        String date_corrected_str = DATE_STR_FORMATTER.format(justDate)+ " " + date_string.substring(potentialTimeInString);

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
        date_corrected_str = date_corrected_str.replace("am" , "AM");
        date_corrected_str = date_corrected_str.replace("pm" , "PM");
        date_corrected_str = date_corrected_str.replaceAll("(\\d+)\\s*(AM|PM)\\s+(UTC|EST|PST|CST|MST)?.*$", "$1 $2 $3");
        date_corrected_str = date_corrected_str.replaceAll("(\\d+)\\s+(UTC|EST|PST|CST|MST).*$", "$1 $2");

        // this is ba rule from here : http://giftedviz.com/2017/05/17/bank-of-england-holds-rates-in-7-1-vote/
        // 17 May 2017, 10:58 | Darnell Patrick
        date_corrected_str = date_corrected_str.replaceAll("\\|\\s+.*$", "");
        date_corrected_str = date_corrected_str.trim();


        // now let's remove whatever if beyonf am/pm or timezone or hours


        for (DateTimeFormatter dtf : date_time_formatter){
            try {
                TemporalAccessor temporalAccessor = dtf.parseBest(date_corrected_str, LocalDateTime::from, LocalDate::from);
                return new DateResolver((LocalDateTime)temporalAccessor
                        , date_corrected_str.length(), true);
            } catch (DateTimeParseException dateTimeParseException){
    //            log.debug("Not a valid pattern for " + date_corrected_str);
            }
        }

        log.debug(date_corrected_str + " doesn't have a time. Trying Date-only parsing.");
        return new DateResolver(justDate, jsutDateStr.length(), false);
    }

    private  static LocalDateTime findDate(String date_string){
        if (date_string == null) return null;
        date_string = date_string.replace("\u00a0"," ");
        if (date_string.length() > 1000) {
            log.error("String is too long > 1000");
            return null;
        }
    //    if (date_string.length() > 400 || date_string.split("\\s+").length > 20) return null;
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

    // TODO: This method going to become the main method to call for extraction

    private static ArrayList<ExtIntervalSimple> datefinderHelper(String substr, int offset){
        ArrayList<ExtIntervalSimple> dates_found = new ArrayList<>();
        ArrayList<Integer> starts = new ArrayList<>();
        ArrayList<Integer> ends = new ArrayList<>();
        for (DateStrHelper e : DATE_PATTERN_MAP) {
            Pattern p = e.pattern;
            Matcher m = p.matcher(substr);
            while (m.find()) {
                DateResolver dr = normalizeDateStr(substr, m, e.digits);
                if (dr == null) continue;
                int date_start_index = m.start(1);
                int date_str_end_index = m.end(1);
                ExtIntervalSimple ext = new ExtIntervalSimple(date_start_index + offset, date_str_end_index +offset);
                //make sure the date found is unique:
                boolean has_overalp = false;
                for (int i =0; i< starts.size(); i++){
                    if (date_start_index >= starts.get(i) && date_start_index <= ends.get(i)) {
                        has_overalp = true;
                        break;
                    }
                    if (date_str_end_index >= starts.get(i) && date_str_end_index <= ends.get(i)) {
                        has_overalp = true;
                        break;
                    }
                }
                if (has_overalp) continue;
                starts.add(date_start_index);
                ends.add(date_str_end_index);
                ext.setType(QTField.QTFieldType.DATETIME);
                ext.setDatetimeValue(dr.date);
                ext.setCustomData(m.group(1));
                dates_found.add(ext);
            }
        }
        return dates_found;
    }

    public static ArrayList<ExtIntervalSimple> resolveDate(String str){
        if (str == null) return null;
        str = str.replace("\u00a0"," ");
        String string_copy = str;
        //TODO: offset may not be needed
        int offset = 0;
        ArrayList<ExtIntervalSimple> dates = datefinderHelper(string_copy, offset);
    //    while (true){
    //        ArrayList<ExtIntervalSimple> dates_found = datefinderHelper(string_copy, offset);
    //        if (dates_found.size() ==0) {// didn't find anything.. time to give up!
    //            break;
    //        }
    //        for (ExtIntervalSimple  ext : dates_found) {
    //            string_copy = string_copy.substring(ext.getEnd() - offset);
    //            offset = ext.getEnd();
    //            dates.add(ext);
    //        }
    //    }
        return dates;
    }

    private static LocalDateTime getBestMatch(List<DateResolver> allDates){
        Map<LocalDateTime, Double> scores = new HashMap<>();
        for (DateResolver dr : allDates){
   //         double score = (double) dr.length / (double) dr.textLength
   //                 *(double) 1 / dr.pos;
            scores.put(dr.date, (double) dr.pos);
        }
        Map<LocalDateTime, Double> sorted = MapSort.sortByValue(scores);
        Map.Entry<LocalDateTime, Double> entry = sorted.entrySet().iterator().next();

        return entry.getKey();
    }

    private static LocalDateTime findDate(List<Element> elements){
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
   //     String txt  = "FW: Interprint Inc; Morten Enterprises Inc - Wind Submission; Eff 7/15/2018";
        String txt = "InceptionPortfolio Benchmark (Annualized) Asset Class Composition (Net market value, as of 10/31/18) Fund Performance External: Local: Sovereign 68% Sovereign 2% The Fund returned -2.67% (net I-shares) in October, underperforming the Quasi Sovereign 10% Quasi Sovereign 0% benchmark by 51 bps.";
        LocalDateTime dt = DateResolver.resolveDateStr(txt);
        ArrayList<ExtIntervalSimple> vals = DateResolver.resolveDate(txt);

    //    Document doc = Jsoup.connect("https://www.sec.gov/Archives/edgar/data/34088/000003408817000041/xom10q2q2017.htm").get();
    //    dt = DateResolver.resolveDate(doc);
        if (dt != null) {
            log.info(dt.toString());
        } else {
            log.info("Date was not found");
        }
    }
}
