package com.quantxt.types;

import com.quantxt.helper.types.QTField;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Search Dictionary model.
 *
 * @author Matin Kamali
 */

public  class Dictionary implements Serializable {

    private static final long serialVersionUID = -5854156830214968792L;

    final private static Pattern default_skip_between_value = Pattern.compile("^\\s+$");
    final private static Pattern default_skip_between_key_and_value = Pattern.compile("^[\\s\"'\\(\\)\\-\\:;%,]+$");


    final private Map<String, List<DictItm>> vocab_map;
    final protected String name;
    protected QTField.QTFieldType valType;
    protected Pattern pattern;
    protected Pattern skip_between_values;
    protected Pattern skip_between_key_and_value;
    protected int [] groups;

    public Dictionary(String name,
                      Map<String, List<DictItm>> vocab_map){
        this.vocab_map = vocab_map;
        this.name = name;
        this.valType = QTField.QTFieldType.NONE;
        this.skip_between_values = default_skip_between_value;
        this.skip_between_key_and_value = default_skip_between_key_and_value;
        this.groups = null;
        this.pattern = null;
    }

    public Dictionary(Map<String, List<DictItm>> vocab_map,
                      String name,
                      QTField.QTFieldType valType,
                      Pattern skip_between_key_and_value,
                      Pattern skip_between_values,
                      Pattern pattern,
                      int [] groups) {
        this.vocab_map = vocab_map;
        this.valType = valType;
        this.skip_between_values = skip_between_values != null ? skip_between_values :
                default_skip_between_value;
        this.groups = groups;
        this.name = name;
        this.pattern = pattern;
        this.skip_between_key_and_value = skip_between_key_and_value != null ? skip_between_key_and_value
                :default_skip_between_key_and_value;
    }

    public Map<String, List<DictItm>> getVocab_map() {
        return vocab_map;
    }

    public int[] getGroups() {
        return groups;
    }

    public QTField.QTFieldType getValType() {
        return valType;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Pattern getSkip_between_key_and_value() {
        return skip_between_key_and_value;
    }

    public Pattern getSkip_between_values() {
        return skip_between_values;
    }

    public String getName() {
        return name;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public void setGroups(int[] groups) {
        this.groups = groups;
    }

    public void setSkip_between_key_and_value(Pattern skip_between_key_and_value) {
        this.skip_between_key_and_value = skip_between_key_and_value;
    }

    public void setSkip_between_values(Pattern skip_between_values) {
        this.skip_between_values = skip_between_values;
    }

    public void setValType(QTField.QTFieldType valType) {
        this.valType = valType;
    }
}