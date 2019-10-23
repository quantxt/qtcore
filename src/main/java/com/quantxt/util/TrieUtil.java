package com.quantxt.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.quantxt.doc.QTDocument.DOCTYPE;
import com.quantxt.trie.Trie;

public class TrieUtil {

    public static Trie buildVerbTree(final byte[] verbArr, boolean isSimple, Function<String,
            List<String>> tokenize) throws IOException {
        JsonParser parser = new JsonParser();
        Trie.TrieBuilder verbs = isSimple ? Trie.builder():
                Trie.builder().onlyWholeWords().ignoreCase();
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
                case "Development" : verbTybe = DOCTYPE.Development;
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
                verbs.addKeyword(verb, verbTybe);
    //            List<String> tokens = tokenize.apply(verb);
    //            verbs.addKeyword(String.join(" ", tokens), verbTybe);
            }
        }
        return verbs.build();
    }

}
