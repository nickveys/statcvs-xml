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
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.cvslib.CvsScmProvider;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

/**
 * Abstraction over the various CVS operations the plugin must accomplish.
 * 
 * TODO compression, quiet, cvsRsh
 * 
 * @author Steffen Pingel
 */
public class CvsConnection {

    /** Current report mojo */
    private StatCvsMojo mojo;

    private ScmRepository scmRepository;

    private ScmProvider provider;

    /** Directory where the files to be updated or CVS logged are located */
    private File cvsSourceLocation;

    /** CVS Log file location */
    private File logFile;

    /**
     * Determine if the cvs executable is available on the path.
     * 
     * @return true if cvs can be found, false otherwise
     */
    public static boolean isForkedCvsAvailable() {
        try {
            new Commandline("cvs").execute();
        } catch (CommandLineException e) {
            return false;
        }
        return true;
    }

    /**
     * Determine if the SCM provider for the given manager and scm URL exists
     * and is a CVS provider.
     * 
     * @param manager scm manager for the project in question
     * @param scmUrl scm url for the project in question
     * 
     * @return true if the scm provider for the url is a cvs provider
     */
    public static boolean isScmProviderCvs(ScmManager manager, String scmUrl) {
        try {
            ScmProvider provider = manager.getProviderByUrl(scmUrl);
            if (provider == null || !(provider instanceof CvsScmProvider)) {
                return false;
            }
        } catch (ScmRepositoryException e) {
            return false;
        } catch (NoSuchScmProviderException e) {
            return false;
        }
        return true;
    }

    public CvsConnection(StatCvsMojo mojo, File logFile) throws ScmException {
        this.mojo = mojo;
        this.logFile = logFile;

        try {
            scmRepository = mojo.getScmManager().makeScmRepository(mojo.getConnectionUrl());
        } catch (Exception e) {
            throw new ScmException("Can't load the scm provider.", e);
        }

        provider = mojo.getScmManager().getProviderByRepository(scmRepository);

        cvsSourceLocation = mojo.getCvsSourceLocation();
    }

    public void execute() throws MojoExecutionException, ScmException {
        if (mojo.isHistory()) {
            String filename = cvsSourceLocation.getAbsolutePath() + "/CVS/Root";
            if (FileUtils.fileExists(filename)) {
                updateHistory();
            } else {
                checkOutHistory();
            }
        }

        if (mojo.isFork()) {
            fetchLogForked();
        } else {
            fetchLog();
        }
    }

    /**
     * Check out a fresh history to the current working directory.
     * 
     * @throws MojoExecutionException if an error occurs during the scm
     *             operation
     * @throws ScmException if an error occurs during the scm operation
     */
    private void checkOutHistory() throws MojoExecutionException, ScmException {
        mojo.getLog().info("Checking out history to " + cvsSourceLocation.getAbsolutePath());

        ScmFileSet fileSet = new ScmFileSet(cvsSourceLocation);
        CheckOutScmResult result = provider.checkOut(scmRepository, fileSet, "1.1");
        checkResult(result, result.getCheckedOutFiles());
    }

    private void checkResult(ScmResult result, List files) throws MojoExecutionException {
        if (!result.isSuccess()) {
            mojo.getLog().error("Provider message:");
            mojo.getLog().error(result.getProviderMessage() == null ? "" : result.getProviderMessage());
            mojo.getLog().error("Command output:");
            mojo.getLog().error(result.getCommandOutput() == null ? "" : result.getCommandOutput());

            throw new MojoExecutionException("Command failed.");
        } else if (files != null) {
            mojo.getLog().info("Processed " + files.size() + " files.");
        }
    }

    /**
     * Fetch the CVS log in-process via the Maven SCM libraries.
     * 
     * @throws MojoExecutionException if an error occurs during the scm
     *             operation
     * @throws ScmException if an error occurs during the scm operation
     */
    private void fetchLog() throws MojoExecutionException, ScmException {
        mojo.getLog().info("Checking out CVS log");

        ScmFileSet fileSet = new ScmFileSet(cvsSourceLocation);
        ChangeLogScmResult result = provider.changeLog(scmRepository, fileSet, null, null, 0, null);
        checkResult(result, null);
    }

    /**
     * Fetch the CVS log via a forked process using the system's installed cvs
     * client.
     * 
     * @throws MojoExecutionException
     * @throws ScmException
     */
    private void fetchLogForked() throws ScmException {
        mojo.getLog().info("Checking out CVS log (forked)");

        Commandline cli = new Commandline();
        cli.setWorkingDirectory(cvsSourceLocation.getAbsolutePath());
        cli.setExecutable("cvs");
        cli.addArguments(new String[] { "-z3", "-f", "-q", "log" });

        Writer stringWriter = new StringWriter();
        StreamConsumer out = new WriterStreamConsumer(stringWriter);
        StreamConsumer err = new WriterStreamConsumer(stringWriter);

        int returnCode;
        try {
            mojo.getLog().debug("Executing " + Arrays.asList(cli.getCommandline()));
            returnCode = CommandLineUtils.executeCommandLine(cli, out, err);
        } catch (CommandLineException e) {
            throw new ScmException("Error executing cvs log", e);
        }
        if (returnCode == 0) {
            try {
                FileUtils.fileWrite(logFile.getAbsolutePath(), stringWriter.toString());
            } catch (IOException e) {
                throw new ScmException("Error writing log file", e);
            }
        }
    }

    /**
     * Update the history contained at the current working directory.
     * 
     * @throws MojoExecutionException if an error occurs during the scm
     *             operation
     * @throws ScmException if an error occurs during the scm operation
     */
    private void updateHistory() throws MojoExecutionException, ScmException {
        mojo.getLog().info("Updating history at " + cvsSourceLocation.getAbsolutePath());

        ScmFileSet fileSet = new ScmFileSet(cvsSourceLocation);
        UpdateScmResult result = provider.update(scmRepository, fileSet, "1.1");
        checkResult(result, result.getUpdatedFiles());
    }
}
