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
    
	$RCSfile: FileUtils.java,v $ 
	Created on $Date: 2003-07-24 00:40:06 $ 
*/
package net.sf.statcvs.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.statcvs.Main;
import net.sf.statcvs.Settings;

/**
 * Some helpful file functions
 * TODO: Remove redundancy and dependency on ConfigurationOptions, write tests
 * @author Lukasz Pekacki
 * @version $Id: FileUtils.java,v 1.5 2003-07-24 00:40:06 vanto Exp $
 */
public class FileUtils {
    /**
     * Copyies a file to a specified desitination
     * @param inputName File
     * @param destination Filename
     * @throws FileNotFoundException if no input file exists
     * @throws IOException if cannot read or write
     */
    public static void copyFile(String inputName, String destination)
        throws FileNotFoundException, IOException {
        File input = new File(inputName);
        File outputFile = new File(destination);
        FileReader in = new FileReader(input);
        FileWriter out = new FileWriter(outputFile);
        int c;
        while ((c = in.read()) != -1) {
            out.write(c);
        }
        in.close();
        out.close();
    }

    /**
     * Copy a InputStream into a File
     * @param in source
     * @param out destination
     * @throws FileNotFoundException if not found
     * @throws IOException if read/write error
     */
    public static void copyFile(InputStream in, File out)
        throws FileNotFoundException, IOException {

        InputStream fis = in;
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
        fis.close();
        fos.close();
    }

    /**
     * Return the full path to the specified filename
     * @param filename desired name of file
     * @return String the full path to the specified filename
     */
    public static String getFilenameWithDirectory(String filename) {
        return Settings.getOutputDir() + filename;
    }

    /**
     * Takes a filename with path and returns just the filename.
     * @param filename a filename with path
     * @return just the filename part
     */
    public static String getFilenameWithoutPath(String filename) {
        File f = new File(filename);
        return f.getName();
    }

    /**
     * Returns the os dependend path seperator
     * @return String os dependend path seperator
     */
    public static String getDirSeparator() {
        return System.getProperty("file.separator");
    }
    /**
     * Returns the java path seperator
     * @return String java  path seperator
     */
    public static String getDefaultDirSeparator() {
        // Thanks for this hint in our bug tracking system
        return java.io.File.separator;
    }

    /**
     * Deletes the ending directory separator of a 
     * given <code>path</code> if there is one and returns
     * the result.
     * Otherwise the path is unhandled returned.
     * 
     * <p>The separator is the one used bye the
     * underlying operating system and it is the one returned
     * bye the <code>getDirSeparator()</code> method.
     * 
     * @param path The <code>path</code> to delete the directory
     * separator from.
     * @return The <code>path</code> without the ending
     * directory separator.
     * @see net.sf.statcvs.util.FileUtils#getDirSeparator
     */
    public static String getPathWithoutEndingSlash(String path) {
        if (path.endsWith(getDefaultDirSeparator())) {
            int pos = path.lastIndexOf(getDefaultDirSeparator());
            return path.substring(0, pos);
        }
        return path;
    }

    /**
     * Concatenates <code>path</code> and filename to an
     * absolute filename by inserting the system file separator.
     * 
     * @param path The path to use.
     * @param filename The filename for concatenation.
     * @return The concatenated absolute filename.
     */
    public static String getAbsoluteName(String path, String filename) {
        return path + getDirSeparator() + filename;
    }

	
	/**
	 * Returns the last compontent of a directory path.
	 * @param path a directory, ending in "/", for example "src/net/sf/statcvs/"
	 * @return the last component of the path, for example "statcvs"
	 */
	public static String getDirectoryName(String path) {
		if ("".equals(path)) {
			throw new IllegalArgumentException("can't get directory name for root");
		}
		String pathWithoutLastSlash = path.substring(0, path.length() - 1);
		int lastSlash = pathWithoutLastSlash.lastIndexOf('/');
		if (lastSlash == -1) {
			return pathWithoutLastSlash;
		}
		return pathWithoutLastSlash.substring(lastSlash + 1);
	}
	
	/**
	 * Returns all but the last compontent of a directory path
	 * @param path a directory, ending in "/", for example "src/net/sf/statcvs/"
	 * @return all but the last component of the path, for example "src/net/sf/"
	 */
	public static String getParentDirectoryPath(String path) {
		if ("".equals(path)) {
			throw new IllegalArgumentException("can't get directory name for root");
		}
		String pathWithoutLastSlash = path.substring(0, path.length() - 1);
		int lastSlash = pathWithoutLastSlash.lastIndexOf('/');
		if (lastSlash == -1) {
			return "";
		}
		return pathWithoutLastSlash.substring(0, lastSlash + 1);
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

	/**
	 * Deletes directory and all subdirectories
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				if (!deleteDir(new File(dir, children[i]))) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * Returns all directories (with trailling seperator)
	 * relative to startDir but with
	 * absolute paths. CVS-dirs are ignored
	 * CONTAINS ITSELF!
	 * 
	 * @param startDir the starting dir
	 * @return List of dirpaths
	 */
	public static String[] getDirectories(String startDir) {
		List dirs = new ArrayList();
		dirs.add(startDir+File.separatorChar);
		
		File start = new File(startDir);
		File[] dirlist = start.listFiles(new DirFileFilter());
		for (int i = 0; i<dirlist.length; i++) {
			String[] subdirs = getDirectories(dirlist[i].getAbsolutePath());
			for (int j = 0; j < subdirs.length; j++) {
				dirs.add(subdirs[j]); 
			}
		}
		return (String[])dirs.toArray(new String[0]);
	}
	
	/**
	 * DirFileFilter
	 * Accepts only directories except CVS
	 * 
	 * @author Tammo van Lessen
	 * @version $Id: FileUtils.java,v 1.5 2003-07-24 00:40:06 vanto Exp $
	 */
	private static class DirFileFilter implements FileFilter {

		/**
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			return pathname.isDirectory() && !pathname.getName().equals("CVS");
		}
	}
}
