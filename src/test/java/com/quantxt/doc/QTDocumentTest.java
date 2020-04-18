package com.quantxt.doc;

import com.quantxt.interval.Interval;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class QTDocumentTest {

    @Test
    public void intervalTest1() {
        String str = "This is a great location\n" +
                " Total Area   \n" +
                "but this is a gap\n"+
                "     1546     \n" +
                "  23    \n";

        Interval interval1 = new Interval(26, 36);
        Interval interval2 = new Interval(63, 67);
        Interval interval3 = new Interval(75, 77);

        Assert.assertEquals(str.substring(interval1.getStart(), interval1.getEnd()), "Total Area");
        Assert.assertEquals(str.substring(interval2.getStart(), interval2.getEnd()), "1546");
        Assert.assertEquals(str.substring(interval3.getStart(), interval3.getEnd()), "23");

    }

    class TestQTDocument extends QTDocument {

        public TestQTDocument(String b, String t, QTDocumentHelper helper) {
            super(b, t, helper);
        }

        @Override
        public List<QTDocument> getChunks(CHUNK chunk) {
            return null;
        }

        @Override
        public String Translate(String text, Language inLang, Language outLang) {
            return null;
        }

        @Override
        public boolean isStatement(String s) {
            return false;
        }
    }
}
