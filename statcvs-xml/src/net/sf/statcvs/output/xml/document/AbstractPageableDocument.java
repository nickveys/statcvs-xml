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
    
	$RCSfile: AbstractPageableDocument.java,v $
	$Date: 2003-07-05 23:42:53 $ 
*/
package net.sf.statcvs.output.xml.document;

import java.util.List;

import net.sf.statcvs.output.xml.*;

import org.jdom.Element;

/**
 * AbstractPageableDocument
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractPageableDocument extends StatCvsDocument 
	implements Pageable {

	private String pContentName;
	private List pContent;
	private int itemsPerPage;

	/**
	 * @param filename
	 */
	public AbstractPageableDocument(String title, String filename) {
		super(title, filename);
	}

	/**
	 * @see net.sf.statcvs.output.xml.Pageable#setItemsPerPage(int)
	 */
	public void setItemsPerPage(int items) {
		itemsPerPage = items;
	}

	/**
	 * @see net.sf.statcvs.output.xml.Pageable#getItemsPerPage()
	 */
	public int getItemsPerPage() {
		return itemsPerPage;
	}

	/**
	 * @see net.sf.statcvs.output.xml.Pageable#getPage(int)
	 */
	public StatCvsDocument getPage(int page) {
//		Element pageableContent = getPageableContent();
//		List pContent = pageableContent.getChildren();
//		String pContentName = pageableContent.getName();

		if (pContent == null) {
			throw new NullPointerException("No pageable content.");
		}

		if (page >= getPageCount()) {
			throw new IllegalArgumentException("No such page.");
		}

		// get paging parent	
		Page documentPage = createPageTemplate(getFilename(page));
		Element contentRoot = documentPage.getContentRoot();

		// add pager
		contentRoot.addContent(createPagerElement(page));				

		// add root of paged content
		Element pageRoot = new Element(pContentName);
		contentRoot.addContent(pageRoot);
		
		// create page
		int lower = page * itemsPerPage;
		int upper = lower + itemsPerPage;
		if (upper > pContent.size()) {
			upper = pContent.size();
		}

		List pageList = pContent.subList(lower, upper);
		
		for (int i = 0; i < pageList.size(); i++) {
			// need to clone, because an element can only be used in 
			// one document at a time
			pageRoot.addContent((Element)((Element)pageList.get(i)).clone());
		}

		// return the document
		return documentPage.getDocument();
	}
	
	/**
	 * This method creates the whole document, which will be repeatet
	 * on every page. the returned element will be the parent of the paged
	 * content.
	 *  
	 * @return pages parent element
	 */
	public abstract Page createPageTemplate(String filename);

	private Element createPagerElement(int currPage) {
		Element pager = new Element("pager");
		pager.setAttribute("current", ""+(currPage + 1));
		pager.setAttribute("total", ""+getPageCount());
		for (int i=0; i < getPageCount(); i++) {
			Element page = new Element("page");
			page.setAttribute("filename", getFilename(i));
			page.setAttribute("nr", ""+(i+1));
			pager.addContent(page);
		}
		return pager;
	}

	/**
	 * @see net.sf.statcvs.output.xml.Pageable#getPageCount()
	 */
	public int getPageCount() {
		return (int) Math.ceil((double)pContent.size() / (double)itemsPerPage);
	}

	public void setPageableContent(Element content) {
		this.pContent = content.getChildren();
		this.pContentName = content.getName();
	}
	
	public String getFilename(int page) {
		return (page == 0)?getFilename():getFilename()+"_"+page;
	}
	
}
