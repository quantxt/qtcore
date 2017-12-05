package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.quantxt.types.NamedEntity;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QTDocument {

	public enum Language
	{
		ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI
	}

	final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	final private static DateFormat dateOnlyFormat = new SimpleDateFormat("yyyy/MM/dd");
	public enum DOCTYPE {Headline, Action, Statement, Aux, Speculation,
		Legal, Acquisition, Production, Partnership, Employment
	}

	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(QTDocument.class);

	protected String title;
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


	public QTDocument(String b, String t){
		if (b != null) {
			body = b.replaceAll("([\\\n\\\r])", " $1");
		}
		title = t.replaceAll("[\\\n\\\r\\\t]","");
	}

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

	public String getDateStr() {
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

	public double[] getVectorizedTitle(QTExtract extract){
		//sub-class should implement this
		return null;
	}

	//interface to process document
	public void processDoc(){
		//sub-class should implement this
	}
	public String Translate(String text, Language inLang, Language outLang){
		//sub-class should implement this
		return null;
	}
	public boolean isStatement(String s){
		//sub-class should implement this
		return false;
	}

	public String normalize(String str){
		//sub-class should implement this
		return str;
	}

	public String [] getPosTags(String [] text)
	{
		//sub-class should implement this
		return null;
	}

	public ArrayList<QTDocument> extractEntityMentions(QTExtract extract) {
		//sub-class should implement this
		return null;
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
}