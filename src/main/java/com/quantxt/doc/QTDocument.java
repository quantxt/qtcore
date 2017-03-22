package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.memetix.mst.language.Language;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class QTDocument {

	final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	final private static DateFormat dateOnlyFormat = new SimpleDateFormat("yyyy/MM/dd");

	final private static String DATE_SEPARATOR_STR = "(?:[\\@\\.\\s,\\-\\/\\\\\\|\\&;]+|$)";
	final private static String MONTH_NAME_STR   = "(Jan|Feb|Mar|Apr|May|June|Jul|Aug|Sep|Oct|Nov|Dec)(?:[a-zA-Z]*)";
	final private static String DAY_STR = "([0123][0-9]|[1-9])";
	final private static String MONTH_STR = "([01][0-9]|[1-9])";
	final private static String YEAR_STR = "([12]\\d{3})";
	private final static HashMap<Pattern, int[]> DATE_PATTERN_MAP = new HashMap<>();

	static {
		DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + MONTH_NAME_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2});
		DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + DAY_STR + DATE_SEPARATOR_STR + MONTH_NAME_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 2, 1});
		DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + DATE_SEPARATOR_STR + YEAR_STR + DATE_SEPARATOR_STR , Pattern.CASE_INSENSITIVE), new int[]{3, 1, 2});
		DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + YEAR_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + DAY_STR + "(?:T|\\s|\\b)"), new int[]{1, 2, 3});
		DATE_PATTERN_MAP.put(Pattern.compile("(?:^|\\s)" + DAY_STR + DATE_SEPARATOR_STR + MONTH_STR + DATE_SEPARATOR_STR + YEAR_STR + "(?:T|\\s|\\b)"), new int[]{3, 2, 1});
	}

	private static DateTimeParser[] DATE_PARSER = {
			DateTimeFormat.forPattern("yyyy MMM dd").getParser(),
			DateTimeFormat.forPattern("yyyy MM dd").getParser()
	};

	private static DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder().append( null, DATE_PARSER ).toFormatter();
	private static DateTimeFormatter DATE_STR_FORMATTER = DateTimeFormat.forPattern( "yyyy-MM-dd");

	private static DateTimeParser[] parsers = {
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss.SSSZ").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ssZ").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ss z").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm:ssz").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mmZ").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm a z").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm a").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd HH:mm z").getParser(),
			DateTimeFormat.forPattern( "yyyy-MM-dd ").getParser()
	};

	private static DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();

	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(QTDocument.class);

	protected String title;
	protected String englishTitle;
	protected String body;
	protected String language;
	protected List<String> sentences;
	protected String excerpt;

	private DateTime date;
	private String link;
	private String source;
	private String author;
	private String logo;
	private String region;
	private String id;

	protected String entity;
	protected Set<String> persons;
	protected List<String> organizations = null;
	private List<String> ticker = null;

//	protected List<String> statements = new ArrayList<>();
//	protected List<String> actions = new ArrayList<>();
//	protected Map<Integer, Integer> topics;
	protected String sector;
	protected String industry;
//	protected List<Integer> topics;
//	protected String topics = null;

	private String categories;
	protected String label;
	private Set<String> tags = new HashSet<>();

	public QTDocument(String b, String t){
		body = b.replaceAll("([\\\n\\\r])"," $1");
		title = t;
		title = title.replaceAll("[\\\n\\\r\\\t]","");
	}
	
	protected String Translate(String text, Language inLang, Language outLang){
		return "";
//		return Translate.execute(text, inLang, outLang);
	}
	
	protected abstract boolean isStatement(String s);

	protected void setSector(String s){
		sector = s;
	}

	protected void setExcerpt(String s){
		excerpt = s;
	}

	public void setBody(String b){
		body = b;
	}

	public void setCategories (String s){
		categories = s;
	}

	public void addTags (List<String> taglist){
		tags.addAll(taglist);
	}

	public void addTag (String tag){
		tags.add(tag);
	}
	
	protected void setIndustry(String s){
		industry = s;
	}

	public void setLogo(String s){
		logo = s;
	}

	public void setSource(String s){
		source = s;
	}

	public void setDate(String d){
		date = findDate(d);
		if (date == null){
			logger.error(d + "is not a valid datetime format");
		}
	}

	public void addPerson(String p){
		persons.add(p);
	}
	public String getCategories (){
		return categories;
	}

	/*
	public static void resetTranslatCred(){
		Translate.setClientId("2b70575a-116a-40db-9cc8-4b5192659506");
		Translate.setClientSecret("g184U2+B7fwftaoGzBDyb59KzYEKtulZZQZsnW71wj4=");
	}
	*/
	public void addOrganization(String o){
		if (organizations == null){
			organizations 	= new ArrayList<String>();
		}
		organizations.add(o);
	}
	
	public void addTicker(String o){
		if (ticker == null){
			ticker 	= new ArrayList<String>();
		}
		ticker.add(o);
	}
	
	public void setLink (String l){
		link = l;
	}

	public void setAuthor(String a){
		author = a;
	}
	
	public void setRegion(String r){
		region = r;
	}
	
	public void setLanguage(String l){
		language = l;
	}

	public void setLabel(String l){
		label = l;
	}

	public void setEntity(String e){
		entity = e;
	}
	
	public String getTokenizedBody(){
		String b = body;
		b = b.toLowerCase();
		b = b.replaceAll("[^a-z0-9]+", " ");
		return b;
	}
	
	public String getLabel(){
		return label;
	}

	public String getBody(){
		return body;
	}

	public String getExcerpt(){
		return excerpt;
	}
	
	public Set<String> getPersons(){
		return persons;
	}
	
	public String getLanguage(){
		return language;
	}
	
	public List<String> getOrganizations(){
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

	public String getSource(){return source;}

//	public int getID() {return id;}

	public Set<String> getTags(){ return tags;}

	public String getDateStr() {
		return dateFormat.format(date);
	}

	public String getAuthor() {return author;}

	public List<String> getSentences(){
		return sentences;
	}

//	public List<String> getStatements(){
//		return statements;
//	}

//	public List<String> getActions(){
//		return actions;
//	}

	//interface to process document
	public void processDoc() {
	}
	
	public String toString(){
		return gson.toJson(this);
	}


	public DateTime setDate(Document doc) throws Exception {
		Elements timeElem = doc.getElementsByTag("time");
		if (timeElem != null && timeElem.size() > 0) {
			for (Attribute attr : timeElem.first().attributes()) {
				date = findDate(attr.getValue());
				if (date != null) return date;
			}
			date = findDate(timeElem.text());
			if (date != null) return date;

		}
		// get the earliest date when pasre the attributes
//        DateTime curr_date = null;
		for(Element element : doc.getAllElements() )
		{
			for (Attribute attr : element.attributes()){
				String text = attr.getValue();
				if (text == null || text.length() > 85 || text.split("\\s+").length > 9) continue;
				//            logger.info(text);
				date = findDate(text);
				if (date != null) return date;
			}
		}

		date = findDate(doc);
		if (date == null){
			logger.debug("Date was not found");
		}
		return date;
	}

	private DateTime normalizeDateStr(String date_string,
											 Matcher m,
											 int [] vals)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(m.group(vals[0]))
				.append(" ")
				.append(m.group(vals[1]))
				.append(" ")
				.append(m.group(vals[2]));
		DateTime justDate = DATE_FORMATTER.parseDateTime(sb.toString());
		String date_corrected_str = date_string.replaceAll("^.*?" + m.group(), DATE_STR_FORMATTER.print(justDate)+ " ");

		date_corrected_str = date_corrected_str.replaceAll("\\-([1-9])00$" , "\\-0" + "$1" + "00"); // for correcting time zone -500 --> 0500
		date_corrected_str = date_corrected_str.replace("AT" , "EST");
		date_corrected_str = date_corrected_str.replace("ET" , "EST");
		date_corrected_str = date_corrected_str.replace("PT" , "PST");
		date_corrected_str = date_corrected_str.replace("MT" , "MST");
		date_corrected_str = date_corrected_str.replace("CT" , "CST");
		date_corrected_str = date_corrected_str.replaceAll("\\s+at\\s+" , " ");
		date_corrected_str = date_corrected_str.replaceAll("(\\d+)\\s+\\-" , "$1\\-");
		date_corrected_str = date_corrected_str.trim();

		try {
			DateTime date_time = formatter.parseDateTime(date_corrected_str);
			return date_time;
		} catch (Exception exp) {
			logger.debug("Time is not valid " + date_string);
		}

		return justDate;
	}

	private DateTime findDate(String date_string){
		if (date_string == null || date_string.length() > 85 || date_string.split("\\s+").length > 9) return null;
		for (Map.Entry<Pattern, int[]> e : DATE_PATTERN_MAP.entrySet()) {
			Pattern p = e.getKey();
			Matcher m = p.matcher(date_string);
			if (m.find()) {
				return normalizeDateStr(date_string, m, e.getValue());
			}
		}
		return null;
	}

	private DateTime findDate(Document doc){
		String date_string = null;
		for (Map.Entry<Pattern, int[]> e : DATE_PATTERN_MAP.entrySet()) {
			Pattern p =e.getKey();
			Elements elems = doc.getElementsMatchingOwnText(p);
			if (elems == null || elems.size() == 0) continue;
			for (Element elem : elems) {
				date_string = elem.text();
				Matcher m = p.matcher(date_string);
				if (m.find()) {
					return normalizeDateStr(date_string, m, e.getValue());
				}
			}
		}
		return null;
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
}