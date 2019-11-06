package com.tanmay.pa_hw2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.*;

public class TermDocument {
	ArrayList<String> termList = new ArrayList<String>();
	
	public void termList_generator(String string) {
		termList.addAll(Arrays.asList(string.split(" ")));
		removeDuplicatesfromtermList();
	}
	
	public void removeDuplicatesfromtermList() {
		Set<String> set = new LinkedHashSet<String>();
		set.addAll(termList);
		termList.clear();
		termList.addAll(set);
	}
	
	public HashMap<String,Integer> createTermHashMap() {
		HashMap<String,Integer> temp = new HashMap<String,Integer>();
		for(String term: termList) {
			temp.put(term,0);
		}
		return temp;
	}
	
	public HashMap<String,Double> createTermHashMapDouble() {
		HashMap<String,Double> temp = new HashMap<String, Double>();
		for(String term: termList) {
			temp.put(term,0.0);
		}
		return temp;
	}
	
	public ArrayList<HashMap<String,Integer>> createDocumentTermMatrix(int n) { 
		ArrayList<HashMap<String,Integer>> documentTermMatrix = new ArrayList<HashMap<String,Integer>>();
		for(int i = 0; i<n; i++) 
			documentTermMatrix.add(createTermHashMap());
		return documentTermMatrix;
	}
	
	public ArrayList<HashMap<String,Integer>> populateDocumentTermMatrix(ArrayList<HashMap<String,Integer>> documentTermMatrix, String[] docs) {
		String[] term2 = new String[2];
		ArrayList<String> arr = new ArrayList<String>();
		int i = 0,j = 0, temp = 0, count = 0;
		for(i = 0; i < docs.length; i++) {
			arr.clear();
			arr.addAll(Arrays.asList(docs[i].split(" ")));
			for(String term: termList) {
				if(term.contains("_") ) {
					term2 = term.split("_");
					count = 0;
					for(j = 0; j < arr.size() - 1; j++) 
						if(arr.get(j).equals(term2[0]) && arr.get(j+1).equals(term2[1]))
							count++;
					temp = documentTermMatrix.get(i).get(term);
					documentTermMatrix.get(i).replace(term, temp+count);
				}
				else {
					count = 0;
					for(j = 0; j < arr.size(); j++) 
						if(arr.get(j).equals(term))
							count++;
					temp = documentTermMatrix.get(i).get(term);
					documentTermMatrix.get(i).replace(term, temp+count);
				}
			}
		}	
		return documentTermMatrix;
	}
	
	public ArrayList<HashMap<String,Double>> calculateTermFrequencyMatrix (ArrayList<HashMap<String,Integer>> documentTermMatrix) { 
		ArrayList<HashMap<String,Double>> termFrequencyMatrix = new ArrayList<HashMap<String,Double>>();
		int i = 0;
		double val = 0, total = 0, temp = 0;
		for(i = 0; i<documentTermMatrix.size(); i++) 
			termFrequencyMatrix.add(createTermHashMapDouble());
		for(i = 0; i < documentTermMatrix.size(); i++) {
			total = 0;
			for(String term: termList) {
				total += documentTermMatrix.get(i).get(term);
			}
			for(String term:termList) {
				temp = documentTermMatrix.get(i).get(term);
				val = temp / total;
				termFrequencyMatrix.get(i).replace(term, val);
			}
		}
		return termFrequencyMatrix;
	}
	
	public HashMap<String,Double> calculateInverseDocFreqMatrix (ArrayList<HashMap<String,Integer>> documentTermMatrix) { 
		HashMap<String,Double> inverseDocFreqMatrix = new HashMap<String,Double>();
		
		int i = 0, j = 0, total = documentTermMatrix.size();
		int[] termdoccount = new int[termList.size()];
		for(i = 0; i < termList.size(); i++)
			termdoccount[i] = 0;
		double val = 0;
		
		for(i = 0; i < termList.size(); i++) {
			for(j = 0; j < documentTermMatrix.size(); j++) {
				if(documentTermMatrix.get(j).get(termList.get(i)) > 0)
					termdoccount[i]++;
			}
		}
		
		
		for(String term:termList) {
			val = Math.log10(total/termdoccount[termList.indexOf(term)]);
			inverseDocFreqMatrix.put(term, val);
		}
		
		return inverseDocFreqMatrix;
	}
	
	public ArrayList<HashMap<String,Double>> calculateTFIDFMatrix (ArrayList<HashMap<String,Double>> termFrequencyMatrix, HashMap<String,Double> inverseDocFreqMatrix) {
		ArrayList<HashMap<String,Double>> tfidfMatrix = new ArrayList<HashMap<String,Double>>();
		int i = 0;
		double val = 0;
		
		for(i = 0; i<termFrequencyMatrix.size(); i++) 
			tfidfMatrix.add(createTermHashMapDouble());
		
		for(i = 0; i < termFrequencyMatrix.size(); i++) {
			for(String term: termList) {
				val = termFrequencyMatrix.get(i).get(term) * inverseDocFreqMatrix.get(term);
				tfidfMatrix.get(i).replace(term, val);
			}
		}
		return tfidfMatrix;
	}
	
	public ArrayList<HashMap<String,Double>> sumVectors(ArrayList<HashMap<String,Double>> tfidfMatrix) {
		ArrayList<HashMap<String,Double>> combinedVectors = new ArrayList<HashMap<String,Double>>();
		int i = 0;
		double val = 0;
		
		for(i = 0; i<3; i++) 
			combinedVectors.add(createTermHashMapDouble());
		
		for(String term: termList) {
			val = 0;
			for(i = 0; i < 8; i++) 
				val += tfidfMatrix.get(i).get(term);
			combinedVectors.get(0).put(term,val);
		}
		for(String term: termList) {
			val = 0;
			for(i = 8; i < 16; i++) 
				val += tfidfMatrix.get(i).get(term);
			combinedVectors.get(1).put(term,val);
		}
		for(String term: termList) {
			val = 0;
			for(i = 16; i < 24; i++) 
				val += tfidfMatrix.get(i).get(term);
			combinedVectors.get(2).put(term,val);
		}
		return combinedVectors;
	}
	
	public HashMap<String, Double> sortByValue(HashMap<String, Double> hm) 
    { 
		HashMap<String, Double> sorted = new HashMap<String,Double>();
		sorted = hm
		        .entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
		        .collect(
		            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		return sorted;
    } 
}