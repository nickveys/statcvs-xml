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
	$Date: 2004-02-21 14:09:36 $ 
*/
package de.berlios.statcvs.xml.chart;

import java.awt.Color;
import java.awt.Paint;
import java.util.Date;
import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.SymbolicName;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.XYStepRenderer;
import org.jfree.data.XYDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * TimeLineChart
 * 
 * @author Tammo van Lessen
 */
public class TimeLineChart extends AbstractChart {

	private TimeSeriesCollection tsc;
	
	/**
	 * @param filename
	 * @param title
	 */
	public TimeLineChart(ReportSettings settings, String filename, String title, 
						String rangeLabel) 
	{
		super(settings, filename, title);

		tsc = new TimeSeriesCollection();

		setChart(ChartFactory.createTimeSeriesChart(
			settings.getProjectName(),
			I18n.tr("Date"), rangeLabel,
			tsc, 
			true,
			true,
			false));
		
		//Paint[] colors = new Paint[1];
		//colors[0] = Color.blue;
		//getChart().getPlot().setSeriesPaint(colors);
		
		// setup axis
		XYPlot plot = getChart().getXYPlot();
		ValueAxis axis = plot.getDomainAxis();
		axis.setVerticalTickLabels(true);
		plot.setRenderer(new XYStepRenderer());
	}

	protected void addSymbolicNames(CvsContent content) 
	{
		XYPlot xyplot = getChart().getXYPlot();
        
		Iterator symIt = content.getSymbolicNames().iterator();
		while (symIt.hasNext()) {
			SymbolicName sn = (SymbolicName)symIt.next();
			xyplot.addAnnotation(new SymbolicNameAnnotation(sn));
		}
	}

	protected void addTimeSeries(TimeSeries series, Date firstDate, int firstValue)
	{
		series.add(new Millisecond(new Date(firstDate.getTime() - 1)), firstValue);
		tsc.addSeries(series);
	}
	
	protected void addTimeSeries(TimeSeries series)
	{
		tsc.addSeries(series);
	}

	protected TimeSeries createTimeSeries(String title, Iterator it, RevisionVisitor visitor) 
	{
		TimeSeries result = new TimeSeries(title, Millisecond.class);
		int value = 0;
		Date currentDate = null;
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision)it.next();
			value = visitor.visit(rev);
			if (currentDate == null) {
				currentDate = rev.getDate();
			}
			else if (!rev.getDate().equals(currentDate)) {
				result.add(new Millisecond(currentDate), value);
				currentDate = rev.getDate();
			}
		}
		if (currentDate != null) {
			result.add(new Millisecond(currentDate), value);
		}
		return result;
	}

}
