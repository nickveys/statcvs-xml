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

import java.util.HashSet;
import java.util.Set;

import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.model.CvsRevision;
import net.sf.statcvs.model.Directory;

/**
 * Integration of the <a href="http://www.horde.org/chora/">Chora CVS Viewer</a>
 *
 * @author Richard Cyganiak
 * @version $Id$
 */
public class ChoraIntegration implements WebRepositoryIntegration {
	private String baseURL;
	private Set atticFileNames = new HashSet();

	/**
	 * @param baseURL base URL of the Chora installation 
	 */
	public ChoraIntegration(String baseURL) {
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
		return "Chora";
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
		if (isInAttic(file)) {
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
		return getFileHistoryUrl(file) + "?r=HEAD";
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getFileViewUrl
	 */
	public String getFileViewUrl(CvsRevision revision) {
		return getFileHistoryUrl(revision.getFile()) + "?r="
				+ revision.getRevisionNumber();
	}

	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#getDiffUrl
	 */
	public String getDiffUrl(CvsRevision oldRevision, CvsRevision newRevision) {
		if (!oldRevision.getFile().equals(newRevision.getFile())) {
			throw new IllegalArgumentException("revisions must be of the same file");
		}
		return getFileHistoryUrl(oldRevision.getFile())
				+ "?r1=" + oldRevision.getRevisionNumber()
				+ "&r2=" + newRevision.getRevisionNumber();
	}
	
	/**
	 * @see net.sf.statcvs.output.WebRepositoryIntegration#setAtticFileNames(java.util.Set)
	 */
	public void setAtticFileNames(Set atticFileNames) {
		this.atticFileNames = atticFileNames;
	}

	private boolean isInAttic(CvsFile file) {
		return atticFileNames.contains(file.getFilenameWithPath());
	}
}
