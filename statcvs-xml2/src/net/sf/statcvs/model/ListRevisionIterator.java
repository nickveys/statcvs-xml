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
    
	$RCSfile: ListRevisionIterator.java,v $ 
	Created on $Date: 2003/03/18 10:33:55 $ 
*/
package net.sf.statcvs.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides a {@link RevisionIterator} on a <code>List</code> of
 * {@link CvsRevision}s.
 * 
 * @author Richard Cyganiak
 * @version $Id: ListRevisionIterator.java,v 1.5 2003/03/18 10:33:55 lukasz Exp $
 */
public class ListRevisionIterator implements RevisionIterator {

	private List revisions;
	private Iterator iterator;

	/**
	 * Helper constructor which is present to simplify subclassing
	 */
	protected ListRevisionIterator() {
	}

	/**
	 * Creates a new instance from a <code>List</code> of
	 * {@link CvsRevision} objects. The <code>ListRevisionIterator</code>
	 * provides access to the list entries.
	 * 
	 * @param revisions a list of revisions
	 */
	public ListRevisionIterator(List revisions) {
		initList(revisions);
	}

	/**
	 * Inits the iterator with a <code>List</code> of CvsRevision objects.
	 * 
	 * @param revisions a list of revision objects
	 */
	protected void initList(List revisions) {
		this.revisions = revisions;
		reset();
	}

	/**
	 * Inits the iterator with a {@link RevisionIterator} object.
	 * 
	 * @param source a source revision iterator
	 */
	protected void initListFromIterator(RevisionIterator source) {
		List revisionList = new ArrayList();
		while (source.hasNext()) {
			revisionList.add(source.next());
		}
		initList(revisionList);
	}
				
	/**
	 * Returns the list of {@link CvsRevision} objects on which
	 * the iterator is based.
	 * 
	 * @return a list of revisions
	 */
	protected List getList() {
		return revisions;
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#hasNext()
	 */
	public boolean hasNext() {
		return iterator.hasNext();
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#next()
	 */
	public CvsRevision next() {
		return (CvsRevision) iterator.next();
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#reset()
	 */
	public void reset() {
		iterator = revisions.iterator();
	}
}
