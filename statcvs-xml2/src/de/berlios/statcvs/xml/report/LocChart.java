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
*/
package de.berlios.statcvs.xml.report;

import java.awt.BasicStroke;
import java.util.Date;
import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.TimeLineChart;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.ReportElement;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 */
public class LocChart extends TimeLineChart {
    
    private CvsContent content;
	
	public LocChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "loc%1.png", I18n.tr("Lines Of Code%1"), I18n.tr("Lines"));

	    this.content = content;
        	
		addTimeSeries("LOC", settings.getRevisionIterator(content));
		setupLocChart(false);
	}

	public LocChart(CvsContent content, ReportSettings settings, Author highlightAuthor)
	{
		super(settings, "loc%1.png", I18n.tr("Lines Of Code (per Author)"), I18n.tr("Lines"));
        this.content = content;
        
		// add a time line for each author
		int i = 0;
		Iterator it = content.getAuthors().iterator();
		while (it.hasNext()) {
			Author author = (Author)it.next();
			addTimeSeries(author.getName(), author.getRevisions().iterator());
			if (author.equals(highlightAuthor)) {
				// make line thicker
				getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			}
			++i;
		}

		setupLocChart(true);		
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		LocChart chart;		
		Object o = settings.get("foreach");
		if (o instanceof Author) {
			chart = new LocChart(content, settings, (Author)o);		
		}
		else {
			chart = new LocChart(content, settings);
		}
		
		return new ChartReportElement(chart.getSubtitle(), chart);
	}

	protected void addTimeSeries(String title, Iterator it)
	{
		TimeSeries series = createTimeSeries(title, it, new LOCCalculator());
		addTimeSeries(series, content.getFirstDate(), 0);
	}
	
	private void setupLocChart(boolean showLegend) 
	{
		addSymbolicNames(content);
		setup(showLegend);
	}
	
	public static class LOCCalculator implements RevisionVisitor
	{
		int loc = 0;
		public int visit(CvsRevision rev)
		{
			loc += rev.getLinesDelta();
			return loc;
		}
	}

}
