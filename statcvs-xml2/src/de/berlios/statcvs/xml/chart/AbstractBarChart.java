package de.berlios.statcvs.xml.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DefaultCategoryDataset;

import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * AbtractBarChart
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractBarChart extends AbstractChart {

	protected DefaultCategoryDataset dataset;
	
	/**
	 * @param filename
	 * @param title
	 */
	public AbstractBarChart(ReportSettings settings, String filename, String title, 
							String domainLabel, String rangeLabel)
	{
		super(settings, filename, title);
		
		dataset = new DefaultCategoryDataset();
		setChart(ChartFactory.createBarChart3D(
			settings.getProjectName(),  // chart title
			domainLabel,    // domain axis label
			rangeLabel,       // range axis label
			dataset,       // data
			PlotOrientation.VERTICAL,
			true,          // include legend
			true,          // tooltips
			false));          // urls
	}

	/**
	 * @deprecated
	 */
	public void setCategoryAxisLabel(String text) {
		CategoryPlot plot = getChart().getCategoryPlot();
		plot.getDomainAxis().setLabel(text);
	}

	/**
	 * @deprecated
	 */
	public void setValueAxisLabel(String text) {
		CategoryPlot plot = getChart().getCategoryPlot();
		plot.getRangeAxis().setLabel(text);
	}

}
