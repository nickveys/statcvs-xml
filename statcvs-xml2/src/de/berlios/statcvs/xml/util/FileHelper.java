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
import de.berlios.statcvs.xml.I18n;
import de.berlios.statcvs.xml.Main;

/**
 * Provides static file helper methods.
 * 
 * @author Steffen Pingel
 * @version $Id: FileHelper.java,v 1.5 2004-06-28 00:52:23 squig Exp $
 */
public class FileHelper {

	private static Logger logger
		= Logger.getLogger("de.berlios.statcvs.xml.util.FileHelper");

	public static boolean copyResource(String filename, File outputPath)
	{
		return copyResource(filename, outputPath, FileUtils.getFilenameWithoutPath(filename));
	}
	
	public static boolean copyResource(URL url, File outputPath, String filename)
	{
		try {
			return copyResource(url.openStream(), outputPath, filename);
		} 
		catch (IOException e) {
			logger.warning(I18n.tr("Could not open {0} ({1})", url, e.getLocalizedMessage()));
		}
		return false;
	}
	
	public static boolean copyResource(String source, File outputPath, String filename)
	{
		InputStream in = FileHelper.getResourceAsStream(source);
		if (in != null) {
			return copyResource(in, outputPath, filename);
		}
		else {
			logger.warning("Resource not found: " + source);
		}
		return false;
	}

	public static boolean copyResource(InputStream in, File outputPath, String filename)
	{
		try {
			FileUtils.copyFile(in, new File(outputPath, filename));
			return true;
		} 
		catch (IOException e) {
			logger.warning(e.getMessage());
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
