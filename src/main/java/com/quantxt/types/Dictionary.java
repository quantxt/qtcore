package com.quantxt.types;

import com.quantxt.helper.types.QTField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Search Dictionary model.
 *
 * @author Matin Kamali
 */

@Getter
@Setter
public class Dictionary {

    final private static Pattern default_kay_padding = Pattern.compile("^\\s+");

    final private Map<String, List<DictItm>> vocab_map;
    protected QTField.QTFieldType valType;
    protected Integer search_distance;
    protected Pattern keyPadding;
    protected Pattern pattern;
    protected int [] groups;
    protected String name;


    public Dictionary(Map<String, List<DictItm>> vocab_map){
        this.vocab_map = vocab_map;
        this.valType = QTField.QTFieldType.NONE;
        this.search_distance = 0;
        this.keyPadding = default_kay_padding;
        this.pattern = null;
        this.groups = null;
        this.name = null;
    }

    public Dictionary(Map<String, List<DictItm>> vocab_map,
                      String name,
                      QTField.QTFieldType valType,
                      int search_distance,
                      Pattern keyPadding,
                      Pattern pattern,
                      int [] groups) {
        this.vocab_map = vocab_map;
        this.valType = valType;
        this.search_distance = search_distance;
        this.keyPadding = keyPadding;
        this.pattern = pattern;
        this.groups = groups;
        this.name = name;
    }
}