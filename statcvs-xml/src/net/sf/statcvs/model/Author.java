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
    
	$RCSfile: Author.java,v $
	$Date: 2003-06-17 16:43:02 $
*/
package net.sf.statcvs.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents an author of one or more revisions in a repository.
 * 
 * TODO: Write tests!
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: Author.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class Author implements Comparable {

	private String name;
	private List revisions = new ArrayList();
	private boolean revisionsSorted = true;

	/**
	 * Creates a new author
	 * @param name the author's name
	 */
	public Author(String name) {
		this.name = name;
	}

	/**
	 * @return the author's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * adds a revision for this author
	 * @param revision a revision committed by this author
	 */
	public void addRevision(CvsRevision revision) {
		revisions.add(revision);
		revisionsSorted = false;
	}
	
	/**
	 * Returns a <tt>RevisionIterator</tt> for this author
	 * @return all revisions of this author, sorted from oldest to newest
	 */
	public RevisionIterator getRevisionIterator() {
		if (!revisionsSorted) {
			Collections.sort(revisions, new RevisionDateComparator());
			revisionsSorted = true;
		}
		return new ListRevisionIterator(revisions);
	}

	/**
	 * Returns a Set of all {@link Directory}s the author has ever committed a
	 * change to
	 * @return directories as a Set of <tt>Directory</tt> objects
	 */
	public Collection getDirectories() {
		Set result = new HashSet();
		Iterator it = revisions.iterator();
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			CvsFile file = rev.getFile();
			Directory dir = file.getDirectory();
			result.add(dir);
		}
		return result;
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
		return name + "(" + revisions.size() + ")";
	}
}