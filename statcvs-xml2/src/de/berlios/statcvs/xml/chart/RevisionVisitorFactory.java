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

package de.berlios.statcvs.xml.chart;

/**
 * @author Steffen Pingel
 */
public class RevisionVisitorFactory {

	private String className;

	public RevisionVisitorFactory(String className)
	{
		this.className = className;
	}

	public RevisionVisitorFactory()
	{
	}

	public RevisionVisitor create(Object group)
	{
		if (className != null) {
			try {
				Class c = Class.forName(className);
				return (RevisionVisitor)c.newInstance();
			}
			catch (Exception e) {
			}
		}
		return null;
	}

}
