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
    
	$RCSfile$
	$Date$ 
*/
package net.sf.statcvs.output;

import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

/**
 * Interface for integration of web repository browsers. Web repository
 * browsers are dynamic web sites where you can browse the contents of
 * a CVS repository, make diffs etc. A good example is
 * <a href="http://viewcvs.sourceforge.net/">ViewCVS</a>.
 * 
 * @author Richard Cyganiak
 * @version $Id$
 */
public interface WebRepositoryIntegration {

	/**
	 * Returns the name of the repository browser
	 * @return the name of the repository browser
	 */
	String getName();

	/**
	 * Returns a URL to a directory in the web repository browser
	 * @param directory the directory
	 * @return a URL to the directory in the web repository browser
	 */
	String getDirectoryUrl(Directory directory);

	/**
	 * Returns a URL to a file in the web repository browser. The
	 * URL points to a history of all revisions of the file.
	 * @param file the file
	 * @return a URL to the file in the web repository browser
	 */
	String getFileHistoryUrl(CvsFile file);

	/**
	 * Returns a URL to a file in the web repository browser. The
	 * URL points to a representation of the file's current contents.
	 * @param file the file
	 * @return a URL to the file in the web repository browser
	 */
	String getFileViewUrl(CvsFile file);

	/**
	 * Returns a URL to a file in the web repository browser. The
	 * URL points to a representation of the specific revision given
	 * as a parameter.
	 * @param revision the revision
	 * @return a URL to the revision in the web repository browser
	 */
	String getFileViewUrl(CvsRevision revision);

	/**
	 * Returns a URL to a diff in the web repository browser. Both revisions
	 * must belong to the same <tt>CvsFile</tt>.
	 * @param oldRevision the old revision
	 * @param newRevision the new revision
	 * @return a URL to a diff
	 */
	String getDiffUrl(CvsRevision oldRevision, CvsRevision newRevision);
}