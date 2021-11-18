package com.quantxt.model.search;

/**
 * Insigts into details of data processing by engine.
 *
 * @author Matin Kamali
 */

public class QTInsight {

    /*
    Number of documents or links sent into the engine
     */
    private Long number_documents_in;

    /*
    Number of pages fron input documents
    */
    private Long number_pages;

    /*
    Time took in ms for processing: qtcurate_end-qtcurate_start
    */
    private Long took;

    /*
    Extraction was kicked off
    */
    private Long start_time;

    /*
        Extraction was ended
        */
    private Long qtcurate_end;

    public QTInsight(){

    }

    public Long getNumber_documents_in() {
        return number_documents_in;
    }

    public Long getStart_time() {
        return start_time;
    }

    public Long getTook() {
        return took;
    }

    public void setNumber_documents_in(Long number_documents_in) {
        this.number_documents_in = number_documents_in;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public Long getNumber_pages() {
        return number_pages;
    }

    public void setNumber_pages(Long number_pages) {
        this.number_pages = number_pages;
    }

    public Long getQtcurate_end() {
        return qtcurate_end;
    }

    public void setQtcurate_end(Long qtcurate_end) {
        this.qtcurate_end = qtcurate_end;
    }
}
