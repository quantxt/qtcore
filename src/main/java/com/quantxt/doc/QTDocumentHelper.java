package com.quantxt.doc;

import java.util.List;

import com.quantxt.model.DictSearch;
import com.quantxt.model.ExtInterval;
import com.quantxt.model.document.BaseTextBox;

public interface QTDocumentHelper {

    enum Language {
        ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
    }

    List<String> tokenize(String str);

    List<ExtInterval> extract(String content,
                              List<DictSearch> extractDictionaries,
                              List<BaseTextBox> textBoxes,
                              boolean searchVertical);
}
