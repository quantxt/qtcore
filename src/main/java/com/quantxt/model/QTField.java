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
    How to pull text out of HTML components
     */

    public String getFname() {
        return fname;
    }

    public String getSname() {
        return sname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }
}
