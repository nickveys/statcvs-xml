package de.berlios.statcvs.xml.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sf.statcvs.model.CvsContent;
import org.apache.maven.reporting.MavenReportException;
import org.apache.tools.ant.taskdefs.ExecuteJava;
import org.codehaus.plexus.util.FileUtils;
import de.berlios.statcvs.xml.Main;
import de.berlios.statcvs.xml.output.ReportSettings;


public class StatCvsReport {

	private File logFile;
	private StatCvsMojo mojo;

	public StatCvsReport(StatCvsMojo mojo, File logFile)
	{
		this.mojo = mojo;
		this.logFile = logFile;
	}

	public void execute() throws MavenReportException
	{
		String outputDirectory = mojo.getOutputDirectory();
		
		if (FileUtils.fileExists(outputDirectory)) {
			FileUtils.mkdir(outputDirectory);
		}
		
		String[] args = getStatCvsArgs(outputDirectory);
		if (mojo.isFork()) {
			executeForked(args);
		}
		else {
			executeInline(args);
		}
	}

	private String[] getStatCvsArgs(String outputDirectory)
	{
		List args = new ArrayList();
		args.add("-output-dir"); args.add(outputDirectory);
		args.add("-title"); args.add(mojo.getTitle());
		args.add("-renderer"); args.add(mojo.getRenderer());
		if (mojo.isVerbose()) {
			args.add("-verbose");
		}
		if (mojo.getIncludes() != null) {
			args.add("-include"); mojo.getIncludes();
		}
		if (mojo.getExcludes() != null) {
			args.add("-exclude"); mojo.getExcludes();
		}
		/*
		if (mojo.isParsePOM()) {
			args.add("-maven2");
		}
		*/
		if (!mojo.isAuthorPictures()) {
			args.add("-no-images");
		}
		args.add(logFile.getAbsolutePath());
		args.add(mojo.getWorkingDirectory().getAbsolutePath());

		return (String[])args.toArray(new String[0]);
	}
	
	public void executeForked(String[] args)
	{
		ExecuteJava task = new ExecuteJava();
		/* 
		  <jvmargs>
		  <ant:classpath>
			<ant:pathelement location="${plugin.getDependencyPath('statcvs:statcvs-xml')}"/>
			<ant:pathelement location="${plugin.getDependencyPath('jfreechart:jfreechart')}"/>
			<ant:pathelement location="${plugin.getDependencyPath('jcommon:jcommon')}"/>
			<ant:pathelement location="${plugin.getDependencyPath('jdom:jdom')}"/>
			<ant:pathelement location="${plugin.getDependencyPath('commons-logging:commons-logging')}"/>
			<ant:pathelement location="${plugin.getDependencyPath('commons-jexl:commons-jexl'	)}"/>
		  </ant:classpath>
		*/
		task.run();
	}
	
	public void executeInline(String[] args) throws MavenReportException
	{
		try {
			ReportSettings settings = Main.readSettings(args);
			CvsContent content = Main.generateContent(settings);
			Main.generateSuite(settings, content);
		}
		catch (Exception e) {
			throw new MavenReportException("Could not create StatCvs reports", e);
		}
	}
	
}
