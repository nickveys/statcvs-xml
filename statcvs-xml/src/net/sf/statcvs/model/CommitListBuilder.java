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
    
	$Name: not supported by cvs2svn $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Takes a {@link RevisionIterator}, which must be sorted by date,
 * and builds a <code>List</code> of {@link Commit}s from it.
 * The result list is sorted by date.
 * 
 * @author Richard Cyganiak
 * @version $Id: CommitListBuilder.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CommitListBuilder {

	private RevisionIterator revisions;
	private Map currentCommits = new HashMap();
	private List commits = new ArrayList();

	/**
	 * Creates a new instance using the given <code>RevisionIterator</code>.
	 * The input iterator must be sorted by date, oldest first. This can
	 * be achieved using a {@link RevisionSortIterator}.
	 * 
	 * @param sortedRevisions a sorted <code>RevisionIterator</code>
	 */
	public CommitListBuilder(RevisionIterator sortedRevisions) {
		this.revisions = sortedRevisions;
	}
	
	/**
	 * Creates a <code>List</code> of {@link Commit}s from the source iterator.
	 * The result list will be sorted by date.
	 * 
	 * @return a new list of {@link Commit} objects
	 */
	public List createCommitList() {
		revisions.reset();
		while (revisions.hasNext()) {
			processRevision(revisions.next());
		}
		return commits;
	}
	
	private void processRevision(CvsRevision rev) {
		Commit commit = (Commit) currentCommits.get(rev.getAuthor());
		if (commit == null || !commit.isSameCommit(rev)) {
			addNewCommit(rev);
		} else {
			addRevToCommit(commit, rev);
		}
	}
	
	private void addNewCommit(CvsRevision rev) {
		Commit newCommit = new Commit(rev);
		currentCommits.put(rev.getAuthor(), newCommit);
		commits.add(newCommit);
	}
	
	private void addRevToCommit(Commit commit, CvsRevision rev) {
		commit.addRevision(rev);
	}
}
