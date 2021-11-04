package com.quantxt.model;

public class QTField {

    public enum DataType {LONG, KEYWORD, DATETIME, STRING, DOUBLE, PERCENT, MONEY, VERB, NOUN}

    public QTField() {

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
    protected boolean filter;

    /*
    Is this field required
     */
    protected boolean required;

    /*
    How to pull text out of HTML components
     */

    public String getFname() {
        return fname;
    }

    public String getSname() {
        return sname;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isFilter() {
        return filter;
    }
}
