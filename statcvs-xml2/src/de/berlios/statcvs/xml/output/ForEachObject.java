package de.berlios.statcvs.xml.output;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;

/**
 * @author Steffen Pingel
 */
public class ForEachObject {

	private Object object;

	private String id;

	public ForEachObject(Object object, String id)
	{
		if (id == null) {
			throw new NullPointerException("id must not be null");
		}
		
		this.object = object;
		this.id = id;
	}

	public Iterator getDirectoryIterator(CvsContent content) 
	{
		return null;
	}
	
	public Iterator getFileIterator(CvsContent content)
	{
		return null;
	}

	public Iterator getRevisionIterator(CvsContent content)
	{
		return null;
	}

	public String getID() 
	{
		return id;
	}

	public Object getObject()
	{
		return object;
	}

}
