# PA_HW2

Steps to run:
Open the project in Eclipse and do a Maven Install.
Then, after the install completes, go to "pa_hw2/target/classes".
Open the folder in terminal and run start.sh (You'll have to do a chmod 777 on start.sh first)

The code does the preprocessing on the documents. In "Run.java", the code for NER is there but coreNLP takes a lot of time to process NER
so it is commented. However you can uncomment the code, build and run too. Also, the centroids for clustering are selected by 
the KMeans++ algorithm. But even it has to select the initial centroid at random so the results of the code may vary after each run.
However, I found that taking the first document of each cluster as the centroid each time always gives the perfect answer. So there is 
a small commented code in "Run.java" which takes the first document of each cluster as the centroids. Simply by uncommenting the code, you can get the best answer each time. 

Also if the following error occurs: 

java.lang.IllegalArgumentException: This dataset already contains a series with the key XXXXX

it simply means that a particular cluster had an equal number of documents from each folder, so the labels assigned to different clusters 
are the same. jFreeChart does not permit this and throws an error. Simply run again to get the output.
