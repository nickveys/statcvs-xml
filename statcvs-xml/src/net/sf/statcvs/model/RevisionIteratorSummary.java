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
    
	$RCSfile: RevisionIteratorSummary.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Utility class which provides useful information
 * about a {@link RevisionIterator}, for example
 * the number of elements on the iterator, a list
 * of affected files, and the date of the first
 * commit.
 * 
 * @author Anja Jentzsch
 * @author Richard Cyganiak
 * @version $Id: RevisionIteratorSummary.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class RevisionIteratorSummary {

	private Vector buffer = new Vector();

	/**
	 * Creates a new <code>RevisionIteratorSummary</code>,
	 * based on a source {@link RevisionIterator}. The
	 * source iterator's <code>reset()<code> method is
	 * called.
	 * 
	 * @param source the source iterator
	 */
	public RevisionIteratorSummary(RevisionIterator source) {
		source.reset();
		while (source.hasNext()) {
			buffer.add(source.next());
		}
		source.reset();
	}
	
	/**
	 * Returns the number of revisions (changes) in this set.
	 * 
	 * @return the number of revisions (changes) in this set
	 */
	public int size() {
		return buffer.size();
	}
	
	/**
	 * Returns a set of all authors which have committed revisions in the
	 * source set.
	 * @return a set of {@link Author} objects
	 */
	public Set getAllAuthors() {
		Set result = new HashSet();
		Iterator it = buffer.iterator();
		while (it.hasNext()) {
			CvsRevision each = (CvsRevision) it.next();
			result.add(each.getAuthor());
		}
		return result;
	}
	
	/**
	 * Gets the date of the earliest revision in the source set.
	 * 
	 * @return the earliest revision
	 */
	public Date getFirstDate() {
		Date result = null;
		Iterator it = buffer.iterator();
		while (it.hasNext()) {
			CvsRevision each = (CvsRevision) it.next();
			if (result == null || result.after(each.getDate())) {
				result = each.getDate();
			}
		}
		return result;
	}

	/**
	 * Gets the date of the latest revision in the source set.
	 * 
	 * @return the latest revision
	 */
	public Date getLastDate() {
		Date result = null;
		Iterator it = buffer.iterator();
		while (it.hasNext()) {
			CvsRevision each = (CvsRevision) it.next();
			if (result == null || result.before(each.getDate())) {
				result = each.getDate();
			}
		}
		return result;
	}
	
	/**
	 * Returns a set of all {@link CvsFile} objects
	 * which are affected by the revisions in the source set.
	 * 
	 * @return a set of <code>CvsFile</code> objects
	 */
	public Set getAllFiles() {
		Set result = new HashSet();
		Iterator it = buffer.iterator();
		while (it.hasNext()) {
			CvsRevision each = (CvsRevision) it.next();
			result.add(each.getFile());
		}
		return result;
	}

	/**
	 * Returns the number of code lines that were added in the
	 * source change set.
	 * 
	 * @return int number of lines added in the change set
	 */
	public int getLineValue() {
		int result = 0;
		Iterator it = buffer.iterator();
		while (it.hasNext()) {
			CvsRevision each = (CvsRevision) it.next();
			result += each.getLineValue();
		}
		return result;
	}
}