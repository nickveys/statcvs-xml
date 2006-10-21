/*
 * Copyright 2005, 2006 Tammo van Lessen, Steffen Pingel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.berlios.statcvs.xml.maven;

import java.io.File;
import java.io.IOException;
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

/**
 * Wrapper around a StatCvs report generation.
 */
public class StatCvsReport {

    /** CVS Log file to parse */
    private File logFile;

    /** Report mojo in execution */
    private StatCvsMojo mojo;

    /**
     * Create a new statcvs report.
     * 
     * @param mojo report mojo executing
     * @param logFile cvs logfile to parse
     */
    public StatCvsReport(StatCvsMojo mojo, File logFile) {
        this.mojo = mojo;
        this.logFile = logFile;
    }

    /**
     * Execute statcvs to generate the report content.
     * 
     * @throws MavenReportException
     */
    public void execute() throws MavenReportException {
        mojo.getLog().info("Executing StatCVS");
        String[] args = getStatCvsArgs();
        if (mojo.isFork()) {
            executeForked(args);
        } else {
            executeInline(args);
        }

        String outputDirectory = mojo.getOutputDirectory();

        /* Locate all png output from the report */
        DirectoryScanner ds = new DirectoryScanner();
        ds.setIncludes(new String[] { "**/*.png" });
        ds.setBasedir(outputDirectory);
        ds.scan();

        /* Copy images to resource directory for inclusion */
        String[] files = ds.getIncludedFiles();
        mojo.getLog().debug("DirectoryScanner matched: " + Arrays.asList(files));
        String resOutputDirectory = mojo.getResourceOutputDirectory();
        mojo.getLog().info("Copying " + files.length + " files to " + resOutputDirectory);
        for (int i = 0; i < files.length; i++) {
            File src = new File(outputDirectory, files[i]);
            File dest = new File(resOutputDirectory, files[i]);
            try {
                FileUtils.copyFile(src, dest);
            } catch (IOException e) {
                throw new MavenReportException("Error copying images to resource dir");
            }
        }
    }

    /**
     * Execute statcvs in a forked process.
     * 
     * @param args arguments to statcvs
     * 
     * @throws MavenReportException
     */
    private void executeForked(String[] args) throws MavenReportException {
        Set ids = new HashSet();
        ids.add("statcvs-xml");
        ids.add("jfreechart");
        ids.add("jcommon");
        ids.add("jdom");
        ids.add("commons-logging");
        ids.add("commons-jexl");

        JavaCommandLine cli = new JavaCommandLine();
        cli.setWorkingDirectory(mojo.getCvsSourceLocation());
        cli.setJvm(mojo.getJvm());
        cli.setMainClass("de.berlios.statcvs.xml.Main");
        for (Iterator it = mojo.getPluginArtifacts().iterator(); it.hasNext();) {
            Artifact artifact = (Artifact) it.next();
            if (ids.contains(artifact.getArtifactId())) {
                cli.addClassPath(artifact.getFile());
            }
        }

        try {
            mojo.getLog().debug("Executing " + Arrays.asList(cli.getCommandline()));
            cli.run(args, mojo.getLog());
        } catch (Exception e) {
            throw new MavenReportException("Error while executing StatCvs.", e);
        }
    }

    /**
     * Execute statcvs using the statcvs wrapper classes.
     * 
     * @param args arguments to statcvs
     * 
     * @throws MavenReportException
     */
    private void executeInline(String[] args) throws MavenReportException {
        try {
            mojo.getLog().debug("Reading settings: " + Arrays.asList(args));
            ReportSettings settings = Main.readSettings(args);
            mojo.getLog().debug("Generating content");
            CvsContent content = Main.generateContent(settings);
            mojo.getLog().debug("Generating suite");
            Main.generateSuite(settings, content);
        } catch (Exception e) {
            throw new MavenReportException("Could not create StatCvs reports", e);
        }
    }

    /**
     * Get the arguments to be passed to statcvs.
     * 
     * @return statcvs arguments
     */
    private String[] getStatCvsArgs() {
        List args = new ArrayList();

        args.add("-output-dir");
        args.add(mojo.getOutputDirectory());

        args.add("-title");
        args.add(mojo.getTitle());

        args.add("-renderer");
        args.add(mojo.getRenderer());

        if (mojo.isVerbose()) {
            args.add("-verbose");
        }

        if (mojo.getIncludes() != null) {
            args.add("-include");
            mojo.getIncludes();
        }

        if (mojo.getExcludes() != null) {
            args.add("-exclude");
            mojo.getExcludes();
        }

        if (mojo.isParsePOM()) {
            args.add("-maven2");
        }

        if (!mojo.isAuthorPictures()) {
            args.add("-no-images");
        }

        args.add(logFile.getAbsolutePath());
        args.add(mojo.getCvsSourceLocation().getAbsolutePath());

        return (String[]) args.toArray(new String[0]);
    }
}
