package com.quantxt.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantxt.model.document.BaseTextBox;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Object representing stored search document details.
 *
 * @author Branko Ostojic
 */

public class SearchDocument {

    /**
     * Stored file name.
     */
    private String uuid;
    /**
     * Owner username.
     */
    private String username;
    /**
     * File original name.
     */
    private String fileName;


    private String link;

    /**
     * Search document content input stream.
     */
    @JsonIgnore
    private InputStream inputStream;
    /**
     * Content of the inputstream pulled by the appropriate reader.
     */
    @JsonIgnore
    private List<String> body;

    /**
     * File content type.
     */
    private String contentType;

    private String [] langs;

    /**
     * number of text unit : number of pages for paginated documents
     * and number of characters / 3000 for the rest
     */
    private int numUnits;

    private List<List<BaseTextBox>> textBoxes;

    public String[] getLangs() {
        return langs;
    }

    public void setLangs(String[] langs) {
        this.langs = langs;
    }

    public SearchDocument(){

    }

    public String getUuid() {
        return uuid;
    }

    public List<String> getBody() {
        return body;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUsername() {
        return username;
    }

    public SearchDocument setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }


    public SearchDocument setUsername(String username) {
        this.username = username;
        return this;
    }


    public SearchDocument setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public SearchDocument setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public SearchDocument setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public SearchDocument setBody(List<String> body) {
        this.body = body;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof SearchDocument)) return false;
        SearchDocument other = (SearchDocument) o;
        if (other.uuid.equals(((SearchDocument) o).uuid)) return true;
        return false;
    }

    public List<List<BaseTextBox>> getTextBoxes() {
        return textBoxes;
    }

    public void setTextBoxes(List<List<BaseTextBox>> textBoxes) {
        this.textBoxes = textBoxes;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = (result*PRIME) + this.uuid.hashCode();
        result = (result*PRIME) + this.username.hashCode();
        return result;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public void setNumUnits(int numUnits) {
        this.numUnits = numUnits;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
