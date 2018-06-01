package com.quantxt.doc;

import com.quantxt.trie.Emit;

import java.util.Collection;
import java.util.Map;

/**
 * Created by matin on 8/18/17.
 */
public interface QTExtract {

    public double [] tag(String str);
    public double terSimilarity(String str1, String str2);
    public Map<String, Collection<Emit>> parseNames(String str);
    public boolean hasEntities();

}
