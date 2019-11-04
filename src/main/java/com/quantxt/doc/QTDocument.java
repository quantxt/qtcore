package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quantxt.helper.types.ExtIntervalSimple;
import com.quantxt.helper.types.QTField;
import com.quantxt.types.DictSearch;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Emit;

import static com.quantxt.helper.types.QTField.QTFieldType.*;
import static com.quantxt.types.Entity.NER_TYPE;

@Getter
@Setter
public abstract class QTDocument {

    public enum Language {
        ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
    }

    final private static int MAX_Key_Length = 150;

    final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//
    final private static Pattern KEY_Thrillings = Pattern.compile("^[\\s\"'\\(\\)\\-\\:;%]+$");

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
    private Set<String> tags = new HashSet<>();
    private Map<String, LinkedHashSet<String>> entity;
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

    public abstract List<QTDocument> getChilds(boolean splitOnNewLine);

    public abstract String Translate(String text, Language inLang, Language outLang);

    public abstract boolean isStatement(String s);


    private int getNextValidIndex(final Pattern pattern,
                                  String str) {
        String lookupString = str.replaceAll("(\\p{Sc})", "");
        if (lookupString.length() == 0) return 0;
        Matcher m = pattern.matcher(lookupString);
        if (m.find()) {
            //shift keyend to end of the pad
            return m.end();
        }
        return 0;
    }

    public void extractKeyValues(DictSearch dictSearch,
                                 String pre_context)
    {
        QTField.QTFieldType valueType = dictSearch.getDictionary().getValType();
        if (valueType == null || valueType == NONE) return;

        final String rawSent_curr = title;

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
            } else if (valueType == STRING || valueType == KEYWORD) {
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

        // sort based on first index
        values.sort((ExtIntervalSimple s1, ExtIntervalSimple s2)->s1.getStart()-s2.getStart());

        Pattern padding = dictSearch.getDictionary().getKeyPadding();
        for (int i = 0; i < values.size(); i++) {
            ExtIntervalSimple intv = values.get(i);
            final int valStart = intv.getStart();

            // find all row values
            ArrayList<ExtIntervalSimple> rowValues = new ArrayList<>();
            rowValues.add(intv);
            for (int j = i + 1; j < values.size(); j++) {
                int lookupStart = values.get(j - 1).getEnd();  // end of the last index we scanned
                int lookupEnd = values.get(j).getStart();
                int shift = getNextValidIndex(KEY_Thrillings, rawSent_curr.substring(lookupStart, lookupEnd));
                if (shift > 0) {
                    // if we aded more than one value then increase the value counter
                    i++;
                    if (i < values.size()) {
                        rowValues.add(values.get(i));
                    }
                } else {
                    break;
                }
            }

            int start_search = valStart - MAX_Key_Length;
            if (start_search < 0) {
                start_search = 0;
            }

            int end_search = valStart + dictSearch.getDictionary().getSearch_distance();
            if (end_search > rawSent_curr.length()) {
                end_search = rawSent_curr.length();
            }

            // find potential keys
            String str_2_search = rawSent_curr.substring(start_search, end_search);
            Map<String, Collection<Emit>> name_match_curr = dictSearch.search(str_2_search);
            if (name_match_curr.size() == 0) continue;

            for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
                String keyGroup = entType.getKey();
                for (Emit matchedName : entType.getValue()) {
                    String key = (String) matchedName.getCustomeData();
                    int keyEnd = matchedName.getEnd();
                    int end_of_key_in_original_string = start_search + keyEnd;

                    // for now we require the key to be before the value
                    if (keyEnd < 0) {
                        logger.error("key wrong ---- {} ----- in '{}'", matchedName.getKeyword(), title);
                        continue;
                    }

                    String string_to_pad_after_key = rawSent_curr.substring(end_of_key_in_original_string);
                    Matcher m = padding.matcher(string_to_pad_after_key);
                    int shift = 0;
                    if (m.find()) {
                        shift = m.end();
                    }
                    int end_of_key_in_original_string_after_shift = end_of_key_in_original_string + shift;

                    if (shift > 0) {// that means we might need to skip some value
                        // so if there is ccc(2) 3 4 5 and the key is ccc we my need to ignore (2)
                        ArrayList<ExtIntervalSimple> new_row_values = new ArrayList<>();
                        for (ExtIntervalSimple rv : rowValues) {
                            if (rv.getStart() >= end_of_key_in_original_string_after_shift) {
                                new_row_values.add(rv);
                            }
                        }
                        rowValues = new_row_values;
                    }

                    if (rowValues.size() == 0) continue;


                    // now the first value should be close enough to key
                    //TODO: this ca be done just be the regex and dist can be removed
                    if ((rowValues.get(0).getStart() - end_of_key_in_original_string_after_shift) > dictSearch.getDictionary().getSearch_distance()) continue;

                    if (this.values == null) this.values = new ArrayList<>();

                    ExtInterval extInterval = new ExtInterval();
                    extInterval.setKeyGroup(keyGroup);
                    extInterval.setKey(key);
                    extInterval.setExtIntervalSimples(rowValues);
                    this.values.add(extInterval);
                }
            }
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

    public ArrayList<QTDocument> extractEntityMentions(DictSearch dictSearch,
                                                       boolean onlyIncludeUttsWithEntities,
                                                       boolean extractNounAndVebPhrases,
                                                       boolean splitOnNewLine) {
        ArrayList<QTDocument> quotes = new ArrayList<>();
        List<QTDocument> childs = getChilds(splitOnNewLine);

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
                Map<String, Collection<Emit>> name_match_curr = dictSearch.search(rawSent_curr);
                if (name_match_curr.size() == 0 && i > 0) {
                    List<String> tokens_b = helper.tokenize(rawSent_before);
                    if (helper.isSentence(rawSent_before, tokens_b)) {

                        Map<String, Collection<Emit>> name_match_befr = dictSearch.search(rawSent_before);
                        for (Map.Entry<String, Collection<Emit>> entType : name_match_befr.entrySet()) {
                            Collection<Emit> ent_set = entType.getValue();
                            if (ent_set.size() != 1) continue;
                            // simple co-ref for now
                            if (helper.getPronouns().contains(parts[0])) {
                                name_match_curr.put(entType.getKey(), ent_set);
                            }
                        }
                    }
                }
                if (name_match_curr.size() > 0) {
                    for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
                        for (Emit matchedName : entType.getValue()) {
                            String ne = (String) matchedName.getCustomeData();
                            workingChild.addEntity(entType.getKey(), ne);
                            //			logger.info("\t" + entType.getKey() + " | " + ne.getName());
                        }
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
        tags.add(tag);
    }

    public void addEntity(String t, String e) {
        LinkedHashSet<String> ents;
        if (entity == null) {
            entity = new HashMap<>();
            ents = new LinkedHashSet<>();
        } else {
            ents = entity.get(t);
            if (ents == null) {
                ents = new LinkedHashSet<>();
            }
        }
        ents.add(e);
        entity.put(t, ents);
    }

    public String toString() {
        return gson.toJson(this);
    }

}