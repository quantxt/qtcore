package com.quantxt.model;

import com.quantxt.model.document.BaseTextBox;

import java.util.List;

public class Interval {

    protected int start;
    protected int end;
    protected String str;
    protected Integer line;

    protected Integer page;
    protected List<BaseTextBox> textBoxes;

    public Interval(){
        this.start = -1;
        this.end = Integer.MAX_VALUE;
    }

    public Interval(int start, int end){
        this.start = start;
        this.end = end;
    }

    public void setStart(int start){this.start = start;}
    public void setEnd(int end){this.end = end;}

    public void setLine(int line){
        this.line = line;
    }

    public String getStr() {
        return str;
    }

    public int getEnd(){
        return end;
    }

    public int getStart(){
        return start;
    }

    public Integer getLine(){
        return line;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public int size() {
        return end - start + 1;
    }

    public boolean overlapsWith(final Interval other) {
        return this.start <= other.getEnd() &&
                this.end >= other.getStart();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Interval)) {
            return false;
        }
        Interval other = (Interval) o;
        return this.start == other.getStart() &&
                this.end == other.getEnd();
    }

    @Override
    public int hashCode() {
        return this.start % 100 + this.end % 100;
    }

    public int compareTo(Object o) {
        if (!(o instanceof Interval)) {
            return -1;
        }
        Interval other = (Interval) o;
        int comparison = this.start - other.getStart();
        return comparison != 0 ? comparison : this.end - other.getEnd();
    }

    public List<BaseTextBox> getTextBoxes() {
        return textBoxes;
    }

    public void setTextBoxes(List<BaseTextBox> textBoxes) {
        this.textBoxes = textBoxes;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "start=" + start +
                ", end=" + end +
                ", str='" + str +
                ", line=" + line;
    }
}
