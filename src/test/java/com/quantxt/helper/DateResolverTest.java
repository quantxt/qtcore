package com.quantxt.helper;

import com.quantxt.helper.types.ExtIntervalSimple;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by matin on 5/24/17.
 */
@Slf4j
public class DateResolverTest {

    @Test
    public void gulfTimes_1() {
        //http://www.gulf-times.com/story/546307/Bank-of-Japan-to-buy-less-of-short-maturity-bonds-
        LocalDateTime ed = LocalDateTime.parse("2017-04-30T20:31:00.000");
        Document doc = getDocument("/gulf-bank-of-japan-buy.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed +" but was " + date, date.equals(ed));
    }


    @Test
    public void nytimes_1() {
        //https://www.nytimes.com/2017/04/11/us/alabama-governor-robert-bentley-sex-scandal.html?hp&action=click&pgtype=Homepage&clickSource=story-heading&module=second-column-region&region=top-news&WT.nav=top-news
        LocalDateTime ed = LocalDateTime.parse("2017-04-12T09:40:25");
        Document doc = getDocument("/nytimes-alabama-christians.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void nytimes_2() {
        //https://www.nytimes.com/2017/04/11/us/politics/sean-spicer-hitler-gas-holocaust-center.html
        LocalDateTime ed = LocalDateTime.parse("2017-04-12T16:23:07");
        Document doc = getDocument("/nytimes-sean-spicer.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }


    @Test
    public void nytimes_3() {
        //https://www.nytimes.com/2017/04/11/technology/personaltech/a-hangout-for-old-desktop-notifications.html
        LocalDateTime ed = LocalDateTime.parse("2017-04-13T00:00:10");
        Document doc = getDocument("/nytime-a-hangout.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void washpost1() {
        //https://www.washingtonpost.com/news/politics/wp/2017/03/29/the-nunes-white-house-question-assessed-minute-by-minute/
        LocalDateTime ed = LocalDateTime.parse("2017-03-29T04:03:00");
        Document doc = getDocument("/washpost_the-nunes.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void nz_herald_1() {
        //http://www.nzherald.co.nz/business/news/article.cfm?c_id=3&objectid=11862084
        LocalDateTime ed = LocalDateTime.parse("2017-05-23T21:21:41");
        Document doc = getDocument("/nzherald_tower-first.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void econotimes_1() {
        //http://www.econotimes.com/Bank-of-Mexico-likely-to-keep-policy-rate-on-hold-204064
        LocalDateTime ed = LocalDateTime.parse("2016-05-04T13:12:00");
        Document doc = getDocument("/econotimes_bank-of-mexico.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void reuters_1() {
        //http://www.reuters.com/article/us-iran-russia-opec-deal-idUSKBN16Z0PM
        LocalDateTime ed = LocalDateTime.parse("2017-03-28T11:45:00.000");
        Document doc = getDocument("/reuters_us-iran.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }


    @Test
    @Ignore
    public void reuters_2() {
        //http://www.reuters.com/article/us-usa-fed-minutes-idUSKBN18K2L5
        LocalDateTime ed = LocalDateTime.parse("2017-05-24T14:59:00");
        Document doc = getDocument("/reuters_us-fed-today.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    @Ignore
    public void reuters_3(){
        //http://www.reuters.com/article/brief-japan-post-bank-receives-approval/brief-japan-post-bank-receives-approval-regarding-development-of-new-business-idUSL3N1JG1WY
        LocalDateTime ed = LocalDateTime.parse("2017-06-19T04:05:00");
        Document doc = getDocument("/japan_post_bank.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void cnbc_1() {
        //http://www.cnbc.com/2017/03/28/apple-iphone-suppliers-outlook-jpmorgan.html
        LocalDateTime ed = LocalDateTime.parse("2017-03-28T23:25:55");
        Document doc = getDocument("/cnbc_apple-iphone.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void bloomberg_1() {
        //https://www.bloomberg.com/news/articles/2017-03-27/tesla-model-3-ramp-up-aims-to-crush-bmw-and-mercedes
        LocalDateTime ed = LocalDateTime.parse("2017-03-27T06:00:07.783");
        Document doc = getDocument("/bloomberg_tesla-model.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void torontosun_1() {
        //http://www.torontosun.com/2016/11/24/bank-of-canada-announces-short-list-of-canadian-women-vying-to-be-on-banknote
        LocalDateTime ed = LocalDateTime.parse("2016-11-25T04:41:39");
        Document doc = getDocument("/torontosun_bank-of-canada.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void kitco_1() {
        //http://ekosvoice.com/2017/05/14/super-thursday-bank-of-england-preview.html
        LocalDateTime ed = LocalDateTime.parse("2017-05-14T00:00:00");
        Document doc = getDocument("/ekosvoice-super-thursday.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void tamu_1() {
        //http://today.tamu.edu/2016/08/26/mosbacher-institute-to-host-deputy-governor-of-the-bank-of-mexico/
        LocalDateTime ed = LocalDateTime.parse("2016-08-26T11:38:06");
        Document doc = getDocument("/tamu-mosbacher-institute.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void giftedviz_1() {
        //http://giftedviz.com/2017/05/17/bank-of-england-holds-rates-in-7-1-vote/
        LocalDateTime ed = LocalDateTime.parse("2017-05-17T10:58:00");
        Document doc = getDocument("/giftedviz_bank-of-england.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void gonefishingfish_1() {
        //http://gonefishingfish.com/2017/05/13/bank-of-england-cuts-growth-forecast-and-warns-real-wages/
        LocalDateTime ed = LocalDateTime.parse("2017-05-13T00:00:00");
        Document doc = getDocument("/gonefishingfish_bank-of-england.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void newswire_1() {
        //http://www.newswire.ca/news-releases/bank-of-canada-unveils-commemorative-bank-note-to-celebrate-canadas-150th-anniversary-of-confederation-618646013.html
        LocalDateTime ed = LocalDateTime.parse("2017-04-07T00:00:00");
        Document doc = getDocument("/newswire-bank-of-canada.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void prnnews_1() {
        //http://www.prnewswire.com/news-releases/dexia-and-cognizant-in-exclusive-talks-for-future-collaboration-on-information-technology-and-business-process-services-300459093.html
        //Has to detect hour : 2:42 ET
        LocalDateTime ed = LocalDateTime.parse("2017-05-17T02:42:00");
        Document doc = getDocument("/prnnews-dexia-and-cognizant.html");
        LocalDateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void datestring_resolve1() {
        LocalDateTime ed = LocalDateTime.parse("2018-12-15T00:00:00");
        String str = "Early adoption is permitted for fiscal years beginning after December 15 2018, and interim periods within those fiscal years.";
        ArrayList<ExtIntervalSimple> dates = DateResolver.resolveDate(str);
        assertTrue("Expected " + ed + " but was " + dates.get(0).getDatetimeValue(), dates.get(0).getDatetimeValue().equals(ed));
    }

    @Test
    public void datestring_resolve2() {
        LocalDateTime ed1 = LocalDateTime.parse("2018-09-30T00:00:00");
        LocalDateTime ed2 = LocalDateTime.parse("2017-09-24T00:00:00");
        String str = "For the Nine Months Ended     (In thousands)   September 30, 2018   September 24, 2017   % Change   September 30, 2018   September 24, 2017";
        ArrayList<ExtIntervalSimple> dates = DateResolver.resolveDate(str);
        assertTrue("Expected " + ed1 + " but was " + dates.get(0).getDatetimeValue(), dates.get(0).getDatetimeValue().equals(ed1));
        assertTrue("Expected " + ed2 + " but was " + dates.get(1).getDatetimeValue(), dates.get(1).getDatetimeValue().equals(ed2));
        assertTrue("Expected September 24, 2017" + " but was " + dates.get(3).getCustomData() , dates.get(3).getCustomData().equals("September 24, 2017"));

    }

    @Test
    public void datestring_resolve3() {
        String str ="22:31:45.736 [main] INFO  com.quantxt.nlp.types.PDFManager - bb: Yes   o     No  x Number of shares of each class of the registrant�s common stock outstanding as of October 30, 2018 (exclusive of treasury shares):  Class A Common Stock 164,146,697 shares Class B Common Stock 803,408 shares   THE NEW YORK TIMES COMPANY INDEX           PART I       Financial Information   1 Item 1   Financial Statements   1       Condensed Consolidated Balance Sheets as of September 30, 2018  (unaudited) and December 31, 2017   1       Condensed Consolidated Statements of Operations (unaudited) for the quarters and nine months ended September 30, 2018 and September 24, 2017   3       Condensed Consolidated Statements of Comprehensive Income (unaudited) for the quarters and nine months ended September 30, 2018 and September 24, 2017   5       Condensed Consolidated Statements of Changes In Stockholder�s Equity (unaudited) as of September 30, 2018 and September 24, 2017   6       Condensed Consolidated Statements of Cash Flows (unaudited) for the nine months ended September 30, 2018 and September 24, 2017   7       Notes to the Condensed Consolidated Financial Statements   8 Item 2   Management�s Discussion and Analysis of Financial Condition and Results of Operations   25 Item 3   Quantitative and Qualitative Disclosures about Market Risk   38 Item 4   Controls and Procedures   39     PART II       Other Information   40 Item 1   Legal Proceedings   40 Item 1A   Risk Factors   40 Item 2   Unregistered Sales of Equity Securities and Use of Proceeds   40 Item 6   Exhibits   41 PART I. FINANCIAL INFORMATION Item 1.";
        ArrayList<ExtIntervalSimple> dates = DateResolver.resolveDate(str);
        assertTrue(dates.size() == 11);
    }

    @Test
    public void datestring_resolve4() {
        LocalDateTime ed1 =LocalDateTime.parse("2018-10-31T00:00:00");
        String str = "InceptionPortfolio Benchmark (Annualized) Asset Class Composition (Net market value, as of 10/31/18) Fund Performance";
        ArrayList<ExtIntervalSimple> dates = DateResolver.resolveDate(str);
        assertTrue("Expected " + ed1 + " but was " + dates.get(0).getDatetimeValue(), dates.get(0).getDatetimeValue().equals(ed1));

    }

    private Document getDocument(String n) {
        try {
            InputStream in = getClass().getResourceAsStream(n);
            Document doc = Jsoup.parse(in,"UTF-8" , "");
            return doc;
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    @Test
    public void date_string_1() {
        String str = "2011-05-19";
        LocalDateTime dt = DateResolver.resolveDateStr(str);
        LocalDateTime datetime = LocalDateTime.parse("2011-05-19T00:00:00");

        assertTrue("Expected " + datetime + " but was " + dt, datetime.equals(dt));

    }

    @Test
    public void date_string_2() {
        String str = "Hello2011-05-19";
        LocalDateTime dt = DateResolver.resolveDateStr(str);
        LocalDateTime datetime = LocalDateTime.parse("2011-05-19T00:00:00");

        assertTrue("Expected " + datetime + " but was " + dt, datetime.equals(dt));


    }

    @Test
    public void date_string_3() {
        String str = "2019-12-02 17:55:00.0000000Z";
        LocalDateTime dt = DateResolver.resolveDateStr(str);
        LocalDateTime datetime = LocalDateTime.parse("2019-12-02T17:55:00");

        assertTrue("Expected " + datetime + " but was " + dt, datetime.equals(dt));


    }

}
