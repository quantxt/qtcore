package com.quantxt.types;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Search Dictionary model.
 *
 * @author Matin Kamali
 */

public  class Dictionary implements Serializable {

    private static final long serialVersionUID = -5854156830214968792L;

    public enum ExtractionType {NUMBER, DATETIME, REGEX };

    final private static Pattern default_skip_between_value = Pattern.compile("^\\s+$");
    final private static Pattern default_skip_between_key_and_value = Pattern.compile("^[\\s\"'\\/\\\\\\.\\(\\)\\-\\_\\&\\%\\~\\?\\@\\$\\:;%,]+$");

    final private List<DictItm> vocab; // category to str s
    final protected String name;
    final protected String id;
    protected ExtractionType valType;
    protected Pattern pattern;
    protected Pattern skip_between_values;
    protected Pattern skip_between_key_and_value;
    protected int [] groups;

    public Dictionary(String id,
                      String name,
                      List<DictItm> vocab){
        this.vocab = vocab;
        this.name = name;
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.skip_between_values = default_skip_between_value;
        this.skip_between_key_and_value = default_skip_between_key_and_value;
        this.groups = null;
        this.pattern = null;
    }

    public Dictionary(List<DictItm> vocab,
                      String id,
                      String name,
                      ExtractionType valType,
                      Pattern skip_between_key_and_value,
                      Pattern skip_between_values,
                      Pattern pattern,
                      int [] groups) {
        this.vocab = vocab;
        this.valType = valType;
        this.skip_between_values = skip_between_values != null ? skip_between_values :
                default_skip_between_value;
        this.groups = groups;
        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.name = name;
        this.pattern = pattern;
        this.skip_between_key_and_value = skip_between_key_and_value != null ? skip_between_key_and_value
                :default_skip_between_key_and_value;
    }

    public List<DictItm> getVocab() {
        return vocab;
    }

    public int[] getGroups() {
        return groups;
    }

    public ExtractionType getValType() {
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

    public String getId() {
        return id;
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

    public void setValType(ExtractionType valType) {
        this.valType = valType;
    }
}