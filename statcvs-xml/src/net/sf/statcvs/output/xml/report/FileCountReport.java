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
    
	$RCSfile: FileCountReport.java,v $ 
	Created on $Date: 2003-06-24 19:18:59 $ 
*/
package net.sf.statcvs.output.xml.report;

import java.util.Iterator;

import net.sf.statcvs.I18n;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.output.xml.ImageElement;
import net.sf.statcvs.output.xml.ValueElement;

/**
 * FileCountReport
 * 
 * @author Tammo van Lessen
 */
public class FileCountReport extends ReportElement {

	private CvsContent content;
	
	/**
	 *
	 */
	public FileCountReport(CvsContent content) {
		super(I18n.tr("File Count"));
		this.content = content;
		createReport();
	}

	private void createReport() {
		int fileCount = 0;
		Iterator fileIt = content.getFiles().iterator();
		while (fileIt.hasNext()) {
			CvsFile file = (CvsFile) fileIt.next();
			if (!file.isDead()) {
				fileCount++;
			}
		}
		
		addContent(new ImageElement(CvsCharts.FILE_COUNT_CHART_FILE));
		addContent(new ValueElement("files", fileCount, "Total Files"));
	}
}
