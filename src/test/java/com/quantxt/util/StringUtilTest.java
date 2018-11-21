package com.quantxt.util;

import com.quantxt.helper.types.ExtInterval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.quantxt.util.StringUtil.findAllSpans;
import static com.quantxt.util.StringUtil.findSpan;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by matin on 2/2/18.
 */
public class StringUtilTest {

    @Test
    public void findMatch_1() {
        String str = "Gilead Sciences Company Profile Gilead Sciences, Inc. "
                + "is a research-based biopharmaceutical company that discovers, "
                + "develops and commercializes medicines in areas of unmet medical need .";
        List<String> tokenlist = new ArrayList<>();
        tokenlist.add("medicines");
        tokenlist.add("in");
        tokenlist.add("areas");
        tokenlist.add("of");
        tokenlist.add("unmet");
        ExtInterval ex = findSpan(str, tokenlist);

        assertNotNull(ex);
        assertEquals(ex.getStart(), 144);
        assertEquals(ex.getEnd(), 171);
    }

    @Test
    public void findMatch_2() {
        String str = "Gilead Sciences Company Profile Gilead Sciences, Inc. "
                + "is a research-based biopharmaceutical company that discovers, "
                + "develops and commercializes medicines in areas of unmet medical need .";
        List<String> tokenlist = new ArrayList<>();
        tokenlist.add("Gilead");
        tokenlist.add("Sciences");
        tokenlist.add("Company");
        tokenlist.add("Profile");
        tokenlist.add("Gilead");
        tokenlist.add("Sciences");
        ExtInterval ex = findSpan(str, tokenlist);

        assertNotNull(ex);
        assertEquals(ex.getStart(), 0);
        assertEquals(ex.getEnd(), 47);
    }

    @Test
    public void findMatch_3() {
        String str = "One positive of these devices is that they aren't cabled to a computer .";
        List<String> tokenlist = new ArrayList<>();
        tokenlist.add("are");
        tokenlist.add("n't");
        tokenlist.add("cabled");
        ExtInterval ex = findSpan(str, tokenlist);

        assertNotNull(ex);
        assertEquals(ex.getStart(), 43);
        assertEquals(ex.getEnd(), 56);
    }

    @Test
    public void findMatch_4() {
        String str = "One positive of these devices is that they aren't cabled to a computer .";
        String [] tokens = new String[]{"positi", "devic" , "comput"};
        ExtInterval [] ex = findAllSpans(str, tokens);

        assertNotNull(ex);
        assertEquals(ex[0].getStart(), 4);
        assertEquals(ex[1].getStart(), 22);
        assertEquals(ex[2].getEnd(), 68);
    }

    @Test
    public void findMatch_5() {
        String str = "みんなのマイリビングは北九州エリアの住宅情報満載";
        String [] tokens = new String[]{"みんな", "ングは" , "の住宅"};

        ExtInterval [] ex = findAllSpans(str, tokens);

        assertNotNull(ex);
        assertEquals(ex[0].getStart(), 0);
        assertEquals(ex[1].getStart(), 8);
        assertEquals(ex[2].getEnd(), 20);
    }

}
