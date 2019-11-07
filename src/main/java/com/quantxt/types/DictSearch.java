package com.quantxt.types;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class DictSearch<T> {

    public enum Mode {
        ORDERED_SPAN, FUZZY_ORDERED_SPAN, SPAN, FUZZY_SPAN, PARTIAL_SPAN,
        PARTIAL_FUZZY_SPAN
    }

    public enum AnalyzType {
        EXACT, EXACT_CI, WHITESPACE, SIMPLE, STANDARD, STEM
    }

    protected Mode [] mode = new Mode [] {Mode.ORDERED_SPAN};
    protected AnalyzType [] analyzType = new AnalyzType[] {AnalyzType.STANDARD};
    protected Dictionary dictionary;

    public Collection<T> search(final String query_string)
    {
        //implement this
        return null;
    }
}
