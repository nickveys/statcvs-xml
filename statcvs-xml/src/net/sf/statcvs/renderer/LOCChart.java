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
    
	$RCSfile: LOCChart.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.sf.statcvs.Messages;
import net.sf.statcvs.util.OutputUtils;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.XYStepRenderer;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.XYDataset;

/**
 * Class for producing Lines Of Code charts
 * 
 * TODO: Replace by TimeSeriesChart
 *
 * @author jentzsch
 * @version $Id: LOCChart.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class LOCChart extends Chart {
	private static Logger logger =
			Logger.getLogger("net.sf.statcvs.renderer.LOCChart");

	/**
	 * Creates a Lines Of Code chart from a <tt>BasicTimeSeries</tt> and
	 * saves it as PNG
	 * @param locSeries the LOC history
	 * @param title the chart title
	 * @param subTitle the chart subtitle
	 * @param fileName the filename where the chart will be saved
	 * @param width width of PNG in pixels
	 * @param height height of PNG in pixels
	 */
	public LOCChart(BasicTimeSeries locSeries, String title, String subTitle,
			String fileName, int width, int height) {
		super(title, subTitle, fileName);
		
		Paint[] colors = new Paint[1];
		colors[0] = Color.blue;

		TimeSeriesCollection collection = new TimeSeriesCollection();
		collection.addSeries(locSeries);
		createLOCChart(collection, colors, title);
		createChart();
		saveChart(width, height);
	}

	/**
	 * Creates a Lines Of Code chart from a list of <tt>BasicTimesSeries</tt> and
	 * saves it as PNG
	 * @param locSeriesList a list of <tt>BasicTimesSeries</tt>
	 * @param title the chart title
	 * @param subTitle the chart subtitle
	 * @param fileName the filename where the chart will be saved
	 * @param width width of PNG in pixels
	 * @param height height of PNG in pixels
	 */
	public LOCChart(List locSeriesList, String title, String subTitle,
			String fileName, int width, int height) {
		super(title, subTitle, fileName);
		
		Paint[] colors = new Paint[locSeriesList.size()];
		int i = 0;
		TimeSeriesCollection collection = new TimeSeriesCollection();
		Iterator it = locSeriesList.iterator();
		while (it.hasNext()) {
			BasicTimeSeries series = (BasicTimeSeries) it.next();
			collection.addSeries(series);
			colors[i] = OutputUtils.getStringColor(series.getName()); 
			i++;
		}
		createLOCChart(collection, colors, title);
		createChart();
		saveChart(width, height);
	}

	private void createLOCChart(TimeSeriesCollection collection, Paint[] colors, 
			String title) {
		logger.finer("creating LOC chart for " + title);

		String domain = Messages.getString("TIME_LOC_DOMAIN");
		String range = Messages.getString("TIME_LOC_RANGE");

		XYDataset data = collection;
		boolean legend = (collection.getSeriesCount() > 1);
		setChart(ChartFactory.createTimeSeriesChart(title, domain, range, data, legend));

		getChart().getPlot().setSeriesPaint(colors);
		XYPlot plot = getChart().getXYPlot();
		HorizontalDateAxis axis = (HorizontalDateAxis) plot.getDomainAxis();
		axis.setVerticalTickLabels(true);
		plot.setXYItemRenderer(new XYStepRenderer());
	}
}
