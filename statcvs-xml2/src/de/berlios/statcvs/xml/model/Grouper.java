package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

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

	public Element createElement(Object group, ReportSettings settings)
	{
		return null;
	}

	public Object getGroup(Directory directory) 
	{
		throw new IllegalStateException(I18n.tr("Grouping directories not possible"));
	}

	public Object getGroup(CvsFile file) 
	{
		throw new IllegalStateException(I18n.tr("Grouping files not possible"));
	}
	
	public Object getGroup(CvsRevision rev) 
	{
		throw new IllegalStateException(I18n.tr("Grouping revisions not possible"));
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
	
	public abstract Iterator getGroups(CvsContent content, ReportSettings settings);
	
	public abstract String getName(Object group);

}
