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

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;

/**
 * HTMLOutputtter
 * 
 * @author Tammo van Lessen
 */
public class HTMLOutputter extends XMLOutputter {

	/** Array of element tag names that never of end tags **/
	private static final Set elementsWithoutEndTags = new HashSet();
	static {
		elementsWithoutEndTags.add("area");
		elementsWithoutEndTags.add("base");
		elementsWithoutEndTags.add("basefont");
		elementsWithoutEndTags.add("br");
		elementsWithoutEndTags.add("col");
		elementsWithoutEndTags.add("frame");
		elementsWithoutEndTags.add("hr");
		elementsWithoutEndTags.add("img");
		elementsWithoutEndTags.add("input");
		elementsWithoutEndTags.add("isindex");
		elementsWithoutEndTags.add("link");
		elementsWithoutEndTags.add("meta");
		elementsWithoutEndTags.add("param");			
	};

	public HTMLOutputter(Format format)
	{
		super(format);
	}
	
	/**
	 * @see org.jdom.output.XMLOutputter#printElement(org.jdom.Element, java.io.Writer, int, org.jdom.output.XMLOutputter.NamespaceStack)
	 */
	protected void printElement(Element element, Writer out, int level, NamespaceStack namespaces)
		throws IOException 
	{
		// work around?
		element.setNamespace(Namespace.NO_NAMESPACE);

		if (elementsWithoutEndTags.contains(element.getQualifiedName())) {
			// plain old html tag
			out.write("<");
			out.write(element.getQualifiedName());
			List attributes = element.getAttributes();
			if (attributes != null) {
				printAttributes(out, attributes, element, namespaces);
			}
			out.write(">");
		} 
		else {
			super.printElement(out, element, level, namespaces); 
		}

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

	/**
	 * @see org.jdom.output.XMLOutputter#output(org.jdom.Document, java.io.Writer)
	 */
	public void output(Document document, Writer out) throws IOException 
	{
		DocType type = new DocType("html", "-//W3C//DTD HTML 4.01//EN", 
								   "http://www.w3.org/TR/html4/strict.dtd");
		
		document.setDocType(type);
		super.output(document, out);
	}

}
