/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.berlios.statcvs.xml.output;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * Report
 * 
 * @author Tammo van Lessen
 * @version $Id: Report.java,v 1.2 2004-02-29 00:15:07 vanto Exp $
 */
public class Report {

	private List elements = new ArrayList();
	
	public Report(ReportElement element)
	{
		if (element != null) {
			elements.add(element);	
		}
	}
	
	public Report(ReportElement element, Separable report)
	{
		int index = element.getContent().indexOf(report);
		System.out.println(element.getContent().contains(report));
		System.out.println(index);
		System.out.println(report.getPageCount());
		for (int i = 0; i < report.getPageCount(); i++) {
			Element clone = (Element)element.clone();
			clone.getContent().set(index, report.getPage(i));	
			elements.add(clone);
		}
	}

	public int getPageCount()
	{
		return elements.size();
	}
	
	public ReportElement getPage(int page)
	{
		return (page < elements.size())?(ReportElement)elements.get(page):null;
	}
	
	public void addReportElement(ReportElement element)
	{
		if (element != null) {
			elements.add(element);	
		}
	}
}
