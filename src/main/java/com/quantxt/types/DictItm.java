package com.quantxt.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DictItm implements Serializable {
    private static final long serialVersionUID = -1878032088113144067L;

    private String category;
    private List<String> phraseList;

    public DictItm(String k, List<String> v){
        category = k;
        phraseList = v;
    }

    public String getCategory(){
        return category;
    }

    public List<String> getPhraseList(){
        return phraseList;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setPhraseList(List<String> phraseList){
        this.phraseList = phraseList;
    }

    public DictItm(String category, String v1, String... values){
        this.category = category;
        this.phraseList = new ArrayList<>();
        this.phraseList.add(v1);
        for (String v : values){
            this.phraseList.add(v);
        }
    }

}
