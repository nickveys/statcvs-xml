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
    
	$RCSfile: TimeSpanPredicate.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Date;

/**
 * Filters revisions that belong to a time span
 * 
 * @author Lukasz Pekacki <lukasz@pekacki.de>
 * @version $Id: TimeSpanPredicate.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class TimeSpanPredicate implements RevisionPredicate {
	private Date from, to;
	/**
	 * Constructor for TimeSpanPredicate.
	 * @param from beginning of time span
	 * @param to end of time span
	 */
	public TimeSpanPredicate(Date from, Date to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Filters revisions that belong to a time span
	 * @see net.sf.statcvs.model.RevisionPredicate#meets(CvsRevision)
	 */
	public boolean meets(CvsRevision rev) {
		Date revDate = rev.getDate();
		return ((from.compareTo(revDate) <= 0) && (to.compareTo(revDate) >= 0));
	}

}
