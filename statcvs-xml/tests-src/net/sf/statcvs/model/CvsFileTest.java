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
 * Test cases for {@link CvsFile}
 * 
 * @author Richard Cyganiak
 * @version $Id: CvsFileTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CvsFileTest extends TestCase {
	private CvsFile file;
	private CvsRevision rev1;
	private CvsRevision rev2;
	private CvsRevision rev3;
	private CvsRevision rev4;
	private CvsRevision rev5;
	private CvsRevision rev6;
	private List rev1list;
	private Directory dirRoot;
	private Directory dirTest;

	/**
	 * Constructor for CvsFileTest.
	 * @param arg0 input
	 */
	public CvsFileTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		rev1 = createRev("1.1", 1000000000, "author", "message");
		rev1.setState(CvsRevision.STATE_INITIAL_REVISION);
		rev2 = createRev("1.2", 1100000000, "author2", "message2");
		rev3 = createRev("1.3", 1200000000, "author3", "message3");
		rev4 = createRev("1.4", 1300000000, "author4", "message4");
		rev5 = createRev("1.5", 1400000000, "author5", "message5");
		rev6 = createRev("1.6", 1500000000, "author6", "message6");
		rev1list = new ArrayList();
		rev1list.add(rev1);
		dirRoot = new DirectoryRoot();
		dirTest = new DirectoryImpl(dirRoot, "test");
	}

	/**
	 * Method testCreation.
	 */
	public void testCreation() {
		file = createFile("file", rev1list);
		assertEquals("file", file.getFilenameWithPath());
		assertEquals(1, file.getRevisions().size());
		assertSame(rev1, file.getLatestRevision());
		assertEquals(0, file.getCurrentLinesOfCode());
		assertEquals(dirRoot, file.getDirectory());
		assertTrue("file was not deleted", !file.isDead());
	}
	
	/**
	 * Method testMustHaveRevision.
	 */
	public void testMustHaveRevision() {
		try {
			createFile("file", new ArrayList());
			fail("no revisions is not allowed");
		} catch (IllegalArgumentException e) {
		// do nothing
		}
	}

	/**
	 * Method testMultipleRevisions.
	 */
	public void testMultipleRevisions() {
		CvsRevision[] revs = {rev3, rev2, rev1};
		file = createFile("file", createRevList(revs));
		assertEquals(3, file.getRevisions().size());
		assertEquals(rev3, file.getLatestRevision());
		assertEquals(rev1, file.getRevisions().get(2));
		assertEquals(0, file.getCurrentLinesOfCode());
		assertEquals(0, rev1.getLinesOfCode());
		assertEquals(0, rev2.getLinesOfCode());
		assertEquals(0, rev3.getLinesOfCode());
	}
	
	/**
	 * Method testModuleName.
	 */
	public void testModuleName() {
		CvsFile file1 = createFile("rootfile.file", dirRoot, rev1list,
				false, false);
		CvsFile file2 = createFile("test/file.file", dirTest, rev1list,
				false, false);
		assertEquals(dirRoot, file1.getDirectory());
		assertEquals(dirTest, file2.getDirectory());
	}

	/**
	 * Method testGetFirstAndLastRev.
	 */
	public void testGetFirstAndLastRev() {
		CvsRevision[] revs = {rev3, rev2, rev1};
		file = createFile("file", createRevList(revs));
		assertSame("should be rev1", rev1, file.getInitialRevision());
		assertSame("should be rev3", rev3, file.getLatestRevision());
	}

	/**
	 * Method testGetFilename
	 * 
	 */
	public void testGetFilename() {
		CvsRevision [] revs = {createRev("1.1", 1, "bla", "test")};
		CvsFile file = createFile("TestFile.java", createRevList(revs));
		assertEquals("TestFile.java", file.getFilename());
		file = createFile("", createRevList(revs));
		assertEquals("", file.getFilename());
		file = createFile("/", createRevList(revs));
		assertEquals("", file.getFilename());
	}

	/**
	 * tests the isInAttic() method
	 */
	public void testIsInAttic() {
		CvsFile file1 = createFile("Ideen", dirRoot, rev1list, false, false);
		CvsFile file2 = createFile("Ideen", dirRoot, rev1list, false, true);
		assertTrue(!file1.isInAttic());
		assertTrue(file2.isInAttic());
	}

	/**
	 * test getPreviousRevision()
	 */
	public void testGetPreviousRevision() {
		CvsRevision[] revs = {rev3, rev2, rev1};
		file = createFile("file", createRevList(revs));
		assertNull(rev1.getPreviousRevision());
		assertNull(file.getPreviousRevision(rev1));
		assertEquals(rev1, rev2.getPreviousRevision());
		assertEquals(rev1, file.getPreviousRevision(rev2));
		try {
			file.getPreviousRevision(rev4);
			fail("should have thrown IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			// expected
		}
	}

	/**
	 * Test if files are added to their directory's file list
	 */
	public void testLinkToDirectory() {
		file = createFile("test/file", dirTest, rev1list, false, false);
		assertEquals(dirTest, file.getDirectory());		
		assertTrue(dirTest.getFiles().contains(file));		
	}

	private CvsRevision createRev(String revision, long time, String author, String message) {
		CvsRevision result = new CvsRevision(revision);
		result.setDate(new Date(time));
		result.setAuthor(new Author(author));
		result.setComment(message);
		return result;
	}
	
	private List createRevList(CvsRevision[] revs) {
		List result = new ArrayList();
		for (int i = 0; i < revs.length; i++) {
			result.add(revs[i]);
		}
		return result;
	}

	private CvsFile createFile(String name, List revisions) {
		return createFile(name, dirRoot, revisions, false, false);
	}
	
	private CvsFile createFile(String workingName, Directory dir, List revisions,
			boolean isBinary, boolean isInAttic) {
		return new CvsFile(workingName, dir, revisions, isBinary, isInAttic);
	}
}
