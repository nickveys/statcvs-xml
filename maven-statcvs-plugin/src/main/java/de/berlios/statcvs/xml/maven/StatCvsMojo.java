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
import java.util.List;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.scm.manager.ScmManager;

/**
 * Generates a StatCvs report.
 * 
 * TODO HTML Generation, I18N
 * 
 * @goal report
 * @phase process-sources
 */
public class StatCvsMojo extends AbstractMavenReport {

    /**
     * Specifies if pictures should be displayed on the author pages.
     * 
     * @parameter default-value="false"
     */
    private boolean authorPictures;

    /**
     * The SCM connection URL.
     * 
     * @parameter expression="${connectionUrl}"
     *            default-value="${project.scm.developerConnection}"
     */
    private String connectionUrl;

    /**
     * Determines the files not to consider when generating the report. Uses ant
     * style file-pattern matching.
     * 
     * @parameter
     */
    private String[] excludes;

    /**
     * Specifies if the StatCvs should be invoked in a separate JVM.
     * 
     * @parameter default-value="false"
     */
    private boolean fork;

    /**
     * When set to true, the plugin will check out a complete copy of all 1.1
     * revisions of the repository defined in the pom to the target directory
     * and fetch the cvs log from there. This will enable StatCvs to correctly
     * determine the lines of code for deleted files and directories.
     * 
     * @parameter default-value="false"
     */
    private boolean history;

    /**
     * When set to false, less output will be generated.
     * 
     * @parameter default-value="true"
     */
    private boolean verbose;

    /**
     * Temporary directory where log file is saved.
     * 
     * @parameter expression="${project.build.directory}/statcvs"
     */
    private File workingDirectory;

    /**
     * Directory to check out history copy to when using history.
     * 
     * @parameter expression="${project.build.directory}/statcvs/history"
     */
    private File historyDirectory;

    /**
     * Determines the files to consider when generating the report. Uses ant
     * style file-pattern matching.
     * 
     * @parameter
     */
    private String[] includes;

    /**
     * SCM Manager
     * 
     * @parameter expression="${component.org.apache.maven.scm.manager.ScmManager}"
     * @required
     * @readonly
     */
    private ScmManager manager;

    /**
     * List of of plugin artifacts.
     * 
     * @parameter expression="${plugin.artifacts}"
     */
    private List pluginArtifacts;

    /**
     * @parameter default-value="${project.reporting.outputDirectory}"
     * @required
     */
    private File reportingDirectory;

    /**
     * Report output directory.
     * 
     * @parameter expression="${project.build.directory}/generated-site/xdoc/statcvs"
     * @required
     */
    private String outputDirectory;

    /**
     * Report resource output directory (for images, etc).
     * 
     * @parameter expression="${project.build.directory}/generated-site/resources/statcvs"
     * @required
     */
    private String resourceOutputDirectory;

    /**
     * Specifies if the pom should be used to determine real names.
     * 
     * @parameter default-value="true"
     */
    private boolean parsePOM;

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Specifies the StatCvs output renderer.
     * 
     * @parameter default-value="xdoc"
     * @required
     */
    private String renderer;

    /**
     * @component
     * @required
     * @readonly
     */
    private Renderer siteRenderer;

    /**
     * The base directory of the project.
     * 
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * Option to specify the jvm (or path to the java executable) to use with
     * the forking options. For the default we will assume that java is in the
     * path.
     * 
     * @parameter expression="${jvm}" default-value="java"
     */
    private String jvm;

    /**
     * Ensure given directory exists and is writable. The directory will be
     * created (and all parents if needed) if possible.
     * 
     * @param dir directory to create/verify
     * 
     * @return true if directory exists and is writable, false otherwise
     */
    private static boolean createAndCheckDirectory(File dir) {
        dir.mkdirs();
        return dir.exists() && dir.canWrite() && dir.isDirectory();
    }

    public boolean canGenerateReport() {
        /* Make sure we're dealing with a CVS stored project */
        if (!CvsConnection.isScmProviderCvs(manager, connectionUrl)) {
            getLog().warn("Project is not a CVS SCM project.");
            return false;
        }

        /* We currently only support forked calls, SCM needs work */
        if (!isFork()) {
            getLog().warn("Only forked mode is currently supported.  Forcing forked mode.");
            fork = true;
        }

        /* If we're forked, make sure we can hit the executables */
        if (isFork()) {
            if (!CvsConnection.isForkedCvsAvailable()) {
                getLog().warn("Unable to locate cvs executable, is it on the path?");
                return false;
            }
            if (!JavaCommandLine.isJavaAvailable(jvm)) {
                getLog().warn("Unable to locate " + jvm + " executable, is it on the path?");
                return false;
            }
        }

        /*
         * Check the various directories we'll need to manipulate
         */

        /* First and foremost, our output directories */
        File outDir = new File(getOutputDirectory());
        if (!createAndCheckDirectory(outDir)) {
            getLog().warn("Unable to create/write " + outDir + ".  Check permissions.");
            return false;
        }
        File resOutDir = new File(getResourceOutputDirectory());
        if (!createAndCheckDirectory(resOutDir)) {
            getLog().warn("Unable to create/write " + resOutDir + ".  Check permissions.");
            return false;
        }

        /* Make sure working directory is ok */
        if (!createAndCheckDirectory(getWorkingDirectory())) {
            getLog().warn("Unable to create/write " + getWorkingDirectory() + ".  Check permissions.");
            return false;
        }

        /* If we're using history, make sure we can use that directory too */
        if (isHistory()) {
            if (!createAndCheckDirectory(historyDirectory)) {
                getLog().warn("Unable to create/write " + historyDirectory + ".  Check permissions.");
                return false;
            }
        }

        return true;
    }

    /**
     * Get the basedir of the executing project.
     * 
     * @return path to basedir of project
     */
    public File getBasedir() {
        return basedir;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    /**
     * Get the directory containing CVS sources from which to generate the
     * report.
     * 
     * @return directory containing cvs files
     */
    public File getCvsSourceLocation() {
        return isHistory() ? historyDirectory : basedir;
    }

    public String getDescription(Locale locale) {
        // TODO: I18N
        return "Statistics about CVS usage generated by StatCvs-XML.";
    }

    public String[] getExcludes() {
        return excludes;
    }

    public String[] getIncludes() {
        return includes;
    }

    public String getJvm() {
        return jvm;
    }

    public String getName(Locale locale) {
        // TODO: I18N
        return "StatCvs Report";
    }

    public String getOutputName() {
        return "statcvs/index";
    }

    public List getPluginArtifacts() {
        return pluginArtifacts;
    }

    public String getRenderer() {
        return renderer;
    }

    public ScmManager getScmManager() {
        return manager;
    }

    public String getTitle() {
        // TODO: I18N
        return "StatCvs Report";
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public boolean isAuthorPictures() {
        return authorPictures;
    }

    public boolean isExternalReport() {
        return true;
    }

    public boolean isFork() {
        return fork;
    }

    public boolean isHistory() {
        return history;
    }

    public boolean isParsePOM() {
        return parsePOM;
    }

    public boolean isVerbose() {
        return verbose;
    }

    protected void executeReport(Locale locale) throws MavenReportException {
        // and start the report
        Sink sink = getSink();

        sink.head();
        sink.title();
        sink.text("StatCvs Report"); // TODO I18N
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();

        createReport();

        String dest = outputDirectory;
        String base = reportingDirectory.getAbsolutePath();
        String relativPath = dest.substring(base.length() + 1);
        sink.link(relativPath + "/index.html");
        sink.text(relativPath + "/index.html");
        sink.link_();

        sink.section1_();
        sink.body_();

        sink.flush();
        sink.close();
    }

    protected String getOutputDirectory() {
        return outputDirectory;
    }

    protected MavenProject getProject() {
        return project;
    }

    protected String getResourceOutputDirectory() {
        return resourceOutputDirectory;
    }

    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    private void createReport() throws MavenReportException {
        try {
            File logFile = new File(getWorkingDirectory(), "cvs.log");

            CvsConnection conneciton = new CvsConnection(this, logFile);
            conneciton.execute();

            StatCvsReport report = new StatCvsReport(this, logFile);
            report.execute();
        } catch (Exception e) {
            throw new MavenReportException("Could not create report", e);
        }
    }
}
