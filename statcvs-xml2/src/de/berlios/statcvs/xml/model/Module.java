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

import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.util.FilePatternMatcher;

/**
 * @author Steffen Pingel
 */
public class Module implements Comparable {

	private String name;
	private FilePatternMatcher matcher;
	private Directory directory;
	private SortedSet revisions = new TreeSet();

	/**
	 * @param string
	 * @param dir
	 */
	public Module(Directory directory) 
	{
		this(directory.getPath());
		
		this.directory = directory;
	}

	public Module(String name, String pattern)
	{
		this(name);
		
		matcher = new FilePatternMatcher(pattern);
	}

	public Module(String name)
	{
		this.name = name;
	}

	public int compareTo(Object o) 
	{
		return getName().compareTo(((Module)o).getName());
	}
	/**
	 * @return
	 */
	public String getName() 
	{
		return name;
	}

	public SortedSet getRevisions()
	{
		return revisions;
	}

	public boolean matches(CvsRevision rev)
	{
		if (directory != null && directory != rev.getFile().getDirectory()) {
			return false;
		}
		if (matcher != null) {
			return matcher.matches(rev.getFile().getFilenameWithPath());
		}
		return true;
	}

	/**
	 * @param rev
	 */
	public void addRevision(CvsRevision rev) 
	{
		revisions.add(rev);
	}

}
