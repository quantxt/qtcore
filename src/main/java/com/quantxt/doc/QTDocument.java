package com.quantxt.doc;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.*;

import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Emit;
import com.quantxt.trie.Trie;
import com.quantxt.types.Entity;
import com.quantxt.types.NamedEntity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class QTDocument {

	public enum Language
	{
		ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI
	}

	final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public enum DOCTYPE {Headline, Action, Statement, Aux, Speculation,
		Legal, Acquisition, Production, Partnership, Employment
	}

	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(QTDocument.class);

	protected String title;
	protected String rawText;
	protected String englishTitle;
	protected String body;
	protected Language language;
	protected List<String> sentences;
	protected String excerpt;

	private DateTime date;
	private String link;
	private double score;
	private String source;
	private String author;
	private String logo;
	private String id;
	private Set<String> categories;
	private Set<String> sub_categories;
	private Set<String> tags = new HashSet<>();
	private Map<String, Object> facts;
	private DOCTYPE docType;
	private Map<String, LinkedHashSet<String>> entity;

	protected Set<String> locations;
	protected Set<String> persons;
	protected Set<String> organizations;


	protected static String removePrnts(String str){
		str = str.replaceAll("\\([^\\)]+\\)", " ");
		str = str.replaceAll("([\\.])+$", " $1");
		str = str.replaceAll("\\s+", " ");
		return str;
	}

	public QTDocument(String b, String t){
		if (b != null) {
			body = b.replaceAll("([\\\n\\\r])", " $1");
		}
		title = t.replaceAll("[\\\n\\\r\\\t]","");
	}


	/*
	protected QTDocument getQuoteDoc(String quote)
	{
		QTDocument sDoc = new QTDocument("", quote);
		sDoc.setDate(getDate());
		sDoc.setLink(getLink());
		sDoc.setLogo(getLogo());
		sDoc.setSource(getSource());
		sDoc.setLanguage(getLanguage());
		return sDoc;
	}
	*/


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

	public Set<String> getPersons(){
		return persons;
	}

	public Language getLanguage(){
		return language;
	}

	public Set<String> getOrganizations(){
		return organizations;
	}

	public String getLink(){
		return link;
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

	public Set<String> getTags(){ return tags;}

	public Set<String> getCategories (){
		return categories;
	}

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

	abstract void processDoc();

	abstract String Translate(String text, Language inLang, Language outLang);

	abstract boolean isStatement(String s);

	abstract String normalize(String str);

	abstract String [] getPosTags(String [] text);

	abstract HashSet<String> getPronouns();

	abstract CharArraySet getStopwords();

	abstract Trie getVerbTree();

	public List<String> tokenize(String str) {
		return null;
	}

	private DOCTYPE getVerbType(String verbPhs) {

		List<String> tokens = tokenize(verbPhs);
		if (tokens.size() == 0) return null;

		Collection<Emit> emits = getVerbTree().parseText(String.join(" ", tokens));
		for (Emit e : emits) {
			DOCTYPE vType = (DOCTYPE) e.getCustomeData();
			if (vType == DOCTYPE.Aux) {
				if (emits.size() == 1) return null;
				continue;
			}
			return vType;
		}
		return null;
	}


	protected List<ExtInterval> getNounAndVerbPhrases(String orig,
													  String[] parts)
	{
		//sub-class should implement this
		return null;
	}

	public ArrayList<QTDocument> extractEntityMentions(QTExtract speaker) {
		ArrayList<QTDocument> quotes = new ArrayList<>();
	//	List<String> sents = getSentences();
		List<QTDocument> childs = getChilds();
		int numSent = childs.size();

		for (int i = 0; i < numSent; i++)
		{
			QTDocument workingChild = childs.get(i);
			final String orig = removePrnts(workingChild.getTitle()).trim();
			final String origBefore = i == 0 ? title : removePrnts(childs.get(i - 1).getTitle()).trim();

			String rawSent_curr = orig;
		//	String[] parts = rawSent_curr.split("\\s+");
			List<String> tokens = tokenize(rawSent_curr);
			String [] parts = tokens.toArray(new String[tokens.size()]);
			int numTokens = parts.length;
			if (numTokens < 6 || numTokens > 80) continue;

/*
            try {
                Files.write(Paths.get("snp500.txt"), (orig  +"\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/
			Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(orig);

			if (name_match_curr.size() == 0) {
				Map<String, Collection<Emit>> name_match_befr = speaker.parseNames(origBefore);
				for (Map.Entry<String, Collection<Emit>> entType : name_match_befr.entrySet()) {
					Collection<Emit> ent_set = entType.getValue();
					if (ent_set.size() != 1) continue;
					// simple co-ref for now
				//	if (parts[0].equalsIgnoreCase("he") || parts[0].equalsIgnoreCase("she")) {
					if (getPronouns().contains(parts[0])){
						Emit matchedName = ent_set.iterator().next();
						String keyword = matchedName.getKeyword();
						parts[0] = keyword;
						rawSent_curr = String.join(" ", parts);
						name_match_curr.put(entType.getKey(), ent_set);
					}
				}
			}

			//if still no emit continue
			if (name_match_curr.size() == 0) {
				continue;
			}


		//	QTDocument newQuote = getQuoteDoc(orig);

			List<ExtInterval> tagged = getNounAndVerbPhrases(rawSent_curr, parts);
			for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
				for (Emit matchedName : entType.getValue()) {
					for (int j = 0; j < tagged.size(); j++) {
						ExtInterval ext = tagged.get(j);
						ExtInterval nextExt = (j < tagged.size() - 1) ? tagged.get(j + 1) : null;
						ExtInterval prevExt = (j > 0) ? tagged.get(j - 1) : null;
						if (ext.overlapsWith(matchedName) && ext.getType().equals("N")) {
							//only if this is a noun type and next one is a verb!
							DOCTYPE verbType = null;
							if (nextExt != null && nextExt.getType().equals("V")) {
								verbType = getVerbType(rawSent_curr.substring(nextExt.getStart(), nextExt.getEnd()));
							} else if (prevExt != null && prevExt.getType().equals("V")) {
								verbType = getVerbType(rawSent_curr.substring(prevExt.getStart(), prevExt.getEnd()));
							}
							NamedEntity ne = (NamedEntity) matchedName.getCustomeData();
							workingChild.addEntity(entType.getKey(), ne.getName());
							if (verbType != null) {
								workingChild.setDocType(verbType);
							}
						}
					}
				}
			}

			if (workingChild.getEntity() == null) {
				logger.debug("Entity is still null or Verb type is not detected: " + orig);
				continue;
			}


			workingChild.setBody(origBefore + " " + orig);
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

	public void setCategories (String s){
		if (categories == null){
			categories = new HashSet<>();
		}
		categories.add(s);
	}

	public void addTags (List<String> taglist){
		tags.addAll(taglist);
	}

	public Map<String, LinkedHashSet<String> > getEntity(){return entity;}

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

	public void addPerson(String p){
		if (persons == null){
			persons = new HashSet<>();
		}
		persons.add(p);
	}

	/*
	public static void resetTranslatCred(){
		Translate.setClientId("2b70575a-116a-40db-9cc8-4b5192659506");
		Translate.setClientSecret("g184U2+B7fwftaoGzBDyb59KzYEKtulZZQZsnW71wj4=");
	}
	*/

	public void addOrganization(String o){
		if (organizations == null){
			organizations 	= new HashSet<>();
		}
		organizations.add(o);
	}

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

	public Trie buildVerbTree(final byte[] verbArr) throws IOException {
		JsonParser parser = new JsonParser();
		Trie.TrieBuilder verbs = Trie.builder().onlyWholeWords().ignoreCase();
		JsonElement jsonElement = parser.parse(new String(verbArr, "UTF-8"));
		JsonObject contextJson = jsonElement.getAsJsonObject();
		for (Map.Entry<String, JsonElement> entry : contextJson.entrySet()) {
			String context_key = entry.getKey();
			DOCTYPE verbTybe = null;
			switch (context_key) {
				case "Speculation" : verbTybe = DOCTYPE.Speculation;
					break;
				case "Action" : verbTybe = DOCTYPE.Action;
					break;
				case "Partnership" : verbTybe = DOCTYPE.Partnership;
					break;
				case "Legal" : verbTybe = DOCTYPE.Legal;
					break;
				case "Acquisition" : verbTybe = DOCTYPE.Acquisition;
					break;
				case "Production" : verbTybe = DOCTYPE.Production;
					break;
				case "Aux" : verbTybe = DOCTYPE.Aux;
					break;
				case "Employment" : verbTybe = DOCTYPE.Employment;
					break;
				case "Statement" : verbTybe = DOCTYPE.Statement;
					break;
			}

			if (verbTybe == null) continue;

			JsonArray context_arr = entry.getValue().getAsJsonArray();
			for (JsonElement e : context_arr) {
				String verb = e.getAsString();
				List<String> tokens = tokenize(verb);
				verbs.addKeyword(String.join(" ", tokens), verbTybe);
			}
		}
		return verbs.build();
	}
}