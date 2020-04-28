package com.quantxt.types;

import java.io.Serializable;
import java.util.Collection;

public abstract class DictSearch<T> implements Serializable {

    private static final long serialVersionUID = 3935799515300870082L;

    public enum Mode {
        ORDERED_SPAN, FUZZY_ORDERED_SPAN, SPAN, FUZZY_SPAN, PARTIAL_SPAN,
        PARTIAL_FUZZY_SPAN
    }

    public enum AnalyzType {
        EXACT, EXACT_CI, WHITESPACE, LETTER, SIMPLE, STANDARD, STEM
    }

    protected Mode [] mode = new Mode [] {Mode.ORDERED_SPAN};
    protected AnalyzType [] analyzType = new AnalyzType[] {AnalyzType.STANDARD};
    protected Dictionary dictionary;

    public abstract Collection<T> search(final String query_string);

    public void setAnalyzType(AnalyzType[] analyzType) {
        this.analyzType = analyzType;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setMode(Mode[] mode) {
        this.mode = mode;
    }

    public AnalyzType[] getAnalyzType() {
        return analyzType;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public Mode[] getMode() {
        return mode;
    }
}
