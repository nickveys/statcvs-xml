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
    
	$RCSfile: DirectorySizesDocument.java,v $ 
	Created on $Date: 2003-06-27 18:15:46 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.chart.AbstractChart;
import net.sf.statcvs.output.xml.report.CvsCharts;
import net.sf.statcvs.output.xml.report.CvsReports;

/**
 * DirectorySizesDocument
 * 
 * @author Tammo van Lessen
 */
public class DirectorySizesDocument extends StatCvsDocument {

	private CvsCharts charts;
	
	/**
	 * @param element
	 * @param filename
	 */
	public DirectorySizesDocument(CvsContent content) {
		super(I18n.tr("Module Sizes"), "dir_sizes");

		CvsReports reports = new CvsReports(content);
		charts = new CvsCharts(content);
		
		getRootElement().addContent(new DirectoryChartReport());
		getRootElement().addContent(reports.getDirectorySizesReport());
	}

	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public AbstractChart[] getCharts() {
		return new AbstractChart[] {charts.getDirectorySizesChart()};
	}

	private class DirectoryChartReport extends ReportElement {

		public DirectoryChartReport() {
			super(I18n.tr("Directory Sizes"));
			addContent(new ChartElement(
				DirectorySizesDocument.this.charts.getDirectorySizesChart()));
		}
	}
}
