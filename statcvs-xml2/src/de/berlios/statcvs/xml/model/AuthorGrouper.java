package de.berlios.statcvs.xml.model;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsRevision;

/**
 * @author Steffen Pingel
 */
public class AuthorGrouper extends Grouper {

	public AuthorGrouper()
	{
		super("author");
	}

	public Object getGroup(CvsRevision rev) 
	{
		return rev.getAuthor();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getValue(Object group) 
	{
		return ((Author)group).getName();
	}

}
