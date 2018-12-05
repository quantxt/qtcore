package com.quantxt.doc;

import java.util.List;
import java.util.Set;

import com.quantxt.doc.QTDocument.DOCTYPE;
import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Trie;

public interface QTDocumentHelper {

    List<String> tokenize(String str);

    List<String> stemmer(String str);

    String normalize(String string);

    String removeStopWords(String string);

    public String[] getSentences(String text);

    String[] getPosTags(String[] text);

    List<ExtInterval> getNounAndVerbPhrases(String orig, String[] tokens);

    Trie getVerbTree();

    boolean isSentence(String str, List<String> tokens);

    Set<String> getStopwords();

    Set<String> getPronouns();

    DOCTYPE getVerbType(String verbPhs);

    boolean isStopWord(String p);

    String getValues(String orig, List<ExtInterval> list);
}
