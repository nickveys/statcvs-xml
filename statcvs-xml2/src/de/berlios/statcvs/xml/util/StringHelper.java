package de.berlios.statcvs.xml.util;

/**
 * @author Steffen Pingel
 */
public class StringHelper {

	public static String escapeFilename(String filename)
	{
		if (filename.endsWith("/")) {
			filename = filename.substring(0, filename.length() - 1);
		}
		filename = filename.replaceAll("/", "_");
		filename = filename.replaceAll("#", "_");
		return filename;
	}

}
