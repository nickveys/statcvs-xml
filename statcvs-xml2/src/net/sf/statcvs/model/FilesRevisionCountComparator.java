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
    
	$RCSfile: FilesRevisionCountComparator.java,v $
	$Date: 2003/06/04 13:20:15 $
*/
package net.sf.statcvs.model;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Compares two files according to their number of changes (revisions).
 * If two files have the same number of changes, the number of changed
 * lines of code is used.
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: FilesRevisionCountComparator.java,v 1.1 2003/06/04 13:20:15 cyganiak Exp $
 */
public class FilesRevisionCountComparator implements Comparator {

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		CvsFile file1 = (CvsFile) o1;
		CvsFile file2 = (CvsFile) o2;
		if (file1.getRevisions().size() < file2.getRevisions().size()) {
			return 2;
		} else if (file1.getRevisions().size() > file2.getRevisions().size()) {
			return -2;
		} else {
			int lines1 = 0;
			Iterator it = file1.getRevisionIterator();
			while (it.hasNext()) {
				CvsRevision rev = (CvsRevision) it.next();
				lines1 += rev.getLineValue();
			}
			int lines2 = 0;
			it = file2.getRevisionIterator();
			while (it.hasNext()) {
				CvsRevision rev = (CvsRevision) it.next();
				lines2 += rev.getLineValue();
			}
			if (lines1 < lines2) {
				return 1;
			} else if (lines1 > lines2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
