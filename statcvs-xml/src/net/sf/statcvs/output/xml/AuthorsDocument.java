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
    
	$RCSfile: AuthorsDocument.java,v $ 
	Created on $Date: 2003-06-20 00:54:41 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.*;

import net.sf.statcvs.*;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.*;
import net.sf.statcvs.output.LOCSeriesBuilder;
import net.sf.statcvs.renderer.*;
import net.sf.statcvs.renderer.LOCChart;
import net.sf.statcvs.reports.*;
import net.sf.statcvs.util.*;

import org.jdom.Element;

import com.jrefinery.data.BasicTimeSeries;

/**
 * The authors document. Contains links to all author documents.
 * 
 * @author Steffen Pingel
 */
public class AuthorsDocument extends StatCvsDocument {

	private CvsContent content;
	
	/**
	 */
	public IndexDocument(CvsContent content) {
		super("User statistics for " 
			  + content.getModuleName(), "authors");

		this.content = content;

		getRootElement().addContent(createAuthorsReport());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			createActivityByHourChart(), createActivityByDayChart(),
			createCodeDistributionChart(),
		};
	}

	private Chart createActivityByHourChart()
	{
		Chart chart = createActivityChart(userRevs, Messages.getString("ACTIVITY_TIME_FOR_AUTHOR_TITLE") + " " 
							+ author.getName(),	getActivityTimeChartFilename(), 
							categoryNamesHours);
		userRevs.reset();
		return chart;
	}

	private Chart createActivityByDayChart()
	{
		Chart chart = createActivityChart(userRevs, Messages.getString("ACTIVITY_DAY_FOR_AUTHOR_TITLE") + " " 
							+ author.getName(),	getActivityDayChartFilename(), 
							categoryNamesDays);
		userRevs.reset();
		return chart;
	}

	private Chart createActivityChart(RevisionIterator revIt, String title, String fileName, 
		String[] categoryNames) {
		return new BarChart(revIt, content.getModuleName(),
				title, fileName, categoryNames.length, categoryNames);
	}

	private Chart createCodeDistributionChart() {
		int totalLinesOfCode = 0;
		while (userRevs.hasNext()) {
			CvsRevision rev = userRevs.next();
			totalLinesOfCode += rev.getLineValue();
		}
		userRevs.reset();
		if (totalLinesOfCode == 0) {
			return null;
		}
		Chart chart = new PieChart(content, content.getModuleName(),
				Messages.getString("PIE_CODEDISTRIBUTION_SUBTITLE") + " " + author.getName(),
				getCodeDistributionChartFilename(),
				author, PieChart.FILTERED_BY_USER);
		userRevs.reset();
		return chart;
	}

	private Element createAuthorsReport()
	{
		Element reportRoot = new ReportElement(I18n.tr("Authors"));
		reportRoot.addContent(new LocReport().calculate());
		return reportRoot;
	}


	private class LocReport extends AbstractLocTableReport 
	{
		public LocReport()
		{
			super(content);
		}

		public Element calculate()
		{
			Element authors = new Element("authors");

			calculateChangesAndLinesPerAuthor
				(content.getRevisionIterator());
			Iterator it = getLinesMap().iteratorSortedByValueReverse();
			while (it.hasNext()) {
				Author author = (Author) it.next();
				Element element = new Element("author");
				element.setAttribute("name", author.getName());
				element.setAttribute("loc", getLinesMap().get(author) + "");
				double percent = (double)getLinesMap().get(author) 
					/ getLinesMap().sum();
				element.setAttribute("locPercent", 
									 Formatter.formatNumber(percent, 1));
				authors.addContent(element);
			}

			return authors;
		}
	}

}
