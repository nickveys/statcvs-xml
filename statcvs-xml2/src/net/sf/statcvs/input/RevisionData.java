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
 * Container for all information contained in one CVS revisionNumber
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @version $Id$
 */
public class RevisionData {
	private String revisionNumber;
	private Date date;
	private String loginName;
	private boolean stateExp = false;
	private boolean stateDead = false;
	private boolean hasNoLines = true;
	private int linesAdded;
	private int linesRemoved;
	private String comment;

	/**
	 * @return Returns the loginName.
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param authorName The loginName to set.
	 */
	public void setLoginName(String authorName) {
		this.loginName = authorName;
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
	 * Checks if the revision contains numbers for the added
	 * and removed lines.
	 * @return true if the revision contains numbers for the
	 * 		added and removed lines
	 */
	public boolean hasNoLines() {
		return hasNoLines;
	}

	/**
	 * Sets the number of added and removed lines.
	 * @param added The number of added lines
	 * @param removed The number of removed lines
	 */
	public void setLines(int added, int removed) {
		this.linesAdded = added;
		this.linesRemoved = removed;
		hasNoLines = false;
	}

	/**
	 * @return Returns the revisionNumber.
	 */
	public String getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * Sets the revision number.
	 * @param revision The revision number
	 */
	public void setRevisionNumber(String revision) {
		this.revisionNumber = revision;
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
	 * Returns <tt>true</tt> if this revisionNumber marks the adding of a new file
	 * on a subbranch. CVS creates a dead 1.1 revisionNumber on the trunk even if
	 * the file never gets merged into the trunk. If we evaluate the trunk,
	 * and the file doesn't have any other revisions on the trunk, then we
	 * ignore this revisionNumber.
	 *  
	 * @return <tt>true</tt> if this is the adding of a new file on a subbranch
	 */
	public boolean isAddOnSubbranch() {
		return stateDead && revisionNumber.equals("1.1");
	}
	
	/**
	 * Returns <tt>true</tt> if this revisionNumber is the removal of a file.
	 * Any dead revisionNumber means that the file was removed. The only exception
	 * is a dead 1.1 revisionNumber, which is an add on a subbranch.
	 * 
	 * @return <tt>true</tt> if this revisionNumber deletes the file.
	 * @see #isAddOnSubbranch
	 */
	public boolean isDeletion() {
		return stateDead && !revisionNumber.equals("1.1");
	}
	
	/**
	 * Returns <tt>true</tt> if this revisionNumber is a normal change, or if it
	 * restores a removed file. The distinction between these two cases
	 * can be made by looking at the previous (in time, not log order) revisionNumber.
	 * If it was a deletion, then this revisionNumber is a restore.
	 * 
	 * @return <tt>true</tt> if this is a normal change or a restore.
	 */
	public boolean isChangeOrRestore() {
		return stateExp && !hasNoLines;
	}
	
	/**
	 * Returns <tt>true</tt> if this revisionNumber is the creation of a new file.
	 * 
	 * @return <tt>true</tt> if this is the creation of a new file.
	 */
	public boolean isCreation() {
		return stateExp && hasNoLines;
	}

	/**
	 * Returns <tt>true</tt> if this revisionNumber is on the main branch.
	 * 
	 * @return <tt>true</tt> if this revisionNumber is on the main branch.
	 */
	public boolean isOnTrunk() {
		return CvsLogUtils.isOnMainBranch(revisionNumber);
	}
	
	/**
	 * Returns <tt>true</tt> if this is an Exp ("exposed"?) revisionNumber.
	 * This is CVS speak for any "live" revisionNumber, that is, if this is
	 * the current revisionNumber, then a file exists in the working copy.
	 * 
	 * @return <tt>true</tt> if this is an Exp revisionNumber
	 */
	public boolean isStateExp() {
		return stateExp;
	}
	
	/**
	 * Returns <tt>true</tt> if this is a dead revisionNumber. If this is the
	 * current revisionNumber, then the file does not exist in the working copy.
	 * 
	 * @return <tt>true</tt> if this is a dead revisionNumber
	 */
	public boolean isStateDead() {
		return stateDead;
	}

	public String toString() {
		return "RevisionData " + revisionNumber;
	}
}