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
	Created on $Date: 2003-06-17 16:43:03 $ 
*/
package net.sf.statcvs.util;

import junit.framework.TestCase;

/**
 * Test cases for {link net.sf.statcvs.util.OutputUtils}
 * 
 * @author Richard Cyganiak
 * @version $Id: OutputUtilsTest.java,v 1.1 2003-06-17 16:43:03 vanto Exp $
 */
public class OutputUtilsTest extends TestCase {

	/**
	 * Constructor for OutputUtilsTest.
	 * @param arg0 input 
	 */
	public OutputUtilsTest(String arg0) {
		super(arg0);
	}

	/**
	 * Method testNormalString.
	 */
	public void testNormalString() {
		assertEquals("abc", OutputUtils.escapeHtml("abc"));
	}
	
	/**
	 * Method testAmp.
	 */
	public void testAmp() {
		assertEquals("x &amp;&amp; y", OutputUtils.escapeHtml("x && y"));
	}
	
	/**
	 * Method testLessThan.
	 */
	public void testLessThan() {
		assertEquals("x &lt; y", OutputUtils.escapeHtml("x < y"));
	}
	
	/**
	 * Method testGreaterThan.
	 */
	public void testGreaterThan() {
		assertEquals("x &gt; y", OutputUtils.escapeHtml("x > y"));
	}
	
	/**
	 * Method testLineBreak.
	 */
	public void testLineBreak() {
		assertEquals("line1<BR>\nline2<BR>\n",
				OutputUtils.escapeHtml("line1\nline2\n"));
	}
	
	/**
	 * Method testCombination.
	 */
	public void testCombination() {
		assertEquals("(x &lt; y) &amp;&amp;<BR>\n(y &gt; x)",
				OutputUtils.escapeHtml("(x < y) &&\n(y > x)"));
	}
}
