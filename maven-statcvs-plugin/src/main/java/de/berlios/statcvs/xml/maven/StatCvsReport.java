package de.berlios.statcvs.xml.maven;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.statcvs.model.CvsContent;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.reporting.MavenReportException;
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
	
	public void executeForked(String[] args) throws MavenReportException
	{
		Set ids = new HashSet();
		ids.add("statcvs-xml");
		ids.add("jfreechart");
		ids.add("jcommon");
		ids.add("jdom");
		ids.add("commons-logging");
		ids.add("commons-jexl");
		
		JavaCommandLine cli = new JavaCommandLine();
		cli.setWorkingDirectory(mojo.getWorkingDirectory());
		cli.setExecutable(mojo.getJvm());
		cli.setMainClass("de.berlios.statcvs.xml.Main");
		for (Iterator it = mojo.getPluginArtifacts().iterator(); it.hasNext();) {
			Artifact artifact = (Artifact)it.next();
			if (ids.contains(artifact.getArtifactId())) {
				cli.addClassPath(artifact.getFile());
			}
		}
		
		try {
			Writer writer = cli.run(args);
			cli.print(mojo.getLog(), writer);
		}
		catch (Exception e) {
			throw new MavenReportException("Error while executing StatCvs.", e);
		}
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
