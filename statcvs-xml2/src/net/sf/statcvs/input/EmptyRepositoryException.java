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
    
	$Name$
	Created on $Date$ 
*/
package net.sf.statcvs.input;

/**
 * Indicates that we can't generate a report because there are no
 * files or revisions in the repository
 * @author Richard Cyganiak
 * @version $Id$
 */
public class EmptyRepositoryException extends Exception {

	/**
	 * Constructor for EmptyRepositoryException.
	 */
	public EmptyRepositoryException() {
		super();
	}

	/**
	 * Constructor for EmptyRepositoryException.
	 * @param message input message
	 */
	public EmptyRepositoryException(String message) {
		super(message);
	}
}
