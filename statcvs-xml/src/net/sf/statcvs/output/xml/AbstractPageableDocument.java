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
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output.xml;

import java.util.List;

import org.jdom.Element;

/**
 * AbstractPageableDocument
 * 
 * @author Tammo van Lessen
 */
public abstract class AbstractPageableDocument extends StatCvsDocument implements Pageable{

	private int itemsPerPage;
	private List content = null;
	private String rootElement = null;

	/**
	 * @param element
	 * @param filename
	 */
	public AbstractPageableDocument(Element element, String filename, int itemsPerPage) {
		super(element, filename);
		setItemsPerPage(itemsPerPage);
	}

	/**
	 * @param filename
	 */
	public AbstractPageableDocument(String filename, int itemsPerPage) {
		super(filename);
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
		// dirty!
		if ((content == null) || (page > getPageCount())) return new Element("nocontent");
		Element element = new Element(rootElement);
		element.setAttribute("page", Integer.toString(page));
		element.setAttribute("totalPages", Integer.toString(getPageCount()));
		List pageList;
		if (content.size() < (page*itemsPerPage)+itemsPerPage) {
			pageList = content.subList(page*itemsPerPage, content.size());
		} else {
			pageList = content.subList(page*itemsPerPage, (page*itemsPerPage)+itemsPerPage);	
		}
		element.setContent(pageList);
		return element;
	}
	
	/**
	 * @see net.sf.statcvs.output.xml.Pageable#getPageCount()
	 */
	public int getPageCount() {
		return content.size() / itemsPerPage;
	}

	/**
	 * @see net.sf.statcvs.output.xml.Pageable#getHeader()
	 */
	public abstract Element getHeader();
	
	/**
	 * @see net.sf.statcvs.output.xml.Pageable#getFooter()
	 */
	public abstract Element getFooter();

	/**
	 * @see net.sf.statcvs.output.xml.Pageable#setContent(org.jdom.Element)
	 */
	public void setContent(Element content) {
		rootElement = content.getName();
		this.content = content.getChildren();
	}

}
