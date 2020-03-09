package com.quantxt.doc;

import java.time.LocalDateTime;
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

import com.quantxt.helper.types.ExtInterval;

import static com.quantxt.helper.types.QTField.QTFieldType.*;

@Getter
@Setter
public abstract class QTDocument {

    public enum Language {
        ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
    }

    final public static String NER_TYPE = "Entity_NER";

    public enum CHUNK {
        LINE, BULLET, SENTENCE, PARAGRAPH, PAGE, NONE
    }

    public enum DOCTYPE {
        Headline, Action, Statement, Aux, Speculation,
        Legal, Acquisition, Production, Partnership, Employment, Development
    }

    protected String title;
    protected String englishTitle;
    protected String body;
    protected Language language;
    protected transient List<String> sentences;
    private DOCTYPE docType;

    private LocalDateTime date;
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

    public abstract List<QTDocument> getChunks(CHUNK chunk);

    public abstract String Translate(String text, Language inLang, Language outLang);

    public abstract boolean isStatement(String s);

    public void extractKeyValues(DictSearch<QTMatch> dictSearch,
                                 boolean vertical_overlap,
                                 String pre_context)
    {
        QTField.QTFieldType valueType = dictSearch.getDictionary().getValType();
        Collection<QTMatch> qtMatches = dictSearch.search(title);
        if (qtMatches.size() == 0) return;

        // for non-typed dictionaries
        if (valueType == null || valueType == NONE){
            for (QTMatch qtMatch : qtMatches) {
                String matching_string = qtMatch.getCustomData();
                String match_group = qtMatch.getGroup();
                addEntity(match_group, matching_string);
            }
            return;
        }

        ArrayList<ExtIntervalSimple> values = new ArrayList<>();
        // get potential values
        if (valueType == DOUBLE || valueType == MONEY || valueType == PERCENT) {
            if (!(title.contains("0") || title.contains("1") || title.contains("2") || title.contains("3") ||
                    title.contains("4") || title.contains("5") || title.contains("6") || title.contains("7") ||
                    title.contains("8") || title.contains("9"))) {
                return;
            }
            helper.getValues(title, pre_context, values);
        } else {
            if (valueType == DATETIME) {
                helper.getDatetimeValues(title, pre_context, values);
            } else if (valueType == STRING ) {
                ArrayList<ExtIntervalSimple> rowValues = new ArrayList<>();
                ExtIntervalSimple extIntervalSimple = new ExtIntervalSimple(0, title.length() - 1);
                extIntervalSimple.setStringValue(title.replaceAll(" +", " ").trim());
                extIntervalSimple.setCustomData(title.replaceAll(" +", " ").trim());
                rowValues.add(extIntervalSimple);
                for (QTMatch qtMatch : qtMatches) {
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
        //        helper.getPatternValues(rawSent_curr, pre_context, regex, groups, values);

                int strLength = title.length();
                for (QTMatch qtMatch : qtMatches) {
                    ArrayList<ExtIntervalSimple> partial_values = new ArrayList<>();
                    int qtMatchEnd = qtMatch.getEnd();
                    // look up to 5000 characters after this
            //        Pattern gapBetweenKeyAndValue =  dictSearch.getDictionary().getSkip_between_key_and_value().
                    int lookupLength = Math.min(5000, strLength - qtMatchEnd);
                    String str_to_apply_regex = title.substring(qtMatchEnd, qtMatchEnd + lookupLength-1);
                    helper.getPatternValues(str_to_apply_regex, pre_context, regex, groups, partial_values);
                    if (partial_values.size() == 0) continue;
                    for (ExtIntervalSimple extIntervalSimple : partial_values){
                        //shift the values by qtMatchEnd
                        int shiftedStart = extIntervalSimple.getStart() + qtMatchEnd;
                        int shiftedEnd = extIntervalSimple.getEnd() + qtMatchEnd;
                        ExtIntervalSimple extIntervalSimpleShifted = new ExtIntervalSimple(shiftedStart, shiftedEnd);
                        extIntervalSimpleShifted.setDatetimeValue(extIntervalSimple.getDatetimeValue());
                        extIntervalSimpleShifted.setStringValue(extIntervalSimple.getStringValue());
                        extIntervalSimpleShifted.setCustomData(extIntervalSimple.getCustomData());
                        extIntervalSimpleShifted.setDoubleValue(extIntervalSimple.getDoubleValue());
                        extIntervalSimpleShifted.setIntValue(extIntervalSimple.getIntValue());
                        values.add(extIntervalSimpleShifted);
                    }
                }
            } else if (valueType == NOUN || valueType == VERB) {
                List<String> tokens = helper.tokenize(title);
                String[] parts = tokens.toArray(new String[tokens.size()]);
                List<ExtIntervalSimple> nounAndVerbs = helper.getNounAndVerbPhrases(title, parts);
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
        all_key_values.addAll(qtMatches);

        Collections.sort(all_key_values, Comparator.comparingInt(Interval::getStart));

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
                boolean isValidInterval = interval_2 instanceof ExtIntervalSimple;
                boolean isMatchingBetweenKeyAndVlaue = rowValues.size() == 0;

                Pattern pattern_to_run_on_gap = isMatchingBetweenKeyAndVlaue ? padding_between_key_value : padding_between_values;
                String gap;

                if (vertical_overlap){
                    if (!isValidInterval) continue;
                    gap = getVerticalGep(interval_1, interval_2, title);
                    if (gap == null) {
                        gap = getHorizentalGap(interval_1, interval_2, title);
                    }
                } else {
                    if (!isValidInterval) break;
                    gap = getHorizentalGap(interval_1, interval_2, title);
                }

                gap = gap.replaceAll(" +", " ");
                Matcher matcher = pattern_to_run_on_gap.matcher(gap);
                if ( (gap.isEmpty() && vertical_overlap )|| matcher.find() ) {
                    ExtIntervalSimple extIntervalSimple_current = (ExtIntervalSimple) interval_2;
                    rowValues.add(extIntervalSimple_current);
                    interval_1 = interval_2;
                } else {
                    //If we are finding more than one value AND the gap between value_n ane value_n+1 is not valid then stop here
                    if (!isMatchingBetweenKeyAndVlaue) break;
                }
            }

            if (rowValues.size() == 0) continue;

            if (this.values == null) this.values = new ArrayList<>();

            ExtInterval extInterval = new ExtInterval();
            extInterval.setKeyGroup(qtKeyMatch.getGroup());
            extInterval.setKey(qtKeyMatch.getCustomData());
            extInterval.setExtIntervalSimples(rowValues);
            extInterval.setStart(qtKeyMatch.getStart());
            extInterval.setEnd(qtKeyMatch.getEnd());
            this.values.add(extInterval);
            addEntity(qtKeyMatch.getGroup(), qtKeyMatch.getCustomData());
        }
    }

    //   --- Interval 1      -------
    //         | Vertical   |
    //         |    Gap     |
    //   ------ Interval 2       ---

    protected String getHorizentalGap(Interval interval1, Interval interval2, String str){
        return str.substring(interval1.getEnd(), interval2.getStart());
    }

    protected String getVerticalGep(Interval interval1, Interval interval2, String str){

        StringBuilder sb = new StringBuilder();
        int offsetStartInterval1 = getOffsetFromLineStart(str, interval1.getStart());
        int offsetStartInterval2 = getOffsetFromLineStart(str, interval2.getStart());

        int length1 = interval1.getEnd() - interval1.getStart();
        int length2 = interval2.getEnd() - interval2.getStart();

        int offsetEndInterval1 = offsetStartInterval1 + length1;
        int offsetEndInterval2 = offsetStartInterval2 + length2;

        //find indices of the vertical column for the gap
        int startGapIndex = Math.min(offsetStartInterval1, offsetStartInterval2);
        int endGapIndex   = Math.max(offsetEndInterval1, offsetEndInterval2);

        // if there is no overlap then return null
        // allow vetically stacked blocks not to be exactly aligned
        if ((endGapIndex - startGapIndex) >= (length1 + length2 + 4)) return null;

        String [] linesBetween = str.substring(interval1.getStart(), interval2.getEnd()).split("\n");

        for (int i=1; i<linesBetween.length-1; i++){
            String line = linesBetween[i];
            int endidx = Math.min(endGapIndex, line.length());
            if (endidx > startGapIndex ) {
                String gap = line.substring(startGapIndex, endidx);
                sb.append(gap).append(" ").append("\n");
            }
        }
        return sb.toString();
    }

    protected int getOffsetFromLineStart(String str, int index){
        return index - str.substring(0, index).lastIndexOf('\n') -1;
    }

    private ExtIntervalSimple getAdjustPageBasedExtraction(ExtIntervalSimple extIntervalSimple){
        //adjust start and end to be offset from beginning of the line
        // beginning of the line:
        int currentStart = extIntervalSimple.getStart();
        int currentEnd = extIntervalSimple.getEnd();
        int lineStart = title.substring(0,currentStart).lastIndexOf('\n');
        ExtIntervalSimple adjustedExtIntervalSimple = new ExtIntervalSimple(currentStart - lineStart, currentEnd -lineStart);
        adjustedExtIntervalSimple.setStringValue(extIntervalSimple.getStringValue());
        adjustedExtIntervalSimple.setDoubleValue(extIntervalSimple.getDoubleValue());
        adjustedExtIntervalSimple.setType(extIntervalSimple.getType());
        adjustedExtIntervalSimple.setCustomData(extIntervalSimple.getCustomData());
        adjustedExtIntervalSimple.setIntValue(extIntervalSimple.getIntValue());
        adjustedExtIntervalSimple.setDatetimeValue(extIntervalSimple.getDatetimeValue());

        return adjustedExtIntervalSimple;
    }

    public void sortValues(){
        if (this.values == null) return;
        Collections.sort(this.values, Comparator.comparingInt(ExtInterval::getStart));
    }

    public void convertValues2titleTable() {
        if (this.values == null) return;
        sortValues();
        LinkedHashSet<String> rows = new LinkedHashSet<>();
        for (ExtInterval ext : this.values) {

            StringBuilder sb = new StringBuilder();
            sb.append("<tr>");
            sb.append("<td>").append(ext.getKey()).append("</td>");
            for (ExtIntervalSimple extvStr : ext.getExtIntervalSimples()) {
                String customData = extvStr.getCustomData();
                if (customData == null) continue;
                sb.append("<td>").append(customData).append("</td>");
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
}