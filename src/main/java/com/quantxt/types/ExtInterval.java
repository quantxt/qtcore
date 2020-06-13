package com.quantxt.types;

import java.util.ArrayList;

/**
 * Created by matin on 5/20/17.
 */

public class ExtInterval extends Interval {

    private Dictionary.ExtractionType type;
    private String category;
    private String dict_name;
    private String dict_id;
    private ArrayList<ExtIntervalSimple> extIntervalSimples;

    public ExtInterval() {
        super();
    }

    public ExtInterval(int start, int end){
        super(start, end);
    }

    public String getDict_name() {
        return dict_name;
    }

    public String getDict_id() {
        return dict_id;
    }

    public String getCategory() {
        return category;
    }

    public ArrayList<ExtIntervalSimple> getExtIntervalSimples(){
        return extIntervalSimples;
    }

    public Dictionary.ExtractionType getType() { return type; }

    public void setDict_name(String dict_name) {
        this.dict_name = dict_name;
    }

    public void setDict_id(String dict_id) {
        this.dict_id = dict_id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setExtIntervalSimples(ArrayList<ExtIntervalSimple> extIntervalSimples){
        this.extIntervalSimples = extIntervalSimples;
    }

    public void setType(Dictionary.ExtractionType type){
        this.type = type;
    }

}
