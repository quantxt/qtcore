package com.quantxt.model.document;

import com.quantxt.model.ExtInterval;

import java.util.ArrayList;
import java.util.List;

public class ExtIntervalTextBox {

    final private ExtInterval extInterval;
    public ExtIntervalTextBox(ExtInterval e, BaseTextBox tb){
        extInterval = e;
        if (tb != null) {
            List<BaseTextBox> bts = new ArrayList<>();
            bts.add(tb);
            extInterval.setTextBoxes(bts);
        }
    }

    public ExtInterval getExtInterval() {
        return extInterval;
    }

    public BaseTextBox getTextBox() {
        if (extInterval.getTextBoxes() == null) return null;
        return extInterval.getTextBoxes().get(0);
    }

    public void setTextBox(BaseTextBox tb) {
        if (tb != null) {
            List<BaseTextBox> bts = new ArrayList<>();
            bts.add(tb);
            extInterval.setTextBoxes(bts);
        }
    }

}
