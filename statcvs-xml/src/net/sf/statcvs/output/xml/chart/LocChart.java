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
	$Date: 2003-07-06 21:26:39 $ 
*/
package net.sf.statcvs.output.xml.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.xml.document.AuthorDocument;
import net.sf.statcvs.output.xml.document.ModuleDocument;
import net.sf.statcvs.util.IntegerMap;

import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.VerticalNumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.RefineryUtilities;

/**
 * LocChart
 * 
 * @author Tammo van Lessen
 */
public class LocChart extends TimeLineChart {
	private Map tagMap = new HashMap();
	
	public LocChart(CvsContent content) {
		super("globalloc.png", I18n.tr("Lines Of Code"));
		addTimeLine(calculate(content.getRevisionIterator()));
		setupLocChart();
		getChart().setLegend(null);
	}

	public LocChart(CvsContent content, Directory dir) {
		super("loc"+ModuleDocument.escapeModuleName(dir)+".png", I18n.tr("Lines Of Code for {0}", dir.toString()));
		
		if (dir.getCurrentFileCount() == 0) {
			setChart(null);
			return;
		}

		TimeLine locTL = calculate(dir.getRevisionIterator());
		locTL.addTimePoint(content.getFirstDate(), 0);
		locTL.addTimePoint(content.getLastDate(), dir.getCurrentLOC());
		addTimeLine(locTL);
		setupLocChart();
		getChart().setLegend(null);
	}
	

	public LocChart(CvsContent content, Author author) {
		super("loc_per_author.png", I18n.tr("Lines Of Code (per Author)"));

		// init timelines per author
		Iterator authorsIt = content.getAuthors().iterator();
		Map authorTimeLineMap = new HashMap();
		IntegerMap authorsLoc = new IntegerMap();
		while (authorsIt.hasNext()) {
			Author aut = (Author) authorsIt.next();
			TimeLine locTL = new TimeLine(aut.getName());
			locTL.setInitialValue(0);
			authorTimeLineMap.put(
					aut,
					locTL);
		}

		// fill timelines and symbolic names map
		RevisionIterator allRevs = new RevisionSortIterator(content.getRevisionIterator());
		while (allRevs.hasNext()) {
			CvsRevision rev = allRevs.next();
			updateSymbolicNamesMap(rev);
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
			Author aut = (Author) it.next();
			addTimeLine((TimeLine)authorTimeLineMap.get(aut));
			if (author != null) {
				setFilename("loc_"+AuthorDocument.escapeAuthorName(author.getName())+".png");
				// make line thicker
				if (author.equals(aut)) {
					getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
				}
			}
			i++;
		}
		setupLocChart();		
	}

	private void setupLocChart() {
		setRangeLabel(I18n.tr("Lines"));
		makeTagAnnotations();
		placeTitle();
	}
	
	private TimeLine calculate(RevisionIterator rit) {
		int loc = 0;
		TimeLine locTL = new TimeLine(null);
		RevisionIterator it = new RevisionSortIterator(rit);
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			updateSymbolicNamesMap(rev);
			if (!rev.getFile().isBinary()) {
				loc += rev.getLinesOfCodeChange();	
			}
			locTL.addTimePoint(rev.getDate(), loc);
		}
		return locTL;
	}

	private void updateSymbolicNamesMap(CvsRevision rev) {
		String[] symnames = rev.getSymbolicNames();
		if (symnames.length != 0) {
			for (int i=0; i<symnames.length; i++) {
				Date lastDate = (Date)tagMap.get(symnames[i]);
				if ((lastDate == null) || (lastDate.before(rev.getDate()))) {
					tagMap.put(symnames[i], rev.getDate());
				}
			}
		}
	}

	private void makeTagAnnotations() {
		XYPlot xyplot = getChart().getXYPlot();
		Iterator tagIt = tagMap.keySet().iterator();
		while (tagIt.hasNext()) {
			String tag = (String)tagIt.next();
			Date date = (Date)tagMap.get(tag);
			
			double x = date.getTime();
			double y1 = ((VerticalNumberAxis) xyplot.getVerticalValueAxis()).getMinimumAxisValue();
			double y2 = ((VerticalNumberAxis) xyplot.getVerticalValueAxis()).getMaximumAxisValue();
			xyplot.addAnnotation(new TagAnnotation(tag, x, y1,y2));
		}
	}

	private class TagAnnotation extends XYLineAnnotation {

		private double x;
		private double y;
		private String tag;

		public TagAnnotation(String tag, double x, double y1, double y2) {
			super(x, y1, x, y2, 
				new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {3.5f}, 0.0f), 
				Color.GRAY);
			this.tag = tag;
			this.x = x;
			this.y = y2;
		}
		
		/**
		 * @see org.jfree.chart.annotations.XYAnnotation#draw(java.awt.Graphics2D, java.awt.geom.Rectangle2D, org.jfree.chart.axis.ValueAxis, org.jfree.chart.axis.ValueAxis)
		 */
		public void draw(Graphics2D g2, Rectangle2D dataArea,
						 ValueAxis domainAxis, ValueAxis rangeAxis) {
			super.draw(g2, dataArea, domainAxis, rangeAxis);

			Font font = new Font("SansSerif", Font.PLAIN, 9);
			FontRenderContext frc = g2.getFontRenderContext();
			Rectangle2D labelBounds = font.getStringBounds(tag, frc);
			// TODO Check why font metric calculation works not properly 
			float baseX = (float) domainAxis.translateValueToJava2D(this.x, dataArea)-2;
			float baseY = (float) rangeAxis.translateValueToJava2D(y, dataArea)+(float)labelBounds.getMaxX()+14;
			g2.setPaint(Color.DARK_GRAY);
			RefineryUtilities.drawRotatedString(tag, g2, baseX, baseY, -Math.PI/2);
		}
	}
}
