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

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Represents one versioned file in the {@link CvsContent Repository},
 * including its name, {@link Directory} and {@link CvsRevision} list.
 *
 * TODO: Rename class to something like VersionedFile, getCurrentLinesOfCode() to getCurrentLines(), maybe getFilenameXXX, isDead() to isDeleted()
 *  
 * @author Manuel Schulze
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id$
 */
public class CvsFile implements Comparable {
	private final String filename;
	private final SortedSet revisions = new TreeSet();
	private final Directory directory;
	private final Set authors = new HashSet();

	/**
	 * Creates a CvsFile object.
	 * 
	 * @param name The full name of the file
	 * @param directory the directory where the file resides
	 */
	public CvsFile(String name, Directory directory) {
		this.filename = name;
		this.directory = directory;
		if (directory != null) {
			directory.addFile(this);
		}
	}

	/**
	 * Adds a revision to this file. Revisions can be added in any order.
	 * @param revision a revision of this file
	 */
	protected void addRevision(CvsRevision revision) {
		revisions.add(revision);
		if (revision.getAuthor() != null) {
			authors.add(revision.getAuthor());
		}
	}

	/**
	 * Returns the full filename.
	 * @return the full filename
	 */
	public String getFilenameWithPath() {
		return filename;
	}

	/**
	 * Returns the filename without path.
	 * @return the filename without path
	 */
	public String getFilename () {
		int lastDelim = this.filename.lastIndexOf("/");
		return this.filename.substring(lastDelim + 1, this.filename.length());
	}

	/**
	 * Returns the file's <tt>Directory</tt>.
	 * @return the file's <tt>Directory</tt>
	 */
	public Directory getDirectory() {
		return directory;
	}

	/**
	 * Gets the latest revision of this file.
	 * @return the latest revision of this file
	 */
	public CvsRevision getLatestRevision() {
		return (CvsRevision) this.revisions.last();
	}

	/**
	 * Gets the earliest revision of this file.
	 * @return the earliest revision of this file
	 */
	public CvsRevision getInitialRevision() {
		return (CvsRevision) this.revisions.first();
	}

	/**
	 * Returns the list of {@link CvsRevision}s of this file,
	 * sorted from earliest to most recent.
	 * @return a <tt>SortedSet</tt> of {@link CvsRevision}s
	 */
	public SortedSet getRevisions() {
		return this.revisions;
	}

	/**
	 * Returns the current number of lines for this file. Binary files
	 * and deleted files are assumed to have 0 lines.
	 * @return the current number of lines for this file
	 */
	public int getCurrentLinesOfCode() {
		return getLatestRevision().getLines();
	}
	
	/**
	 * Returns <code>true</code> if the latest revision of this file was
	 * a deletion.
	 * @return <code>true</code> if this file is deleted
	 */
	public boolean isDead() {
		return getLatestRevision().isDead();
	}
	
	/**
	 * Returns true, if <code>author</code> worked on this file.
	 * @param author The <code>Author</code> to search for
	 * @return <code>true</code>, if the author is listed in one of
	 * this file's revisions
	 */
	public boolean hasAuthor(Author author) {
		return authors.contains(author);
	}
	
	/**
	 * Returns the revision which was replaced by the revision given as
	 * argument. Returns <tt>null</tt> if the given revision is the initial
	 * revision of this file.
	 * @param revision a revision of this file
	 * @return this revision's predecessor
	 */
	public CvsRevision getPreviousRevision(CvsRevision revision) {
		if (!revisions.contains(revision)) {
			throw new IllegalArgumentException("revision not containted in file");
		}
		SortedSet headSet = revisions.headSet(revision);
		if (headSet.isEmpty()) {
			return null;
		}
		return (CvsRevision) headSet.last();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return getFilenameWithPath() + " (" + revisions.size() + " revisions)";
	}
	
	/**
	 * Compares this file to another one, based on filename.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object other) {
		return filename.compareTo(((CvsFile) other).filename);
	}

}