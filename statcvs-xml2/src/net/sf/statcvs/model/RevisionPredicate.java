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
    
	$RCSfile: RevisionPredicate.java,v $ 
	Created on $Date: 2003/03/18 10:33:55 $ 
*/
package net.sf.statcvs.model;

/**
 * Predicate Interface for filtering of revisions
 * @author Lukasz Pekacki <lukasz@pekacki.de>
 * @version $Id: RevisionPredicate.java,v 1.4 2003/03/18 10:33:55 lukasz Exp $
 */
public interface RevisionPredicate {
	/**
	 * Returns TRUE if the Revsion satisfies the predicate, else FALSE
	 * @param rev Revision to with the predicate is applied
	 * @return boolean TRUE if the Revsion satisfies the predicate, else FALSE
	 */
	boolean meets(CvsRevision rev);
}
