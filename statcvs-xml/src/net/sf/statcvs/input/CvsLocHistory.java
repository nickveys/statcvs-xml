/*
 * statcvs-xml
 * TODO
 * Created on 05.07.2003
 *
 */
package net.sf.statcvs.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.statcvs.Main;
import net.sf.statcvs.model.CvsContent;
import net.sf.statcvs.model.CvsFile;
import net.sf.statcvs.output.ConfigurationOptions;
import net.sf.statcvs.util.FileUtils;

/**
 * CvsLibrary
 * 
 * @author Tammo van Lessen
 * @version $id: $
 */
public class CvsLocHistory {

	private static Logger logger = Logger.getLogger(CvsLocHistory.class.getName());
	
	private static CvsLocHistory singleton = new CvsLocHistory();
	
	//private String filename;
	private Map fileLocMap = new HashMap();	
	private Date lastUpdated;
	private File workingDir;
	private boolean loaded = false;
		
	private CvsLocHistory() {
		workingDir = new File(ConfigurationOptions.getCheckedOutDirectory());
	}
	
	public static CvsLocHistory getInstance() {
		return singleton;
	}
	
	public void load(String module) {
		if (loaded) return;
		try {
			String filename = Main.getSettingsPath()+module+".hist";
			FileInputStream in = new FileInputStream(filename);
			try {
				ObjectInputStream ois = new ObjectInputStream(in);
				fileLocMap = (Map)ois.readObject();
				loaded = true;
				logger.info("History file '"+module+".hist' loaded.");
			} catch (ClassNotFoundException e) {
				logger.warning("wrong history file, creating a new one");
			}
			finally {
				in.close();
			}
		} catch (IOException e) {
			logger.info("no history file found");
			// dont try to load in next file
			loaded = true;
			ConfigurationOptions.setGenerateHistory(true);
		}
	}
	
	public void save(String module) {
		if (!loaded) return;
		FileOutputStream out;
		try {
			String filename = Main.getSettingsPath()+module+".hist";
			out = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(out);

			oos.writeObject(fileLocMap);
			oos.flush();
			out.close();
		} catch (IOException e) {
			logger.warning("Could not save history.");
		}
	}

	public void generate(CvsContent content) {
		System.out.println("Generating history file...");
		try {
			File tmpdir = new File(System.getProperty("java.io.tmpdir")+"/statcvs"+ Integer.toHexString(this.hashCode()) +"history");
			//if (!tmpdir.mkdir()) return;
			File cvsdir = new File(tmpdir, "CVS");
			cvsdir.mkdirs();

			FileUtils.copyFile(ConfigurationOptions.getCheckedOutDirectory()+"/CVS/Repository",
				 cvsdir.getAbsolutePath()+"/Repository");
			FileUtils.copyFile(ConfigurationOptions.getCheckedOutDirectory()+"/CVS/Entries",
				 cvsdir.getAbsolutePath()+"/Entries");
			FileUtils.copyFile(ConfigurationOptions.getCheckedOutDirectory()+"/CVS/Root",
				 cvsdir.getAbsolutePath()+"/Root");

			String[] cmd = {"cvs", "-Q", "update","-d", "-r","1.1"};
			try {
				Runtime rt = Runtime.getRuntime();
				final Process p = rt.exec(cmd, null, tmpdir);
		 		p.waitFor();
			} catch (Exception e) {
			}
		   
			RepositoryFileManager repoman = new RepositoryFileManager(tmpdir.getAbsolutePath());
			System.out.print("Indexing...");
			for (int i=0; i<content.getFiles().size(); i++) {
				CvsFile file = (CvsFile)content.getFiles().get(i);
				try {
					int lines = repoman.getLinesOfCode(file.getFilenameWithPath());
					fileLocMap.put(file.getFilenameWithPath(), new Integer(lines));
					logger.finer("indexing first revision of "+file.getFilenameWithPath()+": "+lines+" Lines");
				} catch (RepositoryException e2) {
				}	
			}
			System.out.println(" done");
			loaded = true;
			if (!deleteDir(tmpdir)) {
				logger.info("Could not clean up temp directory.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getLinesOfCode(CvsFile file) {
		if (!loaded) return -1;
		// return 0 because we need a second run after hist generation
		if (ConfigurationOptions.getGenerateHistory()) return 0;
		int lineCount = 0;
		Integer loc = (Integer)fileLocMap.get(file.getFilenameWithPath());
		if (loc == null) {
			try {
				logger.info("History: LOC count unknown, asking cvs: "+file.getFilenameWithPath());
				Process cvs = Runtime.getRuntime().exec("cvs -q update -p -r 1.1 "+file.getFilenameWithPath(), 
						null, workingDir);
				BufferedReader cvsIn = new BufferedReader(new InputStreamReader(cvs.getInputStream()));
				while (cvsIn.readLine() != null) {
					lineCount++;
				}
				cvsIn.close();
				cvs.destroy();
				fileLocMap.put(file.getFilenameWithPath(), new Integer(lineCount));
			} catch (IOException e) {
				logger.info("Could not get linecount of "+file.getFilenameWithPath());
			} 
		} else {
			lineCount = loc.intValue();
			logger.finer("History: loc for file '"+file.getFilenameWithPath()+"': "+lineCount);
		}
		return lineCount;
	}
	
	public boolean deleteDir(File dir) {
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
}
