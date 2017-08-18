package com.quantxt.doc;

import com.quantxt.trie.Emit;

import java.util.Collection;

/**
 * Created by matin on 8/18/17.
 */
public interface QTExtract {

    public double [] tag(String str);
    public Collection<Emit> parseNames(String str);
    public Collection<Emit> parseTitles(String str);

}
