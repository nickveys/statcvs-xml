package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public class DirectoryGrouper extends Grouper {

	public DirectoryGrouper()
	{
		super("directory", I18n.tr("Directory"));
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.CvsFile)
	 */
	public Object getGroup(CvsFile file) 
	{
		return file.getDirectory(); 
	}

	public Object getGroup(CvsRevision rev) 
	{
		return rev.getFile().getDirectory();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.Directory)
	 */
	public Object getGroup(Directory directory) 
	{
		return directory;
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getGroups(CvsContent content, ReportSettings settings) 
	{
		return settings.getDirectoryIterator(content);
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return ((Directory)group).getPath();
	}

}
