package de.berlios.statcvs.xml.output;

import java.util.Iterator;

import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.Directory;

/**
 * @directory Steffen Pingel
 */
public class ForEachDirectory extends ForEachObject {

	private Directory directory;

	/**
	 * @param object
	 * @param id
	 */
	public ForEachDirectory(Directory directory) 
	{
		super(directory, directory.getPath());

		this.directory = directory;
	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getDirectoryIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getDirectoryIterator(CvsContent content) 
	{
		return directory.getSubdirectoriesRecursive().iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getFileIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getFileIterator(CvsContent content) 
	{
		return directory.getFiles().iterator();
	}

	/**
	 *  @see de.berlios.statcvs.xml.output.ForEachObject#getRevisionIterator(net.sf.statcvs.model.CvsContent)
	 */
	public Iterator getRevisionIterator(CvsContent content) 
	{
		return directory.getRevisions().iterator();
	}

}
