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
    
	$RCSfile: LinesOfCodeTest.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/

package net.sf.statcvs.input;

import java.util.Date;

import junit.framework.TestCase;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.DummyRepositoryFileManager;

/**
 * Test cases for {@link Builder}, covering LOC calculations.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @see BuilderTest
 * @version $Id: LinesOfCodeTest.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class LinesOfCodeTest extends TestCase {
	private Builder builder;
	private CvsFile file;
	private Date date11;
	private Date date12;
	private Date date13;
	private Date date14;
	private Date date15;
	private CvsRevision rev0;
	private CvsRevision rev1;
	private CvsRevision rev2;
	private CvsRevision rev3;
	private CvsRevision rev4;
	private DummyRepositoryFileManager fileman;

	/**
	 * Constructor
	 * @param arg0 input
	 */
	public LinesOfCodeTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		fileman = new DummyRepositoryFileManager();
		builder = new Builder(fileman);
		builder.buildFileBegin("file", false, false);
		file = null;
		date11 = new Date(1100000000);
		date12 = new Date(1200000000);
		date13 = new Date(1300000000);
		date14 = new Date(1400000000);
		date15 = new Date(1500000000);
		rev0 = null;
		rev1 = null;
		rev2 = null;
		rev3 = null;
		rev4 = null;
	}
	
	/**
	 * Method testLinesOfCodeWithoutRepository1.
	 */
	public void testLinesOfCodeWithoutRepository1() {
		buildRevision("1.2", date12, 5, 0);
		buildRevisionInitial("1.1", date11);
		finishBuilder();
		assertEquals(5, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 5, 5, 5, 5);
		assertRevisionLines(rev1, 0, 0, 0, 0);
	}

	/**
	 * Method testLinesOfCodeWithoutRepository2.
	 */
	public void testLinesOfCodeWithoutRepository2() {
		buildRevision("1.2", date12, 0, 5);
		buildRevisionInitial("1.1", date11);
		finishBuilder();
		assertEquals(0, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 0, 0, -5, 0);
		assertRevisionLines(rev1, 5, 5, 5, 5);
	}

	/**
	 * Method testLinesOfCodeWithoutRepository3.
	 */
	public void testLinesOfCodeWithoutRepository3() {
		buildRevision("1.5", date15, 10, 15);
		buildRevision("1.4", date14, 10, 0);
		buildRevision("1.3", date13, 25, 15);
		buildRevision("1.2", date12, 10, 0);
		buildRevisionInitial("1.1", date11);
		finishBuilder();
		assertEquals(30, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 30, 30, -5, 10);
		assertRevisionLines(rev1, 35, 35, 10, 10);
		assertRevisionLines(rev2, 25, 25, 10, 25);
		assertRevisionLines(rev3, 15, 15, 10, 10);
		assertRevisionLines(rev4, 5, 5, 5, 5);
	}

	/**
	 * Test a file whose initial revision is dead (this means it was
	 * added on another branch). The builder should filter this file,
	 * so the CvsContent should be empty.
	 */
	public void testLinesOfCodeDeadInitial() {
		buildRevisionDead("1.1", date11);
		builder.buildFileEnd();
		builder.finish();
		assertTrue(builder.getCvsContent().getFiles().isEmpty());
	}

	/**
	 * Simple test to make sure that the Builder pulls the LOC number
	 * from the RepositoryFileManager
	 */
	public void testLinesOfCodeInitial() {
		fileman.setLinesOfCode("file", 100);
		buildRevisionInitial("1.1", date11);
		finishBuilder();
		assertEquals(100, file.getCurrentLinesOfCode());
		assertRevisionLines(rev0, 100, 100, 100, 100);
	}

	/**
	 * Test to make sure that LOC count for binary files is 0
	 */
	public void testLinesOfCodeBinary() {
		// build "file" (text) and "binaryfile" (binary), otherwise identical
		fileman.setLinesOfCode("file", 100);
		buildRevisionInitial("1.1", date11);
		builder.buildFileEnd();
		builder.buildFileBegin("binaryfile", true, false);
		buildRevisionInitial("1.1", date11);
		finishBuilder();

		// get "file"
		file = (CvsFile) builder.getCvsContent().getFiles().get(0);
		assertEquals(100, file.getCurrentLinesOfCode());
		rev0 = (CvsRevision) file.getRevisions().get(0);
		assertRevisionLines(rev0, 100, 100, 100, 100);

		// get "binaryfile"
		file = (CvsFile) builder.getCvsContent().getFiles().get(1);
		assertEquals(0, file.getCurrentLinesOfCode());
		rev0 = (CvsRevision) file.getRevisions().get(0);
		assertRevisionLines(rev0, 0, 0, 0, 0);
	}

	/**
	 * Test the behaviour of a deleted file, for which no HEAD LOC count
	 * is available.
	 */
	public void testLinesOfCodeWithDeletion() {
		buildRevisionDead("1.3", date13);
		buildRevision("1.2", date12, 100, 0);
		buildRevisionInitial("1.1", date11);
		finishBuilder();
		assertTrue(file.isDead());
		assertEquals(0, file.getCurrentLinesOfCode());
		//TODO: WTF should LOC for a deleted file be 100? Counter-intuitive.
		assertRevisionLines(rev0, 100, 0, -100, 0);
		assertRevisionLines(rev1, 100, 100, 100, 100);
		assertRevisionLines(rev2, 0, 0, 0, 0);
	}

	/**
	 * Tests the behaviour for deleted and re-added files.
	 */
	public void testLinesOfCodeWithRestore() {
		fileman.setLinesOfCode("file", 100);
		buildRevision("1.3", date13, 0, 0);
		buildRevisionDead("1.2", date12);
		buildRevisionInitial("1.1", date11);
		finishBuilder();
		assertTrue(!file.isDead());
		assertEquals(100, file.getCurrentLinesOfCode());
		assertTrue(rev0.isReAdd());
		assertRevisionLines(rev0, 100, 100, 100, 0);
		assertTrue(rev1.isDead());
		assertRevisionLines(rev1, 100, 0, -100, 0);
		assertTrue(rev2.isInitialRevision());
		assertRevisionLines(rev2, 100, 100, 100, 100);
	}

	private void buildRevision(String revision, Date date,
			int linesAdded, int linesRemoved) {
		builder.buildRevisionBegin(revision);
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionDate(date);
		builder.buildRevisionStateChange(linesAdded, linesRemoved);
		builder.buildRevisionEnd("comment");
	}

	private void buildRevisionInitial(String revision, Date date) {
		builder.buildRevisionBegin(revision);
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionDate(date);
		builder.buildRevisionStateInitial();
		builder.buildRevisionEnd("comment");
	}

	private void buildRevisionDead(String revision, Date date) {
		builder.buildRevisionBegin(revision);
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionDate(date);
		builder.buildRevisionStateDead();
		builder.buildRevisionEnd("comment");
	}

	private void finishBuilder() {
		builder.buildFileEnd();
		builder.finish();
		file = (CvsFile) builder.getCvsContent().getFiles().get(0);
		try {
			rev0 = (CvsRevision) file.getRevisions().get(0);
			rev1 = (CvsRevision) file.getRevisions().get(1);
			rev2 = (CvsRevision) file.getRevisions().get(2);
			rev3 = (CvsRevision) file.getRevisions().get(3);
			rev4 = (CvsRevision) file.getRevisions().get(4);
		} catch (IndexOutOfBoundsException mightHappen) {
			// do nothing
		}
	}
	
	private void assertRevisionLines(CvsRevision revision, int linesOfCode,
			int effectiveLinesOfCode, int locChange, int lineValue) {
		assertEquals("lines of code", linesOfCode, revision.getLinesOfCode());
		assertEquals("effective lines of code", effectiveLinesOfCode,
				revision.getEffectiveLinesOfCode());
		assertEquals("lines of code change", locChange, revision.getLinesOfCodeChange());
		assertEquals("line value", lineValue, revision.getLineValue());
	}
}