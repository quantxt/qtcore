package com.quantxt.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Created by matin on 3/29/17.
 */

public class ArticleBodyResolverTest {

    private static Logger logger = LoggerFactory.getLogger(ArticleBodyResolverTest.class);

    @Test
    public void nytimes_1() {
        //https://www.nytimes.com/2017/04/11/us/alabama-governor-robert-bentley-sex-scandal.html?hp&action=click&pgtype=Homepage&clickSource=story-heading&module=second-column-region&region=top-news&WT.nav=top-news
        ArticleBodyResolver abr = getABR("/nytimes-alabama-christians.html");
//        assertEquals(abr.getNodeCss(), "#story > div.story-body-supplemental:nth-child(6) > div.story-body.story-body-2");
        assertTrue(abr.getText().get(0).text().startsWith("MONTGOMERY, Ala. — As governor, Robert Bentley"));
    }

    @Test
    public void nytimes_2() {
        //https://www.nytimes.com/2017/04/11/us/politics/sean-spicer-hitler-gas-holocaust-center.html
        ArticleBodyResolver abr = getABR("/nytimes-sean-spicer.html");
    //    assertEquals(abr.getNodeCss(), "#article-body > article");
        assertTrue(abr.getText().get(0).text().startsWith("WASHINGTON — The White House press secretary, Sean Spicer"));
    }


    @Test
    public void nytimes_3() {
        //https://www.nytimes.com/2017/04/11/technology/personaltech/a-hangout-for-old-desktop-notifications.html
        ArticleBodyResolver abr = getABR( "/nytime-a-hangout.html");
        //    assertEquals(abr.getNodeCss(), "#article-body > article");
        assertTrue(abr.getText().get(0).text().startsWith("Q. Several websites and programs"));
    }

    @Test
    public void washpost1() {
        //https://www.washingtonpost.com/news/politics/wp/2017/03/29/the-nunes-white-house-question-assessed-minute-by-minute/?utm_term=.e173fe569434
        ArticleBodyResolver abr = getABR("/washpost_the-nunes.html");
        assertEquals(abr.getNodeCss(), "#article-body > article");
        assertTrue(abr.getText().get(0).text().startsWith("The New Yorker’s Ryan Lizza laid"));
    }

    @Test
    public void cnet1() {
        //https://www.cnet.com/news/heres-how-much-the-galaxy-s8-and-s8-plus-will-cost-you/
        ArticleBodyResolver abr = getABR("/cnet_heres-how-much-the.html");
        assertEquals(abr.getNodeCss(), "#article-body > div.col-7.article-main-body.row");
        assertTrue(abr.getText().get(0).text().startsWith("Earlier today at an event in New York, Samsung"));
    }

    @Test
    public void reuters_1() {
        //http://www.reuters.com/article/us-iran-russia-opec-deal-idUSKBN16Z0PM
        ArticleBodyResolver abr = getABR("/reuters_us-tesla-stocks.html");
        assertEquals(abr.getNodeCss(), "#rcs-articleContent > div.column1.col.col-10");
        //must be this!
        //     assertEquals(abr.getNodeCss(), "#article-text");
        assertTrue(abr.getText().get(0).text().startsWith("SAN FRANCISCO Shares of Tesla (TSLA.O) jumped nearly 3 percent"));
    }


    @Test
    public void reuters2() {
        //http://www.reuters.com/article/us-iran-russia-opec-deal-idUSKBN16Z0PM
        ArticleBodyResolver abr = getABR("/reuters_us-iran.html");
        assertEquals(abr.getNodeCss(), "#rcs-articleContent > div.column1.col.col-10");
        //must be this!
  //     assertEquals(abr.getNodeCss(), "#article-text");
        assertTrue(abr.getText().get(0).text().startsWith("MOSCOW Russia and Iran have pledged to continue efforts"));
    }

    @Test
    public void cnbc1() {
        //http://www.cnbc.com/2017/03/28/apple-iphone-suppliers-outlook-jpmorgan.html
        ArticleBodyResolver abr = getABR("/cnbc_apple-iphone.html");
        assertEquals(abr.getNodeCss(), "#article_body");
        assertTrue(abr.getText().get(0).text().startsWith("Taiwanese manufacturer Hon Hai"));
    }

    @Test
    @Ignore
    public void newarkpostonline() {
        //http://www.newarkpostonline.com/news/article_9ae11f57-fe97-5335-9eb6-5f8e3a2b4da8.html
        ArticleBodyResolver abr = getABR("/newarkpostonline_9ae11f57.html");
        assertEquals(abr.getNodeCss(), "#asset-content > div.row > div.col-lg-12.col-md-12.col-sm-12 > div.asset-body > div.asset-content.p402_premium.subscriber-premium");
        assertTrue(abr.getText().get(0).text().startsWith("A man was captured on camera taking items"));
    }

    @Test
    public void politico() {
        //http://www.politico.com/story/2017/03/donald-trump-chris-christie-endorsement-236644
        ArticleBodyResolver abr = getABR("/politico_donald-trump.html");
        assertEquals(abr.getNodeCss(), "#globalWrapper > main.super-duper > div.super:nth-child(3) > div.super-inner > article.story-main-content > div.content.layout-story.sticky-wrapper > section.content-groupset.pos-beta > div.content-group.story-core > div.story-text");
        assertTrue(abr.getText().get(0).text().startsWith("President Donald Trump on Wednesday needled"));
    }

    @Test
    public void huffingtonpost() {
        //http://www.huffingtonpost.com/entry/ivanka-trump-white-house-job_us_58dc19f1e4b0e6ac7091fa19
        ArticleBodyResolver abr = getABR("/huffingtonpost_ivanka-trump.html");
        assertEquals(abr.getNodeCss(), "#us_58dc19f1e4b0e6ac7091fa19 > div.entry__content.js-entry-content > div.entry__body.js-entry-body > div.entry__text.js-entry-text.bn-entry-text");
        assertTrue(abr.getText().get(0).text().startsWith("Ivanka Trump will take on a more formal"));
    }

    @Test
    public void washingtonpost() {
        //https://www.washingtonpost.com/powerpost/neil-gorsuchs-supreme-court-nomination-is-on-track-to-irreparably-change-the-senate--and-further-divide-the-country/2017/03/29/dc57011e-13d3-11e7-ada0-1489b735b3a3_story.html
        ArticleBodyResolver abr = getABR("/washingtonpost_neil-gorsuchs.html");
        assertEquals(abr.getNodeCss(), "#article-body > article");
        assertTrue(abr.getText().get(0).text().startsWith("Sens. Roger E. Wicker (R-Miss.) and Thoma"));
    }

    @Test
    public void washingtonpost_2() {
        //https://www.washingtonpost.com/world/national-security/kim-jong-uns-rockets-are-getting-an-important-boost--from-china/2017/04/12/4893b0be-1a43-11e7-bcc2-7d1a0973e7b2_story.html?hpid=hp_hp-top-table-main_usnorthkorea-720am%3Ahomepage%2Fstory&utm_term=.b9b1594ddd38
        ArticleBodyResolver abr = getABR("/washingtonpost_kim-jong-uns.html");
//        assertEquals(abr.getNodeCss(), "#article-body > article");
        assertTrue(abr.getText().get(0).text().startsWith("When North Korea launched its"));
    }

    @Test
    public void foxnews_1() {
        //http://www.foxnews.com/politics/2017/03/29/senators-expand-russia-investigation-amid-scrutiny-house-probe.html
        ArticleBodyResolver abr = getABR("/foxnews_senators-expand.html");
        assertEquals(abr.getNodeCss(), "#content > div:nth-child(1) > div.main > article > div > div.article-body > div.article-text");
        assertTrue(abr.getText().get(0).text().startsWith("Leaders of the Senate Intelligence Committ"));
    }


    @Test
    public void theatlantic() {
        //https://www.theatlantic.com/international/archive/2017/03/donald-trump-china-rachman/521055/
        ArticleBodyResolver abr = getABR("/theatlantic_donald-trump.html");
        assertEquals(abr.getNodeCss(), "#article > div.article-body");
        assertTrue(abr.getText().get(0).text().startsWith("Next week, Chinese President"));
    }

    @Test
    @Ignore
    public void npr() {
        //http://www.npr.org/sections/thetwo-way/2017/03/29/521941716/chinese-president-xi-jinping-to-meet-with-president-trump-in-florida
        ArticleBodyResolver abr = getABR("/npr_chinese-president.html");
        assertEquals(abr.getNodeCss(), "#storytext");
        assertTrue(abr.getText().get(0).text().startsWith("Chinese President Xi Jinping will meet with President"));
    }

    @Test
    public void cnn_1() {
        //http://www.cnn.com/2017/03/29/politics/senate-intelligence-committee-conference/index.html
        ArticleBodyResolver abr = getABR("/cnn_senate-intelligence.html");
        assertEquals(abr.getNodeCss(), "#body-text > div.l-container");
        assertTrue(abr.getText().get(0).text().startsWith("The Senate intelligence committee has asked "));
    }

    @Test
    public void cnn_2() {
        //http://www.cnn.com/2017/04/08/middleeast/syria-strikes-russia-donald-trump/
        ArticleBodyResolver abr = getABR("/cnn-syria-strikes-russia.html");
        assertEquals(abr.getNodeCss(), "#body-text > div.l-container");
        assertTrue(abr.getText().get(0).text().startsWith("New airstrikes targeted a town in Syria that was hit by a chemical attack earlier this week, activists said, less than a day after"));
    }

    @Test
    public void cnn_3() {
        //http://www.cnn.com/2017/04/13/politics/donald-trump-moab-afghanistan/index.html
        ArticleBodyResolver abr = getABR("/cnn_donald-trump-moab.html");
   //     assertEquals(abr.getNodeCss(), "#body-text > div.l-container");
        assertTrue(abr.getText().get(0).text().startsWith("The United States on Thursday dropped the most powerful"));
    }

    @Test
    public void bbc_1() {
        //http://www.bbc.com/news/uk-wales-39563034
        ArticleBodyResolver abr = getABR("/bbc_1.html");
  //      assertEquals(abr.getNodeCss(), "#page > div:nth-child(2) > div.container > div.container--primary-and-secondary-columns.column-clearfix > div.column--primary > div.story-body:nth-child(1) > div.story-body__inner");
        assertTrue(abr.getText().get(0).text().startsWith("The landscape topped a poll"));
    }

    @Test
    public void bbc_2() {
        //http://www.bbc.com/news/uk-england-london-39568388
        ArticleBodyResolver abr = getABR("/bbc_2.html");
        assertEquals(abr.getNodeCss(), "#page > div:nth-child(2) > div.container > div.container--primary-and-secondary-columns.column-clearfix > div.column--primary > div.story-body > div.story-body__inner");
        assertTrue(abr.getText().get(0).text().startsWith("Specialists at Great Ormond"));
    }

    @Test
    public void espn_1() {
        //http://www.espn.com/blog/marc-stein/post/_/id/5118/steins-most-improved-player-giannis-antetokounmpo
        ArticleBodyResolver abr = getABR("/espn_steins-most.html");
        assertEquals(abr.getNodeCss(), "#article-feed > article.article > div.container > div.article-body");
        assertTrue(abr.getText().get(0).text().startsWith("Narrowing down to one name for Most"));
    }


    @Test
    public void espn_2() {
        //http://www.espn.com/nfl/story/_/id/19103840/pittsburgh-steelers-qb-ben-roethlisberger-says-return-14th-season
        ArticleBodyResolver abr = getABR("/espn_pittsburgh-steelers.html");
        assertEquals(abr.getNodeCss(), "#article-feed > article.article > div.container > div.article-body");
        assertTrue(abr.getText().get(0).text().startsWith("PITTSBURGH -- Quarterback Ben Roethlisberger has"));
    }

    @Test
    @Ignore
    public void usatoday_1() {
        //http://www.usatoday.com/story/news/politics/2017/03/29/what-ever-happened-president-trumps-gun-advisory-group/99176736/
        ArticleBodyResolver abr = getABR("/usatoday_what-ever-happened.html");
        assertEquals(abr.getNodeCss(), "#page > div:nth-child(2)");
        assertTrue(abr.getText().get(0).text().startsWith("CLOSE Skip in Skip x Embed x Share Just"));
    }

    @Test
    public void usatoday_2() {
        //https://www.usatoday.com/story/sports/college/2017/04/12/north-carolina-nc-state-leave-acc-boycott-championships-house-bill-728/100382142/
        ArticleBodyResolver abr = getABR("/usatoday_north-carolina.html");
        assertEquals(abr.getNodeCss(), "#overlay > div.transition-wrap > article.asset.story.clearfix > div.asset-double-wide.double-wide.p402_premium");
        assertTrue(abr.getText().get(0).text().startsWith("Four North Carolina lawmakers proposed legislation"));
    }

    @Test
    public void cnet2() {
        //https://www.cnet.com/news/heres-how-much-the-galaxy-s8-and-s8-plus-will-cost-you/
        ArticleBodyResolver abr = getABR("/cnet_heres-how-much.html");
        assertEquals(abr.getNodeCss(), "#article-body > div.col-7.article-main-body.row");
        assertTrue(abr.getText().get(0).text().startsWith("Earlier today at an event in New York, Samsung announced"));
    }


    @Test
    @Ignore
    public void dailymail() {
        //http://www.dailymail.co.uk/news/article-4356348/Carlos-Jackal-awaiting-verdict-Paris-court.html
        ArticleBodyResolver abr = getABR("/dailymail_Carlos-Jackal.html");
        assertEquals(abr.getNodeCss(), "#storytext");
        assertTrue(abr.getText().get(0).text().startsWith("Carlos the Jackal, once the world's"));
    }


    @Test
    public void cnnmoney() {
        //http://money.cnn.com/2017/03/28/news/economy/india-china-autos-saic-general-motors/index.html
        ArticleBodyResolver abr = getABR("/cnn_india-china.html");
        assertEquals(abr.getNodeCss(), "#storytext");
        assertTrue(abr.getText().get(0).text().startsWith("Shanghai-based SAIC Motor is talking with"));
    }


    @Test
    @Ignore
    public void motoroids() {
        //http://www.motoroids.com/news/hyundai-motor-india-announces-23rd-free-car-care-clinic/
        ArticleBodyResolver abr = getABR("/motoroids_hyundai-motor.html");
        assertEquals(abr.getNodeCss(), "#storytext");
        assertTrue(abr.getText().get(0).text().startsWith("The FBI searched a remote location"));
    }

    @Test
    public void india1() {
        //http://www.india.com/auto/tata-tamo-racemo-sports-car-india-launch-likely-in-december-1970521/
        ArticleBodyResolver abr = getABR("/india_tata-tamo.html");
        assertEquals(abr.getNodeCss(), "html > body.singular.single.single-post.postid-1970521.single-format-standard.group-blog > section.row > section.container > aside.iwpl-leftwrap > article.article-page > section.content-wrap.eventtracker > div > div:nth-child(2)");
        assertTrue(abr.getText().get(0).text().startsWith("TaMo, Tata Motors sub-brand showcased"));
    }

    @Test
    public void timesofindia() {
    //http://timesofindia.indiatimes.com/business/india-business/ongoing-cab-driver-strikes-impact-car-sales/articleshow/57901735.cms
        ArticleBodyResolver abr = getABR("/timesofindia_ongoing-cab.html");
        assertEquals(abr.getNodeCss(), "#content > div > div.articlepage.clearfix > div.wrapper.clearfix.article-content-wrapper > div.main-content > div.article_content.clearfix > arttextxml > div.section1 > div.Normal");
        assertTrue(abr.getText().get(0).text().startsWith("Chennai: The on-going driver unrest that has"));
    }

    @Test
    public void hooniverse() {
        //http://hooniverse.com/2017/03/29/toyota-australia-creates-the-ultimate-tonka-truck/
        ArticleBodyResolver abr = getABR("/hooniverse_toyota-australia.html");
        assertEquals(abr.getNodeCss(), "#content_left_wrapper > div.content_left > div.box > div.block > div.article.first_main_article");
        assertTrue(abr.getText().get(0).text().startsWith("Toyota is rekindling childhood memories with the reveal"));
    }

    @Test
    public void jalopnik() {
        //http://jalopnik.com/toyota-might-make-the-suv-comeback-tour-a-bloodbath-1793799055
        ArticleBodyResolver abr = getABR("/jalopnik_toyota-might.html");
        assertEquals(abr.getNodeCss(), "#post_1793799055 > div.post-content.entry-content.js_entry-content");
        assertTrue(abr.getText().get(0).text().startsWith("Toyota has teased a beefy, but honestly"));
    }

    @Test
    @Ignore
    public void autoblog() {
        //http://www.autoblog.com/2017/03/29/toyota-ft-4x-off-road-concept-new-york/
        ArticleBodyResolver abr = getABR("/autoblog_toyota-ft.html");
        assertEquals(abr.getNodeCss(), "#donut-hole > div.panel.panel-default > div.panel-body > div.copy.clearfix");
        assertTrue(abr.getText().get(0).text().startsWith("Fans of the legendary Toyota FJ40"));
    }

    @Test
    public void bloomberg() {
        //https://www.bloomberg.com/news/articles/2017-03-27/tesla-model-3-ramp-up-aims-to-crush-bmw-and-mercedes
        ArticleBodyResolver abr = getABR("/bloomberg_tesla-model.html");
        assertEquals(abr.getNodeCss(), "html > body > main.transporter-container > div.transporter-item.current > article > div.content-well > section.main-column > div.body-copy");
        assertTrue(abr.getText().get(0).text().startsWith("One year ago this week, Elon Musk took"));
    }

    @Test
    @Ignore
    public void engadget() {
        //https://www.engadget.com/2017/03/29/latest-tesla-patch-enables-autosteer-at-90-mph-for-hw2-models/
        ArticleBodyResolver abr = getABR("/engadget_latest-tesla-patch.html");
        assertEquals(abr.getNodeCss(), "#donut-hole > div.panel.panel-");
        assertTrue(abr.getText().get(0).text().startsWith("The new Autopilot features have been lurking"));
    }


    @Test
    public void nasdaq() {
        //http://www.nasdaq.com/article/uk-debt-charity-says-demand-for-help-hits-record-high-20170327-01245
        ArticleBodyResolver abr = getABR("/nasdaq_uk-debt-charity.html");
        assertEquals(abr.getNodeCss(), "#articlebody > pre");
        assertTrue(abr.getText().get(0).text().startsWith("LONDON, March 28 (Reuters) - The number of people"));
    }



    private List<Element> getFileFromResources(String n){
        try {
            InputStream in = getClass().getResourceAsStream(n);
            Document doc = Jsoup.parse(in,"UTF-8" , "");
            ArticleBodyResolver abr = new ArticleBodyResolver(doc);
            abr.analyze3();

            return abr.getExtractions();
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

    private ArticleBodyResolver getABR(String n) {
        try {
            InputStream in = getClass().getResourceAsStream(n);
            Document doc = Jsoup.parse(in,"UTF-8" , "");
            ArticleBodyResolver abr = new ArticleBodyResolver(doc);
            abr.analyze3();
            return abr;
        } catch (Exception e){
            logger.error(e.getMessage());
        }
        return null;
    }

}
