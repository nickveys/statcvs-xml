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
    
	$RCSfile: ConfigurationOptions.java,v $
	$Date: 2004-02-17 15:44:43 $ 
*/
package net.sf.statcvs.output;

import java.io.File;

import net.sf.statcvs.util.FilePatternMatcher;
import net.sf.statcvs.util.FileUtils;

/**
 * Class for storing all command line parameters. The parameters
 * are set by the {@link net.sf.statcvs.Main#main} method. Interested classes
 * can read all parameter values from here.
 * 
 * @author jentzsch
 * @version $Id: ConfigurationOptions.java,v 1.3 2004-02-17 15:44:43 squig Exp $
 */
public class ConfigurationOptions {

	private static FilePatternMatcher includePattern = null;
	private static FilePatternMatcher excludePattern = null;
	
	/**
	 * Sets a file include pattern list. Only files matching one of the
	 * patterns will be included in the analysis.
	 * @param patternList a list of Ant-style wildcard patterns, seperated
	 *                    by : or ;
	 * @see net.sf.statcvs.util.FilePatternMatcher
	 */
	public static void setIncludePattern(String patternList) {
		includePattern = new FilePatternMatcher(patternList);
	}
	
	/**
	 * Sets a file exclude pattern list. Files matching any of the
	 * patterns will be excluded from the analysis.
	 * @param patternList a list of Ant-style wildcard patterns, seperated
	 *                    by : or ;
	 * @see net.sf.statcvs.util.FilePatternMatcher
	 */
	public static void setExcludePattern(String patternList) {
		excludePattern = new FilePatternMatcher(patternList);
	}
	
	/**
	 * Matches a filename against the include and exclude patterns. If no
	 * include pattern was specified, all files will be included. If no
	 * exclude pattern was specified, no files will be excluded.
	 * @param filename a filename
	 * @return <tt>true</tt> if the filename matches one of the include
	 *         patterns and does not match any of the exclude patterns.
	 *         If it matches an include and an exclude pattern, <tt>false</tt>
	 *         will be returned.
	 */
	public static boolean matchesPatterns(String filename) {
		if (excludePattern != null && excludePattern.matches(filename)) {
			return false;
		}
		if (includePattern != null) {
			return includePattern.matches(filename);
		}
		return true;
	}

}
