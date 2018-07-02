package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Emit;
import com.quantxt.types.NamedEntity;
import com.quantxt.util.StringUtil;

import static com.quantxt.types.Entity.NER_TYPE;

public abstract class QTDocument {

	public enum Language
	{
		ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE
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
	protected transient List<String> sentences;
	protected String excerpt;

	private DateTime date;
	private String link;
	private double score;
	private String source;
	private String author;
	private String logo;
	private String id;

	private ArrayList<String> verbs;
	private ArrayList<String> nouns;

	private Set<String> categories;
	private Set<String> sub_categories; //TODO Check this - not used
	private Set<String> tags = new HashSet<>();
	private Map<String, Object> facts;
	private DOCTYPE docType;
	private Map<String, LinkedHashSet<String>> entity;

	protected Set<String> locations;
	protected Set<String> persons;
	protected Set<String> organizations;
	protected transient QTDocumentHelper helper;

	public QTDocument(String b, String t, QTDocumentHelper helper){
		if (b != null) {
			body = b.replaceAll("([\\\n\\\r])", " $1");
		}
		title = t.replaceAll("[\\\n\\\r\\\t]","");
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

	public List<String> getNouns(){ return nouns;}

	public List<String> getVerbs(){ return verbs;}

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

	abstract String Translate(String text, Language inLang, Language outLang);

	abstract boolean isStatement(String s);

	public ArrayList<QTDocument> extractEntityMentionsV2(QTExtract speaker) {
		ArrayList<QTDocument> quotes = new ArrayList<>();
		List<QTDocument> childs = getChilds();
		int numSent = childs.size();

		for (int i = 0; i < numSent; i++)
		{
			QTDocument workingChild = childs.get(i);
			final String orig = StringUtil.removePrnts(workingChild.getTitle()).trim();
			final String origBefore = i == 0 ? title : StringUtil.removePrnts(childs.get(i - 1).getTitle()).trim();

			String rawSent_curr = orig;

			List<String> tokens = helper.tokenize(rawSent_curr);
			String [] parts = tokens.toArray(new String[tokens.size()]);
			if (! helper.isSentence(rawSent_curr, tokens)) continue;

			List<ExtInterval> tagged = helper.getNounAndVerbPhrases(rawSent_curr, parts);

			if (tagged.size() == 0) continue;
			workingChild.verbs = new ArrayList<>();
			workingChild.nouns = new ArrayList<>();
			for (ExtInterval ei : tagged){
				String typ = ei.getType();
				String str = rawSent_curr.substring(ei.getStart(), ei.getEnd());
				switch (typ) {
					case "N" : workingChild.nouns.add(str);
						break;
					case "V" : workingChild.verbs.add(str);
						break;
				}
			}
			if (speaker != null) {
				Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(orig);
				if (name_match_curr.size() == 0) {
					Map<String, Collection<Emit>> name_match_befr = speaker.parseNames(origBefore);
					for (Map.Entry<String, Collection<Emit>> entType : name_match_befr.entrySet()) {
						Collection<Emit> ent_set = entType.getValue();
						if (ent_set.size() != 1) continue;
						// simple co-ref for now
						if (helper.getPronouns().contains(parts[0])) {
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
									verbType = helper.getVerbType(rawSent_curr.substring(nextExt.getStart(), nextExt.getEnd()));
								} else if (prevExt != null && prevExt.getType().equals("V")) {
									verbType = helper.getVerbType(rawSent_curr.substring(prevExt.getStart(), prevExt.getEnd()));
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
			}

			workingChild.setBody(origBefore + " " + orig);
			quotes.add(workingChild);
		}

		return quotes;
	}

	public ArrayList<QTDocument> extractEntityMentions(QTExtract speaker) {
		ArrayList<QTDocument> quotes = new ArrayList<>();
		List<QTDocument> childs = getChilds();
		int numSent = childs.size();

		for (int i = 0; i < numSent; i++)
		{
			QTDocument workingChild = childs.get(i);
			final String orig = StringUtil.removePrnts(workingChild.getTitle()).trim();
			final String origBefore = i == 0 ? title : StringUtil.removePrnts(childs.get(i - 1).getTitle()).trim();

			String rawSent_curr = orig;

			List<String> tokens = helper.tokenize(rawSent_curr);
			String [] parts = tokens.toArray(new String[tokens.size()]);
			if (! helper.isSentence(rawSent_curr, tokens)) continue;

/*
            try {
                Files.write(Paths.get("snp500.txt"), (orig  +"\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
*/

			if (speaker != null && speaker.hasEntities()) {
				Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(orig);
				if (name_match_curr.size() == 0) {
					Map<String, Collection<Emit>> name_match_befr = speaker.parseNames(origBefore);
					for (Map.Entry<String, Collection<Emit>> entType : name_match_befr.entrySet()) {
						Collection<Emit> ent_set = entType.getValue();
						if (ent_set.size() != 1) continue;
						// simple co-ref for now
						if (helper.getPronouns().contains(parts[0])) {
							Emit matchedName = ent_set.iterator().next();
							String keyword = matchedName.getKeyword();
							parts[0] = keyword;
							rawSent_curr = String.join(" ", parts);
							name_match_curr.put(entType.getKey(), ent_set);
						}
					}
				}

				if (name_match_curr.size() != 0) {
					for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
						for (Emit matchedName : entType.getValue()) {
							NamedEntity ne = (NamedEntity) matchedName.getCustomeData();
							workingChild.addEntity(entType.getKey(), ne.getName());
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
					case ("V") :
						String verb = rawSent_curr.substring(ext.getStart(), ext.getEnd());
						DOCTYPE verbType = helper.getVerbType(verb);
						if (verbType != null) {
							workingChild.setDocType(verbType);
						}
					break;
					case ("N") :
						String noun = rawSent_curr.substring(ext.getStart(), ext.getEnd());
						workingChild.addEntity(NER_TYPE, noun);
				}
			}


			if (workingChild.getEntity() == null  || workingChild.getEntity().size() == 0) {
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

    public QTDocumentHelper getHelper() {
        return helper;
    }

    public void setHelper(QTDocumentHelper helper) {
        this.helper = helper;
    }

}