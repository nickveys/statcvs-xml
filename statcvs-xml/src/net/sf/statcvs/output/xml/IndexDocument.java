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
    
	$RCSfile: IndexDocument.java,v $ 
	Created on $Date: 2003-06-26 23:04:55 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.Iterator;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.RevisionIterator;
import net.sf.statcvs.model.RevisionSortIterator;
import net.sf.statcvs.output.LOCSeriesBuilder;
import net.sf.statcvs.output.xml.report.CvsReports;
import net.sf.statcvs.renderer.Chart;
import net.sf.statcvs.renderer.LOCChart;
import net.sf.statcvs.reports.AbstractLocTableReport;
import net.sf.statcvs.util.DateUtils;
import net.sf.statcvs.util.Formatter;

import org.jdom.Element;

import com.jrefinery.data.BasicTimeSeries;

/**
 * The index document. Contains links to all other documents.
 * 
 * @author Steffen Pingel
 */
public class IndexDocument extends StatCvsDocument {

	private static final String LOC_IMAGE_FILENAME = "loc_small.png";
	private CvsContent content;
	
	/**
	 */
	public IndexDocument(CvsContent content) {
		super("Development statistics for " 
			  + content.getModuleName(), "index");

		this.content = content;
		CvsReports reports = new CvsReports(content);
		getRootElement().addContent(createGeneralReport());
		getRootElement().addContent(createReportRefs());
		getRootElement().addContent(createLOCReport());
		getRootElement().addContent(createAuthorsReport());
		getRootElement().addContent(reports.getModulesTreeReport());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			createLOCChart(LOC_IMAGE_FILENAME, 640, 480),
		};
	}

	private Chart createLOCChart(String filename,
								 int width, int height) {
		String projectName = content.getModuleName();
		RevisionIterator it
			= new RevisionSortIterator(content.getRevisionIterator());
		LOCSeriesBuilder locCounter 
			= new LOCSeriesBuilder(I18n.tr("Lines Of Code"), true);
		while (it.hasNext()) {
			locCounter.addRevision(it.next());
		}
		BasicTimeSeries series = locCounter.getTimeSeries();
		
		if (series == null) {
			return null;
		}
		return new LOCChart(series, projectName, I18n.tr("Lines Of Code"),
							filename, width, height);
	}

	private Element createGeneralReport()
	{
		Element reportRoot = new ReportElement("General");
		reportRoot.addContent
			(new PeriodElement(I18n.tr("Summary Period"),
							   content.getFirstDate(), content.getLastDate()));
		reportRoot.addContent
			(new PeriodElement(I18n.tr("Generated"),
							   DateUtils.currentDate()));
		return reportRoot;
	}

	private Element createReportRefs() {
		Element reportRoot = new Element("report");
		reportRoot.setAttribute("name", "Modules");

		Element list = new Element("reports");
		reportRoot.addContent(list);

		list.addContent(new LinkElement("authors", I18n.tr("Authors")));
		list.addContent(new LinkElement("commit_log", I18n.tr("Commit Log")));
		list.addContent(new LinkElement("loc", I18n.tr("Lines Of Code")));
		list.addContent(new LinkElement("file_sizes", 
										I18n.tr("File Sizes And Counts")));
		list.addContent(new LinkElement("dir_sizes", 
										I18n.tr("Directory Sizes")));
							 
		return reportRoot;
	}

	private Element createLOCReport() {
		Element reportRoot = new ReportElement(I18n.tr("Lines Of Code"));

		reportRoot.addContent(new ImageElement (LOC_IMAGE_FILENAME));
		reportRoot.addContent
			(new ValueElement ("loc", content.getCurrentLOC(),
							   I18n.tr("Lines Of Code")));

//  		element.setAttribute("date", 
//  							 DateUtils.formatDateAndTime(content.getLastDate()));

		return reportRoot;
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
									 Formatter.formatPercent(percent));
				authors.addContent(element);
			}

			return authors;
		}
	}

}
