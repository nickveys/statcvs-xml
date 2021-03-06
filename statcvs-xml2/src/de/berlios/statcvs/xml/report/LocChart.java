/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.berlios.statcvs.xml.report;

import java.awt.BasicStroke;
import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;

import org.jfree.data.time.TimeSeries;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.AbstractTimeSeriesChart;
import de.berlios.statcvs.xml.chart.RevisionVisitor;
import de.berlios.statcvs.xml.chart.RevisionVisitorFactory;
import de.berlios.statcvs.xml.model.Grouper;
import de.berlios.statcvs.xml.output.ChartReportElement;
import de.berlios.statcvs.xml.output.Report;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 * @author Steffen Pingel
 */
public class LocChart extends AbstractTimeSeriesChart {
    
    private CvsContent content;
	
	protected LocChart(CvsContent content, ReportSettings settings, String title)
	{
		super(settings, "loc%1.png", title, I18n.tr("Lines"));

		this.content = content;
	}
	
	public LocChart(CvsContent content, ReportSettings settings) 
	{
		this(content, settings, I18n.tr("Lines Of Code%1"));
        
        Grouper grouper = settings.getGrouper();
        if (grouper != null) {
        	Map serieses = createTimeSeries(grouper, settings.getRevisionIterator(content), new RevisionVisitorFactory(LOCCalculator.class.getName()));
        	
        	Object feo = settings.getForEachObject();
        	
        	for (Iterator it = serieses.keySet().iterator(); it.hasNext();) {
        		Object group = it.next();
        		addTimeSeries((TimeSeries)serieses.get(group), content.getFirstDate(), 0);
        		
        		if (group == settings.getForEachObject()) {
        			// make line thicker
        			getChart().getXYPlot().getRenderer().setSeriesStroke(getSeriesCount() - 1, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        		}
        		
        	}
        	
			addSymbolicNames(settings.getSymbolicNameIterator(content));
			setup(true);
        }
        else {
			addTimeSeries("LOC", settings.getRevisionIterator(content));
			addSymbolicNames(settings.getSymbolicNameIterator(content));
			setup(false);
        }
	}

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		LocChart chart = new LocChart(content, settings);
		return (chart.hasData()) ? new Report(new ChartReportElement(chart)) : null;
	}

	protected void addTimeSeries(String title, Iterator it)
	{
		TimeSeries series = createTimeSeries(title, it, new LOCCalculator());
		addTimeSeries(series, content.getFirstDate(), 0);
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
