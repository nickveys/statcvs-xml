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

import java.util.Date;

import org.jdom.Element;

import de.berlios.statcvs.xml.util.Formatter;

/**
 * ElementContainer
 * 
 * @author Steffen Pingel
 */
public class TextElement extends Element {

	ReportSettings settings;

	public TextElement(ReportSettings settings, String key) 
	{
		super("container");
		
		this.settings = settings;
		
		setAttribute("key", key);
	}

	public ListElement addList()
	{
		ListElement element = new ListElement();
		addContent(element);
		return element;
	}

	public TextElement addPeriod(String name, Date from, Date to) 
	{
		Element element = new Element("period");
		element.setAttribute("name", name);
		element.setAttribute("from", Formatter.formatDate(from));
		if (to != null) {
			element.setAttribute("to", Formatter.formatDate(to));
		}
		addContent(element);
		return this;
	}

	public TextElement addPeriod(String name, Date at) 
	{
		return addPeriod(name, at, null);
	}

	public TextElement addValue(String key, long value, String description) 
	{
		Element element = new Element("value");
		element.setAttribute("key", name);
		element.setAttribute("value", value + "");
		element.setText(description);
		addContent(element);
		return this;
	}

	public TextElement addValue(String key, double value, double percentValue,
							   String description) 
	{
		Element element = new Element("value");
		element.setAttribute("key", name);
		element.setAttribute("value", value + "");
		element.setAttribute("percentage", value + "");
		element.setText(description);
		addContent(element);
		return this;
	}

	public TextElement addValue(String key, double value, String description) 
	{
		Element element = new Element("value");
		element.setAttribute("key", name);
		element.setAttribute("value", Formatter.formatNumber(value, 1));
		element.setText(description);
		addContent(element);
		return this;
	}

	public TextElement addValue(String key, String value, String description)
	{
		Element element = new Element("value");
		element.setAttribute("key", name);
		element.setAttribute("value", value);
		element.setText(description);
		addContent(element);
		return this;
	}
	
	public TextElement addText(String text)
	{
		Element element = new Element("text");
		element.addContent(text);
		addContent(element);
		return this;
	}

	public class ListElement extends Element
	{
		
		public ListElement()
		{
			super("ul");
		}
		
		public ListElement addItem(String filename, String link)
		{
			Element element = new Element("link");
			element.setAttribute("ref", filename);
			element.setText(link);
			
			Element item = new Element("li");
			item.addContent(element);
			addContent(item);
			
			return this;
		}
		
		public ListElement addString(String text)
		{
			Element item = new Element("li");
			item.setText(text);
			addContent(item);
			
			return this;
		}
		
	}

}
