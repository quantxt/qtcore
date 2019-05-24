package com.quantxt.helper.types;

import org.jsoup.nodes.Element;

/**
 * Created by matin on 4/14/17.
 */

public class ElemScore {
    private Element elem;
    private int numWords;
    private int depth;
    private int index;
    private int numChilds;
    private int numShortTextChilds;
    private int numLongTextChilds;

    public static int getLevel(Element elem) {
        int level = 0;
        Element parent = elem.parent();
        while (parent != null){
            parent = parent.parent();
            level++;
        }
        return level;
    }

    public static int getLength(Element e){
        String eOwnText = e.ownText().toLowerCase().replaceAll("[^a-z\\s]+" , "").trim();
        int eTextNumWords = eOwnText.split("\\s+").length;
        return  eTextNumWords;
    }

    public ElemScore(Element e){
        elem = e;
        depth = getLevel(e);
        numWords = getLength(e);
    }
}
