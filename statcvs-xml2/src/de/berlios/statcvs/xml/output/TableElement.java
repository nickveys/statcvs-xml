package de.berlios.statcvs.xml.output;

import org.jdom.Element;

/**
 * @author Steffen Pingel
 */
public class TableElement extends Element {

	/**
	 * @param name the name of the report
	 */
	public TableElement(String[] headers) 
	{
		super("table");
		
		Element row = new Element("tr");
		for (int i = 0; i < headers.length; i++) {
			row.addContent(new Element("th").addContent(headers[i]));
		}
	}
	
}
