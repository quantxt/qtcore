package com.quantxt.doc;

import java.util.List;
import java.util.Set;

import com.quantxt.types.DictSearch;
import com.quantxt.types.ExtIntervalSimple;

public interface QTDocumentHelper {

    List<String> tokenize(String str);

    String[] getSentences(String text);

    String[] getPosTags(String[] text);

    String extractHtmlExcerptForDocument(QTDocument qtDocument);

    String getValues(String orig, String context, List<ExtIntervalSimple> list);

    void extract(QTDocument qtDocument,
                 List<DictSearch> extractDictionaries,
                 boolean canSearchVertical,
                 String context);

}
