package com.quantxt.model;

import com.quantxt.model.document.BaseTextBox;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public abstract class DictSearch<T, E> implements Serializable {

    private static final long serialVersionUID = 3935799515300870082L;

    public enum Mode {
        ORDERED_SPAN, FUZZY_ORDERED_SPAN, SPAN, FUZZY_SPAN, PARTIAL_SPAN,
        PARTIAL_FUZZY_SPAN, PARTIAL_ORDERED_SPAN, PARTIAL_FUZZY_ORDERED_SPAN
    }

    public enum AnalyzType {
        EXACT, EXACT_CI, WHITESPACE, LETTER, SIMPLE, STANDARD, STEM
    }

    protected Mode [] mode = new Mode [] {Mode.ORDERED_SPAN};
    protected AnalyzType [] analyzType = new AnalyzType[] {AnalyzType.STANDARD};
    protected Dictionary dictionary;

    protected Dictionary negativeDictionary;

    public abstract Collection<T> search(final String query_string);

    public abstract Collection<T> search(final String query_string, int slop);

    public abstract Collection<E> search(final String query_string, Map<Integer, BaseTextBox> lineTextBoxMap, int slop, boolean isolatedLabelsOnly);

    public void setAnalyzType(AnalyzType[] analyzType) {
        this.analyzType = analyzType;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setNegativeDictionary(Dictionary dictionary) {
        this.negativeDictionary = dictionary;
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
