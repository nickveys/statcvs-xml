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
    
	$Name:  $ 
	Created on $Date: 2003/12/18 00:31:38 $ 
*/
package net.sf.statcvs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Takes a {@link RevisionIterator}, which must be sorted by date,
 * and builds a <code>List</code> of {@link Commit}s from it.
 * The result list is sorted by date.
 * 
 * TODO: commits should be built at log creation time and this class moved to input package
 * @author Richard Cyganiak
 * @version $Id: CommitListBuilder.java,v 1.11 2003/12/18 00:31:38 cyganiak Exp $
 */
public class CommitListBuilder {

	private SortedSet revisions;
	private Map currentCommits = new HashMap();
	private List commits = new ArrayList();

	/**
	 * Creates a new instance using the given set of {@link CvsRevision}s.
	 * The set must be sorted by date, oldest first.
	 * 
	 * @param revisions a set of {@link CvsRevision}s
	 */
	public CommitListBuilder(SortedSet revisions) {
		this.revisions = revisions;
	}
	
	/**
	 * Creates a <code>List</code> of {@link Commit}s from the source iterator.
	 * The result list will be sorted by date.
	 * 
	 * @return a new list of {@link Commit} objects
	 */
	public List createCommitList() {
		Iterator it = revisions.iterator();
		while (it.hasNext()) {
			processRevision((CvsRevision) it.next());
		}
		return commits;
	}
	
	protected void processRevision(CvsRevision rev) {
		if (rev.getAuthor() == null) {
			return;
		}
		Commit commit = (Commit) currentCommits.get(rev.getAuthor());
		if (commit == null || !commit.isSameCommit(rev)) {
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
}
