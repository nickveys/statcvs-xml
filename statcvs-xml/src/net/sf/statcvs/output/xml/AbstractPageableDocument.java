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
	$Date: 2003-06-19 23:48:27 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.List;

import org.jdom.Element;

/**
 * AbstractPageableDocument
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractPageableDocument extends StatCvsDocument implements Pageable {

	private String pContentName;
	private List pContent;
	private int itemsPerPage;

	/**
	 * @param filename
	 */
	public AbstractPageableDocument(String title, String filename, int itemsPerPage) {
		super(title, filename);
		setItemsPerPage(itemsPerPage);
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
	public Element getPage(int page) {
//		Element pageableContent = getPageableContent();
//		List pContent = pageableContent.getChildren();
//		String pContentName = pageableContent.getName();


		// get paging parent	
		Element pageParent = createPageTemplate();
		// fetch enclosing document
		Element element = pageParent.getDocument().getRootElement();
		// get parents children
		List doc = pageParent.getContent();
		// add pager
		doc.add(createPagerElement(page));				
		Element pageRoot = new Element(pContentName);
		doc.add(pageRoot);
		
		List elCont = pageRoot.getContent();
		
		// create page
		List pageList = null;// = new ArrayList();
		if ((pContent != null) && (page < getPageCount())) {
			if (pContent.size() < (page*itemsPerPage)+itemsPerPage) {
				pageList = pContent.subList(page*itemsPerPage, pContent.size());
			} else {
				pageList = pContent.subList(page*itemsPerPage, (page*itemsPerPage)+itemsPerPage);	
			}
		}
		
		// this: elCont.add(pageList); make that element gets null, why?
		// workaround:
		for (int i=0; i < pageList.size(); i++) {
			elCont.add(((Element)pageList.get(i)).clone());//pageList.get(i));
		}
		
		return element.detach();
	}
	
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
	
	/**
	 * This method creates the whole document, which will be repeatet
	 * on every page. the returned element will be the parent of the paged
	 * content.
	 *  
	 * @return pages parent element
	 */
	public abstract Element createPageTemplate();
	//public abstract Element getPageableContent();
}
