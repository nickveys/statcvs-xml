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
    
	$RCSfile: CvsLogUtils.java,v $
	$Date: 2003-06-17 16:43:02 $
*/
package net.sf.statcvs.util;

/**
 * Utility class containing various methods related to CVS logfile parsing
 * 
 * @author Richard Cyganiak <rcyg@gmx.de>
 * @version $Id: CvsLogUtils.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class CvsLogUtils {

	/**
	 * <p>Determines if a file is in the attic by comparing the location of
	 * the RCS file and the working file.</p>
	 * 
	 * <p>The RCS file is the file containing the version history.
	 * It is located in the CVSROOT directory of the repository.
	 * It's name ends in ",v". The filename is absoulte.</p>
	 * 
	 * <p>The working filename is the actual filename relative to the
	 * root of the checked-out module.</p>
	 * 
	 * <p>A file is said to be in the attic if and only if it is dead
	 * on the main branch. If a file is in the attic, it's RCS file is
	 * moved to a subdirectory called "Attic". This method checks if
	 * the RCS file is in the "Attic" subdirectory.
	 *  
	 * @param rcsFilename a version-controlled file's RCS filename
	 * @param workingFilename a version-controlled file's working filename
	 * @return <tt>true</tt> if the file is in the attic
	 */
	public static boolean isInAttic(String rcsFilename, String workingFilename) {
		int lastDelim = workingFilename.lastIndexOf("/");
		String filename =
				workingFilename.substring(lastDelim + 1, workingFilename.length());

		int rcsPathLength = rcsFilename.length() - filename.length() - 2;
		String rcsPath = rcsFilename.substring(0, rcsPathLength);
		return rcsPath.endsWith("/Attic/");
	}

	/**
	 * Returns <code>true</code> if this revision is part of the main branch,
	 * and <code>false</code> if it is part of a side branch. Revisions
	 * like 1.1 and 5.212 are on the main branch, 1.1.1.1 and 1.4.2.13 and
	 * 1.4.2.13.4.1 are on side branches.
	 * @param revisionNumber the revision's number, for example "1.1"
	 * @return <code>true</code> if this revision is part of the main branch.
	 */
	public static boolean isOnMainBranch(String revisionNumber) {
		int index = 0;
		int dotCount = 0;
		while (revisionNumber.indexOf('.', index) != -1) {
			index = revisionNumber.indexOf('.', index) + 1;
			dotCount++;
		}
		return (dotCount == 1);
	}

	/**
	 * Determines the module name by comparing the RCS filename and
	 * the working filename.  
	 * @param rcsFilename a version-controlled file's RCS filename
	 * @param workingFilename a version-controlled file's working filename
	 * @return the module name
	 */
	public static String getModuleName(String rcsFilename, String workingFilename) {
		int localLenght = workingFilename.length() + ",v".length();
		if (CvsLogUtils.isInAttic(rcsFilename, workingFilename)) {
			localLenght += "/Attic".length();
		}
		String cvsroot = rcsFilename.substring(0,
				rcsFilename.length() - localLenght - 1);
		int lastSlash = cvsroot.lastIndexOf("/");
		if (lastSlash == -1) {
			return "";
		}
		return cvsroot.substring(lastSlash + 1);
	}
}
