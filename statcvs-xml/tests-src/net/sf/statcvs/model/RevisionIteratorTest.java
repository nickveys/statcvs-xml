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
    
	$RCSfile: RevisionIteratorTest.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;

import junit.framework.TestCase;
import net.sf.statcvs.input.Builder;

/**
 * 
 * 
 * @author Manuel Schulze
 * @version $Id: RevisionIteratorTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class RevisionIteratorTest extends TestCase {

	private Builder builder;
	private List list1;
	private List list2;
	private List list3;
	private List list4;
	private List list5;
	private List list6;
	private List files;
	private Author author1;
	private Author author2;
	private Author author3;
	private Author author4;

	/**
	 * Constructor for RevisionIteratorTest.
	 * @param arg0 input
	 */
	public RevisionIteratorTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		builder = new Builder(null);
		list1 = new ArrayList();
		list2 = new ArrayList();
		list3 = new ArrayList();
		list4 = new ArrayList();
		list5 = new ArrayList();
		list6 = new ArrayList();
		files = new ArrayList();
		author1 = new Author("author1");
		author2 = new Author("author2");
		author3 = new Author("author3");
		author4 = new Author("author4");
	}

	/**
	 * Method testRepositoryRevisionsIteratorWithUserFilterOneUser.
	 */
	public void testRepositoryRevisionsIteratorWithUserFilterOneUser() {
		CvsRevision rev12 = createRevision("1.2", author1, null, list1);
		createRevision("1.1", author2, null, list1);
		CvsFile file1 = createFile("test/test1.java", list1);

		CvsRevision rev22 = createRevision("1.2", author1, null, list2);
		createRevision("1.1", author3, null, list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		CvsRevision rev32 = createRevision("1.2", author1, null, list3);
		createRevision("1.1", author2, null, list3);
		CvsFile file3 = createFile("test/test3.java", list3);

		createRevision("1.2", author3, null, list4);
		CvsRevision rev41 = createRevision("1.1", author1, null, list4);
		CvsFile file4 = createFile("test/test4.java", list4);

		CvsRevision rev52 = createRevision("1.2", author1, null, list5);
		createRevision("1.1", author4, null, list5);
		CvsFile file5 = createFile("test/test5.java", list5);

		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		files.add(file5);
		CvsContent content = new CvsContent("module", files);

		RevisionIterator it = new RevisionFilterIterator(
				content.getRevisionIterator(),
				new UserPredicate(rev12.getAuthor()));
		assertNotNull(it);

		List revs = new ArrayList();
		for (int i = 0; i < 5; i++) {
			assertTrue("should contain more; i = " + i, it.hasNext());
			revs.add(it.next());
		}
		assertTrue("should not contain more", !it.hasNext());
		assertTrue(revs.contains(rev12));
		assertTrue(revs.contains(rev22));
		assertTrue(revs.contains(rev32));
		assertTrue(revs.contains(rev41));
		assertTrue(revs.contains(rev52));
		try {
			it.next();
			fail("should have cought NoSuchElementException");
		} catch (NoSuchElementException nee) {
		// do nothing; catching just for testing
		}
		it.reset();

		revs = new ArrayList();
		for (int i = 0; i < 5; i++) {
			assertTrue("should contain more; i = " + i, it.hasNext());
			revs.add(it.next());
		}
		assertTrue("should not contain more", !it.hasNext());
		assertTrue(revs.contains(rev12));
		assertTrue(revs.contains(rev22));
		assertTrue(revs.contains(rev32));
		assertTrue(revs.contains(rev41));
		assertTrue(revs.contains(rev52));
		try {
			it.next();
			fail("should have cought NoSuchElementException");
		} catch (NoSuchElementException nee) {
			// test catch; do nothing
		}
	}

	/**
	 * Method testRepositoryRevisionsIteratorWithUserFilter.
	 */
	public void testRepositoryRevisionsIteratorWithUserFilter() {
		CvsRevision rev12 = createRevision("1.2", author1, new Date(15000000), list1);
		createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);

		CvsRevision rev22 = createRevision("1.2", author1, new Date(14000000), list2);
		CvsRevision rev21 = createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		CvsContent content = new CvsContent("module", files);

		RevisionIterator it = new RevisionFilterIterator(
				content.getRevisionIterator(),
				new UserPredicate(rev12.getAuthor()));
		assertNotNull(it);
		assertTrue("should be true", it.hasNext());
		assertSame(rev12, it.next());
		assertTrue("should be true", it.hasNext());
		assertSame(rev22, it.next());
		assertTrue("should be true", it.hasNext());
		assertSame(rev21, it.next());
		assertTrue("should be false", !it.hasNext());
		try {
			it.next();
			fail("should have cought NoSuchElementException");
		} catch (NoSuchElementException nee) {
			// test catch; do nothing
		}

	}

	/**
	 * Method testRevisionSortIterator.
	 */
	public void testRevisionSortIterator() {
		CvsRevision rev12 = createRevision("1.2", author1, new Date(15000000), list1);
		CvsRevision rev11 = createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);

		CvsRevision rev22 = createRevision("1.2", author3, new Date(14000000), list2);
		CvsRevision rev21 = createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		CvsContent content = new CvsContent("module", files);

		RevisionIterator it = new RevisionSortIterator(content.getRevisionIterator());
		Vector revs = new Vector();

		assertNotNull(it);
		for (int i = 0; i < 4; i++) {
			assertTrue("should have next, i = " + i, it.hasNext());
			CvsRevision rev = it.next();
			assertNotNull("should be not null, i = " + i, rev);
			revs.add(rev);
		}
		assertTrue("should not have next", !it.hasNext());

		assertEquals(4, revs.size());
		assertSame("wrong sort order", rev11, revs.get(0));
		assertSame("wrong sort order", rev21, revs.get(1));
		assertSame("wrong sort order", rev22, revs.get(2));
		assertSame("wrong sort order", rev12, revs.get(3));
	}

	/**
	 * Unit test
	 */
	public void testRevisionSortIterator2() {
		CvsRevision rev12 = createRevision("1.2", author1, new Date(15000000), list1);
		CvsRevision rev11 = createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);
		
		CvsRevision rev22 = createRevision("1.2", author3, new Date(14000000), list2);
		CvsRevision rev21 = createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		new CvsContent("module", files);
		
		List sourceRevs = new ArrayList();
		sourceRevs.add(rev12);
		sourceRevs.add(rev11);
		sourceRevs.add(rev22);
		sourceRevs.add(rev21);
		RevisionIterator it = new RevisionSortIterator(sourceRevs);
		Vector revs = new Vector();

		assertNotNull(it);
		for (int i = 0; i < 4; i++) {
			assertTrue("should have next, i = " + i, it.hasNext());
			CvsRevision rev = it.next();
			assertNotNull("should be not null, i = " + i, rev);
			revs.add(rev);
		}
		assertTrue("should not have next", !it.hasNext());
		
		assertEquals(4, revs.size());
		assertSame("wrong sort order", rev11, revs.get(0));
		assertSame("wrong sort order", rev21, revs.get(1));
		assertSame("wrong sort order", rev22, revs.get(2));
		assertSame("wrong sort order", rev12, revs.get(3));
	}

	/**
	 * Unit test
	 */
	public void testRevisionLimitIterator() {
		createRevision("1.2", author1, new Date(15000000), list1);
		createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);
		
		createRevision("1.2", author3, new Date(14000000), list2);
		createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		CvsContent content = new CvsContent("module", files);
		
		RevisionIterator it = new RevisionLimitIterator(content.getRevisionIterator(), 3);
		Vector revs = new Vector();
		
		assertNotNull(it);
		for (int i = 0; i < 3; i++) {
			assertTrue("should have next, i = " + i, it.hasNext());
			CvsRevision rev = it.next();
			assertNotNull("should be not null, i = " + i, rev);
			revs.add(rev);
		}
		assertTrue("should not have next", !it.hasNext());
		
		assertEquals(3, revs.size());
	}

	/**
	 * Unit test
	 */
	public void testRevisionIteratorSummary() {
		createRevision("1.2", author1, new Date(15000000), list1);
		createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);

		createRevision("1.2", author3, new Date(14000000), list2);
		createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		CvsContent content = new CvsContent("module", files);
		
		RevisionIteratorSummary summary =
				new RevisionIteratorSummary(content.getRevisionIterator());
		
		assertNotNull(summary);
		assertEquals(4, summary.size());
		assertEquals(new Date(10000000), summary.getFirstDate());
		assertEquals(new Date(15000000), summary.getLastDate());
		Set users = summary.getAllAuthors();
		assertEquals(3, users.size());
		assertTrue("should contain author1", users.contains(author1));
		assertTrue("should contain author2", users.contains(author2));
		assertTrue("should contain author3", users.contains(author3));
		Set files = summary.getAllFiles();
		assertEquals(2, files.size());
		assertTrue("should contain file1", files.contains(file1));
		assertTrue("should contain file2", files.contains(file2));
	}

	/**
	 * Unit test
	 */
	public void testRepositoryRevisionsIteratorByFilename() {
		CvsRevision rev12 = createRevision("1.2", author1, new Date(15000000), list1);
		CvsRevision rev11 = createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);

		CvsRevision rev22 = createRevision("1.2", author3, new Date(14000000), list2);
		CvsRevision rev21 = createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		CvsContent content = new CvsContent("module", files);
		
		RevisionIterator it = content.getRevisionIterator();
		Vector revs = new Vector();
		
		assertNotNull(it);
		for (int i = 0; i < 4; i++) {
			assertTrue("should have next, i = " + i, it.hasNext());
			CvsRevision rev = it.next();
			assertNotNull("should be not null, i = " + i, rev);
			revs.add(rev);
		}
		assertTrue("should not have next", !it.hasNext());
		
		assertEquals(4, revs.size());
		assertTrue("should contain rev11", revs.contains(rev11));
		assertTrue("should contain rev12", revs.contains(rev12));
		assertTrue("should contain rev21", revs.contains(rev21));
		assertTrue("should contain rev22", revs.contains(rev22));
		
		it.reset();
		revs.clear();
		
		for (int i = 0; i < 4; i++) {
			assertTrue("should have next, i = " + i, it.hasNext());
			CvsRevision rev = it.next();
			assertNotNull("should be not null, i = " + i, rev);
			revs.add(rev);
		}
		assertTrue("should not have next", !it.hasNext());
		
		assertEquals(4, revs.size());
		assertTrue("should contain rev11", revs.contains(rev11));
		assertTrue("should contain rev12", revs.contains(rev12));
		assertTrue("should contain rev21", revs.contains(rev21));
		assertTrue("should contain rev22", revs.contains(rev22));
	}

	/**
	 * Unit test
	 */
	public void testGetAllRevsByDate() {
		CvsRevision rev12 = createRevision("1.2", author1, new Date(15000000), list1);
		CvsRevision rev11 = createRevision("1.1", author2, new Date(10000000), list1);
		CvsFile file1 = createFile("test/test1.java", list1);

		CvsRevision rev22 = createRevision("1.2", author3, new Date(14000000), list2);
		CvsRevision rev21 = createRevision("1.1", author1, new Date(11000000), list2);
		CvsFile file2 = createFile("test/test2.java", list2);

		files.add(file1);
		files.add(file2);
		CvsContent content = new CvsContent("module", files);
		
		RevisionIterator it = new RevisionSortIterator(content.getRevisionIterator());
		assertNotNull(it);
		assertTrue(it.hasNext());
		assertSame(rev11, it.next());
		assertTrue(it.hasNext());
		assertSame(rev21, it.next());
		assertTrue(it.hasNext());
		assertSame(rev22, it.next());
		assertTrue(it.hasNext());
		assertSame(rev12, it.next());
		assertTrue(!it.hasNext());
	}

	private CvsRevision createRevision(String revision, Author author, Date date, List addTo) {
		CvsRevision result = new CvsRevision(revision);
		result.setAuthor(author);
		result.setDate(date);
		addTo.add(result);
		return result;
	}

	private CvsFile createFile(String name, List revisions) {
		Directory dir = builder.getDirectory(name);
		return new CvsFile(name, dir, revisions, false, false);
	}
}
