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
    
	$RCSfile: CvsReports.java,v $
	$Date: 2003-07-04 21:52:34 $ 
*/
package net.sf.statcvs.output.xml;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.output.xml.report.AuthorsPerFileReport;
import net.sf.statcvs.output.xml.report.AuthorsReport;
import net.sf.statcvs.output.xml.report.DirectorySizesReport;
import net.sf.statcvs.output.xml.report.LargestFilesReport;
import net.sf.statcvs.output.xml.report.ModulesTreeReport;
import net.sf.statcvs.output.xml.report.MostRevisionsReport;
import net.sf.statcvs.output.xml.report.ReportElement;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class CvsReports {

	private DirectorySizesReport directorySizesReport;
	private ReportElement authorsReport;
	private ReportElement modulesTreeReport;
	private CvsContent content;

	/**
	 * 
	 */
	public CvsReports(CvsContent content) 
	{
		this.content = content;
	}

	public ReportElement getAuthorsPerFileReport()
	{
		return new AuthorsPerFileReport(content);
	}

	public ReportElement getMostRevisionsReport()
	{
		return new MostRevisionsReport(content);
	}

	public ReportElement getLargestFilesReport()
	{
		return new LargestFilesReport(content);
	}

	public ReportElement getAuthorsReport() {
		if (authorsReport == null) {
			authorsReport = new AuthorsReport(content);
		}
		return authorsReport;
	}
	
	public ReportElement getModulesTreeReport() {
		if (modulesTreeReport == null) {
			modulesTreeReport = new ModulesTreeReport(content);
		}
		return modulesTreeReport;
	}
	
	public ReportElement getDirectorySizesReport() {
		if (directorySizesReport == null) {
			directorySizesReport = new DirectorySizesReport(content);
		}
		return directorySizesReport;
	}
}

