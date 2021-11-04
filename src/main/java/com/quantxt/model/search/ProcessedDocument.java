package com.quantxt.model.search;


import com.quantxt.model.ExtInterval;

import java.util.List;

public class ProcessedDocument {

    // Unique id of the data processing job created this document
    private String searchId;

    // document unique id
    private String source;
    private String link;
    private int position;
    private List<ExtInterval> values;

    public ProcessedDocument(){

    }
    public ProcessedDocument(String searchId,
                             String source,
                             String link,
                             List<ExtInterval> values,
                             int position) {
        this.searchId = searchId;
        this.source = source;
        this.link = link;
        this.values = values;
        this.position = position;
    }


    public String getSource() {
        return source;
    }

    public int getPosition() {
        return position;
    }

    public String getSearchId() {
        return searchId;
    }

    public String getLink() {
        return link;
    }

    public List<ExtInterval> getValues() {
        return values;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setValues(List<ExtInterval> values) {
        this.values = values;
    }
}