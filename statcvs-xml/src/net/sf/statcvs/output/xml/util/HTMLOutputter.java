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
	$Date: 2003-07-05 13:57:03 $ 
*/
package net.sf.statcvs.output.xml.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

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
			//"li"
		};

	/**
	 * @see org.jdom.output.XMLOutputter#printElement(org.jdom.Element, java.io.Writer, int, org.jdom.output.XMLOutputter.NamespaceStack)
	 */
	protected void printElement(Element element, Writer out,
						int level,
						NamespaceStack namespaces)
					throws IOException {
		
		if (!isEmptyHtmlElement(element.getQualifiedName())) {
			element.setNamespace(Namespace.NO_NAMESPACE);
			super.printElement(element, out, level, namespaces); 
		} else {
			out.write("<");
			out.write(element.getQualifiedName());
			List attributes = element.getAttributes();
			if (attributes != null) {
				printAttributes(attributes, element, out, namespaces);
			}
			out.write(">");

			/*List content = element.getContent();
			int start = skipLeadingWhite(content, 0);
			int size = content.size();
			if (start >= size) {
				// Case content is empty or all insignificant whitespace
				if (currentFormat.expandEmptyElements) {
					out.write("></");
					out.write(element.getQualifiedName());
					out.write(">");
				}
				else {
					out.write(" />");
				}
			}
			else {
				out.write(">");

				// For a special case where the content is only CDATA
				// or Text we don't want to indent after the start or
				// before the end tag.

				if (nextNonText(content, start) < size) {
					// Case Mixed Content - normal indentation
					newline(out);
					printContentRange(out, content, start, size,
									  level + 1, namespaces);
					newline(out);
					indent(out, level);
				}
				else {
					// Case all CDATA or Text - no indentation
					printTextRange(out, content, start, size);
				}
				out.write("</");
				out.write(element.getQualifiedName());
				out.write(">");
			}*/
		}
		
		
	}

	private boolean isEmptyHtmlElement(String elementName)
	{
		boolean aReturn = false;
		for (int i = 0; !aReturn && i < elementsWithoutEndTags.length; i++)
			aReturn = elementsWithoutEndTags[i].equals(elementName);
		return aReturn;
	}

	/**
	 * @see org.jdom.output.XMLOutputter#output(org.jdom.Document, java.io.Writer)
	 */
	public void output(Document document, Writer out) throws IOException {
		DocType type     = new DocType("html", "-//W3C//DTD HTML 4.01//EN", 
											 "http://www.w3.org/TR/html4/strict.dtd");

		document.setDocType(type);
		super.output(document, out);
	}

}
