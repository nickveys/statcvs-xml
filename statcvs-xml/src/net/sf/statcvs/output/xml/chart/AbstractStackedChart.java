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
    
	$RCSfile: AbstractStackedChart.java,v $
	$Date: 2003-07-06 21:26:39 $ 
*/
package net.sf.statcvs.output.xml.chart;

import net.sf.statcvs.Settings;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.DefaultCategoryDataset;

/**
 * AbstractStackedChart
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractStackedChart extends AbstractChart {

	DefaultCategoryDataset dataset;
	/**
	 * @param filename
	 * @param title
	 */
	public AbstractStackedChart(String filename, String title) {
		super(filename, title);
		dataset = new DefaultCategoryDataset();
		createChart();
	}

	/**
	 * 
	 */
	private void createChart() {
		// create the chart...
		JFreeChart chart = ChartFactory.createStackedVerticalBarChart3D(
												  Settings.getProjectName(),  // chart title
												  "no desc",    // domain axis label
												  "no desc",       // range axis label
												  dataset,       // data
												  true,          // include legend
												  true,          // tooltips
												  false          // urls
											  );
		setChart(chart);  
	}

	public void setCategoryAxisLabel(String text) {
		CategoryPlot plot = getChart().getCategoryPlot();
		CategoryAxis axis = (CategoryAxis) plot.getDomainAxis();
		axis.setLabel(text);
	}

	public void setValueAxisLabel(String text) {
		CategoryPlot plot = getChart().getCategoryPlot();
		ValueAxis axis = plot.getRangeAxis();
		axis.setLabel(text);
	}
}
