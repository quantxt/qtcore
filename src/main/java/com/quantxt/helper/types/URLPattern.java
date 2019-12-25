package com.quantxt.helper.types;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 3/14/17.
 */

@Getter
@Setter
@NoArgsConstructor
public class URLPattern {

    final private static Logger logger = LoggerFactory.getLogger(URLPattern.class);
    final private static Pattern DATE = Pattern.compile("__DATE_(-?[1-9]\\d*|0)");
    final private static Pattern NDATE = Pattern.compile("__MIN__(-?[1-9]\\d*|0)_(-?[1-9]\\d*|0)_(-?[1-9]\\d*|0)");
    final private static Pattern COUNTER = Pattern.compile("__COUNT_(\\d+)_(\\d+)_(\\d+)__");
    final private static Pattern QUERY   = Pattern.compile("__QUERY__");
    final private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private String urlPattern = "href=\"(http[^\"]+)\"";
    private String name;
    private String mode = "html";
    private String to;
    private String from;
    private String selector;
    private String dateSort = "";
    private String seprator = "+";

    private boolean fastParent;
    private boolean fastChild = true;
    private boolean isTs = true;
    private boolean capline = false;
    private boolean sortByDate;
    private boolean sortByPosition;  // for pdf scraping

    private String  [] include_patterns;
    private String  [] exclude_patterns;
    private String  [] links;
    private QTField [] fields;

    private Map<String, String> headers;

    public boolean getIsTs(){
        return isTs;
    }

    public void setIsTs(boolean b){
        isTs = b;
    }

    public URLPattern(URLPattern up) {
        this.urlPattern = up.urlPattern;
        this.name = up.name;
        this.mode = up.mode;
        this.selector = up.selector;
        this.dateSort = up.dateSort;
        this.seprator = up.seprator;
        this.to = up.to;
        this.from = up.from;

        this.capline = up.capline;
        this.fastParent = up.fastParent;
        this.sortByDate = up.sortByDate;
        this.sortByPosition = up.sortByPosition;
        this.isTs = up.isTs;
        this.fastChild = up.fastChild;

        if (up.include_patterns != null) {
            this.include_patterns = Arrays.copyOf(up.include_patterns, up.include_patterns.length);
        }

        if (up.exclude_patterns != null) {
            this.exclude_patterns = Arrays.copyOf(up.exclude_patterns, up.exclude_patterns.length);
        }

        if (up.links != null) {
            this.links = Arrays.copyOf(up.links, up.links.length);
        }

        if (up.fields != null) {
            this.fields = Arrays.copyOf(up.fields, up.fields.length);
        }

        if (up.headers != null) {
            this.headers = new HashMap<>();
            for (Map.Entry<String, String> e : up.headers.entrySet()) {
                this.headers.put(e.getKey(), e.getValue());
            }
        }
    }

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

    public void addQueryAndPaging(Collection<String> all_search_terms,
                                  LocalDateTime fromDate) {
        ArrayList<String> processed = new ArrayList<>();

        LocalDateTime today = LocalDateTime.now();
        //check for date
        for (String searchUrl : links) {
            if (searchUrl.contains("__DATE_")) {
                Matcher date_match = DATE.matcher(searchUrl);
                while (date_match.find()) {
                    LocalDateTime finaldate = today.plusDays(Integer.parseInt(date_match.group(1)));
                    String date_str = dtf.format(finaldate);
                    searchUrl = searchUrl.replace(date_match.group(0), date_str);
                }
                processed.add(searchUrl);
            } else if (searchUrl.contains("__MIN__")) {
                Matcher date_match = NDATE.matcher(searchUrl);
                while (date_match.find()) {
                    int end = Integer.parseInt(date_match.group(3));
                    int start = Integer.parseInt(date_match.group(1));
                    int offset = Integer.parseInt(date_match.group(2));

                    for (int i = start; i > end; i += offset) {
                        LocalDateTime start_d = today.plusDays(i + offset);
                        LocalDateTime end_d = today.plusDays(i - 1);
                        // make sure this is after the anticipated fromDate
                        if (fromDate != null && end_d.isBefore(fromDate)) continue;
                        from = dtf.format(start_d);
                        to = dtf.format(end_d);
                        String srchCopy = searchUrl.replace(date_match.group(0), from);
                        srchCopy = srchCopy.replace("__MAX__", to);
                        processed.add(srchCopy);
                    }
                }
            } else {
                processed.add(searchUrl);
            }
        }

        ArrayList<String> counter_added = new ArrayList<>();
        for (String searchUrl : processed) {
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
        for (String l : counter_added) {
            Matcher query_match = QUERY.matcher(l);
            if (query_match.find()) {
                for (String search_term : all_search_terms) {
                    search_term = search_term.trim().replace(" ", seprator);
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
}
