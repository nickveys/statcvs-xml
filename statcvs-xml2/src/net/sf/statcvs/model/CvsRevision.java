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
	Created on $Date$ 
*/
package net.sf.statcvs.model;

import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Object which contains information about one revision of a file.
 * 
 * <p>Everytime an author checks his code into a repository the revision
 * number he was working on, is incremented. Revision numbers have an odd
 * number of periods. And on each forked revision an even integer is appended to
 * the original revision (e.g. Rev 1.3 forks to 1.3.2.1 :-). The 0 is not
 * used for revision
 * numbering. It has a special meaning in the cvs branching mechanism. These are
 * the so called &quot;magic branches&quot;.</p>
 *
 * TODO: Replace type code with hierarchy
 * 
 * TODO: give linesAdded, linesRemoved and linesOfCode intuitive
 *       semantics (and not the strange semantics of cvs log)
 *
 * @author Manuel Schulze
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id$
 */
public class CvsRevision implements Comparable {

	/**
	 * Marks a revision that creates a new file. The file did not exist
	 * in the current branch before this revision, and it does exist
	 * afterwards. Possibly the file existed before, that is, it was
	 * deleted and restored. 
	 */
	public static final int TYPE_CREATION = 1;

	/**
	 * Marks a revision that changes the file. It does neither create nor
	 * delete the file.
	 */
	public static final int TYPE_CHANGE = 2;

	/**
	 * Marks a revision that deletes the file. The file existed before, but
	 * does not exist afterwards in the current branch.
	 */
	public static final int TYPE_DELETION = 3;

	/**
	 * Marks a revision at the very beginning of the log timespan. This is
	 * only a container for the number of code lines at the beginning of
	 * the log. It is not a real revision committed by an author.
	 */
	public static final int TYPE_BEGIN_OF_LOG = 5;

	private CvsFile file;
	private String revision;
	private int type;
	private Author author;
	private Date date;
	private String comment;
	private int linesAdded;
	private int linesRemoved;
	private int linesOfCode;
	private SortedSet symbolicNames;
	
	/**
	 * Creates a new revision of a file with the
	 * specified revision number.
	 * @param file CvsFile that belongs to this revision
	 * @param revision revision number, for example "1.1"
	 * @param type a <tt>TYPE_XXX</tt> constant
	 * @param author the author of the revision
	 * @param date the date of the revision
	 * @param comment the author's comment
	 * @param added number of lines added
	 * @param removed number of lines removed
	 * @param currentLOC number of lines in the file; if the revision is a deletion, then number of lines before
     * @param symbolicNames list of symbolic names for this revision or null if this revision has no symbolic names
	 */
	public CvsRevision(CvsFile file, String revision, int type,
			Author author, Date date, String comment, int added, int removed, 
            int currentLOC, SortedSet symbolicNames) {
		this.file = file;
		this.revision = revision;
		this.type = type;
		this.author = author;
		this.date = date;
		this.comment = comment;
		this.linesAdded = added;
		this.linesRemoved = removed;
		this.linesOfCode = currentLOC;
        this.symbolicNames = symbolicNames;
        
		if (file != null) {
			file.addRevision(this);
		}
		if (author != null) {
			author.addRevision(this);
		}
        
        if (symbolicNames != null) {
            Iterator it = symbolicNames.iterator();
            while (it.hasNext()) {
                ((SymbolicName)it.next()).addRevision(this);
            }
        }
	}

	/**
	 * Returns the revision number.
	 * @return String
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * Returns the author
	 * @return the author of this revision
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * Returns the comment.
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Returns the date.
	 * @return Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Returns the number of added lines.
	 * @return int
	 */
	public int getLinesAdded() {
		if (file.isBinary()) {
			return 0;
		}
		return linesAdded;
	}

	/**
	 * Returns the number of removed lines.
	 * @return int
	 */
	public int getLinesRemoved() {
		if (file.isBinary()) {
			return 0;
		}
		return linesRemoved;
	}

	/**
	 * Returns the lines of code value for this revision. This is the
	 * number of code lines the file contained in this revision, or
	 * 0 for binary files. Deleted files still keep their lines of
	 * code value.
	 * 
	 * @return the number of code lines
	 */
	public int getLinesOfCode() {
		return linesOfCode;
	}

	/**
	 * Returns the lines of code value for this revision. This is the
	 * number of code lines the file contained in this revision, or
	 * 0 for binary files and dead files.
	 * 
	 * @return the number of code lines
	 */
	public int getEffectiveLinesOfCode() {
		if (isDead()) {
			return 0;
		}
		return linesOfCode;
	}

	/**
	 * Returns by how many lines the line count changed with this
	 * revision. Deletions return -<code>getLinesOfCode()</code>,
	 * re-adds and initial revisions return <code>getLinesOfCode()</code>.
	 * 
	 * @return the line count change of this revision
	 */
	public int getLinesOfCodeChange() {
		if (isInitialRevision()) {
			return getLinesOfCode();
		}
		if (isDead()) {
			return -getLinesOfCode();
		}
		return getLinesAdded() - getLinesRemoved();
	}

	/**
	 * Returns the change of the file count caused by this revision.
	 * This is 1 for initial revisions and re-adds, -1 for deletions,
	 * and 0 for normal revisions.
	 * @return the file count change of this revision
	 */
	public int getFileCountChange() {
		if (isInitialRevision()) {
			return 1;
		} else if (isDead()) {
			return -1;
		} else {
			return 0;
		}
	}
				
	/**
	 * Returns <code>true</code> if this is the first revision for
	 * @return <code>true</code> if this is the first revision for
	 * this file.
	 */
	public boolean isInitialRevision() {
		return type == TYPE_CREATION;
	}

	/**
	 * @return <code>true</code> if the file is deleted in this revision
	 */
	public boolean isDead() {
		return type == TYPE_DELETION;
	}

	/**
	 * @return <code>true</code> if this revision exists
	 * only for StatCvs bookkeeping purposes
	 */
	public boolean isBeginOfLog() {
		return type == TYPE_BEGIN_OF_LOG;
	}

	/**
	 * Returns a string representation of this objects content.
	 * @return String representation
	 */
	public String toString() {
		return this.author.getName() + " - " + this.revision;
	}

	/**
	 * Returns the {@link CvsFile} object of this revision.
	 * @return the {@link CvsFile} object of this revision.
	 */
	public CvsFile getFile() {
		return file;
	}

	/**
	 * Returns the lines of code value of this revision. This is the sum
	 * of lines changed and added in this revision.
	 * @return lines changed or added
	 */
	public int getLineValue() {
		if (file.isBinary()) {
			return 0;
		}
		if (isInitialRevision()) {
			return getLinesOfCode();
		}
		return getLinesAdded();
	}
	
	/**
	 * Returns the lines of code removing value of this revision.
	 * This is the sum of lines changed and deleted in this revision.
	 * TODO: Write test case for this and getLineValue() for the case
	 * that a file is deleted and re-added
	 * @return lines changed or deleted
	 */
	public int getRemovingValue() {
		if (file.isBinary()) {
			return 0;
		}
		if (isDead()) {
			return getLinesOfCode();
		}
		return getLinesRemoved();
	}
	
	/**
	 * Returns the predecessor of this revision or <tt>null</tt> if it
	 * is the first revision.
	 * @return the predecessor of this revision
	 */
	public CvsRevision getPreviousRevision() {
		return file.getPreviousRevision(this);
	}

	/**
	 * Returns a list of {@link SymbolicName}s of this revision or null if
	 * the revision has no symbolic names. The list is ordered from 
	 * latest to oldest.
     *
	 * @return list of symbolic names 
	 */
	public SortedSet getSymbolicNames()
	{
		return symbolicNames;		
	}
	
	/**
	 * Compares this revision to another revision. A revision is considered
	 * smaller if its date is smaller. If the dates are identical, the filename,
	 * author name, revision number and comment will be used to break the tie.
	 */
	public int compareTo(Object other) {
		if (this == other) {
			return 0;
		}
		CvsRevision otherRevision = (CvsRevision) other;
		int result = date.compareTo(otherRevision.getDate());
		if (result != 0) {
			return result;
		}
		result = file.getFilenameWithPath().compareTo(otherRevision.getFile().getFilenameWithPath());
		if (result != 0) {
			return result;
		}
		result = revision.compareTo(otherRevision.getRevision());
		if (result != 0) {
			return result;
		}
		if (author != null && otherRevision.getAuthor() != null) {
			result = author.compareTo(otherRevision.getAuthor());
			if (result != 0) {
				return result;
			}
		}
		if (comment != null && otherRevision.getComment() != null) {
			return comment.compareTo(otherRevision.getComment());
		}
		return 1;
	}
}