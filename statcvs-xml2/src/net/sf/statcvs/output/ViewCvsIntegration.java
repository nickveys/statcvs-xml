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
 * Integration of ViewCVS
 *
 * @author Richard Cyganiak
 * @version $Id$
 */
public class ViewCvsIntegration implements WebRepositoryIntegration {

	private String baseURL;

	/**
	 * @param baseURL base URL of the ViewCVS installation 
	 */
	public ViewCvsIntegration(String baseURL) {
		if (baseURL.endsWith("/")) {
			this.baseURL = baseURL.substring(0, baseURL.length() - 1);
		} else {
			this.baseURL = baseURL;
		}
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getName
	 */
	public String getName() {
		return "ViewCVS";
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getDirectoryUrl
	 */
	public String getDirectoryUrl(Directory directory) {
		return baseURL + "/" + directory.getPath();
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getFileHistoryUrl
	 */
	public String getFileHistoryUrl(CvsFile file) {
		if (file.isInAttic()) {
			String path = file.getDirectory().getPath();
			String filename = file.getFilename();
			return baseURL + "/" + path + "Attic/" + filename;
		} else {
			return baseURL + "/" + file.getFilenameWithPath();
		}
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getFileViewUrl
	 */
	public String getFileViewUrl(CvsFile file) {
		return getFileHistoryUrl(file) + "?rev=HEAD&content-type=text/vnd.viewcvs-markup";
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getFileViewUrl
	 */
	public String getFileViewUrl(CvsRevision revision) {
		return getFileHistoryUrl(revision.getFile()) + "?rev="
				+ revision.getRevision() + "&content-type=text/vnd.viewcvs-markup";
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getDiffUrl
	 */
	public String getDiffUrl(CvsRevision oldRevision, CvsRevision newRevision) {
		if (!oldRevision.getFile().equals(newRevision.getFile())) {
			throw new IllegalArgumentException("revisions must be of the same file");
		}
		return getFileHistoryUrl(oldRevision.getFile())
				+ ".diff?r1=" + oldRevision.getRevision()
				+ "&r2=" + newRevision.getRevision();
	}
}
