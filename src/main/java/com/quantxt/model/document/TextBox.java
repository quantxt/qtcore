package com.quantxt.model.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TextBox implements Comparable<TextBox> , Serializable {

    private static final long serialVersionUID = 1292615444754670471L;
    List<TextBox> childs;
    private int page;
    private float page_height;   // starty
    private float page_width;  // endy

    private int line;
    private String line_str;

    private float top;   // starty
    private float base;  // endy
    private float left;  // startx
    private float right; // endx
    private String str;
    private boolean processed;

    public TextBox(){

    }
    public TextBox(float t, float b, float l, float r, String s) {
        this.top = t;
        this.base = b;
        this.left = l;
        this.right = r;
        this.str = s.trim();
        childs = new ArrayList<>();
    }

    @Override
    public int hashCode()
    {
        return (int)right * 31 + (int)left * 31 * str.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        TextBox obj = (TextBox) o;
        return obj.left == left && obj.right == right &&
                obj.top == top && obj.base == base;
    }

    @Override
    public int compareTo(TextBox that) {

        if (this.base == that.base && this.top == that.top) return 0;

        //primitive numbers follow this form
        if (this.base < that.base) return -1;
        if (this.base > that.base) return +1;

        return 0;
    }

    public float getTop() {
        return top;
    }

    public float getBase() {
        return base;
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public String getStr() {
        return str;
    }

    public String getLine_str() {
        return line_str;
    }

    public int getPage() {
        return page;
    }

    public int getLine() {
        return line;
    }

    public boolean isProcessed(){return processed;}

    public void setTop(float top) {
        this.top = top;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setLine_str(String line_str) {
        this.line_str = line_str;
    }

    public void setProcessed(boolean b){processed = b;}

    public List<TextBox> getChilds() {
        return childs;
    }

    public void setChilds(List<TextBox> childs) {
        this.childs = childs;
    }

    public float getPage_height() {
        return page_height;
    }

    public void setPage_height(float page_height) {
        this.page_height = page_height;
    }

    public float getPage_width() {
        return page_width;
    }

    public void setPage_width(float page_width) {
        this.page_width = page_width;
    }

    @Override
    public String toString(){
        if (childs.size() == 0) return str;
        StringBuilder sb = new StringBuilder();
        for (TextBox tb : childs){
            sb.append(tb.getStr()).append(" ");
        }
        return sb.toString().trim();
    }
}
