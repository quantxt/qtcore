package com.quantxt.model.document;

import com.quantxt.model.ExtInterval;

public class ExtIntervalTextBox {

    final private ExtInterval extInterval;
    private BaseTextBox textBox;

    public ExtIntervalTextBox(ExtInterval e, BaseTextBox tb){
        extInterval = e;
        textBox = tb;
    }

    public ExtInterval getExtInterval() {
        return extInterval;
    }

    public BaseTextBox getTextBox() {
        return textBox;
    }

    public void setTextBox(BaseTextBox textBox) {
        this.textBox = textBox;
    }

}
