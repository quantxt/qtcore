package com.quantxt.trie;

import com.quantxt.interval.Interval;
import com.quantxt.interval.Intervalable;

public class Emit extends Interval implements Intervalable {

    private final String keyword;
    private Object customeData;

    public Emit(final int start, final int end, final String keyword) {
        super(start, end);
        this.keyword = keyword;
    }

    public Emit(final int start, final int end, final String keyword, final Object c) {
        super(start, end);
        this.keyword = keyword;
        customeData = c;
    }

    public void addCustomeData(Object c){
        customeData = c;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public Object getCustomeData(){
        return this.customeData;
    }

    @Override
    public String toString() {
        return super.toString() + "=" + this.keyword;
    }
}
