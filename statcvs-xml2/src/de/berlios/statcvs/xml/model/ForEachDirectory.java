/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
 
package de.berlios.statcvs.xml.model;

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
