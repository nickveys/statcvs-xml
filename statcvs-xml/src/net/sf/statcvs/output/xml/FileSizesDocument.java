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
	Created on $Date: 2003-06-24 19:18:59 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
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
	
	/**
	 */
	public FileSizesDocument(CvsContent content) {
		super(I18n.tr("File Sizes and File Counts"), "file_sizes");
		CvsReports report = new CvsReports(content);
		charts = new CvsCharts(content);
		
		Element root = getRootElement();
		root.addContent(report.getFileCountReport());
		root.addContent(report.getAvgFileSizeReport());
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
			
			//createFileCountChart(FILE_COUNT_IMG, 640, 480),
			//createAvgFileSizeChart(FILE_SIZE_IMG, 640, 480)
		};
	}
}
