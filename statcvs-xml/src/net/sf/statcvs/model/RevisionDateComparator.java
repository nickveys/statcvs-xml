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
    
	$RCSfile: RevisionDateComparator.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Comparator;

/**
 * 
 * 
 * @author Manuel Schulze
 * @version $Id: RevisionDateComparator.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class RevisionDateComparator implements Comparator {

	private int order;

	/**
	 * Default Constructor
	 * constructs a RevisionDateComparator with ascending order
	 */
	public RevisionDateComparator() {
		this(RevisionSortIterator.ORDER_ASC);
	}

	/**
	 * Constructor for a RevisionDateComparator with specified order
	 * @param order order of the comparator
	 */
	public RevisionDateComparator(int order) {
		this.order = order;
	}

	/**
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public int compare(Object revision1, Object revision2) {
		CvsRevision rev1 = (CvsRevision) revision1;
		CvsRevision rev2 = (CvsRevision) revision2;
		if (order == RevisionSortIterator.ORDER_ASC) {
			return rev1.getDate().compareTo(rev2.getDate());
		} else {
			return rev2.getDate().compareTo(rev1.getDate());
		}
	}
}

