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
    
	$RCSfile: PredicateTest.java,v $
	$Date: 2003-06-17 16:43:02 $
*/
package net.sf.statcvs.model;

import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

/**
 * Tests for the {@link TimeSpanPredicate}
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: PredicateTest.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class PredicateTest extends TestCase {

	/**
	 * @param arg0 arg
	 */
	public PredicateTest(String arg0) {
		super(arg0);
	}

	/**
	 * test {@link TimeSpanPredicate}
	 */
	public void testTimeSpanPredicate() {
		Date from = new GregorianCalendar(2003, 4, 22, 21, 29).getTime();
		Date to = new GregorianCalendar(2003, 4, 23, 15, 0).getTime();
		TimeSpanPredicate tsp = new TimeSpanPredicate(from, to);
		CvsRevision rev1 = new CvsRevision("1.1");
		rev1.setDate(new GregorianCalendar(2003, 4, 22, 22, 00).getTime());
		CvsRevision rev2 = new CvsRevision("1.1");
		rev2.setDate(new GregorianCalendar(2003, 4, 21, 22, 00).getTime());
		CvsRevision rev3 = new CvsRevision("1.1");
		rev3.setDate(new GregorianCalendar(2003, 4, 23, 22, 00).getTime());
		assertTrue(tsp.meets(rev1));
		assertTrue(!tsp.meets(rev2));
		assertTrue(!tsp.meets(rev3));
	}
}
