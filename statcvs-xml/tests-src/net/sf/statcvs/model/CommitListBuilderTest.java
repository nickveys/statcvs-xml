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
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: CommitListBuilderTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CommitListBuilderTest extends TestCase {

	private static final int DATE = 10000000;

	private CvsRevision rev1;
	private CvsRevision rev2;
	private CvsRevision rev3;
	private CvsRevision rev4;
	private CvsRevision rev4b;
	private CvsRevision rev5;
	private CvsRevision rev6;
	private List commits;
	private Author author1;
	private Author author2;
	private Author author3;
	
	/**
	 * Constructor for CommitListBuilderTest.
	 * @param arg0 input argument
	 */
	public CommitListBuilderTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		author1 = new Author("author1");
		author2 = new Author("author2");
		author3 = new Author("author3");
		rev1 = createRev("rev1", DATE, author1, "message1");
		rev2 = createRev("rev2", DATE + 100, author2, "message1");
		rev3 = createRev("rev3", DATE + 200, author1, "message2");
		rev4 = createRev("rev4", DATE + 100000, author3, "message1");
		rev4b = createRev("rev4b", DATE + 100000, author1, "message1");
		rev5 = createRev("rev5", DATE + 200000, author1, "message1");
		rev6 = createRev("rev6", DATE + 250000, author2, "message1");
	}

	private CvsRevision createRev(String revision, long time, Author author, String message) {
		CvsRevision result = new CvsRevision(revision);
		result.setDate(new Date(time));
		result.setAuthor(author);
		result.setComment(message);
		return result;
	}

	/**
	 * Method testCreation.
	 */
	public void testCreation() {
		runBuilder(null);
		assertEquals(0, commits.size());
	}
	
	/**
	 * Method testOneRevision.
	 */
	public void testOneRevision() {
		CvsRevision[] revs = {rev1};
		runBuilder(revs);
		assertEquals(1, commits.size());
		assertEquals(1, getCommit(0).getChangeCount());
		assertEquals(rev1, getCommit(0).getRevisions().get(0));
	}

	/**
	 * Method testOneCommitMultipleRevisions.
	 */
	public void testOneCommitMultipleRevisions() {
		CvsRevision[] revs = {rev1, rev4b, rev5};
		runBuilder(revs);
		assertEquals(1, commits.size());
		assertEquals(3, getCommit(0).getChangeCount());
		assertTrue(getCommit(0).getRevisions().contains(rev1));
		assertTrue(getCommit(0).getRevisions().contains(rev4b));
		assertTrue(getCommit(0).getRevisions().contains(rev5));
	}

	/**
	 * Method testMultipleCommits.
	 */
	public void testMultipleCommits() {
		CvsRevision[] revs = {rev1, rev2, rev3};
		runBuilder(revs);
		assertEquals(3, commits.size());
		assertEquals(1, getCommit(0).getChangeCount());
		assertEquals(1, getCommit(1).getChangeCount());
		assertEquals(1, getCommit(2).getChangeCount());
		assertTrue(getCommit(0).getRevisions().contains(rev1));
		assertTrue(getCommit(1).getRevisions().contains(rev2));
		assertTrue(getCommit(2).getRevisions().contains(rev3));
	}
		
	/**
	 * Method testSimultaneousCommits.
	 */
	public void testSimultaneousCommits() {
		CvsRevision[] revs = {rev1, rev2, rev4, rev5, rev6};
		runBuilder(revs);
		assertEquals(3, commits.size());
		assertEquals(2, getCommit(0).getChangeCount());
		assertEquals(2, getCommit(1).getChangeCount());
		assertEquals(1, getCommit(2).getChangeCount());
		assertTrue(getCommit(0).getRevisions().contains(rev1));
		assertTrue(getCommit(0).getRevisions().contains(rev5));
		assertTrue(getCommit(1).getRevisions().contains(rev2));
		assertTrue(getCommit(1).getRevisions().contains(rev6));
		assertTrue(getCommit(2).getRevisions().contains(rev4));
	}

	private Commit getCommit(int index) {
		return (Commit) commits.get(index);
	}

	private void runBuilder(CvsRevision[] revisions) {
		List revList = new ArrayList();
		if (revisions != null) {
			for (int i = 0; i < revisions.length; i++) {
				revList.add(revisions[i]);
			}
		}
		commits = new CommitListBuilder(new RevisionSortIterator(revList)).createCommitList();
	}
}
