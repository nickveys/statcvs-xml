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
    
	$RCSfile: DirectoryRoot.java,v $
	$Date: 2003-07-06 21:26:39 $
*/
package net.sf.statcvs.model;

import net.sf.statcvs.I18n;

/**
 * The root of a tree of <tt>Directory</tt> objects
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: DirectoryRoot.java,v 1.2 2003-07-06 21:26:39 vanto Exp $
 */
public class DirectoryRoot extends Directory {

	/**
	 * @see net.sf.statcvs.model.Directory#getName()
	 */
	public String getName() {
		return "";
	}

	/**
	 * @see net.sf.statcvs.model.Directory#getPath()
	 */
	public String getPath() {
		return "";
	}

	/**
	 * @see net.sf.statcvs.model.Directory#getParent()
	 */
	public Directory getParent() {
		return null;
	}

	/**
	 * @see net.sf.statcvs.model.Directory#isRoot()
	 */
	public boolean isRoot() {
		return true;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return I18n.tr("root directory");
	}
	
	/**
	 * @see net.sf.statcvs.model.Directory#getDepth()
	 */
	public int getDepth() {
		return 0;
	}
}