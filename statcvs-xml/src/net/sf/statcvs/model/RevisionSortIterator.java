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
    
	$RCSfile: RevisionSortIterator.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Collections;
import java.util.List;

/**
 * Sorts the output of a given {@link RevisionIterator} by date,
 * starting with the oldest revision.
 * 
 * This is achieved by caching the output of the source iterator.
 * 
 * @author Richard Cyganiak
 * @version $Id: RevisionSortIterator.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class RevisionSortIterator extends ListRevisionIterator {

	/**
	 * constant for descending sort order to be used in
	 * {@link #RevisionSortIterator(RevisionIterator, int)}
	 */
	public static final int ORDER_ASC = 0;

	/**
	 * constant for ascending sort order to be used in
	 * {@link #RevisionSortIterator(RevisionIterator, int)}
	 */
	public static final int ORDER_DESC = 1;
	
	/**
	 * Creates a new iterator from a source iterator. The new
	 * iterator will return all {@link CvsRevision} objects
	 * of the source, but sorted by date, starting with oldest.
	 * 
	 * @param source the source iterator to be sorted by date.
	 */
	public RevisionSortIterator(RevisionIterator source) {
		this(source, ORDER_ASC);
	}

	/**
	 * Creates a new iterator from a source iterator. The new
	 * iterator will return all {@link CvsRevision} objects
	 * of the source, but sorted by date. The sort order
	 * can be specified.
	 * 
	 * @param source the source iterator to be sorted by date.
	 * @param order one of {@link #ORDER_ASC} or {@link #ORDER_DESC}
	 */
	public RevisionSortIterator(RevisionIterator source, int order) {
		initListFromIterator(source);
		Collections.sort(getList(), new RevisionDateComparator(order));
		reset();
	}

	/**
	 * Returns a new instance which sorts a <code>List</code> of
	 * revisions by date, starting with oldest.
	 * 
	 * @param revisions a list of revisions
	 */
	public RevisionSortIterator(List revisions) {
		initList(revisions);
		Collections.sort(getList(), new RevisionDateComparator(ORDER_ASC));
		reset();
	}
}
