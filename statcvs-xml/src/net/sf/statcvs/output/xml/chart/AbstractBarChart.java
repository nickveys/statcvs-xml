/*
 * statcvs-xml
 * TODO
 * Created on 27.06.2003
 *
 */
package net.sf.statcvs.output.xml.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.HorizontalCategoryAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.DefaultCategoryDataset;

/**
 * AbtractBarChart
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public abstract class AbstractBarChart extends AbstractChart {

	DefaultCategoryDataset dataset;
	
	/**
	 * @param filename
	 * @param title
	 */
	public AbstractBarChart(String filename, String title) {
		super(filename, title);
		dataset = new DefaultCategoryDataset();
		createChart();
	}

	/**
	 * 
	 */
	private void createChart() {
		// create the chart...
		JFreeChart chart = ChartFactory.createVerticalBarChart3D(
												  getTitle(),  // chart title
												  "no desc",    // domain axis label
												  "no desc",       // range axis label
												  dataset,       // data
												  true,          // include legend
												  true,          // tooltips
												  false          // urls
											  );
		setChart(chart);  
		//placeTitle();      
	}

	public void setCategoryAxisLabel(String text) {
		CategoryPlot plot = getChart().getCategoryPlot();
		HorizontalCategoryAxis3D axis = (HorizontalCategoryAxis3D) plot.getDomainAxis();
		axis.setLabel(text);
	}

	public void setValueAxisLabel(String text) {
		CategoryPlot plot = getChart().getCategoryPlot();
		ValueAxis axis = plot.getRangeAxis();
		axis.setLabel(text);
	}

}
