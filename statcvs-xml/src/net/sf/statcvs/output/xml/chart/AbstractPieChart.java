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
    
	$RCSfile: AbstractPieChart.java,v $
	$Date: 2003-06-27 12:23:30 $ 
*/
package net.sf.statcvs.output.xml.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Pie3DPlot;
import org.jfree.data.DefaultPieDataset;

/**
 * AbstractPieChart
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractPieChart extends AbstractChart {

	DefaultPieDataset dataset;
	/**
	 * @param filename
	 * @param title
	 */
	public AbstractPieChart(String filename, String title) {
		super(filename, title);
		dataset = new DefaultPieDataset();
		createChart();
	}
	
	/**
	 * 
	 */
	private void createChart() {
		// create the chart...
		JFreeChart chart = ChartFactory.createPie3DChart(getTitle(),  // chart title
														 dataset,                // data
														 true,                // include legend
														 true,
														 false
														 );

		Pie3DPlot plot = (Pie3DPlot) chart.getPlot();
		plot.setStartAngle(270);
		plot.setDirection(Pie3DPlot.CLOCKWISE);
		plot.setForegroundAlpha(0.5f);

		setChart(chart);
		placeTitle();
	}

}
