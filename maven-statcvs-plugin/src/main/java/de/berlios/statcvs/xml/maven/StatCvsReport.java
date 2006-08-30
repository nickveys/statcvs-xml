package de.berlios.statcvs.xml.maven;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.statcvs.model.CvsContent;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.DirectoryScanner;
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
        FileUtils.mkdir( outputDirectory );
		
		String[] args = getStatCvsArgs(outputDirectory);
		if (mojo.isFork()) {
			executeForked(args);
		}
		else {
			executeInline(args);
		}

        /* Copy image resources to gen-site/resources for inclusion */
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes( new String[] { "**/*.png" } );
        ds.setBasedir( outputDirectory );
        ds.scan();

        String[] files = ds.getIncludedFiles();
        mojo.getLog().debug("DirectoryScanner matched: " + Arrays.asList(files));
        String resOutputDirectory = mojo.getResourceOutputDirectory();
        mojo.getLog().info("Copying " + files.length + " files to " + resOutputDirectory);
        FileUtils.mkdir( outputDirectory );
        for ( int i = 0; i < files.length; i++ ) {
            File src = new File( outputDirectory, files[i] );
            File dest = new File( resOutputDirectory, files[i] );
            try {
                FileUtils.copyFile( src, dest );
            } catch ( IOException e ) {
                throw new MavenReportException( "Error copying images to resource dir" );
            }
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
        mojo.getLog().info("Executing StatCVS");

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
            mojo.getLog().debug("Executing " + Arrays.asList(cli.getCommandline()));
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
            mojo.getLog().debug("Reading settings: " + Arrays.asList(args));
			ReportSettings settings = Main.readSettings(args);
            mojo.getLog().debug("Generating content");
			CvsContent content = Main.generateContent(settings);
            mojo.getLog().debug("Generating suite");
			Main.generateSuite(settings, content);
		}
		catch (Exception e) {
			throw new MavenReportException("Could not create StatCvs reports", e);
		}
	}
	
}
