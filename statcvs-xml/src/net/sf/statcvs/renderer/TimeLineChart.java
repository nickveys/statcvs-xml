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
    
	$RCSfile: TimeLineChart.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.renderer;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;
import java.util.logging.Logger;

import net.sf.statcvs.Messages;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.reportmodel.TimePoint;

import com.jrefinery.chart.ChartFactory;
import com.jrefinery.chart.HorizontalDateAxis;
import com.jrefinery.chart.XYPlot;
import com.jrefinery.chart.XYStepRenderer;
import com.jrefinery.data.BasicTimeSeries;
import com.jrefinery.data.Millisecond;
import com.jrefinery.data.TimeSeriesCollection;
import com.jrefinery.data.XYDataset;

/**
 * Creates charts from {@link net.sf.statcvs.reportmodel.TimeLine}s and
 * saves them to PNG.
 *
 * TODO: Should call TimeLine#isEmpty and not generate the chart if true
 *  
 * @author Richard Cyganiak
 * @version $Id: TimeLineChart.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class TimeLineChart extends Chart {
	private static Logger logger =
			Logger.getLogger("net.sf.statcvs.renderer.LOCChart");

	/**
	 * Creates a chart from a time line.
	 * @param timeLine the time line data for the chart 
	 * @param title the title for the chart
	 * @param fileName the file name for the PNG image
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public TimeLineChart(TimeLine timeLine, String title, String fileName,
			int width, int height) {

		super(title, timeLine.getTitle(), fileName);
		
		Paint[] colors = new Paint[1];
		colors[0] = Color.blue;

		TimeSeriesCollection collection = new TimeSeriesCollection();
		collection.addSeries(createTimeSeries(timeLine));

		logger.finer("creating time line chart for "
				+ title + " / " + timeLine.getTitle());

		String range = timeLine.getRangeLabel();
		String domain = Messages.getString("DOMAIN_TIME");

		XYDataset data = collection;
		setChart(ChartFactory.createTimeSeriesChart(title, domain, range, data, false));

		getChart().getPlot().setSeriesPaint(colors);
		XYPlot plot = getChart().getXYPlot();
		HorizontalDateAxis axis = (HorizontalDateAxis) plot.getDomainAxis();
		axis.setVerticalTickLabels(true);
		plot.setXYItemRenderer(new XYStepRenderer());

		createChart();
		saveChart(width, height);
	}

	private BasicTimeSeries createTimeSeries(TimeLine timeLine) {
		BasicTimeSeries result =
				new BasicTimeSeries("!??!SERIES_LABEL!??!", Millisecond.class);
		Iterator it = timeLine.getDataPoints().iterator();
		while (it.hasNext()) {
			TimePoint timePoint = (TimePoint) it.next();
			result.add(new Millisecond(timePoint.getDate()), timePoint.getValue());
		}
		return result;
	}
}
