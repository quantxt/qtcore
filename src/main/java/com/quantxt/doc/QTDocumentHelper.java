package com.quantxt.doc;

import java.util.List;
import java.util.Set;

import com.quantxt.helper.types.ExtIntervalSimple;

public interface QTDocumentHelper {

    List<String> tokenize(String str);

    List<String> stemmer(String str);

    String normalize(String string);

    String removeStopWords(String string);

    String[] getSentences(String text);

    String[] getPosTags(String[] text);

    boolean isSentence(String str, List<String> tokens);

    Set<String> getStopwords();

    boolean isStopWord(String p);

    String getValues(String orig, String context, List<ExtIntervalSimple> list);

}
