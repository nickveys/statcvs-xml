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
    
	$RCSfile: CvsRevision.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Date;

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
 * TODO: Make this class immutable (no setters, fat constructor)
 *       and give linesAdded, linesRemoved and linesOfCode intuitive
 *       semantics (and not the strange semantics of cvs log)
 *   
 * @author Manuel Schulze
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: CvsRevision.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CvsRevision {

	/**
	 * state constant which marks an initial release of a file
	 */
	public static final int STATE_INITIAL_REVISION = 1;

	/**
	 * state constant for normal revisions
	 */
	public static final int STATE_NORMAL = 2;

	/**
	 * state constant for dead (deleted) revisions
	 */
	public static final int STATE_DEAD = 3;

	/**
	 * state constant which marks a re-add of a previously deleted file
	 */
	public static final int STATE_RE_ADDED = 4;
	
	private int state = STATE_NORMAL;
	private String revision = null;
	private Date date = null;
	private Author author = null;
	private int linesAdded = 0;
	private int linesRemoved = 0;
	private int linesOfCode = 0;
	private String comment = null;
	private CvsFile file = null;

	/**
	 * Creates a new revision of a file with the
	 * specified revision number.
	 * @param revision revision number, for example "1.1"
	 */
	public CvsRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * Sets the {@link CvsFile} which the revision belongs to.
	 * Called by {@link CvsFile#CvsFile}.
	 * @param file CvsFile that belongs to this revision
	 */
	protected void setCvsFile(CvsFile file) {
		this.file = file;
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
	 * Sets the state flag of this revision. May be one of the
	 * <code>STATE_XXX</code> constants.
	 * 
	 * @param state the state of this revision
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Sets the author.
	 * @param author The author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Sets the date.
	 * @param date The date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Sets the number of added lines.
	 * @param linesadded The linesadded to set
	 */
	public void setLinesAdded(int linesadded) {
		this.linesAdded = linesadded;
	}

	/**
	 * Sets the number of removed lines.
	 * @param linesremoved The linesremoved to set
	 */
	public void setLinesRemoved(int linesremoved) {
		this.linesRemoved = linesremoved;
	}

	/**
	 * Sets the lines of code value for this revision. This is the
	 * number of code lines the file contained in this revision, or
	 * 0 for binary files. Deleted files still keep their lines of
	 * code value.
	 * 
	 * @param linesOfCode the number of code lines
	 */
	public void setLinesOfCode(int linesOfCode) {
		this.linesOfCode = linesOfCode;
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
		if (isInitialRevision() || isReAdd()) {
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
		if (isInitialRevision() || isReAdd()) {
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
		return state == STATE_INITIAL_REVISION;
	}

	/**
	 * Returns TRUE if Revision is re-added, FALSE otherwise
	 * @return boolean TRUE if Revision is re-added, FALSE otherwise
	 */
	public boolean isReAdd() {
		return state == STATE_RE_ADDED;
	}

	/**
	 * @return <code>true</code> if the file is deleted in this revision
	 */
	public boolean isDead() {
		return state == STATE_DEAD;
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
}