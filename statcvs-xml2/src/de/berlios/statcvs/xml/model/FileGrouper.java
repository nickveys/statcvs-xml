package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import de.berlios.statcvs.xml.I18n;

/**
 * @author Steffen Pingel
 */
public class FileGrouper extends Grouper {

	public FileGrouper()
	{
		super("file", I18n.tr("File"));
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroup(net.sf.statcvs.model.CvsFile)
	 */
	public Object getGroup(CvsFile file) 
	{
		return file; 
	}

	public Object getGroup(CvsRevision rev) 
	{
		return rev.getFile();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getGroups(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getGroups(CvsContent content) {
		return content.getFiles().iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return ((CvsFile)group).getFilenameWithPath();
	}

}
