package de.berlios.statcvs.xml.maven;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.checkout.CheckOutScmResult;
import org.apache.maven.scm.command.update.UpdateScmResult;
import org.apache.maven.scm.provider.ScmProvider;
import org.apache.maven.scm.provider.cvslib.repository.CvsScmProviderRepository;
import org.apache.maven.scm.repository.ScmRepository;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.WriterStreamConsumer;

/**
 * TODO compression, quiet, cvsRsh
 * 
 * @author Steffen Pingel
 */
public class CvsConnection {

	private StatCvsMojo mojo;
	private CvsScmProviderRepository repository;
	private ScmProvider provider;
	private File workingDirectory;
	private ScmRepository scmRepository;
	private File logFile;

	public CvsConnection(StatCvsMojo mojo, File logFile) throws ScmException
	{
		this.mojo = mojo;
		this.logFile = logFile;
		
		initialize();
	}
	
    public void initialize() throws ScmException
    {
		try {
			scmRepository = mojo.getScmManager().makeScmRepository(mojo.getConnectionUrl());
		}
		catch (Exception e) {
			throw new ScmException( "Can't load the scm provider.", e );
		}
        
    	if (!(scmRepository.getProviderRepository() instanceof CvsScmProviderRepository)) {
        	throw new ScmException("SCM provider '" + scmRepository.getProvider() + "' invalid, only 'cvs' is supported");
        }
    	repository = (CvsScmProviderRepository)scmRepository.getProviderRepository();
    	provider = mojo.getScmManager().getProviderByRepository(scmRepository);
    	
       	workingDirectory = mojo.getWorkingDirectory();
    }

    public void execute() throws MojoExecutionException, ScmException
    {
    	if (mojo.isHistory()) {
    		String filename = workingDirectory.getAbsolutePath() + "/CVS/Root";
    		if (FileUtils.fileExists(filename)) {
    			updateHistory();
    		}
    		else {
    			FileUtils.mkdir(workingDirectory.getParentFile().getAbsolutePath());
    			checkOutHistory();
    		}
    	}
    	
    	// FIXME scm log command needs to be extended
    	//fetchLog();
    	fetchLogForked();
    }
    
    private void checkOutHistory() throws ScmException, MojoExecutionException
    {
    	mojo.getLog().info("Checking out repository at " + workingDirectory.getAbsolutePath());
    	
    	ScmFileSet fileSet = new ScmFileSet(workingDirectory);
    	CheckOutScmResult result = provider.checkOut(scmRepository, fileSet, "1.1");
    	checkResult(result, result.getCheckedOutFiles());
    }
    
    private void updateHistory() throws ScmException, MojoExecutionException
    {
    	mojo.getLog().info("Updating repository at " + workingDirectory.getAbsolutePath());
    	
    	ScmFileSet fileSet = new ScmFileSet(workingDirectory);
    	UpdateScmResult result = provider.update(scmRepository, fileSet, "1.1");
    	checkResult(result, result.getUpdatedFiles());
    }

    private void fetchLog() throws MojoExecutionException, ScmException
    {
    	ScmFileSet fileSet = new ScmFileSet(workingDirectory);
    	ChangeLogScmResult result = provider.changeLog(scmRepository, fileSet, null, null, 0, null);
    	checkResult(result, null);    	
    }
    
    private void fetchLogForked() throws MojoExecutionException, ScmException
    {
		Commandline cli = new Commandline();
		cli.setWorkingDirectory(workingDirectory.getAbsolutePath());
		cli.setExecutable("cvs");
		cli.addArguments(new String[] { "-z3", "-f", "-q", "log" });
		
		Writer stringWriter = new StringWriter();
		StreamConsumer out = new WriterStreamConsumer(stringWriter);
		StreamConsumer err = new WriterStreamConsumer(stringWriter);

		int returnCode;
		try {
			returnCode = CommandLineUtils.executeCommandLine(cli, out, err);
		}
		catch (CommandLineException e) {
			throw new ScmException("Error executing cvs log", e);
		}
		if (returnCode == 0) {
			try {
				FileUtils.fileWrite(logFile.getAbsolutePath(), stringWriter.toString());
			}
			catch (IOException e) {
				throw new ScmException("Error writing log file", e);
			}
		}
    }

    private void checkResult(ScmResult result, List files) throws MojoExecutionException
	{
		if (!result.isSuccess()) {
			mojo.getLog().error("Provider message:");
			mojo.getLog().error(result.getProviderMessage() == null ? "" : result.getProviderMessage());
			mojo.getLog().error("Command output:");
			mojo.getLog().error(result.getCommandOutput() == null ? "" : result.getCommandOutput());
			
			throw new MojoExecutionException("Command failed.");
		}
		else if (files != null) {
			mojo.getLog().info("Processed " + files.size() + " files.");
		}
	}

    
}