package com.quantxt.doc;

import java.util.List;

import com.quantxt.model.DictSearch;
import com.quantxt.model.ExtInterval;
import com.quantxt.model.document.TextBox;

public interface QTDocumentHelper {

    List<String> tokenize(String str);

    List<ExtInterval> extract(String content,
                              List<DictSearch> extractDictionaries,
                              List<TextBox> textBoxes,
                              boolean searchVertical);
}
