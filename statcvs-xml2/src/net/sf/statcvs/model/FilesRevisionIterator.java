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
    
	$RCSfile: FilesRevisionIterator.java,v $ 
	Created on $Date: 2003/03/18 10:33:55 $ 
*/
package net.sf.statcvs.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides access to all revisions inside a list of {@link CvsFile} objects.
 * Instances of this class are returned by
 * {@link CvsContent#getRevisionIterator}.
 * 
 * The revisions are not in any particular order. You might use a
 * {@link RevisionSortIterator} to sort the revisions, or a
 * {@link RevisionFilterIterator} to filter by user, directory,
 * date range...
 * 
 * @author Richard Cyganiak
 * @version $Id: FilesRevisionIterator.java,v 1.4 2003/03/18 10:33:55 lukasz Exp $
 */
public class FilesRevisionIterator implements RevisionIterator {

	private Collection repositoryFiles;
	private Iterator filesIterator;
	private Iterator revisionsIterator;
	private CvsFile nextFile;

	/**
	 * Creates a new {@link RevisionIterator} which provides access to all
	 * revisions in the collection of <code>CvsFile</code> objects.
	 * 
	 * @param repositoryFiles a collection of <code>CvsFile</code> objects
	 */
	public FilesRevisionIterator(Collection repositoryFiles) {
		this.repositoryFiles = repositoryFiles;
		reset();
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#hasNext()
	 */
	public boolean hasNext() {
		while (revisionsIterator == null || !revisionsIterator.hasNext()) {
			try {
				nextFile();
			} catch (NoSuchElementException allFilesProcessed) {
				return false;
			}
		}
		return true;
	}

	private void nextFile() {
		if (!filesIterator.hasNext()) {
			throw new NoSuchElementException();
		}
		nextFile = (CvsFile) filesIterator.next();
		revisionsIterator = nextFile.getRevisionIterator();
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#next()
	 */
	public CvsRevision next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return (CvsRevision) revisionsIterator.next();
	}

	/**
	 * @see net.sf.statcvs.model.RevisionIterator#reset()
	 */
	public void reset() {
		filesIterator = repositoryFiles.iterator();
		revisionsIterator = null;
	}
}
