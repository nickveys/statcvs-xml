/*
	StatCvs - CVS statistics generation 
	Copyright (C) 2002  Lukasz Pekacki <lukasz@pekacki.de>
	http://statcvs.sf.net/
    
	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
    
	$RCSfile$
	$Date$
*/
package net.sf.statcvs.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents an author of one or more revisions in a repository.
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id$
 */
public class Author implements Comparable {
	private String name;
	private SortedSet revisions = new TreeSet();
	private Set directories = new HashSet();

	/**
	 * Creates a new author
	 * @param name the author's name
	 */
	public Author(String name) {
		this.name = name;
	}

	/**
	 * adds a revision for this author; called by {@link CvsRevision} constructor
	 * @param revision a revision committed by this author
	 */
	protected void addRevision(CvsRevision revision) {
		revisions.add(revision);
		directories.add(revision.getFile().getDirectory());
	}
	
	/**
	 * @return the author's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a list of {@link CvsRevision}s for this author
	 * @return all revisions of this author, sorted from oldest to newest
	 */
	public SortedSet getRevisions() {
		return revisions;
	}

	/**
	 * Returns a Set of all {@link Directory}s the author has ever committed a
	 * change to
	 * @return directories as a Set of <tt>Directory</tt> objects
	 */
	public Collection getDirectories() {
		return directories;
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return name.compareTo(((Author) o).getName());
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name + "(" + revisions.size() + " revisions)";
	}
}