package com.quantxt.doc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.quantxt.helper.types.ExtIntervalSimple;
import com.quantxt.helper.types.QTField;
import com.quantxt.interval.Interval;
import lombok.Getter;
import lombok.Setter;
import lombok.var;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quantxt.helper.types.ExtInterval;
import com.quantxt.trie.Emit;
import com.quantxt.types.NamedEntity;

import static com.quantxt.helper.types.QTField.QTFieldType.*;
import static com.quantxt.types.Entity.NER_TYPE;

@Getter
@Setter
public abstract class QTDocument {

	public enum Language
	{
		ENGLISH, SPANISH, GERMAN, FRENCH, ARABIC, RUSSIAN, FARSI, JAPANESE, PORTUGUESE
	}

	final private static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	final private static Pattern KEY_Thrillings = Pattern.compile("^[\\s\"'\\(\\)\\-\\:;%]+$");

	public enum DOCTYPE {Headline, Action, Statement, Aux, Speculation,
		Legal, Acquisition, Production, Partnership, Employment, Development
	}

	private static Gson gson = new Gson();
	private static Logger logger = LoggerFactory.getLogger(QTDocument.class);

	protected String title;
	protected String englishTitle;
	protected String body;
	protected Language language;
	protected transient List<String> sentences;
	private DOCTYPE docType;

	private DateTime date;
	private String link;
	private double score;
	private int position;
	private String source;
	private String id;

	protected transient QTDocumentHelper helper;

	//TODO: all these should be combined into a generic class
	private Set<String> tags = new HashSet<>();
	private Map<String, LinkedHashSet<String>> entity;
	private ArrayList<String> verbs;
	private ArrayList<String> nouns;
	private ArrayList<ExtInterval> values;

	public QTDocument(String b, String t, QTDocumentHelper helper){
		title = t;
		body  = b;
		this.helper = helper;
	}

	// Getters

	public synchronized String getDateStr() {
		return dateFormat.format(date);
	}

	abstract List<QTDocument> getChilds(boolean splitOnNewLine);

	abstract double[] getVectorizedTitle(QTExtract extract);

	abstract String Translate(String text, Language inLang, Language outLang);

	abstract boolean isStatement(String s);


	private int getNextValidIndex(final Pattern pattern, String str,
								  int lookaheadStart, int lookupEnd)
	{
		int subLen = str.length() - lookaheadStart;
		if (subLen < 0) return -1;
		String lookupString = lookupEnd > 0 ? str.substring(lookaheadStart, lookupEnd):
				str.substring(lookaheadStart);
		lookupString = lookupString.replaceAll("(\\p{Sc})", "");
		lookupString = lookupString.replaceAll("(\\([^\\)]{1,25}\\))", "");
		if (lookupString.length() == 0) return 0;
		Matcher m = pattern.matcher(lookupString);
		if (!m.find()) return -1;
		//shift keyend to end of the pad
		return m.end();
	}

	public void extractKeyValues(QTExtract speaker,
								 String context,
								 int dist) {
		// TODO: hacky quick check
		// If there are no numbers then don't bother
        QTField.QTFieldType valueType = speaker.getType();

		if (valueType == null || valueType == DOUBLE || valueType == INT || valueType == SHORT) {
			if (!(title.contains("0") || title.contains("1") || title.contains("2") || title.contains("3") ||
					title.contains("4") || title.contains("5") || title.contains("6") || title.contains("7") ||
					title.contains("8") || title.contains("9"))) {
				return;
			}
		}

		final String rawSent_curr = title;

		Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(rawSent_curr);
		if (name_match_curr.size() == 0) return;
		ArrayList<ExtIntervalSimple> values = new ArrayList<>();
		// get potential values


        if (valueType == null || valueType == DOUBLE || valueType == INT || valueType == SHORT) {
			helper.getValues(rawSent_curr, context, values);
		} else {
			if (valueType == DATETIME){
				helper.getDatetimeValues(rawSent_curr, context, values);
			} else if (valueType == STRING || valueType == KEYWORD){
			    Pattern regex = speaker.getPattern();
			    int [] groups = speaker.getGroups();
				helper.getPatternValues(rawSent_curr, context, regex, groups, values);
			}
		}
		if (values.size() == 0) return;

		/*
		for (ExtIntervalSimple v : values){
			logger.debug("--- \t" + v.toString(rawSent_curr));
		}
		*/

		//ignore duplicate rows

		for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
			String keyGroup = entType.getKey();
			for (Emit matchedName : entType.getValue()) {
				NamedEntity ne = (NamedEntity) matchedName.getCustomeData();
				String key = ne.getName();
				int keyEnd = matchedName.getEnd();
				if (keyEnd < 0) {
					logger.error("key wrong ---- {} ----- in '{}'", matchedName.getKeyword(), title);
					continue;
				}

				logger.debug(" ---- KEY: '" + key + "' === " + matchedName.getKeyword());
				ArrayList<ExtIntervalSimple> rowValues = new ArrayList<>();
				for (int v = 0; v < values.size(); v++) {
					ExtIntervalSimple intv = values.get(v);
					int valStart = intv.getStart();
					int diff = (valStart - keyEnd);
					if (diff < 0) continue;
					if (diff > dist) break;

					logger.debug("\t value in {} is ----------------- {} --> '{}'", rawSent_curr, key, intv.toString(rawSent_curr));

					int lookupStart = keyEnd;  // end of the last index we scanned
					int lookupEnd = valStart;
					int shift = getNextValidIndex(KEY_Thrillings, rawSent_curr, lookupStart, lookupEnd);
					if (shift < 0) break;
					keyEnd = intv.getEnd();
					if (shift > 0) {
						rowValues.add(intv);
					}
				}

				if (rowValues.size() > 0) {
					ExtInterval extInterval = new ExtInterval();
					extInterval.setKeyGroup(keyGroup);
					extInterval.setKey(key);
					extInterval.setExtIntervalSimples(rowValues);
					if (this.values == null) this.values = new ArrayList<>();
					this.values.add(extInterval);
				}
			}
		}
	}

	public void convertValues2titleTable()
	{
		if (this.values == null)  return;

		Collections.sort(this.values, new Comparator<ExtInterval>(){
			public int compare(ExtInterval p1, ExtInterval p2){
				Integer s1 = p1.getExtIntervalSimples().get(0).getStart();
				Integer s2 = p2.getExtIntervalSimples().get(0).getStart();
				return s1.compareTo(s2);
			}
		});

		LinkedHashSet<String> rows = new LinkedHashSet<>();
		for (ExtInterval ext : this.values) {

			StringBuilder sb = new StringBuilder();
			sb.append("<tr>");
			sb.append("<td>").append(ext.getKey()).append("</td>");
			for (ExtIntervalSimple extvStr : ext.getExtIntervalSimples()) {
				sb.append("<td>").append(extvStr.getCustomData().toString()).append("</td>");
			}
			sb.append("</tr>");
			String row2add = sb.toString();
			rows.add(row2add);
		}

		if (!rows.isEmpty()) {
			if (!title.startsWith("<table ")){
				title = "";
			}
			title += "<table width=\"100%\">" + String.join("", rows) + "</table>";
		} else {
			if (!title.startsWith("<table ")){
				title = "";
			}
		}
	}

	public ArrayList<QTDocument> extractEntityMentions(QTExtract speaker,
													   boolean onlyIncludeUttsWithEntities,
													   boolean extractNounAndVebPhrases,
													   boolean splitOnNewLine) {
		ArrayList<QTDocument> quotes = new ArrayList<>();
		List<QTDocument> childs =  getChilds(splitOnNewLine);

		int numSent = childs.size();

		for (int i = 0; i < numSent; i++)
		{
			QTDocument workingChild = childs.get(i);
			final String rawSent_curr = workingChild.getTitle();
			String rawSent_before = "";

			if (i > 0){
				rawSent_before = childs.get(i - 1).getTitle();
				workingChild.setBody(rawSent_before + " " + rawSent_curr);
			} else {
				workingChild.setBody(rawSent_curr);
			}

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
				Map<String, Collection<Emit>> name_match_curr = speaker.parseNames(rawSent_curr);
				if (name_match_curr.size() == 0 && i > 0) {
					List<String> tokens_b = helper.tokenize(rawSent_before);
					if (helper.isSentence(rawSent_before, tokens_b)) {

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
				}
				if (name_match_curr.size() > 0) {
					for (Map.Entry<String, Collection<Emit>> entType : name_match_curr.entrySet()) {
						for (Emit matchedName : entType.getValue()) {
							NamedEntity ne = (NamedEntity) matchedName.getCustomeData();
							workingChild.addEntity(entType.getKey(), ne.getName());
							//			logger.info("\t" + entType.getKey() + " | " + ne.getName());
						}
					}
				}
				if (name_match_curr.size() == 0 && onlyIncludeUttsWithEntities) continue;
			}

			if (extractNounAndVebPhrases) {
				List<ExtIntervalSimple> nounsAndVerbs = helper.getNounAndVerbPhrases(rawSent_curr, parts);
				for (ExtIntervalSimple ext : nounsAndVerbs) {
					switch (ext.getType()) {
						case VERB:
							String verb = rawSent_curr.substring(ext.getStart(), ext.getEnd());
							DOCTYPE verbType = helper.getVerbType(verb);
							if (verbType != null) {
								workingChild.setDocType(verbType);
							}
							break;
						case NOUN:
							String noun = rawSent_curr.substring(ext.getStart(), ext.getEnd());
							workingChild.addEntity(NER_TYPE, noun);
					}
				}
			}

			if (onlyIncludeUttsWithEntities){
				if (workingChild.getEntity() != null && workingChild.getEntity().size() != 0) {
					quotes.add(workingChild);
				}
			} else {
				quotes.add(workingChild);
			}

		}

		return quotes;
	}

	public void addTags (List<String> taglist){
		tags.addAll(taglist);
	}

	public void addTag (String tag){
		tags.add(tag);
	}

	/*
	public static void resetTranslatCred(){
		Translate.setClientId("2b70575a-116a-40db-9cc8-4b5192659506");
		Translate.setClientSecret("g184U2+B7fwftaoGzBDyb59KzYEKtulZZQZsnW71wj4=");
	}
	*/

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

	public String toString(){
		return gson.toJson(this);
	}

}