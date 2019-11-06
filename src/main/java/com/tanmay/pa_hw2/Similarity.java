package com.tanmay.pa_hw2;
public class Similarity {
	String distance_type = "c";
	
	public Similarity(String distance_type) {
		this.distance_type = distance_type;
	}
	
	public double computeDistance(double[] x, double[] y) {
		double distance = 0.0;
		if(distance_type == "c")
			distance = computeCosineSimilarity(x,y);
		else if(distance_type == "e")
			distance = computeEuclideanDistance(x,y);
		return distance;
	}
	
	private double computeCosineSimilarity(double[] x, double[] y) {
		double cosine_similarity = 0.0;
		double dot_product = 0.0;
		double norm_x = 0; double norm_y = 0;
		for(int i = 0; i < x.length; i++) {
			dot_product += x[i] * y[i];
			norm_x += x[i] * x[i];
			norm_y += y[i] * y[i];
		}
		if(norm_x == 0 || norm_y == 0)
			cosine_similarity = 0;
		else {
			cosine_similarity = dot_product / (Math.sqrt(norm_x) * Math.sqrt(norm_y));
		}
		return cosine_similarity;
	}
	
	private double computeEuclideanDistance(double[] x, double[] y) {
		double sum_of_squares = 0;
		for(int i = 0; i < x.length; i++) {
			sum_of_squares += Math.pow(Math.abs(x[i] - y[i]), 2);
		}
		
		return Math.sqrt(sum_of_squares);
	}
}
