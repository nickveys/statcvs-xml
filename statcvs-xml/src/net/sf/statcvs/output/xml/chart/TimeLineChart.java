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
	$Date: 2003-07-06 12:30:23 $ 
*/
package net.sf.statcvs.output.xml.chart;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

import net.sf.statcvs.ConfigurationOptions;
import net.sf.statcvs.I18n;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.HorizontalDateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYStepRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

/**
 * TimeLineChart
 * 
 * @author Tammo van Lessen
 */
public class TimeLineChart extends AbstractChart {

	private String rangeLabel;
	private TimeSeriesCollection tsc;
	
	/**
	 * @param filename
	 * @param title
	 */
	public TimeLineChart(String filename, String title) {
		super(filename, title);

		Paint[] colors = new Paint[1];
		colors[0] = Color.blue;

		tsc = new TimeSeriesCollection();
		//collection.addSeries(createTimeSeries(timeline));

		//String range = timeline.getRangeLabel();
		String domain = I18n.tr("Date");

		setChart(ChartFactory.createTimeSeriesChart(
			ConfigurationOptions.getProjectName(),
			I18n.tr("Date"), rangeLabel,
			(XYDataset)tsc, 
			true,
			true,
			false));
		

		//getChart().getPlot().setSeriesPaint(colors);
		
		XYPlot plot = getChart().getXYPlot();
		HorizontalDateAxis axis = (HorizontalDateAxis) plot.getDomainAxis();
		axis.setVerticalTickLabels(true);
		plot.setRenderer(new XYStepRenderer());
	}

	void addTimeLine(TimeLine timeLine) {
		TimeSeries result =
				new TimeSeries("!??!SERIES_LABEL!??!", Millisecond.class);
		Iterator it = timeLine.getDataPoints().iterator();
		while (it.hasNext()) {
			TimePoint timePoint = (TimePoint) it.next();
			result.add(new Millisecond(timePoint.getDate()), timePoint.getValue());
		}
		tsc.addSeries(result);
	}
	
	void setRangeLabel(String rl) {
		this.rangeLabel = rl;
	}

}
