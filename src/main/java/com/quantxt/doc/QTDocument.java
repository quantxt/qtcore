package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Emit;
import com.quantxt.types.NamedEntity;

import static com.quantxt.types.Entity.NER_TYPE;

public abstract class QTDocument {

	public enum Language
	{
		ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
	}

	final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	final private static Pattern PAD = Pattern.compile("^\\s+");
	public enum DOCTYPE {Headline, Action, Statement, Aux, Speculation,
		Legal, Acquisition, Production, Partnership, Employment, Development
	}

	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(QTDocument.class);

	protected String title;
	protected String rawTitle;
	protected String englishTitle;
	protected String body;
	protected Language language;
	protected transient List<String> sentences;
	protected String excerpt;
	private DOCTYPE docType;

	private DateTime date;
	private String link;
	private double score;
	private String source;
	private String author;
	private String logo;
	private String id;

	protected transient QTDocumentHelper helper;

	//TODO: all these should be combined into a generic class
	private Set<String> tags = new HashSet<>();
	private Map<String, Object> facts;
	private Map<String, LinkedHashSet<String>> entity;
	private ArrayList<String> verbs;
	private ArrayList<String> nouns;
	private ArrayList<ExtInterval> values;

	public QTDocument(String b, String t, QTDocumentHelper helper){
		if (b != null) {
			body = b.replaceAll("([\\n\\r])", " $1");
		}
		title = t.replaceAll("[\\n\\r]+","").trim();
		this.helper = helper;
	}

	protected void addBasicPropstoSubDoc(QTDocument reference)
	{
		this.language = reference.language;
		this.date = reference.date;
		this.link = reference.link;
		this.logo = reference.logo;
		this.source = reference.source;
	}

	// Getters
	public String getBody(){
		return body;
	}

	public String getExcerpt(){
		return excerpt;
	}

	public Language getLanguage(){
		return language;
	}

	public String getLink(){
		return link;
	}

	public String getRawTitle(){
		return rawTitle;
	}

	public String getTitle(){
		return title;
	}

	public String getEnglishTitle(){
		return englishTitle;
	}

	public String getLogo(){return logo;}

	public double getScore(){return score;}

	public String getSource(){return source;}

	public String getId() {return id;}

	public DOCTYPE getDocType(){return docType;}

	public List<String> getNouns(){ return nouns;}

	public List<ExtInterval> getValues(){ return values;}

	public List<String> getVerbs(){ return verbs;}

	public Set<String> getTags(){ return tags;}

	public synchronized String getDateStr() {
		return dateFormat.format(date);
	}

	public void setDocType(DOCTYPE dt){
		docType = dt;
	}

	public String getAuthor() {return author;}

	public Map<String, Object> getKey_values(){return facts;}

	public DateTime getDate() {return date;}

	public List<String> getSentences(){
		return sentences;
	}

	abstract List<QTDocument> getChilds();

	abstract double[] getVectorizedTitle(QTExtract extract);

	abstract String Translate(String text, Language inLang, Language outLang);

	abstract boolean isStatement(String s);

	private int getNextValidIndex(String str, int startlookup){
		int lookaheadStart = startlookup + 1;
		int subLen = str.length() - lookaheadStart;
		if (subLen < 0) return 0;
		Matcher m = PAD.matcher(str.substring(lookaheadStart));
		if (!m.find()) return 0;
		//shift keyend to end of the pad
		return m.end();
	}

	public void extractKeyValues(QTExtract speaker,
								 int dist,
								 boolean changeTitle) {
		// TODO: hacky quick check
		// If there are no numbers then don't bother
		if ( !(title.contains("0") || title.contains("1") || title.contains("2") || title.contains("3") ||
				title.contains("4") || title.contains("5") || title.contains("6") || title.contains("7")||
				title.contains("8") || title.contains("9") ) ){
			return;
		}

		final String rawSent_curr = title;

		Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(rawSent_curr);
		if (name_match_curr.size() == 0) return;
		ArrayList<ExtInterval> values = new ArrayList<>();
		// get potential values
		helper.getValues(rawSent_curr, values);
		if (values.size() == 0) return;

		for (ExtInterval v : values){
			logger.debug("--- \t" + v.toString(rawSent_curr));
		}
		// now we try to find keys for every row
		String titleTable = "";
		//ignore duplicate rows
		HashSet<String> rows = new HashSet<>();

		for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
			String keyGroup = entType.getKey();
			for (Emit matchedName : entType.getValue()) {
				NamedEntity ne = (NamedEntity) matchedName.getCustomeData();
				String key = ne.getName();
				int keyEnd = matchedName.getEnd();
				if (keyEnd < 0){
					logger.error("key wrong ---- {} ----- in '{}'", matchedName.getKeyword() , title);
					continue;
				}
				int shift = getNextValidIndex(rawSent_curr, keyEnd);
				keyEnd += shift;

				logger.debug(" ---- KEY: '" + key + "' === " + matchedName.getKeyword());
				ArrayList<ExtInterval> rowValues = new ArrayList<>();
				for (ExtInterval extv : values) {
					int valStart = extv.getStart();
					int diff = (valStart - keyEnd);
					if (diff >= 0 && diff < dist) {
			//			logger.info("\t value in {} is ----------------- {} --> {}", rawSent_curr, key, extv.toString(rawSent_curr));
						keyEnd = extv.getEnd();
						shift = getNextValidIndex(rawSent_curr, keyEnd);
						keyEnd += shift;
						rowValues.add(extv);
					}
				}

				if (rowValues.size() > 0) {
					StringBuilder sb = new StringBuilder();
					sb.append("<tr>");
					sb.append("<td>").append(key).append("</td>");
					for (ExtInterval extv : rowValues) {
						sb.append("<td>").append(extv.getCustomData().toString()).append("</td>");
					}
					sb.append("</tr>");
					String row2add = sb.toString();
					if (rows.contains(row2add)) continue;
					titleTable += row2add;
					rows.add(row2add);
				}
			}
		}

		if (changeTitle) {
			if (!titleTable.isEmpty()) {
				title = "<table width=\"100%\">" + titleTable + "</table>";
			} else {
				title = "";
			}
		}
		this.values = values;
	}

	public ArrayList<QTDocument> extractEntityMentions(QTExtract speaker) {
		ArrayList<QTDocument> quotes = new ArrayList<>();
		List<QTDocument> childs = getChilds();
		int numSent = childs.size();

		for (int i = 0; i < numSent; i++)
		{
			QTDocument workingChild = childs.get(i);
			final String rawSent_curr = workingChild.getTitle();
			final String rawSent_before = i == 0 ? title : childs.get(i - 1).getTitle();

			List<String> tokens = helper.tokenize(rawSent_curr);
			String [] parts = tokens.toArray(new String[tokens.size()]);
//			logger.info("RAW {}", rawSent_curr);
			if (! helper.isSentence(rawSent_curr, tokens)) continue;
//			final String orig_tokenized = String.join(" ", parts);
/*
            try {
                Files.write(Paths.get("snp500.txt"), (orig  +"\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

			if (speaker != null && speaker.hasEntities()) {
		//		Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(orig_tokenized);
				Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(rawSent_curr);
				if (name_match_curr.size() == 0) {
			//		final String before_tokenized = String.join(" ", helper.tokenize(rawSent_before));
			//		Map<String, Collection<Emit>> name_match_befr = speaker.parseNames(before_tokenized);
					Map<String, Collection<Emit>> name_match_befr = speaker.parseNames(rawSent_before);
					for (Map.Entry<String, Collection<Emit>> entType : name_match_befr.entrySet()) {
						Collection<Emit> ent_set = entType.getValue();
						if (ent_set.size() != 1) continue;
						// simple co-ref for now
						if (helper.getPronouns().contains(parts[0])) {
							name_match_curr.put(entType.getKey(), ent_set);
						}
					}
				}

				if (name_match_curr.size() != 0) {
					for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
						for (Emit matchedName : entType.getValue()) {
							NamedEntity ne = (NamedEntity) matchedName.getCustomeData();
							workingChild.addEntity(entType.getKey(), ne.getName());
				//			logger.info("\t" + entType.getKey() + " | " + ne.getName());
						}
					}
				}
			}



				//if still no emit continue
	//			if (name_match_curr.size() == 0) {
	//				continue;
	//			}

			List<ExtInterval> tagged = helper.getNounAndVerbPhrases(rawSent_curr, parts);
			for (ExtInterval ext : tagged) {
				switch (ext.getType()) {
					case VERB :
						String verb = rawSent_curr.substring(ext.getStart(), ext.getEnd());
						DOCTYPE verbType = helper.getVerbType(verb);
						if (verbType != null) {
							workingChild.setDocType(verbType);
						}
					break;
					case NOUN :
						String noun = rawSent_curr.substring(ext.getStart(), ext.getEnd());
						workingChild.addEntity(NER_TYPE, noun);
				}
			}

			if (workingChild.getEntity() == null  || workingChild.getEntity().size() == 0) {
				logger.debug("Entity is still null or Verb type is not detected: " + rawSent_curr);
				continue;
			}

			workingChild.setBody(rawSent_before + " " + rawSent_curr);
			quotes.add(workingChild);
		}

		return quotes;
	}

	public void setScore(double s){
		score = s;
	}

	protected void setExcerpt(String s){
		excerpt = s;
	}

	public void setBody(String b){
		body = b;
	}

	public void addTags (List<String> taglist){
		tags.addAll(taglist);
	}

	public Map<String, LinkedHashSet<String> > getEntity(){return entity;}

	public void setEntity(Map<String, LinkedHashSet<String>> entity){this.entity = entity;}

	public void addTag (String tag){
		tags.add(tag);
	}

	public void setTags (Set<String> tags){
		this.tags = tags;
	}

	public void setLogo(String s){
		logo = s;
	}

	public void setSource(String s){
		source = s;
	}

	public void setSentences(List<String> s){
		sentences = s;
	}

	public void setDate(DateTime d){date = d; }

	public void setRawTitle(String s){rawTitle = s; }

	public void setValues(ArrayList<ExtInterval> s){values = s; }

	public void addFacts(String key, Object val){
		if (facts == null){
			facts = new HashMap<>();
		}

		facts.put(key, val);
	}

	public void setFacts(Map<String, Object> map){
		if (map == null || map.size() == 0){
			facts = null;
			return;
		}

		if (facts == null){
			facts = new HashMap<>();
		}

		facts.putAll(map);
	}

	/*
	public static void resetTranslatCred(){
		Translate.setClientId("2b70575a-116a-40db-9cc8-4b5192659506");
		Translate.setClientSecret("g184U2+B7fwftaoGzBDyb59KzYEKtulZZQZsnW71wj4=");
	}
	*/

	public void setLink (String l){
		link = l;
	}

	public void setAuthor(String a){
		author = a;
	}

	public void setLanguage(Language l){
		language = l;
	}

	public void addEntity(String t, String e){
		LinkedHashSet<String> ents;
		if (entity == null){
			entity = new HashMap<>();
			ents = new LinkedHashSet<>();
		} else {
			ents = entity.get(t);
			if (ents == null) {
				ents = new LinkedHashSet<>();
			}
		}
		ents.add(e);
		entity.put(t, ents);
	}

	public String getWPDocument(){
		String date_str = dateFormat.format(date);
		JsonObject json = new JsonObject();
//		json.addProperty("ID", id);
//		json.addProperty("post_id", id);
		json.addProperty("post_date", date_str);
		json.addProperty("post_date_gmt", date_str);
		json.addProperty("post_title", title);
		json.addProperty("post_content", body);
		json.addProperty("post_status", "publish");
		json.addProperty("post_name", title);
		json.addProperty("post_type", "post");
		json.addProperty("permalink", link);
		return json.toString();
	}

	public String toString(){
		return gson.toJson(this);
	}

    public QTDocumentHelper getHelper() {
        return helper;
    }

    public void setHelper(QTDocumentHelper helper) {
        this.helper = helper;
    }

}