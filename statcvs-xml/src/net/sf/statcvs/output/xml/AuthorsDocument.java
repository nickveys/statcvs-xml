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
	Created on $Date: 2003-06-26 23:04:55 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.report.CvsCharts;
import net.sf.statcvs.output.xml.report.CvsReports;
import net.sf.statcvs.renderer.Chart;

/**
 * The authors document. Contains links to all author documents.
 * 
 * @author Steffen Pingel
 */
public class AuthorsDocument extends StatCvsDocument {


	private CvsCharts charts;
	private CvsContent content;

	/**
	 */
	public AuthorsDocument(CvsContent content) {
		super("User statistics for " 
			  + content.getModuleName(), "authors");

		this.content = content;
		this.charts = new CvsCharts(content);	
		CvsReports reports = new CvsReports(content);
		getRootElement().addContent(reports.getAuthorsReport());
		getRootElement().addContent(new LinesOfCodeReport());
		getRootElement().addContent(new ActivityByClockTime());
		getRootElement().addContent(new AuthorActivityReport());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			charts.getLOCPerAuthorChart(),
			charts.getActivityByDayChart(),
			charts.getActivityByHourChart(),
			charts.getAuthorActivityChart()
		};
	}


	private class LinesOfCodeReport extends ReportElement {
		public LinesOfCodeReport() {
			super(I18n.tr("Lines of Code"));
			addContent(new ChartElement(charts.getLOCPerAuthorChart()));
		}
	}

	private class ActivityByClockTime extends ReportElement {
		public ActivityByClockTime() {
			super(I18n.tr("Activity by Clock Time"));
			addContent(new ChartElement(charts.getActivityByHourChart()));
			addContent(new ChartElement(charts.getActivityByDayChart()));
		}
	}

	private class AuthorActivityReport extends ReportElement {
		public AuthorActivityReport() {
			super(I18n.tr("Author Activity"));
			addContent(new ChartElement(charts.getAuthorActivityChart()));
		}
	}
}
