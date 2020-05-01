package com.quantxt.helper;

import com.quantxt.types.MapSort;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by matin on 3/26/17.
 */
public class ArticleBodyResolver {

    final private static Logger log = LoggerFactory.getLogger(ArticleBodyResolver.class);

    public static String PUNCS = "。.!?؟¿¡";

    private Element element;
    private List<Element> extractions;

    //0: default
    //1: Good for well written text, long enough sentences ending with punctuations
    private int mode;

    private List<Element> articleNode = new ArrayList<>();

    public ArticleBodyResolver(Element element) {
        this.element = element;
        extractions = new ArrayList<>();
        mode = 1;
    }

    // mode 0 simple
    // mode 1 Parse HTML with pre-defined rules (current)
    public ArticleBodyResolver(Element element, int mode) {
        this.element = element;
        extractions = new ArrayList<>();
        this.mode = mode;
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
                if (elem == null) return;
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
                    log.debug("Jsoup parsing error " + exp.getMessage());
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
        nt.traverse(element);
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
        Map<Element, Integer> toKeepsorted = MapSort.sortByValue(toKeep);
        articleNode.addAll(toKeepsorted.keySet());

    }

    public List<Element> getExtractions() {
        return extractions;
    }
}
