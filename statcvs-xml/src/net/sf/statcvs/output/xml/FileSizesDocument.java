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
    
	$RCSfile: FileSizesDocument.java,v $ 
	Created on $Date: 2003-06-27 17:24:04 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.Iterator;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.output.xml.report.CvsCharts;
import net.sf.statcvs.output.xml.report.CvsReports;
import net.sf.statcvs.renderer.Chart;

import org.jdom.Element;


/**
 * FileSizesDocument
 * 
 * @author Tammo van Lessen
 */
public class FileSizesDocument extends StatCvsDocument {

	CvsCharts charts;
	CvsContent content;
	/**
	 */
	public FileSizesDocument(CvsContent content) {
		super(I18n.tr("File Sizes and File Counts"), "file_sizes");
		this.content = content;
		CvsReports report = new CvsReports(content);
		charts = new CvsCharts(content);
		
		Element root = getRootElement();
		root.addContent(new FileCountReport());
		root.addContent(new AverageFileSizeReport());
		root.addContent(report.getLargestFilesReport());
		root.addContent(report.getMostRevisionsReport());
		root.addContent(report.getAuthorsPerFileReport());		
	}


	/**
	 * @see net.sf.statcvs.output.xml.StatCvsDocument#getCharts()
	 */
	public Chart[] getCharts() {
		return new Chart[] {
			charts.getFileCountChart(),
			charts.getAvgFileSizeChart()
		};
	}

	private class AverageFileSizeReport extends ReportElement 
	{
		public AverageFileSizeReport() {
			super(I18n.tr("Average File Size"));
			addContent(new ChartElement(charts.getAvgFileSizeChart()));			
		}
	}

	private class FileCountReport extends ReportElement {

		public FileCountReport() {
			super(I18n.tr("File Count"));

			int fileCount = 0;
			Iterator fileIt = FileSizesDocument.this.content.getFiles().iterator();
			while (fileIt.hasNext()) {
				CvsFile file = (CvsFile) fileIt.next();
				if (!file.isDead()) {
					fileCount++;
				}
			}
			addContent(new ChartElement(charts.getFileCountChart()));
			addContent(new ValueElement("files", fileCount, "Total Files"));
		}
	}
}
