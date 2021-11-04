package com.quantxt.doc;

import java.time.LocalDateTime;
import java.util.*;

import com.quantxt.model.ExtInterval;

public class QTDocument {

    public enum Language {
        ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
    }

    protected String title;
    protected List<String> body;
    protected Language language;
    private LocalDateTime date;
    private String link;
    private int position;
    private String source;
    private String id;
    private ArrayList<ExtInterval> values;

    protected transient QTDocumentHelper helper;

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

    public String getLink() {
        return link;
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

    public QTDocumentHelper getHelper() {
        return helper;
    }

    public String getSource() {
        return source;
    }

    public void setBody(List<String> body) {
        this.body = body;
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

    public void setSource(String source) {
        this.source = source;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setValues(ArrayList<ExtInterval> values) {
        this.values = values;
    }
}