/*
    StatCvs - CVS statistics generation 
    Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
    http://statcvs.sf.net/
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile: AbstractBarChart.java,v $
	$Date: 2003-07-06 12:30:23 $ 
*/
package net.sf.statcvs.output.xml.chart;

import net.sf.statcvs.ConfigurationOptions;

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
												  ConfigurationOptions.getProjectName(),  // chart title
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
