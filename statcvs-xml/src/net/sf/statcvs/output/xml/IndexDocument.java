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
	Created on $Date: 2003-07-04 12:51:08 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.chart.AbstractChart;
import net.sf.statcvs.output.xml.report.CvsCharts;
import net.sf.statcvs.output.xml.report.CvsReports;
import net.sf.statcvs.util.DateUtils;

import org.jdom.Element;

/**
 * The index document. Contains links to all other documents.
 * 
 * @author Steffen Pingel
 */
public class IndexDocument extends StatCvsDocument {

	private CvsContent content;
	public CvsCharts charts;
	
	/**
	 */
	public IndexDocument(CvsContent content) {
		super("Development statistics for " 
			  + content.getModuleName(), "index");

		this.content = content;
		CvsReports reports = new CvsReports(content);
		charts = new CvsCharts(content);
		getRootElement().addContent(new GeneralReport());
		getRootElement().addContent(new ReportListReport());
		getRootElement().addContent(new LocChartReport());
		getRootElement().addContent(reports.getAuthorsReport());
		getRootElement().addContent(reports.getModulesTreeReport());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public AbstractChart[] getCharts() {
		return new AbstractChart[] {
			charts.getLocChart()
		};
	}


	private class GeneralReport extends ReportElement
	{
		public GeneralReport() {
			super(I18n.tr("General"));
			CvsContent content = IndexDocument.this.content; 
			addContent(new PeriodElement(I18n.tr("Summary Period"),
								   content.getFirstDate(), content.getLastDate()));
			addContent(new PeriodElement(I18n.tr("Generated"),
								   DateUtils.currentDate()));
		}
	}

	private class ReportListReport extends ReportElement 
	{
		public ReportListReport() {
			super(I18n.tr("Modules"));

			Element list = new Element("reports");
			addContent(list);

			list.addContent(new LinkElement("authors", I18n.tr("Authors")));
			list.addContent(new LinkElement("commit_log", I18n.tr("Commit Log")));
			//list.addContent(new LinkElement("loc", I18n.tr("Lines Of Code")));
			list.addContent(new LinkElement("file_sizes", 
											I18n.tr("File Sizes And Counts")));
			list.addContent(new LinkElement("dir_sizes", 
											I18n.tr("Directory Sizes")));
			list.addContent(new LinkElement("dir_activity", 
											I18n.tr("Directory Activity")));
		}
	}


	private class LocChartReport extends ReportElement {

		public LocChartReport() {
			super(I18n.tr("Lines of Code"));
			CvsContent content = IndexDocument.this.content;
			addContent(new ChartElement(IndexDocument.this.charts.getLocChart()));
			addContent(new ValueElement("loc", content
					.getCurrentLOC(),I18n.tr("Lines Of Code")));
		}
	}


}
