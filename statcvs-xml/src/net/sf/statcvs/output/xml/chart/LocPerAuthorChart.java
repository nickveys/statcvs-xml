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
    
	$RCSfile: LocPerAuthorChart.java,v $
	$Date: 2003-06-28 11:12:27 $
*/
package net.sf.statcvs.output.xml.chart;

import java.awt.BasicStroke;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.xml.AuthorDocument;
import net.sf.statcvs.reportmodel.TimeLine;
import net.sf.statcvs.util.IntegerMap;

/**
 * LocPerAuthorChart
 * 
 * @author Tammo van Lessen
 */
public class LocPerAuthorChart extends TimeLineChart {

	public LocPerAuthorChart(CvsContent content) {
		this(content, null);
	}
	
	public LocPerAuthorChart(CvsContent content, Author author) {
		super("loc_per_author.png", I18n.tr("Line Of Code (per Author)"));

		Iterator authorsIt = content.getAuthors().iterator();
		Map authorTimeLineMap = new HashMap();
		IntegerMap authorsLoc = new IntegerMap();
		while (authorsIt.hasNext()) {
			Author aut = (Author) authorsIt.next();
			TimeLine locTL = new TimeLine(null, null);
			locTL.setInitialValue(0);
			authorTimeLineMap.put(
					aut,
					locTL);
		}

		RevisionIterator allRevs = new RevisionSortIterator(content.getRevisionIterator());
		while (allRevs.hasNext()) {
			CvsRevision rev = allRevs.next();
			TimeLine timeline = (TimeLine) authorTimeLineMap.get(rev.getAuthor());
			if (!rev.getFile().isBinary()) {
				authorsLoc.addInt(rev.getAuthor(), rev.getLinesOfCodeChange());
				timeline.addTimePoint(rev.getDate(), authorsLoc.get(rev.getAuthor()));
			}
		}
		
		Iterator it = authorTimeLineMap.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Author aut = (Author) it.next();
			addTimeLine((TimeLine)authorTimeLineMap.get(aut));
			if (author != null) {
				setFilename("loc_"+AuthorDocument.escapeAuthorName(author.getName())+".png");
				// make line thicker
				if (author.equals(aut)) {
					getChart().getXYPlot().getRenderer().setSeriesStroke(i, new BasicStroke(2));
				}
			}
			i++;
		}
		
		setRangeLabel(I18n.tr("Lines"));
		
		getChart().setLegend(null);
		
		
		placeTitle();

	}

}
