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
    
	$Name$ 
	Created on $Date$ 
*/
package net.sf.statcvs.input;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import net.sf.statcvs.model.Commit;
import net.sf.statcvs.model.CvsRevision;

/**
 * Takes a set of revisions, and builds a <code>List</code> of 
 * {@link Commit}s from it. The result list is sorted by date.
 * 
 * The implementation allows for a tolerance of several minutes
 * between individual file commits, but author and message must be identical.
 * 
 * @author Richard Cyganiak
 * @version $Id$
 */
public class CommitListBuilder {
	private static final int MAX_TIME_BETWEEN_CHANGES_MILLISECONDS = 300000;

	private Iterator revisions;
	private Map currentCommits = new HashMap();
	private List commits;

	/**
	 * Creates a new instance using the given set of {@link CvsRevision}s.
	 * The set must be sorted by date, oldest first.
	 * 
	 * @param revisions a set of {@link CvsRevision}s
	 */
	public CommitListBuilder(SortedSet revisions) {
		this(revisions.iterator());
	}
	
	public CommitListBuilder(Iterator revisions)
	{
		this.revisions = revisions;
	}
	
	/**
	 * Creates a <code>List</code> of {@link Commit}s from the source iterator.
	 * The result list will be sorted by date.
	 * 
	 * @return a new list of {@link Commit} objects
	 */
	public List createCommitList() {
		if (commits != null) {
			return commits;
		}
		
		commits = new LinkedList();
		while (revisions.hasNext()) {
			processRevision((CvsRevision)revisions.next());
		}
		return commits;
	}
	
	protected void processRevision(CvsRevision rev) {
		if (rev.getAuthor() == null) {
			return;
		}
		Commit commit = (Commit) currentCommits.get(rev.getAuthor());
		if (commit == null || !isSameCommit(commit, rev)) {
			addNewCommit(rev);
		} else {
			addRevToCommit(commit, rev);
		}
	}
	
	protected void addNewCommit(CvsRevision rev) {
		Commit newCommit = new Commit(rev);
		currentCommits.put(rev.getAuthor(), newCommit);
		commits.add(newCommit);
	}
	
	protected void addRevToCommit(Commit commit, CvsRevision rev) {
		commit.addRevision(rev);
	}

	/**
	 * Returns <code>true</code> if change is part of the commit, that is if
	 * they have the same author, the same message, and are within the same
	 * timeframe.
	 * 
	 * @param commit the commit
	 * @param rev the revision to check against this commit
	 * @return <code>true</code> if change is part of this commit
	 */
	public static boolean isSameCommit(Commit commit, CvsRevision rev) {
		return commit.getAuthor().equals(rev.getAuthor())
				&& commit.getComment().equals(rev.getComment())
				&& isInTimeFrame(commit, rev.getDate())
                && rev.getMainBranch().equals(commit.getMainBranch());
	}

	/**
	 * Returns <code>true</code> if the date lies within the timespan of
	 * the commit, plus/minus a tolerance.
	 * 
	 * @param date the date to check against this commit
	 * @return <code>true</code> if the date lies within the timespan of the commit
	 */
	public static boolean isInTimeFrame(Commit commit, Date date) {
		return date.getTime() > (commit.getDate().getTime() - MAX_TIME_BETWEEN_CHANGES_MILLISECONDS)
				&& (date.getTime() < commit.getDate().getTime() + MAX_TIME_BETWEEN_CHANGES_MILLISECONDS);
	}
}
