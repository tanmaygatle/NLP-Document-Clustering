package com.tanmay.pa_hw2;
import java.util.ArrayList;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Evaluation {
	int[][] original_cluster_count_in_final_clusters;
	String[] final_cluster_labels;
	
	private void storeOriginalClusterCountInFinalClusters(int[][] final_clusters, ArrayList<ArrayList<Integer>> original_cluster_indices) {
		original_cluster_count_in_final_clusters = new int[final_clusters.length][original_cluster_indices.size()];
		
		int count;
		for(int j = 0; j < original_cluster_indices.size(); j++) {
			for(int i = 0; i < final_clusters.length; i++) {
				count = 0;
				for(int k = 0; k < original_cluster_indices.get(j).size(); k++) {
					if(final_clusters[i][original_cluster_indices.get(j).get(k)] == 1) {
						count++;
					}
				}
				original_cluster_count_in_final_clusters[i][j] = count;
			}
		}
	}
	
	public String[] getFinalClusterLabels() {
		return final_cluster_labels;
	}
	
	private void storeFinalClusterLabels(String[] cluster_labels) {
		int max = Integer.MIN_VALUE; int max_i = -1;
		final_cluster_labels = new String[original_cluster_count_in_final_clusters.length];
		
		for(int i = 0; i < original_cluster_count_in_final_clusters.length; i++) {
			max = Integer.MIN_VALUE;
			max_i = -1;
			for(int j = 0; j < original_cluster_count_in_final_clusters[i].length; j++) {
				if(original_cluster_count_in_final_clusters[i][j] > max) {
					max = original_cluster_count_in_final_clusters[i][j];
					max_i = j;
				}
			}
			final_cluster_labels[i] = cluster_labels[max_i];
		}
	}
	
	private void printConfusionMatrix(String[] cluster_labels) {
		System.out.println("\nConfusion Matrix:\n");
		
		System.out.format("%32s", "");
		for(int i = 0; i < original_cluster_count_in_final_clusters.length; i++)
			System.out.format("%15s", "Cluster " + (i+1) + " ");
		System.out.println();
		
		for(int i = 0; i < original_cluster_count_in_final_clusters[0].length; i++) {
			System.out.format("%32s", cluster_labels[i]);
			for(int j = 0; j < original_cluster_count_in_final_clusters.length; j++) {
				System.out.format("%15d", original_cluster_count_in_final_clusters[j][i]);
			}
			System.out.println();
		}
	}
	
	private void calculateEvaluationMetrics(int[][] final_clusters) {
		int N = final_clusters[0].length;
		int total_pairs_N = (N*(N-1))/2;
		int TPplusFP = 0; int TP = 0; int FP = 0;
		int TNplusFN = 0; int TN = 0; int FN = 0;
		int count = 0; int temp = 0;
		
		for(int i = 0; i < final_clusters.length; i++) {
			count = 0;
			for(int j = 0; j < final_clusters[0].length; j++)
				if(final_clusters[i][j] == 1)
					count++;
			TPplusFP += CombinatoricsUtils.binomialCoefficient(count, 2);
		}
		
		for(int i = 0; i < original_cluster_count_in_final_clusters.length; i++)
			for(int j = 0; j < original_cluster_count_in_final_clusters[0].length; j++)
					if(original_cluster_count_in_final_clusters[i][j] >= 2)
						TP += CombinatoricsUtils.binomialCoefficient(original_cluster_count_in_final_clusters[i][j], 2);
		
		FP = TPplusFP - TP;
		
		TNplusFN = total_pairs_N - TPplusFP;
		
		for(int j = 0; j < original_cluster_count_in_final_clusters[0].length; j++)
			for(int i = 0; i < original_cluster_count_in_final_clusters.length-1; i++) {
				count = 0;
				temp = original_cluster_count_in_final_clusters[i][j];
				
				for(int k = i+1; k < original_cluster_count_in_final_clusters.length; k++)
					count += original_cluster_count_in_final_clusters[k][j];
				
				FN += temp * count;
			}
		
		TN = TNplusFN - FN;
		
		double precision = ((double)TP)/((double) TPplusFP);
		double recall = ((double)TP)/((double)TP + (double)FN);
		double fmeasure = (2*precision*recall)/(precision+recall);
		
		System.out.println("\nCombined Confusion Matrix:\n");
		System.out.format("%20s", "");
		System.out.format("%20s", "Same Cluster");
		System.out.format("%20s", "Different Clusters");
		System.out.println();
		System.out.format("%20s", "Same Class");
		System.out.format("%20s", "TP = "+ TP);
		System.out.format("%20s", "FN = "+ FN);
		System.out.println();
		System.out.format("%20s", "Different Classes");
		System.out.format("%20s", "FP = "+ FP);
		System.out.format("%20s", "TN = "+ TN);
		System.out.println();
		
		System.out.println("\n\nPrecision = " + precision);
		System.out.println("Recall = " + recall);
		System.out.println("FMeasure = " + fmeasure);
	}

	public void buildConfusionMatrix(int[][] final_clusters, int[][] original_clusters, String[] cluster_labels) {
		if(final_clusters.length != original_clusters.length)
			return;
		
		ArrayList<ArrayList<Integer>> original_cluster_indices = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < final_clusters.length; i++) {
			original_cluster_indices.add(new ArrayList<Integer>());
			for(int j = 0; j < final_clusters[0].length; j++) {
				if(original_clusters[i][j] == 1) {
					original_cluster_indices.get(i).add(j);
				}
			}
		}
		
		storeOriginalClusterCountInFinalClusters(final_clusters, original_cluster_indices);
		storeFinalClusterLabels(cluster_labels);
		printConfusionMatrix(cluster_labels);
		calculateEvaluationMetrics(final_clusters);
	}
	
}
