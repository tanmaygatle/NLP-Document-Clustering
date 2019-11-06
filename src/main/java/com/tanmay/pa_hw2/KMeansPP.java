package com.tanmay.pa_hw2;
import java.util.Random;

public class KMeansPP {
    double[][] doc_vector;
    int no_of_clusters;
    
    public KMeansPP(int no_of_clusters, double[][] doc_vector){
        this.no_of_clusters = no_of_clusters;
        this.doc_vector = doc_vector;
    }
    
    public double[][] getCentroids(){
        double[][] centroids = new double[no_of_clusters][doc_vector[0].length];
        int rand_centroid_index = new Random().nextInt(doc_vector.length);
        for(int i = 0;i<doc_vector[0].length;i++){
            centroids[0][i] = doc_vector[rand_centroid_index][i];
        }
        
        double[] distances = new double[doc_vector.length];
        double distance = 0; double max;
        int final_centroid = 0;
        
        for(int cluster_no = 1; cluster_no < no_of_clusters; cluster_no++){
            max = Double.MIN_VALUE;
            for(int i = 0; i < doc_vector.length; i++){
                distances[i] = Double.MAX_VALUE;
                for(int j = 0; j < cluster_no; j++){
                    
                	for(int z = 0; z < doc_vector[0].length; z++)
                        distance = distance + Math.pow(doc_vector[i][z]-centroids[j][z], 2);

                    if(distance < distances[i])
                        distances[i] = distance;
                    
                    distance = 0;
                }
                if(distances[i] > max){
                    max = distances[i];
                    final_centroid = i;
                }
            }
            
            for(int k = 0; k < doc_vector[0].length; k++){
                centroids[cluster_no][k] = doc_vector[final_centroid][k];
            }
        }
        return centroids;
    }
}
