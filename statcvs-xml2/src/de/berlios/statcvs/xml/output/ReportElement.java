package de.berlios.statcvs.xml.output;

import java.io.File;
import java.io.IOException;

import org.jdom.Element;

/**
 * @author Steffen Pingel
 */
public class ReportElement extends Element {

	/**
	 * Sets the attributes.
	 *
	 * @param name the name of the report
	 */
	public ReportElement(String name) 
	{
		super("report");
		
		setAttribute("name", name);
	}

	public ReportElement(ReportSettings settings, String name)
	{
		this((settings.getForeachId() == null)
				?name
				:name.replaceAll("%1", settings.getForeachId()));
	}
	
	public void setReportName(String name) 
	{
		setAttribute("name", name);
	}
	
	public void saveResources(File outputPath) throws IOException
	{
	}
	
}
