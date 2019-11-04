package com.quantxt.types;

import com.quantxt.trie.Emit;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class DictSearch {

    public enum Mode {
        ORDERED_SPAN, FUZZY_ORDERED_SPAN, SPAN, FUZZY_SPAN
    }

    public enum AnalyzType {
        EXACT, EXACT_CI, WHITESPACE, SIMPLE, STANDARD, STEM
    }

    protected Mode mode = Mode.ORDERED_SPAN;
    protected AnalyzType analyzType = AnalyzType.STANDARD;
    protected Dictionary dictionary;

    public Map<String, Collection<Emit>> search(final String query_string)
    {
        //implement this
        return null;
    }
}
