package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsRevision;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public class AuthorGrouper extends Grouper {

	public AuthorGrouper()
	{
		super("author", I18n.tr("Author"));
	}

	public Object getGroup(CvsRevision rev) 
	{
		return rev.getAuthor();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups()
	 */
	public Iterator getGroups(CvsContent content, ReportSettings settings) 
	{
		return content.getAuthors().iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return ((Author)group).getName();
	}

}
