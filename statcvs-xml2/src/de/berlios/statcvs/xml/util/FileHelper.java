package de.berlios.statcvs.xml.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import de.berlios.statcvs.xml.Main;

/**
 * Provides static file helper methods.
 * 
 * @author Steffen Pingel
 * @version $Id: FileHelper.java,v 1.1 2004-02-15 14:22:19 squig Exp $
 */
public class FileHelper {

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
