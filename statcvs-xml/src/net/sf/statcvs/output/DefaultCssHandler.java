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
    
	$RCSfile: DefaultCssHandler.java,v $
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.sf.statcvs.Main;
import net.sf.statcvs.util.FileUtils;

/**
 * CSS handler for a CSS file included in the distribution JAR file.
 * 
 * @author Richard Cyganiak
 */
public class DefaultCssHandler implements CssHandler {

	private static Logger logger =
		Logger.getLogger("net.sf.statcvs.output.CssHandler");

	private String filename;
	
	/**
	 * Creates a new DefaultCssHandler for a CSS file in the
	 * <code>/src/net/sf/statcvs/web-files/</code> folder of the distribution JAR.
	 * This must be a filename only, without a directory.
	 * @param filename Name of the css file
	 */
	public DefaultCssHandler(String filename) {
		this.filename = filename;
	}

	/**
	 * @see net.sf.statcvs.output.CssHandler#getLink()
	 */
	public String getLink() {
		return filename;
	}

	/**
	 * No external resources are necessary for default CSS files, so
	 * nothing is done here
	 * @see net.sf.statcvs.output.CssHandler#checkForMissingResources()
	 */
	public void checkForMissingResources() throws ConfigurationException {
		// do nothing
	}

	/**
	 * Extracts the CSS file from the distribution JAR and saves it
	 * into the output directory
	 * @see net.sf.statcvs.output.CssHandler#createOutputFiles()
	 */
	public void createOutputFiles() throws IOException {
		String destination = FileUtils.getFilenameWithDirectory(filename);
		logger.info("Creating CSS file at '" + destination + "'");
		FileUtils.copyFile(
			Main.class.getResourceAsStream(HTMLOutput.WEB_FILE_PATH + filename),
			new File(destination));
	}

	/**
	 * toString
	 * @return string
	 */
	public String toString() {
		return "default CSS file (" + filename + ")";
	}
}
