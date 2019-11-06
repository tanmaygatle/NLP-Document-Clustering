package com.tanmay.pa_hw2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Run {

	public static void main(String[] args) throws Exception {
		
		Preprocessing preproc_obj = new Preprocessing();
		
		//Reading Documents
		BufferedReader br = new BufferedReader(new InputStreamReader(preproc_obj.getClass().getResourceAsStream("/data.txt")));
//		File file = new File("data.txt"); 
		Scanner sc = new Scanner(br); 	  
		String[][] document_text_matrix = new String[3][8];
		int i = 0, j = 0;
		String path = "";
		while (sc.hasNextLine()) { 
			path = sc.nextLine();
			for(j = 0; j < 8; j++) {
				document_text_matrix[i][j] = 
						preproc_obj.readFileAsString(path+"article0"+Integer.toString(j+1)+".txt")
										.replaceAll("\\s{2,}", " ");
			}
			i++;
		}
		sc.close();
		
		//Copying into another matrix for later use
		int k = 0;
		i = 0; j = 0;
		String[] document_matrix = new String[24];
		for(i = 0; i < 3; i++)
			for(j = 0; j < 8; j++) {
				document_matrix[k] = document_text_matrix[i][j];
				k++;
			}
		
		//Pre-processing
		i = 0;j=0;
		int tempval = 0;
		String temp = "";
		Map<String,Integer> ngram_map = new HashMap<String,Integer>();
		Map<String,Integer> temp_map = new HashMap<String,Integer>();
		for(i = 0; i < 3; i++)
			for(j = 0; j < 8; j++) {
				temp = preproc_obj.lemmatizer(document_text_matrix[i][j]);
				
				temp = preproc_obj.stopWordRemover(temp);
				document_text_matrix[i][j] = temp;
				
				
// Uncomment the below lines for NER tagging. Warning: The NER tagging takes a long time to run.
//				System.out.println(document_text_matrix[i][j]);				
//				temp = preproc_obj.nerTagger(document_text_matrix[i][j]);
//				document_text_matrix[i][j] = document_text_matrix[i][j] + " " + temp;
				

				temp_map = preproc_obj.ngram_creator(document_text_matrix[i][j], 2); //Put n = 3 for 3 grams
				for(String key: temp_map.keySet()) {
					if(ngram_map.get(key) == null)
						ngram_map.put(key, temp_map.get(key));
					else {
						tempval = ngram_map.get(key);
						ngram_map.replace(key, tempval, tempval + 1);
					}
				}
			}
		
		ArrayList<String> tempkeys = new ArrayList<String>();
		for(String key:ngram_map.keySet()) {
			if(ngram_map.get(key) < 3)
				tempkeys.add(key);
		}
		for(String key: tempkeys) {
			ngram_map.remove(key);
		}
		
		//Preparing Document Term Matrix
		TermDocument tfidf_obj = new TermDocument();
		
		ArrayList<HashMap<String,Integer>> docTermMatrix = new ArrayList<HashMap<String,Integer>>();
		ArrayList<HashMap<String,Double>> termFrequencyMatrix = new ArrayList<HashMap<String,Double>>();
		HashMap<String,Double> inverseDocFreqMatrix = new HashMap<String,Double>();
		ArrayList<HashMap<String,Double>> tfidfMatrix = new ArrayList<HashMap<String,Double>>();
		
		i = 0; j = 0;
		for(i = 0; i<3;i++)
			for(j = 0; j < 8; j++) {
				tfidf_obj.termList_generator(document_text_matrix[i][j]);
			}
		tfidf_obj.termList_generator(String.join(" ",ngram_map.keySet()));
		docTermMatrix = tfidf_obj.createDocumentTermMatrix(24);
		
		for(i =0; i<24;i++) {
			temp = preproc_obj.lemmatizer(document_matrix[i]);
			temp = preproc_obj.stopWordRemover(temp);
			document_matrix[i] = temp;
		}
		docTermMatrix = tfidf_obj.populateDocumentTermMatrix(docTermMatrix,document_matrix);
		termFrequencyMatrix = tfidf_obj.calculateTermFrequencyMatrix(docTermMatrix);
		inverseDocFreqMatrix = tfidf_obj.calculateInverseDocFreqMatrix(docTermMatrix);
		tfidfMatrix = tfidf_obj.calculateTFIDFMatrix(termFrequencyMatrix, inverseDocFreqMatrix);
		
		ArrayList<HashMap<String,Double>> combined_folder_vectors = new ArrayList<HashMap<String,Double>>();
		combined_folder_vectors = tfidf_obj.sumVectors(tfidfMatrix);
		combined_folder_vectors.set(0, tfidf_obj.sortByValue(combined_folder_vectors.get(0)));
		combined_folder_vectors.set(1, tfidf_obj.sortByValue(combined_folder_vectors.get(1)));
		combined_folder_vectors.set(2, tfidf_obj.sortByValue(combined_folder_vectors.get(2)));
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("topics.txt"));
		for(i = 0; i < 3; i++) {
			writer.write("Topics for folder "+ String.valueOf(i+1) + " are:\n");
			j = 0;
			for(String key:combined_folder_vectors.get(i).keySet()) {
				if(j == 10)
					break;
				writer.write(key+"\n");
				j++;
			}
			writer.write("\n---------------------------------\n");
		}
	    writer.close();	
	    
	    String[] cluster_labels = new String[3];
	    String[] temp_label = new String[3];
	    for(i = 0; i < cluster_labels.length; i++) {
	    	j = 0;
	    	for(String key:combined_folder_vectors.get(i).keySet()) {
				if(j == 3)
					break;
				temp_label[j] = key;
				j++;
			}
	    	cluster_labels[i] = String.join("/", temp_label);
	    }
	    
	    //Clustering
	    double[][] doc_vectors = new double[tfidfMatrix.size()][tfidfMatrix.get(0).size()];
	    ArrayList<String> terms = new ArrayList<String>();
	    for(String key:tfidfMatrix.get(0).keySet())
	    	terms.add(key);
	    
	    
	    for(i = 0; i < 24; i++) {
	    	for(j = 0; j < terms.size(); j++) {
	    		doc_vectors[i][j] = tfidfMatrix.get(i).get(terms.get(j));
	    	}
	    } 
	    
	    double[][] centroids = new double[3][doc_vectors[0].length];
        KMeansPP kmeans_pp = new KMeansPP(3, doc_vectors);
        centroids = kmeans_pp.getCentroids();

// Uncomment the below code for the perfect clustering. 
// It selects the 1st document from each cluster as the centroids.
        
//        int p = 0;
//        for(i = 0;i<3;i++) {
//        	for(j = 0;j<doc_vectors[0].length;j++) {
//        		centroids[i][j] = doc_vectors[p][j];
//        	}
//        	p += 8;
//        }
        
        KMeans kmeans = new KMeans(doc_vectors, centroids, "c", 10);
        int[][] final_clusters = kmeans.applyKMeans();
        
        Evaluation eval_obj = new Evaluation();
        int[][] original_clusters = 
        {{1,1,1,1,1,1,1,1,0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0,0, 0, 0, 0, 0, 0, 0,1,1,1,1,1,1,1,1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0,0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,1,1,1,1,1,1,1,1}};
        eval_obj.buildConfusionMatrix(final_clusters, original_clusters, cluster_labels);
        String[] final_cluster_labels = eval_obj.getFinalClusterLabels();
        
        PCA_Solver pca_solver = new PCA_Solver();
        double[][] pca_matrix = pca_solver.applyPCA(doc_vectors);      
        
        new Visualisation("OriginalClusters.png", "Original Clusters Visualisation", 
        		cluster_labels, original_clusters, pca_matrix);
        new Visualisation("KMeansPPClusters.png", "Final Clusters Visualisation", 
        		final_cluster_labels, final_clusters, pca_matrix);
	}
}