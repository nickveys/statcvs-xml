package de.berlios.statcvs.xml.output;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.Directory;

import org.jdom.Element;

import de.berlios.statcvs.xml.util.Formatter;

/**
 * @author Steffen Pingel
 */
public class TableElement extends Element {

	ReportSettings settings;
	
	/**
	 * @param name the name of the report
	 */
	public TableElement(ReportSettings settings, String[] headers) 
	{
		super("table");
		this.settings = settings;
		Element row = new Element("tr");
		for (int i = 0; i < headers.length; i++) {
			row.addContent(new Element("th").addContent(headers[i]));
		}
	}
	
	public RowElement addRow()
	{
		RowElement row = new RowElement();
		addContent(row);
		return row;
	}
	
	public class RowElement extends Element
	{
		public RowElement()
		{
			super("row");
		}
		
		public RowElement addInteger(String key, int value, double percentage)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + value);
			number.setAttribute("percentage", "" + Formatter.formatPercent(percentage) );
			
			addContent(number);
			return this;
		}

		public RowElement addInteger(String key, int value)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + value);
			
			addContent(number);
			return this;
		}
		
		public RowElement addDouble(String key, double value, double percentage)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + Formatter.formatNumber(value, 2));
			number.setAttribute("percentage", "" + Formatter.formatPercent(percentage) );
			
			addContent(number);
			return this;
		}

		public RowElement addDouble(String key, double value)
		{
			Element number = new Element("number");
			number.setAttribute("key", key);
			number.setAttribute("value", "" + Formatter.formatNumber(value, 2));
			
			addContent(number);
			return this;
		}
		
		public RowElement addString(String key, String value) 
		{
			addContent(new Element("string").setAttribute("key", key).setAttribute("value", value));
			return this;
		}
		
		public RowElement addLink(String key, String value, String url)
		{
			addContent(new Element("link")
							.setAttribute("key", key)
							.setAttribute("value", value)
							.setAttribute("url", url));
			return this;
		}
		
		public RowElement addDirectory(Directory dir) 
		{
			addContent(new Element("directory").setAttribute("name", dir.getPath()));
			return this;
		}
		
		public RowElement addModule(Module module) 
		{
			addContent(new Element("module").setAttribute("name", module.getName()));
			return this;
		}
		
		public RowElement addAuthor(Author author) 
		{
			addContent(new Element("author").setAttribute("name", author.getName()));
			return this;
		}
	}
}
