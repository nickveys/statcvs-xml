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
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Richard Cyganiak
 * @version $Id: DirectoryTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class DirectoryTest extends TestCase {

	private Directory root;
	private Directory rootSrc;
	private Directory rootSrcNet;
	private Directory rootSrcNetSf;
	private Directory rootSrcNetSfStatcvs;

	/**
	 * Constructor
	 * @param arg0 input
	 */
	public DirectoryTest(String arg0) {
		super(arg0);
		root = new DirectoryRoot();
		rootSrc = new DirectoryImpl(root, "src");
		rootSrcNet = new DirectoryImpl(rootSrc, "net");
		rootSrcNetSf = new DirectoryImpl(rootSrcNet, "sf");
		rootSrcNetSfStatcvs = new DirectoryImpl(rootSrcNetSf, "statcvs");
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * test the {@link DirectoryRoot} object
	 */
	public void testRoot() {
		assertNotNull(root);
		assertTrue(root.isRoot());
		assertNull(root.getParent());
		assertEquals("", root.getName());
		assertEquals("", root.getPath());
	}
	
	/**
	 * test the {@link DirectoryImpl} object
	 */
	public void testNonRootDirectory() {
		assertNotNull(rootSrcNetSfStatcvs);
		assertTrue(!rootSrcNetSfStatcvs.isRoot());
		assertNotNull(rootSrcNetSfStatcvs.getParent());
		assertEquals("statcvs", rootSrcNetSfStatcvs.getName());
	}
	
	/**
	 * test the correct linking of parents, and {@link Directory.getPath()}
	 */
	public void testPath() {
		assertEquals(rootSrcNetSf, rootSrcNetSfStatcvs.getParent());
		assertEquals(rootSrcNet, rootSrcNetSf.getParent());
		assertEquals(rootSrc, rootSrcNet.getParent());
		assertEquals(root, rootSrc.getParent());
		assertEquals("src/net/sf/statcvs/", rootSrcNetSfStatcvs.getPath());
	}
	
	/**
	 * tests {@link Directory.getRevisionIterator()}
	 */
	public void testRevisionIterator() {
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		List list3 = new ArrayList();
		Author author = new Author("chevette");
		CvsRevision rev12 = createRevision("1.2", author, list1);
		CvsRevision rev11 = createRevision("1.1", author, list1);
		CvsRevision rev21 = createRevision("2.1", author, list2);
		createRevision("3.1", author, list3);
		createFile("src/net/sf/statcvs/Main.java", list1, rootSrcNetSfStatcvs);
		createFile("src/net/sf/statcvs/README", list2, rootSrcNetSfStatcvs);
		createFile("fileInRoot", list3, root);
		RevisionIterator revIt = rootSrcNetSfStatcvs.getRevisionIterator();
		Collection revs = new ArrayList();
		while (revIt.hasNext()) {
			revs.add(revIt.next());
		}
		assertEquals(revs.size(), 3);
		assertTrue(revs.contains(rev12));
		assertTrue(revs.contains(rev11));
		assertTrue(revs.contains(rev21));
	}

	/**
	 * tests {@link Directory.compareTo(Object)
	 */
	public void testCompareSame() {
		assertEquals(0, rootSrcNetSf.compareTo(rootSrcNetSf));
	}

	/**
	 * tests {@link Directory.compareTo(Object)
	 */
	public void testCompareDifferent() {
		Directory dir1 = new DirectoryImpl(root, "abc");
		Directory dir2 = new DirectoryImpl(root, "abc");
		Directory dir3 = new DirectoryImpl(root, "xyz");
		assertEquals(0, dir1.compareTo(dir2));
		assertTrue(dir1.compareTo(dir3) < 0);
		assertTrue(dir3.compareTo(dir2) > 0);
	}

	/**
	 * tests {@link Directory.getDepth()}
	 */
	public void testGetDepth() {
		assertEquals(0, root.getDepth());
		assertEquals(1, rootSrc.getDepth());
		assertEquals(2, rootSrcNet.getDepth());
		assertEquals(3, rootSrcNetSf.getDepth());
		assertEquals(4, rootSrcNetSfStatcvs.getDepth());
	}

	/**
	 * Tests automatic creation of backlinks to subdirectories
	 */
	public void testSubdirectories() {
		assertEquals(1, root.getSubdirectories().size());
		assertTrue(root.getSubdirectories().contains(rootSrc));
		assertEquals(1, rootSrc.getSubdirectories().size());
		assertTrue(rootSrc.getSubdirectories().contains(rootSrcNet));
		assertEquals(1, rootSrcNet.getSubdirectories().size());
		assertTrue(rootSrcNet.getSubdirectories().contains(rootSrcNetSf));
		assertEquals(1, rootSrcNetSf.getSubdirectories().size());
		assertTrue(rootSrcNetSf.getSubdirectories().contains(rootSrcNetSfStatcvs));
		assertTrue(rootSrcNetSfStatcvs.getSubdirectories().isEmpty());
	}

	private CvsRevision createRevision(String revision, Author author, List addTo) {
		CvsRevision result = new CvsRevision(revision);
		result.setAuthor(author);
		result.setDate(new Date(1000000));
		addTo.add(result);
		return result;
	}
	
	private CvsFile createFile(String name, List revisions, Directory dir) {
		return new CvsFile(name, dir, revisions, false, false);
	}
}
