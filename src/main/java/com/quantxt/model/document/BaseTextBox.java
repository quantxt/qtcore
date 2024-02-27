package com.quantxt.model.document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseTextBox implements Serializable {

    private static final long serialVersionUID = 5943489568794179775L;

    protected int page;  // 1-based
    protected int line;  // 0-based
    protected int start; // 0-based - From begining of the line
    protected int end;   // 0-based - From begining of the line

    protected float top;   // starty
    protected float base;  // endy
    protected float left;  // startx
    protected float right; // endx
    protected String str;

    protected float width;   // starty
    protected float height;  // endy

    protected transient List<BaseTextBox> childs = new ArrayList<>();
    protected transient String line_str;

    public BaseTextBox() {

    }

    public BaseTextBox(float t, float b, float l, float r, String s) {
        setTop(t);
        setBase(b);
        setLeft(l);
        setRight(r);
        setStr(s);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getBase() {
        return base;
    }

    public void setBase(float base) {
        this.base = base;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public int hashCode()
    {
        return (int)right * 31 + (int)left * 31 * str.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        BaseTextBox obj = (BaseTextBox) o;
        return obj.getLeft() == left && obj.getRight() == right &&
                obj.getTop() == top && obj.getBase() == base;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<BaseTextBox> getChilds() {
        return childs;
    }

    public void setChilds(List<BaseTextBox> childs) {
        this.childs = childs;
    }

    public String getLine_str() {
        return line_str;
    }

    public void setLine_str(String line_str) {
        this.line_str = line_str;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}