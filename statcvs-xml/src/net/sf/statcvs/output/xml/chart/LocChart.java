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
	$Date: 2003-07-05 16:30:33 $ 
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
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.xml.document.ModuleDocument;
import net.sf.statcvs.reportmodel.TimeLine;

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
	private TimeLine locTL = new TimeLine(null, I18n.tr("Lines"));
	private Map tagMap = new HashMap();
	
	public LocChart(CvsContent content) {
		super("globalloc.png", I18n.tr("Line Of Code"));
		RevisionIterator it
			= new RevisionSortIterator(content.getRevisionIterator());
		calculate(it);
		setRangeLabel(locTL.getRangeLabel());
		addTimeLine(locTL);
		
		makeTagAnnotations();
		getChart().setLegend(null);
		placeTitle();
	}

	public LocChart(CvsContent content, Directory dir) {
		super("loc"+ModuleDocument.escapeModuleName(dir)+".png", I18n.tr("Line Of Code for {0}", dir.getPath()));
		
		if (dir.getCurrentFileCount() == 0) {
			setChart(null);
			return;
		}
		
		locTL.addTimePoint(content.getFirstDate(), 0);
		RevisionIterator it
			= new RevisionSortIterator(dir.getRevisionIterator());
		calculate(it);
		setRangeLabel(locTL.getRangeLabel());
		addTimeLine(locTL);
		makeTagAnnotations();
		getChart().setLegend(null);
		placeTitle();
	}
	
	private void calculate(RevisionIterator it) {
		int loc = 0;
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			String[] symnames = rev.getSymbolicNames();
			if (symnames.length != 0) {
				for (int i=0; i<symnames.length; i++) {
					Date lastDate = (Date)tagMap.get(symnames[i]);
					if ((lastDate == null) || (lastDate.before(rev.getDate()))) {
						tagMap.put(symnames[i], rev.getDate());
					}
				}
			}
			if (!rev.getFile().isBinary()) {
				loc += rev.getLinesOfCodeChange();	
			}
			locTL.addTimePoint(rev.getDate(), loc);
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
