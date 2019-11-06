package com.tanmay.pa_hw2;
import java.util.ArrayList;

public class KMeans {
	
	double[][] doc_vectors;
	double[][] centroids;
	String distance_type;
	int no_of_iterations;
	ArrayList<ArrayList<Integer>> prev_cluster_list = new ArrayList<ArrayList<Integer>>();
	ArrayList<ArrayList<Integer>> cluster_list = new ArrayList<ArrayList<Integer>>();
	
	public KMeans(double[][] doc_vectors, double[][] centroids, String distance_type, int no_of_iterations) {
		this.doc_vectors = doc_vectors;
		this.centroids = centroids;
		this.distance_type = distance_type;
		this.no_of_iterations = no_of_iterations;
		for(int i = 0; i < centroids.length; i++) {
			cluster_list.add(new ArrayList<Integer>(0));
			prev_cluster_list.add(new ArrayList<Integer>(0));
		}
	}
	
	private boolean areClustersSame() {
		if(cluster_list.size() != prev_cluster_list.size())
			return false;
		for(int i = 0; i < cluster_list.size(); i++) {
			for(int j = 0; j < cluster_list.get(i).size(); j++) {
				if(!prev_cluster_list.get(i).contains(cluster_list.get(i).get(j)))
					return false;
			}
		}
		return true;
	}
	
	private void storePrevClusterList() {
		for(int i = 0; i < cluster_list.size(); i++) {
			prev_cluster_list.get(i).clear();
			for(int j = 0; j < cluster_list.get(i).size(); j++) {
				prev_cluster_list.get(i).add(cluster_list.get(i).get(j));
			}
		}
	}
	
	private void updateCentroids() {
		for(int i = 0; i < centroids.length; i ++) {
			for(int j = 0; j < centroids[0].length; j++) {
				centroids[i][j] = 0.0;
			}
		}
		
		for(int i = 0; i < cluster_list.size(); i++) {
			for(int j = 0; j < cluster_list.get(i).size(); j++) {
				for(int z = 0; z < doc_vectors[0].length;z++) {
					centroids[i][z] = centroids[i][z] + doc_vectors[cluster_list.get(i).get(j)][z]; 
				}
			}
			for(int z = 0; z < doc_vectors[0].length;z++) {
				centroids[i][z] = centroids[i][z] / cluster_list.get(i).size(); 
			}
		}
	}
	
	public int[][] applyKMeans() {
		double min,max,distance;int assigned_cluster = 0;
		int iteration_no = 0;
		Similarity sim = new Similarity(distance_type);
		System.out.println("Applying KMeans:\n");
		
		do {
			System.out.println("Iteration "+ (iteration_no+1));
			if(iteration_no != 0)
				storePrevClusterList();
			
			for(int i = 0; i < cluster_list.size(); i++) {
				cluster_list.get(i).clear();
			}
			
			for(int doc_no = 0; doc_no < doc_vectors.length; doc_no++) {
				min = Double.MAX_VALUE;
				max = Double.MIN_VALUE;
				for(int centroid_no = 0; centroid_no < centroids.length; centroid_no++) {
					distance = sim.computeDistance(centroids[centroid_no], doc_vectors[doc_no]);
					if(distance_type == "c") {
						if(distance > max) {
							max = distance;
							assigned_cluster = centroid_no;
						}
					}
					else if(distance_type == "e") {
						if(distance < min) {
							min = distance;
							assigned_cluster = centroid_no;
						}
					}
				}
				cluster_list.get(assigned_cluster).add(doc_no);
			}
			
			for(int i = 0; i < cluster_list.size(); i++) {
				System.out.print("Cluster "+(i+1)+" : ");
				for(int j = 0; j < cluster_list.get(i).size(); j++) {
					System.out.print("Doc["+ cluster_list.get(i).get(j)+"] ");
				}
				System.out.println();
			}
			
			if(areClustersSame())
				break;
			
			updateCentroids();
			iteration_no++;
			
		}while(iteration_no < no_of_iterations);
		
		int[][] final_clusters = new int[cluster_list.size()][doc_vectors.length];
		for(int i = 0; i < cluster_list.size(); i++) {
			for(int j = 0; j < doc_vectors.length; j++) {
				if(cluster_list.get(i).contains(j))
					final_clusters[i][j] = 1;
				else
					final_clusters[i][j] = 0;
			}
		}
		return final_clusters;
	}
}
