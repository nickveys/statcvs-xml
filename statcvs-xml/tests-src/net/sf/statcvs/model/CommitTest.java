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
import java.util.Set;

import junit.framework.TestCase;
import net.sf.statcvs.input.Builder;

/**
 * @author Richard Cyganiak
 * @version $Id: CommitTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CommitTest extends TestCase {

	private static final int DATE = 10000000;

	private Builder builder;
	private CvsRevision rev1;
	private CvsRevision rev2;
	private CvsRevision rev3;
	private CvsRevision rev4;
	private CvsRevision rev5;
	private CvsRevision rev6;
	private CvsRevision rev7;
	private CvsRevision rev8;
	private Commit commit;
	private Author author1;
	private Author author2;
	
	/**
	 * Constructor for CommitTest.
	 * @param arg0 input
	 */
	public CommitTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		builder = new Builder(null);
		author1 = new Author("author1");
		author2 = new Author("author2");
		rev1 = createRev("rev1", DATE, author1, "message1");
		rev2 = createRev("rev2", DATE + 100, author2, "message1");
		rev3 = createRev("rev3", DATE + 200, author1, "message2");
		rev4 = createRev("rev4", DATE + 100000, author1, "message1");
		rev5 = createRev("rev5", DATE + 200000, author1, "message1");
		rev6 = createRev("rev6", DATE + 400000, author1, "message1");
		rev7 = createRev("rev7", DATE + 650000, author1, "message1");
		rev8 = createRev("rev8", DATE + 900000, author1, "message1");
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
		commit = new Commit(rev1);
		assertEquals(author1, commit.getAuthor());
		assertEquals("message1", commit.getComment());
		assertEquals(rev1.getDate(), commit.getDate());
		assertEquals(1, commit.getChangeCount());
		assertSame(rev1, commit.getRevisions().get(0));
	}

	/**
	 * Method testIsInTimeFrame1.
	 */
	public void testIsInTimeFrame1() {
		commit = new Commit(rev6);
		assertTrue("rev5 should be in time frame", commit.isInTimeFrame(rev5.getDate()));
		assertTrue("rev7 should be in time frame", commit.isInTimeFrame(rev7.getDate()));
		assertTrue("rev4 should not be in time frame", !commit.isInTimeFrame(rev4.getDate()));
		assertTrue("rev8 should not be in time frame", !commit.isInTimeFrame(rev8.getDate()));
	}

	/**
	 * Method testAddAfter.
	 */
	public void testAddAfter() {
		commit = new Commit(rev6);
		commit.addRevision(rev7);
		assertEquals(author1, commit.getAuthor());
		assertEquals("message1", commit.getComment());
		assertEquals(rev6.getDate(), commit.getDate());
		assertEquals(2, commit.getChangeCount());
		assertTrue("should contain rev6", commit.getRevisions().contains(rev6));
		assertTrue("should contain rev7", commit.getRevisions().contains(rev7));
	}

	/**
	 * Method testAddBefore.
	 */
	public void testAddBefore() {
		commit = new Commit(rev6);
		commit.addRevision(rev5);
		assertEquals(author1, commit.getAuthor());
		assertEquals("message1", commit.getComment());
		assertEquals(rev5.getDate(), commit.getDate());
		assertEquals(2, commit.getChangeCount());
		assertTrue("should contain rev6", commit.getRevisions().contains(rev6));
		assertTrue("should contain rev5", commit.getRevisions().contains(rev5));
	}

	/**
	 * Method testIsInTimeFrame2.
	 */
	public void testIsInTimeFrame2() {
		commit = new Commit(rev6);
		commit.addRevision(rev5);
		assertTrue("rev5 should be in time frame", commit.isInTimeFrame(rev5.getDate()));
		assertTrue("rev7 should be in time frame", commit.isInTimeFrame(rev7.getDate()));
		assertTrue("rev4 should be in time frame", commit.isInTimeFrame(rev4.getDate()));
		assertTrue("rev8 should not be in time frame", !commit.isInTimeFrame(rev8.getDate()));
		assertTrue("rev1 should be in time frame", commit.isInTimeFrame(rev1.getDate()));
	}

	/**
	 * Method testIsSameCommit.
	 */
	public void testIsSameCommit() {
		commit = new Commit(rev1);
		assertTrue("has different author", !commit.isSameCommit(rev2));
		assertTrue("has different comment", !commit.isSameCommit(rev3));
		assertTrue("is same commit", commit.isSameCommit(rev4));
	}

	/**
	 * Method testOverlap.
	 */
	public void testOverlap() {
		commit = new Commit(rev1);
		Commit commit2 = new Commit(rev5);
		Commit commit3 = new Commit(rev6);
		Commit commit4 = new Commit(rev8);
		assertTrue(commit.overlaps(commit2));
		assertTrue(commit2.overlaps(commit));
		assertTrue(commit2.overlaps(commit3));
		assertTrue(commit3.overlaps(commit2));
		assertTrue(!commit3.overlaps(commit4));
		assertTrue(!commit4.overlaps(commit3));
		assertTrue(!commit.overlaps(commit3));
		assertTrue(!commit3.overlaps(commit));
	}

	/**
	 * Method testOverlapExtendUpper.
	 */
	public void testOverlapExtendUpper() {
		commit = new Commit(rev1);
		commit.addRevision(rev6);
		Commit commit2 = new Commit(rev8);
		assertTrue(!commit.overlaps(commit2));
		commit.addRevision(rev7);
		assertTrue(commit.overlaps(commit2));
	}

	/**
	 * Method testOverlapExtendLower.
	 */
	public void testOverlapExtendLower() {
		commit = new Commit(rev1);
		commit.addRevision(rev6);
		Commit commit2 = new Commit(rev8);
		assertTrue(!commit.overlaps(commit2));
		commit2.addRevision(rev7);
		assertTrue(commit.overlaps(commit2));
	}
	
	/**
	 * Method testAffectedFiles.
	 */
	public void testAffectedFiles() {
		List file1revs = new ArrayList();
		file1revs.add(rev6);
		file1revs.add(rev1);
		List file2revs = new ArrayList();
		file2revs.add(rev7);
		file2revs.add(rev4);
		List file3revs = new ArrayList();
		file3revs.add(rev5);
		List file4revs = new ArrayList();
		file4revs.add(rev8);
		createFile("file1", file1revs);
		createFile("file2", file2revs);
		createFile("file3", file3revs);
		createFile("file4", file4revs);
		commit = new Commit(rev1);
		commit.addRevision(rev4);
		commit.addRevision(rev5);
		commit.addRevision(rev6);
		commit.addRevision(rev7);
		commit.addRevision(rev8);
		Set affectedFiles = commit.getAffectedFiles();
		assertEquals(4, affectedFiles.size());
		assertTrue("should contain file1", affectedFiles.contains("file1"));
		assertTrue("should contain file2", affectedFiles.contains("file2"));
		assertTrue("should contain file3", affectedFiles.contains("file3"));
		assertTrue("should contain file4", affectedFiles.contains("file4"));
	}

	private CvsFile createFile(String name, List revisions) {
		Directory dir = builder.getDirectory(name);
		return new CvsFile(name, dir, revisions, false, false);
	}
}
