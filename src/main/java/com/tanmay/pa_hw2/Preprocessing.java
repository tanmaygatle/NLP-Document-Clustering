package com.tanmay.pa_hw2;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import edu.stanford.nlp.simple.*;
import java.util.regex.Pattern;

public class Preprocessing {
	
	ArrayList<String> stopwords = new ArrayList<String>();
	
	public Preprocessing() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/google_stop_words.txt")));
//		File file = new File("/google_stop_words.txt"); 
		Scanner sc = new Scanner(br);
		while(sc.hasNextLine()) {
			stopwords.add(sc.nextLine().trim());
		}
		sc.close();
	}
	
	public String readFileAsString(String fileName)throws Exception 
	  { 
	    String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get(fileName))); 
	    return data; 
	  }
	
	public String stopWordRemover(String string) {
		Document doc = new Document(string);
		ArrayList<String> result = new ArrayList<String>();
		for(Sentence sent:doc.sentences()) {
			for(String word: sent.words()) {
				if(!Pattern.matches("[\\p{Punct}\\s\\d]+", word))
					if(!stopwords.contains(word))
						result.add(word.trim());
			}	
		}
		return String.join(" ", result);
	}
	
	public String tokenizer(String string) {
		Document doc = new Document(string);
		ArrayList<String> result = new ArrayList<String>();
		for(Sentence sent:doc.sentences()) {
			for(String word: sent.words()) {
				if(!Pattern.matches("[\\p{Punct}\\s\\d]+", word))
					result.add(word.trim());
			}	
		}
		return String.join(" ", result);
	}
	
	public String lemmatizer(String string) {
		Document doc = new Document(string);
		ArrayList<String> result = new ArrayList<String>();
		for(Sentence sent:doc.sentences()) {
			for(String word: sent.lemmas()) {
				if(!Pattern.matches("[\\p{Punct}\\s\\d]+", word))
					result.add(word.trim());
			}	
		}
		return String.join(" ", result);
	}
	
	public String nerTagger(String string) {
		Document doc = new Document(string);		
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> nerTags = new ArrayList<String>();
		ArrayList<String> temp = new ArrayList<String>();
		int i = 0, j = 0;
		for(Sentence sent:doc.sentences()) {
			words.addAll(sent.words());
			nerTags.addAll(sent.nerTags());
			while(i<words.size()){
				if(!nerTags.get(i).equals("O")) {
					temp.clear();
					temp.add(words.get(i).trim());
					for(j =i+1; j < words.size(); j++) {
						if(nerTags.get(j).equals(nerTags.get(i)))
							temp.add(words.get(j).trim());
						else
							break;
						
					}
					i=j;
					result.add(String.join("_", temp));
				}
				i=i+1;
			}
		}
		return String.join(" ", result);
	}
	
	public static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? "_" : "") + words[i]);
        return sb.toString();
    }
	
	public Map<String,Integer> ngram_creator(String string, int n) {
        String[] words = string.split(" ");
		Map<String,Integer> map = new HashMap<String,Integer>();
		String temp = "";
		int i = 0, newval = 0, oldval = 0;
		for(i = 0; i < words.length - n + 1; i++) {
			temp = concat(words,i,i+n);
			if(map.get(temp) == null) {
				map.put(temp, 1);
			}
			else {
				oldval = map.get(temp);
				newval = oldval + 1;
				map.replace(temp, oldval, newval);
			}
		}
		return map;
	}
}