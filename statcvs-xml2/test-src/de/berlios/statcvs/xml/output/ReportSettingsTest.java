/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package de.berlios.statcvs.xml.output;

import java.util.Hashtable;

import junit.framework.TestCase;

/**
 * @author Steffen Pingel
 */
public class ReportSettingsTest extends TestCase {

	private Hashtable uberSettings;
	private ReportSettings rootSettings;
	private ReportSettings settings1;
	private ReportSettings settings2;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		uberSettings = new Hashtable();
		rootSettings = new ReportSettings(uberSettings);
		settings1 = new ReportSettings(rootSettings);
		settings2 = new ReportSettings(settings1); 
	}

	protected void tearDown() throws Exception
	{
	}

	public void testGet()
	{
		rootSettings.put("foo", "bar");
		settings2.put("foo", "baz");
		
		assertEquals(rootSettings.get("foo"), "bar");
		assertEquals(settings1.get("foo"), "bar");
		assertEquals(settings2.get("foo"), "baz");
	}

	public void testGetId()
	{
		settings1.setId("s1");
		settings2.setId("s2");
		
		rootSettings.put("foo", "bar");
		rootSettings.put("s1.s2.foo", "bar");
		settings1.put("foo", "baz");
		settings2.put("foo", "baz");
		
		assertEquals(rootSettings.get("foo"), "bar");
		assertEquals(settings1.get("foo"), "baz");
		assertEquals(settings2.get("foo"), "bar");
	}

	public void testGetIdFallback()
	{
		settings1.setId("s1");
		settings2.setId("s2");
		
		rootSettings.put("s1.foo", "bar");
		settings1.put("foo", "baz");
		settings2.put("foo", "baz");
		
		assertEquals(settings1.get("foo"), "bar");
		assertEquals(settings2.get("foo"), "bar");
	}

	public void testGetUber()
	{
		uberSettings.put("foo", "bar");
		settings1.put("foo", "baz");
		
		assertEquals(settings1.get("foo"), "bar");
		assertEquals(settings2.get("foo"), "bar");
	}

}
