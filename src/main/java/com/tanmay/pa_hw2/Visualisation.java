package com.tanmay.pa_hw2;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.swing.JFrame;  
import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.XYPlot;  
import org.jfree.data.xy.XYDataset;  
import org.jfree.data.xy.XYSeries;  
import org.jfree.data.xy.XYSeriesCollection;  
import org.jfree.chart.ChartUtils;

public class Visualisation extends JFrame{
	private static final long serialVersionUID = 6294689542092367723L;  
	public Visualisation(String chartName, String title,String[] labels_per_cluster, int[][] assigncluster, double[][] points) {  
	    super(title);  
	  
	    XYDataset dataset = createDataset(labels_per_cluster,assigncluster, points);  
	  
	    JFreeChart chart = ChartFactory.createScatterPlot(  
	        "Cluster Visualisation",   
	        "X-Axis", "Y-Axis", dataset);  
	  
	      
	    XYPlot plot = (XYPlot)chart.getPlot();  
	    plot.setBackgroundPaint(new Color(255,255,255));  
	    
	    ChartPanel panel = new ChartPanel(chart);  
	    panel.setSize(1000, 500); 
	    
	    try {

		    OutputStream out = new FileOutputStream(chartName);
		    ChartUtils.writeChartAsPNG(out,
		            chart,
		            panel.getWidth(),
		            panel.getHeight());

		} catch (Exception ex) {
		    System.out.print("Error " + ex.toString());
		}
	  }  
	  
	  private XYDataset createDataset(String[] labels_per_cluster, int[][] assigncluster, double[][] points) {  
	    XYSeriesCollection dataset = new XYSeriesCollection();  
	    
	    for(int i = 0; i < assigncluster.length; i++) {
	    	XYSeries series = new XYSeries(labels_per_cluster[i]);
	    	for(int j = 0; j < assigncluster[0].length; j++) {
	    		if(assigncluster[i][j] == 0)
	    			continue;
	    		else {
	    			series.add(points[j][0], points[j][1]);
	    		}
	    	}
	    	dataset.addSeries(series);
	    }
	    return dataset;  
	  }  
}