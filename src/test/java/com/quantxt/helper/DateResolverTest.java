package com.quantxt.helper;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by matin on 5/24/17.
 */
public class DateResolverTest {

    private static Logger logger = LoggerFactory.getLogger(ArticleBodyResolverTest.class);

    @Test
    public void gulfTimes_1() {
        //http://www.gulf-times.com/story/546307/Bank-of-Japan-to-buy-less-of-short-maturity-bonds-
        DateTime ed = new DateTime("2017-04-30T20:31:00.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/gulf-bank-of-japan-buy.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed +" but was " + date, date.equals(ed));
    }

    @Test
    public void nytimes_1() {
        //https://www.nytimes.com/2017/04/11/us/alabama-governor-robert-bentley-sex-scandal.html?hp&action=click&pgtype=Homepage&clickSource=story-heading&module=second-column-region&region=top-news&WT.nav=top-news
        DateTime ed = new DateTime("2017-04-12T09:40:25-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/nytimes-alabama-christians.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void nytimes_2() {
        //https://www.nytimes.com/2017/04/11/us/politics/sean-spicer-hitler-gas-holocaust-center.html
        DateTime ed = new DateTime("2017-04-12T16:23:07-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/nytimes-sean-spicer.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }


    @Test
    public void nytimes_3() {
        //https://www.nytimes.com/2017/04/11/technology/personaltech/a-hangout-for-old-desktop-notifications.html
        DateTime ed = new DateTime("2017-04-13T00:00:10-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/nytime-a-hangout.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void washpost1() {
        //https://www.washingtonpost.com/news/politics/wp/2017/03/29/the-nunes-white-house-question-assessed-minute-by-minute/
        DateTime ed = new DateTime("2017-03-29T04:03:00-05:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/washpost_the-nunes.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void nz_herald_1() {
        //http://www.nzherald.co.nz/business/news/article.cfm?c_id=3&objectid=11862084
        DateTime ed = new DateTime("2017-05-23T21:21:41Z").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/nzherald_tower-first.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void econotimes_1() {
        //http://www.econotimes.com/Bank-of-Mexico-likely-to-keep-policy-rate-on-hold-204064
        DateTime ed = new DateTime("2016-05-04T13:12:00-00:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/econotimes_bank-of-mexico.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void reuters_1() {
        //http://www.reuters.com/article/us-iran-russia-opec-deal-idUSKBN16Z0PM
        DateTime ed = new DateTime("2017-03-28T11:45:00.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/reuters_us-iran.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void reuters_2() {
        //http://www.reuters.com/article/us-usa-fed-minutes-idUSKBN18K2L5
        DateTime ed = new DateTime("2017-05-24T14:59:00.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/reuters_us-fed-today.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void reuters_3(){
        //http://www.reuters.com/article/brief-japan-post-bank-receives-approval/brief-japan-post-bank-receives-approval-regarding-development-of-new-business-idUSL3N1JG1WY
        DateTime ed = new DateTime("2017-06-19T04:05:00.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/japan_post_bank.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void cnbc_1() {
        //http://www.cnbc.com/2017/03/28/apple-iphone-suppliers-outlook-jpmorgan.html
        DateTime ed = new DateTime("2017-03-28T23:25:55.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/cnbc_apple-iphone.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void bloomberg_1() {
        //https://www.bloomberg.com/news/articles/2017-03-27/tesla-model-3-ramp-up-aims-to-crush-bmw-and-mercedes
        DateTime ed = new DateTime("2017-03-27T02:00:07.783-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/bloomberg_tesla-model.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void torontosun_1() {
        //http://www.torontosun.com/2016/11/24/bank-of-canada-announces-short-list-of-canadian-women-vying-to-be-on-banknote
        DateTime ed = new DateTime("2016-11-24T23:41:39.000-05:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/torontosun_bank-of-canada.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void kitco_1() {
        //http://ekosvoice.com/2017/05/14/super-thursday-bank-of-england-preview.html
        DateTime ed = new DateTime("2017-05-14T00:00:00").withZoneRetainFields(DateTimeZone.UTC);
        Document doc = getDocument("/ekosvoice-super-thursday.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void tamu_1() {
        //http://today.tamu.edu/2016/08/26/mosbacher-institute-to-host-deputy-governor-of-the-bank-of-mexico/
        DateTime ed = new DateTime("2016-08-26T12:38:06.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/tamu-mosbacher-institute.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void giftedviz_1() {
        //http://giftedviz.com/2017/05/17/bank-of-england-holds-rates-in-7-1-vote/
        DateTime ed = new DateTime("2017-05-17T10:58:00.000-04:00").withZone(DateTimeZone.UTC);
        Document doc = getDocument("/giftedviz_bank-of-england.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void gonefishingfish_1() {
        //http://gonefishingfish.com/2017/05/13/bank-of-england-cuts-growth-forecast-and-warns-real-wages/
        DateTime ed = new DateTime("2017-05-13T00:00:00").withZoneRetainFields(DateTimeZone.UTC);
        Document doc = getDocument("/gonefishingfish_bank-of-england.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void newswire_1() {
        //http://www.newswire.ca/news-releases/bank-of-canada-unveils-commemorative-bank-note-to-celebrate-canadas-150th-anniversary-of-confederation-618646013.html
        DateTime ed = new DateTime("2017-04-07T00:00:00").withZoneRetainFields(DateTimeZone.UTC);
        Document doc = getDocument("/newswire-bank-of-canada.html");
        DateTime date = DateResolver.resolveDate(doc);
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }

    @Test
    public void prnnews_1() {
        //http://www.prnewswire.com/news-releases/dexia-and-cognizant-in-exclusive-talks-for-future-collaboration-on-information-technology-and-business-process-services-300459093.html
        //Has to detect hour : 2:42 ET
        DateTime ed = new DateTime("2017-05-17T00:00:00").withZoneRetainFields(DateTimeZone.UTC);
        Document doc = getDocument("/prnnews-dexia-and-cognizant.html");
        DateTime date = DateResolver.resolveDate(doc);
        logger.info("Your date is " + date + " " + date.getZone() + " / " + ed.getZone());
        assertTrue("Expected " + ed + " but was " + date, date.equals(ed));
    }


    private Document getDocument(String n) {
        try {
            InputStream in = getClass().getResourceAsStream(n);
            Document doc = Jsoup.parse(in,"UTF-8" , "");
            return doc;
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }
}
