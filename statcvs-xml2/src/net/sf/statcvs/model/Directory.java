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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents a directory in the module. A container for {@link CvsFile}s.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id$
 */
public abstract class Directory implements Comparable {

	private Collection files = new ArrayList();
	private List directories = new ArrayList();
	private boolean directoriesSorted = true;

	/**
	 * Returns the directory's name without full path or any slashes, e.g. "src"
	 * @return the directory's name
	 */
	public abstract String getName();

	/**
	 * Returns the directory's full path with trailing slash,
	 * for example "src/net/sf/statcvs/"
	 * @return the directory's path
	 */
	public abstract String getPath();

	/**
	 * Returns the directory's parent directory or <tt>null</tt> if it is the root
	 * @return the directory's parent
	 */
	public abstract Directory getParent();

	/**
	 * @return <tt>true</tt> if this is the root of the directory tree
	 */
	public abstract boolean isRoot();
	
	/**
	 * @return the level of this directory in the directory tree.
	 *         0 for the root.
	 */
	public abstract int getDepth();

	/**
	 * Adds a file to this directory
	 * @param file a file in this directory
	 */
	public void addFile(CvsFile file) {
		files.add(file);
	}
	
	/**
	 * Returns all {@link CvsFile} objects in this directory
	 * @return the files in this directory, unordered
	 */
	public Collection getFiles() {
		return files;
	}
	
	/**
	 * Returns all {@link CvsRevision}s to files in
	 * this directory, in order from oldest to most recent.
	 * @return list of <tt>CvsRevision</tt>s for this directory
	 */
	public SortedSet getRevisions() {
		SortedSet result = new TreeSet();
		Iterator iterator = files.iterator();
		while (iterator.hasNext()) {
			CvsFile file = (CvsFile) iterator.next();
			result.addAll(file.getRevisions());
		}
		return result;		
	}

	/**
	 * Adds a subdirectory to this directory.
	 * @param dir an immediate subdirectory 
	 */
	public void addSubdirectory(Directory dir) {
		directories.add(dir);
		directoriesSorted = false;
	}

	/**
	 * Returns a collection of all immediate subdirectories
	 * @return collection of {@link Directory} objects
	 */
	public Collection getSubdirectories() {
		return directories;
	}

	/**
	 * Returns a list of all subdirectories, including their subdirectories
	 * and this directory itself. The list is preordered, beginning with this
	 * directory itself.
	 * @return list of {@link Directory} objects
	 */
	public List getSubdirectoriesRecursive() {
		sortDirectories();
		List result = new ArrayList();
		result.add(this);
		Iterator it = directories.iterator();
		while (it.hasNext()) {
			Directory dir = (Directory) it.next();
			result.addAll(dir.getSubdirectoriesRecursive());
		}
		return result;
	}

	/**
	 * Returns the number of code lines in this directory. The returned number
	 * will be for the current revisions of all files.
	 * TODO: Write tests!
	 * @return LOC in this directory
	 */
	public int getCurrentLOC() {
		int result = 0;
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			result += file.getCurrentLinesOfCode();
		}
		return result;
	}

	/**
	 * Returns the number of files in this directory. Deleted files are not
	 * counted.
	 * TODO: Write tests!
	 * @return number of files in this directory
	 */
	public int getCurrentFileCount() {
		int result = 0;
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			if (!file.isDead()) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Returns <code>true</code> if all files in this directory and its
	 * subdirectories are deleted, or if it doesn't have any files and
	 * subdirectories at all.
	 * TODO: Write tests!
	 * @return <code>true</code> if the directory is currently empty
	 */
	public boolean isEmpty() {
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			if (!file.isDead()) {
				return false;
			}
		}
		it = directories.iterator();
		while (it.hasNext()) {
			Directory subdir = (Directory) it.next();
			if (!subdir.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private void sortDirectories() {
		if (!directoriesSorted) {
			Collections.sort(directories);
			directoriesSorted = true;
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return getPath().compareTo(((Directory) o).getPath());
	}
}