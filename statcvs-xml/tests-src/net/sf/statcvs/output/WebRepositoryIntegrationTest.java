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
    
	$RCSfile: WebRepositoryIntegrationTest.java,v $
	$Date: 2003-06-17 16:43:03 $
*/
package net.sf.statcvs.output;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.statcvs.input.Builder;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

/**
 * Test cases for {ViewCvsIntegration}
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: WebRepositoryIntegrationTest.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class WebRepositoryIntegrationTest extends TestCase {

	private static final String BASE = "http://example.com/";

	private WebRepositoryIntegration viewcvs;
	private WebRepositoryIntegration cvsweb;
	private WebRepositoryIntegration chora;
	private List revlist;
	private Builder builder;
	
	/**
	 * Checkstyle drives me nuts
	 * @param arg0 stuff
	 */
	public WebRepositoryIntegrationTest(String arg0) {
		super(arg0);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		builder = new Builder(null);
		viewcvs = new ViewCvsIntegration(BASE);
		cvsweb = new CvswebIntegration(BASE);
		chora = new ChoraIntegration(BASE);
		revlist = new ArrayList();
		revlist.add(new CvsRevision("1.1"));
	}

	/**
	 * test
	 */
	public void testViewcvsCreation() {
		assertEquals("ViewCVS", viewcvs.getName());
	}
	
	/**
	 * Tests if stuff still works when the trailing slash is omitted from
	 * the base URL
	 */
	public void testViewcvsForgivingBaseURL() {
		ViewCvsIntegration viewcvs2 = new ViewCvsIntegration("http://example.com");
		CvsFile file = createFile("file", revlist, false);
		assertEquals("http://example.com/file", viewcvs2.getFileHistoryUrl(file));
	}

	/**
	 * test URLs for a normal file
	 */
	public void testViewcvsNormalFile() {
		CvsFile file = createFile("path/file", revlist, false);
		assertEquals(BASE + "path/file", viewcvs.getFileHistoryUrl(file));
		assertEquals(BASE + "path/file?rev=HEAD&content-type=text/vnd.viewcvs-markup",
				viewcvs.getFileViewUrl(file));
	}

	/**
	 * test URLs for an attic file
	 */
	public void testViewcvsAtticFile() {
		CvsFile file = createFile("path/file", revlist, true);
		assertEquals(BASE + "path/Attic/file", viewcvs.getFileHistoryUrl(file));
		assertEquals(BASE + "path/Attic/file?rev=HEAD&content-type=text/vnd.viewcvs-markup",
				viewcvs.getFileViewUrl(file));
	}
	
	/**
	 * Test URLs for directories
	 */
	public void testViewcvsDirectory() {
		assertEquals("http://example.com/",
				viewcvs.getDirectoryUrl(builder.getDirectory("")));
		assertEquals("http://example.com/dir/",
				viewcvs.getDirectoryUrl(builder.getDirectory("dir/")));
	}
	
	/**
	 * Test URLs for diff
	 */
	public void testViewcvsDiff() {
		CvsRevision rev1 = new CvsRevision("1.1");
		CvsRevision rev2 = new CvsRevision("1.2");
		List revs = new ArrayList();
		revs.add(rev1);
		revs.add(rev2);
		createFile("file", revs, false);
		assertEquals(
				"http://example.com/file.diff?r1=1.1&r2=1.2",
				viewcvs.getDiffUrl(rev1, rev2));
	}


	/**
	 * test
	 */
	public void testCvswebCreation() {
		assertEquals("cvsweb", cvsweb.getName());
	}
	
	/**
	 * Tests if stuff still works when the trailing slash is omitted from
	 * the base URL
	 */
	public void testCvswebForgivingBaseURL() {
		CvswebIntegration cvsweb2 = new CvswebIntegration("http://example.com");
		CvsFile file = createFile("file", revlist, false);
		assertEquals("http://example.com/file", cvsweb2.getFileHistoryUrl(file));
	}

	/**
	 * test URLs for a normal file
	 */
	public void testCvswebNormalFile() {
		CvsFile file = createFile("path/file", revlist, false);
		assertEquals(BASE + "path/file", cvsweb.getFileHistoryUrl(file));
		assertEquals(BASE + "path/file?rev=HEAD&content-type=text/vnd.viewcvs-markup",
				cvsweb.getFileViewUrl(file));
	}

	/**
	 * test URLs for an attic file
	 */
	public void testCvswebAtticFile() {
		CvsFile file = createFile("path/file", revlist, true);
		assertEquals(BASE + "path/Attic/file", cvsweb.getFileHistoryUrl(file));
		assertEquals(BASE + "path/Attic/file?rev=HEAD&content-type=text/vnd.viewcvs-markup",
				cvsweb.getFileViewUrl(file));
	}
	
	/**
	 * Test URLs for directories
	 */
	public void testCvswebDirectory() {
		assertEquals("http://example.com/",
				cvsweb.getDirectoryUrl(builder.getDirectory("")));
		assertEquals("http://example.com/dir/",
				cvsweb.getDirectoryUrl(builder.getDirectory("dir/")));
	}
	
	/**
	 * Test URLs for diff
	 */
	public void testCvswebDiff() {
		CvsRevision rev1 = new CvsRevision("1.1");
		CvsRevision rev2 = new CvsRevision("1.2");
		List revs = new ArrayList();
		revs.add(rev1);
		revs.add(rev2);
		createFile("file", revs, false);
		assertEquals(
				"http://example.com/file.diff?r1=1.1&r2=1.2&f=h",
				cvsweb.getDiffUrl(rev1, rev2));
	}


	/**
	 * test
	 */
	public void testChoraCreation() {
		assertEquals("Chora", chora.getName());
	}
	
	/**
	 * Tests if stuff still works when the trailing slash is omitted from
	 * the base URL
	 */
	public void testChoraForgivingBaseURL() {
		ChoraIntegration chora2 = new ChoraIntegration("http://example.com");
		CvsFile file = createFile("file", revlist, false);
		assertEquals("http://example.com/file", chora2.getFileHistoryUrl(file));
	}

	/**
	 * test URLs for a normal file
	 */
	public void testChoraNormalFile() {
		CvsFile file = createFile("path/file", revlist, false);
		assertEquals(BASE + "path/file", chora.getFileHistoryUrl(file));
		assertEquals(BASE + "path/file?r=HEAD", chora.getFileViewUrl(file));
	}

	/**
	 * test URLs for an attic file
	 */
	public void testChoraAtticFile() {
		CvsFile file = createFile("path/file", revlist, true);
		assertEquals(BASE + "path/Attic/file", chora.getFileHistoryUrl(file));
		assertEquals(BASE + "path/Attic/file?r=HEAD", chora.getFileViewUrl(file));
	}
	
	/**
	 * Test URLs for directories
	 */
	public void testChoraDirectory() {
		assertEquals("http://example.com/",
				viewcvs.getDirectoryUrl(builder.getDirectory("")));
		assertEquals("http://example.com/dir/",
				viewcvs.getDirectoryUrl(builder.getDirectory("dir/")));
	}
	
	/**
	 * Test URLs for diff
	 */
	public void testChoraDiff() {
		CvsRevision rev1 = new CvsRevision("1.1");
		CvsRevision rev2 = new CvsRevision("1.2");
		List revs = new ArrayList();
		revs.add(rev1);
		revs.add(rev2);
		createFile("file", revs, false);
		assertEquals(
				"http://example.com/file?r1=1.1&r2=1.2",
				chora.getDiffUrl(rev1, rev2));
	}
	
	private CvsFile createFile(String workingName, List revs, boolean isInAttic) {
		Directory dir = builder.getDirectory(workingName);
		return new CvsFile(workingName, dir, revs, false, isInAttic); 
	}
}