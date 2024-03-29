package com.quantxt.model.search;

import java.io.Serializable;
import java.util.List;

public class RawDocument implements Cloneable, Serializable {
    private static final long serialVersionUID = 2923554776254897664L;

    private List<String> body;
    private String source;
    private String link;

    public List<String> getBody() {
        return body;
    }

    public String getSource() {
        return source;
    }

    public String getLink() {
        return link;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setLink(String link) {
        this.link = link;
    }

}