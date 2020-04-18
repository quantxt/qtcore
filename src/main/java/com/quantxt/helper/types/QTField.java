package com.quantxt.helper.types;

import java.util.ArrayList;

public class QTField {
    public enum QTFieldType {INT, SHORT, LONG, FLOAT, DOUBLE, STRING, KEYWORD, DATETIME, NOUN, VERB, PERCENT, MONEY, NONE}

    public QTField(){

    }

    /*
    Full name of the data field
     */
    protected String fname;

    /*
    Short name for the data field
     */
    protected String sname;

    /*
    Is the field aggregable?
     */
    protected boolean isFilter;

    /*
    This field processed by a Value; mostly for KEYWORD and STRING fields
     */
    protected boolean hasValue;

    /*
    number of bukcets
     */

    protected int numBuckets;

    /*
    Sort by name or count
     */
    protected boolean isSortByName;

    /*
    Type of data field
     */
    protected QTFieldType type = QTFieldType.NONE;

    /*
    Extraction path in source
     */
    protected String path;

    /*
    Translate to English
     */
    protected boolean translate2En;

    /*
    Autodetect the type
     */
    protected boolean autoDetect;

    /*
    Name of mapping file that should be applied on extractions
     */
    protected String mapping;

    /*
    Depth of extrcation: L1 or L2
     */
    protected String level;

    /*
    Rules to be applied in extracted data
     */
    protected ArrayList<String []> regexReplace;

    /*
    Rules to be applied in extracted data
     */
    protected ArrayList<String []> regexSelect;

    /*
    if not null split the value for field. Field will have an array of values
     */
    protected String splitter;

    /*
    Is this field indexable?
     */
    protected boolean index = true;

    /*
    Is this field required
     */
    protected boolean required;

    /*
    Field has an array of nested childern
     */
    protected QTField [] fields;

    public boolean isSortByName() {
        return isSortByName;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public int getNumBuckets() {
        return numBuckets;
    }

    public boolean isTranslate2En() {
        return translate2En;
    }

    public QTFieldType getType() {
        return type;
    }

    public String getFname() {
        return fname;
    }

    public String getSname() {
        return sname;
    }

    public String getPath() {
        return path;
    }

    public void setAutoDetect(boolean autoDetect) {
        this.autoDetect = autoDetect;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public ArrayList<String[]> getRegexReplace() {
        return regexReplace;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setHasValue(boolean hasValue) {
        this.hasValue = hasValue;
    }

    public ArrayList<String[]> getRegexSelect() {
        return regexSelect;
    }

    public void setNumBuckets(int numBuckets) {
        this.numBuckets = numBuckets;
    }

    public String getLevel() {
        return level;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public boolean isIndex() {
        return index;
    }

    public boolean isRequired() {
        return required;
    }

    public QTField[] getFields() {
        return fields;
    }

    public void setSortByName(boolean sortByName) {
        isSortByName = sortByName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTranslate2En(boolean translate2En) {
        this.translate2En = translate2En;
    }

    public String getMapping() {
        return mapping;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSplitter() {
        return splitter;
    }

    public void setFields(QTField[] fields) {
        this.fields = fields;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public void setRegexReplace(ArrayList<String[]> regexReplace) {
        this.regexReplace = regexReplace;
    }

    public void setRegexSelect(ArrayList<String[]> regexSelect) {
        this.regexSelect = regexSelect;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setSplitter(String splitter) {
        this.splitter = splitter;
    }

    public void setType(QTFieldType type) {
        this.type = type;
    }

    public boolean isAutoDetect() {
        return autoDetect;
    }

    public boolean isFilter() {
        return isFilter;
    }
}
