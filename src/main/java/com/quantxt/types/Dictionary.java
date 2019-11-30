package com.quantxt.types;

import com.quantxt.helper.types.QTField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
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

public  class Dictionary implements Serializable {

    private static final long serialVersionUID = -5854156830214968792L;

    final private static Pattern default_skip_between_value = Pattern.compile("^\\s+$");
    final private static Pattern default_skip_between_key_and_value = Pattern.compile("^[\\s\"'\\(\\)\\-\\:;%,]+$");


    final private Map<String, List<DictItm>> vocab_map;
    protected QTField.QTFieldType valType;
    protected Integer search_distance; // min distance between key and value
    protected Pattern pattern;
    protected Pattern skip_between_values;
    protected Pattern skip_between_key_and_value;
    protected int [] groups;
    protected String name;


    public Dictionary(Map<String, List<DictItm>> vocab_map){
        this.vocab_map = vocab_map;
        this.valType = QTField.QTFieldType.NONE;
        this.search_distance = 1;
        this.skip_between_values = default_skip_between_value;
        this.skip_between_key_and_value = default_skip_between_key_and_value;
        this.groups = null;
        this.pattern = null;
        this.name = null;
    }

    public Dictionary(Map<String, List<DictItm>> vocab_map,
                      String name,
                      QTField.QTFieldType valType,
                      int search_distance,
                      Pattern skip_between_key_and_value,
                      Pattern skip_between_values,
                      Pattern pattern,
                      int [] groups) {
        this.vocab_map = vocab_map;
        this.valType = valType;
        this.search_distance = search_distance;
        this.skip_between_values = skip_between_values != null ? skip_between_values :
                default_skip_between_value;
        this.groups = groups;
        this.name = name;
        this.pattern = pattern;
        this.skip_between_key_and_value = skip_between_key_and_value != null ? skip_between_key_and_value
                :default_skip_between_key_and_value;
    }
}