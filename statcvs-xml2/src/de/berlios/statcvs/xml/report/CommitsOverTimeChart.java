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
import java.util.Date;
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
 * @author Steffen Pingel
 */
public class CommitsOverTimeChart extends AbstractTimeSeriesChart {
    
    private CvsContent content;

	public CommitsOverTimeChart(CvsContent content, ReportSettings settings) 
	{
		super(settings, "commits_time%1.png", I18n.tr("Commits%1"), I18n.tr("Commits"));

		this.content = content;
        
        Grouper grouper = settings.getGrouper();
        if (grouper != null) {
        	Map serieses = createTimeSeries(grouper, settings.getRevisionIterator(content), new RevisionVisitorFactory(Calculator.class.getName()));
        	
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
			addTimeSeries(createTimeSeries("Commits", settings.getRevisionIterator(content), new Calculator()));
			addSymbolicNames(settings.getSymbolicNameIterator(content));
			setup(false);
        }
	}

	public static Report generate(CvsContent content, ReportSettings settings)
	{
		CommitsOverTimeChart chart = new CommitsOverTimeChart(content, settings);
		return (chart.hasData()) ? new Report(new ChartReportElement(chart)) : null;
	}

	public static class Calculator implements RevisionVisitor
	{
		Date lastDate = null;
		int commits = 0;
		public int visit(CvsRevision rev)
		{
			if (lastDate == null || !lastDate.equals(rev.getDate())) {
				commits = 0;
				lastDate = rev.getDate();
			}
			return ++commits;
		}
	}

}
