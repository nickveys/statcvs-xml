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
    
	$RCSfile: LinkElement.java,v $
	$Date: 2003-06-19 23:56:28 $ 
*/
package net.sf.statcvs.output.xml;

import org.jdom.Element;

/**
 * @author Steffen Pingel
 */
public class LinkElement extends Element {

	/**
	 * Sets the attributes.
	 *
	 * @param ref the filename of the referenced file
	 * @param text the text for the link
	 */
	public LinkElement(String ref, String text) {
		super("link");

		setAttribute("ref", ref);
		setText(text);
	}

}
