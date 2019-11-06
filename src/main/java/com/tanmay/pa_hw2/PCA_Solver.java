package com.tanmay.pa_hw2;
import smile.projection.PCA;

public class PCA_Solver {
	public double[][] applyPCA(double[][] doc_vectors) {
		double[][] pointsArray = doc_vectors.clone();
		
		System.out.println("\nApplying PCA:\n");
		PCA pca = new PCA(pointsArray);
		pca.setProjection(2);
		double[][] pca_matrix = pca.project(pointsArray); 
		
		System.out.format("%32s%32s\n", "PCA1", "PCA2");
		for(int i = 0; i < pca_matrix.length; i++) {
			for(int j = 0; j < pca_matrix[0].length; j++) {
				System.out.format("%32f",pca_matrix[i][j]);
			}
			System.out.println();
		}
		System.out.println("PCA Completed");
		return pca_matrix;
	}
}