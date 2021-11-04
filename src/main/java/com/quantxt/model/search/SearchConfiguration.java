package com.quantxt.model.search;

import com.quantxt.model.QTField;

/**
 * Search configuration model.
 *
 * @author Branko Ostojic
 */

public class SearchConfiguration {

    protected String title;

    protected String id;
    protected String userId;
    protected String username;
    protected String log_stream;
    protected String out_stream;

    protected Integer numWorkers;

    protected QTSearchDictionary[] searchDictionaries;
    protected String [] ocrLangs;

    protected QTField[] fields;
    protected String function;
    private boolean editable;

    public SearchConfiguration(){

    }

    public String getId() {
        return id;
    }

    public Integer getNumWorkers() {
        return numWorkers;
    }

    public String getLog_stream() {
        return log_stream;
    }

    public String getUserId() {
        return userId;
    }

    public QTField[] getFields() {
        return fields;
    }

    public String getOut_stream() {
        return out_stream;
    }

    public QTSearchDictionary[] getSearchDictionaries() {
        return searchDictionaries;
    }

    public String getUsername(){ return username;}

    public void setUsername(String username) {
        this.username = username;
    }

    public SearchConfiguration setId(String id) {
        this.id = id;
        return this;
    }

    public SearchConfiguration setLog_stream(String log_stream) {
        this.log_stream = log_stream;
        return this;
    }

    public SearchConfiguration setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public SearchConfiguration setFields(QTField[] fields) {
        this.fields = fields;
        return this;
    }

    public SearchConfiguration setOut_stream(String out_stream) {
        this.out_stream = out_stream;
        return this;
    }

    public SearchConfiguration setNumWorkers(Integer numWorkers) {
        this.numWorkers = numWorkers;
        return this;
    }

    public SearchConfiguration setSearchDictionaries(QTSearchDictionary[] searchDictionaries) {
        this.searchDictionaries = searchDictionaries;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public SearchConfiguration setTitle(String title) {
        this.title = title;
        return this;
    }

    public String[] getOcrLangs() {
        return ocrLangs;
    }

    public SearchConfiguration setOcrLangs(String[] ocrLangs) {
        this.ocrLangs = ocrLangs;
        return this;

    }

    public String getFunction() {
        return function;
    }

    public SearchConfiguration setFunction(String function) {
        this.function = function;
        return this;
    }

    public boolean isEditable() {
        return editable;
    }

    public SearchConfiguration setEditable(boolean editable) {
        this.editable = editable;
        return this;
    }
}
