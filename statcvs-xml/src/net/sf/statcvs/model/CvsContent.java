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
    
	$RCSfile: CvsContent.java,v $ 
	Created on $Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Representation of a CVS Repository. The content is stored 
 * in a HashMap. The keys are the filenames and the values 
 * a reference to a CvsFile object.
 * 
 * @author Manuel Schulze
 * @see net.sf.statcvs.model.CvsFile
 * @version $Id: CvsContent.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CvsContent {
	private List files;
	private Map authors = new HashMap();
	private Directory root;
	private String moduleName = null;
	private Date firstDate = null;
	private Date lastDate = null;

	/**
	 * The CvsContent constructor.
	 * 
	 * @param name The name of the module
	 * @param files a <tt>List</tt> of {@link CvsFile} objects 
	 */
	public CvsContent(String name, List files) {
		this.moduleName = name;
		this.files = files;
		Iterator it = files.iterator();
		while (it.hasNext()) {
			addFile((CvsFile) it.next());
		}
		initRoot();
	}

	/**
	 * Returns the name of currnet module.
	 * 
	 * @return The module name
	 */
	public String getModuleName() {
		return this.moduleName;
	}

	/**
	 * Inserts the information about one file into
	 * the set of existing fileinformation.
	 * @param entry The representation of the fileinformation.
	 */
	private void addFile(CvsFile file) {
		// Username list update
		Iterator it = file.getRevisions().iterator();
		while (it.hasNext()) {
			CvsRevision revision = (CvsRevision) it.next();
			authors.put(revision.getAuthor().getName(), revision.getAuthor());

			Date currentRevisionDate = revision.getDate();
			if (firstDate == null) {
				setFirstDate(currentRevisionDate);
			} else {
				if (firstDate.compareTo(currentRevisionDate) > 0) {
					setFirstDate(currentRevisionDate);
				}
			}
			if (lastDate == null) {
				setLastDate(currentRevisionDate);
			} else {
				if (lastDate.compareTo(currentRevisionDate) < 0) {
					setLastDate(currentRevisionDate);
				}
			}
		}
	}

	private void setLastDate(Date currentRevisionDate) {
		lastDate = currentRevisionDate;
	}

	private void setFirstDate(Date currentRevisionDate) {
		firstDate = currentRevisionDate;
	}

	/**
	 * Returns the latest {@link java.util.Date} when there
	 * were changes on the repository.
	 * 
	 * @return The latest Date
	 */
	public Date getLastDate() {
		return lastDate;
	}

	/**
	 * Returns the first {@link java.util.Date} when there
		 * were changes on the repository.
		 * 
		 * @return The first Date
		 */
	public Date getFirstDate() {
		return firstDate;
	}

	/**
	 * returns the current LOC count of the repository
	 * @return the current line count of the repository
	 */
	public int getCurrentLOC() {
		int result = 0;
		Iterator it = files.iterator();
		while (it.hasNext()) {
			CvsFile file = (CvsFile) it.next();
			result += file.getCurrentLinesOfCode();
		}
		return result;
	}

	/**
	 * Returns a list of all {@link CvsFile}s, in no particular order
	 * @return a list of all {@link CvsFile}s
	 */
	public List getFiles() {
		return files;
	}

	/**
	 * @return <tt>true</tt> if the repository is empty, e.g. it contains no files
	 */
	public boolean isEmpty() {
		return (files.isEmpty());
	}

	/**
	 * Returns a {@link RevisionIterator} on all revisions
	 * in the repository. The revisions are in no particular order.
	 * 
	 * @return an iterator on all revisions in the repository.
	 */
	public RevisionIterator getRevisionIterator() {
		return new FilesRevisionIterator(files);
	}

	/**
	 * Returns a collection of all {@link Directory} objects in the repository,
	 * ordered in tree order
	 * @return a collection of <tt>Directory</tt> objects
	 */
	public List getDirectories() {
		return getRoot().getSubdirectoriesRecursive();
	}

	/**
	 * @return the root directory
	 */
	public Directory getRoot() {
		return root;
	}

	/**
	 * Returns a string representation of all files in the repository.
	 * 
	 * @return A string representation of all files in the repository
	 */
	public String toString() {
		String result = "";
		Iterator it = files.iterator();
		CvsFile cf = null;
		while (it.hasNext()) {
			cf = (CvsFile) it.next();
			result += cf.toString() + "\n";
		}
		return result;
	}

	/**
	 * @param name an author's name
	 * @return the corresponding <tt>Author</tt> object
	 */
	public Author getAuthor(String name) {
		return (Author) authors.get(name);
	}

	/**
	 * @return a <tt>Set</tt> of all {@link Author}s who have committed to the
	 * repository
	 */
	public Collection getAuthors() {
		return authors.values();
	}

	private void initRoot() {
		if (files.isEmpty()) {
			return;
		}
		CvsFile file = (CvsFile) files.get(0);
		Directory dir = file.getDirectory();
		while (!dir.isRoot()) {
			dir = dir.getParent();
		}
		root = dir;
	}
}