package com.quantxt.helper.types;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class QTField {
    public enum QTFieldType {SHORT, INT, LONG, FLOAT, DOUBLE, STRING, KEYWORD, BOOL, DATETIME,
        NOUN, VERB, PERCENT, MONEY, NONE}

    /*
    Full name of the data field
     */
    protected String fname;

    /*
    Short name for the data field
     */
    protected String sname;

    /*
    Is the field aggregable?
     */
    protected boolean isFilter;

    /*
    This field processed by a Value; mostly for KEYWORD and STRING fields
     */
    protected boolean hasValue;

    /*
    number of bukcets
     */

    protected int numBuckets;

    /*
    Sort by name or count
     */
    protected boolean isSortByName;

    /*
    Type of data field
     */
    protected QTFieldType type = QTFieldType.NONE;

    /*
    Extraction path in source
     */
    protected String path;

    /*
    Translate to English
     */
    protected boolean translate2En;

    /*
    Autodetect the type
     */
    protected boolean autoDetect;

    /*
    Name of mapping file that should be applied on extractions
     */
    protected String mapping;

    /*
    Depth of extrcation: L1 or L2
     */
    protected String level;

    /*
    Rules to be applied in extracted data
     */
    protected ArrayList<String []> regexReplace;

    /*
    Rules to be applied in extracted data
     */
    protected ArrayList<String []> regexSelect;

    /*
    if not null split the value for field. Field will have an array of values
     */
    protected String splitter;

    /*
    Is this field indexable?
     */
    protected boolean index = true;

    /*
    Is this field required
     */
    protected boolean required;

    /*
    Field has an array of nested childern
     */
    protected QTField [] fields;

    public void setIsFilter(boolean s){
        isFilter = s;
    }

    public boolean getIsFilter(){
        return isFilter;
    }

    public void setIsSortByName(boolean s){
        isSortByName = s;
    }

    public boolean getIsSortByName(){
        return isSortByName;
    }
}
