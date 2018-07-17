package com.quantxt.helper.types;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 3/14/17.
 */

public class URLPattern {

    final private static Logger logger = LoggerFactory.getLogger(URLPattern.class);
    final private static Pattern DATE = Pattern.compile("__DATE_(-?[1-9]\\d*|0)");
    final private static Pattern NDATE = Pattern.compile("__MIN__(-?[1-9]\\d*|0)_(-?[1-9]\\d*|0)_(-?[1-9]\\d*|0)");
    final private static Pattern COUNTER = Pattern.compile("__COUNT_(\\d+)_(\\d+)_(\\d+)__");
    final private static Pattern QUERY   = Pattern.compile("__QUERY__");
    final private static DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy");


    private String [] links;
    private String urlPattern = "href=\"(http[^\"]+)\"";
    private String host;
    private String name;
    private boolean sortByDate;
    private String selector;
    private String sTitleField;
    private String sBodyField;
    private String dateField;
    private String dateSort = "";
    private int analysis_mode = 2;
    private String seprator = "+";
    private Map<String, String> headers;
    private boolean adx;
    private String mode;

    public URLPattern(){

    }

    public URLPattern(URLPattern up){
        this.links = up.links;
        this.urlPattern = up.urlPattern;
        this.host = up.host;
        this.name = up.name;
        this.sortByDate = up.sortByDate;
        this.selector = up.selector;
        this.sTitleField = up.sTitleField;
        this.sBodyField = up.sBodyField;
        this.dateField = up.dateField;
        this.dateSort = up.dateSort;
        this.seprator = up.seprator;
        this.headers = up.headers;
        this.adx = up.adx;
        this.mode = up.mode;

    }

    public URLPattern(String [] links){
        this.links = links;
    }

    public String getUrlPattern() {return urlPattern;}

    public String getHost() {return host;}
    public String [] getLinks() {
        if (!sortByDate) {
            return links;
        }
        ArrayList<String> linksCopy = new ArrayList<>();
        for (String l : links){
            linksCopy.add(l + dateSort);
        }
        return linksCopy.toArray(new String[linksCopy.size()]);
    }

    public String getsTitleField(){
        return sTitleField;
    }

    public String getsBodyField(){
        return sBodyField;
    }

    public String getName(){return name;}
    public int getAnalysis_mode(){return analysis_mode;}
    public String getMode(){return mode;}
    public boolean getAdx(){return adx;}
    public String getResElement() {return selector;}
    public String getSeprator(){return seprator;}
    public Map<String, String> getHeaders() {return headers;}
    public String getDateField(){
        return dateField;
    }

    public void setUrlPattern(String p){
        urlPattern = p;
    }
    public void setLinks(String [] links) {
        this.links = links;
    }

    public void addQueryAndPaging(Collection<String> all_search_terms,
                                  DateTime fromDate) throws UnsupportedEncodingException
    {
        ArrayList<String> processed = new ArrayList<>();

        DateTime today = new DateTime();
        //check for date
        for (String  searchUrl : links) {
            if (searchUrl.contains("__DATE_")) {
                Matcher date_match = DATE.matcher(searchUrl);
                while (date_match.find()) {
                    DateTime finaldate = today.plusDays(Integer.parseInt(date_match.group(1)));
                    String date_str = dtf.print(finaldate);
                    searchUrl = searchUrl.replace(date_match.group(0), date_str);
                }
                processed.add(searchUrl);
            } else if (searchUrl.contains("__MIN__")) {
                Matcher date_match = NDATE.matcher(searchUrl);
                while (date_match.find()) {
                    int end = Integer.parseInt(date_match.group(3));
                    int start = Integer.parseInt(date_match.group(1));
                    int offset = Integer.parseInt(date_match.group(2));

                    for (int i=start; i > end; i+=offset){
                        DateTime start_d = today.plusDays(i+offset);
                        DateTime end_d = today.plusDays(i-1);
                        // make sure this is after the anticipated fromDate
                        if (fromDate != null && end_d.isBefore(fromDate)) continue;
                        String start_date_str = dtf.print(start_d);
                        String end_date_str = dtf.print(end_d);
                        String srchCopy = searchUrl.replace(date_match.group(0), start_date_str);
                        srchCopy = srchCopy.replace("__MAX__", end_date_str);
                        processed.add(srchCopy);
                    }
                }
            } else {
                processed.add(searchUrl);
            }
        }

        ArrayList<String> counter_added = new ArrayList<>();
        for (String  searchUrl : processed) {
            Matcher counter_match = COUNTER.matcher(searchUrl);

            if (counter_match.find()) {
                int curPage = Integer.parseInt(counter_match.group(1));
                int step = Integer.parseInt(counter_match.group(3));
                int numPage = Integer.parseInt(counter_match.group(2));

                String all_match = counter_match.group(0);
                for (int i = 0; i < numPage; i++) {
                    String newLink = searchUrl.replace(all_match, String.valueOf(curPage));
                    counter_added.add(newLink);
                    curPage += step;
                }
            } else {
                counter_added.add(searchUrl);
            }
        }

        Set<String> links = new HashSet<>();
        for (String l : counter_added){
            Matcher query_match = QUERY.matcher(l);
            if (query_match.find()) {
                for (String search_term : all_search_terms) {
                    search_term = search_term.trim().replace(" " , seprator);
                    String searchLink = l.replace(query_match.group(0), search_term);
                    if (links.contains(searchLink)) continue;
                    links.add(searchLink);
                }
            } else {
                links.add(l);
            }
        }

        this.links = links.toArray(new String[links.size()]);
    }

    public static void main(String[] args) throws Exception {
        String bas_google_search_url = "https://www.google.com/search?q=__QUERY__&start=__COUNT_0_1_50__&lr=lang_es&num=50&tbm=nws&tbs=lr:lang_1es,cdr:1,cd_min:__MIN__0_-5_-15,cd_max:__MAX__";
        URLPattern up = new URLPattern();
        up.links = new String[] {bas_google_search_url};
        List<String> terms = new ArrayList<>();
        terms.add("iran india");
        up.addQueryAndPaging(terms, null);
        for (String s : up.links){
            logger.info(s);
        }
    }
}
