package com.quantxt.doc;

import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;

import com.quantxt.doc.QTDocument.DOCTYPE;
import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Trie;

public interface QTDocumentHelper {

    List<String> tokenize(String str);

    List<String> stemmer(String str);

    String normalize(String string);

    public String[] getSentences(String text);

    String[] getPosTags(String[] text);

    List<ExtInterval> getNounAndVerbPhrases(String orig, String[] parts);

    Trie getVerbTree();

    CharArraySet getStopwords();

    Set<String> getPronouns();

    DOCTYPE getVerbType(String verbPhs);

    boolean isStopWord(String p);

}
