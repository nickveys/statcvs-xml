package de.berlios.statcvs.xml.model;

import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

/**
 * @author Steffen Pingel
 */
public abstract class Grouper {

	private String id;

	public Grouper(String id)
	{
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}

		this.id = id;
	}

	public Object getGroup(Directory directory) 
	{
		return null;
	}

	public Object getGroup(CvsFile file) 
	{
		return null;
	}
	
	public Object getGroup(CvsRevision rev) 
	{
		return null;
	}

	public String getID() 
	{
		return id;
	}
	
	public abstract String getValue(Object group);

}
