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
    
	$RCSfile: Builder.java,v $
	$Date: 2003-07-06 12:30:23 $
*/
package net.sf.statcvs.input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.statcvs.ConfigurationOptions;
import net.sf.statcvs.model.Author;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;
import net.sf.statcvs.model.DirectoryImpl;
import net.sf.statcvs.model.DirectoryRoot;
import net.sf.statcvs.util.CvsLogUtils;
import net.sf.statcvs.util.FileUtils;

/**
 * <p>Helps building the {@link net.sf.statcvs.model.CvsContent} from a CVS
 * log. The <tt>Builder</tt> is fed by some CVS history data source, for
 * example a CVS log parser. It creates and collects the <tt>CvsFile</tt> and
 * <tt>CvsRevision</tt> objects. It calculates LOC values to the individual
 * revisions.</p>
 * 
 * <p>It also takes care of the creation of <tt>Author</tt> and 
 * </tt>Directory</tt> objects and makes sure that there's only one of these
 * for each author name and path.</p>
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: Builder.java,v 1.4 2003-07-06 12:30:23 vanto Exp $
 */
public class Builder {

	private static Logger logger = Logger.getLogger(Builder.class.getName());

	private Map authors = new HashMap();
	private Map directories = new HashMap();
	private List files = new ArrayList();
	private RepositoryFileManager repositoryFileManager;

	private boolean ignoreCurrentFile;

	private String currentFileName;
	private boolean currentFileBinary;
	private boolean currentFileInAttic;
	private List currentFileRevisions;

	private String currentRevNo;
	private Date currentRevDate;
	private Author currentRevAuthor;
	private int currentRevState;
	private int currentRevLinesAdded;
	private int currentRevLinesRemoved;
	private Map currentSymbolicNames;

	private CvsContent cvsContent;

	/**
	 * Creates a new <tt>Builder</tt>
	 * @param repositoryFileManager the {@link RepositoryFileManager} that
	 * 								can be used to retrieve LOC counts for
	 * 								the files that this builder will create
	 */
	public Builder(RepositoryFileManager repositoryFileManager) {
		this.repositoryFileManager = repositoryFileManager;
		directories.put("", new DirectoryRoot());
	}

	/**
	 * Called after all files and revisions have been added (built).
	 * Does stuff to make sure that the internals of the <tt>CvsContent</tt>
	 * are in a valid state.
	 */
	public void finish() {
		cvsContent = new CvsContent(ConfigurationOptions.getProjectName(), files);
	}
	
	/**
	 * returns the <tt>Author</tt> of the given name or creates it
	 * if it does not yet exist. 
	 * @param name the author's name
	 * @return a corresponding <tt>Author</tt> object
	 */
	public Author getAuthor(String name) {
		if (authors.containsKey(name)) {
			return (Author) authors.get(name);
		}
		Author newAuthor = new Author(name);
		authors.put(name, newAuthor);
		return newAuthor;
	}
	
	/**
	 * Returns the <tt>Directory</tt> of the given filename or creates it
	 * if it does not yet exist.
	 * @param filename the name and path of a file, for example "src/Main.java"
	 * @return a corresponding <tt>Directory</tt> object
	 */
	public Directory getDirectory(String filename) {
		int lastSlash = filename.lastIndexOf('/');
		if (lastSlash == -1) {
			return getDirectoryForPath("");
		}
		return getDirectoryForPath(filename.substring(0, lastSlash + 1));
	}
	
	/**
	 * Starts building a new file. The files are not expected to be created
	 * in any particular order.
	 * @param filename the file's name with path, for example "path/file.txt"
	 * @param isBinary <tt>true</tt> if it's a binary file
	 * @param isInAttic <tt>true</tt> if the file is dead on the main branch
	 */
	public void buildFileBegin(String filename, boolean isBinary, boolean isInAttic) {
		if (isFilteredFile(filename)) {
			ignoreCurrentFile = true;
			return;
		}
		ignoreCurrentFile = false;

		currentFileName = filename;
		currentFileBinary = isBinary;
		currentFileInAttic = isInAttic;
		currentFileRevisions = new ArrayList();
		currentSymbolicNames = null;
	}

	/**
	 * Sets the symbolic names map for the current file
	 * @param date the date
	 */
	public void buildFileSymbolicNames(Map symNames) {
		currentSymbolicNames = symNames;
	}
	
	/**
	 * Finishes building a file.
	 */
	public void buildFileEnd() {
		if (ignoreCurrentFile
				|| currentFileRevisions.isEmpty()
				|| isOnOtherBranch(currentFileRevisions)) {
			return;
		}

		Directory dir = getDirectory(currentFileName);
		CvsFile file = new CvsFile(currentFileName, dir, currentFileRevisions,
				currentFileBinary, currentFileInAttic);
		Iterator it = file.getRevisionIterator();
		while (it.hasNext()) {
			CvsRevision rev = (CvsRevision) it.next();
			rev.getAuthor().addRevision(rev);
			//process symbolic names
			if (currentSymbolicNames != null) {
				String symname = (String)currentSymbolicNames.get(rev.getRevision());
				if (symname != null) {
					rev.addSymbolicName(symname);
				}
			}
		} 

		if (ConfigurationOptions.getUseHistory() && !CvsLocHistory.getInstance().isEmpty()) {
			calculateRealLinesOfCode(file);
		} else {
			calculateLinesOfCode(file);
		}
			
		files.add(file);
		logger.finer(file.getFilenameWithPath()
				+ " (" + file.getRevisions().size() + " revisions)");
	}
	
	private void calculateRealLinesOfCode(CvsFile file) {
		if (file.isBinary()) {
			Iterator it = file.getRevisionIterator();
			while (it.hasNext()) {
				CvsRevision revision = (CvsRevision) it.next();
				revision.setLinesOfCode(0);
			}
			return;
		}

		int currentLinesOfCode = 0;

		CvsRevision previous = null;
		List revisions = file.getRevisions();
		Collections.reverse(revisions);
		for (int i=0; i<revisions.size(); i++) {
			CvsRevision rev = (CvsRevision)revisions.get(i);
			if (rev.isInitialRevision()) {
				currentLinesOfCode = CvsLocHistory.getInstance().getLinesOfCode(file); 
			} else {
				currentLinesOfCode += rev.getLinesAdded();
				currentLinesOfCode -= rev.getLinesRemoved();
			}
			if ((previous != null) && (previous.isDead())) {
				if (!rev.isDead()) {
					rev.setState(CvsRevision.STATE_RE_ADDED);
				}
			}
			rev.setLinesOfCode(currentLinesOfCode);
		}
	}

	/**
	 * Starts building a new revision for the current file. The revisions
	 * must be built in reverse chronological order, that is, build the
	 * latest revision first. 
	 * @param revisionNumber the revision number, for example "1.12"
	 */
	public void buildRevisionBegin(String revisionNumber) {
		if (ignoreCurrentFile) {
			return;
		}
		currentRevNo = revisionNumber;
		currentRevDate = null;
		currentRevAuthor = null;
		currentRevState = -1;
		currentRevLinesAdded = 0;
		currentRevLinesRemoved = 0;
	}

	/**
	 * Sets the date for the current revision
	 * @param date the date
	 */
	public void buildRevisionDate(Date date) {
		if (ignoreCurrentFile) {
			return;
		}
		currentRevDate = date;
	}
	
	/**
	 * Sets the author's name for the current revision
	 * @param author the author's name
	 */
	public void buildRevisionAuthor(String author) {
		if (ignoreCurrentFile) {
			return;
		}
		currentRevAuthor = getAuthor(author);
	}
	
	/**
	 * Makes the current revision an initial revision
	 */
	public void buildRevisionStateInitial() {
		if (ignoreCurrentFile) {
			return;
		}
		currentRevState = CvsRevision.STATE_INITIAL_REVISION;
	}
	
	/**
	 * Makes the current revision a normal file-modifying revision.
	 * @param linesAdded number of lines added to this revision
	 * @param linesRemoved number of lines removed to this revision
	 */
	public void buildRevisionStateChange(int linesAdded, int linesRemoved) {
		if (ignoreCurrentFile) {
			return;
		}
		currentRevLinesAdded = linesAdded;
		currentRevLinesRemoved = linesRemoved;
		currentRevState = CvsRevision.STATE_NORMAL;
	}
	
	/**
	 * Makes the current revision a dead (deleted) revision
	 */
	public void buildRevisionStateDead() {
		if (ignoreCurrentFile) {
			return;
		}
		currentRevState = CvsRevision.STATE_DEAD;
	}

	/**
	 * Finishes building a revision
	 * @param comment the revision comment
	 */
	public void buildRevisionEnd(String comment) {
		if (ignoreCurrentFile) {
			return;
		}
		if (!CvsLogUtils.isOnMainBranch(currentRevNo)) {
			return;
		}
		CvsRevision newRevision = new CvsRevision(currentRevNo);
		newRevision.setAuthor(currentRevAuthor);
		newRevision.setComment(comment);
		newRevision.setDate(currentRevDate);
		newRevision.setState(currentRevState);
		newRevision.setLinesAdded(currentRevLinesAdded);
		newRevision.setLinesRemoved(currentRevLinesRemoved);
		currentFileRevisions.add(newRevision);
	}

	/**
	 * Calculates lines of code for all revisions of this file.
	 * Adding more revisions after calling this method might not work.
	 * 
	 * TODO: refactor
	 */
	private void calculateLinesOfCode(CvsFile file) {
		if (file.isBinary()) {
			Iterator it = file.getRevisionIterator();
			while (it.hasNext()) {
				CvsRevision revision = (CvsRevision) it.next();
				revision.setLinesOfCode(0);
			}
			return;
		}

		int currentLinesOfCode;
		if (file.isDead() || repositoryFileManager == null) {
			currentLinesOfCode = getLinesOfCodeWithoutHead(file);
		} else {
			try {
				currentLinesOfCode = repositoryFileManager.getLinesOfCode(
						file.getFilenameWithPath());
			} catch (RepositoryException e) {
				currentLinesOfCode = getLinesOfCodeWithoutHead(file);
			}
		}	

		CvsRevision previous = null;
		Iterator it = file.getRevisionIterator();
		while (it.hasNext()) {
			CvsRevision revision = (CvsRevision) it.next();
			if (revision.isDead() && previous != null) {
				if (previous.isDead()) {
					logger.warning(
							"adjacent dead revisions - should not happen");
				} else {
					previous.setState(CvsRevision.STATE_RE_ADDED);
				}
			}
			revision.setLinesOfCode(currentLinesOfCode);
			currentLinesOfCode -= revision.getLinesAdded();
			currentLinesOfCode += revision.getLinesRemoved();
			previous = revision;
		}
	}
	
	private int getLinesOfCodeWithoutHead(CvsFile file) {
		int max = 0;
		int current = 0;
		Iterator it = file.getRevisionIterator();
		while (it.hasNext()) {
			CvsRevision revision = (CvsRevision) it.next();
			current += revision.getLinesAdded();
			max = Math.max(current, max);
			current -= revision.getLinesRemoved();
		}
		return max;
	}	
		
	/**
	 * Returns a CvsContent object of all files
	 * @return CvsContent a CvsContent object
	 */
	public CvsContent getCvsContent() {
		return cvsContent;
	}
	
	/**
	 * Takes a filename and checks if it should be processed or not.
	 * Can be used to filter out unwanted files.
	 * 
	 * @param workingFile the filename
	 * @return <tt>true</tt> if this file should not be processed
	 */
	private boolean isFilteredFile(String workingFileName) {
		return workingFileName.startsWith("CVSROOT")
				|| !ConfigurationOptions.matchesPatterns(workingFileName);
	}

	/**
	 * Takes the revision list of a file and returns <tt>true</tt> if the
	 * file was added on another branch and not merged into the main branch
	 * (that is, it is not present on the main branch). Such a file will
	 * have only one revision on the main branch, which is dead. All revisions
	 * on other branches will have been filtered out before.
	 * @param revisions a <tt>List</tt> of
	 *                  {@link net.sf.statcvs.model.CvsRevision}s
	 * @return <tt>true</tt> if the file is not present on the main branch
	 */
	private boolean isOnOtherBranch(List revisions) {
		return (revisions.size() == 1
				&& ((CvsRevision) revisions.get(0)).isDead());
	}

	/**
	 * @param for example "src/net/sf/statcvs/"
	 * @return the <tt>Directory</tt> corresponding to <tt>statcvs</tt>
	 */
	private Directory getDirectoryForPath(String path) {
		if (directories.containsKey(path)) {
			return (Directory) directories.get(path);
		}
		Directory parent =
				getDirectoryForPath(FileUtils.getParentDirectoryPath(path));
		Directory newDirectory =
				new DirectoryImpl(parent, FileUtils.getDirectoryName(path));
		directories.put(path, newDirectory);
		return newDirectory;
	}
}