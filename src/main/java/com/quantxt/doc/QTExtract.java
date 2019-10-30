package com.quantxt.doc;

import com.quantxt.helper.types.QTField;
import com.quantxt.trie.Emit;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by matin on 8/18/17.
 */

@Deprecated
public interface QTExtract {

    double [] tag(String str);
    double terSimilarity(String str1, String str2);
    Map<String, Collection<Emit>> parseNames(String str);
    boolean hasEntities();

    QTField.QTFieldType getType();
    void setType(QTField.QTFieldType type);
    Pattern getPattern();
    void setPattern(Pattern ptr);

    int [] getGroups();
    void setGroups(int [] groups);

}
