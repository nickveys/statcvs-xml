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
    
	$RCSfile: StatCvsTask.java,v $
	$Date: 2003-07-04 14:38:44 $ 
*/
package net.sf.statcvs.ant;

import net.sf.statcvs.Main;
import net.sf.statcvs.output.ConfigurationException;
import net.sf.statcvs.output.ConfigurationOptions;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
/**
 * Ant task for running statcvs. 
 * 
 * @author Andy Glover
 * @author Richard Cyganiak
 */
public class StatCvsTask extends Task {
	private String outputSuite;
	private String webRepositoryType;
	private String webRepositoryUrl;
	private String title;
	private String logFile;
	private String pDir;
	private String outDir;
	private String cssFile;
	private String notesFile;
	private boolean showCredits = true;
	private String include = null;
	private String exclude = null;
	
	/**
	 * Constructor for StatCvsTask.
	 */
	public StatCvsTask() {
		super();
	}

	/**
	 * Runs the task
	 * @throws BuildException if an IO Error occurs
	 */
	public void execute() throws BuildException {
		try {
			this.initProperties();
			Main.run();
		} catch (Exception e) {
			throw new BuildException(e.getMessage());
		}
	}
	
	/**
	 * method initializes the ConfigurationOptions object with
	 * received values. 
	 */
	private void initProperties() throws ConfigurationException {

		// required params
		ConfigurationOptions.setLogFileName(this.logFile);
		ConfigurationOptions.setCheckedOutDirectory(this.pDir);
		
		// optional params
		if (this.title != null) {
			ConfigurationOptions.setProjectTitle(this.title);
		}
		if (this.outDir != null) {
			ConfigurationOptions.setOutputDir(this.outDir);
		}
		if (cssFile != null) {
			ConfigurationOptions.setCssFile(this.cssFile);
		}
		if (notesFile != null) {
			ConfigurationOptions.setNotesFile(this.notesFile);
		}
		if (!showCredits) {
			ConfigurationOptions.setShowCreditInformation(false);
		}
		if (webRepositoryType != null && webRepositoryUrl != null) {
			if (webRepositoryType.equals("cvsweb")) {
				ConfigurationOptions.setCvswebURL(webRepositoryUrl);
			} else if (webRepositoryType.equals("viewcvs")) {
				ConfigurationOptions.setViewCvsURL(webRepositoryUrl);
			} else if (webRepositoryType.equals("chora")) {
				ConfigurationOptions.setChoraURL(webRepositoryUrl);	
			}
		}
		if (include != null) {
			ConfigurationOptions.setIncludePattern(this.include);
		}
		if (exclude != null) {
			ConfigurationOptions.setExcludePattern(this.exclude);
		}
		if (outputSuite != null) {
			ConfigurationOptions.setOutputSuite(outputSuite);
		}
	}

	/**
	 * @param title String representing the title to be used in the reports
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param logFile String representing the cvs log file
	 */
	public void setCvsLogFile(String logFile) {
		this.logFile = logFile;
	}
	
	/**
	 * @param modDir String representing the directory containing the CVS project
	 */
	public void setProjectDirectory(String modDir) {
		this.pDir = modDir;
	}
	
	/**
	 * @param outDir String representing the output directory of the report
	 */
	public void setOutputDirectory(String outDir) {
		this.outDir = outDir;
	}
	
	/**
	 * @param cssFile String representing the CSS file to use for the report
	 */
	public void setCssFile(String cssFile) {
		this.cssFile = cssFile;
	}
	
	/**
	 * @param notesFile String representing the notes file to include on
	 * the report's index page
	 */
	public void setNotesFile(String notesFile) {
		this.notesFile = notesFile;
	}
	
	/**
	 * @param showCredits Show credit information in report?
	 */
	public void setShowCredits(boolean showCredits) {
		this.showCredits = showCredits;
	}
	
	/**
	 * @param url String representing the URL of a 
	 * Webrepository CVS installation
	 */
	public void setWebRepositoryUrl(String url) {
		this.webRepositoryUrl = url;
	}
	
	/**
	 * @param url String representing the vendor of a 
	 * Webrepository CVS installation
	 * 
	 * allowed values: "viewcvs", "cvsweb", "chora"
	 */
	public void setWebRepositoryType(String type) {
		this.webRepositoryType = type;
	}
	
	/**
	 * Specifies files to include in the analysis.
	 * @param include a list of Ant-style wildcard patterns, delimited by : or ;
	 * @see net.sf.statcvs.util.FilePatternMatcher
	 */
	public void setIncludeFiles(String include) {
		this.include = include;
	}
	
	/**
	 * Specifies files to exclude from the analysis.
	 * @param exclude a list of Ant-style wildcard patterns, delimited by : or ;
	 * @see net.sf.statcvs.util.FilePatternMatcher
	 */
	public void setExcludeFiles(String exclude) {
		this.exclude = exclude;
	}
	
	public void setOutputSuite(String suite) {
		this.outputSuite = suite;
	}
}
