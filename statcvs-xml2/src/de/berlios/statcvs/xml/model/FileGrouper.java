package de.berlios.statcvs.xml.model;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.output.WebRepositoryIntegration;

import org.jdom.Element;

import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.output.ReportSettings;

/**
 * @author Steffen Pingel
 */
public class FileGrouper extends Grouper {

	public FileGrouper()
	{
		super("file", I18n.tr("File"));
	}

	public Element createElement(Object group, ReportSettings settings)
	{
		CvsFile file = (CvsFile)group;
		Element element = new Element("file");
		element.setAttribute("name", file.getFilenameWithPath());
			
		WebRepositoryIntegration webRepository = settings.getWebRepository();
		if (webRepository != null) {
			element.setAttribute("url", webRepository.getFileViewUrl(file));				
		}
		return element;
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
	public Iterator getGroups(CvsContent content, ReportSettings settings)
	{
		return settings.getFileIterator(content);
	}

	/**
	 *  @see de.berlios.statcvs.xml.model.Grouper#getValue(java.lang.Object)
	 */
	public String getName(Object group) 
	{
		return ((CvsFile)group).getFilenameWithPath();
	}

}
