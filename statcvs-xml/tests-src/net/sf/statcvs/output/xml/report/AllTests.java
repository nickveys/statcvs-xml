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
	Created on $Date: 2003-07-06 12:30:23 $ 
*/
package net.sf.statcvs.output.xml.report;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * AllTests
 * 
 * @author Tammo van Lessen
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite =
			new TestSuite("Test for net.sf.statcvs.output.xml.report");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(FileCollectionFormatterTest.class));
		//$JUnit-END$
		return suite;
	}
}
