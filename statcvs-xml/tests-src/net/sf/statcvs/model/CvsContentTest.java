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
 * @version $Id: CvsContentTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CvsContentTest extends TestCase {
	private List rev1list;
	private CvsRevision rev1;
	private List list1;
	private List list2;
	private List list3;
	private List list4;
	private List list5;
	private List list6;
	private List files;
	private Author tester;
	private Author tester1;
	private Author tester2;
	private Author tester3;
	private Author tester4;
	private Directory dirRoot;
	private Directory dirTest;
	private Directory dirTest1;

	/**
	 * Constructor for CvsContentTest.
	 * @param arg0 input
	 */
	public CvsContentTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		tester = new Author("tester");
		tester1 = new Author("tester1");
		tester2 = new Author("tester2");
		tester3 = new Author("tester3");
		tester4 = new Author("tester4");
		rev1list = new ArrayList();
		rev1 = createRevision("1.1", tester, rev1list);
		list1 = new ArrayList();
		list2 = new ArrayList();
		list3 = new ArrayList();
		list4 = new ArrayList();
		list5 = new ArrayList();
		list6 = new ArrayList();
		files = new ArrayList();
		dirRoot = new DirectoryRoot();
		dirTest = new DirectoryImpl(dirRoot, "test");
		dirTest1 = new DirectoryImpl(dirRoot, "test1");
	}

	/**
	 * Method testCreation.
	 */
	public void testCreation() {
		new CvsContent("TEST", files);
	}

	/**
	 * Method testGetDirectories.
	 */
	public void testGetDirectories() {
		CvsFile file1 = createFile(dirTest, "test/test1.java", rev1list);
		CvsFile file2 = createFile(dirRoot, "test2.java", rev1list);
		CvsFile file3 = createFile(dirTest1, "test1/test3.java", rev1list);
		CvsFile file4 = createFile(dirTest, "test/test2.java", rev1list);
		CvsFile file5 = createFile(dirTest1, "test1/test1.java", rev1list);
		CvsFile file6 = createFile(dirTest, "test/test3.java", rev1list);

		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		files.add(file5);
		files.add(file6);
		CvsContent content = new CvsContent("module", files);

		Collection dirs = content.getDirectories();

		assertEquals(3, dirs.size());
		assertTrue(dirs.contains(dirTest));
		assertTrue(dirs.contains(dirTest1));
		assertTrue(dirs.contains(dirRoot));

	}

	/**
	 * Method testGetDirectoriesPerUser.
	 */
	public void testGetDirectoriesPerUser() {
		createRevision("1.2", tester1, list1);
		createRevision("1.1", tester2, list1);
		createRevision("2.3", tester1, list2);
		createRevision("2.2", tester1, list2);
		createRevision("2.1", tester3, list2);
		createRevision("3.1", tester2, list3);
		createRevision("4.1", tester2, list4);
		createRevision("5.3", tester2, list5);
		createRevision("5.2", tester2, list5);
		createRevision("5.1", tester2, list5);
		createRevision("6.2", tester1, list6);
		createRevision("6.1", tester3, list6);

		CvsFile file1 = createFile(dirTest, "test/test1.java", list1);
		CvsFile file2 = createFile(dirRoot, "test2.java", list2);
		CvsFile file3 = createFile(dirTest1, "test1/test3.java", list3);
		CvsFile file4 = createFile(dirTest, "test/test2.java", list4);
		CvsFile file5 = createFile(dirTest1, "test1/test1.java", list5);
		CvsFile file6 = createFile(dirTest, "test/test3.java", list6);

		files.add(file1);
		files.add(file2);
		files.add(file3);
		files.add(file4);
		files.add(file5);
		files.add(file6);
		new CvsContent("module", files);

		Collection dirs = tester1.getDirectories();
		assertEquals(2, dirs.size());
		assertTrue(dirs.contains(dirTest));
		assertTrue(dirs.contains(dirRoot));

		dirs = tester2.getDirectories();
		assertEquals(2, dirs.size());
		assertTrue(dirs.contains(dirTest));
		assertTrue(dirs.contains(dirTest1));

		dirs = tester3.getDirectories();
		assertEquals(2, dirs.size());
		assertTrue(dirs.contains(dirRoot));
		assertTrue(dirs.contains(dirTest));
	}

	/**
	 * Method testUserNames.
	 */
	public void testUserNames() {
		createRevision("1.3", tester1, list1);
		createRevision("1.2", tester2, list1);
		createRevision("1.1", tester1, list1);
		createRevision("2.2", tester3, list2);
		createRevision("2.1", tester4, list2);
		createRevision("3.4", tester2, list3);
		createRevision("3.3", tester4, list3);
		createRevision("3.2", tester1, list3);
		createRevision("3.1", tester2, list3);
		CvsFile file1 = createFile(dirTest, "test/Burg.java", list1);
		CvsFile file2 = createFile(dirTest, "test/History.java", list2);
		CvsFile file3 = createFile(dirTest, "test/Spieler.java", list3);
		files.add(file1);
		files.add(file2);
		files.add(file3);
		CvsContent content = new CvsContent("modul", files);

		assertEquals(4, content.getAuthors().size());
	}

	private CvsRevision createRevision(String revision, Author author, List addTo) {
		CvsRevision result = new CvsRevision(revision);
		result.setAuthor(author);
		result.setDate(new Date(1000000));
		author.addRevision(result);
		addTo.add(result);
		return result;
	}
	
	private CvsFile createFile(Directory dir, String name, List revisions) {
		return new CvsFile(name, dir, revisions, false, false);
	}
}
