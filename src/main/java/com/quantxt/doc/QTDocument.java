package com.quantxt.doc;

import java.time.LocalDateTime;
import java.util.*;

import com.quantxt.types.ExtIntervalSimple;

import com.quantxt.types.ExtInterval;

public abstract class QTDocument {

    public enum Language {
        ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
    }

    public enum CHUNK {
        LINE, BULLET, SENTENCE, PARAGRAPH, PAGE, NONE
    }

    protected String title;
    protected List<String> body;
    protected Language language;
    private LocalDateTime date;
    private String link;
    private double score;
    private int position;
    private String source;
    private String id;
    private ArrayList<ExtInterval> values;

    protected transient QTDocumentHelper helper;

    //TODO: all these should be combined into a generic class
    private List<String> tags;
    private Map<String, List<String>> entity;


    public QTDocument(String b, String t, QTDocumentHelper helper) {
        title = t;
        body = new ArrayList<>();
        body.add(b);
        this.helper = helper;
    }

    public QTDocument(List<String> b, String t, QTDocumentHelper helper) {
        title = t;
        body = b;
        this.helper = helper;
    }

    // Getters

    public abstract List<QTDocument> getChunks(CHUNK chunk);

    public abstract String Translate(String text, Language inLang, Language outLang);

    public abstract boolean isStatement(String s);

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
            sb.append("<td>").append(ext.getCategory()).append("</td>");
            for (ExtIntervalSimple extvStr : ext.getExtIntervalSimples()) {
                String customData = extvStr.getStr();
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

    public String getLink() {
        return link;
    }

    public double getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public int getPosition() {
        return position;
    }

    public Language getLanguage() {
        return language;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<String> getBody() {
        return body;
    }

    public ArrayList<ExtInterval> getValues() {
        return values;
    }

    public String getId() {
        return id;
    }

    public List<String> getTags() {
        return tags;
    }

    public QTDocumentHelper getHelper() {
        return helper;
    }

    public String getSource() {
        return source;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setHelper(QTDocumentHelper helper) {
        this.helper = helper;
    }

    public Map<String, List<String>> getEntity() {
        return entity;
    }

    public void setEntity(Map<String, List<String>> entity) {
        this.entity = entity;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setValues(ArrayList<ExtInterval> values) {
        this.values = values;
    }
}