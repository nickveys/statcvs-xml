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
    
	$RCSfile: LookaheadReaderTest.java,v $
	$Date: 2003-06-17 16:43:03 $
*/
package net.sf.statcvs.util;

import java.io.StringReader;

import junit.framework.TestCase;

/**
 * Tests for {@link LookaheadReader}
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: LookaheadReaderTest.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class LookaheadReaderTest extends TestCase {

	private LookaheadReader l;

	/**
	 * Constructor
	 * @param arg arg
	 */
	public LookaheadReaderTest(String arg) {
		super(arg);
	}
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() {
		l = new LookaheadReader(new StringReader("1\n2\n3"));
	}

	/**
	 * Tests creation of a new LookaheadReader and reading of the first line
	 * @throws Exception on error
	 */
	public void testCreation() throws Exception {
		assertNotNull(l);
		assertEquals(1, l.getLineNumber());
		assertEquals("1", l.getCurrentLine());
	}
	
	/**
	 * Tests {@link LookaheadReader.getCurrentLine} and
	 * {@link LookaheadReader.getNextLine}
	 * @throws Exception on error
	 */
	public void testCurrentLine() throws Exception {
		assertEquals("1", l.getCurrentLine());
		assertEquals("1", l.getCurrentLine());
		assertEquals("1", l.getCurrentLine());
		assertEquals("2", l.getNextLine());
		assertEquals("2", l.getCurrentLine());
		assertEquals("2", l.getCurrentLine());
		assertEquals("3", l.getNextLine());
		assertEquals("3", l.getCurrentLine());
		assertEquals("3", l.getCurrentLine());
		assertEquals(null, l.getNextLine());
		assertEquals(null, l.getCurrentLine());
		assertEquals(null, l.getCurrentLine());
	}
	
	/**
	 * Tests {@link LookaheadReader.getLineNumber}
	 * @throws Exception on error
	 */
	public void testLineNumbers() throws Exception {
		assertEquals(1, l.getLineNumber());
		l.getCurrentLine();
		assertEquals(1, l.getLineNumber());
		l.getNextLine();
		assertEquals(2, l.getLineNumber());
		l.getCurrentLine();
		assertEquals(2, l.getLineNumber());
		l.getNextLine();
		assertEquals(3, l.getLineNumber());
		l.getCurrentLine();
		assertEquals(3, l.getLineNumber());
		l.getNextLine();
		assertEquals(3, l.getLineNumber());
	}
	
	/**
	 * Tests {@link LookaheadReader.isAfterEnd}
	 * @throws Exception on error
	 */
	public void testIsAfterEnd() throws Exception {
		assertTrue(!l.isAfterEnd());
		l.getNextLine();
		assertTrue(!l.isAfterEnd());
		l.getNextLine();
		assertTrue(!l.isAfterEnd());
		l.getNextLine();
		assertTrue(l.isAfterEnd());
	}
}