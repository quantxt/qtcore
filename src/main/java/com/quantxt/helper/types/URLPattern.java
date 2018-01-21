package com.quantxt.helper.types;

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
    final private static Pattern COUNTER = Pattern.compile("__COUNT_(\\d+)_(\\d+)_(\\d+)__");
    final private static Pattern QUERY   = Pattern.compile("__QUERY__");

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
    private String seprator = "+";
    private Map<String, String> headers;

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

    public void addQueryAndPaging(Collection<String> all_search_terms) throws UnsupportedEncodingException
    {
        ArrayList<String> counter_added = new ArrayList<>();
        for (String  searchUrl : links) {

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
                    search_term = search_term.toLowerCase().trim().replace(" " , seprator);

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
        String bas_google_search_url = "https://www.google.com/search?q=__QUERY__&start=__COUNT_0_10_50__&num=50&lr=lang_en&tbas=0&biw=1280&bih=726";
        URLPattern up = new URLPattern();
        up.links = new String[] {bas_google_search_url};
        List<String> terms = new ArrayList<>();
        terms.add("iran india");
        up.addQueryAndPaging(terms);
        for (String s : up.links){
            logger.info(s);
        }
    }
}
