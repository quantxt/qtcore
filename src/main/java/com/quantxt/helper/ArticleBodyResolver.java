package com.quantxt.helper;

import com.quantxt.types.MapSort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by matin on 3/26/17.
 */
public class ArticleBodyResolver {

    private static Logger logger = LoggerFactory.getLogger(ArticleBodyResolver.class);

    public static Set<String> NO_TEXT_TAGS = new HashSet<>(Arrays.asList("h1" , "caption", "cite", "h2", "audio" ,"script", "nav", "iframe", "embed", "footer", "form", "figcaption", "img", "video" , "figure"));

    private Document doc;
    private int totoalWords = 0;
    private List<Element> extractions;

    private Element articleNode;

    public ArticleBodyResolver(Document doc){
        this.doc = doc;
        extractions = new ArrayList<>();
    }


    public void  analyze2(){
        HashSet<Element> dontcare = new HashSet<>();
        Elements elements = doc.body().select("*");
        //reomve iframes
        for (int i=0; i <elements.size(); i++) {
            Element e = elements.get(i);
            if (dontcare.contains(e)) continue;
            String tagName = e.tag().getName();
            if (NO_TEXT_TAGS.contains(tagName)) continue;
//            logger.info(e.tag().getName() + " " + e.tag().isBlock());
            String eOwnText = e.ownText();
            if (eOwnText.isEmpty()) continue;

            int eNumWords = eOwnText.split("\\s+").length;
            if (eNumWords < 10) continue;
            //    String eText = e.text();

            Element parent = e.parent();
            String pText = parent.text();
            boolean isIframe = false;
            while (pText.length() == eOwnText.length()) {
                if (parent.tagName().equals("iframe")){
                    isIframe = true;
                    break;
                }
                parent = parent.parent();
                pText = parent.text();
            }
            if (isIframe) continue;


            //  EnhancedElement ee = new EnhancedElement(e, numWords, i);
            //        int numParentElems = parent.children().size();
            Elements imgs = parent.select("img,figure,video");
            double numImg = (double) imgs.size();
            double r = numImg * 10 / (double) parent.text().split("\\s+").length;
            if (r > .4) continue;

            int pNumWords = pText.split("\\s+").length;
            totoalWords+= pNumWords;

            if (!extractions.contains(parent)) {
                extractions.add(parent);
                Elements toberemoved = parent.select("*");
                dontcare.addAll(toberemoved);
            }
        }
    }

    public int getTotoalWords(){
        return totoalWords;
    }

    public void analyze(){
        Map<Element, Integer> text_heavy_elems = new HashMap<>();
        Map<Element, Integer> order_map = new HashMap<>();
        HashSet<Element> dontcare = new HashSet<>();

        Elements elements = doc.body().select("*:matches(\\S+\\s+\\S+\\s+\\S+)");
        for (Element e : elements) {
            String eOwnText = e.ownText().toLowerCase().replaceAll("[^a-z\\s]+" , "").trim();
            if (eOwnText.isEmpty()) continue;
            if (dontcare.contains(e)) continue;
            String tagName = e.tag().getName();
            if (NO_TEXT_TAGS.contains(tagName)) continue;

            int numWords = eOwnText.split("\\s+").length;
            if (numWords < 10) continue;

            Element child  = e;
            Element parent = e.parent();
            String pText = parent.text().toLowerCase().replaceAll("[^a-z\\s]+" , "").trim();
            //    String eText = e.text();
            while (pText.length() == eOwnText.length()) {
                child = parent;
                parent = parent.parent();
                pText = parent.text().toLowerCase().replaceAll("[^a-z\\s]+" , "").trim();
            }

//            if (!e.cssSelector().equals(parent.cssSelector())){
//                logger.info(e.cssSelector() + " --> " + parent.cssSelector());
//            }

            Elements imgs = child.select("img,figure,video");
            double numImg = (double) imgs.size();
            double r = numImg * 10 / (double) child.text().split("\\s+").length;
            // if there is a lot of caption...
            if (r > .4) continue;

            Integer c = text_heavy_elems.get(child);
            if (c == null) {
                c = 0;
            }

            text_heavy_elems.put(child, c + numWords);
            Integer order = order_map.get(parent);
            if (order == null){
                order_map.put(child, order_map.size());
            }
        }


        Map<Element, Integer> sorted = MapSort.sortdescByValue(text_heavy_elems);

        Map<Element, Integer> content_elems = new HashMap<>();
        double max = (double) sorted.entrySet().iterator().next().getValue();
        for (Map.Entry<Element, Integer> e : sorted.entrySet()){
            Element elem = e.getKey();
            int level = elem.cssSelector().split(" > ").length;
            //elem.cssSelector()
            logger.info("(" + level + ")" + "\t" +  " --> " + elem.text().replaceAll("\\s+" , " "));
            double word_ratio = (double) e.getValue() / max;
            if (word_ratio > .7){
                //               logger.info(e.getKey().id() + " | " + elem.className() + " | " + e.getValue() + " " + word_ratio);
                Integer order = order_map.get(elem);
                content_elems.put(elem, order);
            }
        }
        Map<Element, Integer> final_res = MapSort.sortByValue(content_elems);
        for (Map.Entry<Element, Integer> e : final_res.entrySet()){
            extractions.add(e.getKey());
        }
    }

    public static int getLevel(Element e){
        int level = 0;
        Element parent = e.parent();
        while (parent != null){
            parent = parent.parent();
            level++;
        }
        return level;
    }

    public int getLength(Element e){
        String eOwnText = e.ownText().toLowerCase().replaceAll("[^a-z\\s]+" , "").trim();
        int eTextNumWords = eOwnText.split("\\s+").length;
        return  eTextNumWords;
    }

    public int getTLength(Element e){
        String eText = e.text().toLowerCase().replaceAll("[^a-z\\s]+" , "").trim();
        int eTextNumWords = eText.split("\\s+").length;
        return  eTextNumWords;
    }

    public int getTLength(List<Element> es){
        int eTextNumWords = 0;
        for (Element e : es) {
            eTextNumWords += getTLength(e);
        }
        return  eTextNumWords;
    }

    public int getLength(Elements es){
        int eTextNumWords = 0;
        for (Element e : es) {
            eTextNumWords += getLength(e);
        }
        return  eTextNumWords;
    }

    public int getLength(List<Element> es){
        int eTextNumWords = 0;
        for (Element e : es) {
            eTextNumWords += getLength(e);
        }
        return  eTextNumWords;
    }

    public List<Element> getTextElements(Elements parent, int minNumWords, int maxNumWords){
        List<Element> elems = new ArrayList<>();
        for (Element e : parent){
            String text = e.ownText();
            int n = text.split("\\s+").length;
            if (n >= minNumWords && n <= maxNumWords) {
                elems.add(e);
            }
        }
        return elems;
    }

    public List<Element> getTextAllElements(Elements elements, int minNumWords, int maxNumWords){
        List<Element> elems = new ArrayList<>();
        for (Element e : elements){
            if (NO_TEXT_TAGS.contains(e.tagName())) continue;
            Element parent = e.parent();
  //          String parenetText = parent.text();
            final String text = e.text();
 /*           while(parenetText.equals(text)){
                parent = parent.parent();
                parenetText = parent.text();
                for (String cs : parent.classNames()){
                    e.addClass(cs);
                }
            }
 */           int n = text.split("\\s+").length;
            if (n >= minNumWords && n <= maxNumWords) {
                elems.add(e);
            }
        }
        return elems;
    }

    public Elements getArticleNode(){
        Elements docElements = doc.body().getAllElements();
        docElements.select(".comments-panel").remove();
        List<Element> textHeavyElements = getTextAllElements(docElements, 10, 10000);

        int numWords = 0;
        List<Element> allTextHeavyOwnElements = new ArrayList<>();
        List<Element> allTextOwnElements = new ArrayList<>();
        for (Element e : docElements){
            if (NO_TEXT_TAGS.contains(e.tagName())) continue;
            String text = e.ownText();
            int n = text.split("\\s+").length;
            if (n >= 10){
                allTextHeavyOwnElements.add(e);
                numWords += n;
            } else if ( n < 3){
                allTextOwnElements.add(e);
            }
        }

        Map<Integer, Double> elem2score = new HashMap<>();

        for (int idx = 0; idx < textHeavyElements.size(); idx++){
            Element e = textHeavyElements.get(idx);

            List<Element> textOwnbHeavyElements = getTextElements(e.getAllElements(), 10, 10000);
            if(textOwnbHeavyElements.size() < 1) continue;

            List<Element> textOwnElements = getTextElements(e.getAllElements(), 0, 2);
            double totalWords = (double) getTLength(textOwnbHeavyElements);
            for (Element et : textOwnbHeavyElements){
                double tw = (double) getLength(et);
         //       logger.info(idx + " " +et.elementSiblingIndex() + " / " + tw/totalWords + " " + textOwnbHeavyElements.size());
            }

        //    double p1 = (double) textOwnbHeavyElements.size() / (double)allTextHeavyOwnElements.size();
            double p3 =  totalWords / (double)numWords;
            double p2 = allTextOwnElements.size() == 0 ? 0 : (double) textOwnElements.size() / (double)allTextOwnElements.size();

            double depth = getLevel(e);
            double score = p3 * depth * ( 1 - p2);
            elem2score.put(idx, score);
        }

        LinkedHashMap<Integer, Double> sortedElem2score = MapSort.sortdescByValue(elem2score);


        int thresh = 2;
        ArrayList<Integer> topIdxs = new ArrayList<>();
        for (Map.Entry<Integer, Double> ses : sortedElem2score.entrySet()){
            if (thresh-- < 0) break;
            topIdxs.add(ses.getKey());
        }
        Collections.sort(topIdxs);

        List<Element> topTextElems = new ArrayList<>();
        for (int id : topIdxs){
            Element goodElem = textHeavyElements.get(id);

            for (String tagName : NO_TEXT_TAGS){
                goodElem.select(tagName).remove();
            }
            topTextElems.add(goodElem);
        }
        int textHeavyElementId = sortedElem2score.entrySet().iterator().next().getKey();
        articleNode = textHeavyElements.get(textHeavyElementId);
        return new Elements(topTextElems);
    }

    private String getElementStr(Element e){
        StringBuilder sb = new StringBuilder();

        Set<String> classNames = e.classNames();
        if (classNames.size() != 0) {
            for (String c : classNames){
                if (c == null || c.isEmpty()) continue;
                sb.append(c).append("class");
            }
        } else {
            Map<String, String> attr = e.attributes().dataset();
            if (attr != null){
                for (Map.Entry<String, String> entry : attr.entrySet()){
                    sb.append(entry.getKey()).append(entry.getValue());
                }
            }
        }
        return sb.toString();
    }

    public List<Element> getText(){
        Map<String, Integer> elemType2Count = new HashMap<>();
        List<Element> goodElems = new ArrayList<>();
        List<Element> articleNodes = getArticleNode();
        for (Element articleNode : articleNodes) {
            List<Element> allelems = getTextAllElements(articleNode.getAllElements(), 6, 10000);

            for (Element e : allelems) {
                int numWords = getLength(e);
                String str = getElementStr(e);
                if (str.length() == 0) {
                    String tagName = e.tagName();
                    int depth = getLevel(e);
                    for (int i= depth-2; i< depth+3; i++){
                        String stri = "==" + tagName + i + "==";
                        Integer c = elemType2Count.get(stri);
                        if (c == null) {
                            c = 0;
                        }
                        elemType2Count.put(stri, c + numWords);
                    }
                } else {
                    Integer c = elemType2Count.get(str);
                    if (c == null) {
                        c = 0;
                    }
                    elemType2Count.put(str, c + numWords);
                }
            }
        }

        LinkedHashMap<String, Integer> sortedElemType2Count = MapSort.sortdescByValue(elemType2Count);
        String bestTagStr = sortedElemType2Count.entrySet().iterator().next().getKey();
        for (Element articleNode : articleNodes) {
            for (Element e : articleNode.getAllElements()) {
                String elementStr = getElementStr(e);
                String eText = e.text();
                eText = eText.replace("\u00A0", "").trim();
                if (elementStr.length() != 0) {
                    if (elementStr.equals(bestTagStr)) {
                        for (Element child : e.getAllElements()) {
                            String tag = child.tagName();
                            String text = child.ownText();
                            if (text.isEmpty()){
                                child.remove();
                            }
//                            if (TEXT_TAGS.contains(tag)) continue;
//                            child.remove();
                        }
                        if (eText.isEmpty()) continue;
                        goodElems.add(e);
                    }
                } else {
                    String tagName = e.tagName();
                    int depth = getLevel(e);
                    for (int i= depth-2; i< depth+3; i++){
                        elementStr = "==" + tagName + i + "==";
                        if (elementStr.equals(bestTagStr)) {
                            for (Element child : e.getAllElements()) {
                                String tag = child.tagName();
                                String text = child.ownText();
                                if (text.isEmpty()){
                                    child.remove();
                                }
//                                if (TEXT_TAGS.contains(tag)) continue;
//                                child.remove();
                            }
                            if (eText.isEmpty()) continue;
                            goodElems.add(e);
                        }
                    }
                }
            }
        }
  //      logger.info(goodElems.get(0).text());
        return goodElems;
    }

    public String getNodeCss(){
        return articleNode.cssSelector();
    }

    public void analyze3(){
        getArticleNode();
        logger.info(getNodeCss());
        /*
        Map<String, Double> elementScore = new HashMap<>();

        Elements elements = doc.body().select("*:matchesOwn(\\S+\\s+\\S+\\s+\\S+)");
        for (Element e : elements) {
            double score = getLengthScore(e);
            String cssSelector = e.cssSelector();
            elementScore.put(cssSelector, score);
        }

        Map<String, Double> sortedElementScore = MapSort.sortdescByValue(elementScore);
        for (Map.Entry<String, Double> e : sortedElementScore.entrySet()){
            logger.info("GG " + doc.select(e.getKey()).text() + " " + e.getValue());
        }

        Map<String, ArrayList<Double>> singleElementScoreArray = new HashMap<>();
        for (Map.Entry<String, Double> ee : elementScore.entrySet()){
            String [] elems = ee.getKey().split(" > ");
            StringBuilder sb = new StringBuilder();

            for (String e : elems){
                sb.append(e);
                ArrayList<Double> s = singleElementScoreArray.get(e);
                if (s == null){
                    s = new ArrayList<>();
                }
                s.add(ee.getValue());
                singleElementScoreArray.put(sb.toString(), s);
                sb.append(" > ");
            }
        }

        Map<String, Double> singleElementScore = new HashMap<>();
        for (Map.Entry<String, ArrayList<Double>> ses : singleElementScoreArray.entrySet()){
            double sum = 0;
            for (double d : ses.getValue()){
                sum +=d;
            }
            double avg = sum / (double) ses.getValue().size();
            singleElementScore.put(ses.getKey(), avg);
        }



        Map<String, Double> sortedSingleElementScore = MapSort.sortdescByValue(singleElementScore);
        for (Map.Entry<String, Double> e : sortedSingleElementScore.entrySet()){
            logger.info(e.getKey() + " " + e.getValue());
        }
        */
    }

    public void analyze1() throws Exception {
        Map<Element, Integer> length_lev_1Map = new HashMap<>();

        Elements pElements = doc.body().select("p,div,li");
        for (Element e : pElements) {
            String eOwnText = e.ownText();
            if (eOwnText.isEmpty()) continue;

            int numWords = eOwnText.split("\\s+").length;
            if (numWords < 10) continue;

            Element parent = e.parent();
            String pText = parent.text();
            String eText = e.text();
            while (pText.length() == eText.length()) {
                parent = parent.parent();
                pText = parent.text();
            }

            Integer c = length_lev_1Map.get(parent);
            if (c == null) {
                c = 0;
            }
            length_lev_1Map.put(parent, c + numWords);
        }

        Map<Element, Integer> sorted = MapSort.sortdescByValue(length_lev_1Map);

        double max = (double) sorted.entrySet().iterator().next().getValue();
        for (Map.Entry<Element, Integer> e : sorted.entrySet()){
            double word_ratio = (double) e.getValue() / max;
            if (word_ratio > .7){
                extractions.add(e.getKey());
            }
        }


    }

    public List<Element> getExtractions(){
        return extractions;
    }

    public static void main(String[] args) throws Exception {

        String [] urls = {
     //           "http://www.dailymail.co.uk/news/article-4356348/Carlos-Jackal-awaiting-verdict-Paris-court.html"
     //           "http://www.nasdaq.com/article/china-stocksfactors-to-watch-on-tuesday-20170327-01268"
                "http://milwaukeecourieronline.com/index.php/2017/05/27/u-s-senator-tammy-baldwin-statement-on-cbo-score-of-house-passed-health-care-bill/"
                //truncated
                //  "https://www.nytimes.com/2017/03/29/world/asia/china-taiwan-activist-lee-ming-cheh.html"
                // "http://www.cnn.com/2017/03/29/politics/senate-filibuster-neil-gorsuch/index.html"

                //not working
                //"http://www.cnbc.com/2017/03/28/apple-iphone-suppliers-outlook-jpmorgan.html"

                //wrong order
                //"https://www.theatlantic.com/international/archive/2017/03/donald-trump-china-rachman/521055/",
                // "http://www.express.co.uk/news/uk/784786/marine-a-sentence-reduced-seven-years-alexander-blackman-law-crime"
        };

        for (String u : urls) {
            logger.info(u);
            try {
                Document doc = Jsoup.connect(u).get();
                ArticleBodyResolver abr = new ArticleBodyResolver(doc);
                abr.analyze3();
                List<Element> elems = abr.getText();
                for (Element e : elems) {
                    logger.info(e.cssSelector() + " / " + e.text());
                }
            } catch (Exception e){
                logger.error("Error: " +e);
            }
        }
    }

}
