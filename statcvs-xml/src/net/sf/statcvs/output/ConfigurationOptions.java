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
	$Date: 2003-06-17 16:43:02 $ 
*/
package net.sf.statcvs.output;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.sf.statcvs.util.FilePatternMatcher;
import net.sf.statcvs.util.FileUtils;

/**
 * Class for storing all command line parameters. The parameters
 * are set by the {@link net.sf.statcvs.Main#main} method. Interested classes
 * can read all parameter values from here.
 * 
 * @author jentzsch
 * @version $Id: ConfigurationOptions.java,v 1.1 2003-06-17 16:43:02 vanto Exp $
 */
public class ConfigurationOptions {

	private static final String LOGGING_CONFIG_DEFAULT = "logging.properties";
	private static final String LOGGING_CONFIG_VERBOSE = "logging-verbose.properties";
	private static final String LOGGING_CONFIG_DEBUG = "logging-debug.properties";

	private static String logFileName = null;
	private static String checkedOutDirectory = null;
	private static String projectTitle = null;
	private static String outputDir = "";
	private static String loggingProperties = LOGGING_CONFIG_DEFAULT;
	private static boolean showCreditInformation = true;
	private static String notesFile = null;
	private static String notes = null;

	private static FilePatternMatcher includePattern = null;
	private static FilePatternMatcher excludePattern = null;

	private static CssHandler cssHandler = new DefaultCssHandler("statcvs.css");
	private static WebRepositoryIntegration webRepository = null;

	private static String outputSuite = null;

	/**
	 * returns the {@link CssHandler}
	 * @return the CssHandler
	 */
	public static CssHandler getCssHandler() {
		return cssHandler;
	}
	
	/**
	 * Method getProjectName.
	 * @return String name of the project
	 */
	public static String getProjectName() {
		return projectTitle;
	}

	/**
	 * Method getCheckedOutDirectory.
	 * @return String name of the checked out directory
	 */
	public static String getCheckedOutDirectory() {
		return checkedOutDirectory;
	}

	/**
	 * Method getLogfilename.
	 * @return String name of the logfile to be parsed
	 */
	public static String getLogFileName() {
		return logFileName;
	}

	/**
	 * Returns the outputDir.
	 * @return String output Directory
	 */
	public static String getOutputDir() {
		return outputDir;
	}

	/**
	 * Returns the report notes (from "-notes filename" switch) or <tt>null</tt>
	 * if not specified
	 * @return the report notes
	 */
	public static String getNotes() {
		return notes;
	}

	public static String getOutputSuite()
	{
		return outputSuite;
	}

	/**
	 * Returns whether the credit information should be shown
	 * @return boolean showCreditInformation
	 */
	public static boolean getShowCreditInformation() {
		return showCreditInformation;
	}

	/**
	 * Returns a {@link WebRepositoryIntegration} object if the user
	 * has specified a URL to one. <tt>null</tt> otherwise.
	 * @return the web repository
	 */
	public static WebRepositoryIntegration getWebRepository() {
		return webRepository;
	}

	/**
	 * Sets the checkedOutDirectory.
	 * @param checkedOutDirectory The checkedOutDirectory to set
	 * @throws ConfigurationException if directory does not exist
	 */
	public static void setCheckedOutDirectory(String checkedOutDirectory) 
			throws ConfigurationException {
		File directory = new File (checkedOutDirectory);
		if (!directory.exists() || !directory.isDirectory()) {
			throw new ConfigurationException(
					"directory does not exist: " + checkedOutDirectory);
		}
		ConfigurationOptions.checkedOutDirectory = checkedOutDirectory;
	}

	/**
	 * Sets the cssFile. Currently, the css file can be any local file or
	 * a HTTP URL. If it is a local file, a copy will be included in the
	 * output directory. If this method is never called, a default CSS file
	 * will be generated in the output directory.
	 *
	 * @param cssFile The cssFile to set
	 * @throws ConfigurationException if the specified CSS file can not be
	 * accessed from local file system or from URL source, or if the specified
	 * CSS file is local and does not exist
	 */
	public static void setCssFile(String cssFile) throws ConfigurationException {
		try {
			URL url = new URL(cssFile);
			if (!url.getProtocol().equals("http")) {
				throw new ConfigurationException(
						"Only HTTP URLs or local files allowed for -css");
			}
			cssHandler = new UrlCssHandler(url);
		} catch (MalformedURLException isLocalFile) {
			cssHandler = new LocalFileCssHandler(cssFile);
		}
		cssHandler.checkForMissingResources();
	}

	/**
	 * Sets the logFileName.
	 * @param logFileName The logFileName to set
	 * @throws ConfigurationException if the file does not exist
	 */
	public static void setLogFileName(String logFileName) throws ConfigurationException {
		File inputFile = new File(logFileName);
		if (!inputFile.exists()) {
			throw new ConfigurationException(
					"Specified logfile not found: " + logFileName);
		}
		ConfigurationOptions.logFileName = logFileName;
	}

	/**
	 * Sets the outputDir.
	 * @param outputDir The outputDir to set
	 * @throws ConfigurationException if the output directory cannot be created
	 */
	public static void setOutputDir(String outputDir) throws ConfigurationException {
		if (!outputDir.endsWith(FileUtils.getDirSeparator())) {
			outputDir += FileUtils.getDefaultDirSeparator();
		}
		File outDir = new File(outputDir);
		if (!outDir.exists()) {
			outDir.mkdir();
		}
		if (outDir.length() > 0 && (!outDir.exists() || !outDir.isDirectory())) {
			throw new ConfigurationException(
					"Can't create output directory: " + outputDir);
		}
		ConfigurationOptions.outputDir = outputDir;
	}

	/**
	 * Sets the name of the notes file. The notes file will be included
	 * on the {@link IndexPage} of the output. It must contain a valid
	 * block-level HTML fragment (for example
	 * <tt>"&lt;p&gt;Some notes&lt;/p&gt;"</tt>) 
	 * @param notesFile a local filename
	 * @throws ConfigurationException if the file is not found or can't be read
	 */
	public static void setNotesFile(String notesFile) throws ConfigurationException {
		File f = new File(notesFile);
		if (!f.exists()) {
			throw new ConfigurationException(
					"Notes file not found: " + notesFile);
		}
		if (!f.canRead()) {
			throw new ConfigurationException(
					"Can't read notes file: " + notesFile);
		}
		ConfigurationOptions.notesFile = notesFile;
		try {
			notes = readNotesFile();
		} catch (IOException e) {
			throw new ConfigurationException(e.getMessage());
		}
	}

	/**
	 * Sets the URL to a <a href="http://viewcvs.sourceforge.net/">ViewCVS</a>
	 * web-based CVS browser. This must be the URL at which the checked-out
	 * module's root can be viewed in ViewCVS.
	 * @param url URL to a ViewCVS repository
	 */
	public static void setViewCvsURL(String url) {
		ConfigurationOptions.webRepository = new ViewCvsIntegration(url);
	}

	/**
	 * Sets the URL to a 
	 * <a href="http://www.freebsd.org/projects/cvsweb.html">cvsweb</a>
	 * web-based CVS browser. This must be the URL at which the checked-out
	 * module's root can be viewed in cvsweb.
	 * @param url URL to a cvsweb repository
	 */
	public static void setCvswebURL(String url) {
		ConfigurationOptions.webRepository = new CvswebIntegration(url);
	}

	/**
	 * Sets the URL to a <a href="http://www.horde.org/chora/">Chora</a>
	 * web-based CVS browser. This must be the URL at which the checked-out
	 * module's root can be viewed in Chora.
	 * @param url URL to a cvsweb repository
	 */
	public static void setChoraURL(String url) {
		ConfigurationOptions.webRepository = new ChoraIntegration(url);
	}

	/**
	 * Sets a project title to be used in the reports
	 * @param projectTitle The project title to be used in the reports
	 */
	public static void setProjectTitle(String projectTitle) {
		ConfigurationOptions.projectTitle = projectTitle;
	}

	/**
	 * Gets the name of the logging properties file
	 * @return the name of the logging properties file
	 */
	public static String getLoggingProperties() {
		return loggingProperties;
	}

	/**
	 * Sets the logging level to verbose
	 */
	public static void setVerboseLogging() {
		ConfigurationOptions.loggingProperties = LOGGING_CONFIG_VERBOSE;
	}

	/**
	 * Sets the logging level to debug
	 */
	public static void setDebugLogging() {
		ConfigurationOptions.loggingProperties = LOGGING_CONFIG_DEBUG;
	}

	private static String readNotesFile() throws IOException {
		BufferedReader r = new BufferedReader(new FileReader(notesFile));
		String line = r.readLine();
		String result = "";
		while (line != null) {
			result += line; 
			line = r.readLine();
		}
		return result;
	}

	/**
	 * Enabe or disable the credit information in the generated charts
	 * @param enable Enabe or disable the credit information in the generated charts
	 */
	public static void setShowCreditInformation(boolean enable) {
		showCreditInformation = enable;
	}

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

	public static void setOutputSuite(String outputSuite) {
		ConfigurationOptions.outputSuite = outputSuite;
	}

}
