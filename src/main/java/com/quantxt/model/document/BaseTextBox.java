package com.quantxt.model.document;

import java.io.Serializable;

public class BaseTextBox implements Serializable {

    private static final long serialVersionUID = 5943489568794179775L;

    private int page;  // 1-based
    private float top;   // starty
    private float base;  // endy
    private float left;  // startx
    private float right; // endx
    private String str;

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

}