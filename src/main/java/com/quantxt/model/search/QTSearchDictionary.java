package com.quantxt.model.search;

import com.quantxt.doc.QTDocument;
import com.quantxt.model.DictSearch;
import com.quantxt.model.Dictionary;
import com.quantxt.model.QTField;

import java.util.List;

import static com.quantxt.model.DictSearch.AnalyzType.SIMPLE;
import static com.quantxt.model.DictSearch.Mode.ORDERED_SPAN;

public class QTSearchDictionary {

    protected String vocabId;
    protected String vocabName;

    protected Dictionary.ExtractionType vocabValueType;
    protected QTField.DataType dataType;

    protected QTDocument.Language language = QTDocument.Language.ENGLISH;

    protected List<String> stopwordList;
    protected List<String> synonymList;

    protected DictSearch.Mode searchMode = ORDERED_SPAN;

    protected DictSearch.AnalyzType analyzeStrategy = SIMPLE;

    protected String skipPatternBetweenKeyAndValue;
    protected String skipPatternBetweenValues;

    protected String phraseMatchingPattern;
    protected String [] phraseMatchingGroups;

    public QTSearchDictionary(){
    }

    public QTSearchDictionary(String vocabId,
                              String vocabName,
                              Dictionary.ExtractionType vocabValueType,
                              QTField.DataType dataType,
                              QTDocument.Language language,
                              List<String> stopwordList,
                              List<String> synonymList,
                              DictSearch.Mode searchMode,
                              DictSearch.AnalyzType analyzeStrategy,
                              String skipPatternBetweenKeyAndValue,
                              String skipPatternBetweenValues,
                              String phraseMatchingPattern,
                              String [] phraseMatchingGroups)
    {
        this.vocabId = vocabId;
        this.vocabName = vocabName;
        this.vocabValueType = vocabValueType;
        this.dataType = dataType;
        this.language = language;
        this.stopwordList = stopwordList;
        this.synonymList = synonymList;
        this.searchMode = searchMode;
        this.analyzeStrategy= analyzeStrategy;
        this.skipPatternBetweenKeyAndValue = skipPatternBetweenKeyAndValue;
        this.skipPatternBetweenValues = skipPatternBetweenValues;
        this.phraseMatchingPattern = phraseMatchingPattern;
        this.phraseMatchingGroups = phraseMatchingGroups;
    }

    public QTDocument.Language getLanguage() {
        return language;
    }

    public List<String> getStopwordList() {
        return stopwordList;
    }

    public DictSearch.AnalyzType getAnalyzeStrategy() {
        return analyzeStrategy;
    }

    public Dictionary.ExtractionType  getVocabValueType() {
        return vocabValueType;
    }

    public List<String> getSynonymList() {
        return synonymList;
    }

    public String getVocabName() {
        return vocabName;
    }

    public DictSearch.Mode getSearchMode() {
        return searchMode;
    }

    public String getVocabId() {
        return vocabId;
    }

    public String getPhraseMatchingPattern() {
        return phraseMatchingPattern;
    }

    public String getSkipPatternBetweenKeyAndValue() {
        return skipPatternBetweenKeyAndValue;
    }

    public String getSkipPatternBetweenValues() {
        return skipPatternBetweenValues;
    }

    public String[] getPhraseMatchingGroups() {
        return phraseMatchingGroups;
    }

    public QTField.DataType getDataType() {
        return dataType;
    }

    public QTSearchDictionary setVocabName(String vocabName) {
        this.vocabName = vocabName;
        return this;
    }

    public QTSearchDictionary setStopwordList(List<String> stopwordList) {
        this.stopwordList = stopwordList;
        return this;
    }

    public QTSearchDictionary setAnalyzeStrategy(DictSearch.AnalyzType analyzeStrategy) {
        this.analyzeStrategy = analyzeStrategy;
        return this;
    }

    public QTSearchDictionary setVocabId(String vocabId) {
        this.vocabId = vocabId;
        return this;
    }

    public QTSearchDictionary setSearchMode(DictSearch.Mode searchMode) {
        this.searchMode = searchMode;
        return this;
    }

    public QTSearchDictionary setPhraseMatchingPattern(String phraseMatchingPattern) {
        this.phraseMatchingPattern = phraseMatchingPattern;
        return this;
    }

    public QTSearchDictionary setVocabValueType(Dictionary.ExtractionType  vocabValueType) {
        this.vocabValueType = vocabValueType;
        return this;
    }

    public QTSearchDictionary setSkipPatternBetweenKeyAndValue(String skipPatternBetweenKeyAndValue) {
        this.skipPatternBetweenKeyAndValue = skipPatternBetweenKeyAndValue;
        return this;
    }

    public QTSearchDictionary setSkipPatternBetweenValues(String skipPatternBetweenValues) {
        this.skipPatternBetweenValues = skipPatternBetweenValues;
        return this;
    }

    public QTSearchDictionary setSynonymList(List<String> synonymList) {
        this.synonymList = synonymList;
        return this;
    }

    public QTSearchDictionary setPhraseMatchingGroups(String[] phraseMatchingGroups) {
        this.phraseMatchingGroups = phraseMatchingGroups;
        return this;
    }

    public QTSearchDictionary setLanguage(QTDocument.Language language) {
        this.language = language;
        return this;
    }

    public QTSearchDictionary setDataType(QTField.DataType dataType) {
        this.dataType = dataType;
        return this;
    }
}
