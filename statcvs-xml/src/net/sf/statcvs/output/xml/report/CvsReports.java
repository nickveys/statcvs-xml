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
	$Date: 2003-06-24 19:18:59 $ 
*/
package net.sf.statcvs.output.xml.report;

import net.sf.statcvs.model.CvsContent;

/**
 * 
 * 
 * @author Steffen Pingel
 */
public class CvsReports {

	private CvsContent content;

	/**
	 * 
	 */
	public CvsReports(CvsContent content) 
	{
		this.content = content;
	}

	public ReportElement getLOCReport()
	{
		return new LocReport();
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

	public ReportElement getFileCountReport()
	{
		return new FileCountReport(content);
	}

	public ReportElement getAvgFileSizeReport()
	{
		return new AverageFileSizeReport();
	}
}

