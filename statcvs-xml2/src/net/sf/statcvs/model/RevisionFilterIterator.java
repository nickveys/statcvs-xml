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
    
	$RCSfile: RevisionFilterIterator.java,v $ 
	Created on $Date: 2003/03/18 10:33:55 $ 
*/
package net.sf.statcvs.model;

import java.util.NoSuchElementException;
import java.util.logging.Logger;

/**
 * 
 * 
 * @author Manuel Schulze
 * @version $Id: RevisionFilterIterator.java,v 1.6 2003/03/18 10:33:55 lukasz Exp $
 */
public class RevisionFilterIterator implements RevisionIterator {
	private static Logger logger = null;	
	private RevisionIterator revit = null;
	private RevisionPredicate revpred = null;
	private CvsRevision nextRevision = null;

	/**
	 * Creates a new instance from a source iterator and a filter
	 * predicate. Only entries which match the predicate will
	 * become part of this iterator.
	 * 
	 * @param revisionIterator the source revision iterator
	 * @param revisionPredicate the filter predicate
	 */
	public RevisionFilterIterator(
		RevisionIterator revisionIterator,
		RevisionPredicate revisionPredicate) {
		logger = Logger.getLogger(getClass().getName());
		this.revit = revisionIterator;
		this.revpred = revisionPredicate;
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#hasNext()
	 */
	public boolean hasNext() {
		while (this.revit.hasNext()) {
			CvsRevision tmp = revit.next();
			if (this.revpred.meets(tmp)) {
				this.nextRevision = tmp;
				tmp = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#next()
	 */
	public CvsRevision next() { 
		if (this.nextRevision != null) {
			CvsRevision tmp = this.nextRevision;
			this.nextRevision = null;
			return tmp;
		} else {
			if (hasNext()) {
				CvsRevision tmp = this.nextRevision;
				this.nextRevision = null;
				return tmp;
			}
		}
		throw new NoSuchElementException("no more elements in filter iteration");
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#reset()
	 */
	public void reset() {
		this.nextRevision = null;
		this.revit.reset();
	}

}
