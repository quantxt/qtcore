package com.quantxt.doc;

import java.util.List;
import java.util.Set;

import com.quantxt.types.ExtIntervalSimple;

public interface QTDocumentHelper {

    List<String> tokenize(String str);

    String[] getSentences(String text);

    String[] getPosTags(String[] text);

    boolean isSentence(String str, List<String> tokens);

    Set<String> getStopwords();

    String getValues(String orig, String context, List<ExtIntervalSimple> list);
}
