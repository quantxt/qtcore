package com.quantxt.helper;

import com.quantxt.types.MapSort;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by matin on 3/26/17.
 */
public class ArticleBodyResolver {

    private static Logger logger = LoggerFactory.getLogger(ArticleBodyResolver.class);

    public static Set<String> NO_TEXT_TAGS = new HashSet<>(Arrays.asList("h1", "caption", "cite", "h2", "audio", "script", "nav", "iframe", "embed", "footer", "form", "figcaption", "img", "video", "figure"));
    public static String PUNCS = "。.!?؟¿¡";
    public static Pattern PUNCSPattern = Pattern.compile("[" + PUNCS +"]\\s+");


    private Document doc;
    private int totoalWords = 0;
    private List<Element> extractions;
    //0: default
    //1: Good for well written text, long enough sentences ending with punctuations
    private int mode;

    private List<Element> articleNode = new ArrayList<>();

    public ArticleBodyResolver(Document doc) {
        this.doc = doc;
        extractions = new ArrayList<>();
        mode = 1;
    }

    // mode 0 simple
    // mode 1 Parse HTML with pre-defined rules (current)
    public ArticleBodyResolver(Document doc, int mode) {
        this.doc = doc;
        extractions = new ArrayList<>();
        this.mode = mode;
    }


    public void analyze2() {
        HashSet<Element> dontcare = new HashSet<>();
        Elements elements = doc.body().select("*");
        //reomve iframes
        for (int i = 0; i < elements.size(); i++) {
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
                if (parent.tagName().equals("iframe")) {
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
            totoalWords += pNumWords;

            if (!extractions.contains(parent)) {
                extractions.add(parent);
                Elements toberemoved = parent.select("*");
                dontcare.addAll(toberemoved);
            }
        }
    }

    public int getTotoalWords() {
        return totoalWords;
    }

    public void analyze() {
        Map<Element, Integer> text_heavy_elems = new HashMap<>();
        Map<Element, Integer> order_map = new HashMap<>();
        HashSet<Element> dontcare = new HashSet<>();

        Elements elements = doc.body().select("*:matches(\\S+\\s+\\S+\\s+\\S+)");
        for (Element e : elements) {
            String eOwnText = e.ownText().toLowerCase().replaceAll("[^a-z\\s]+", "").trim();
            if (eOwnText.isEmpty()) continue;
            if (dontcare.contains(e)) continue;
            String tagName = e.tag().getName();
            if (NO_TEXT_TAGS.contains(tagName)) continue;

            int numWords = eOwnText.split("\\s+").length;
            if (numWords < 10) continue;

            Element child = e;
            Element parent = e.parent();
            String pText = parent.text().toLowerCase().replaceAll("[^a-z\\s]+", "").trim();
            //    String eText = e.text();
            while (pText.length() == eOwnText.length()) {
                child = parent;
                parent = parent.parent();
                pText = parent.text().toLowerCase().replaceAll("[^a-z\\s]+", "").trim();
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
            if (order == null) {
                order_map.put(child, order_map.size());
            }
        }


        Map<Element, Integer> sorted = MapSort.sortdescByValue(text_heavy_elems);

        Map<Element, Integer> content_elems = new HashMap<>();
        double max = (double) sorted.entrySet().iterator().next().getValue();
        for (Map.Entry<Element, Integer> e : sorted.entrySet()) {
            Element elem = e.getKey();
            int level = elem.cssSelector().split(" > ").length;
            //elem.cssSelector()
            logger.info("(" + level + ")" + "\t" + " --> " + elem.text().replaceAll("\\s+", " "));
            double word_ratio = (double) e.getValue() / max;
            if (word_ratio > .7) {
                //               logger.info(e.getKey().id() + " | " + elem.className() + " | " + e.getValue() + " " + word_ratio);
                Integer order = order_map.get(elem);
                content_elems.put(elem, order);
            }
        }
        Map<Element, Integer> final_res = MapSort.sortByValue(content_elems);
        for (Map.Entry<Element, Integer> e : final_res.entrySet()) {
            extractions.add(e.getKey());
        }
    }

    public static int getLevel(Element e) {
        int level = 0;
        Element parent = e.parent();
        while (parent != null) {
            parent = parent.parent();
            level++;
        }
        return level;
    }

    public int getTLength(Element e) {
        int eTextNumWords = e.text().length();
        return eTextNumWords;
    }

    public int getTLength(List<Element> es) {
        int eTextNumWords = 0;
        for (Element e : es) {
            eTextNumWords += getTLength(e);
        }
        return eTextNumWords;
    }

    public List<Element> getTextElements(Elements parent, int minNumWords, int maxNumWords) {
        List<Element> elems = new ArrayList<>();
        for (Element e : parent) {
            int n = getLevel(e);
            if (n >= minNumWords && n <= maxNumWords) {
                elems.add(e);
            }
        }
        return elems;
    }

    public List<Element> getTextAllElements(Elements elements, int minNumWords, int maxNumWords) {
        List<Element> elems = new ArrayList<>();
        for (Element e : elements) {
            if (NO_TEXT_TAGS.contains(e.tagName())) continue;
            final String text = e.text();
            int n = text.length();
            if (n >= minNumWords && n <= maxNumWords) {
                elems.add(e);
            }
        }
        return elems;
    }

    public Elements getArticleNode() {
        Elements docElements = doc.body().getAllElements();
        docElements.select(".comments-panel").remove();
  //      List<Element> textHeavyElements = getTextAllElements(docElements, 10, 10000);
        List<Element> textHeavyElements = getTextAllElements(docElements, 60, 10000);

        int numWords = 0;
        List<Element> allTextHeavyOwnElements = new ArrayList<>();
        List<Element> allTextOwnElements = new ArrayList<>();
        for (Element e : docElements) {
            if (NO_TEXT_TAGS.contains(e.tagName())) continue;
            String text = e.ownText();
  //          int n = text.split("\\s+").length;
            int n = text.length();
            if (n >= 60) {
                allTextHeavyOwnElements.add(e);
 //               logger.info("TEXT " + text);
                numWords += n;
            } else if (n < 10 /*3*/) {
                allTextOwnElements.add(e);
            }
        }

        Map<Integer, Double> elem2score = new HashMap<>();

        for (int idx = 0; idx < textHeavyElements.size(); idx++) {
            Element e = textHeavyElements.get(idx);

            Elements allEments = e.getAllElements();
    //        List<Element> textOwnbHeavyElements = getTextElements(e.getAllElements(), 10, 10000);
            List<Element> textOwnbHeavyElements = getTextElements(allEments, 60, 10000);
            if (textOwnbHeavyElements.size() < 1) continue;

  //          List<Element> textOwnElements = getTextElements(e.getAllElements(), 0, 2);
            List<Element> textOwnElements = getTextElements(allEments, 0, 59);
            double totalWords = (double) getTLength(textOwnbHeavyElements);
    //        for (Element et : textOwnbHeavyElements) {
    //            double tw = (double) getLength(et);
                //       logger.info(idx + " " +et.elementSiblingIndex() + " / " + tw/totalWords + " " + textOwnbHeavyElements.size());
    //        }

            //    double p1 = (double) textOwnbHeavyElements.size() / (double)allTextHeavyOwnElements.size();
            double p3 = totalWords / (double) numWords;
            double p2 = allTextOwnElements.size() == 0 ? 0 : (double) textOwnElements.size() / (double) allTextOwnElements.size();

            double depth = getLevel(e);
            double score = p3 * depth * (1 - p2);
            elem2score.put(idx, score);
        }

        LinkedHashMap<Integer, Double> sortedElem2score = MapSort.sortdescByValue(elem2score);


        int thresh = 2;
        ArrayList<Integer> topIdxs = new ArrayList<>();
        for (Map.Entry<Integer, Double> ses : sortedElem2score.entrySet()) {
            if (thresh-- < 0) break;
            topIdxs.add(ses.getKey());
        }
        Collections.sort(topIdxs);

        List<Element> topTextElems = new ArrayList<>();
        for (int id : topIdxs) {
            Element goodElem = textHeavyElements.get(id);

            for (String tagName : NO_TEXT_TAGS) {
                goodElem.select(tagName).remove();
            }
            topTextElems.add(goodElem);
        }
        int textHeavyElementId = sortedElem2score.entrySet().iterator().next().getKey();
   //     articleNode = textHeavyElements.get(textHeavyElementId);
        return new Elements(topTextElems);
    }

    private String getElementStr(Element e) {
        StringBuilder sb = new StringBuilder();

        Set<String> classNames = e.classNames();
        if (classNames.size() != 0) {
            for (String c : classNames) {
                if (c == null || c.isEmpty()) continue;
                sb.append(c).append("class");
            }
        } else {
            Map<String, String> attr = e.attributes().dataset();
            if (attr != null) {
                for (Map.Entry<String, String> entry : attr.entrySet()) {
                    sb.append(entry.getKey()).append(entry.getValue());
                }
            }
        }
        return sb.toString();
    }

    public List<Element> getText() {
        ArrayList<Element> topTextElems = new ArrayList<>();
        switch (mode){
            case 0 :
                for (Element ae : articleNode) {
                    topTextElems.add(ae);
                }
                break;
            case 1:
                for (Element ae : articleNode) {
                    String txt = ae.text();
                    if (txt == null || txt.isEmpty()) continue;
                    txt = txt.replaceAll("[\"«»“”〝〟『』]","");
                    txt = txt.replaceAll("[               　 ]+"," ");
                    txt = txt.trim();
                    if (txt.length() > 1 && PUNCS.contains(txt.substring(txt.length() - 1))) {
                        topTextElems.add(ae);
                    } else {
                        txt = ae.ownText();
                        if (txt == null || txt.isEmpty()) continue;
                        txt = txt.replaceAll("[\"«»“”〝〟『』]","");
                        txt = txt.replaceAll("[               　 ]+"," ");
                        txt = txt.trim();
                        if (txt.length() > 1 && PUNCS.contains(txt.substring(txt.length() - 1))) {
                            topTextElems.add(ae);
                        }
                    }
                }
        }

        return topTextElems;
    }

    public String getNodeCss() {
        return articleNode.get(0).cssSelector();
    }

    private static final class QTNode implements Comparable{
        static int counter = 0;
        final static String css_delim = " > ";
        int id;
        int depth;
        Double length;
        Element node;
        LinkedHashSet<String> roots;

        public QTNode(Element n, int d){
            node = n;
            length = 0d;
            id = counter++;
            depth = d;
            roots = new LinkedHashSet<>();
            String [] parts  = n.cssSelector().split(css_delim);
            if (parts.length == 1){
                roots.add(parts[0]);
            } else {
                for (int i = 0; i < parts.length-1; i++) {
                    if (parts[i].contains("child")) break;
                    roots.add(String.join(css_delim, Arrays.copyOfRange(parts, 0, i+1)));
                }
            }
        }

        @Override
        public int compareTo(Object obj) {
            return this.length.compareTo(((QTNode)obj).length);
        }
    }

    private static final class ToTextNodeVisitor implements NodeVisitor {
        HashMap<String, QTNode> buffer;

        ToTextNodeVisitor() {
            buffer = new HashMap<>();
        }

        public HashMap<String, QTNode> getBuffer(){
            return buffer;
        }

        @Override
        public void head(Node node, int depth) {
            if (!(node instanceof TextNode)) return;
            TextNode textNode = (TextNode) node;
            String text = textNode.text().replace('\u00A0', ' ').trim(); // non breaking space
            if (text.isEmpty()) return;

            double length = text.length();

            if (length > 30) {
                Element elem = (Element) node.parent();
                try {
                    String key = elem.cssSelector();
                    QTNode qtnode = buffer.get(key);
                    if (qtnode == null){
                        qtnode = new QTNode(elem, depth);
                        buffer.put(key, qtnode);
                        qtnode = buffer.get(key);
                    }
                    qtnode.length += length;
                } catch (Exception exp){
                    logger.error("Jsoup parsing error");
                }

            }
        }

        @Override
        public void tail(Node node, int depth) {

        }
    }


    public void analyze3() {
        ToTextNodeVisitor nvistor = new ToTextNodeVisitor();
        NodeTraversor nt = new NodeTraversor(nvistor);
        nt.traverse(doc);
        Map<String, QTNode> output = nvistor.getBuffer();

        Map<String, Double> outputRootDepth = new HashMap<>();
        for (Map.Entry<String, QTNode> e : output.entrySet()){
            QTNode qtNode = e.getValue();
            for (String r : qtNode.roots) {
                Double val = outputRootDepth.get(r);
                if (val == null) {
                    val = 0d;
                }
                outputRootDepth.put(r, qtNode.length * qtNode.depth + val);
            }
        }
        Map<String, Double> outputRootDepthSorted = MapSort.sortdescByValue(outputRootDepth);

        double contentValue = outputRootDepthSorted.entrySet().iterator().next().getValue();
        HashSet<String> keptCssSelectors = new HashSet<>();

        HashMap<Element, Integer> toKeep = new HashMap<>();
        for (Map.Entry<String, Double> e : outputRootDepthSorted.entrySet()){
            double p = e.getValue() / contentValue;
            String key = e.getKey();
            if (p > .3) {
                for (Map.Entry<String, QTNode> qte : output.entrySet()){
                    QTNode qtNode = qte.getValue();
                    Element elem = qtNode.node;
                    String elemCssPath = elem.cssSelector();
                    if (!keptCssSelectors.contains(elemCssPath) && qtNode.roots.contains(key)){
                        keptCssSelectors.add(elemCssPath);
                        toKeep.put(elem, qtNode.id);
                    }
                }
            } else {
                break;
            }
        }
        /*

        Map<String, QTNode> outputSorted = MapSort.sortdescByValue(output);
//        int topN = 2;

        HashMap<Element, Integer> toKeep = new HashMap<>();
        double pastTLength = outputSorted.entrySet().iterator().next().getValue().node.text().length();
        int d = outputSorted.entrySet().iterator().next().getValue().depth;
        for (Map.Entry<String, QTNode> e : outputSorted.entrySet()){
            QTNode qtNode = e.getValue();
            Element txtElem = qtNode.node;
            String elemText = txtElem.text();
            double portion = elemText.length() / pastTLength;
            if (portion > .15 && Math.abs(d - qtNode.depth) < 2){
                toKeep.put(txtElem, qtNode.id);
            }
        //    articleNode =  txtElem;

        }
        */
        Map<Element, Integer> toKeepsorted = MapSort.sortByValue(toKeep);
        articleNode.addAll(toKeepsorted.keySet());

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
        for (Map.Entry<Element, Integer> e : sorted.entrySet()) {
            double word_ratio = (double) e.getValue() / max;
            if (word_ratio > .7) {
                extractions.add(e.getKey());
            }
        }


    }

    public List<Element> getExtractions() {
        return extractions;
    }

    public static void main(String[] args) throws Exception {

        String[] urls = {
                "http://www.yomiuri.co.jp/national/20180529-OYT1T50136.html?from=ytop_main8"
        //        "https://www.duolingo.com/comment/13955228/A-guide-to-the-Russian-word-order"
          //      "https://www.jpost.com/Blogs/A-Mid-East-Journal/The-trouble-with-Iran-558371"
           //     "http://dailycaller.com/2018/05/27/iran-deal-architect-chants-death-to-america-john-kerry/"
             //   "https://www.nytimes.com/2017/04/11/us/politics/sean-spicer-hitler-gas-holocaust-center.html"
            //               "http://www.dailymail.co.uk/news/article-4356348/Carlos-Jackal-awaiting-verdict-Paris-court.html"
           //          "https://www.cnet.com/news/heres-how-much-the-galaxy-s8-and-s8-plus-will-cost-you/"
          //      "https://www.cnet.com/news/heres-how-much-the-galaxy-s8-and-s8-plus-will-cost-you/"
         //       "https://www.nytimes.com/2017/04/11/us/alabama-governor-robert-bentley-sex-scandal.html?hp&action=click&pgtype=Homepage&clickSource=story-heading&module=second-column-region&region=top-news&WT.nav=top-news"
            //    "https://www.washingtonpost.com/news/politics/wp/2017/03/29/the-nunes-white-house-question-assessed-minute-by-minute/?utm_term=.e173fe569434"
         //   "https://www.reuters.com/article/us-tesla-stocks-idUSKBN17F2FF"
                //  "https://www.nytimes.com/2017/03/29/world/asia/china-taiwan-activist-lee-ming-cheh.html"
             //    "http://www.cnn.com/2017/03/29/politics/senate-filibuster-neil-gorsuch/index.html"
       //     "https://www.msn.com/en-us/news/world/guineas-new-pm-unveils-government/ar-AAxSLyG"
        //        "http://www.tampabay.com/amid-rape-allegation-jordaan-re-elected-safrica-soccer-boss-ap_sportse89c99a3f2954689943a0302208e4daf"
       //         "https://www.usnews.com/news/world/articles/2018-05-27/south-africas-opposition-rejects-report-that-party-will-split"
       //  "https://www.10news.com/newsy/at-least-22-people-dead-14-injured-after-3vehicle-crash-in-uganda"
           //     "http://www.cnbc.com/2017/03/28/apple-iphone-suppliers-outlook-jpmorgan.html"

              //  "https://www.theatlantic.com/international/archive/2017/03/donald-trump-china-rachman/521055/",
              //  "http://www.express.co.uk/news/uk/784786/marine-a-sentence-reduced-seven-years-alexander-blackman-law-crime"
        };

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";

        String allPunc = "。.!?؟¿¡";
        for (String u : urls) {
            logger.info(u);
            try {

                Document doc = Jsoup.connect(u)
                        .followRedirects(true)
                        .referrer("http://www.google.com")
                        .userAgent(USER_AGENT)
                        .ignoreContentType(true)
                        .timeout(10000)
                        .method(Connection.Method.GET)
               //         .execute()
               //         .bodyAsBytes()
                        .get();

                ArticleBodyResolver abr = new ArticleBodyResolver(doc, 1);
                abr.analyze3();
                List<Element> elems = abr.getText();
                for (Element e : elems) logger.info(e.tagName() + " | " + e.text());

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                logger.error("Error: " + sStackTrace);
            }
        }
    }

}
