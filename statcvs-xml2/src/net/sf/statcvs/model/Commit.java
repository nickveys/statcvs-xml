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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates a CVS commit, which may consist of several {@link CvsRevision}
 * objects (several files committed at once by the same author with the same
 * message). The implementation allows for a tolerance of several minutes
 * between individual file commits, but author and message must be identical.
 * 
 * TODO: Implement Comparable (by date)
 * TODO: make ordering assumptions for addRevision and getRevisions() explicit (and use SortedSet to store revisions?)
 * @author Richard Cyganiak
 * @version $Id$
 */
public class Commit {

	private static final int MAX_TIME_BETWEEN_CHANGES_MILLISECONDS = 300000;

	private List revisions = new ArrayList();
	private CvsRevision firstChange;
	private CvsRevision lastChange;

	/**
	 * Creates a new instance which consists of the given revision.
	 * @param revision the single revision out of which the commit will be created
	 */
	public Commit(CvsRevision revision) {
		revisions.add(revision);
		firstChange = revision;
		lastChange = revision;
	}

	/**
	 * Returns the author of the commit
	 * @return the author
	 */
	public Author getAuthor() {
		return firstChange.getAuthor();
	}

	/**
	 * Returns the comment of the commit
	 * 
	 * @return the comment
	 */
	public String getComment() {
		return getRevision(0).getComment();
	}
	
	/**
	 * Returns <code>true</code> if change is part of this commit, that is if
	 * they have the same author, the same message, and are within the same
	 * timeframe.
	 * 
	 * @param change the revision to check against this commit
	 * @return <code>true</code> if change is part of this commit
	 */
	public boolean isSameCommit(CvsRevision change) {
		return isSameAuthorAndMessage(change) && isInTimeFrame(change.getDate());
	}

	private boolean isSameAuthorAndMessage(CvsRevision change) {
		return change.getAuthor().equals(getAuthor())
				&& change.getComment().equals(getComment());
	}

	/**
	 * Returns <code>true</code> if the date lies within the timespan of
	 * the commit, plus/minus a tolerance.
	 * 
	 * @param date the date to check against this commit
	 * @return <code>true</code> if the date lies within the timespan of the commit
	 */
	public boolean isInTimeFrame(Date date) {
		return date.getTime() > getBeginTime() && date.getTime() < getEndTime();
	}

	/**
	 * Returns <code>true</code> if two commits overlap and should be merged.
	 * 
	 * @param commit the commit to check
	 * @return <code>true</code> if two commits overlap
	 */
	public boolean overlaps(Commit commit) {
		return firstChange.getDate().getTime() <= commit.getEndTime()
				&& lastChange.getDate().getTime() >= commit.getBeginTime();
	}

	/**
	 * Adds a revision to the commit. The revision must be part of the commit.
	 * 
	 * @param change the <code>CvsRevision</code> to add.
	 */
	public void addRevision(CvsRevision change) {
		revisions.add(change);
		if (change.getDate().before(firstChange.getDate())) {
			firstChange = change;
		}
		if (change.getDate().after(lastChange.getDate())) {
			lastChange = change;
		}
	}

	/**
	 * Returns a list of {@link CvsRevision} objects, which make up this commit.
	 * May be used to access the individual files which were changed by the commit.
	 * 
	 * @return a list of changes
	 */
	public List getRevisions() {
		return revisions;
	}

	/**
	 * Returns a <code>String</code> <code>Set</code> containing all filenames
	 * which were affected by this <code>Commit</code>.
	 * 
	 * @return a <code>Set</code> of <code>String</code>s
	 */
	public Set getAffectedFiles() {
		Set result = new HashSet();
		Iterator it = revisions.iterator();
		while (it.hasNext()) {
			CvsRevision element = (CvsRevision) it.next();
			result.add(element.getFile().getFilenameWithPath());
		}
		return result;
	}
	
	/**
	 * Returns the number of individual changes which make up this commit.
	 * 
	 * @return the number of changes
	 */
	public int getChangeCount() {
		return revisions.size();
	}

	/**
	 * Returns the date when the commit took place. The implementation
	 * simply returns the timestamp of the first change of the commit.
	 * 
	 * @return a date within the timeframe of the commit
	 */
	public Date getDate() {
		return firstChange.getDate();
	}

	private long getBeginTime() {
		return firstChange.getDate().getTime()
				- MAX_TIME_BETWEEN_CHANGES_MILLISECONDS;
	}

	private long getEndTime() {
		return lastChange.getDate().getTime()
				+ MAX_TIME_BETWEEN_CHANGES_MILLISECONDS;
	}

	private CvsRevision getRevision(int index) {
		return (CvsRevision) revisions.get(index);
	}
}
