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
    
	$RCSfile: LocChart.java,v $
	$Date: 2004-02-18 18:33:28 $ 
*/
package de.berlios.statcvs.xml.report;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.SymbolicName;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.plot.XYPlot;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.chart.*;
import de.berlios.statcvs.xml.chart.TimeLine;
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
		super(settings, "loc.png", I18n.tr("Lines Of Code"));
	    this.content = content;
        	
		addTimeLine(calculate(settings.getRevisionIterator(content)));
		setupLocChart();
		getChart().setLegend(null);
	}

	public LocChart(CvsContent content, ReportSettings settings, Directory dir) 
	{
		super(settings, null, I18n.tr("Lines Of Code for {0}", dir.toString()));
        this.content = content;
        
		TimeLine locTL = calculate(settings.getRevisionIterator(content));
		locTL.addTimePoint(content.getFirstDate(), 0);
		locTL.addTimePoint(content.getLastDate(), dir.getCurrentLOC());
		addTimeLine(locTL);
		setupLocChart();
		getChart().setLegend(null);
	}
	

	public LocChart(CvsContent content, ReportSettings settings, Author author)
	{
		super(settings, "loc_" + author.getName() +".png", I18n.tr("Lines Of Code (per Author)"));
        this.content = content;
        
		// init timelines per author
		Iterator authorsIt = content.getAuthors().iterator();
		Map authorTimeLineMap = new HashMap();
		IntegerMap authorsLoc = new IntegerMap();
		while (authorsIt.hasNext()) {
			Author aut = (Author) authorsIt.next();
			TimeLine locTL = new TimeLine(aut.getName());
			locTL.setInitialValue(0);
			authorTimeLineMap.put(aut, locTL);
		}

		// fill timelines and symbolic names map
		Iterator allRevs = content.getRevisions().iterator();
		while (allRevs.hasNext()) {
			CvsRevision rev = (CvsRevision)allRevs.next();
			TimeLine timeline = (TimeLine) authorTimeLineMap.get(rev.getAuthor());
			if (!rev.getFile().isBinary()) {
				authorsLoc.addInt(rev.getAuthor(), rev.getLinesOfCodeChange());
				timeline.addTimePoint(rev.getDate(), authorsLoc.get(rev.getAuthor()));
			}
		}
		
		// create chart
		Iterator it = authorTimeLineMap.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Author aut = (Author)it.next();
			addTimeLine((TimeLine)authorTimeLineMap.get(aut));
			if (author != null) {
//				setFilename("loc_"+AuthorDocument.escapeAuthorName(author.getName())+".png");
				// make line thicker
				if (author.equals(aut)) {
					getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
				}
			}
			i++;
		}
		setupLocChart();		
	}

	public static ReportElement generate(CvsContent content, ReportSettings settings)
	{
		LocChart chart;		
		Object o = settings.get("foreach");
		if (o instanceof Author) {
			chart = new LocChart(content, settings, (Author)o);
		}
		else if (o instanceof Directory) {
			chart = new LocChart(content, settings, (Directory)o);
		}
		else {
			chart = new LocChart(content, settings);
		}
		 
		return new ChartReportElement(chart.getTitle(), chart);
	}

	private void setupLocChart() {
		setRangeLabel(I18n.tr("Lines"));
		makeTagAnnotations();
		placeTitle();
	}
	
	private TimeLine calculate(Iterator it) 
	{
		int loc = 0;
		TimeLine locTL = new TimeLine(null);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			if (!rev.getFile().isBinary()) {
				loc += rev.getLinesOfCodeChange();	
				//loc = rev.getEffectiveLinesOfCode();
			}
			locTL.addTimePoint(rev.getDate(), loc);
		}
		return locTL;
	}

	private void makeTagAnnotations() 
	{
		XYPlot xyplot = getChart().getXYPlot();
        
        Iterator symIt = content.getSymbolicNames().iterator();
        while (symIt.hasNext()) {
            SymbolicName sn = (SymbolicName)symIt.next();
            xyplot.addAnnotation(new SymbolicNameAnnotation(sn));
        }
	}

}
