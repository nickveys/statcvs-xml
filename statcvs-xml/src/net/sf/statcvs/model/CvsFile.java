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
    
	$RCSfile: CvsFile.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Iterator;
import java.util.List;

/**
 * Represents the information about one file in the
 * source repository.
 * 
 * @author Manuel Schulze
 * @author Richard Cyganiak
 * @version $Id: CvsFile.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CvsFile {
	private String workingname;
	private boolean isInAttic;
	private boolean isBinary;
	private List revisions;
	private Directory directory;

	/**
	 * Creates a CvsFile object.
	 * 
	 * @param workingname The name of the file
	 * @param directory the directory where the file resides
	 * @param revisions the list of {@link CvsRevision}s of this file,
	 * from latest to oldest
	 * @param isBinary <tt>true</tt> if it's a binary file
	 * @param isInAttic <tt>true</tt> iff the file is dead on the main branch
	 */
	public CvsFile(String workingname, Directory directory,
			List revisions, boolean isBinary, boolean isInAttic) {

		this.workingname = workingname;
		this.directory = directory;
		this.isBinary = isBinary;
		this.isInAttic = isInAttic;
		directory.addFile(this);
		setRevisions(revisions);
	}

	/**
	 * Returns the workingname.
	 * @return String
	 */
	public String getFilenameWithPath() {
		return workingname;
	}

	/**
	 * Returns the filename.
	 * @return The filename.
	 */
	public String getFilename () {
		int lastDelim = this.workingname.lastIndexOf("/");
		return this.workingname.substring(lastDelim + 1, this.workingname.length());
	}

	/**
	 * @return the file's <tt>Directory</tt>
	 */
	public Directory getDirectory() {
		return directory;
	}

	/**
	 * Gets the latest revision of this file.
	 * 
	 * @return the latest revision of this file
	 */
	public CvsRevision getLatestRevision() {
		return (CvsRevision) this.revisions.get(0);
	}

	/**
	 * Gets the earliest revision of this file.
	 * 
	 * @return the latest revision of this file
	 */
	public CvsRevision getInitialRevision() {
		return (CvsRevision) this.revisions.get(revisions.size() - 1);
	}

	private void setRevisions(List revisions) {
	 	if (revisions.isEmpty()) {
	 		throw new IllegalArgumentException("revision list must not be empty");
	 	}
		this.revisions = revisions;
		Iterator it = getRevisionIterator();
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			rev.setCvsFile(this);
		}
	}

	/**
	 * Returns the list of {@link CvsRevision}s of this file, from latest
	 * to oldest.
	 * @return A list which contains the revisions.
	 */
	public List getRevisions() {
		return this.revisions;
	}

	/**
	 * Returns the number of code lines for this file. 0 will be returned
	 * for binary files and for files that are deleted.
	 * 
	 * @return the number of code lines for this file.
	 */
	public int getCurrentLinesOfCode() {
		return getLatestRevision().getEffectiveLinesOfCode();
	}
	
	/**
	 * Returns an iterator over of revisions in this file.
	 * 
	 * @return An itertor over this files revisions
	 */
	public Iterator getRevisionIterator() {
		return revisions.iterator();
	}

	/**
	 * Returns <code>true</code> if the latest revision has state dead.
	 * 
	 * @return <code>True</code>, if this file is deleted in the repository
	 */
	public boolean isDead() {
		return getLatestRevision().isDead();
	}
	
	/**
	 * Returns <code>true</code> if the file is checked in as a binary file.
	 * 
	 * @return <code>True</code>, if this file is a binary file
	 */
	public boolean isBinary() {
		return isBinary;
	}

	/**
	 * Returns true, if <code>author</code> worked on this file.
	 * 
	 * @param author The <code>Author</code> to search for
	 * @return <code>true</code>, if the author is listed in one of
	 * this file's revisions
	 */
	public boolean hasAuthor(Author author) {
		Iterator it = this.revisions.iterator();
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			if (rev.getAuthor().equals(author)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns <tt>true</tt> if the file is in the Attic.
	 * @return <tt>true</tt> if the file is in the Attic.
	 */
	public boolean isInAttic() {
		return isInAttic;
	}

	/**
	 * Returns the revision which was replaced by the revision given as
	 * argument. Returns <tt>null</tt> if the given revision is the initial
	 * revision of this file.
	 * @param revision a revision of this file
	 * @return this revision's predecessor
	 */
	public CvsRevision getPreviousRevision(CvsRevision revision) {
		Iterator it = getRevisionIterator();
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			if (rev.equals(revision)) {
				if (!it.hasNext()) {
					return null;
				} else {
					return (CvsRevision) it.next();
				}
			}
		}
		throw new IllegalArgumentException("revision was not part of this file");
	}

	/**
	 * Returns a string representation of this objects content.
	 * 
	 * @return String representation
	 */
	public String toString() {
		return "Working Name         : "
			+ this.workingname
			+ "\n"
			+ "Revisions            : "
			+ this.revisions;
	}
}
