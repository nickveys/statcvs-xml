/*
 *  StatCvs-XML - XML output for StatCvs.
 *
 *  Copyright by Steffen Pingel, Tammo van Lessen.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  version 2 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package de.berlios.statcvs.xml.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import net.sf.statcvs.util.FileUtils;
import de.berlios.statcvs.xml.Main;

/**
 * Provides static file helper methods.
 * 
 * @author Steffen Pingel
 * @version $Id: FileHelper.java,v 1.4 2004-02-27 17:30:39 vanto Exp $
 */
public class FileHelper {

	private static Logger logger
		= Logger.getLogger("de.berlios.statcvs.xml.util.FileHelper");

	public static boolean copyResource(String filename, File outputPath)
	{
		InputStream in = FileHelper.getResourceAsStream(filename);
		if (in != null) {
			try {
				FileUtils.copyFile(in, new File(outputPath, FileUtils.getFilenameWithoutPath(filename)));
				return true;
			} 
			catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}
		else {
			logger.warning("Resource not found: " + filename);
		}
		return false;
	}


	/**
	 * Returns a url to a resource. The classpath is searched first,
	 * then the file system is searched.
	 */
	public static URL getResource(String filename)
	{
		URL url = ClassLoader.getSystemResource(filename);
		if (url == null) {
			url = Main.class.getResource(filename);
			if (url == null) {
				try {
					url = new File(filename).toURL();
				}
				catch (MalformedURLException e) {
					return null;
				}
			}
		}
		return url;
	}

	/**
	 * 
	 */
	public static InputStream getResourceAsStream(String filename)
	{
		try {
			URL url = getResource(filename);
			if (url != null) {
				return url.openStream();
			}
		}
		catch (IOException e) {
		}
		return null;
	}

}
