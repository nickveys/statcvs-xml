package de.berlios.statcvs.xml.output;

import java.util.Iterator;
import java.util.Properties;

import net.sf.statcvs.model.CvsContent;

/**
 * @author Steffen Pingel
 */
public class ReportSettings extends Properties {

	/**
	 * @param defaults
	 */
	public ReportSettings(Properties defaults) 
	{
		super(defaults);
	}

	/**
	 * 
	 */
	public ReportSettings() 
	{

	}

	public Iterator getRevisionIterator(CvsContent content)
	{
		return content.getRevisions().iterator();
	}

}
