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
    
	$RCSfile: BuilderTest.java,v $ 
	Created on $Date: 2003-06-17 16:43:03 $ 
*/

package net.sf.statcvs.input;

import java.util.Iterator;

import junit.framework.TestCase;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

/**
 * Test cases for {@link Builder}.
 * TODO: Add test for the case that a file was initially added on a side branch
 *       and later merged into the main branch. Is the first revision treated
 *       correctly? It should have isInitial()==true and should have the right
 *       LOC and LocChange
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @see LinesOfCodeTest
 * @version $Id: BuilderTest.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class BuilderTest extends TestCase {

	private Builder builder;
	/**
	 * Constructor
	 * @param arg0 input
	 */
	public BuilderTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		builder = new Builder(null);
	}
	
	/**
	 * test {@link Builder.getAuthor(String)}
	 */
	public void testGetAuthor() {
		Author author1 = builder.getAuthor("author1");
		Author author2 = builder.getAuthor("author2");
		Author author1b = builder.getAuthor("author1");
		assertEquals(author1, author1b);
		assertTrue(!author1.equals(author2));
		assertTrue(!author1b.equals(author2));
	}
	
	/**
	 * test {@link Builder.getDirectory(String)}
	 */
	public void testGetDirectoryRoot() {
		Directory dir1 = builder.getDirectory("fileInRoot");
		Directory dir2 = builder.getDirectory("anotherFileInRoot");
		assertEquals(dir1, dir2);
		assertTrue(dir1.isRoot());
		assertEquals("", dir1.getName());
		assertEquals("", dir1.getPath());
	}

	/**
	 * test {@link Builder.getDirectory(String)}
	 */
	public void testGetDirectoryDeepPath() {
		Directory dir1 = builder.getDirectory("src/file");
		Directory dir2 = builder.getDirectory("src/net/sf/statcvs/Main.java");
		assertEquals(dir1, dir2.getParent().getParent().getParent());
		assertTrue(dir1.getParent().isRoot());
	}
	
	/**
	 * test {@link Builder.getDirectory(String)}
	 */
	public void testGetDirectorySeveralPaths() {
		Directory dir1 = builder.getDirectory("src/net/sf/statcvs/Main.java");
		Directory dir2 = builder.getDirectory("src/com/microsoft/Windows95.java");
		Directory dir3 = builder.getDirectory("src/com/microsoft/Windows98.java");
		assertEquals(dir2, dir3);
		assertEquals(dir1.getParent().getParent().getParent(), dir2.getParent().getParent());
	}
	
	/**
	 * test {@link Builder.addFile(CvsFile)} and {@link Builder.getFiles()}
	 */
	public void testFilesEmpty() {
		Builder builder1 = new Builder(null);
		builder1.finish();

		assertNotNull(builder1.getCvsContent().getFiles());
		assertTrue(builder1.getCvsContent().getFiles().isEmpty());
	}
	
	/**
	 * test {@link Builder.addFile(CvsFile)} and {@link Builder.getFiles()}
	 */
	public void testFilesOneFile() {
		Builder builder = new Builder(null);
		builder.buildFileBegin("file1", false, false);
		builder.buildRevisionBegin("1.1");
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionStateInitial();
		builder.buildRevisionEnd("comment");
		builder.buildFileEnd();
		builder.finish();

		assertNotNull(builder.getCvsContent().getFiles());
		assertEquals(1, builder.getCvsContent().getFiles().size());
		CvsFile file1 = (CvsFile) builder.getCvsContent().getFiles().get(0);
		assertEquals("file1", file1.getFilenameWithPath());
		assertEquals(builder.getDirectory(""), file1.getDirectory());
		assertEquals(1, file1.getRevisions().size());
		assertTrue(!file1.isBinary());
	}
	
	/**
	 * test {@link Builder.addFile(CvsFile)} and {@link Builder.getFiles()}
	 */
	public void testBinaryFile() {
		Builder builder = new Builder(null);
		builder.buildFileBegin("file", true, false);
		builder.buildRevisionBegin("1.1");
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionStateInitial();
		builder.buildRevisionEnd("comment");
		builder.buildFileEnd();
		builder.finish();

		CvsFile file1 = (CvsFile) builder.getCvsContent().getFiles().get(0);
		assertTrue(file1.isBinary());
	}
	
	/**
	 * test {@link Builder.addFile(CvsFile)} and {@link Builder.getFiles()}
	 */
	public void testFileTwoFiles() {
		Builder builder = new Builder(null);
		builder.buildFileBegin("file2", false, false);
		builder.buildRevisionBegin("1.1");
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionStateInitial();
		builder.buildRevisionEnd("comment");
		builder.buildFileEnd();
		builder.buildFileBegin("file3", false, false);
		builder.buildRevisionBegin("1.1");
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionStateInitial();
		builder.buildRevisionEnd("comment");
		builder.buildFileEnd();
		builder.finish();

		assertNotNull(builder.getCvsContent().getFiles());
		assertEquals(2, builder.getCvsContent().getFiles().size());
		CvsFile file2 = (CvsFile) builder.getCvsContent().getFiles().get(0);
		CvsFile file3 = (CvsFile) builder.getCvsContent().getFiles().get(1);
		assertEquals("file2", file2.getFilenameWithPath());
		assertEquals("file3", file3.getFilenameWithPath());
	}

	/**
	 * Tests {@link Builder.buildRevisionBegin}
	 */
	public void testBuildRevision() {
		Builder builder = new Builder(null);
		builder.buildFileBegin("file", false, false);
		builder.buildRevisionBegin("1.3");
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionStateDead();
		builder.buildRevisionEnd("comment3");
		builder.buildRevisionBegin("1.2");
		builder.buildRevisionAuthor("author2");
		builder.buildRevisionStateChange(10, -2);
		builder.buildRevisionEnd("comment2");
		builder.buildRevisionBegin("1.1");
		builder.buildRevisionAuthor("author1");
		builder.buildRevisionStateInitial();
		builder.buildRevisionEnd("comment1");
		builder.buildFileEnd();
		builder.finish();
		
		CvsFile file = (CvsFile) builder.getCvsContent().getFiles().get(0);
		Iterator it = file.getRevisionIterator();
		assertTrue(it.hasNext());
		CvsRevision rev3 = (CvsRevision) it.next();
		assertTrue(it.hasNext());
		CvsRevision rev2 = (CvsRevision) it.next();
		assertTrue(it.hasNext());
		CvsRevision rev1 = (CvsRevision) it.next();
		assertTrue(!it.hasNext());
		
		assertEquals("1.1", rev1.getRevision());
		assertTrue(rev1.isInitialRevision());
		assertEquals("author1", rev1.getAuthor().getName());
		assertEquals("comment1", rev1.getComment());

		assertEquals("1.2", rev2.getRevision());
		assertTrue(!rev2.isInitialRevision());
		assertEquals(10, rev2.getLinesAdded());
		assertEquals(-2, rev2.getLinesRemoved());
		assertEquals("author2", rev2.getAuthor().getName());

		assertEquals("1.3", rev3.getRevision());
		assertTrue(rev3.isDead());
		assertEquals("author1", rev3.getAuthor().getName());

		assertSame(rev1.getAuthor(), rev3.getAuthor());
	}
}