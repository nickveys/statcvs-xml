package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

/**
 * @author Steffen Pingel
 */
public abstract class Grouper {

	private String name;
	private String id;

	public Grouper(String id, String name)
	{
		if (id == null) {
			throw new IllegalArgumentException("id must not be null");
		}

		this.id = id;
		this.name = name;
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
	
	public String getName()
	{
		return name; 
	}

	public Object getOtherGroup()
	{
		return null;
	}
	
	public abstract Iterator getGroups(CvsContent content);
	
	public abstract String getName(Object group);

}
