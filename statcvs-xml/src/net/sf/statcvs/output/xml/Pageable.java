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
    
	$RCSfile: Pageable.java,v $
	$Date: 2003-07-05 20:12:32 $ 
*/
package net.sf.statcvs.output.xml;

import org.jdom.Element;

import net.sf.statcvs.output.xml.document.*;

/**
 * Pageable
 * 
 * @author Tammo van Lessen
 */
public interface Pageable {

	public void setItemsPerPage(int items);

	public int getItemsPerPage();

	public StatCvsDocument getPage(int page);

	public int getPageCount();

	public String getFilename(int page);

	/**
	 * This method creates the whole document, which will be repeatet
	 * on every page. the returned element will be the parent of the paged
	 * content.
	 *  
	 * @return pages parent element
	 */
	public Page createPageTemplate();

}
