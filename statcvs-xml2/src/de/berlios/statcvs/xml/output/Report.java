/*
 * statcvs-xml2
 * TODO
 * Created on 28.02.2004
 *
 */
package de.berlios.statcvs.xml.output;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * Report
 * 
 * @author Tammo van Lessen
 * @version $Id: Report.java,v 1.1 2004-02-28 21:45:47 vanto Exp $
 */
public class Report {

	private List elements = new ArrayList();
	
	public Report(ReportElement element)
	{
		if (element != null) {
			elements.add(element);	
		}
	}
	
	public Report(ReportSettings settings, ReportElement element, Element pParent, final String pageable)
	{
		if (element == null) {
			return;
		}
		
		if (!settings.isPaging()) {
			elements.add(element);
			return;
		}
		
		Element pClone = (Element)pParent.clone();
		List children = pClone.getChildren(pageable);
		
		
		ReportElement currPage = (ReportElement)element.clone();
		pParent.removeChildren(pageable);
		System.out.println(children.size());
		for (int i = 0; i < children.size(); i++) {
			currPage.getChildren().add(children.get(i));
			
			// new page
			if (i % settings.getLimit() == 0) {
				elements.add(currPage);
				currPage = (ReportElement)element.clone();
				currPage.removeChildren(pageable);				
			}
		}
		
		// add last page if not empty
		if (currPage.getChildren(pageable).size() > 0) {
			elements.add(currPage);	
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
