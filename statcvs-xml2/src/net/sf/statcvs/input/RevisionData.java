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
package net.sf.statcvs.input;

import java.util.Date;

import net.sf.statcvs.util.CvsLogUtils;

/**
 * Container for all information contained in one CVS revision
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id$
 */
public class RevisionData {
	private String revision;
	private Date date;
	private String authorName;
	private boolean stateExp = false;
	private boolean stateDead = false;
	private boolean lineCount = false;
	private int linesAdded;
	private int linesRemoved;
	private String comment;

	/**
	 * @return Returns the authorName.
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @param authorName The authorName to set.
	 */
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date The date to set.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return Returns the linesAdded.
	 */
	public int getLinesAdded() {
		return linesAdded;
	}

	/**
	 * @return Returns the linesRemoved.
	 */
	public int getLinesRemoved() {
		return linesRemoved;
	}

	/**
	 * @return
	 */
	public boolean hasNoLines() {
		return !lineCount;
	}

	/**
	 * @param linesRemoved The linesRemoved to set.
	 */
	public void setLines(int added, int removed) {
		this.linesAdded = added;
		this.linesRemoved = removed;
		lineCount = true;
	}

	/**
	 * @return Returns the revision.
	 */
	public String getRevision() {
		return revision;
	}

	/**
	 * @param revision The revision to set.
	 */
	public void setRevision(String revision) {
		this.revision = revision;
	}

	public void setStateDead() {
		stateDead = true;
	}

	public void setStateExp() {
		stateExp = true;
	}

	/**
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Returns <tt>true</tt> if this revision marks the adding of a new file
	 * on a subbranch. CVS creates a dead 1.1 revision on the trunk even if
	 * the file never gets merged into the trunk. If we evaluate the trunk,
	 * and the file doesn't have any other revisions on the trunk, then we
	 * ignore this revision.
	 *  
	 * @return <tt>true</tt> if this is the adding of a new file on a subbranch
	 */
	public boolean isAddOnSubbranch() {
		return stateDead && revision.equals("1.1");
	}
	
	/**
	 * Returns <tt>true</tt> if this revision is the removal of a file.
	 * Any dead revision means that the file was removed. The only exception
	 * is a dead 1.1 revision, which is an add on a subbranch.
	 * 
	 * @return <tt>true</tt> if this revision deletes the file.
	 * @see #isAddOnSubbranch
	 */
	public boolean isDeletion() {
		return stateDead && !revision.equals("1.1");
	}
	
	/**
	 * Returns <tt>true</tt> if this revision is a normal change, or if it
	 * restores a removed file. The distinction between these two cases
	 * can be made by looking at the previous (in time, not log order) revision.
	 * If it was a deletion, then this revision is a restore.
	 * 
	 * @return <tt>true</tt> if this is a normal change or a restore.
	 */
	public boolean isChangeOrRestore() {
		return stateExp && lineCount;
	}
	
	/**
	 * Returns <tt>true</tt> if this revision is the creation of a new file.
	 * 
	 * @return <tt>true</tt> if this is the creation of a new file.
	 */
	public boolean isCreation() {
		return stateExp && !lineCount;
	}

	/**
	 * Returns <tt>true</tt> if this revision is on the main branch.
	 * 
	 * @return <tt>true</tt> if this revision is on the main branch.
	 */
	public boolean isOnTrunk() {
		return CvsLogUtils.isOnMainBranch(revision);
	}
	
	/**
	 * Returns <tt>true</tt> if this is an Exp ("exposed"?) revision.
	 * This is CVS speak for any "live" revision, that is, if this is
	 * the current revision, then a file exists in the working copy.
	 * 
	 * @return <tt>true</tt> if this is an Exp revision
	 */
	public boolean isStateExp() {
		return stateExp;
	}
	
	/**
	 * Returns <tt>true</tt> if this is a dead revision. If this is the
	 * current revision, then the file does not exist in the working copy.
	 * 
	 * @return <tt>true</tt> if this is a dead revision
	 */
	public boolean isStateDead() {
		return stateDead;
	}

	public String toString() {
		return "RevisionData " + revision;
	}
}