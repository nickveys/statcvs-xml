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
*/
package net.sf.statcvs.model;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents an author of one or more {@link CvsRevision}s in a repository.
 * 
 * TODO: Rename to <tt>Login</tt>
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id$
 */
public class Author implements Comparable {
	private final String name;
	private final SortedSet revisions = new TreeSet();
	private final SortedSet directories = new TreeSet();

	/**
	 * Creates a new author.
	 * @param name the author's login name
	 */
	public Author(String name) {
		this.name = name;
	}

	/**
	 * Adds a revision for this author; called by {@link CvsRevision} constructor
	 * @param revision a revision committed by this author
	 */
	protected void addRevision(CvsRevision revision) {
		revisions.add(revision);
		directories.add(revision.getFile().getDirectory());
	}
	
	/**
	 * Returns the author's login name.
	 * @return the author's login name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns all {@link CvsRevision}s of this author, sorted from oldest
	 * to most recent.
	 * @return all revisions of this author
	 */
	public SortedSet getRevisions() {
		return revisions;
	}

	/**
	 * Returns all {@link Directory}s the author
	 * has committed a change to, sorted by name.
	 * @return a set of <tt>Directory</tt> objects
	 */
	public SortedSet getDirectories() {
		return directories;
	}

	/**
	 * Compares the instance to another author, using their login names.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return name.compareTo(((Author) o).getName());
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return name + "(" + revisions.size() + " revisions)";
	}
}