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

import java.util.*;
import net.sf.statcvs.util.CvsLogUtils;

/**
 * Represents one versioned file in the {@link CvsContent Repository},
 * including its name, {@link Directory} and {@link CvsRevision} list.
 * Revisions can be created using the <tt>addXXXRevision</tt> factory
 * methods. Revisions can be created in any order.
 * 
 * TODO: Rename class to something like VersionedFile, getCurrentLinesOfCode() to getCurrentLines(), maybe getFilenameXXX, isDead() to isDeleted()
 *  
 * @author Manuel Schulze
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id$
 */
public class CvsFile implements Comparable {
	private final String filename;
	private final SortedSet revisions;
	private final Directory directory;
	private final Set authors = new HashSet();
    private final Map revisionsByBranchName = new HashMap();

	/**
	 * Creates a CvsFile object.
	 * 
	 * @param name The full name of the file
	 * @param directory the directory where the file resides
     * @param cvsBranches a map of Cvs Branches this file appears on, with the names of the branches as keys
	 */
	public CvsFile(String name, Directory directory) {
		this.filename = name;
		this.directory = directory;
		if (directory != null) {
			directory.addFile(this);
		}
		revisions = new TreeSet();
		revisionsByBranchName.put(CvsLogUtils.HEAD_BRANCH_NAME, revisions);
	}

	public boolean existsOnBranch(String branchName) {
		final TreeSet branchRevisions = (TreeSet)revisionsByBranchName.get(branchName);
		return (branchRevisions != null && !branchRevisions.isEmpty());
	}
	
	/**
	 * Returns a list of authors that have commited at least one revision of the file.
	 * @return a list of authors
	 */
	public Set getAuthors()
	{
		return authors;
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
	 * Gets the latest revision of this file on HEAD.
	 * @return the latest revision of this file
	 */
	public CvsRevision getLatestRevision() {
		return (CvsRevision) this.revisions.last();
	}

	/**
	 * Gets the latest revision of this file on the named branch.
     * @param branchName the name of the branch
	 * @return the latest revision of this file on the named branch
	 */
    public CvsRevision getLatestRevision(String branchName)
    {
		return (CvsRevision)getRevisions(branchName).last();
    }

	/**
	 * Gets the earliest revision of this file on HEAD.
	 * @return the earliest revision of this file
	 */
	public CvsRevision getInitialRevision() {
		return (CvsRevision) this.revisions.first();
	}

	/**
	 * Gets the earliest revision of this file on the named branch.
     * @param branchName the name of a branch.
	 * @return the earliest revision of this file on the named branch
	 */
	public CvsRevision getInitialRevision(String branchName) {
        return (CvsRevision)getRevisions(branchName).first();
	}

	/**
	 * Returns the list of {@link CvsRevision}s of this file,
	 * sorted from earliest to most recent (by date) across all branches.
	 * @return a <tt>SortedSet</tt> of {@link CvsRevision}s
	 */
	public SortedSet getRevisions() {
		return this.revisions;
	}

	public SortedSet getAllRevisions() {
		SortedSet allRevisions = new TreeSet();
		for (Iterator it = revisionsByBranchName.values().iterator(); it.hasNext();) {
			SortedSet branchRevisions = (SortedSet)it.next();
			allRevisions.addAll(branchRevisions);
		}
		return allRevisions;
	}

	/**
	 * Returns the list of {@link CvsRevision}s of this file on the named
	 * branch, sorted from earliest to most recent (by date) across all
	 * branches.
	 * 
	 * @return a <tt>SortedSet</tt> of {@link CvsRevision}s
	 */
	public SortedSet getRevisions(String branchName) {
        final TreeSet branchRevisions = (TreeSet)revisionsByBranchName.get(branchName);
        if (branchRevisions == null)
            throw new IllegalArgumentException("Cannot find branch with name '" + branchName + "'.");
		return branchRevisions;
	}

	/**
	 * Returns the current number of lines for this file on HEAD. Binary files
	 * and deleted files are assumed to have 0 lines.
	 * @return the current number of lines for this file
	 */
	public int getCurrentLinesOfCode() {
		return getLatestRevision().getLines();
	}
	
	/**
	 * Returns the current number of lines for this file on a named branch.
     * Binary filesand deleted files are assumed to have 0 lines.
     * @param branchName the name of the branch
	 * @return the current number of lines for this file on the named branch
	 */
	public int getCurrentLinesOfCode(String branchName) {
		return getLatestRevision(branchName).getLines();
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
	 * Returns <code>true</code> if the latest revision of this file on the branch was
	 * a deletion.
	 * @return <code>true</code> if this file is deleted
	 */
    public boolean isDead(String branchName) {
        return getLatestRevision(branchName).isDead();
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
		SortedSet revisions = getRevisions(revision.getMainBranch().getName());
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

	/**
	 * Adds an initial revision to the file. An initial revision is either
	 * the first revision of the file, or a re-add after the file was
	 * deleted.
	 * @param revisionNumber the revision number, for example "1.1"
	 * @param author the login from which the change was committed
	 * @param date the time when the change was committed
	 * @param comment the commit message
	 * @param lines the number of lines of the new file
	 */
	public CvsRevision addInitialRevision(CvsBranch mainBranch, String revisionNumber, Author author,
									Date date, String comment, int lines, SortedSet symbolicNames) {
		CvsRevision result = new CvsRevision(this, mainBranch, revisionNumber,
				CvsRevision.TYPE_CREATION, author, date, comment,
				lines, lines, 0, symbolicNames);
		addRevision(result);
		return result;
	}
	
	/**
	 * Adds a change revision to the file.
	 * @param revisionNumber the revision number, for example "1.1"
	 * @param author the login from which the change was committed
	 * @param date the time when the change was committed
	 * @param comment the commit message
	 * @param lines the number of lines in the file after the change
	 * @param linesDelta the change in the number of lines
	 * @param replacedLines number of lines that were removed and replaced by others
	 */
	public CvsRevision addChangeRevision(CvsBranch mainBranch, String revisionNumber, Author author,
								  Date date, String comment, int lines,
								  int linesDelta, int replacedLines, SortedSet symbolicNames) {
		CvsRevision result = new CvsRevision(this, mainBranch, revisionNumber,
				CvsRevision.TYPE_CHANGE, author, date, comment,
				lines, linesDelta, replacedLines, symbolicNames);
		addRevision(result);
		return result;
	}

	/**
	 * Adds a deletion revision to the file.
	 * @param revisionNumber the revision number, for example "1.1"
	 * @param author the login from which the change was committed
	 * @param date the time when the change was committed
	 * @param comment the commit message
	 * @param lines the number of lines in the file before it was deleted
	 */
	public CvsRevision addDeletionRevision(CvsBranch mainBranch, String revisionNumber, Author author,
									Date date, String comment, int lines, SortedSet symbolicNames) {
		CvsRevision result = new CvsRevision(this, mainBranch, revisionNumber,
				CvsRevision.TYPE_DELETION, author, date, comment,
				0, -lines, 0, symbolicNames);
		addRevision(result);
		return result;
	}
	
	/**
	 * Adds a "begin of log" revision to the file. This kind of revision
	 * only marks the beginning of the log timespan if the file was
	 * already present in the repository at this time. It is not an actual
	 * revision committed by an author.
	 * @param date the begin of the log
	 * @param lines the number of lines in the file at that time
	 */
	public CvsRevision addBeginOfLogRevision(CvsBranch mainBranch, Date date, int lines, SortedSet symbolicNames) {
		CvsRevision result = new CvsRevision(this, mainBranch, "0.0",
				CvsRevision.TYPE_BEGIN_OF_LOG, null, date, null,
				lines, 0, 0, symbolicNames);
		addRevision(result);
		return result;
	}

	private void addRevision(CvsRevision revision) {
		CvsBranch branch = revision.getMainBranch();
		// TODO review
		TreeSet branchRevisions = (TreeSet)revisionsByBranchName.get(branch.getName());
			//((branch.getName() != null) ? branch.getName() : CvsLogUtils.HEAD_BRANCH_NAME);
		if (branchRevisions == null) {
			branchRevisions = new TreeSet();
			revisionsByBranchName.put(branch.getName(), branchRevisions);
		}
		branchRevisions.add(revision);
		if (revision.getAuthor() != null) {
			authors.add(revision.getAuthor());
		}
	}
}