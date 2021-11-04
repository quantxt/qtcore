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
    Number of documents that had meaningful values and kept by the engine
    */
    private Long number_documents_out;

    /*
    Number of results - This is essentially number of input segments that had one or more values
    */
    private Long number_of_results;

    /*
    Number of pages fron input documents
    */
    private Long number_pages;

    /*
    Total number of bytes of all input text
    */
    private Long number_bytes_in;

    /*
    Time took in ms for processing: qtcurate_end-qtcurate_start
    */
    private Long took;

    /*
    Time data collection was kicked off
    */
    private Long start_time;

    /*
    Time the engine is booted and can read from kinesis
    */
    private Long qtcurate_start;

    /*
    Time the engine starts processing
    */
    private Long qtcurate_process_start;

    /*
    Time the engine finished processing data
    */
    private Long qtcurate_end;

    public QTInsight(){

    }

    public Long getNumber_bytes_in() {
        return number_bytes_in;
    }

    public Long getNumber_documents_in() {
        return number_documents_in;
    }

    public Long getNumber_documents_out() {
        return number_documents_out;
    }

    public Long getNumber_of_results() {
        return number_of_results;
    }

    public Long getStart_time() {
        return start_time;
    }

    public Long getTook() {
        return took;
    }

    public Long getQtcurate_end() {
        return qtcurate_end;
    }

    public Long getQtcurate_process_start() {
        return qtcurate_process_start;
    }

    public Long getQtcurate_start() {
        return qtcurate_start;
    }

    public void setNumber_bytes_in(Long number_bytes_in) {
        this.number_bytes_in = number_bytes_in;
    }

    public void setNumber_documents_in(Long number_documents_in) {
        this.number_documents_in = number_documents_in;
    }

    public void setNumber_documents_out(Long number_documents_out) {
        this.number_documents_out = number_documents_out;
    }

    public void setNumber_of_results(Long number_of_results) {
        this.number_of_results = number_of_results;
    }

    public void setStart_time(Long start_time) {
        this.start_time = start_time;
    }

    public void setQtcurate_end(Long qtcurate_end) {
        this.qtcurate_end = qtcurate_end;
    }

    public void setQtcurate_process_start(Long qtcurate_process_start) {
        this.qtcurate_process_start = qtcurate_process_start;
    }

    public void setQtcurate_start(Long qtcurate_start) {
        this.qtcurate_start = qtcurate_start;
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
}
