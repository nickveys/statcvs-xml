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
    
	$RCSfile: HTMLOutputter.java,v $
	$Date: 2003-07-01 20:49:52 $ 
*/
package net.sf.statcvs.output.xml.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jdom.Element;

/**
 * HTMLOutputtter
 * 
 * @author Tammo van Lessen
 */
public class HTMLOutputter extends XMLOutputter {

	/** Array of element tag names that never of end tags **/
	private static final String[] elementsWithoutEndTags =
		{"area",
			"base",
			"basefont",
			"br",
			"col",
			"frame",
			"hr",
			"img",
			"input",
			"isindex",
			"link",
			"meta",
			"param",			
			"li"
		};

	/**
	 * @see org.jdom.output.XMLOutputter#printElement(org.jdom.Element, java.io.Writer, int, org.jdom.output.XMLOutputter.NamespaceStack)
	 */
	protected void printElement(Element element, Writer out,
						int level,
						NamespaceStack namespaces)
					throws IOException {
		
		if (!isEmptyHtmlElement(element.getQualifiedName())) {
			super.printElement(element, out, level, namespaces); 
		} else {
			out.write("<");
			out.write(element.getQualifiedName());
			List attributes = element.getAttributes();
			if (attributes != null) {
				printAttributes(attributes, element, out, namespaces);
			}
			out.write(">");
		}
		
		
	}

	private boolean isEmptyHtmlElement(String elementName)
	{
		boolean aReturn = false;
		for (int i = 0; !aReturn && i < elementsWithoutEndTags.length; i++)
			aReturn = elementsWithoutEndTags[i].equals(elementName);
		return aReturn;
	}

}
