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
    
	$RCSfile: FileBlockParserTest.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/

package net.sf.statcvs.input;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.util.DateUtils;
import net.sf.statcvs.util.LookaheadReader;

/**
 * TODO: Some of those tests should be in BuilderTest because they check
 *       the Builder's functionality and not the parsing.
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: FileBlockParserTest.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class FileBlockParserTest extends TestCase {

	private static final String REVISION_DELIMITER =
			"----------------------------\n";
	private static final String FILE_DELIMITER =
		"=============================================================================\n";

	/**
	 * Constructor for FileBlockParserTest.
	 * @param arg0 input
	 */
	public FileBlockParserTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Method testSimpleLog.
	 * @throws Exception on error
	 */
	public void testSimpleLog() throws Exception {
		String log = getDefaultFileHead();
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		assertNotNull("file was null", file);
		assertEquals("testfile", file.getFilenameWithPath());
		assertTrue(!file.isBinary());

		List revisions = file.getRevisions();
		assertEquals(1, revisions.size());

		CvsRevision revision = (CvsRevision) revisions.get(0);
		assertEquals("ewender", revision.getAuthor().getName());
		assertEquals("comment text", revision.getComment());
		assertEquals(false, revision.isDead());
		assertEquals("1.1", revision.getRevision());
		assertEquals(DateUtils.convertFromLogTime("2002/05/25 09:52:07 GMT"), revision.getDate());
		assertEquals(0, revision.getLinesAdded());
		assertEquals(0, revision.getLinesRemoved());
	}

	/**
	 * Method testParser.
	 * @throws Exception on error
	 */
	public void testParser() throws Exception {
		String log = "\n" + getDefaultFileHead();
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		Builder builder = new Builder(null);
		new CvsLogfileParser(new StringReader(log), builder).parse();
		CvsContent content = builder.getCvsContent();
		assertNotNull(content);
		assertEquals(1, content.getFiles().size());
	}

	/**
	 * Method testParser.
	 * @throws Exception on error
	 */
	public void testParserWithUncommittedFiles() throws Exception {
		String log = "? uncommitted-file\n\n" + getDefaultFileHead();
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		Builder builder = new Builder(null);
		new CvsLogfileParser(new StringReader(log), builder).parse();
		CvsContent content = builder.getCvsContent();
		assertNotNull(content);
		assertEquals(1, content.getFiles().size());
	}

	/**
	 * Method testIsCheckIn.
	 * @throws Exception on error
	 */
	public void testIsCheckIn() throws Exception {
		String log = getDefaultFileHead();
		log += "revision 1.2\n";
		log += "date: 2002/05/26 15:22:52;  author: autor2;  state: Exp;  lines: +3 -2\n";
		log += "abc\n";
		log += REVISION_DELIMITER;
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		assertNotNull("file was null", file);

		List revisions = file.getRevisions();
		assertEquals(2, revisions.size());

		CvsRevision revision = (CvsRevision) revisions.get(1);
		assertTrue("rev 1.1 is a checkin", revision.isInitialRevision());

		revision = (CvsRevision) revisions.get(0);
		assertTrue("rev 1.2 is no checkin", !revision.isInitialRevision());
	}

	/**
	 * Method testTwoRevisions.
	 * @throws Exception on error
	 */
	public void testTwoRevisions() throws Exception {
		String log = getDefaultFileHead();
		log += "revision 1.2\n";
		log += "date: 2002/05/26 15:22:52;  author: autor2;  state: Exp;  lines: +3 -2\n";
		log += "abc\n";
		log += REVISION_DELIMITER;
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		assertNotNull("file was null", file);

		List revisions = file.getRevisions();
		assertEquals(2, revisions.size());

		CvsRevision revision = (CvsRevision) revisions.get(1);
		assertEquals("ewender", revision.getAuthor().getName());
		assertEquals("comment text", revision.getComment());
		assertEquals(false, revision.isDead());
		assertEquals("1.1", revision.getRevision());
		assertEquals(DateUtils.convertFromLogTime("2002/05/25 09:52:07 GMT"), revision.getDate());
		assertEquals(0, revision.getLinesAdded());
		assertEquals(0, revision.getLinesRemoved());
		revision = (CvsRevision) revisions.get(0);
		assertEquals("autor2", revision.getAuthor().getName());
		assertEquals("abc", revision.getComment());
		assertEquals(false, revision.isDead());
		assertEquals("1.2", revision.getRevision());
		assertEquals(DateUtils.convertFromLogTime("2002/05/26 15:22:52 GMT"), revision.getDate());
		assertEquals(3, revision.getLinesAdded());
		assertEquals(2, revision.getLinesRemoved());
	}

	/**
	 * Method testTwoFiles.
	 * @throws Exception on error
	 */
	public void testTwoFiles() throws Exception {
		String log = "\n";
		log += getDefaultFileHead();
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		log += "\n";
		log += "RCS file: /home/CVSROOT/TEST/etc/chat/Server.java,v\n";
		log += "Working file: etc/chat/Server.java\n";
		log += "head: 1.1\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "keyword substitution: o\n";
		log += "total revisions: 1;     selected revisions: 1\n";
		log += "description:\n";
		log += REVISION_DELIMITER;
		log += "revision 1.1\n";
		log += "date: 2002/07/13 12:32:04;  author: cyganiak;  state: Exp;\n";
		log += "/etc/ als Sammelstelle für \"sonstiges\": Chat, j3d-Tests, ...\n";
		log += FILE_DELIMITER;
		Builder builder = new Builder(null);
		new CvsLogfileParser(new StringReader(log), builder).parse();
		CvsContent content = builder.getCvsContent();
		assertEquals(2, content.getFiles().size());
	}

	/**
	 * Method testDescription.
	 * @throws Exception on error
	 */
	public void testDescription() throws Exception {
		String log = "";
		log += "RCS file: /home/CVSROOT/TEST/etc/chat/Server.java,v\n";
		log += "Working file: etc/chat/Server.java\n";
		log += "head: 1.1\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "keyword substitution: o\n";
		log += "total revisions: 1;     selected revisions: 1\n";
		log += "description:\n";
		log += "Beschreibung Zeile 1\n";
		log += "Beschreibung Zeile 2\n";
		log += REVISION_DELIMITER;
		log += "revision 1.1\n";
		log += "date: 2002/07/13 12:32:04;  author: cyganiak;  state: Exp;\n";
		log += "comment\n";
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		List revisions = file.getRevisions();
		assertEquals(1, revisions.size());

		CvsRevision revision = (CvsRevision) revisions.get(0);
		assertEquals("cyganiak", revision.getAuthor().getName());
		assertEquals("comment", revision.getComment());
		assertEquals(false, revision.isDead());
		assertEquals("1.1", revision.getRevision());
	}

	/**
	 * Method testDescription.
	 * @throws Exception on error
	 */
	public void testLocks() throws Exception {
		String log = "";
		log += "RCS file: /home/CVSROOT/TEST/etc/chat/Server.java,v\n";
		log += "Working file: etc/chat/Server.java\n";
		log += "head: 1.1\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "cyganiak: 1.1\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "keyword substitution: o\n";
		log += "total revisions: 1;     selected revisions: 1\n";
		log += "description:\n";
		log += REVISION_DELIMITER;
		log += "revision 1.1\n";
		log += "date: 2002/07/13 12:32:04;  author: cyganiak;  state: Exp;\n";
		log += "comment\n";
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		List revisions = file.getRevisions();
		assertEquals(1, revisions.size());

		CvsRevision revision = (CvsRevision) revisions.get(0);
		assertEquals("cyganiak", revision.getAuthor().getName());
		assertEquals("comment", revision.getComment());
		assertEquals(false, revision.isDead());
		assertEquals("1.1", revision.getRevision());
	}

	/**
	 * Method testNoRevisionSelected
	 * Necessary when specifying ranges of dates or tags. log still
	 * contains all files but no selected revisions
	 * @throws Exception on error
	 */
	public void testNoRevisionSelected() throws Exception {
		String log = "";
		log += "RCS file: /home/CVSROOT/TEST/etc/chat/Server.java,v\n";
		log += "Working file: etc/chat/Server.java\n";
		log += "head: 1.1\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "cyganiak: 1.1\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "keyword substitution: o\n";
		log += "total revisions: 1;     selected revisions: 0\n";
		log += "description:\n";
		log += FILE_DELIMITER;
		Builder builder = new Builder(null);
		new CvsFileBlockParser(new LookaheadReader(new StringReader(log)), builder).parse();
		builder.finish();
		assertTrue(builder.getCvsContent().getFiles().isEmpty());
	}

	/**
	 * Method testEmptyLinesAfterEnd.
	 * @throws Exception on error
	 */
	public void testEmptyLinesAfterEnd() throws Exception {
		String log = "\n" + getDefaultFileHead();
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		log += "\n\n";
		Builder builder = new Builder(null);
		new CvsLogfileParser(new StringReader(log), builder).parse();
		CvsContent content = builder.getCvsContent();
		assertEquals(1, content.getFiles().size());
	}

	/**
	 * Tests a file that was added on another branch. It should be removed.
	 * @throws Exception on error
	 */
	public void testFileOnOtherBranch() throws Exception {
		String log = "";
		log += "RCS file: /home/bude/cyganiak/cvstest/test/Attic/testfile2,v\n";
		log += "Working file: testfile2\n";
		log += "head: 1.1\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "		branch: 1.1.0.2\n";
		log += "keyword substitution: kv\n";
		log += "total revisions: 2;     selected revisions: 2\n";
		log += "description:\n";
		log += REVISION_DELIMITER;
		log += "revision 1.1\n";
		log += "date: 2003/05/02 21:33:31;  author: cyganiak;  state: dead;\n";
		log += "branches:  1.1.2;\n";
		log += "file testfile2 was initially added on branch branch.\n";
		log += REVISION_DELIMITER;
		log += "revision 1.1.2.1\n";
		log += "date: 2003/05/02 21:33:31;  author: cyganiak;  state: Exp;  lines: +1 -0\n";
		log += "asdf\n";
		log += FILE_DELIMITER;
		Builder builder = new Builder(null);
		new CvsFileBlockParser(
				new LookaheadReader(new StringReader(log)), builder).parse();
		builder.finish();
		assertTrue(builder.getCvsContent().getFiles().isEmpty());
	}
	
	/**
	 * Do we really ignore all revisions on branches except the main one?
	 * @throws Exception on error
	 */
	public void testRemoveRevisionsOnOtherBranch() throws Exception {
		String log = getDefaultFileHead();
		log += "revision 1.1\n";
		log += "date: 2002/05/26 15:22:52;  author: autor2;  state: Exp;  lines: +3 -2\n";
		log += "abc\n";
		log += REVISION_DELIMITER;
		log += "revision 1.1.2.1\n";
		log += "date: 2002/05/26 16:22:52;  author: autor2;  state: Exp;  lines: +3 -2\n";
		log += "def\n";
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		assertNotNull("file was null", file);

		List revisions = file.getRevisions();
		assertEquals(1, revisions.size());

		CvsRevision revision = (CvsRevision) revisions.get(0);
		assertEquals("1.1", revision.getRevision());
	}

	/**
	 * Tests if the parser can handle a revision delimiter in the comment.
	 * @throws Exception
	 */
	public void testRevisionDelimiterInComment() {
		String comment = "MY MUCH TOO LOONGISH COMMENT\n";
		comment += REVISION_DELIMITER;
		comment += "\n";
		comment += "This is a much too long comment for a\n";
		comment += "revision and it will cause trouble."; 
		String log = getDefaultFileHead();
		log += "revision 1.2\n";
		log += "date: 2002/05/26 15:22:52;  author: autor2;  state: Exp;  lines: +3 -2\n";
		log += comment + "\n";
		log += REVISION_DELIMITER;
		log += getDefaultRevision();
		log += FILE_DELIMITER;

		CvsFile file = null;
		try {
			file = parseString(log);
		} catch (LogSyntaxException e) {
			fail("Wrong file syntax." + e.toString());
		} catch (IOException e) {
			fail("Error in IO access" + e.toString());
		}

		List revisions = file.getRevisions();

		CvsRevision revision = (CvsRevision) revisions.get(0);
		assertEquals(comment, revision.getComment());

		assertEquals(2, revisions.size());
	}
	
	/**
	 * Files in the CVSROOT directory should be ignored because they
	 * are administrative stuff and not repository contents.
	 * @throws Exception on error
	 */
	public void testFilterCVSROOT() throws Exception {
		String log = "";
		log += "RCS file: /home/CVSROOT/checkoutlist,v\n";
		log += "Working file: CVSROOT/checkoutlist\n";
		log += "head: 1.1\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "keyword substitution: kv\n";
		log += "total revisions: 1;     selected revisions: 1\n";
		log += "description:\n";
		log += REVISION_DELIMITER;
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		Builder builder = new Builder(null);
		new CvsFileBlockParser(
				new LookaheadReader(new StringReader(log)), builder).parse();
		builder.finish();
		assertTrue(builder.getCvsContent().getFiles().isEmpty());
	}

	/**
	 * CVSNT has a slightly different logfile format: After the "lines: +x -y"
	 * part of each revision, there will be a ";" and maybe more fields.
	 * @throws Exception on error
	 */
	public void testCVSNTLog() throws Exception {
		String log = "";
		log += "RCS file: k:/cvsroot/Ellison/index.html,v\n";
		log += "Working file: index.html\n";
		log += "head: 1.1\n";
		log += "branch: 1.1.1\n";
		log += "locks: strict\n";
		log += "access list:\n";
		log += "symbolic names:\n";
		log += "release_2003_03_31: 1.1.1.1.0.2\n";
		log += "start: 1.1.1.1\n";
		log += "Ellison: 1.1.1\n";
		log += "keyword substitution: kv\n";
		log += "total revisions: 2;selected revisions: 2\n";
		log += "description:\n";
		log += "----------------------------\n";
		log += "revision 1.2\n";
		log += "date: 2002/06/04 13:49:00;  author: kdavis;  state: Exp;\n";
		log += "branches:  1.1.1;\n";
		log += "Initial revision\n";
		log += "----------------------------\n";
		log += "revision 1.1\n";
		log += "date: 2002/06/04 13:48:00;  author: kdavis;  state: Exp;  lines: +3 -2;\n";
		log += "Initial import.\n";
		log += FILE_DELIMITER;		
		CvsFile file = parseString(log);
		List revs = file.getRevisions();
		assertNotNull(revs);
		assertEquals(2, revs.size());
		CvsRevision rev = (CvsRevision) revs.get(1);
		assertEquals(3, rev.getLinesAdded());
		assertEquals(2, rev.getLinesRemoved());
	}

	/**
	 * tests a file with access list
	 * @throws Exception on error
	 */
	public void testAccessList() throws Exception {
		String log = "";
		log += "RCS file: /cvs/gpg/gpg_local_prefs.txt,v\n";
		log += "Working file: gpg_local_prefs.txt\n";
		log += "head: 1.2\n";
		log += "branch:\n";
		log += "locks: strict\n";
		log += "access list:\n";
		log += "		tyler\n";
		log += "		brian\n";
		log += "symbolic names:\n";
		log += "keyword substitution: kv\n";
		log += "total revisions: 1;     selected revisions: 1\n";
		log += "description:\n";
		log += REVISION_DELIMITER;
		log += getDefaultRevision();
		log += FILE_DELIMITER;
		CvsFile file = parseString(log);
		assertEquals(1, file.getRevisions().size());
	}

	/**
	 * Tests if attic files are correctly identified. 
	 * @throws Exception on error
	 * @see net.sf.statcvs.util.CvsLogUtilsTest.testIsInAttic
	 */
	public void testIsInAttic() throws Exception {
		String notInAttic = "";
		notInAttic += "RCS file: /cvsroot/module/file,v\n";
		notInAttic += "Working file: file\n";
		notInAttic += getDefaultFileWithoutName();
		String inAttic = "";
		inAttic += "RCS file: /cvsroot/module/Attic/file,v\n";
		inAttic += "Working file: file\n";
		inAttic += getDefaultFileWithoutName();
		CvsFile file1 = parseString(notInAttic);
		CvsFile file2 = parseString(inAttic);
		assertTrue(!file1.isInAttic());
		assertTrue(file2.isInAttic());
	}

	private CvsFile parseString(String log) throws LogSyntaxException, IOException {
		Builder builder = new Builder(null);
		new CvsFileBlockParser(
				new LookaheadReader(new StringReader(log)), builder).parse();
		builder.finish();
		return (CvsFile) builder.getCvsContent().getFiles().get(0);
	}

	private String getDefaultFileHead() {
		String result = "";
		result += "RCS file: /home/CVSROOT/TEST/testfile,v\n";
		result += "Working file: testfile\n";
		result += "head: 1.3\n";
		result += "branch:\n";
		result += "locks: strict\n";
		result += "access list:\n";
		result += "symbolic names:\n";
		result += "keyword substitution: kv\n";
		result += "total revisions: 1;     selected revisions: 1\n";
		result += "description:\n";
		result += REVISION_DELIMITER;
		return result;
	}

	private String getDefaultRevision() {
		String result = "";
		result += "revision 1.1\n";
		result += "date: 2002/05/25 09:52:07;  author: ewender;  state: Exp;\n";
		result += "comment text\n";
		return result;
	}

	private String getDefaultFileWithoutName() {
		String result = "";
		result += "head: 1.1\n";
		result += "branch:\n";
		result += "locks: strict\n";
		result += "access list:\n";
		result += "symbolic names:\n";
		result += "keyword substitution: kv\n";
		result += "total revisions: 1;     selected revisions: 1\n";
		result += "description:\n";
		result += REVISION_DELIMITER;
		result += getDefaultRevision();
		result += FILE_DELIMITER;
		return result;
	}
}