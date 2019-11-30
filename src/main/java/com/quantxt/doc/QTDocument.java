package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quantxt.helper.types.ExtIntervalSimple;
import com.quantxt.helper.types.QTField;
import com.quantxt.helper.types.QTMatch;
import com.quantxt.interval.Interval;
import com.quantxt.types.DictSearch;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.quantxt.helper.types.ExtInterval;

import static com.quantxt.helper.types.QTField.QTFieldType.*;
import static com.quantxt.types.Entity.NER_TYPE;

@Getter
@Setter
public abstract class QTDocument {

    public enum Language {
        ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
    }

    public enum CHUNK {
        LINE, BULLET, SENTENCE, PARAGRAPH, PAGE, NONE
    }

    final private static int MAX_Key_Length = 150;

    final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//

    public enum DOCTYPE {
        Headline, Action, Statement, Aux, Speculation,
        Legal, Acquisition, Production, Partnership, Employment, Development
    }

    private static Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(QTDocument.class);

    protected String title;
    protected String englishTitle;
    protected String body;
    protected Language language;
    protected transient List<String> sentences;
    private DOCTYPE docType;

    private DateTime date;
    private String link;
    private double score;
    private int position;
    private String source;
    private String id;

    protected transient QTDocumentHelper helper;

    //TODO: all these should be combined into a generic class
    private List<String> tags;
    private Map<String, List<String>> entity;
    private ArrayList<String> verbs;
    private ArrayList<String> nouns;
    private ArrayList<ExtInterval> values;

    public QTDocument(String b, String t, QTDocumentHelper helper) {
        title = t;
        body = b;
        this.helper = helper;
    }

    // Getters

    public synchronized String getDateStr() {
        return dateFormat.format(date);
    }

    public abstract List<QTDocument> getChunks(CHUNK chunking);

    public abstract String Translate(String text, Language inLang, Language outLang);

    public abstract boolean isStatement(String s);

    public void extractKeyValues(DictSearch<QTMatch> dictSearch,
                                 String pre_context)
    {
        QTField.QTFieldType valueType = dictSearch.getDictionary().getValType();
        final String rawSent_curr = title;
        Collection<QTMatch> name_match_curr = dictSearch.search(title);
        if (name_match_curr.size() == 0) return;

        // for non-typed dictionaries
        if (valueType == null || valueType == NONE){
            for (QTMatch qtMatch : name_match_curr) {
                String matching_string = qtMatch.getCustomData();
                String match_group = qtMatch.getGroup();
                addEntity(match_group, matching_string);
            }
            return;
        }

        ArrayList<ExtIntervalSimple> values = new ArrayList<>();
        // get potential values
        if (valueType == DOUBLE || valueType == FLOAT || valueType == INT ||
                valueType == SHORT || valueType == MONEY || valueType == PERCENT) {
            if (!(title.contains("0") || title.contains("1") || title.contains("2") || title.contains("3") ||
                    title.contains("4") || title.contains("5") || title.contains("6") || title.contains("7") ||
                    title.contains("8") || title.contains("9"))) {
                return;
            }
            helper.getValues(rawSent_curr, pre_context, values);
        } else {
            if (valueType == DATETIME) {
                helper.getDatetimeValues(rawSent_curr, pre_context, values);
            } else if (valueType == STRING ) {
                ArrayList<ExtIntervalSimple> rowValues = new ArrayList<>();
                ExtIntervalSimple extIntervalSimple = new ExtIntervalSimple(0, title.length() - 1);
                extIntervalSimple.setStringValue(title);
                extIntervalSimple.setCustomData(title);
                rowValues.add(extIntervalSimple);
                for (QTMatch qtMatch : name_match_curr) {
                    String keyGroup = qtMatch.getGroup();
                    String key = qtMatch.getCustomData();
                    ExtInterval extInterval = new ExtInterval();
                    extInterval.setKeyGroup(keyGroup);
                    extInterval.setKey(key);
                    extInterval.setExtIntervalSimples(rowValues);
                    if (this.values == null) this.values = new ArrayList<>();
                    this.values.add(extInterval);
                    addEntity(keyGroup, key);
                }
                return;
            } else if (valueType == KEYWORD) {
                Pattern regex = dictSearch.getDictionary().getPattern();
                int[] groups = dictSearch.getDictionary().getGroups();
                helper.getPatternValues(rawSent_curr, pre_context, regex, groups, values);
            } else if (valueType == NOUN || valueType == VERB) {
                List<String> tokens = helper.tokenize(rawSent_curr);
                String[] parts = tokens.toArray(new String[tokens.size()]);
                List<ExtIntervalSimple> nounAndVerbs = helper.getNounAndVerbPhrases(rawSent_curr, parts);
                for (ExtIntervalSimple ext : nounAndVerbs){
                    if (valueType == ext.getType() ){
                        values.add(ext);
                    }
                }
            }
        }

        if (values.size() == 0) return;

        ArrayList<Interval> all_key_values = new ArrayList<>();
        all_key_values.addAll(values);
        all_key_values.addAll(name_match_curr);

        all_key_values.sort((Interval s1, Interval s2)->s1.getStart()-s2.getStart());

        Pattern padding_between_values = dictSearch.getDictionary().getSkip_between_values();
        Pattern padding_between_key_value = dictSearch.getDictionary().getSkip_between_key_and_value();

        int total_intervals = all_key_values.size();

        for (int i = 0; i < total_intervals-1; i++) {
            Interval interval_1 = all_key_values.get(i);
            if (!(interval_1 instanceof QTMatch)) continue;
            QTMatch qtKeyMatch = (QTMatch) interval_1;
            ArrayList<ExtIntervalSimple> rowValues = new ArrayList<>();

            for (int j=i+1; j<total_intervals; j++) {
                Interval interval_2 = all_key_values.get(j);
                if (!(interval_2 instanceof ExtIntervalSimple)) break;

                ExtIntervalSimple extIntervalSimple_current = (ExtIntervalSimple) interval_2;

                String gap_between_key_and_value = rawSent_curr.substring(interval_1.getEnd(), interval_2.getStart());
                Pattern pattern_to_run_on_gap = rowValues.size() == 0 ? padding_between_key_value
                        : padding_between_values;
                Matcher k = pattern_to_run_on_gap.matcher(gap_between_key_and_value);
                if (!k.find()) continue;
                rowValues.add(extIntervalSimple_current);
                interval_1 = interval_2;
            }

            if (rowValues.size() == 0) continue;

            if (this.values == null) this.values = new ArrayList<>();


            ExtInterval extInterval = new ExtInterval();
            extInterval.setKeyGroup(qtKeyMatch.getGroup());
            extInterval.setKey(qtKeyMatch.getCustomData());
            extInterval.setExtIntervalSimples(rowValues);
            this.values.add(extInterval);
            addEntity(qtKeyMatch.getGroup(), qtKeyMatch.getCustomData());
        }
    }

    public void convertValues2titleTable() {
        if (this.values == null) return;

        Collections.sort(this.values, new Comparator<ExtInterval>() {
            public int compare(ExtInterval p1, ExtInterval p2) {
                Integer s1 = p1.getExtIntervalSimples().get(0).getStart();
                Integer s2 = p2.getExtIntervalSimples().get(0).getStart();
                return s1.compareTo(s2);
            }
        });

        LinkedHashSet<String> rows = new LinkedHashSet<>();
        for (ExtInterval ext : this.values) {

            StringBuilder sb = new StringBuilder();
            sb.append("<tr>");
            sb.append("<td>").append(ext.getKey()).append("</td>");
            for (ExtIntervalSimple extvStr : ext.getExtIntervalSimples()) {
                sb.append("<td>").append(extvStr.getCustomData().toString()).append("</td>");
            }
            sb.append("</tr>");
            String row2add = sb.toString();
            rows.add(row2add);
        }

        if (!rows.isEmpty()) {
            if (!title.startsWith("<table ")) {
                title = "";
            }
            title += "<table width=\"100%\">" + String.join("", rows) + "</table>";
        } else {
            if (!title.startsWith("<table ")) {
                title = "";
            }
        }
    }

    public void addNounAndVerbs(){
        List<String> tokens = helper.tokenize(title);
        String[] parts = tokens.toArray(new String[tokens.size()]);
        List<ExtIntervalSimple> nounsAndVerbs = helper.getNounAndVerbPhrases(title, parts);
        for (ExtIntervalSimple ext : nounsAndVerbs) {
            switch (ext.getType()) {
                case VERB:
                    String verb = title.substring(ext.getStart(), ext.getEnd());
                    DOCTYPE verbType = helper.getVerbType(verb);
                    if (verbType != null) {
                        setDocType(verbType);
                    }
                    break;
                case NOUN:
                    String noun = title.substring(ext.getStart(), ext.getEnd());
                    addEntity(NER_TYPE, noun);
            }
        }
    }

    // Re-factor co-referring part
    @Deprecated
    public ArrayList<QTDocument> extractEntityMentions(DictSearch<QTMatch> dictSearch,
                                                       boolean onlyIncludeUttsWithEntities,
                                                       boolean extractNounAndVebPhrases,
                                                       CHUNK chunking) {
        ArrayList<QTDocument> quotes = new ArrayList<>();
        List<QTDocument> childs = getChunks(chunking);

        int numSent = childs.size();

        for (int i = 0; i < numSent; i++) {
            QTDocument workingChild = childs.get(i);
            final String rawSent_curr = workingChild.getTitle();
            String rawSent_before = "";

            if (i > 0) {
                rawSent_before = childs.get(i - 1).getTitle();
                workingChild.setBody(rawSent_before + " " + rawSent_curr);
            } else {
                workingChild.setBody(rawSent_curr);
            }

            List<String> tokens = helper.tokenize(rawSent_curr);
            String[] parts = tokens.toArray(new String[tokens.size()]);
            if (!helper.isSentence(rawSent_curr, tokens)) continue;

            if (dictSearch != null) {
               Collection<QTMatch> name_match_curr = dictSearch.search(rawSent_curr);
                if (name_match_curr.size() == 0 && i > 0) {
                    List<String> tokens_b = helper.tokenize(rawSent_before);
                    if (helper.isSentence(rawSent_before, tokens_b)) {

                        Collection<QTMatch> name_match_befr = dictSearch.search(rawSent_before);
                        //TODO: this needs to be revised
                        if (name_match_befr.size() != 1) continue;
                        for (QTMatch qtMatch : name_match_befr) {
                            String vocab_name = qtMatch.getGroup();
                    //        Collection<QTMatch> ent_set = e.getValue();
                            // simple co-ref for now
                            if (helper.getPronouns().contains(parts[0])) {
                                name_match_curr.add(qtMatch);
                            }
                        }
                    }
                }
                if (name_match_curr.size() > 0) {
                    for (QTMatch qtMatch : name_match_curr) {
                        String ne = qtMatch.getCustomData();
                        workingChild.addEntity(qtMatch.getGroup(), ne);
                            //			logger.info("\t" + entType.getKey() + " | " + ne.getName());
                    }
                }
                if (name_match_curr.size() == 0 && onlyIncludeUttsWithEntities) continue;
            }

            if (extractNounAndVebPhrases) {
                List<ExtIntervalSimple> nounsAndVerbs = helper.getNounAndVerbPhrases(rawSent_curr, parts);
                for (ExtIntervalSimple ext : nounsAndVerbs) {
                    switch (ext.getType()) {
                        case VERB:
                            String verb = rawSent_curr.substring(ext.getStart(), ext.getEnd());
                            DOCTYPE verbType = helper.getVerbType(verb);
                            if (verbType != null) {
                                workingChild.setDocType(verbType);
                            }
                            break;
                        case NOUN:
                            String noun = rawSent_curr.substring(ext.getStart(), ext.getEnd());
                            workingChild.addEntity(NER_TYPE, noun);
                    }
                }
            }

            if (onlyIncludeUttsWithEntities) {
                if (workingChild.getEntity() != null && workingChild.getEntity().size() != 0) {
                    quotes.add(workingChild);
                }
            } else {
                quotes.add(workingChild);
            }

        }

        return quotes;
    }

    public void addTags(List<String> taglist) {
        tags.addAll(taglist);
    }

    public void addTag(String tag) {
        if (tags == null){
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public void addEntity(String t, String e) {
        List<String> list;
        if (entity == null) {
            entity = new HashMap<>();
            list = new ArrayList<>();
        } else {
            list = entity.get(t);
            if (list == null) {
                list = new ArrayList<>();
            }
        }
        list.add(e);
        entity.put(t, list);
    }

    public String toString() {
        return gson.toJson(this);
    }

}