package com.quantxt.helper.types;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class QTField {
    public enum QTFieldType {SHORT, INT, LONG, FLOAT, DOUBLE, STRING, KEYWORD, BOOL, DATETIME,
        NOUN, VERB, PERCENT, MONEY, NONE}

    /*
    Full name of the data field
     */
    private String fname;

    /*
    Short name for the data field
     */
    private String sname;

    /*
    Is the field aggregable?
     */
    private boolean isFilter;

    /*
    This field processed by a Value; mostly for KEYWORD and STRING fields
     */
    private boolean hasValue;

    /*
    number of bukcets
     */

    private int numBuckets;

    /*
    Sort by name or count
     */
    private boolean isSortByName;

    /*
    Type of data field
     */
    private QTFieldType type = QTFieldType.KEYWORD;

    /*
    Extraction path in source
     */
    private String path;

    /*
    Translate to English
     */
    private boolean translate2En;

    /*
    Autodetect the type
     */
    private boolean autoDetect;

    /*
    Name of mapping file that should be applied on extractions
     */
    private String mapping;

    /*
    Depth of extrcation: L1 or L2
     */
    private String level;

    /*
    Rules to be applied in extracted data
     */
    private ArrayList<String []> regexReplace;

    /*
    Rules to be applied in extracted data
     */
    private ArrayList<String []> regexSelect;

    /*
    if not null split the value for field. Field will have an array of values
     */
    private String splitter;

    /*
    Is this field indexable?
     */
    private boolean index = true;

    /*
    Is this field required
     */
    private boolean required;

    /*
    Field has an array of nested childern
     */
    private QTField [] fields;

    public QTField(){

    }
}
