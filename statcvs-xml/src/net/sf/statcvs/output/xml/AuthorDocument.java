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
    
	$RCSfile: AuthorDocument.java,v $ 
	Created on $Date: 2003-06-27 18:34:33 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionIteratorSummary;
import net.sf.statcvs.output.xml.chart.AbstractChart;
import net.sf.statcvs.output.xml.report.CommitLogReport;
import net.sf.statcvs.output.xml.report.CvsCharts;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class AuthorDocument extends StatCvsDocument {

	private CvsCharts charts;
	private Author author;
	private CvsContent content;

	/**
	 */
	public AuthorDocument(CvsContent content, Author author, String filename) {
		super("User statistics for " 
			  + content.getModuleName(), filename);

		this.content = content;
		this.author = author;
		
		this.charts = new CvsCharts(content);
		getRootElement().addContent(new GeneralReport());
		getRootElement().addContent(new ModuleReport());
		getRootElement().addContent(new ActivityReport());
		getRootElement().addContent(new CommitLogReport(author, 20));
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public AbstractChart[] getCharts() {
		return new AbstractChart[] {
			charts.getActivityByHourChart(author),
			charts.getActivityByDayChart(author),
			charts.getDirectorySizesChart(author)
		};
	}


	private class GeneralReport extends ReportElement {
		
		public GeneralReport() {
			super(I18n.tr("General Statistics for {0}", author.getName()));
			CvsContent content = AuthorDocument.this.content;
			RevisionIteratorSummary summary;
			summary = new RevisionIteratorSummary(content.getRevisionIterator());
			long totalChangeCount = summary.size();
			long totalLineCount = summary.getLineValue();

			RevisionIterator userRevs = author.getRevisionIterator();
			summary = new RevisionIteratorSummary(userRevs);
			long userChangeCount = summary.size();
			long userLineCount = summary.getLineValue();

			double percent = (double)userChangeCount * 100 / totalChangeCount;
			
			addContent
				(new ValueElement("totalChanges", userChangeCount, percent,
					  I18n.tr("Total changes")));
			percent = (double)userLineCount * 100 / totalLineCount;
			
			addContent
				(new ValueElement("loc", userLineCount, percent, 
					  I18n.tr("Lines of code")));
		}
	}

	private class ModuleReport extends ReportElement {

		public ModuleReport() {
			super(I18n.tr("Modules"));
			addContent
				(new ChartElement(charts.getDirectorySizesChart(author)));
		}
	}

	private class ActivityReport extends ReportElement
	{
		public ActivityReport() {
			super(I18n.tr("Activity By Time"));
			addContent
				(new ChartElement(charts.getActivityByHourChart(author)));
			addContent
				(new ChartElement(charts.getActivityByDayChart(author)));
		}
	}

}
