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
*/
package net.sf.statcvs.input;

import java.util.Map;

/**
 * <p>Interface for defining a Builder that constructs a data structure from
 * a CVS logfile. {@link CvsLogfileParser} takes an instance of this
 * interface and will call methods on the interface for every piece of
 * data it encounters in the log.</p>
 * 
 * <p>First, {@link #buildModule} will be called with the name of the
 * module. Then, {@link #buildFile} will be called with the filename and
 * other pieces of information of the first file in the log. Then, for
 * every revision of this file, {@link #buildRevision} is called. The
 * calls to <tt>buildFile</tt> and <tt>buildRevision</tt> are repeated
 * for every file in the log.</p>
 * 
 * <p>The files are in no particular order. The revisions of one file
 * are ordered by time, beginning with the <em>most recent</em>.</p>
 * 
 * @author Richard Cyganiak <richard@cyganiak.de>
 * @author Tammo van Lessen
 * @version $Id$
 */
public interface CvsLogBuilder {
	
	/**
	 * Starts building a module.
	 * 
	 * @param moduleName the name of the module
	 */
	public abstract void buildModule(String moduleName);

	/**
	 * Starts building a new file. The files are not processed in any
	 * particular order.
	 * 
	 * @param filename the file's name with path relative to the module,
	 * for example "path/file.txt"
	 * @param isBinary <tt>true</tt> if it's a binary file
	 * @param isInAttic <tt>true</tt> if the file is dead on the main branch
     * @param revBySymnames maps revision (string) by symbolic name (string)
	 */
	public abstract void buildFile(String filename, boolean isBinary, 
                                     boolean isInAttic, Map revBySymnames);

	/**
	 * Adds a revision to the last file that was built.. The revisions are added in
	 * CVS logfile order, that is starting with the most recent one.
	 * 
	 * @param data the revision
	 */
	public abstract void buildRevision(RevisionData data);
}